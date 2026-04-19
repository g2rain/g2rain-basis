package com.g2rain.basis.dto;

import com.g2rain.common.model.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 控制单元表查询DTO
 * 表名: control_unit
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "控制单元 DTO")
public class ControlUnitDto extends BaseDto {

    /**
     * 应用标识
     */
    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "应用标识")
    private Long applicationId;

    /**
     * 控制单元名称
     */
    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "控制单元名称")
    private String controlUnitName;

    /**
     * 控制单元类型[OPERATION("运营功能"), CUSTOMER("客户功能"), PERPETUAL("永久有效功能")]
     */
    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "控制单元类型[OPERATION(\"运营功能\"), CUSTOMER(\"客户功能\"), PERPETUAL(\"永久有效功能\")]", allowableValues = {"OPERATION", "CUSTOMER", "PERPETUAL"})
    private String controlUnitScope;

    /**
     * 业务说明
     */
    @Schema(description = "业务说明")
    private String description;
}
