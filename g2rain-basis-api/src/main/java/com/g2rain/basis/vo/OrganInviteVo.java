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
@Schema(description = "机构邀请码 VO")
public class OrganInviteVo {

    @Schema(description = "邀请码")
    private String inviteCode;

    @Schema(description = "机构 ID")
    private Long organId;

    @Schema(description = "机构名称")
    private String organName;

    @Schema(description = "角色 ID")
    private Long roleId;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色类型")
    private String roleType;

    @Schema(description = "过期时间")
    private LocalDateTime expireAt;
}
