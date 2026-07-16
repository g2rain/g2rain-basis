package com.g2rain.basis.api;

import com.g2rain.basis.idp.resolve.dto.IdpPassportResolveRequest;
import com.g2rain.basis.idp.resolve.vo.IdpPassportResolveVo;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * IdP 登录 Passport 解析内部 API，仅供 IAM 等服务间调用。
 */
@Tag(name = "IdP Passport 解析（内部）", description = "IdP 登录链路无隔离 Passport 检索")
public interface IdpPassportInternalApi {

    /**
     * 按 IdP 主体解析 passport、绑定与各机构 user 列表。
     */
    @PostMapping("/resolve")
    @Operation(summary = "解析 IdP Passport", description = "无隔离检索 binding、passport 与跨机构 user 列表")
    Result<IdpPassportResolveVo> resolve(@RequestBody @Validated IdpPassportResolveRequest request);
}
