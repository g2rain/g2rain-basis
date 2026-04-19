package com.g2rain.basis.dao.po;

import com.g2rain.common.model.BasePo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 机构表返回Po
 * 关联表名: organ
 * 功能：封装实体数据，继承BasePo复用基础字段逻辑
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrganPo extends BasePo {

    /**
     * 机构名称
     */
    private String organName;

    /**
     * 机构类型[服务商、渠道、公司、租户]
     */
    private String organType;

    /**
     * 机构状态[ACTIVE:有效, INACTIVE:无效]
     */
    private String status;

    /**
     * 运营标记
     */
    private Boolean admin;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    private Boolean deleteFlag;
}
