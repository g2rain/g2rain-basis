package com.g2rain.basis.idp.sync.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 拉取 IdP 通讯录快照请求。
 */
@Setter
@Getter
@NoArgsConstructor
@Schema(description = "拉取 IdP 通讯录快照请求")
public class IdpFetchSnapshotRequest {

    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "企业/租户标识（钉钉 corpId）")
    private String corpId;

    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "接入形态（IdpBindMode 枚举名）",
        allowableValues = {"INTERNAL", "THIRD_PARTY"})
    private String bindMode;

    @Schema(description = "IdP 侧应用标识（钉钉 clientId）；为空时由 IAM 按 bindMode 从配置解析")
    private String idpApplicationCode;
}
