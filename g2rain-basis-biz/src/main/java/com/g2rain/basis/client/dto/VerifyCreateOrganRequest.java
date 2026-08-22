package com.g2rain.basis.client.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Feign 调用 IAM {@code /internal/tenant_provision/verify_create_organ} 的请求体。
 */
@Getter
@Setter
@NoArgsConstructor
public class VerifyCreateOrganRequest {

    private Long passportId;

    public VerifyCreateOrganRequest(Long passportId) {
        this.passportId = passportId;
    }
}
