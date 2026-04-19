package com.g2rain.basis.service;

import com.g2rain.basis.dto.ApiEndpointDto;
import com.g2rain.basis.dto.ApiEndpointSelectDto;
import com.g2rain.basis.vo.ApiEndpointVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;

import java.util.List;

/**
 * 接口地址表服务接口
 * 表名: api_endpoint
 *
 * @author Alpha
 */
public interface ApiEndpointService {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return VO 对象列表
     */
    List<ApiEndpointVo> selectList(ApiEndpointSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页 VO 数据
     */
    PageData<ApiEndpointVo> selectPage(PageSelectListDto<ApiEndpointSelectDto> selectDto);

    /**
     * 根据接口地址的标签
     *
     * @return 标签集合
     */
    List<String> selectApiTags();

    /**
     * 新增或更新数据
     *
     * @param dto 数据传输对象
     * @return 操作结果（影响行数）
     */
    Long save(ApiEndpointDto dto);

    /**
     * 根据 ID 删除数据
     *
     * @param id 主键 ID
     * @return 操作结果（影响行数）
     */
    int delete(Long id);
}
