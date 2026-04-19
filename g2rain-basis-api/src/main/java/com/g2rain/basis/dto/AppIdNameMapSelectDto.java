package com.g2rain.basis.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

/**
 * 请求体：批量获取应用 ID–名称映射
 * <p>
 * 用于批量查询应用对应的 ID 和名称
 * </p>
 *
 * @author alpha
 * @since 2026/1/20
 */
@Setter
@Getter
@NoArgsConstructor
@Schema(description = "请求体：批量获取应用 ID–名称映射")
public class AppIdNameMapSelectDto {

    /**
     * 应用 ID 集合
     */
    @NotEmpty
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "应用标识集合")
    private Set<Long> ids;
}
