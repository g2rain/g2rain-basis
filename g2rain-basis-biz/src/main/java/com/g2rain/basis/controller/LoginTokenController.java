package com.g2rain.basis.controller;

import com.g2rain.basis.api.LoginTokenApi;
import com.g2rain.basis.dto.LoginTokenDto;
import com.g2rain.basis.dto.LoginTokenSelectDto;
import com.g2rain.basis.service.LoginTokenService;
import com.g2rain.basis.vo.LoginTokenVo;
import com.g2rain.basis.vo.StaticAccessTokenResolveVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import com.g2rain.common.web.TokenJWTPayload;
import jakarta.annotation.Resource;
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

    @Override
    public Result<Long> save(String applicationCode, LoginTokenDto dto) {
        return Result.success(loginTokenService.save(applicationCode, dto));
    }

    @Override
    public Result<TokenJWTPayload> fetchTokenContext(Long passportId, Long userId, String applicationCode,
                                                     Boolean thirdPartyIdpLogin, String idpType, String idpSubject,
                                                     String idpApplicationCode) {
        return Result.success(loginTokenService.fetchTokenContext(passportId, userId, applicationCode,
            thirdPartyIdpLogin, idpType, idpSubject, idpApplicationCode));
    }

    @Override
    public Result<TokenJWTPayload> fetchAnonymousTokenContext(Long organId, String applicationCode,
                                                              List<Long> roleIds) {
        return Result.success(loginTokenService.fetchAnonymousTokenContext(organId, applicationCode, roleIds));
    }

    @Override
    public Result<StaticAccessTokenResolveVo> fetchStaticTokenContext(String apiKey) {
        return Result.success(loginTokenService.fetchStaticTokenContext(apiKey));
    }
}
