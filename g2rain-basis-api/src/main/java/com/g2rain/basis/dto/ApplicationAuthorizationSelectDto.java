package com.g2rain.basis.dto;

import com.g2rain.common.model.BaseSelectListDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 应用授权记录表查询入参DTO
 * 用于ApplicationAuthorizationDao.selectList方法的条件筛选
 * 表名: application_authorization
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "应用授权记录查询入参 DTO")
public class ApplicationAuthorizationSelectDto extends BaseSelectListDto {

    /**
     * 机构标识
     */
    @Schema(description = "机构标识")
    private Long organId;

    /**
     * 应用标识
     */
    @Schema(description = "应用标识")
    private Long applicationId;

    /**
     * 控制域标识
     */
    @Schema(description = "控制域标识")
    private Long controlDomainId;

    /**
     * 订阅标识
     */
    @Schema(description = "订阅标识")
    private Long subscriptionId;

    /**
     * 应用授权状态[ACTIVATED:激活, DEACTIVATED:关停]
     */
    @Schema(description = "应用授权状态[ACTIVATED:激活, DEACTIVATED:关停]", allowableValues = {"ACTIVATED", "DEACTIVATED"})
    private String status;
}
