package com.g2rain.basis.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 服务间解析外部企业 → 平台 organId 的返回。
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "外部企业与机构映射解析结果")
public class IdpEnterpriseOrganResolveVo {

    @Schema(description = "平台机构标识")
    private Long organId;

    @Schema(description = "身份源类型")
    private String idpType;

    @Schema(description = "外部企业/租户标识")
    private String enterpriseId;

    @Schema(description = "接入形态")
    private String bindMode;

    @Schema(description = "映射状态")
    private String status;
}
