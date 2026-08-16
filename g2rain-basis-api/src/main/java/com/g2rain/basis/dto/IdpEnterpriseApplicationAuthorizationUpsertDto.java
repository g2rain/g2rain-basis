package com.g2rain.basis.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class IdpEnterpriseApplicationAuthorizationUpsertDto {
    @NotBlank
    private String idpType;
    @NotBlank
    private String bindMode;
    @NotBlank
    private String idpApplicationCode;
    @NotBlank
    private String enterpriseId;
    private String installedApplicationId;
    @NotBlank
    private String authorizationStatus;
    private String credentialCiphertext;
    private String credentialKeyId;
    private LocalDateTime authorizedAt;
    private LocalDateTime credentialExpireAt;
    private String rawAuthorization;
}
