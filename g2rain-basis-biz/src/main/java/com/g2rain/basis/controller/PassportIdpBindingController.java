package com.g2rain.basis.controller;

import com.g2rain.basis.api.PassportIdpBindingApi;
import com.g2rain.basis.dto.PassportIdpBindingBindDto;
import com.g2rain.basis.dto.PassportIdpBindingDto;
import com.g2rain.basis.dto.PassportIdpBindingSelectDto;
import com.g2rain.basis.service.PassportIdpBindingService;
import com.g2rain.basis.vo.PassportIdpBindingVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 账号与外部身份源绑定表控制器
 * 表名: passport_idp_binding
 *
 * @author G2rain Generator
 */
@RestController
@RequestMapping("/passport_idp_binding")
public class PassportIdpBindingController implements PassportIdpBindingApi {

    @Resource(name = "passportIdpBindingServiceImpl")
    private PassportIdpBindingService passportIdpBindingService;

    @Override
    public Result<List<PassportIdpBindingVo>> selectList(PassportIdpBindingSelectDto selectDto) {
        return Result.success(passportIdpBindingService.selectList(selectDto));
    }

    @Override
    public Result<PageData<PassportIdpBindingVo>> selectPage(PageSelectListDto<PassportIdpBindingSelectDto> selectDto) {
        return Result.successPage(passportIdpBindingService.selectPage(selectDto));
    }

    @Override
    public Result<Long> save(PassportIdpBindingDto dto) {
        return Result.success(passportIdpBindingService.save(dto));
    }

    @Override
    public Result<Long> bind(PassportIdpBindingBindDto dto) {
        return Result.success(passportIdpBindingService.bind(dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除绑定记录", description = "根据主键删除账号与外部身份源绑定记录")
    public Result<Integer> delete(@Parameter(description = "绑定记录标识") @PathVariable Long id) {
        return Result.success(passportIdpBindingService.delete(id));
    }
}
