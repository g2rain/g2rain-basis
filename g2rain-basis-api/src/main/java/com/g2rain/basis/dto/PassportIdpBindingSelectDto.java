package com.g2rain.basis.dto;

import com.g2rain.common.model.BaseSelectListDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 账号与外部身份源绑定表查询入参DTO
 * 用于PassportIdpBindingDao.selectList方法的条件筛选
 * 表名: passport_idp_binding
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "账号与外部身份源绑定查询入参 DTO")
public class PassportIdpBindingSelectDto extends BaseSelectListDto {

    /**
     * 账号标识，关联 passport.id
     */
    @Schema(description = "账号标识，关联 passport.id")
    private Long passportId;

    /**
     * 身份源类型，与 {@link com.g2rain.basis.enums.IdpType} 一致。
     */
    @Schema(description = "身份源类型（与 IdpType 枚举名一致）",
        allowableValues = {"DINGTALK", "FEISHU", "WECHAT_WORK"})
    private String idpType;

    /**
     * IdP 侧稳定主体标识，建议存钉钉 unionId
     */
    @Schema(description = "IdP 侧稳定主体标识，建议存钉钉 unionId")
    private String idpSubject;

    /**
     * 钉钉企业 corpId；企业内部模式可由 IAM 写入默认 corp
     */
    @Schema(description = "钉钉企业 corpId；企业内部模式可由 IAM 写入默认 corp")
    private String corpId;

    /**
     * 钉钉 userid（corp 内），可选，便于审计与运营排查
     */
    @Schema(description = "钉钉 userid（corp 内），可选，便于审计与运营排查")
    private String idpUserId;

    /**
     * 三方应用在 IdP 侧的应用标识（如钉钉 OAuth clientId）
     */
    @Schema(description = "三方应用在 IdP 侧的应用标识（如钉钉 OAuth clientId）")
    private String idpApplicationCode;

    /**
     * 接入形态，与 {@link com.g2rain.basis.enums.IdpBindMode} 一致（企业内部应用 / 第三方应用换票链路）。
     */
    @Schema(description = "接入形态（与 IdpBindMode 枚举名一致）",
        allowableValues = {"INTERNAL", "THIRD_PARTY"})
    private String bindMode;

    /**
     * IdP 返回的原始用户信息快照（可选）
     */
    @Schema(description = "IdP 返回的原始用户信息快照（可选）")
    private String rawProfile;
}
