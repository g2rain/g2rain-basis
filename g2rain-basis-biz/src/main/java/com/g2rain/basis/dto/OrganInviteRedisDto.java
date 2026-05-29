package com.g2rain.basis.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 机构邀请码 Redis 载荷
 */
@Setter
@Getter
@NoArgsConstructor
public class OrganInviteRedisDto {

    private Long organId;

    private String organName;

    private Long roleId;

    private String roleName;

    private String roleType;

    private Long createdBy;

    private Integer maxUses;

    private Integer usedCount;
}
