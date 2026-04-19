package com.g2rain.basis.service;

import com.g2rain.basis.dto.RoleControlUnitRelationDto;
import com.g2rain.basis.dto.RoleControlUnitRelationSelectDto;
import com.g2rain.basis.model.RoleControlUnitRelation;
import com.g2rain.basis.vo.RoleControlUnitRelationVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;

import java.util.List;

/**
 * 角色控制单元关联表服务接口
 * 表名: role_control_unit_relation
 *
 * @author Alpha
 */
public interface RoleControlUnitRelationService {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return VO 对象列表
     */
    List<RoleControlUnitRelationVo> selectList(RoleControlUnitRelationSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页 VO 数据
     */
    PageData<RoleControlUnitRelationVo> selectPage(PageSelectListDto<RoleControlUnitRelationSelectDto> selectDto);

    /**
     * 根据条件查询列表
     *
     * @param roleId 角色 ID
     * @return VO 对象列表
     */
    List<RoleControlUnitRelationVo> selectByRole(Long roleId);

    /**
     * 新增或更新数据
     *
     * @param dto 数据传输对象
     * @return 操作结果（影响行数）
     */
    Integer save(RoleControlUnitRelationDto dto);

    /**
     * 内部新增或更新数据
     *
     * @param dto 数据传输对象
     * @return 操作结果（影响行数）
     */
    Integer internalSave(RoleControlUnitRelation dto);

    /**
     * 修改角色控制单元关联表记录状态
     *
     * @param organId                    机构标识
     * @param applicationAuthorizationId 授权记录标识
     * @param status                     控制单元状态
     * @return 修改的记录数量
     */
    int changeStatus(Long organId, Long applicationAuthorizationId, String status);
}
