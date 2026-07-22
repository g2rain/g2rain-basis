package com.g2rain.basis.api;

import com.g2rain.basis.dto.TenantIdpSyncDto;
import com.g2rain.basis.vo.TenantIdpSyncResultVo;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 租户 IdP 成员/部门同步 API。
 */
@Tag(name = "租户 IdP 同步", description = "将三方应用绑定租户的成员与部门同步到平台")
public interface TenantIdpSyncApi {

    /**
     * 同步三方应用绑定租户的成员与部门。
     */
    @PostMapping("/sync")
    @Operation(summary = "同步租户成员与部门",
        description = "从 IdP 拉取通讯录，创建/更新平台成员、部门及部门关系；首期支持钉钉 INTERNAL")
    Result<TenantIdpSyncResultVo> sync(@RequestBody @Validated TenantIdpSyncDto dto);
}
