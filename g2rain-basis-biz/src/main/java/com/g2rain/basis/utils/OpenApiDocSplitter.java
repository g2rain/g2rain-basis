package com.g2rain.basis.utils;

import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.json.JsonCodec;
import com.g2rain.common.json.JsonCodecFactory;
import com.g2rain.common.utils.Asserts;
import com.g2rain.common.utils.Strings;
import org.springframework.http.HttpMethod;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.ObjectNode;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 将整份 OpenAPI / Swagger JSON 拆成「单接口最小文档」并抽出元数据。
 * <p>
 * 只做 JSON 剪枝与 {@code $ref} 收集拷贝，不做版本转换、不做 {@code $ref} 展开。
 * JSON 编解码统一走 {@link JsonCodec}（Jackson 3）。
 * </p>
 */
public final class OpenApiDocSplitter {

    private static final JsonCodec CODEC = JsonCodecFactory.instance();
    private static final JsonNodeFactory NODES = JsonNodeFactory.instance;

    /**
     * OpenAPI Path Item 上的 HTTP 操作名（小写）。
     * <p>来自 Spring {@link HttpMethod#values()}，不用 {@code valueOf}（未知串也会造实例）。</p>
     */
    private static final Set<String> HTTP_METHODS = Arrays.stream(HttpMethod.values())
        .map(method -> method.name().toLowerCase(Locale.ROOT))
        .collect(Collectors.toUnmodifiableSet());

    /**
     * 拆分时不整块复制的顶层字段（paths 按接口重建；components/definitions 按 $ref 按需拷贝）。
     */
    private static final Set<String> ROOT_SKIP = Set.of("paths", "components", "definitions");

    /**
     * 单次拆分允许拷贝的最大 $ref 数，防止恶意文档拖垮内存。
     */
    private static final int MAX_REFS = 512;

    /**
     * JSON Pointer 最大段数。
     */
    private static final int MAX_POINTER_DEPTH = 32;

    private static final int NAME_MAX = 128;
    private static final int TAGS_MAX = 128;
    private static final int DESC_MAX = 512;

    private OpenApiDocSplitter() {

    }

    /**
     * 拆分结果：接口元数据 + 规范化后的最小 OpenAPI 文档 JSON。
     *
     * @param method          HTTP 方法（大写）
     * @param path            接口路径
     * @param name            名称（优先 summary）
     * @param apiTags         标签，逗号拼接
     * @param description     说明，可空
     * @param openapiDocument 键序排序后的最小文档 JSON
     */
    public record SplitResult(String method, String path, String name, String apiTags,
                              String description, String openapiDocument) {

    }

    /**
     * 将整份文档拆成接口级最小文档列表。
     *
     * @param documentJson 整份 OpenAPI / Swagger JSON 文本
     * @return 拆分结果；无 paths 时返回空列表
     */
    public static List<SplitResult> split(String documentJson) {
        Asserts.isTrue(Strings.isNotBlank(documentJson), SystemErrorCode.PARAM_REQUIRED, "document");

        // JsonCodec 非法 JSON 会抛 BusinessException
        JsonNode root = CODEC.str2node(documentJson);
        Asserts.isTrue(Objects.nonNull(root) && root.isObject(), SystemErrorCode.PARAM_VAL_INVALID, "document");

        ObjectNode rootObj = (ObjectNode) root;
        JsonNode pathsNode = rootObj.get("paths");
        if (Objects.isNull(pathsNode) || !pathsNode.isObject()) {
            return List.of();
        }

        List<SplitResult> results = new ArrayList<>();
        for (Map.Entry<String, JsonNode> pathEntry : pathsNode.properties()) {
            String path = pathEntry.getKey();
            JsonNode pathItem = pathEntry.getValue();
            if (Objects.isNull(pathItem) || !pathItem.isObject()) {
                continue;
            }

            ObjectNode pathItemObj = (ObjectNode) pathItem;
            // Path Item 级 parameters 需并入每个 Operation
            JsonNode pathLevelParams = pathItemObj.get("parameters");

            for (Map.Entry<String, JsonNode> opEntry : pathItemObj.properties()) {
                String methodKey = opEntry.getKey();
                if (!HTTP_METHODS.contains(methodKey.toLowerCase())) {
                    continue;
                }

                JsonNode operation = opEntry.getValue();
                if (Objects.isNull(operation) || !operation.isObject()) {
                    continue;
                }

                ObjectNode operationCopy = mergePathParameters((ObjectNode) operation.deepCopy(), pathLevelParams);
                ObjectNode mini = buildMiniDocument(rootObj, path, methodKey.toLowerCase(), operationCopy);
                copyReferencedDefs(rootObj, mini, collectRefs(operationCopy));
                copySecuritySchemes(rootObj, mini);
                copySecurityDefinitions(rootObj, mini);

                results.add(toSplitResult(methodKey, path, operationCopy, mini));
            }
        }

        return results;
    }

