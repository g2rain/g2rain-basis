package com.g2rain.basis.dao.po;

import com.g2rain.common.model.BasePo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 账号与外部身份源绑定表返回Po
 * 关联表名: passport_idp_binding
 * 功能：封装实体数据，继承BasePo复用基础字段逻辑
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PassportIdpBindingPo extends BasePo {

    /**
     * 账号标识，关联 passport.id
     */
    private Long passportId;

    /**
     * 身份源类型[DINGTALK:钉钉，可扩展 WECHAT 等]
     */
    private String idpType;

    /**
     * IdP 侧稳定主体标识，建议存钉钉 unionId
     */
    private String idpSubject;

    /**
     * 钉钉企业 corpId；企业内部模式可由 IAM 写入默认 corp
     */
    private String corpId;

    /**
     * 钉钉 userid（corp 内），可选，便于审计与运营排查
     */
    private String idpUserId;

    /**
     * 三方应用在 IdP 侧的应用标识（如钉钉 OAuth clientId），与 {@code application_idp_provision} 对齐
     */
    private String idpApplicationCode;

    /**
     * 接入形态[INTERNAL:企业内部应用, THIRD_PARTY:第三方企业应用]
     */
    private String bindMode;

    /**
     * IdP 返回的原始用户信息快照（可选）
     */
    private String rawProfile;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    private Boolean deleteFlag;
}