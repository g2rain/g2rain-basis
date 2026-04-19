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
 * 应用资源菜单 DTO
 * 表名: resource_menu
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "应用资源菜单 DTO")
public class ResourceMenuDto extends BaseDto {

    /**
     * 父菜单标识
     */
    @Schema(description = "父菜单标识")
    private Long parentId;

    /**
     * 应用标识
     */
    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "应用标识")
    private Long applicationId;

    /**
     * 菜单名称
     */
    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "菜单名称")
    private String menuName;

    /**
     * 菜单编码
     */
    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "菜单编码")
    private String menuCode;

    /**
     * 链接路径
     */
    @Schema(description = "链接路径")
    private String linkPath;

    /**
     * 展示图标
     */
    @Schema(description = "展示图标")
    private String icon;

    /**
     * 菜单排序
     */
    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "菜单排序")
    private Integer menuSortOrder;
}
