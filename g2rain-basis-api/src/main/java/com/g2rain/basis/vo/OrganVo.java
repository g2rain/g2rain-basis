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
 * 机构表返回VO
 * 关联表名: organ
 * 功能：封装接口返回数据，继承BaseVo复用基础字段逻辑，隔离数据库实体与前端展示层
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "机构 VO")
public class OrganVo extends BaseVo {

    /**
     * 机构名称
     */
    @Schema(description = "机构名称")
    private String organName;

    /**
     * 机构类型[服务商、渠道、公司、租户]
     */
    @Schema(description = "机构类型[服务商、渠道、公司、租户]")
    private String organType;

    /**
     * 机构状态[ACTIVE:有效, INACTIVE:无效]
     */
    @Schema(description = "机构状态[ACTIVE:有效, INACTIVE:无效]")
    private String status;

    /**
     * 运营标记
     */
    @Schema(description = "运营标记")
    private Boolean admin;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    @ConditionalJsonIgnore(adminCompany = AdminCompanyCondition.TRUE)
    @Schema(description = "删除标识[0:未删除, 1:已删除]")
    private Boolean deleteFlag;
}
