package com.g2rain.basis.enums;


import com.g2rain.common.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author alpha
 * @since 2025/12/5
 */
@Schema(description = "基础模块错误码枚举")
public enum BasisErrorCode implements ErrorCode {
    @Schema(description = "SELF_RELATION_ILLEGAL")
    SELF_RELATION_ILLEGAL("basis.40001", "自关联非法"),

    @Schema(description = "CIRCULAR_DEPENDENCY_ILLEGAL")
    CIRCULAR_DEPENDENCY_ILLEGAL("basis.40002", "循环依赖非法"),

    @Schema(description = "HIERARCHY_ATTACHMENT_ILLEGAL")
    HIERARCHY_ATTACHMENT_ILLEGAL("basis.40003", "层级挂载操作非法"),

    @Schema(description = "NON_DIRECT_DELETE_ILLEGAL")
    NON_DIRECT_DELETE_ILLEGAL("basis.40004", "非直属关系禁止删除"),

    @Schema(description = "NON_LEAF_DELETE_ILLEGAL")
    NON_LEAF_DELETE_ILLEGAL("basis.40005", "只能删除没有下级的组织节点"),

    @Schema(description = "KEY_PAIR_GENERATION_FAILED")
    KEY_PAIR_GENERATION_FAILED("basis.40006", "公私钥生成失败"),

    @Schema(description = "PASSWORD_ENCRYPTION_FAILED")
    PASSWORD_ENCRYPTION_FAILED("basis.40007", "密码加密失败"),

    @Schema(description = "PASSWORD_HASH_FORMAT_INVALID")
    PASSWORD_HASH_FORMAT_INVALID("basis.40008", "密码哈希格式无效"),

    @Schema(description = "PARAM_ALREADY_EXISTS")
    PARAM_ALREADY_EXISTS("basis.40009", "参数 {0:paramName} 的值 {1:paramValue} 已存在"),

    @Schema(description = "PUBLISHED_UPDATE_ILLEGAL")
    PUBLISHED_UPDATE_ILLEGAL("basis.40010", "已发布的应用数据不允许修改"),

    @Schema(description = "PUBLISHED_DELETE_ILLEGAL")
    PUBLISHED_DELETE_ILLEGAL("basis.40011", "已发布的应用数据不允许删除"),

    @Schema(description = "INTEGRATED_APP_CLASSIFY_ILLEGAL")
    INTEGRATED_APP_CLASSIFY_ILLEGAL("basis.40012", "调整具备集成功能，请先删除应用归类数据"),

    @Schema(description = "NON_SUB_APPLICATION_CLASSIFY_ILLEGAL")
    NON_SUB_APPLICATION_CLASSIFY_ILLEGAL("basis.40013", "非子应用不允许进行应用归类"),

    @Schema(description = "NON_MAIN_APPLICATION_CLASSIFY_ILLEGAL")
    NON_MAIN_APPLICATION_CLASSIFY_ILLEGAL("basis.40014", "非主应用不允许作为归类目标"),

    @Schema(description = "DEL_LANDING_CALLBACK_APPLICATION_ILLEGAL")
    DEL_LANDING_CALLBACK_APPLICATION_ILLEGAL("basis.40015", "不允许删除默认应用"),

    @Schema(description = "DELIVERY_SCOPE_RESTRICTED_ILLEGAL")
    DELIVERY_SCOPE_RESTRICTED_ILLEGAL("basis.40016", "交易开通类型不允许设置平台运营交付范围"),

    @Schema(description = "CONTROL_UNIT_CROSS_APP_NOT_ALLOWED")
    CONTROL_UNIT_CROSS_APP_NOT_ALLOWED("basis.40017", "不允许跨应用设置功能权限"),

    @Schema(description = "MENU_CROSS_APP_NOT_ALLOWED")
    MENU_CROSS_APP_NOT_ALLOWED("basis.40018", "不允许跨应用设置菜单"),

    @Schema(description = "MENU_SELF_RELATION_ILLEGAL")
    MENU_SELF_RELATION_ILLEGAL("basis.40019", "菜单不允许自关联"),

    @Schema(description = "DEL_ADMIN_ROLE_ILLEGAL")
    DEL_ADMIN_ROLE_ILLEGAL("basis.40020", "不允许删除超管角色"),

