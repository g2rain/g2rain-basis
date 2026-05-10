package com.g2rain.basis.vo;

import com.g2rain.common.json.AdminCompanyCondition;
import com.g2rain.common.json.ConditionalJsonIgnore;
import com.g2rain.common.model.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 资源接口表返回VO
 * 关联表名: resource_api
 * 功能：封装接口返回数据，继承BaseVo复用基础字段逻辑，隔离数据库实体与前端展示层
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "服务注册记录 VO")
public class ResourceApiVo extends BaseVo {

    /**
     * 服务编码
     */
    @Schema(description = "服务编码")
    private String serviceCode;

    /**
     * 服务名称
     */
    @Schema(description = "服务名称")
    private String serviceName;

    /**
     * 资源接口标签
     */
    @Schema(description = "资源接口标签")
    private String apiTags;

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

    /**
     * 资源接口说明
     */
    @Schema(description = "资源接口说明")
    private String description;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    @Schema(description = "删除标识[0:未删除, 1:已删除]")
    @ConditionalJsonIgnore(adminCompany = AdminCompanyCondition.TRUE)
    private Boolean deleteFlag;
}
