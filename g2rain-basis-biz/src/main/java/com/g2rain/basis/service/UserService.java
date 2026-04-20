package com.g2rain.basis.service;

import com.g2rain.basis.dto.UserDto;
import com.g2rain.basis.dto.UserSelectDto;
import com.g2rain.basis.vo.UserOptionVo;
import com.g2rain.basis.vo.UserVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;

import java.util.List;

/**
 * 用户表服务接口
 * 表名: user
 *
 * @author Alpha
 */
public interface UserService {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return VO 对象列表
     */
    List<UserVo> selectList(UserSelectDto selectDto);

    /**
     * 根据 ID 查询用户信息
     *
     * @param id 主键 ID
     * @return 用户 VO 对象
     */
    UserVo selectById(Long id);

    /**
     * 根据 ID 查询用户信息（非隔离语义）
     *
     * @param id 主键 ID
     * @return 用户 VO 对象
     */
    UserVo selectByIdWithoutIsolation(Long id);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页 VO 数据
     */
    PageData<UserVo> selectPage(PageSelectListDto<UserSelectDto> selectDto);

    /**
     * 获取用于下拉选择的用户列表
     *
     * @return 用户简要信息集合，包含用户 ID 和名称等用于展示的字段
     * @see UserOptionVo
     */
    List<UserOptionVo> getUserOptions();

    /**
     * 根据条件查询列表
     *
     * @param roleId 角色 ID
     * @return VO 对象列表
     */
    List<UserVo> selectByRole(Long roleId);

    /**
     * 新增或更新数据
     *
     * @param dto 数据传输对象
     * @return 操作结果（影响行数）
     */
    Long save(UserDto dto);

    /**
     * 新增或更新数据（非隔离语义）
     *
     * @param dto 数据传输对象
     * @return 用户标识
     */
    Long saveWithoutIsolation(UserDto dto);

    /**
     * 检查机构用户是否存在
     *
     * @param organId 机构 ID
     * @return 用户数量
     */
    long checkUserExists(Long organId);

    /**
     * 根据 ID 删除数据
     *
     * @param id 主键 ID
     * @return 操作结果（影响行数）
     */
    int delete(Long id);
}
