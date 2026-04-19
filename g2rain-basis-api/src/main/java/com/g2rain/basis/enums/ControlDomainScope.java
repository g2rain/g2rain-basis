package com.g2rain.basis.enums;


import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.utils.Strings;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 控制域作用域枚举。
 * <p>
 * 用于区分控制域在系统中的使用范围或功能归属。
 * </p>
 *
 * <ul>
 *   <li>{@link #CUSTOMER}：客户交付范围，表示面向最终客户的功能或权限。</li>
 *   <li>{@link #OPERATION}：平台运营范围，表示面向平台运营或管理后台的功能或权限。</li>
 * </ul>
 *
 * <p>提供方法 {@link #fromName(String)} 用于根据字符串名称获取枚举实例，并校验其合法性。</p>
 *
 * <p>如果传入的名称无效，将抛出 {@link BusinessException}。</p>
 *
 * @author alpha
 * @since 2026/1/14
 */
@Schema(description = "控制域作用域枚举")
public enum ControlDomainScope {

    /**
     * 客户交付
     */
    @Schema(description = "客户交付范围")
    CUSTOMER,

    /**
     * 平台运营
     */
    @Schema(description = "平台运营范围")
    OPERATION;

    /**
     * 根据给定的字符串名称校验该名称是否存在于枚举中。
     * <p>
     * 如果字符串名称对应的枚举不存在，则抛出参数值无效的异常。
     * </p>
     *
     * @param name 枚举名称字符串，表示应用类型的名称。
     * @throws BusinessException 如果指定的枚举名称无效，抛出此异常，表示参数值无效。
     */
    public static ControlDomainScope fromName(String name) {
        if (Strings.isNotBlank(name)) {
            // 遍历所有枚举值，检查是否与传入的名称匹配
            for (ControlDomainScope type : values()) {
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
