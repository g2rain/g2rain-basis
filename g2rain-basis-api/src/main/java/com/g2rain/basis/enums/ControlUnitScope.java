package com.g2rain.basis.enums;


import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.utils.Strings;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 控制单元作用域枚举。
 * <p>
 * 用于区分控制单元的功能类型或有效范围：
 * </p>
 *
 * <ul>
 *   <li>{@link #CUSTOMER}：客户功能，面向最终客户的功能。</li>
 *   <li>{@link #OPERATION}：运营功能，面向运营或管理后台的功能。</li>
 *   <li>{@link #PERPETUAL}：永久有效，表示控制单元长期有效，不受限制。</li>
 * </ul>
 *
 * <p>提供方法 {@link #fromName(String)} 用于根据字符串名称获取枚举实例，并校验其合法性。</p>
 *
 * <p>如果传入的名称无效，将抛出 {@link BusinessException}。</p>
 *
 * @author alpha
 * @since 2026/1/14
 */
@Schema(description = "控制单元作用域枚举")
public enum ControlUnitScope {

    /**
     * 客户功能
     */
    @Schema(description = "客户功能（面向最终客户）")
    CUSTOMER,

    /**
     * 运营功能
     */
    @Schema(description = "运营功能（面向运营/管理后台）")
    OPERATION,

    /**
     * 永久有效
     */
    @Schema(description = "永久有效")
    PERPETUAL;

    /**
     * 根据给定的字符串名称校验该名称是否存在于枚举中。
     * <p>
     * 如果字符串名称对应的枚举不存在，则抛出参数值无效的异常。
     * </p>
     *
     * @param name 枚举名称字符串，表示应用类型的名称。
     * @throws BusinessException 如果指定的枚举名称无效，抛出此异常，表示参数值无效。
     */
    public static ControlUnitScope fromName(String name) {
        if (Strings.isNotBlank(name)) {
            // 遍历所有枚举值，检查是否与传入的名称匹配
            for (ControlUnitScope type : values()) {
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
