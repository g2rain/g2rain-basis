package com.g2rain.basis.controller;

import com.g2rain.basis.api.ApplicationIdpProvisionApi;
import com.g2rain.basis.dto.ApplicationIdpProvisionDto;
import com.g2rain.basis.dto.ApplicationIdpProvisionSelectDto;
import com.g2rain.basis.service.ApplicationIdpProvisionService;
import com.g2rain.basis.vo.ApplicationIdpProvisionVo;
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
 * 外部身份源应用与平台应用的绑定控制器
 * 表名: application_idp_provision
 *
 * @author G2rain Generator
 */
@RestController
@RequestMapping("/application_idp_provision")
public class ApplicationIdpProvisionController implements ApplicationIdpProvisionApi {

    @Resource(name = "applicationIdpProvisionServiceImpl")
    private ApplicationIdpProvisionService applicationIdpProvisionService;

    @Override
    public Result<List<ApplicationIdpProvisionVo>> selectList(ApplicationIdpProvisionSelectDto selectDto) {
        return Result.success(applicationIdpProvisionService.selectList(selectDto));
    }

    @Override
    public Result<PageData<ApplicationIdpProvisionVo>> selectPage(PageSelectListDto<ApplicationIdpProvisionSelectDto> selectDto) {
        return Result.successPage(applicationIdpProvisionService.selectPage(selectDto));
    }

    @PostMapping("/save")
    @Operation(summary = "新增或更绑定", description = "新增或更新账号与外部身份源绑定信息")
    public Result<Long> save(@RequestBody ApplicationIdpProvisionDto dto) {
        return Result.success(applicationIdpProvisionService.save(dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除绑定记录", description = "根据主键删除账号与外部身份源绑定记录")
    public Result<Integer> delete(@Parameter(description = "绑定记录标识") @PathVariable Long id) {
        return Result.success(applicationIdpProvisionService.delete(id));
    }
}
