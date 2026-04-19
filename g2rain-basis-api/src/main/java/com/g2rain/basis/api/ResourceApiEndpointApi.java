package com.g2rain.basis.api;

import com.g2rain.basis.dto.ResourceApiSelectDto;
import com.g2rain.basis.vo.ResourceApiVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


/**
 * 应用资源接口地址表API接口
 * 表名: resource_api_endpoint
 *
 * @author Alpha
 */
@Tag(name = "应用资源接口", description = "应用资源接口地址相关接口")
public interface ResourceApiEndpointApi {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return 数据列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询应用资源接口列表", description = "根据查询条件返回应用资源接口列表")
    Result<List<ResourceApiVo>> selectList(ResourceApiSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询应用资源接口列表", description = "分页查询应用资源接口列表")
    Result<PageData<ResourceApiVo>> selectPage(PageSelectListDto<ResourceApiSelectDto> selectDto);
}
