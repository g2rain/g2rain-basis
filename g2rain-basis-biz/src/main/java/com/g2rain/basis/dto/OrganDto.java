package com.g2rain.basis.dto;

import com.g2rain.common.model.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 机构 DTO
 * 表名: organ
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "机构 DTO")
public class OrganDto extends BaseDto {
    /**
     * 机构名称
     */
    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "机构名称")
    private String organName;

    /**
     * 机构类型[服务商、渠道、公司、租户]
     */
    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "机构类型[服务商、渠道、公司、租户]", allowableValues = {"SERVICE_PROVIDER", "SALES_PARTNER", "COMPANY", "TENANT"})
    private String organType;
}
