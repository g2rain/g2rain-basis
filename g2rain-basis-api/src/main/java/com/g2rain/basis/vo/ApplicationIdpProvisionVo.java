package com.g2rain.basis.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.g2rain.common.model.BaseVo;

import com.g2rain.common.json.ConditionalJsonIgnore;
import com.g2rain.common.json.AdminCompanyCondition;

/**
 * 外部身份源应用与平台应用的绑定返回VO
 * 关联表名: application_idp_provision
 * 功能：封装接口返回数据，继承BaseVo复用基础字段逻辑，隔离数据库实体与前端展示层
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ApplicationIdpProvisionVo extends BaseVo {

    /**
     * 平台应用标识，关联 application.id
     */
    private Long applicationId;

    /**
     * 身份源类型，与 IdpType 枚举名一致
     */
    private String idpType;

    /**
     * 三方应用在 IdP 侧的标识（如钉钉 OAuth clientId）
     */
    private String idpApplicationCode;

    /**
     * 备注
     */
    private String remark;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    @ConditionalJsonIgnore(adminCompany = AdminCompanyCondition.TRUE)
    private Boolean deleteFlag;
}