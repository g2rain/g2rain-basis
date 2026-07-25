package com.g2rain.basis.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class IdpEnterpriseApplicationAuthorizationResolveVo {
    private Long authorizationId;
    private String idpType;
    private String bindMode;
    private String idpApplicationCode;
    private String enterpriseId;
    private String installedApplicationId;
    private String authorizationStatus;
}
