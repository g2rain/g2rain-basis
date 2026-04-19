package com.g2rain.basis.dao.po;

import com.g2rain.common.model.BasePo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 接口地址表返回Po
 * 关联表名: api_endpoint
 * 功能：封装实体数据，继承BasePo复用基础字段逻辑
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ApiEndpointPo extends BasePo {

    /**
     * 接口名称
     */
    private String apiName;

    /**
     * 接口路径
     */
    private String apiUrl;

    /**
     * 请求方法
     */
    private String requestMethod;

    /**
     * 接口标签, 接口分类
     */
    private String apiTag;

    /**
     * 业务说明
     */
    private String description;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    private Boolean deleteFlag;
}
