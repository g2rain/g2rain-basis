package com.g2rain.basis.dao.po;

import com.g2rain.common.model.BasePo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 角色表返回Po
 * 关联表名: role
 * 功能：封装实体数据，继承BasePo复用基础字段逻辑
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RolePo extends BasePo {

    /**
     * 机构标识
     */
    private Long organId;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色类型[ADMIN:默认角色-只读, USER:用户角色]
     */
    private String roleType;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    private Boolean deleteFlag;
}
