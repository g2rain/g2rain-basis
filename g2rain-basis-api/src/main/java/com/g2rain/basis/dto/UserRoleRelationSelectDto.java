package com.g2rain.basis.dto;

import com.g2rain.common.model.BaseSelectListDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;


/**
 * 用户角色关联表查询入参DTO
 * 用于UserRoleRelationDao.selectList方法的条件筛选
 * 表名: user_role_relation
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户角色关联查询入参 DTO")
public class UserRoleRelationSelectDto extends BaseSelectListDto {

    /**
     * 用户标识
     */
    @Schema(description = "用户标识")
    private Long userId;

    /**
     * 用户 ID 集合
     */
    @Schema(description = "用户标识集合")
    private Set<Long> userIds;

    /**
     * 角色标识
     */
    @Schema(description = "角色标识")
    private Long roleId;
}