    @Schema(description = "DEL_ADMIN_ROLE_CONTROL_UNIT_ILLEGAL")
    DEL_ADMIN_ROLE_CONTROL_UNIT_ILLEGAL("basis.40021", "不允许删除超管角色的功能权限"),

    @Schema(description = "ADD_ADMIN_ROLE_CONTROL_UNIT_ILLEGAL")
    ADD_ADMIN_ROLE_CONTROL_UNIT_ILLEGAL("basis.40022", "不允许给超管角色添加功能权限"),

    @Schema(description = "CONTROL_UNIT_INVALID_FOR_ROLE")
    CONTROL_UNIT_INVALID_FOR_ROLE("basis.40023", "功能权限不存在或已关停，角色不允许设置该功能权限"),

    @Schema(description = "ADMIN_ROLE_NOT_EXISTS_ILLEGAL")
    ADMIN_ROLE_NOT_EXISTS_ILLEGAL("basis.40024", "机构不存在默认角色, 请为机构添加默认角色"),

    @Schema(description = "DEL_ACCOUNT_ILLEGAL")
    DEL_ACCOUNT_ILLEGAL("basis.40025", "存在用户关联，不能删除账号"),

    @Schema(description = "DEL_ROLE_ILLEGAL")
    DEL_ROLE_ILLEGAL("basis.40026", "存在用户关联，不能删除角色"),

    @Schema(description = "ACCOUNT_FROZEN")
    ACCOUNT_FROZEN("basis.40027", "账号已冻结"),

    @Schema(description = "USERNAME_OR_PASSWORD_INCORRECT")
    USERNAME_OR_PASSWORD_INCORRECT("basis.40028", "用户名或密码不正确"),

    @Schema(description = "ORGAN_UNAVAILABLE")
    ORGAN_UNAVAILABLE("basis.40029", "机构不可用，请确认机构状态"),

    @Schema(description = "APP_AUTHORIZATION_UNMODIFIABLE")
    APP_AUTHORIZATION_UNMODIFIABLE("basis.40030", "应用授权记录不可修改"),

    @Schema(description = "APP_AUTHORIZATION_UNDELETABLE")
    APP_AUTHORIZATION_UNDELETABLE("basis.40031", "应用授权记录不可删除"),

    @Schema(description = "DEL_ORGAN_UNDELETABLE")
    DEL_ORGAN_UNDELETABLE("basis.40032", "机构记录不可删除"),

    @Schema(description = "DEL_ADMIN_USER_UNDELETABLE")
    DEL_ADMIN_USER_UNDELETABLE("basis.40033", "管理员不可删除"),

    @Schema(description = "DEL_APP_EXIST_DOMAIN_UNDELETABLE")
    DEL_APP_EXIST_DOMAIN_UNDELETABLE("basis.40034", "存在业务能力, 不允许删除应用"),

    @Schema(description = "DEL_APP_EXIST_AUTH_UNDELETABLE")
    DEL_APP_EXIST_AUTH_UNDELETABLE("basis.40035", "存在授权记录, 不允许删除应用"),

    @Schema(description = "DEL_RESOURCE_EXIST_CONTROL_UNIT_UNDELETABLE")
    DEL_RESOURCE_EXIST_CONTROL_UNIT_UNDELETABLE("basis.40036", "已关联功能权限, 不允许删除资源"),

    @Schema(description = "DEL_RESOURCE_EXIST_ELEMENT_UNDELETABLE")
    DEL_RESOURCE_EXIST_ELEMENT_UNDELETABLE("basis.40037", "已关联页面元素, 不允许删除资源"),

    @Schema(description = "DEL_API_EXIST_RESOURCE_API_UNDELETABLE")
    DEL_API_EXIST_RESOURCE_API_UNDELETABLE("basis.40038", "已关联资源接口, 不允许删除后端接口"),

    @Schema(description = "RESOURCE_UPLOAD_FAIL")
    RESOURCE_UPLOAD_FAIL("basis.40039", "资源文件解析失败"),

    @Schema(description = "PUB_CONTROL_UNIT_LOCKED")
    PUB_CONTROL_UNIT_LOCKED("basis.40040", "已发布功能权限禁止操作"),

