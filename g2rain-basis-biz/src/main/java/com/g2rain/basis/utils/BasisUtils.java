package com.g2rain.basis.utils;


import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.enums.KeyAlgorithm;
import com.g2rain.basis.enums.PublicKeyFormat;
import com.g2rain.basis.vo.PublicKeyDescriptorVo;
import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.utils.Asserts;
import com.g2rain.common.utils.Collections;
import com.g2rain.common.utils.Strings;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HexFormat;
import java.util.regex.Pattern;

/**
 * 系统基础工具类，用于集中管理通用操作和常量。
 *
 * <p>
 * 该类作为不可实例化的工具类，提供静态方法和常量，用于系统安全、令牌管理、密码加密等基础功能。
 * 创建该工具类的主要目的在于：
 * </p>
 * <ul>
 *     <li>集中管理系统中反复使用的基础工具逻辑，避免方法散落在各处。</li>
 *     <li>保证工具方法的一致性和可维护性，减少重复代码。</li>
 *     <li>统一定义系统常量，如令牌有效期等，提高代码可读性与可配置性。</li>
 *     <li>提供安全相关的工具方法，为加密、签名、令牌生成等功能提供基础支持。</li>
 * </ul>
 *
 * <p>
 * 设计原则：
 * <ul>
 *     <li>不可实例化：所有方法和常量均为静态，防止外部创建实例。</li>
 *     <li>方法粒度小、职责单一：每个方法完成明确的通用功能，便于复用。</li>
 *     <li>高度内聚：与系统安全、令牌、加密相关的方法集中管理，方便维护。</li>
 * </ul>
 * </p>
 *
 * <p>使用方式：直接调用静态方法，无需实例化。</p>
 *
 * @author alpha
 * @since 2026/1/10
 */
public final class BasisUtils {
    /**
     * 私有构造函数，防止工具类被实例化。
     */
    private BasisUtils() {
        // 禁止实例化
    }

    /**
     * 默认非阻塞的 SecureRandom 实例, 可复用生成盐值或随机数
     */
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * 访问令牌有效期（秒级）。
     * <p>
     * 控制 Access Token 的过期时间，超过时间需要重新获取。
     * </p>
     */
    public static final Integer ACCESS_TOKEN_EXPIRES_IN = 30 * 60;

    /**
     * 刷新令牌有效期（秒级）。
     * <p>
     * 控制 Refresh Token 的过期时间，超过时间用户需要重新登录。
     * </p>
     */
    public static final Integer REFRESH_TOKEN_EXPIRES_IN = 60 * 60;

    /**
     * 密码哈希算法名称。
     * <p>
     * 使用 PBKDF2 HMAC SHA-256 算法生成密码哈希。
     * </p>
     */
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    /**
     * PBKDF2 默认迭代次数。
     */
    private static final int ITERATIONS = 65536;

    /**
     * 盐值长度（字节）。
     */
    private static final int SALT_LENGTH = 16;

    /**
     * 哈希输出长度（字节）。
     */
    private static final int HASH_LENGTH = 32;

    /**
     * 密码哈希存储格式的正则表达式。
     * <p>
     * 格式：算法名$迭代次数$Base64盐$Base64哈希
     * </p>
     */
    private static final String PASSWORD_HASH_PATTERN =
        "^([a-zA-Z0-9]+)\\$(\\d+)\\$([A-Za-z0-9+/=]+)\\$([A-Za-z0-9+/=]+)$";

    /**
     * 用于清理 PEM 文本中非 Base64 内容的正则：
     * <ul>
     *     <li>BEGIN / END 头尾</li>
     *     <li>所有空白字符（换行、空格、制表符等）</li>
     * </ul>
     */
    private static final Pattern PEM_CLEAN_PATTERN = Pattern.compile("-----BEGIN [^-]+-----|-----END [^-]+-----|\\s");

    /**
     * Base64 解码器（线程安全，可复用）
     */
    private static final Base64.Decoder BASE64_DECODER = Base64.getDecoder();

