package com.g2rain.basis.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 角色类型枚举。
 * <p>
 * 用于区分系统中的角色类型，不同类型的角色可能对应不同的权限范围或使用场景。
 * </p>
 *
 * <ul>
 *   <li>{@link #ADMIN}：默认角色，仅分配给机构和管理员，通常为只读角色。</li>
 *   <li>{@link #USER}：用户角色，分配给普通用户，权限和功能根据系统策略控制。</li>
 * </ul>
 *
 * @author alpha
 * @since 2026/1/15
 */
@Schema(description = "角色类型枚举")
public enum RoleType {

    /**
     * 默认角色-只读
     * 只分配给机构和管理员
     */
    @Schema(description = "默认角色(只读), 只分配给机构和管理员")
    ADMIN,

    /**
     * 用户角色
     */
    @Schema(description = "用户角色")
    USER
}
