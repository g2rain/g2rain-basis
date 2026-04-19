package com.g2rain.basis.dao.po;

import com.g2rain.common.model.BasePo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 控制域表返回Po
 * 关联表名: control_domain
 * 功能：封装实体数据，继承BasePo复用基础字段逻辑
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ControlDomainPo extends BasePo {

    /**
     * 应用标识
     */
    private Long applicationId;

    /**
     * 控制域名称
     */
    private String controlDomainName;

    /**
     * 控制域类型[TRADE("交易开通"), APPLICATION("应用授权开通")]
     */
    private String controlDomainType;

    /**
     * 交付范围[CUSTOMER("客户交付"), OPERATION("平台运营")]
     */
    private String controlDomainScope;

    /**
     * 业务说明
     */
    private String description;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    private Boolean deleteFlag;
}
