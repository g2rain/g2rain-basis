package com.g2rain.basis.dao.po;

import com.g2rain.common.model.BasePo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 服务注册表返回Po
 * 关联表名: service_registry
 * 功能：封装实体数据，继承BasePo复用基础字段逻辑
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ServiceRegistryPo extends BasePo {

    /**
     * 服务逻辑编码
     */
    private String serviceCode;

    /**
     * 服务显示名称
     */
    private String name;

    /**
     * 服务目标地址
     */
    private String endpoint;

    /**
     * 网关路由前缀
     */
    private String routePrefix;

    /**
     * 后端服务说明
     */
    private String description;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    private Boolean deleteFlag;
}