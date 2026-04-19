package com.g2rain.basis.service;

import com.g2rain.basis.dto.RoleAssignUsersDto;
import com.g2rain.basis.dto.UserRoleRelationDto;
import com.g2rain.basis.dto.UserRoleRelationSelectDto;
import com.g2rain.basis.vo.UserRoleRelationVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;

import java.util.List;

/**
 * 用户角色关联表服务接口
 * 表名: user_role_relation
 *
 * @author Alpha
 */
public interface UserRoleRelationService {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return VO 对象列表
     */
    List<UserRoleRelationVo> selectList(UserRoleRelationSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页 VO 数据
     */
    PageData<UserRoleRelationVo> selectPage(PageSelectListDto<UserRoleRelationSelectDto> selectDto);

    /**
     * 新增或更新数据
     *
     * @param dto 数据传输对象
     * @return 操作结果（影响行数）
     */
    Long save(UserRoleRelationDto dto);

    /**
     * 角色分配用户
     *
     * @param dto 数据传输对象
     * @return 操作结果（影响行数）
     */
    Integer assignUsers(RoleAssignUsersDto dto);

    /**
     * 根据用户 ID 删除数据
     *
     * @param userId 用户 ID
     * @return 操作结果（影响行数）
     */
    int deleteByUserId(Long userId);
}
