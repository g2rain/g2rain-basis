package com.g2rain.basis.idp.sync.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * IdP 侧部门节点快照。
 */
@Setter
@Getter
@NoArgsConstructor
@Schema(description = "IdP 部门节点")
public class IdpDepartmentNode {

    @Schema(description = "IdP 侧部门标识")
    private String idpDeptId;

    @Schema(description = "IdP 侧父部门标识，根部门父标识为空或 0")
    private String parentIdpDeptId;

    @Schema(description = "部门名称")
    private String name;

    @Schema(description = "排序")
    private Integer sortOrder;
}
