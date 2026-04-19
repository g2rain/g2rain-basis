package com.g2rain.basis.api;


import com.g2rain.basis.dto.LoginDto;
import com.g2rain.basis.vo.PassportVo;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 登录相关接口定义。
 *
 * <p>该接口用于声明认证模块对外暴露的 HTTP 协议，
 * 作为 Api Jar 提供给其他服务（如 Controller、Feign Client）统一复用。</p>
 *
 * @author alpha
 * @since 2026/1/16
 */
@Hidden
@Tag(name = "登录", description = "登录认证相关接口")
public interface LoginApi {

    /**
     * 用户登录接口。
     *
     * <p>接收用户登录参数，完成身份校验与认证，
     * 登录成功后返回用户通行凭证信息。</p>
     *
     * @param dto 登录请求参数，包含账号凭证等信息，需通过参数校验
     * @return 登录结果，成功时返回 {@link PassportVo}
     */
    @PostMapping("/internal_login")
    @Operation(summary = "用户登录", hidden = true, description = "接收登录参数完成认证, 登录成功后返回用户通行凭证信息")
    Result<PassportVo> login(@RequestBody @Validated LoginDto dto);
}
