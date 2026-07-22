package com.g2rain.basis.controller;

import com.g2rain.basis.api.IdpPassportInternalApi;
import com.g2rain.basis.idp.resolve.dto.IdpPassportResolveRequest;
import com.g2rain.basis.idp.resolve.vo.IdpPassportResolveVo;
import com.g2rain.basis.service.IdpPassportResolveService;
import com.g2rain.common.model.Result;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * IdP 登录 Passport 解析内部控制器。
 */
@RestController
@RequestMapping("/internal/idp_passport")
public class IdpPassportInternalController implements IdpPassportInternalApi {

    @Resource(name = "idpPassportResolveServiceImpl")
    private IdpPassportResolveService idpPassportResolveService;

    @Override
    public Result<IdpPassportResolveVo> resolve(@RequestBody @Validated IdpPassportResolveRequest request) {
        return Result.success(idpPassportResolveService.resolve(request));
    }
}
