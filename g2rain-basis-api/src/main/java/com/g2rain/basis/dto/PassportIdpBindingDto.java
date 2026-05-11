package com.g2rain.basis.dto;

import com.g2rain.common.model.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 账号与外部身份源绑定表查询DTO
 * 表名: passport_idp_binding
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "账号与外部身份源绑定新增或更新 DTO")
public class PassportIdpBindingDto extends BaseDto {

    /**
     * 账号标识，关联 passport.id
     */
    @Schema(description = "账号标识，关联 passport.id")
    private Long passportId;

    /**
     * 身份源类型[DINGTALK:钉钉，可扩展 WECHAT 等]
     */
    @Schema(description = "身份源类型[DINGTALK:钉钉，可扩展 WECHAT 等]")
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
     * 接入形态[INTERNAL:企业内部应用, THIRD_PARTY:第三方企业应用]
     */
    @Schema(description = "接入形态[INTERNAL:企业内部应用, THIRD_PARTY:第三方企业应用]",
        allowableValues = {"INTERNAL", "THIRD_PARTY"})
    private String bindMode;

    /**
     * IdP 返回的原始用户信息快照（可选）
     */
    @Schema(description = "IdP 返回的原始用户信息快照（可选）")
    private String rawProfile;
}
