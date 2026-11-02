package com.g2rain.basis.dao.po;

import com.g2rain.common.model.BasePo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 资源接口表返回Po
 * 关联表名: resource_api
 * 功能：封装实体数据，继承BasePo复用基础字段逻辑
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ResourceApiPo extends BasePo {

    /**
     * 服务逻辑编码
     */
    private String serviceCode;

    /**
     * 资源接口标签
     */
    private String apiTags;

    /**
     * 资源接口名称
     */
    private String name;

    /**
     * 接口请求方法
     */
    private String method;

    /**
     * 接口请求路径
     */
    private String path;

    /**
     * 资源接口说明
     */
    private String description;

    /**
     * 单接口最小 OpenAPI 文档
     */
    private String openapiDocument;

    /**
     * 规范化单接口文档的 sha256
     */
    private String documentDigest;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    private Boolean deleteFlag;
}