    @Schema(description = "PUB_CONTROL_UNIT_READONLY")
    PUB_CONTROL_UNIT_READONLY("basis.40041", "已发布功能权限不可修改状态"),

    @Schema(description = "DEL_MENU_EXIST_CHILD_MENU_UNDELETABLE")
    DEL_MENU_EXIST_CHILD_MENU_UNDELETABLE("basis.40042", "存在子菜单, 不允许删除菜单"),

    @Schema(description = "ADD_CONTROL_UNIT_READONLY_ILLEGAL")
    ADD_CONTROL_UNIT_READONLY_ILLEGAL("basis.40043", "不允许关联未发布的功能权限"),

    @Schema(description = "DEL_LANDING_CONTROL_UNIT_ILLEGAL")
    DEL_LANDING_CONTROL_UNIT_ILLEGAL("basis.40044", "不允许删除默认功能权限"),

    @Schema(description = "OPERATION_ORG_CREATE_DENIED")
    OPERATION_ORG_CREATE_DENIED("basis.40045", "运营公司不允许创建机构"),

    @Schema(description = "OLD_PASSWORD_ILLEGAL")
    OLD_PASSWORD_ILLEGAL("basis.40046", "旧密码不正确"),

    @Schema(description = "BUSINESS_CAPABILITY_DISABLED")
    BUSINESS_CAPABILITY_DISABLED("basis.40047", "业务能力已关停，请续费后再登录"),

    @Schema(description = "PUB_KEY_UNSUPPORTED_ALGORITHM")
    PUB_KEY_UNSUPPORTED_ALGORITHM("basis.40048", "不支持的公钥算法"),

    @Schema(description = "PUB_KEY_TYPE_MISMATCH")
    PUB_KEY_TYPE_MISMATCH("basis.40049", "公钥类型与算法不匹配"),

    @Schema(description = "PUB_KEY_INVALID_KEY")
    PUB_KEY_INVALID_KEY("basis.40050", "无效公钥"),

    @Schema(description = "USER_NOT_EXISTS_ILLEGAL")
    USER_NOT_EXISTS_ILLEGAL("basis.40051", "用户不存在"),

    @Schema(description = "RESOURCE_API_EXISTS_ILLEGAL")
    RESOURCE_API_EXISTS_ILLEGAL("basis.40052", "已关联资源接口, 不允许删除服务注册信息"),

    @Schema(description = "APPLICATION_IDP_PROVISION_MISSING")
    APPLICATION_IDP_PROVISION_MISSING("basis.40053", "当前外部身份源应用未与该平台应用建立绑定"),

    @Schema(description = "PASSPORT_IDP_BINDING_MISMATCH")
    PASSPORT_IDP_BINDING_MISMATCH("basis.40054", "当前通行证与此外部身份源主体或应用绑定不一致"),

    @Schema(description = "PASSWORD_NOT_TRUSTED")
    PASSWORD_NOT_TRUSTED("basis.40055", "账号尚未设置可信密码，请使用第三方登录或修改密码后再试"),

    @Schema(description = "ONLY_OWN_ORG_APIKEY_ALLOWED")
    ONLY_OWN_ORG_APIKEY_ALLOWED("basis.40056", "只能创建本机构的 API Key"),

    @Schema(description = "IDP_ENTERPRISE_ORGAN_NOT_BOUND")
    IDP_ENTERPRISE_ORGAN_NOT_BOUND("basis.40057", "当前租户尚未绑定该钉钉企业，请联系管理员完成三方企业绑定"),

    @Schema(description = "PASSPORT_IDP_SUBJECT_ALREADY_BOUND")
    PASSPORT_IDP_SUBJECT_ALREADY_BOUND("basis.40058", "该钉钉账号已绑定其他通行证，无法重复绑定");

    private final String code;

    private final String messageTemplate;

    /**
     * 构造系统错误码
     *
     * @param code            错误码（遵循4xxx客户端错误，5xxx服务器错误）
     * @param messageTemplate 消息模板（支持{0:param}顺序占位符或{key}键值对占位符）
     */
    BasisErrorCode(String code, String messageTemplate) {
        this.code = code;
        this.messageTemplate = messageTemplate;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String messageTemplate() {
        return messageTemplate;
    }
}
