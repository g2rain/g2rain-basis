package com.g2rain.basis.dto;

import com.g2rain.common.model.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.g2rain.common.validation.CreateGroup;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 匿名接入码表查询DTO
 * 表名: anonymous_entry
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "匿名接入码表 DTO")
public class AnonymousEntryDto extends BaseDto {

    /**
     * 机构标识（租户机构）
     */
    @NotNull(groups = CreateGroup.class)
    @Schema(description = "机构标识（租户机构）（新增必填）")
    private Long organId;

    /**
     * 应用标识
     */
    @NotNull(groups = CreateGroup.class)
    @Schema(description = "应用标识（新增必填）")
    private Long applicationId;

    /**
     * 接入码名称
     */
    @NotBlank(groups = CreateGroup.class)
    @Size(max = 64)
    @Schema(description = "接入码名称（新增必填）", maxLength = 64)
    private String entryName;
}
