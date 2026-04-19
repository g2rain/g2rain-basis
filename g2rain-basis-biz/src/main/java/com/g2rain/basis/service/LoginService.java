package com.g2rain.basis.service;


import com.g2rain.basis.dto.LoginDto;
import com.g2rain.basis.vo.PassportVo;

/**
 * 登录服务接口，提供用户登录相关功能。
 *
 * <p>该接口定义了用户登录操作，完成用户身份校验、认证以及生成用户通行凭证。</p>
 *
 * <p>实现类需提供具体的身份认证逻辑，并返回用户的登录凭证。</p>
 *
 * @author alpha
 * @since 2026/1/15
 */
public interface LoginService {

    /**
     * 用户登录接口。
     *
     * <p>接收用户登录信息，完成身份校验与认证，
     * 登录成功后返回用户通行凭证信息。</p>
     *
     * @param dto 登录请求参数，包含账号凭证等信息，需通过参数校验
     * @return 登录结果，成功时返回 {@link PassportVo}
     */
    PassportVo passportLogin(LoginDto dto);
}
