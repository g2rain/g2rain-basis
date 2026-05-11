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
 * 外部企业/租户与平台机构关联表返回VO
 * 关联表名: idp_enterprise_organ
 * 功能：封装接口返回数据，继承BaseVo复用基础字段逻辑，隔离数据库实体与前端展示层
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "外部企业/租户与平台机构关联 VO")
public class IdpEnterpriseOrganVo extends BaseVo {

    /**
     * 身份源类型[DINGTALK, WECHAT_WORK, FEISHU, ...]
     */
    @Schema(description = "身份源类型[DINGTALK, WECHAT_WORK, FEISHU, ...]")
    private String idpType;

    /**
     * 外部企业/租户标识（与 passport_idp_binding.enterprise_id 一致）
     */
    @Schema(description = "外部企业/租户标识（与 passport_idp_binding.enterprise_id 一致）")
    private String enterpriseId;

    /**
     * 机构标识，关联 organ.id（业务上应为租户类型机构）
     */
    @Schema(description = "机构标识，关联 organ.id（业务上应为租户类型机构）")
    private Long organId;

    /**
     * 状态[ACTIVE:有效, INACTIVE:停用]
     */
    @Schema(description = "状态[ACTIVE:有效, INACTIVE:停用]", allowableValues = {"ACTIVE", "INACTIVE"})
    private String status;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    @ConditionalJsonIgnore(adminCompany = AdminCompanyCondition.TRUE)
    @Schema(description = "删除标识[0:未删除, 1:已删除]")
    private Boolean deleteFlag;
}
