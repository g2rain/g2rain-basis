package com.g2rain.basis.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

/**
 * @author alpha
 * @since 2026/1/24
 */
@Setter
@Getter
@NoArgsConstructor
@Schema(description = "角色赋予用户 DTO")
public class RoleAssignUsersDto {
    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "角色标识")
    private Long roleId;

    @Schema(description = "添加用户标识集合")
    private Set<Long> userIds;

    @Schema(description = "删除用户标记集合")
    private Set<Long> deleteUserIds;
}
