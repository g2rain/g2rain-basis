package com.g2rain.basis.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 机构邀请码生成结果 VO
 */
@Setter
@Getter
@NoArgsConstructor
@Schema(name = "OrganInviteVo", description = "机构邀请码生成结果")
public class OrganInviteVo {

    @Schema(
        description = "邀请码（32 位十六进制字符串）",
        example = "a1b2c3d4e5f6789012345678abcdef01"
    )
    private String inviteCode;

    @Schema(description = "机构 ID", example = "10001")
    private Long organId;

    @Schema(description = "机构名称", example = "示例科技有限公司")
    private String organName;

    @Schema(description = "加入后分配的角色 ID", example = "20001")
    private Long roleId;

    @Schema(description = "角色名称", example = "普通成员")
    private String roleName;

    @Schema(
        description = "角色类型（与 RoleType 枚举名一致）",
        allowableValues = {"ADMIN", "USER"},
        example = "USER"
    )
    private String roleType;

    @Schema(description = "过期时间", example = "2026-06-04T12:00:00")
    private LocalDateTime expireAt;
}
