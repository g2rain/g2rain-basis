package com.g2rain.basis.dao.po;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 统计角色控制单元数量
 * 关联表名: role_control_unit_relation
 * 功能：封装实体数据，继承BasePo复用基础字段逻辑
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
public class CountRoleControlUnitPo {

    /**
     * 角色控制单元数量
     */
    private Long totalControlUnitCount;

    /**
     * 有效角色控制单元数量
     */
    private Long activeControlUnitCount;
}
