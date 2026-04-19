package com.g2rain.basis.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

/**
 * <p>角色与控制单元关联关系</p>
 *
 * <p>用于表示角色与控制单元的关系，包含角色 ID、控制单元 ID 集合和应用授权 ID。</p>
 *
 * <p><strong>注意：</strong>该对象可用于角色权限控制，并绑定特定的控制单元。</p>
 *
 * <p><em>Author:</em> alpha</p>
 * <p><em>Since:</em> 2026/1/19</p>
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoleControlUnitRelation {

    /**
     * 角色标识
     */
    private Long roleId;

    /**
     * 控制单元标识集合
     */
    private Set<Long> controlUnitIds;

    /**
     * 应用授权标识
     */
    private Long applicationAuthorizationId;

    public RoleControlUnitRelation(Long roleId, Set<Long> controlUnitIds) {
        this.roleId = roleId;
        this.controlUnitIds = controlUnitIds;
    }
}
