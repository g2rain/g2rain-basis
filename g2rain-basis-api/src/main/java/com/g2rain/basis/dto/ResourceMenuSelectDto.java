package com.g2rain.basis.dto;

import com.g2rain.common.model.BaseSelectListDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 应用资源菜单表查询入参DTO
 * 用于ResourceMenuDao.selectList方法的条件筛选
 * 表名: resource_menu
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "应用资源菜单查询入参 DTO")
public class ResourceMenuSelectDto extends BaseSelectListDto {

    /**
     * 父菜单标识
     */
    @Schema(description = "父菜单标识")
    private Long parentId;

    /**
     * 应用标识
     */
    @Schema(description = "应用标识")
    private Long applicationId;

    /**
     * 菜单名称
     */
    @Schema(description = "菜单名称")
    private String menuName;

    /**
     * 菜单编码
     */
    @Schema(description = "菜单编码")
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
    @Schema(description = "菜单排序")
    private Integer menuSortOrder;
}
