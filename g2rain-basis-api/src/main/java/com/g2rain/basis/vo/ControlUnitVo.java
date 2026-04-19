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
 * 控制单元表返回VO
 * 关联表名: control_unit
 * 功能：封装接口返回数据，继承BaseVo复用基础字段逻辑，隔离数据库实体与前端展示层
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "控制单元 VO")
public class ControlUnitVo extends BaseVo {

    /**
     * 应用标识
     */
    @Schema(description = "应用标识")
    private Long applicationId;

    /**
     * 控制单元名称
     */
    @Schema(description = "控制单元名称")
    private String controlUnitName;

    /**
     * 控制单元类型[OPERATION("运营功能"), CUSTOMER("客户功能"), PERPETUAL("永久有效功能")]
     */
    @Schema(description = "控制单元类型[OPERATION(\"运营功能\"), CUSTOMER(\"客户功能\"), PERPETUAL(\"永久有效功能\")]")
    private String controlUnitScope;

    /**
     * 默认控制单元
     */
    @Schema(description = "默认控制单元")
    private Boolean landing;

    /**
     * 控制单元状态
     */
    @Schema(description = "控制单元状态")
    private String status;

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
