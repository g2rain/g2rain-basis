package com.g2rain.basis.dao.po;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 接口权限值对象 Po
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
public class AuthorityApiEndpointPo {
    /**
     * 资源接口标识
     */
    private Long id;

    /**
     * 接口名称
     */
    private String apiName;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 目标地址
     */
    private String endpoint;

    /**
     * 路由前缀
     */
    private String routePrefix;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 请求路径
     */
    private String path;

    /**
     * 接口状态
     */
    private String status;
}
