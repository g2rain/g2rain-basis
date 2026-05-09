package com.g2rain.basis.controller;

import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import com.g2rain.basis.api.ServiceRegistryApi;
import com.g2rain.basis.dto.ServiceRegistryDto;
import com.g2rain.basis.dto.ServiceRegistrySelectDto;
import com.g2rain.basis.service.ServiceRegistryService;
import com.g2rain.basis.vo.ServiceRegistryVo;
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
 * 服务注册表控制器
 * 表名: service_registry
 *
 * @author G2rain Generator
 */
@RestController
@RequestMapping("/service_registry")
public class ServiceRegistryController implements ServiceRegistryApi {

    @Resource(name = "serviceRegistryServiceImpl")
    private ServiceRegistryService serviceRegistryService;

    @Override
    public Result<List<ServiceRegistryVo>> selectList(ServiceRegistrySelectDto selectDto) {
        return Result.success(serviceRegistryService.selectList(selectDto));
    }

    @Override
    public Result<PageData<ServiceRegistryVo>> selectPage(PageSelectListDto<ServiceRegistrySelectDto> selectDto) {
        return Result.successPage(serviceRegistryService.selectPage(selectDto));
    }

    @PostMapping("/save")
    @Operation(summary = "新增或更新服务注册", description = "新增或更新服务注册信息")
    public Result<Long> save(@RequestBody ServiceRegistryDto dto) {
        return Result.success(serviceRegistryService.save(dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除服务注册", description = "根据主键删除服务注册记录")
    public Result<Integer> delete(@Parameter(description = "服务注册标识") @PathVariable Long id) {
        return Result.success(serviceRegistryService.delete(id));
    }
}
