package com.g2rain.basis.controller;

import com.g2rain.basis.api.ApiEndpointApi;
import com.g2rain.basis.dto.ApiEndpointDto;
import com.g2rain.basis.dto.ApiEndpointSelectDto;
import com.g2rain.basis.service.ApiEndpointService;
import com.g2rain.basis.vo.ApiEndpointVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 接口地址表控制器
 * 表名: api_endpoint
 *
 * @author Alpha
 */
@RestController
@RequestMapping("/api_endpoint")
public class ApiEndpointController implements ApiEndpointApi {

    @Resource(name = "apiEndpointServiceImpl")
    private ApiEndpointService apiEndpointService;

    /**
     * 查询接口地址列表（不分页）
     *
     * @param selectDto 查询条件 DTO
     * @return 接口地址视图对象列表
     */
    @Override
    public Result<List<ApiEndpointVo>> selectList(ApiEndpointSelectDto selectDto) {
        return Result.success(apiEndpointService.selectList(selectDto));
    }

    /**
     * 分页查询接口地址列表
     *
     * @param selectDto 分页查询参数及查询条件
     * @return 分页后的接口地址视图对象数据
     */
    @Override
    public Result<PageData<ApiEndpointVo>> selectPage(PageSelectListDto<ApiEndpointSelectDto> selectDto) {
        return Result.successPage(apiEndpointService.selectPage(selectDto));
    }

    /**
     * 查询所有接口标签列表
     *
     * @return 接口标签字符串集合
     */
    @GetMapping("/api_tags")
    @Operation(summary = "查询接口标签列表", description = "查询系统中已维护的接口标签字符串集合")
    public Result<List<String>> getApiTags() {
        return Result.success(apiEndpointService.selectApiTags());
    }

    /**
     * 新增或更新接口地址信息
     *
     * @param dto 接口地址数据传输对象（已校验）
     * @return 保存成功后的主键 ID
     */
    @PostMapping("/save")
    @Operation(summary = "新增或更新接口地址", description = "新增或更新接口地址信息")
    public Result<Long> save(@RequestBody @Validated ApiEndpointDto dto) {
        return Result.success(apiEndpointService.save(dto));
    }

    /**
     * 根据主键删除接口地址记录
     *
     * @param id 接口地址主键 ID
     * @return 受影响的记录行数
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除接口地址", description = "根据主键删除接口地址记录")
    public Result<Integer> delete(@Parameter(description = "接口地址标识") @PathVariable Long id) {
        return Result.success(apiEndpointService.delete(id));
    }
}