    /**
     * Base64 MIME 编码器：每 64 字符换行，符合 PEM 规范
     */
    private static final Base64.Encoder BASE64_MIME_ENCODER = Base64.getMimeEncoder(64, new byte[]{'\n'});

    /**
     * 对 UTF-8 字符串计算 SHA-256，返回小写十六进制字符串。
     *
     * @param input 明文输入，通常为原始 API Key
     * @return 64 字符 hex 串
     * @throws IllegalStateException JVM 不支持 SHA-256 时（理论上不应发生）
     */
    public static String sha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new BusinessException(SystemErrorCode.SYSTEM_INTERNAL_ERROR, ex.getMessage());
        }
    }

    /**
     * 校验客户端上传的公钥，并统一规范化为 PEM 格式返回。
     *
     * <p>处理流程：</p>
     * <ol>
     *     <li>校验算法名称是否受支持</li>
     *     <li>自动识别上传内容是 PEM 还是 DER</li>
     *     <li>将公钥解析为 {@link PublicKey} 进行合法性校验</li>
     *     <li>确保公钥类型与算法严格匹配</li>
     *     <li>最终统一返回 PEM 格式（用于数据库存储）</li>
     * </ol>
     *
     * @param algorithmStr 前端传入的算法名称（如 {@code "EC"}）
     * @param uploadedKey  客户端上传的公钥原始字节（PEM 或 DER）
     * @return 公钥描述对象，包含算法、上传格式、规范化后的 PEM 内容
     * @throws BusinessException 算法不支持、公钥格式非法或公钥类型与算法不匹配
     */
    public static PublicKeyDescriptorVo validateAndNormalizePublicKey(String algorithmStr, byte[] uploadedKey) {
        if (Collections.isEmpty(uploadedKey)) {
            throw new BusinessException(BasisErrorCode.PUB_KEY_INVALID_KEY);
        }

        final KeyAlgorithm algorithm = parseAlgorithm(algorithmStr);

        try {
            // 仅用于格式识别，绝不作为密钥解析依据
            String ascii = new String(uploadedKey, StandardCharsets.US_ASCII).trim();
            boolean isPem = ascii.startsWith("-----BEGIN");

            byte[] derBytes = isPem ? pemToDerInternal(ascii) : uploadedKey;
            String pem = toPem(derBytes);

            KeyFactory keyFactory = KeyFactory.getInstance(algorithm.getAlgorithmName());
            PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(derBytes));

            boolean match = switch (algorithm) {
                case EC -> publicKey instanceof ECPublicKey;
            };

            Asserts.isTrue(match, BasisErrorCode.PUB_KEY_TYPE_MISMATCH);
            String format = isPem ? PublicKeyFormat.PEM.name() : PublicKeyFormat.DER.name();
            return new PublicKeyDescriptorVo(algorithm.name(), format, pem);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(BasisErrorCode.PUB_KEY_INVALID_KEY);
        }
    }

    /**
     * 将 PEM 格式公钥转换为 DER 二进制字节。
     *
     * <p>该方法通常用于：</p>
     * <ul>
     *     <li>客户端下载 DER 格式公钥</li>
     *     <li>运行期密钥解析、加密、验签</li>
     * </ul>
     *
     * @param pem PEM 格式公钥字符串
     * @return DER 编码的公钥字节
     * @throws BusinessException 当 PEM 内容非法或 Base64 解码失败时抛出
     */
    public static byte[] toDer(String pem) {
        if (Strings.isBlank(pem)) {
            throw new BusinessException(BasisErrorCode.PUB_KEY_INVALID_KEY);
        }

        try {
            return pemToDerInternal(pem);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(BasisErrorCode.PUB_KEY_INVALID_KEY);
        }
    }

    /**
     * 内部方法：从 PEM 文本中提取 Base64 内容并解码为 DER。
     */
    private static byte[] pemToDerInternal(String pem) {
        String base64 = PEM_CLEAN_PATTERN.matcher(pem).replaceAll("");
        return BASE64_DECODER.decode(base64);
    }

    /**
     * 将 DER 编码的公钥字节转换为标准 PEM 格式字符串。
     *
     * @param derBytes X.509 DER 编码的公钥字节
     * @return PEM 格式公钥字符串
     */
    private static String toPem(byte[] derBytes) {
        return """
            -----BEGIN PUBLIC KEY-----
            %s
            -----END PUBLIC KEY-----
            """.formatted(BASE64_MIME_ENCODER.encodeToString(derBytes));
    }

    /**
     * 解析并校验算法名称。
     *
     * @param algorithmStr 算法字符串
     * @return 对应的 {@link KeyAlgorithm}
     * @throws BusinessException 当算法不受支持时抛出
     */
    private static KeyAlgorithm parseAlgorithm(String algorithmStr) {
        try {
            return KeyAlgorithm.valueOf(algorithmStr.toUpperCase());
        } catch (Exception e) {
            throw new BusinessException(BasisErrorCode.PUB_KEY_UNSUPPORTED_ALGORITHM);
        }
    }

    /**
     * 对明文密码进行哈希处理。
     * <p>
     * 使用 PBKDF2 HMAC SHA-256 算法生成盐和哈希，并将结果按格式存储：
     * {@code 算法名$迭代次数$Base64盐$Base64哈希}。
     * </p>
     *
     * @param password 明文密码
     * @return 哈希后的密码字符串
     * @throws BusinessException 如果哈希生成失败
     */
    public static String hashPassword(String password) {
        try {
            // 生成随机盐值
            byte[] salt = generateSalt();

            // 使用 PBKDF2 HMAC SHA-256 算法生成密码哈希，指定迭代次数和输出长度
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, HASH_LENGTH * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            // 计算哈希值
            byte[] hash = skf.generateSecret(spec).getEncoded();

            // 将盐值编码为 Base64 字符串
            String base64Salt = Base64.getEncoder().encodeToString(salt);
            // 将哈希值编码为 Base64 字符串
            String base64Hash = Base64.getEncoder().encodeToString(hash);

            // 拼接最终存储字符串：算法名$迭代次数$Base64盐$Base64哈希
            return ALGORITHM + "$" + ITERATIONS + "$" + base64Salt + "$" + base64Hash;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new BusinessException(BasisErrorCode.PASSWORD_ENCRYPTION_FAILED);
        }
    }

    /**
     * 验证明文密码与存储的哈希值是否匹配。
     *
     * @param password   明文密码
     * @param storedHash 存储的哈希值
     * @return 匹配返回 {@code true}，否则 {@code false}
     * @throws BusinessException 如果哈希格式不合法或验证失败
     */
    public static boolean verifyPassword(String password, String storedHash) {
        try {
            // 校验存储的哈希格式是否符合规范
            if (!Pattern.matches(PASSWORD_HASH_PATTERN, storedHash)) {
                throw new BusinessException(BasisErrorCode.PASSWORD_HASH_FORMAT_INVALID);
            }

            // 按 $ 分割存储字符串，依次获取算法名、迭代次数、盐值和哈希
            String[] parts = storedHash.split("\\$");
            // 解析迭代次数
            int iterations = Integer.parseInt(parts[1]);
            // 解析盐值
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            // 解析哈希值
            byte[] storedHashBytes = Base64.getDecoder().decode(parts[3]);

            // 使用 PBKDF2 算法对输入密码生成哈希值
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, storedHashBytes.length * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(parts[0]);
            byte[] calculatedHash = skf.generateSecret(spec).getEncoded();

            // 使用安全方式比较存储哈希和计算哈希是否一致
            return MessageDigest.isEqual(storedHashBytes, calculatedHash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new BusinessException(BasisErrorCode.PASSWORD_ENCRYPTION_FAILED);
        }
    }

    /**
     * 生成随机盐值。
     *
     * @return 随机生成的盐值字节数组
     */
    private static byte[] generateSalt() {
        // 创建指定长度的盐值字节数组
        byte[] salt = new byte[SALT_LENGTH];
        SECURE_RANDOM.nextBytes(salt);
        // 返回生成的盐值
        return salt;
    }
}
