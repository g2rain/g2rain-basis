package com.g2rain.basis.dao.po;

import com.g2rain.common.model.BasePo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 权限点资源关联表返回Po
 * 关联表名: control_unit_resource_relation
 * 功能：封装实体数据，继承BasePo复用基础字段逻辑
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ControlUnitResourceRelationPo extends BasePo {

    /**
     * 控制单元标识
     */
    private Long controlUnitId;

    /**
     * 资源标识
     */
    private Long resourceId;

    /**
     * 资源类型[MENU:菜单, PAGE:页面, PAGE_ELEMENT:页面元素, API_ENDPOINT:接口地址]
     */
    private String resourceType;

    /**
     * 激活状态[VISIBLE:显示, ENABLED:可用]
     */
    private String status;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    private Boolean deleteFlag;
}
