package com.g2rain.basis.controller;

import com.g2rain.basis.api.LoginTokenApi;
import com.g2rain.basis.dto.LoginTokenDto;
import com.g2rain.basis.dto.LoginTokenSelectDto;
import com.g2rain.basis.service.LoginTokenService;
import com.g2rain.basis.vo.LoginTokenVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import com.g2rain.common.web.TokenJWTPayload;
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
 * 登录信息表, 记录了当前登录状态的相关信息控制器
 * 表名: login_token
 *
 * @author Alpha
 */
@RestController
@RequestMapping("/login_token")
public class LoginTokenController implements LoginTokenApi {

    @Resource(name = "loginTokenServiceImpl")
    private LoginTokenService loginTokenService;

    @Override
    public Result<List<LoginTokenVo>> selectList(LoginTokenSelectDto selectDto) {
        return Result.success(loginTokenService.selectList(selectDto));
    }

    @Override
    public Result<PageData<LoginTokenVo>> selectPage(PageSelectListDto<LoginTokenSelectDto> selectDto) {
        return Result.successPage(loginTokenService.selectPage(selectDto));
    }

    @PostMapping("/save")
    @Operation(summary = "新增或更新登录令牌", description = "新增或更新登录状态令牌信息")
    public Result<Long> save(@RequestBody LoginTokenDto dto) {
        return Result.success(loginTokenService.save(dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除登录令牌记录", description = "根据主键删除登录令牌记录")
    public Result<Integer> delete(@Parameter(description = "登录令牌标识") @PathVariable Long id) {
        return Result.success(loginTokenService.delete(id));
    }

    @Override
    public Result<TokenJWTPayload> fetchTokenContext(Long userId, String applicationCode) {
        return Result.success(loginTokenService.fetchTokenContext(userId, applicationCode));
    }
}
