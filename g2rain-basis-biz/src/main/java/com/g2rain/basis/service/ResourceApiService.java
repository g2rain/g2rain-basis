package com.g2rain.basis.service;

import com.g2rain.basis.dto.ResourceApiDto;
import com.g2rain.basis.dto.ResourceApiSelectDto;
import com.g2rain.basis.dto.UploadResourceApiDto;
import com.g2rain.basis.vo.ResourceApiVo;
import com.g2rain.basis.vo.RouteDefinitionVo;
import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;

import java.util.List;

/**
 * 资源接口表服务接口
 * 表名: resource_api
 *
 * @author G2rain Generator
 */
public interface ResourceApiService {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return VO 对象列表
     */
    List<ResourceApiVo> selectList(ResourceApiSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页 VO 数据
     */
    PageData<ResourceApiVo> selectPage(PageSelectListDto<ResourceApiSelectDto> selectDto);

    /**
     * 查询供网关动态路由使用的路由定义列表
     *
     * @return 动态路由定义列表
     */
    List<RouteDefinitionVo> selectRouteDefinitions();

    /**
     * 新增或更新数据
     *
     * @param dto 数据传输对象
     * @return 操作结果（影响行数）
     */
    Long save(ResourceApiDto dto);

    /**
     * 批量新增或更新资源接口数据
     *
     * @param serviceCode 服务编码
     * @param resourceApi 资源接口数据列表
     * @return 成功操作的记录数
     * @throws BusinessException 当接口编码重复或字段缺失时抛出
     */
    Long batchSave(String serviceCode, UploadResourceApiDto resourceApi);

    /**
     * 根据 ID 删除数据
     *
     * @param id 主键 ID
     * @return 操作结果（影响行数）
     */
    int delete(Long id);
}
