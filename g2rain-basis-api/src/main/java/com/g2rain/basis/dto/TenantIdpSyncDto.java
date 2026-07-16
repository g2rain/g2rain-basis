package com.g2rain.basis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 租户 IdP 成员/部门同步请求 DTO。
 */
@Setter
@Getter
@NoArgsConstructor
@Schema(description = "租户 IdP 同步请求 DTO")
public class TenantIdpSyncDto {

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "机构标识")
    private Long organId;

    @Schema(description = "身份源类型，默认 DINGTALK",
        allowableValues = {"DINGTALK", "FEISHU", "WECHAT_WORK"})
    private String idpType = "DINGTALK";

    @Schema(description = "接入形态；为空时从 idp_enterprise_organ 记录读取",
        allowableValues = {"INTERNAL", "THIRD_PARTY"})
    private String bindMode;

    @Schema(description = "同步模式，默认 FULL（全量对账）",
        allowableValues = {"FULL", "INCREMENTAL"})
    private String syncMode = "FULL";
}
