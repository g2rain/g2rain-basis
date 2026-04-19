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
 * 权限点资源关联表查询DTO
 * 表名: control_unit_resource_relation
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "权限点资源关联 DTO")
public class ControlUnitResourceRelationDto extends BaseDto {

    /**
     * 控制单元标识
     */
    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "控制单元标识")
    private Long controlUnitId;

    /**
     * 资源标识
     */
    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "资源标识")
    private Long resourceId;

    /**
     * 资源类型[MENU:菜单, PAGE:页面, PAGE_ELEMENT:页面元素, API_ENDPOINT:接口地址]
     */
    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "资源类型[MENU:菜单, PAGE:页面, PAGE_ELEMENT:页面元素, API_ENDPOINT:接口地址]", allowableValues = {"MENU", "PAGE", "PAGE_ELEMENT", "API_ENDPOINT"})
    private String resourceType;

    /**
     * 激活状态[VISIBLE:显示, ENABLED:可用]
     */
    @Schema(description = "激活状态[VISIBLE:显示, ENABLED:可用]", allowableValues = {"VISIBLE", "ENABLED"})
    private String status;
}
