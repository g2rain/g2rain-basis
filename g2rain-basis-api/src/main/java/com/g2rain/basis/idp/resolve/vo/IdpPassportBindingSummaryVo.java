package com.g2rain.basis.idp.resolve.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * IdP 绑定摘要，供 IAM 登录链路使用。
 */
@Setter
@Getter
@NoArgsConstructor
@Schema(description = "IdP 绑定摘要")
public class IdpPassportBindingSummaryVo {

    @Schema(description = "绑定记录 ID")
    private Long id;

    @Schema(description = "账号 ID")
    private Long passportId;

    @Schema(description = "身份源类型")
    private String idpType;

    @Schema(description = "IdP 稳定主体")
    private String idpSubject;

    @Schema(description = "企业 corpId")
    private String corpId;

    @Schema(description = "IdP 企业内用户 ID")
    private String idpUserId;

    @Schema(description = "IdP 开放平台 openId")
    private String idpOpenId;

    @Schema(description = "IdP 应用标识")
    private String idpApplicationCode;

    @Schema(description = "接入形态")
    private String bindMode;
}
