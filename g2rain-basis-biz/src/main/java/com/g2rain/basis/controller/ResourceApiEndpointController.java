package com.g2rain.basis.controller;

import com.g2rain.basis.api.ResourceApiEndpointApi;
import com.g2rain.basis.dto.ResourceApiEndpointDto;
import com.g2rain.basis.dto.ResourceApiSelectDto;
import com.g2rain.basis.service.ResourceApiEndpointService;
import com.g2rain.basis.vo.ResourceApiVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 应用资源接口地址表控制器
 * 表名: resource_api_endpoint
 *
 * @author Alpha
 */
@RestController
@RequestMapping("/resource_api_endpoint")
public class ResourceApiEndpointController implements ResourceApiEndpointApi {

    @Resource(name = "resourceApiEndpointServiceImpl")
    private ResourceApiEndpointService resourceApiEndpointService;

    /**
     * 查询应用资源接口列表（不分页）
     *
     * @param selectDto 查询条件 DTO
     * @return 应用资源接口视图对象列表
     */
    @Override
    public Result<List<ResourceApiVo>> selectList(ResourceApiSelectDto selectDto) {
        return Result.success(resourceApiEndpointService.selectList(selectDto));
    }

    /**
     * 分页查询应用资源接口列表
     *
     * @param selectDto 分页参数及查询条件
     * @return 分页后的应用资源接口视图对象数据
     */
    @Override
    public Result<PageData<ResourceApiVo>> selectPage(PageSelectListDto<ResourceApiSelectDto> selectDto) {
        return Result.successPage(resourceApiEndpointService.selectPage(selectDto));
    }

    /**
     * 新增或更新应用资源接口记录
     *
     * @param dto 应用资源接口数据传输对象（已校验）
     * @return 保存成功后的主键 ID
     */
    @PostMapping("/save")
    @Operation(summary = "新增或更新资源接口关联", description = "新增或更新应用资源与接口地址关联记录")
    public Result<Long> save(@RequestBody @Validated ResourceApiEndpointDto dto) {
        return Result.success(resourceApiEndpointService.save(dto));
    }

    /**
     * 根据主键删除应用资源接口记录
     *
     * @param id 应用资源接口主键 ID
     * @return 受影响的记录行数
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除资源接口关联记录", description = "根据主键删除应用资源接口关联记录")
    public Result<Integer> delete(@Parameter(description = "资源接口关联标识") @PathVariable Long id) {
        return Result.success(resourceApiEndpointService.delete(id));
    }
}
