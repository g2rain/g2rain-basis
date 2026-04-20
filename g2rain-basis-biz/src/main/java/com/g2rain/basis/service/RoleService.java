package com.g2rain.basis.service;

import com.g2rain.basis.enums.RoleType;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.basis.dto.RoleDto;
import com.g2rain.basis.dto.RoleSelectDto;
import com.g2rain.basis.vo.RoleVo;

import java.util.List;

/**
 * 角色表服务接口
 * 表名: role
 *
 * @author Alpha
 */
public interface RoleService {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return VO 对象列表
     */
    List<RoleVo> selectList(RoleSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页 VO 数据
     */
    PageData<RoleVo> selectPage(PageSelectListDto<RoleSelectDto> selectDto);

    /**
     * 新增或更新数据
     *
     * @param dto 数据传输对象
     * @return 操作结果（影响行数）
     */
    Long save(RoleDto dto);

    /**
     * 新增或更新数据（隔离语义）
     *
     * @param roleType 角色类型
     * @param dto 数据传输对象
     * @return 角色标识
     */
    Long saveWithIsolation(RoleType roleType, RoleDto dto);

    /**
     * 新增或更新数据（非隔离语义）
     *
     * @param roleType 角色类型
     * @param dto 数据传输对象
     * @return 角色标识
     */
    Long saveWithoutIsolation(RoleType roleType, RoleDto dto);

    /**
     * 列表查询（非隔离语义）。
     *
     * @param selectDto 查询条件
     * @return 角色列表
     */
    List<RoleVo> selectListWithoutIsolation(RoleSelectDto selectDto);

    /**
     * 根据 ID 删除数据
     *
     * @param id 主键 ID
     * @return 操作结果（影响行数）
     */
    int delete(Long id);
}
