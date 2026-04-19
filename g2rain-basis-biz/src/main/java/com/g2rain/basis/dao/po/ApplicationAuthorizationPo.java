package com.g2rain.basis.dao.po;

import com.g2rain.common.model.BasePo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 应用授权记录表返回Po
 * 关联表名: application_authorization
 * 功能：封装实体数据，继承BasePo复用基础字段逻辑
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ApplicationAuthorizationPo extends BasePo {

    /**
     * 机构标识
     */
    private Long organId;

    /**
     * 应用标识
     */
    private Long applicationId;

    /**
     * 控制域标识
     */
    private Long controlDomainId;

    /**
     * 订阅标识
     */
    private Long subscriptionId;

    /**
     * 应用授权状态[ACTIVATED:激活, DEACTIVATED:关停]
     */
    private String status;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    private Boolean deleteFlag;
}
