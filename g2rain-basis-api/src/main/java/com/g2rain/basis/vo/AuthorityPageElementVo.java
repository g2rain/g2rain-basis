package com.g2rain.basis.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 页面元素权限值对象（VO）。
 * <p>
 * 用于前后端交互中传递页面元素的基本信息及权限标识。
 * 每个页面元素对应系统中的一个功能点或操作控件。
 * </p>
 *
 * @author alpha
 * @since 2026/1/15
 */
@Setter
@Getter
@NoArgsConstructor
@Schema(description = "页面元素权限 VO")
public class AuthorityPageElementVo {

    /**
     * 页面元素标识
     */
    @Schema(description = "页面元素标识")
    private Long id;

    /**
     * 页面编码
     */
    @Schema(description = "页面编码")
    private String pageCode;

    /**
     * 页面元素名称
     */
    @Schema(description = "页面元素名称")
    private String pageElementName;

    /**
     * 页面元素编码
     */
    @Schema(description = "页面元素编码")
    private String pageElementCode;

    @Schema(description = "页面元素状态")
    private String status;
}
