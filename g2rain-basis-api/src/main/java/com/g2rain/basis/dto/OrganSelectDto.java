package com.g2rain.basis.dto;

import com.g2rain.common.model.BaseSelectListDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 机构表查询入参DTO
 * 用于OrganDao.selectList方法的条件筛选
 * 表名: organ
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "机构查询入参 DTO")
public class OrganSelectDto extends BaseSelectListDto {

    /**
     * 登录的机构数据隔离标识
     */
    @Schema(description = "数据隔离标识", hidden = true)
    private Long organId;

    /**
     * 机构名称
     */
    @Schema(description = "机构名称")
    private String organName;

    /**
     * 机构类型[服务商、渠道、公司、租户]
     */
    @Schema(description = "机构类型[服务商、渠道、公司、租户]", allowableValues = {"SERVICE_PROVIDER", "SALES_PARTNER", "COMPANY", "TENANT"})
    private String organType;

    /**
     * 机构状态[ACTIVE:有效, INACTIVE:无效]
     */
    @Schema(description = "机构状态[ACTIVE:有效, INACTIVE:无效]", allowableValues = {"ACTIVE", "INACTIVE"})
    private String status;

    /**
     * 运营标记
     */
    @Schema(description = "运营标记")
    private Boolean admin;
}
