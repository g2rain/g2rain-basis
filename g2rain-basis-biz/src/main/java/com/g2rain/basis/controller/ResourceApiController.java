package com.g2rain.basis.controller;

import com.g2rain.basis.api.ResourceApiApi;
import com.g2rain.basis.dto.ResourceApiDto;
import com.g2rain.basis.dto.ResourceApiSelectDto;
import com.g2rain.basis.dto.UploadResourceApiDto;
import com.g2rain.basis.service.ResourceApiService;
import com.g2rain.basis.vo.ResourceApiVo;
import com.g2rain.basis.vo.RouteDefinitionVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 资源接口表控制器
 * 表名: resource_api
 *
 * @author G2rain Generator
 */
@RestController
@RequestMapping("/resource_api")
public class ResourceApiController implements ResourceApiApi {

    @Resource(name = "resourceApiServiceImpl")
    private ResourceApiService resourceApiService;

    @Override
    public Result<List<ResourceApiVo>> selectList(ResourceApiSelectDto selectDto) {
        return Result.success(resourceApiService.selectList(selectDto));
    }

    @Override
    public Result<PageData<ResourceApiVo>> selectPage(PageSelectListDto<ResourceApiSelectDto> selectDto) {
        return Result.successPage(resourceApiService.selectPage(selectDto));
    }

    @Override
    public Result<List<RouteDefinitionVo>> selectRouteDefinitions() {
        return Result.success(resourceApiService.selectRouteDefinitions());
    }

    @PostMapping("/save")
    @Operation(summary = "新增或更新资源接口", description = "新增或更新资源接口信息")
    public Result<Long> save(@RequestBody ResourceApiDto dto) {
        return Result.success(resourceApiService.save(dto));
    }

    @PostMapping("/{serviceCode}/import")
    @Operation(summary = "批量导入资源接口", description = "批量导入资源接口信息")
    public Result<Long> batchImport(@Parameter(description = "资源接口标识") @PathVariable String serviceCode, @RequestBody UploadResourceApiDto dto) {
        return Result.success(resourceApiService.batchSave(serviceCode, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "根据主键删除资源接口记录", description = "根据主键删除资源接口记录")
    public Result<Integer> delete(@Parameter(description = "资源接口标识") @PathVariable Long id) {
        return Result.success(resourceApiService.delete(id));
    }
}
