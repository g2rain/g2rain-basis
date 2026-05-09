package com.g2rain.basis.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.g2rain.common.model.BaseVo;

import com.g2rain.common.json.ConditionalJsonIgnore;
import com.g2rain.common.json.AdminCompanyCondition;

/**
 * 服务注册表返回VO
 * 关联表名: service_registry
 * 功能：封装接口返回数据，继承BaseVo复用基础字段逻辑，隔离数据库实体与前端展示层
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "服务注册 VO")
public class ServiceRegistryVo extends BaseVo {

    /**
     * 服务逻辑编码
     */
    @Schema(description = "服务逻辑编码", example = "G2RAIN_BASIS")
    private String serviceCode;

    /**
     * 服务显示名称
     */
    @Schema(description = "服务显示名称", example = "业务支撑服务")
    private String name;

    /**
     * 服务目标地址
     */
    @Schema(description = "服务目标地址", example = "lb://g2rain-basis")
    private String endpoint;

    /**
     * 网关路由前缀
     */
    @Schema(description = "网关路由前缀", example = "basis")
    private String routePrefix;

    /**
     * 后端服务说明
     */
    @Schema(description = "后端服务说明", example = "后端服务说明")
    private String description;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    @ConditionalJsonIgnore(adminCompany = AdminCompanyCondition.TRUE)
    @Schema(description = "删除标识(0 未删除, 1 已删除)", example = "false")
    private Boolean deleteFlag;
}
