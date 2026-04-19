package com.g2rain.basis.enums;


import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.utils.Strings;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 应用类型枚举类，表示不同的应用类型。
 * <p>
 * 该枚举类包含五种应用类型：支撑应用、系统应用、开放平台应用和私有平台应用。
 * </p>
 *
 * @author alpha
 * @since 2026/1/10
 */
@Schema(description = "应用类型枚举")
public enum ApplicationType {

    /**
     * 微前端子应用，属于支撑系统，提供平台管理功能。
     * <p>
     * 该类型应用主要供开发和产品人员使用，用于维护应用、字典、路由等信息。
     * 运营后台子应用也属于此类型，供运营人员使用。
     * </p>
     */
    @Schema(description = "支撑应用(微前端子应用, 平台管理/运营后台等)")
    SUPPORT,

    /**
     * 微前端子应用，由平台开发并供客户使用。
     * <p>
     * 该类型应用是平台提供的功能，供客户直接使用。
     * </p>
     */
    @Schema(description = "系统应用(微前端子应用, 平台提供给客户使用)")
    SYSTEM,

    /**
     * 开放平台应用，第三方开发并供客户使用的应用。
     * <p>
     * 该类型应用是由外部开发者开发的，供平台客户使用。
     * </p>
     */
    @Schema(description = "开放平台应用(第三方开发供客户使用)")
    PUBLIC,

    /**
     * 开放平台应用，租户自己实现的应用。
     * <p>
     * 该类型应用是租户自己根据平台提供的能力开发的应用。
     * </p>
     */
    @Schema(description = "私有平台应用(租户自研应用)")
    PRIVATE;

    /**
     * 根据给定的字符串名称校验该名称是否存在于枚举中。
     * <p>
     * 如果字符串名称对应的枚举不存在，则抛出参数值无效的异常。
     * </p>
     *
     * @param name 枚举名称字符串，表示应用类型的名称。
     * @throws BusinessException 如果指定的枚举名称无效，抛出此异常，表示参数值无效。
     */
    public static ApplicationType fromName(String name) {
        if (Strings.isNotBlank(name)) {
            // 遍历所有枚举值，检查是否与传入的名称匹配
            for (ApplicationType type : values()) {
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

    public static boolean nonMicroApp(ApplicationType type) {
        return PUBLIC.equals(type) || PRIVATE.equals(type);
    }
}
