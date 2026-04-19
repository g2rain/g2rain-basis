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
 * 角色控制单元关联表查询 DTO
 * 表名: role_control_unit_relation
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "角色控制单元关联 DTO")
public class RoleControlUnitRelationDto extends BaseDto {

    /**
     * 角色标识
     */
    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "角色标识")
    private Long roleId;

    /**
     * 控制单元标识集合
     */
    @Schema(description = "添加控制单元标识集合")
    private Set<Long> controlUnitIds;

    /**
     * 删除控制单元标识集合
     */
    @Schema(description = "删除控制单元标识集合")
    private Set<Long> deleteControlUnitIds;
}
