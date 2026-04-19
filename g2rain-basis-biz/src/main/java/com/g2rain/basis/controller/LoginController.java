package com.g2rain.basis.controller;


import com.g2rain.basis.api.LoginApi;
import com.g2rain.basis.dto.LoginDto;
import com.g2rain.basis.service.LoginService;
import com.g2rain.basis.vo.PassportVo;
import com.g2rain.common.model.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录控制器。
 *
 * <p>实现 {@link LoginApi}，负责将 HTTP 请求转发至登录领域服务，
 * 本类不再重复定义接口协议，仅承载 Web 层适配职责。</p>
 *
 * @author alpha
 * @since 2026/1/15
 */
@RestController
@RequestMapping("/internal_auth")
public class LoginController implements LoginApi {

    @Resource(name = "loginServiceImpl")
    private LoginService loginService;

    /**
     * 用户登录接口。
     *
     * <p>接收用户登录信息，完成身份校验与认证，
     * 登录成功后返回用户通行凭证信息。</p>
     *
     * @param dto 登录请求参数，包含账号凭证等信息，需通过参数校验
     * @return 登录结果，成功时返回 {@link PassportVo}
     */
    @Override
    public Result<PassportVo> login(LoginDto dto) {
        return Result.success(loginService.passportLogin(dto));
    }
}
