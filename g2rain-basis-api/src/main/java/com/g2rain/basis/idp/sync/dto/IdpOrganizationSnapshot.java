package com.g2rain.basis.idp.sync.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * IdP 企业通讯录快照，供租户同步编排使用。
 */
@Setter
@Getter
@NoArgsConstructor
@Schema(description = "IdP 企业通讯录快照")
public class IdpOrganizationSnapshot {

    @Schema(description = "部门列表")
    private List<IdpDepartmentNode> departments = new ArrayList<>();

    @Schema(description = "成员列表")
    private List<IdpMemberNode> members = new ArrayList<>();

    @Schema(description = "IAM 解析后的 IdP 侧应用标识（如钉钉 OAuth clientId）")
    private String idpApplicationCode;
}
