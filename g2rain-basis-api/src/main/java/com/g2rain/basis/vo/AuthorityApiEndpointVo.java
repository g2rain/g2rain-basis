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
public class AuthorityApiEndpointVo extends BaseAuthorityApiVo {
    /**
     * 服务名称
     */
    @Schema(description = "服务名称")
    private String serviceName;

    /**
     * 目标地址
     */
    @Schema(description = "目标地址")
    private String endpoint;

    /**
     * 路由前缀
     */
    @Schema(description = "路由前缀")
    private String routePrefix;

    /**
     * 接口名称
     */
    @Schema(description = "接口名称")
    private String apiName;

    /**
     * 请求方法
     */
    @Schema(description = "请求方法")
    private String method;

    /**
     * 请求路径
     */
    @Schema(description = "请求路径")
    private String path;
}
