package com.g2rain.basis.dao.po;

import com.g2rain.common.model.BasePo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 控制域控制单元关联表返回Po
 * 关联表名: control_domain_control_unit_relation
 * 功能：封装实体数据，继承BasePo复用基础字段逻辑
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ControlDomainControlUnitRelationPo extends BasePo {

    /**
     * 控制域标识
     */
    private Long controlDomainId;

    /**
     * 控制单元标识
     */
    private Long controlUnitId;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    private Boolean deleteFlag;
}
