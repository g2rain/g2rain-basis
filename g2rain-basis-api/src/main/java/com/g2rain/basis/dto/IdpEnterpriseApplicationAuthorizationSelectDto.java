package com.g2rain.basis.dto;

import com.g2rain.common.model.BaseSelectListDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "外部 IdP 企业应用授权查询参数")
public class IdpEnterpriseApplicationAuthorizationSelectDto extends BaseSelectListDto {
    private String idpType;
    private String bindMode;
    private String idpApplicationCode;
    private String enterpriseId;
    private String installedApplicationId;
    private String authorizationStatus;
}
