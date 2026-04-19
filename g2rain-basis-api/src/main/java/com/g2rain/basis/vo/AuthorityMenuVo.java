package com.g2rain.basis.vo;


import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单权限值对象（VO）。
 * <p>
 * 用于前后端交互中传递菜单信息及权限标识，
 * 支持层级结构，每个菜单可包含子菜单，实现递归嵌套。
 * </p>
 *
 * @author alpha
 * @since 2026/1/15
 */
@Setter
@Getter
@NoArgsConstructor
@Schema(description = "菜单权限 VO")
public class AuthorityMenuVo {

    /**
     * 菜单标识
     */
    @Schema(description = "菜单标识")
    private Long id;

    /**
     * 父菜单标识
     */
    @Schema(description = "父菜单标识")
    private Long parentId;

    /**
     * 应用编码
     */
    @Schema(description = "应用编码")
    private String applicationCode;

    /**
     * 访问地址
     */
    @Schema(description = "访问地址")
    private String endpointUrl;

    /**
     * 应用路径
     */
    @Schema(description = "应用路径")
    private String contextPath;

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

    /**
     * 子菜单列表
     * <p>可为空或包含多个子菜单，实现树形结构递归</p>
     */
    @ArraySchema(
        arraySchema = @Schema(description = "子菜单集合"),
        schema = @Schema(description = "子菜单权限")
    )
    private List<AuthorityMenuVo> subMenus = new ArrayList<>();
}
