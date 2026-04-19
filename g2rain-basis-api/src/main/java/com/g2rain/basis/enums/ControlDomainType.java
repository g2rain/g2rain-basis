package com.g2rain.basis.enums;


import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.utils.Strings;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 控制域类型枚举。
 * <p>
 * 用于区分系统中不同类型的控制域，决定权限或功能的开通范围。
 * </p>
 *
 * <ul>
 *   <li>{@link #TRADE}：交易开通类型，表示与交易相关的功能开通。</li>
 *   <li>{@link #APPLICATION}：应用授权开通类型，表示与应用权限或授权相关的功能开通。</li>
 * </ul>
 *
 * <p>提供方法 {@link #fromName(String)} 用于根据字符串名称获取枚举实例，并校验其合法性。</p>
 *
 * <p>如果传入的名称无效，将抛出 {@link BusinessException}。</p>
 *
 * @author alpha
 * @since 2026/1/14
 */
@Schema(description = "控制域类型枚举")
public enum ControlDomainType {

    /**
     * 交易开通
     */
    @Schema(description = "交易开通类型")
    TRADE,

    /**
     * 应用授权开通
     */
    @Schema(description = "应用授权开通类型")
    APPLICATION;

    /**
     * 根据给定的字符串名称校验该名称是否存在于枚举中。
     * <p>
     * 如果字符串名称对应的枚举不存在，则抛出参数值无效的异常。
     * </p>
     *
     * @param name 枚举名称字符串，表示应用类型的名称。
     * @throws BusinessException 如果指定的枚举名称无效，抛出此异常，表示参数值无效。
     */
    public static ControlDomainType fromName(String name) {
        if (Strings.isNotBlank(name)) {
            // 遍历所有枚举值，检查是否与传入的名称匹配
            for (ControlDomainType type : values()) {
                // 如果找到匹配的枚举，直接返回
                if (type.name().equals(name)) {
                    return type;
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
