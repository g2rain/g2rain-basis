package com.g2rain.basis.enums;


import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.utils.Strings;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 个人静态访问令牌状态枚举。
 * <p>
 * 对应 {@code personal_static_access_token.status} 字段及数据字典 {@code STATIC_TOKEN_STATUS}。
 * </p>
 *
 * <ul>
 *   <li>{@link #ACTIVATED}：已启用，令牌可正常使用。</li>
 *   <li>{@link #REVOKED}：已吊销，令牌不可再使用。</li>
 * </ul>
 *
 * @author alpha
 * @since 2026/5/19
 */
@Schema(description = "静态访问令牌状态枚举")
public enum StaticTokenStatus {

    /**
     * 已启用
     */
    @Schema(description = "已启用")
    ACTIVATED,

    /**
     * 已吊销
     */
    @Schema(description = "已吊销")
    REVOKED;

    /**
     * 根据给定的字符串名称解析枚举值。
     *
     * @param name 枚举名称字符串
     * @return 匹配的枚举值
     * @throws BusinessException 枚举名称无效时抛出
     */
    public static StaticTokenStatus fromName(String name) {
        if (Strings.isNotBlank(name)) {
            for (StaticTokenStatus status : values()) {
                if (status.name().equals(name)) {
                    return status;
                }
            }
        }

        throw new BusinessException(
            SystemErrorCode.PARAM_VAL_INVALID,
            name
        );
    }

    /**
     * 校验给定的字符串名称是否为有效枚举值。
     *
     * @param name 枚举名称字符串
     * @throws BusinessException 枚举名称无效时抛出
     */
    public static void validate(String name) {
        fromName(name);
    }
}
