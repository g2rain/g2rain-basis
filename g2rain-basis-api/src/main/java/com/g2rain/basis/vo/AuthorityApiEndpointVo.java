package com.g2rain.basis.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 接口权限值对象（VO）。
 * <p>
 * 用于前后端交互中传递接口的基本信息及权限标识，
 * 包括接口名称、路径、请求方法和分类标签。
 * </p>
 *
 * @author alpha
 * @since 2026/1/15
 */
@Setter
@Getter
@NoArgsConstructor
@Schema(description = "接口权限 VO")
public class AuthorityApiEndpointVo {

    /**
     * 接口标识
     */
    @Schema(description = "接口标识")
    private Long id;

    /**
     * 接口名称
     */
    @Schema(description = "接口名称")
    private String apiName;

    /**
     * 接口路径
     */
    @Schema(description = "接口路径")
    private String apiUrl;

    /**
     * 请求方法
     */
    @Schema(description = "请求方法")
    private String requestMethod;

    /**
     * 接口标签, 接口分类
     */
    @Schema(description = "接口标签, 接口分类")
    private String apiTag;

    /**
     * 接口地址状态
     */
    @Schema(description = "接口地址状态")
    private String status;
}