    /**
     * 复制顶层字段（跳过 paths/components/definitions），再放入当前 path + method。
     */
    private static ObjectNode buildMiniDocument(ObjectNode root, String path, String method, ObjectNode operation) {
        ObjectNode mini = NODES.objectNode();
        for (Map.Entry<String, JsonNode> field : root.properties()) {
            if (ROOT_SKIP.contains(field.getKey())) {
                continue;
            }

            mini.set(field.getKey(), field.getValue().deepCopy());
        }

        ObjectNode miniPathItem = NODES.objectNode();
        miniPathItem.set(method, operation);
        ObjectNode miniPaths = NODES.objectNode();
        miniPaths.set(path, miniPathItem);
        mini.set("paths", miniPaths);
        return mini;
    }

    /**
     * 将 path 级 parameters 合并进 operation（path 在前，operation 在后）。
     */
    private static ObjectNode mergePathParameters(ObjectNode operation, JsonNode pathLevelParams) {
        if (Objects.isNull(pathLevelParams) || !pathLevelParams.isArray() || pathLevelParams.isEmpty()) {
            return operation;
        }

        ArrayNode merged = NODES.arrayNode();
        pathLevelParams.forEach(merged::add);
        JsonNode opParams = operation.get("parameters");
        if (Objects.nonNull(opParams) && opParams.isArray()) {
            opParams.forEach(merged::add);
        }

        operation.set("parameters", merged);
        return operation;
    }

    /**
     * 从节点树递归收集 {@code $ref} 文本值。
     */
    private static Set<String> collectRefs(JsonNode node) {
        Set<String> refs = new LinkedHashSet<>();
        collectRefsInto(node, refs);
        return refs;
    }

    private static void collectRefsInto(JsonNode node, Set<String> refs) {
        if (Objects.isNull(node) || node.isNull()) {
            return;
        }

        if (node.isObject()) {
            JsonNode ref = node.get("$ref");
            if (Objects.nonNull(ref) && ref.isString()) {
                refs.add(ref.asString());
            }

            for (Map.Entry<String, JsonNode> field : node.properties()) {
                collectRefsInto(field.getValue(), refs);
            }

            return;
        }

        if (node.isArray()) {
            node.forEach(child -> collectRefsInto(child, refs));
        }
    }

    /**
     * 按 JSON Pointer 将引用目标拷入 mini，并继续收集嵌套引用（有上限、防环）。
     */
    private static void copyReferencedDefs(ObjectNode root, ObjectNode mini, Set<String> seedRefs) {
        Set<String> pending = new LinkedHashSet<>(seedRefs);
        Set<String> done = new LinkedHashSet<>();

        while (!pending.isEmpty()) {
            Asserts.isTrue(done.size() + pending.size() <= MAX_REFS, SystemErrorCode.PARAM_VAL_INVALID, "document.$ref");

            String ref = pending.iterator().next();
            pending.remove(ref);
            if (!done.add(ref)) {
                continue;
            }

            List<String> parts = parseLocalRef(ref);
            if (parts.isEmpty()) {
                continue;
            }

            JsonNode target = resolveInRoot(root, parts);
            if (Objects.isNull(target)) {
                continue;
            }

            ObjectNode parent = ensureObjectPath(mini, parts.subList(0, parts.size() - 1));
            if (Objects.isNull(parent)) {
                continue;
            }

            JsonNode copied = target.deepCopy();
            parent.set(parts.getLast(), copied);

            for (String nested : collectRefs(copied)) {
                if (!done.contains(nested)) {
                    pending.add(nested);
                }
            }
        }
    }

