package com.g2rain.basis.idp.sync.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * IdP 侧成员节点快照。
 */
@Setter
@Getter
@NoArgsConstructor
@Schema(description = "IdP 成员节点")
public class IdpMemberNode {

    @Schema(description = "IdP 稳定主体标识（钉钉 unionId）")
    private String unionId;

    @Schema(description = "IdP 企业内用户标识（钉钉 userid）")
    private String idpUserId;

    @Schema(description = "成员姓名")
    private String name;

    @Schema(description = "手机号")
    private String mobile;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "所属 IdP 部门标识列表")
    private List<String> deptIdpDeptIds = new ArrayList<>();
}
