package com.g2rain.basis.vo;

import com.g2rain.common.model.BaseVo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class IdpEnterpriseApplicationAuthorizationVo extends BaseVo {
    private String idpType;
    private String bindMode;
    private String idpApplicationCode;
    private String enterpriseId;
    private String installedApplicationId;
    private String authorizationStatus;
    private String credentialCiphertext;
    private String credentialKeyId;
    private String authorizedAt;
    private String revokedAt;
    private String credentialExpireAt;
    private String rawAuthorization;
    private Boolean deleteFlag;
}
