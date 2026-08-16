package com.g2rain.basis.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class IdpEnterpriseApplicationAuthorizationRevokeDto {
    @NotBlank
    private String idpType;
    @NotBlank
    private String idpApplicationCode;
    @NotBlank
    private String enterpriseId;
    private LocalDateTime revokedAt;
}
