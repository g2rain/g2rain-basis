package com.g2rain.basis.enums;


import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.utils.Strings;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 应用状态枚举类，表示应用的发布状态。
 * <p>
 * 该枚举类包含两种状态：已发布和未发布。
 * </p>
 *
 * @author alpha
 * @since 2026/1/10
 */
@Schema(description = "应用状态枚举(发布状态)")
public enum ApplicationStatus {

    /**
     * 已发布状态，表示应用已经正式发布并且客户可以使用。
     */
    @Schema(description = "已发布")
    PUBLISHED,

    /**
     * 未发布状态，表示应用尚未正式发布，无法供客户使用。
     */
    @Schema(description = "未发布")
    UNPUBLISHED;

    /**
     * 根据给定的字符串名称校验该名称是否存在于枚举中。
     * <p>
     * 如果字符串名称对应的枚举不存在，则抛出参数值无效的异常。
     * </p>
     *
     * @param name 枚举名称字符串，表示应用状态的名称。
     * @throws BusinessException 如果指定的枚举名称无效，抛出此异常，表示参数值无效。
     */
    public static void validate(String name) {
        if (Strings.isNotBlank(name)) {
            // 遍历所有枚举值，检查是否与传入的名称匹配
            for (ApplicationStatus status : values()) {
                // 如果找到匹配的枚举，直接返回
                if (status.name().equals(name)) {
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
