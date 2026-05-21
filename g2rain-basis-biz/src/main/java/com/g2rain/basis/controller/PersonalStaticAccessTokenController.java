package com.g2rain.basis.controller;

import com.g2rain.basis.api.PersonalStaticAccessTokenApi;
import com.g2rain.basis.dto.PersonalStaticAccessTokenDto;
import com.g2rain.basis.dto.PersonalStaticAccessTokenSelectDto;
import com.g2rain.basis.dto.UpdateStatusDto;
import com.g2rain.basis.service.PersonalStaticAccessTokenService;
import com.g2rain.basis.vo.PersonalStaticAccessTokenVo;
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
 * 个人静态访问令牌表控制器
 * 表名: personal_static_access_token
 *
 * @author G2rain Generator
 */
@RestController
@RequestMapping("/personal_static_access_token")
public class PersonalStaticAccessTokenController implements PersonalStaticAccessTokenApi {

    @Resource(name = "personalStaticAccessTokenServiceImpl")
    private PersonalStaticAccessTokenService personalStaticAccessTokenService;

    @Override
    public Result<List<PersonalStaticAccessTokenVo>> selectList(PersonalStaticAccessTokenSelectDto selectDto) {
        return Result.success(personalStaticAccessTokenService.selectList(selectDto));
    }

    @Override
    public Result<PageData<PersonalStaticAccessTokenVo>> selectPage(PageSelectListDto<PersonalStaticAccessTokenSelectDto> selectDto) {
        return Result.successPage(personalStaticAccessTokenService.selectPage(selectDto));
    }

    @PostMapping("/save")
    @Operation(summary = "新增或更新个人静态访问令牌信息", description = "新增或更新个人静态访问令牌基础信息")
    public Result<Long> save(@RequestBody PersonalStaticAccessTokenDto dto) {
        return Result.success(personalStaticAccessTokenService.save(dto));
    }

    /**
     * 修改个人静态访问令牌记录状态
     *
     * @param id  个人静态访问令牌记录 ID
     * @param dto 修改状态参数
     * @return 受影响的记录行数
     */
    @PostMapping("/{id}/status")
    @Operation(summary = "修改个人静态访问令牌记录状态", description = "修改个人静态访问令牌记录状态")
    public Result<Integer> updateStatus(@Parameter(description = "个人静态访问令牌记录状态标识") @PathVariable Long id, @RequestBody @Validated UpdateStatusDto dto) {
        return Result.success(personalStaticAccessTokenService.updateStatus(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除个人静态访问令牌记录", description = "根据主键删除个人静态访问令牌记录")
    public Result<Integer> delete(@Parameter(description = "个人静态访问令牌标识") @PathVariable Long id) {
        return Result.success(personalStaticAccessTokenService.delete(id));
    }
}
