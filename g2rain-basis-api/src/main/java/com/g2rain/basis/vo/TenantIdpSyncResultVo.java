package com.g2rain.basis.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 租户 IdP 同步结果 VO。
 */
@Setter
@Getter
@NoArgsConstructor
@Schema(description = "租户 IdP 同步结果")
public class TenantIdpSyncResultVo {

    @Schema(description = "新增部门数")
    private int departmentsCreated;

    @Schema(description = "更新部门数")
    private int departmentsUpdated;

    @Schema(description = "新增成员数")
    private int membersCreated;

    @Schema(description = "更新成员数")
    private int membersUpdated;

    @Schema(description = "新增绑定数")
    private int bindingsCreated;

    @Schema(description = "更新绑定数")
    private int bindingsUpdated;

    @Schema(description = "新增部门-成员关系数")
    private int departmentRelationsCreated;

    @Schema(description = "软删除成员数（FULL 模式下快照外成员）")
    private int membersDeleted;

    @Schema(description = "软删除绑定数（FULL 模式下快照外绑定）")
    private int bindingsDeleted;

    @Schema(description = "停用部门数（FULL 模式下快照外部门）")
    private int departmentsDisabled;

    @Schema(description = "移除部门-成员关系数（FULL 模式下关系对账）")
    private int departmentRelationsRemoved;

    @Schema(description = "本次新增的用户-角色关系数")
    private int rolesAssigned;

    @Schema(description = "耗时毫秒")
    private long elapsedMs;
}
