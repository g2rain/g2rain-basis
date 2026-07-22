package com.g2rain.basis.api;

import com.g2rain.basis.dto.PassportIdpBindingBindDto;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 账号与外部身份源绑定内部 API，仅供 IAM 等服务间调用。
 */
@Tag(name = "账号与外部身份源绑定（内部）", description = "IAM 扫码绑定等服务间调用")
public interface PassportIdpBindingInternalApi {

    /**
     * IAM 扫码绑定落库（幂等）；身份字段来自 IAM 已校验 state，不经公开网关暴露。
     */
    @PostMapping("/bind")
    @Operation(summary = "内部绑定外部身份源", description = "IAM 钉钉扫码绑定回调落库，仅供服务间调用")
    Result<Long> bind(@RequestBody @Validated PassportIdpBindingBindDto dto);
}
