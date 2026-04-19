package com.g2rain.basis.service;

import com.g2rain.basis.dto.ResourcePageElementDto;
import com.g2rain.basis.dto.ResourcePageElementSelectDto;
import com.g2rain.basis.dto.UploadResourcePageElementDto;
import com.g2rain.basis.vo.ResourcePageElementVo;
import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;

import java.util.List;

/**
 * 应用资源页面元素表服务接口
 * 表名: resource_page_element
 *
 * @author Alpha
 */
public interface ResourcePageElementService {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return VO 对象列表
     */
    List<ResourcePageElementVo> selectList(ResourcePageElementSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页 VO 数据
     */
    PageData<ResourcePageElementVo> selectPage(PageSelectListDto<ResourcePageElementSelectDto> selectDto);

    /**
     * 新增或更新数据
     *
     * @param dto 数据传输对象
     * @return 操作结果（影响行数）
     */
    Long save(ResourcePageElementDto dto);

    /**
     * 批量新增或更新页面元素数据
     *
     * <p>逻辑说明：
     * <ul>
     *     <li>遍历元素列表，判断是新增还是更新</li>
     *     <li>同一页面下元素编码唯一，重复时抛异常</li>
     *     <li>新增时使用 IdGenerator 生成主键</li>
     *     <li>更新时修改已有记录的属性值</li>
     * </ul>
     *
     * @param applicationId 应用 ID
     * @param pageElements  页面元素数据列表
     * @return 成功操作的记录数
     * @throws BusinessException 当元素编码重复或字段缺失时抛出
     */
    Long batchSave(Long applicationId, List<UploadResourcePageElementDto> pageElements);

    /**
     * 根据 ID 删除数据
     *
     * @param id 主键 ID
     * @return 操作结果（影响行数）
     */
    int delete(Long id);
}
