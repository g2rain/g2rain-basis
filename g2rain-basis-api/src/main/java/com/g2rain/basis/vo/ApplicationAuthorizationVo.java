package com.g2rain.basis.vo;

import com.g2rain.common.json.AdminCompanyCondition;
import com.g2rain.common.json.ConditionalJsonIgnore;
import com.g2rain.common.model.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 应用授权记录表返回VO
 * 关联表名: application_authorization
 * 功能：封装接口返回数据，继承BaseVo复用基础字段逻辑，隔离数据库实体与前端展示层
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "应用授权记录 VO")
public class ApplicationAuthorizationVo extends BaseVo {

    /**
     * 机构标识
     */
    @Schema(description = "机构标识")
    private Long organId;

    /**
     * 应用标识
     */
    @Schema(description = "应用标识")
    private Long applicationId;

    /**
     * 控制域标识
     */
    @Schema(description = "控制域标识")
    private Long controlDomainId;

    /**
     * 控制域标识
     */
    @Schema(description = "控制域标识")
    private String controlDomainName;

    /**
     * 默认控制域描述
     */
    @Schema(description = "默认控制域描述")
    private String controlDomainDesc;

    /**
     * 订阅标识
     */
    @Schema(description = "订阅标识")
    private Long subscriptionId;

    /**
     * 应用授权状态[ACTIVATED:激活, DEACTIVATED:关停]
     */
    @Schema(description = "应用授权状态[ACTIVATED:激活, DEACTIVATED:关停]")
    private String status;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    @ConditionalJsonIgnore(adminCompany = AdminCompanyCondition.TRUE)
    @Schema(description = "删除标识[0:未删除, 1:已删除]")
    private Boolean deleteFlag;
}
