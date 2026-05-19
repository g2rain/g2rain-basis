package com.g2rain.basis.controller;

import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import com.g2rain.basis.api.ApplicationIdpProvisionApi;
import com.g2rain.basis.dto.ApplicationIdpProvisionDto;
import com.g2rain.basis.dto.ApplicationIdpProvisionSelectDto;
import com.g2rain.basis.service.ApplicationIdpProvisionService;
import com.g2rain.basis.vo.ApplicationIdpProvisionVo;
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
    public Result<Long> save(@RequestBody ApplicationIdpProvisionDto dto) {
        return Result.success(applicationIdpProvisionService.save(dto));
    }

    @DeleteMapping("/{id}")
    public Result<Integer> delete(@PathVariable Long id) {
        return Result.success(applicationIdpProvisionService.delete(id));
    }
}