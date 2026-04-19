package com.g2rain.basis.enums;


import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.utils.Strings;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 资源状态枚举。
 * <p>
 * 用于表示系统资源的状态，可用于权限控制、界面显示或功能可用性判断。
 * </p>
 *
 * <ul>
 *   <li>{@link #VISIBLE}：资源可见，通常用于界面显示控制。</li>
 *   <li>{@link #ENABLED}：资源可用，表示功能可正常访问或操作。</li>
 * </ul>
 *
 * <p>注意：可根据具体业务区分“可见性”和“可用性”，二者可能不完全相同。</p>
 *
 * @author alpha
 * @since 2026/1/15
 */
@Schema(description = "资源状态枚举")
public enum ResourceStatus {

    /**
     * 显示
     */
    @Schema(description = "显示")
    VISIBLE,

    /**
     * 可用
     */
    @Schema(description = "可用")
    ENABLED;

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
            for (ResourceStatus type : values()) {
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
