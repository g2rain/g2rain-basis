package com.g2rain.basis.idp.resolve.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * IdP 登录 Passport 解析请求。
 */
@Setter
@Getter
@NoArgsConstructor
@Schema(description = "IdP 登录 Passport 解析请求")
public class IdpPassportResolveRequest {

    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "身份源类型（IdpType 枚举名）")
    private String idpType;

    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "IdP 稳定主体（如钉钉 unionId）")
    private String idpSubject;

    @Schema(description = "三方应用在 IdP 侧的应用标识（如钉钉 OAuth clientId）")
    private String idpApplicationCode;

    @Schema(description = "IdP 企业内用户标识（钉钉 userid），未命中 unionId 时作为次级 lookup")
    private String idpUserId;
}
