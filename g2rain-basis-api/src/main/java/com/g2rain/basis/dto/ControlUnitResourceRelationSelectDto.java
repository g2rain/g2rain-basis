package com.g2rain.basis.dto;

import com.g2rain.common.model.BaseSelectListDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 权限点资源关联表查询入参DTO
 * 用于ControlUnitResourceRelationDao.selectList方法的条件筛选
 * 表名: control_unit_resource_relation
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "权限点资源关联查询入参 DTO")
public class ControlUnitResourceRelationSelectDto extends BaseSelectListDto {

    /**
     * 控制单元标识
     */
    @Schema(description = "控制单元标识")
    private Long controlUnitId;

    /**
     * 资源标识
     */
    @Schema(description = "资源标识")
    private Long resourceId;

    /**
     * 资源类型[MENU:菜单, PAGE:页面, PAGE_ELEMENT:页面元素, API_ENDPOINT:接口地址]
     */
    @Schema(description = "资源类型[MENU:菜单, PAGE:页面, PAGE_ELEMENT:页面元素, API_ENDPOINT:接口地址]", allowableValues = {"MENU", "PAGE", "PAGE_ELEMENT", "API_ENDPOINT"})
    private String resourceType;

    /**
     * 激活状态[VISIBLE:显示, ENABLED:可用]
     */
    @Schema(description = "激活状态[VISIBLE:显示, ENABLED:可用]", allowableValues = {"VISIBLE", "ENABLED"})
    private String status;
}
