package com.g2rain.basis.dao.po;

import com.g2rain.common.model.BasePo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 应用资源菜单表返回Po
 * 关联表名: resource_menu
 * 功能：封装实体数据，继承BasePo复用基础字段逻辑
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ResourceMenuPo extends BasePo {

    /**
     * 父菜单标识
     */
    private Long parentId;

    /**
     * 应用标识
     */
    private Long applicationId;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 菜单编码
     */
    private String menuCode;

    /**
     * 链接路径
     */
    private String linkPath;

    /**
     * 展示图标
     */
    private String icon;

    /**
     * 菜单排序
     */
    private Integer menuSortOrder;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    private Boolean deleteFlag;
}
