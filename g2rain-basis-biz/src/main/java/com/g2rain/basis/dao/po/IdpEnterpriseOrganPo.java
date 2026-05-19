package com.g2rain.basis.dao.po;

import com.g2rain.common.model.BasePo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 外部企业/租户与平台机构关联表返回Po
 * 关联表名: idp_enterprise_organ
 * 功能：封装实体数据，继承BasePo复用基础字段逻辑
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class IdpEnterpriseOrganPo extends BasePo {

    /**
     * 身份源类型[DINGTALK, WECHAT_WORK, FEISHU, ...]
     */
    private String idpType;

    /**
     * 外部企业/租户标识（与 passport_idp_binding.enterprise_id 一致）
     */
    private String enterpriseId;

    /**
     * 机构标识，关联 organ.id（业务上应为租户类型机构）
     */
    private Long organId;

    /**
     * 状态[ACTIVE:有效, INACTIVE:停用]
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    private Boolean deleteFlag;
}