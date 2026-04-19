package com.g2rain.basis.dao.po;

import com.g2rain.common.model.BasePo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 应用资源页面元素表返回Po
 * 关联表名: resource_page_element
 * 功能：封装实体数据，继承BasePo复用基础字段逻辑
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ResourcePageElementPo extends BasePo {

    /**
     * 应用标识
     */
    private Long applicationId;

    /**
     * 页面编码
     */
    private String pageCode;

    /**
     * 页面元素名称
     */
    private String pageElementName;

    /**
     * 页面元素编码
     */
    private String pageElementCode;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    private Boolean deleteFlag;
}
