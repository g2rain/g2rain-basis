package com.g2rain.basis.api;

import com.g2rain.basis.dto.IdpEmployeeEnsureRequest;
import com.g2rain.basis.vo.IdpEmployeeEnsureVo;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * IdP 员工登录 JIT：确保 organ 下存在非 ADMIN 的 User。
 */
@Tag(name = "IdP 员工确保", description = "服务间幂等确保机构员工 User")
public interface IdpEmployeeApi {

    @PostMapping("/internal/idp/employee/ensure")
    @Operation(summary = "幂等确保机构员工 User", description = "仅供受信服务间调用；不授予 ADMIN 角色", hidden = true)
    Result<IdpEmployeeEnsureVo> ensure(
        @RequestBody @Validated IdpEmployeeEnsureRequest request);
}
