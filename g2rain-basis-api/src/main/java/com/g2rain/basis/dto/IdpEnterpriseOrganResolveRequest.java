package com.g2rain.basis.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 服务间解析外部企业 → 平台 organId。
 */
@Getter
@Setter
@NoArgsConstructor
public class IdpEnterpriseOrganResolveRequest {

    @NotBlank
    private String idpType;

    @NotBlank
    private String enterpriseId;

    /** 可选；传入时进一步按接入形态过滤。 */
    private String bindMode;
}
