package com.g2rain.basis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 已登录通行证绑定外部身份源（钉钉扫码）请求 DTO
 */
@Setter
@Getter
@NoArgsConstructor
@Schema(description = "通行证绑定外部身份源请求 DTO")
public class PassportIdpBindingBindDto {

    @NotNull
    @Schema(description = "账号ID")
    private Long passportId;

    @NotNull
    @Schema(description = "组织机构ID")
    private Long organId;

    @NotBlank
    @Schema(description = "身份源类型（与 IdpType 枚举名一致）")
    private String idpType;

    @NotBlank
    @Schema(description = "IdP 侧稳定主体标识（钉钉 unionId）")
    private String idpSubject;

    @Schema(description = "钉钉企业 corpId")
    private String corpId;

    @Schema(description = "钉钉 userid（corp 内）")
    private String idpUserId;

    @NotBlank
    @Schema(description = "三方应用在 IdP 侧的应用标识")
    private String idpApplicationCode;

    @NotBlank
    @Schema(description = "接入形态（与 IdpBindMode 枚举名一致）")
    private String bindMode;

    @Schema(description = "IdP 返回的原始用户信息快照")
    private String rawProfile;

    @Schema(description = "发起绑定时的会话类型（与 SessionType 枚举名一致，IAM 扫码绑定时传入）")
    private String sessionType;

    @Schema(description = "发起绑定时是否为机构管理员（user.admin，IAM 扫码绑定时传入）")
    private Boolean adminUser;
}
