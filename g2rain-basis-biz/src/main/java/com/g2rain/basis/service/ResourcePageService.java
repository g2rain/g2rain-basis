package com.g2rain.basis.service;

import com.g2rain.basis.dto.UploadResourcePageDto;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.basis.dto.ResourcePageDto;
import com.g2rain.basis.dto.ResourcePageSelectDto;
import com.g2rain.basis.vo.ResourcePageVo;

import java.util.List;

/**
 * 应用资源页面表服务接口
 * 表名: resource_page
 *
 * @author Alpha
 */
public interface ResourcePageService {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return VO 对象列表
     */
    List<ResourcePageVo> selectList(ResourcePageSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页 VO 数据
     */
    PageData<ResourcePageVo> selectPage(PageSelectListDto<ResourcePageSelectDto> selectDto);

    /**
     * 新增或更新数据
     *
     * @param dto 数据传输对象
     * @return 操作结果（影响行数）
     */
    Long save(ResourcePageDto dto);

    /**
     * 批量新增或更新页面资源
     *
     * @param applicationId 应用 ID
     * @param pages 页面数据列表
     * @return 成功操作的记录数
     */
    Long batchSave(Long applicationId, List<UploadResourcePageDto> pages);

    /**
     * 根据 ID 删除数据
     *
     * @param id 主键 ID
     * @return 操作结果（影响行数）
     */
    int delete(Long id);
}
