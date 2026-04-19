package com.g2rain.basis.dao.po;

import com.g2rain.common.model.BasePo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 应用资源接口地址表返回Po
 * 关联表名: resource_api_endpoint
 * 功能：封装实体数据，继承BasePo复用基础字段逻辑
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ResourceApiPo extends BasePo {

    /**
     * 应用标识
     */
    private Long applicationId;

    /**
     * 接口地址标识
     */
    private Long apiEndpointId;

    /**
     * 接口标签, 接口分类
     */
    private String apiTag;

    /**
     * 接口名称
     */
    private String apiName;

    /**
     * 请求方法
     */
    private String requestMethod;

    /**
     * 接口路径
     */
    private String apiUrl;
}
