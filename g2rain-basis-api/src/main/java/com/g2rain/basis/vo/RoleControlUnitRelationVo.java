package com.g2rain.basis.vo;

import com.g2rain.common.json.AdminCompanyCondition;
import com.g2rain.common.json.ConditionalJsonIgnore;
import com.g2rain.common.model.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 角色控制单元关联表返回VO
 * 关联表名: role_control_unit_relation
 * 功能：封装接口返回数据，继承BaseVo复用基础字段逻辑，隔离数据库实体与前端展示层
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "角色控制单元关联 VO")
public class RoleControlUnitRelationVo extends BaseVo {

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
     * 控制单元名称
     */
    @Schema(description = "控制单元名称")
    private String controlUnitName;

    /**
     * 应用授权标识
     */
    @Schema(description = "应用授权标识")
    private Long applicationAuthorizationId;

    /**
     * 控制单元状态[ACTIVATED:激活, DEACTIVATED:关停]
     */
    @Schema(description = "控制单元状态[ACTIVATED:激活, DEACTIVATED:关停]")
    private String status;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    @ConditionalJsonIgnore(adminCompany = AdminCompanyCondition.TRUE)
    @Schema(description = "删除标识[0:未删除, 1:已删除]")
    private Boolean deleteFlag;
}
