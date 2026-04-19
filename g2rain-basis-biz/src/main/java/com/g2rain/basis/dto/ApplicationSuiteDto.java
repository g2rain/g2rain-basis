package com.g2rain.basis.dto;

import com.g2rain.common.model.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;


/**
 * 应用归类关系表查询DTO
 * 表名: application_suite
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "应用归类关系 DTO")
public class ApplicationSuiteDto extends BaseDto {

    /**
     * 应用标识
     */
    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "应用标识")
    private Long applicationId;

    /**
     * 主应用标识集合
     */
    @Schema(description = "主应用标识集合")
    private Set<Long> masterApplicationIds;

    /**
     * 删除主应用标识集合
     */
    @Schema(description = "删除主应用标识集合")
    private Set<Long> deleteMasterApplicationIds;
}
