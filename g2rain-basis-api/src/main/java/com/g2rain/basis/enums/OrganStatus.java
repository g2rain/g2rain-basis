package com.g2rain.basis.enums;


import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.utils.Strings;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 机构状态枚举。
 * <p>
 * 用于表示机构的状态，例如是否有效或可用。
 * 提供枚举值校验方法 {@link #validate(String)}，用于验证传入的字符串是否为合法枚举名称。
 * </p>
 *
 * <ul>
 *   <li>{@link #ACTIVE}：机构有效，可正常使用或操作。</li>
 *   <li>{@link #INACTIVE}：机构无效，不可使用或操作。</li>
 * </ul>
 *
 * <p>注意：{@link #validate(String)} 方法为通用校验，可用于任何需要验证枚举名称合法性的场景。</p>
 *
 * @author alpha
 * @since 2026/1/14
 */
@Schema(description = "机构状态枚举")
public enum OrganStatus {

    /**
     * 有效
     */
    @Schema(description = "有效")
    ACTIVE,

    /**
     * 无效
     */
    @Schema(description = "无效")
    INACTIVE;

    /**
     * 根据给定的字符串名称校验该名称是否存在于枚举中。
     * <p>
     * 如果字符串名称对应的枚举不存在，则抛出参数值无效的异常。
     * </p>
     *
     * @param name 枚举名称字符串，表示应用类型的名称。
     * @throws BusinessException 如果指定的枚举名称无效，抛出此异常，表示参数值无效。
     */
    public static void validate(String name) {
        if (Strings.isNotBlank(name)) {
            // 遍历所有枚举值，检查是否与传入的名称匹配
            for (OrganStatus type : values()) {
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
