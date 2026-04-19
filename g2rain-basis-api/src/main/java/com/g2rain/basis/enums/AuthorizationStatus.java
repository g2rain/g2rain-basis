package com.g2rain.basis.enums;


import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.utils.Strings;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 授权状态枚举。
 * <p>
 * 用于表示系统中应用授权或角色控制单元关联的状态。
 * 该状态会同时体现在 {@code application_authorization} 表和 {@code role_control_unit_relation} 表中，
 * 当授权状态变化时，关联表的状态也会联动更新。
 * </p>
 *
 * <ul>
 *   <li>{@link #ACTIVATED}：激活状态，表示授权或关联已启用，可正常使用。</li>
 *   <li>{@link #DEACTIVATED}：关停状态，表示授权或关联已停用，不可使用。</li>
 * </ul>
 *
 * @author alpha
 * @since 2026/1/15
 */
@Schema(description = "授权状态枚举")
public enum AuthorizationStatus {

    /**
     * 激活
     */
    @Schema(description = "激活")
    ACTIVATED,

    /**
     * 关停
     */
    @Schema(description = "关停")
    DEACTIVATED;

    /**
     * 根据给定的字符串名称校验该名称是否存在于枚举中。
     * <p>
     * 如果字符串名称对应的枚举不存在，则抛出参数值无效的异常。
     * </p>
     *
     * @param name 枚举名称字符串，表示应用类型的名称。
     * @throws BusinessException 如果指定的枚举名称无效，抛出此异常，表示参数值无效。
     */
    public static AuthorizationStatus fromName(String name) {
        if (Strings.isNotBlank(name)) {
            // 遍历所有枚举值，检查是否与传入的名称匹配
            for (AuthorizationStatus type : values()) {
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
