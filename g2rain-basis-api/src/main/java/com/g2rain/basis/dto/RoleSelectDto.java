package com.g2rain.basis.dto;

import com.g2rain.common.model.BaseSelectListDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 角色表查询入参DTO
 * 用于RoleDao.selectList方法的条件筛选
 * 表名: role
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "角色查询入参 DTO")
public class RoleSelectDto extends BaseSelectListDto {

    /**
     * 机构标识
     */
    @Schema(description = "机构标识")
    private Long organId;

    /**
     * 角色名称
     */
    @Schema(description = "角色名称")
    private String roleName;

    /**
     * 角色类型[ADMIN:默认角色-只读, USER:用户角色]
     */
    @Schema(description = "角色类型[ADMIN:默认角色-只读, USER:用户角色]", allowableValues = {"ADMIN", "USER"})
    private String roleType;
}
