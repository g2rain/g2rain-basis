package com.g2rain.basis.service;

import com.g2rain.basis.dto.UpdateStatusDto;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.basis.dto.PersonalStaticAccessTokenDto;
import com.g2rain.basis.dto.PersonalStaticAccessTokenSelectDto;
import com.g2rain.basis.vo.PersonalStaticAccessTokenVo;

import java.util.List;

/**
 * 个人静态访问令牌表服务接口
 * 表名: personal_static_access_token
 *
 * @author G2rain Generator
 */
public interface PersonalStaticAccessTokenService {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return VO 对象列表
     */
    List<PersonalStaticAccessTokenVo> selectList(PersonalStaticAccessTokenSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页 VO 数据
     */
    PageData<PersonalStaticAccessTokenVo> selectPage(PageSelectListDto<PersonalStaticAccessTokenSelectDto> selectDto);

    /**
     * 新增或更新数据
     *
     * @param dto 数据传输对象
     * @return 操作结果（影响行数）
     */
    Long save(PersonalStaticAccessTokenDto dto);

    /**
     * 修改个人静态访问令牌状态
     *
     * @param id 个人静态访问令牌 ID
     * @param dto 修改状态参数
     * @return 修改的记录数量
     */
    int updateStatus(Long id, UpdateStatusDto dto);

    /**
     * 根据 ID 删除数据
     *
     * @param id 主键 ID
     * @return 操作结果（影响行数）
     */
    int delete(Long id);
}
