package com.g2rain.basis.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author alpha
 * @since 2026/4/28
 */
@Setter
@Getter
@NoArgsConstructor
@Schema(description = "网关路由 VO")
public class RouteDefinitionVo {
    /**
     * 资源接口标识
     */
    @Schema(description = "资源接口标识")
    private Long id;

    /**
     * 服务目标地址
     */
    @Schema(description = "服务目标地址")
    private String endpoint;

    /**
     * 网关路由前缀
     */
    @Schema(description = "网关路由前缀")
    private String routePrefix;

    /**
     * 资源接口名称
     */
    @Schema(description = "资源接口名称")
    private String name;

    /**
     * 接口请求方法
     */
    @Schema(description = "接口请求方法")
    private String method;

    /**
     * 接口请求路径
     */
    @Schema(description = "接口请求路径")
    private String path;
}
