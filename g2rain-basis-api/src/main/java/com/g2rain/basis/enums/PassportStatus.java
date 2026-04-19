package com.g2rain.basis.enums;


import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.utils.Strings;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 通行证状态枚举。
 * <p>
 * 用于表示用户或实体的通行证状态，例如系统账号或访问凭证。
 * 提供了枚举值校验方法 {@link #validate(String)}，用于验证传入的字符串是否为合法枚举名称。
 * </p>
 *
 * <ul>
 *   <li>{@link #NORMAL}：状态正常，可正常使用。</li>
 *   <li>{@link #FROZEN}：状态冻结，暂时不可使用或访问。</li>
 * </ul>
 *
 * @author alpha
 * @since 2026/1/14
 */
@Schema(description = "通行证状态枚举")
public enum PassportStatus {

    /**
     * 正常
     */
    @Schema(description = "正常")
    NORMAL,

    /**
     * 冻结
     */
    @Schema(description = "冻结")
    FROZEN;

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
        if (Strings.isNotBlank(name)) {
            // 遍历所有枚举值，检查是否与传入的名称匹配
            for (PassportStatus type : values()) {
                // 如果找到匹配的枚举，直接返回
                if (type.name().equals(name)) {
                    return;
                }
            }
        }

        // 如果没有找到匹配的枚举，抛出业务异常
        throw new BusinessException(
            SystemErrorCode.PARAM_VAL_INVALID,
            name
        );
    }
}
