package com.g2rain.basis.service;

import com.g2rain.basis.idp.resolve.dto.IdpPassportResolveRequest;
import com.g2rain.basis.idp.resolve.vo.IdpPassportResolveVo;

/**
 * IdP 登录 Passport 无隔离解析服务。
 */
public interface IdpPassportResolveService {

    /**
     * 按 IdP 主体解析 passport、绑定与各机构 user 列表；未命中时返回 {@code null}。
     */
    IdpPassportResolveVo resolve(IdpPassportResolveRequest request);
}
