package com.g2rain.basis.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class IdpEnterpriseApplicationAuthorizationResolveRequest {
    @NotBlank
    private String idpType;
    @NotBlank
    private String bindMode;
    @NotBlank
    private String idpApplicationCode;
    @NotBlank
    private String enterpriseId;
}
