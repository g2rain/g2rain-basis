package com.g2rain.basis.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.g2rain.common.model.BaseSelectListDto;


/**
 * 外部身份源应用与平台应用的绑定查询入参DTO
 * 用于ApplicationIdpProvisionDao.selectList方法的条件筛选
 * 表名: application_idp_provision
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ApplicationIdpProvisionSelectDto extends BaseSelectListDto {

    /**
     * 平台应用标识，关联 application.id
     */
    private Long applicationId;

    /**
     * 身份源类型，与 IdpType 枚举名一致
     */
    private String idpType;

    /**
     * 三方应用在 IdP 侧的标识（如钉钉 OAuth clientId）
     */
    private String idpApplicationCode;

    /**
     * 备注
     */
    private String remark;
}