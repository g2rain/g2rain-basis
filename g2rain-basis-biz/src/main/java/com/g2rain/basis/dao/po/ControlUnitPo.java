package com.g2rain.basis.dao.po;

import com.g2rain.common.model.BasePo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 控制单元表返回Po
 * 关联表名: control_unit
 * 功能：封装实体数据，继承BasePo复用基础字段逻辑
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ControlUnitPo extends BasePo {

    /**
     * 应用标识
     */
    private Long applicationId;

    /**
     * 控制单元名称
     */
    private String controlUnitName;

    /**
     * 控制单元类型[OPERATION("运营功能"), CUSTOMER("客户功能"), PERPETUAL("永久有效功能")]
     */
    private String controlUnitScope;

    /**
     * 默认控制单元
     */
    private Boolean landing;

    /**
     * 控制单元状态
     */
    private String status;

    /**
     * 业务说明
     */
    private String description;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    private Boolean deleteFlag;
}
