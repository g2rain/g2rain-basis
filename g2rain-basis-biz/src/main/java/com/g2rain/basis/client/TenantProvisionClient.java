package com.g2rain.basis.client;

import com.g2rain.basis.client.dto.VerifyCreateOrganRequest;
import com.g2rain.common.model.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * IAM 租户开通权限校验 Feign 客户端。
 */
@FeignClient(name = "g2rain-iam", contextId = "tenantProvisionClient", path = "/internal/tenant_provision")
public interface TenantProvisionClient {

    @PostMapping("/verify_create_organ")
    Result<Void> verifyCreateOrgan(@RequestBody VerifyCreateOrganRequest request);
}
