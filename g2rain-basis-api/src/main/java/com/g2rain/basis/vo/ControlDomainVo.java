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
 * 控制域表返回VO
 * 关联表名: control_domain
 * 功能：封装接口返回数据，继承BaseVo复用基础字段逻辑，隔离数据库实体与前端展示层
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "控制域 VO")
public class ControlDomainVo extends BaseVo {

    /**
     * 应用标识
     */
    @Schema(description = "应用标识")
    private Long applicationId;

    /**
     * 控制域名称
     */
    @Schema(description = "控制域名称")
    private String controlDomainName;

    /**
     * 控制域类型[TRADE("交易开通"), APPLICATION("应用授权开通")]
     */
    @Schema(description = "控制域类型[TRADE(\"交易开通\"), APPLICATION(\"应用授权开通\")]")
    private String controlDomainType;

    /**
     * 交付范围[CUSTOMER("客户交付"), OPERATION("平台运营")]
     */
    @Schema(description = "交付范围[CUSTOMER(\"客户交付\"), OPERATION(\"平台运营\")]")
    private String controlDomainScope;

    /**
     * 业务说明
     */
    @Schema(description = "业务说明")
    private String description;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    @ConditionalJsonIgnore(adminCompany = AdminCompanyCondition.TRUE)
    @Schema(description = "删除标识[0:未删除, 1:已删除]")
    private Boolean deleteFlag;
}
