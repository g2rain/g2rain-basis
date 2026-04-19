package com.g2rain.basis.dto;

import com.g2rain.common.model.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 控制域控制单元关联表查询DTO
 * 表名: control_domain_control_unit_relation
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "控制域控制单元关联 DTO")
public class ControlDomainControlUnitRelationDto extends BaseDto {

    /**
     * 控制域标识
     */
    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "控制域标识")
    private Long controlDomainId;

    /**
     * 控制单元标识
     */
    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "控制单元标识")
    private Long controlUnitId;
}
