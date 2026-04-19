package com.g2rain.basis.dto;

import com.g2rain.common.model.BaseSelectListDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 控制域表查询入参DTO
 * 用于ControlDomainDao.selectList方法的条件筛选
 * 表名: control_domain
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "控制域查询入参 DTO")
public class ControlDomainSelectDto extends BaseSelectListDto {

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
    @Schema(description = "控制域类型[TRADE(\"交易开通\"), APPLICATION(\"应用授权开通\")]", allowableValues = {"TRADE", "APPLICATION"})
    private String controlDomainType;

    /**
     * 交付范围[CUSTOMER("客户交付"), OPERATION("平台运营")]
     */
    @Schema(description = "交付范围[CUSTOMER(\"客户交付\"), OPERATION(\"平台运营\")]", allowableValues = {"CUSTOMER", "OPERATION"})
    private String controlDomainScope;

    /**
     * 业务说明
     */
    @Schema(description = "业务说明")
    private String description;
}
