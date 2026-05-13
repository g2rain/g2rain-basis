package com.g2rain.basis.dto;

import com.g2rain.common.model.BaseSelectListDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 外部企业/租户与平台机构关联表查询入参DTO
 * 用于IdpEnterpriseOrganDao.selectList方法的条件筛选
 * 表名: idp_enterprise_organ
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "外部企业/租户与平台机构关联查询入参 DTO")
public class IdpEnterpriseOrganSelectDto extends BaseSelectListDto {

    /**
     * 身份源类型，与 {@link com.g2rain.basis.enums.IdpType} 一致。
     */
    @Schema(description = "身份源类型（与 IdpType 枚举名一致）",
        allowableValues = {"DINGTALK", "FEISHU", "WECHAT_WORK"})
    private String idpType;

    /**
     * 外部企业/租户标识（与 passport_idp_binding.enterprise_id 一致）
     */
    @Schema(description = "外部企业/租户标识（与 passport_idp_binding.enterprise_id 一致）")
    private String enterpriseId;

    /**
     * 机构标识，关联 organ.id（业务上应为租户类型机构）
     */
    @Schema(description = "机构标识，关联 organ.id（业务上应为租户类型机构）")
    private Long organId;

    /**
     * 状态[ACTIVE:有效, INACTIVE:停用]
     */
    @Schema(description = "状态[ACTIVE:有效, INACTIVE:停用]", allowableValues = {"ACTIVE", "INACTIVE"})
    private String status;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}
