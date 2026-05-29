package com.g2rain.basis.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 生成机构邀请码请求 DTO
 */
@Setter
@Getter
@NoArgsConstructor
@Schema(name = "OrganInviteGenerateDto", description = "生成机构邀请码请求")
public class OrganInviteGenerateDto {

    @NotNull
    @Schema(
        description = "目标机构 ID",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "10001"
    )
    private Long organId;

    @NotNull
    @Schema(
        description = "加入后分配的角色 ID（须属于目标机构）",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "20001"
    )
    private Long roleId;

    @Min(1)
    @Max(365)
    @Schema(description = "有效天数，默认 7", example = "7", minimum = "1", maximum = "365")
    private Integer validDays = 7;
}
