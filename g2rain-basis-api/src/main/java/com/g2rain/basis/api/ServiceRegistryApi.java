package com.g2rain.basis.api;

import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import com.g2rain.basis.dto.ServiceRegistrySelectDto;
import com.g2rain.basis.vo.ServiceRegistryVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


/**
 * 服务注册表API接口
 * 表名: service_registry
 *
 * @author G2rain Generator
 */
@Tag(name = "服务注册", description = "服务注册接口")
public interface ServiceRegistryApi {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return 数据列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询服务注册列表", description = "根据查询条件返回服务注册列表")
    Result<List<ServiceRegistryVo>> selectList(ServiceRegistrySelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询服务注册列表", description = "分页查询服务注册列表")
    Result<PageData<ServiceRegistryVo>> selectPage(PageSelectListDto<ServiceRegistrySelectDto> selectDto);
}