    /**
     * 解析本地 JSON Pointer（仅支持 {@code #/...}）；非法或过深返回空列表。
     */
    private static List<String> parseLocalRef(String ref) {
        if (Strings.isBlank(ref) || !ref.startsWith("#/")) {
            return List.of();
        }

        String[] raw = ref.substring(2).split("/");
        if (raw.length < 2 || raw.length > MAX_POINTER_DEPTH) {
            return List.of();
        }

        List<String> parts = new ArrayList<>(raw.length);
        for (String part : raw) {
            parts.add(unescapePointer(part));
        }

        return parts;
    }

    private static JsonNode resolveInRoot(ObjectNode root, List<String> parts) {
        JsonNode cursor = root;
        for (String part : parts) {
            if (Objects.isNull(cursor) || !cursor.isObject()) {
                return null;
            }

            cursor = cursor.get(part);
        }

        return cursor;
    }

    /**
     * 在 mini 上确保 pointer 中间路径均为 ObjectNode，返回叶子的父节点。
     */
    private static ObjectNode ensureObjectPath(ObjectNode mini, List<String> parentParts) {
        ObjectNode parent = mini;
        for (String part : parentParts) {
            JsonNode child = parent.get(part);
            if (Objects.isNull(child) || !child.isObject()) {
                ObjectNode created = NODES.objectNode();
                parent.set(part, created);
                parent = created;
            } else {
                parent = (ObjectNode) child;
            }
        }

        return parent;
    }

    /**
     * OpenAPI 3：整块复制 components.securitySchemes（若存在）。
     */
    private static void copySecuritySchemes(ObjectNode root, ObjectNode mini) {
        JsonNode components = root.get("components");
        if (Objects.isNull(components) || !components.isObject()) {
            return;
        }

        JsonNode schemes = components.get("securitySchemes");
        if (Objects.isNull(schemes)) {
            return;
        }

        ObjectNode miniComponents = mini.has("components") && mini.get("components").isObject()
            ? (ObjectNode) mini.get("components")
            : NODES.objectNode();
        if (!mini.has("components")) {
            mini.set("components", miniComponents);
        }

        if (!miniComponents.has("securitySchemes")) {
            miniComponents.set("securitySchemes", schemes.deepCopy());
        }
    }

    /**
     * Swagger 2：整块复制顶层 securityDefinitions（若存在）。
     */
    private static void copySecurityDefinitions(ObjectNode root, ObjectNode mini) {
        JsonNode definitions = root.get("securityDefinitions");
        if (Objects.isNull(definitions) || mini.has("securityDefinitions")) {
            return;
        }

        mini.set("securityDefinitions", definitions.deepCopy());
    }

    private static SplitResult toSplitResult(String methodKey, String path, ObjectNode operation, ObjectNode mini) {
        String name = truncate(textOrEmpty(operation, "summary"), NAME_MAX);
        if (Strings.isBlank(name)) {
            name = truncate(methodKey.toUpperCase() + " " + path, NAME_MAX);
        }

        String apiTags = truncate(joinTags(operation.get("tags")), TAGS_MAX);
        if (Strings.isBlank(apiTags)) {
            apiTags = "default";
        }

        String description = truncate(textOrEmpty(operation, "description"), DESC_MAX);
        // 键序排序，保证落库与 digest 比较稳定
        String document = CODEC.sort(CODEC.obj2str(mini).getBytes(StandardCharsets.UTF_8));

        return new SplitResult(
            methodKey.toUpperCase(),
            path,
            name,
            apiTags,
            Strings.isBlank(description) ? null : description,
            document
        );
    }

    private static String textOrEmpty(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (Objects.isNull(value) || value.isNull()) {
            return "";
        }

        return value.asString("");
    }

    private static String joinTags(JsonNode tagsNode) {
        if (Objects.isNull(tagsNode) || !tagsNode.isArray() || tagsNode.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (JsonNode tag : tagsNode) {
            if (Objects.isNull(tag) || tag.isNull()) {
                continue;
            }

            if (!sb.isEmpty()) {
                sb.append(',');
            }

            sb.append(tag.asString());
        }

        return sb.toString();
    }

    private static String truncate(String value, int max) {
        if (Objects.isNull(value) || value.length() <= max) {
            return Objects.isNull(value) ? "" : value;
        }

        return value.substring(0, max);
    }

    private static String unescapePointer(String part) {
        return part.replace("~1", "/").replace("~0", "~");
    }
}
