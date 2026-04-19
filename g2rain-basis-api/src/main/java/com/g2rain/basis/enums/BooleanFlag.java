package com.g2rain.basis.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 布尔标记枚举。
 * <p>
 * 用于表示简单的布尔状态或标记，{@link #TRUE} 表示为真或启用，{@link #FALSE} 表示为假或禁用。
 * 可在多种场景中复用，例如功能开关、标识字段等。
 * </p>
 *
 * @author alpha
 * @since 2026/1/15
 */
@Schema(description = "布尔标记枚举(TRUE / FALSE)")
public enum BooleanFlag {

    /**
     * 假 / 否 / 禁用
     */
    @Schema(description = "假 / 否 / 禁用")
    FALSE,

    /**
     * 真 / 是 / 启用
     */
    @Schema(description = "真 / 是 / 启用")
    TRUE
}
