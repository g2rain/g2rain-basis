package com.g2rain.basis.controller;

import com.g2rain.basis.api.PassportIdpBindingInternalApi;
import com.g2rain.basis.dto.PassportIdpBindingBindDto;
import com.g2rain.basis.service.PassportIdpBindingService;
import com.g2rain.common.model.Result;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 账号与外部身份源绑定内部控制器。
 */
@RestController
@RequestMapping("/internal/passport_idp_binding")
public class PassportIdpBindingInternalController implements PassportIdpBindingInternalApi {

    @Resource(name = "passportIdpBindingServiceImpl")
    private PassportIdpBindingService passportIdpBindingService;

    @Override
    public Result<Long> bind(@RequestBody @Validated PassportIdpBindingBindDto dto) {
        return Result.success(passportIdpBindingService.bindInternal(dto));
    }
}
