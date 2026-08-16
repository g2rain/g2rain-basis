package com.g2rain.basis.dao.po;

import com.g2rain.common.model.BasePo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class IdpEnterpriseApplicationAuthorizationPo extends BasePo {
    private String idpType;
    private String bindMode;
    private String idpApplicationCode;
    private String enterpriseId;
    private String installedApplicationId;
    private String authorizationStatus;
    private String credentialCiphertext;
    private String credentialKeyId;
    private LocalDateTime authorizedAt;
    private LocalDateTime revokedAt;
    private LocalDateTime credentialExpireAt;
    private String rawAuthorization;
    private Boolean deleteFlag;
}
