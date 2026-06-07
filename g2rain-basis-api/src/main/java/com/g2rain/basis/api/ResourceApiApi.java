package com.g2rain.basis.api;

import com.g2rain.basis.dto.ResourceApiSelectDto;
import com.g2rain.basis.vo.ResourceApiVo;
import com.g2rain.basis.vo.RouteDefinitionVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


/**
 * 资源接口表API接口
 * 表名: resource_api
 *
 * @author G2rain Generator
 */
@Tag(name = "资源接口", description = "资源接口接口")
public interface ResourceApiApi {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return 数据列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询资源接口列表", description = "根据查询条件返回资源接口列表")
    Result<List<ResourceApiVo>> selectList(ResourceApiSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询资源接口列表", description = "分页查询资源接口列表")
    Result<PageData<ResourceApiVo>> selectPage(PageSelectListDto<ResourceApiSelectDto> selectDto);

    /**
     * 查询动态路由专用的资源接口定义列表
     *
     * @return 动态路由定义列表
     */
    @GetMapping("/route_definitions")
    @Operation(summary = "查询网关路由定义", hidden = true, description = "返回网关动态路由装配所需的资源接口与服务注册聚合视图")
    Result<List<RouteDefinitionVo>> selectRouteDefinitions();
}
