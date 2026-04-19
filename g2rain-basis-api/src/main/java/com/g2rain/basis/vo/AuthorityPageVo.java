package com.g2rain.basis.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 页面权限值对象（VO）。
 * <p>
 * 用于前后端交互中传递页面的基本信息及权限标识。
 * </p>
 *
 * @author alpha
 * @since 2026/1/15
 */
@Setter
@Getter
@NoArgsConstructor
@Schema(description = "页面权限 VO")
public class AuthorityPageVo {

    /**
     * 页面标识
     */
    @Schema(description = "页面标识")
    private Long id;

    /**
     * 页面名称
     */
    @Schema(description = "页面名称")
    private String pageName;

    /**
     * 页面编码
     */
    @Schema(description = "页面编码")
    private String pageCode;

    /**
     * 链接路径
     */
    @Schema(description = "链接路径")
    private String linkPath;
}
