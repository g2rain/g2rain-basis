package com.g2rain.basis.enums;


import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.utils.Strings;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 性别枚举类型。
 * <p>
 * 用于表示用户或相关实体的性别，目前支持男性和女性。
 * 提供了枚举值校验方法，可用于验证传入的字符串是否为合法枚举名称。
 * </p>
 *
 * @author alpha
 * @since 2026/1/14
 */
@Schema(description = "性别枚举")
public enum Sex {

    /**
     * 男性
     */
    @Schema(description = "男性")
    MALE,

    /**
     * 女性
     */
    @Schema(description = "女性")
    FEMALE;

    /**
     * 校验指定的字符串是否为合法的枚举名称。
     * <p>
     * 如果 {@code name} 为 {@code null} 或空字符串，则认为合法。
     * 否则遍历枚举的所有值，如果找到匹配的名称则校验通过；
     * 如果未找到匹配值，将抛出业务异常 {@link BusinessException}。
     * </p>
     *
     * @param name 待校验的枚举名称字符串
     * @throws BusinessException 如果 {@code name} 不为空且不是合法的枚举名称
     */
    public static void validate(String name) {
        if (Strings.isBlank(name)) {
            return;
        }

        // 遍历所有枚举值，检查是否与传入的名称匹配
        for (Sex type : values()) {
            // 如果找到匹配的枚举，直接返回
            if (type.name().equals(name)) {
                return;
            }
        }

        // 如果没有找到匹配的枚举，抛出业务异常
        throw new BusinessException(
            SystemErrorCode.PARAM_VAL_INVALID,
            name
        );
    }
}
