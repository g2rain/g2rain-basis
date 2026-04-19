package com.g2rain.basis.dto;

import com.g2rain.common.model.BaseSelectListDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 控制域控制单元关联表查询入参DTO
 * 用于ControlDomainControlUnitRelationDao.selectList方法的条件筛选
 * 表名: control_domain_control_unit_relation
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "控制域控制单元关联查询入参 DTO")
public class ControlDomainControlUnitRelationSelectDto extends BaseSelectListDto {

    /**
     * 控制域标识
     */
    @Schema(description = "控制域标识")
    private Long controlDomainId;

    /**
     * 控制单元标识
     */
    @Schema(description = "控制单元标识")
    private Long controlUnitId;
}
