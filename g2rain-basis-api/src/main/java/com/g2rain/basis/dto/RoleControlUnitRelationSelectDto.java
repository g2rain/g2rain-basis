package com.g2rain.basis.dto;

import com.g2rain.common.model.BaseSelectListDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;


/**
 * 角色控制单元关联表查询入参DTO
 * 用于RoleControlUnitRelationDao.selectList方法的条件筛选
 * 表名: role_control_unit_relation
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "角色控制单元关联查询入参 DTO")
public class RoleControlUnitRelationSelectDto extends BaseSelectListDto {

    /**
     * 角色标识
     */
    @Schema(description = "角色标识")
    private Long roleId;

    /**
     * 控制单元标识
     */
    @Schema(description = "控制单元标识")
    private Long controlUnitId;

    /**
     * 控制单元标识集合
     */
    @Schema(description = "控制单元标识集合")
    private Set<Long> controlUnitIds;

    /**
     * 应用授权标识
     */
    @Schema(description = "应用授权标识")
    private Long applicationAuthorizationId;

    @Schema(description = "应用授权标识为空标记", hidden = true)
    private boolean applicationAuthorizationIdIsNull;

    /**
     * 控制单元状态[ACTIVATED:激活, DEACTIVATED:关停]
     */
    @Schema(description = "控制单元状态[ACTIVATED:激活, DEACTIVATED:关停]", allowableValues = {"ACTIVATED", "DEACTIVATED"})
    private String status;
}
