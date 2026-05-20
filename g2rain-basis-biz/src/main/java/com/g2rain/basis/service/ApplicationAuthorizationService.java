package com.g2rain.basis.service;

import com.g2rain.basis.dto.ApplicationAuthorizationDto;
import com.g2rain.basis.dto.ApplicationAuthorizationSelectDto;
import com.g2rain.basis.dto.UpdateStatusDto;
import com.g2rain.basis.vo.ApplicationAuthorizationVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;

import java.util.List;

/**
 * 应用授权记录表服务接口
 * 表名: application_authorization
 *
 * @author Alpha
 */
public interface ApplicationAuthorizationService {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return VO 对象列表
     */
    List<ApplicationAuthorizationVo> selectList(ApplicationAuthorizationSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页 VO 数据
     */
    PageData<ApplicationAuthorizationVo> selectPage(PageSelectListDto<ApplicationAuthorizationSelectDto> selectDto);

    /**
     * 新增或更新数据
     *
     * @param dto 数据传输对象
     * @return 操作结果（影响行数）
     */
    Long save(ApplicationAuthorizationDto dto);

    /**
     * 修改应用授权记录状态
     *
     * @param id  授权记录 ID
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
