package com.g2rain.basis.dto;

import com.g2rain.common.model.BaseSelectListDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;


/**
 * 服务注册表查询入参DTO
 * 用于ServiceRegistryDao.selectList方法的条件筛选
 * 表名: service_registry
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "服务注册查询入参 DTO")
public class ServiceRegistrySelectDto extends BaseSelectListDto {

    /**
     * 服务逻辑编码
     */
    @Schema(description = "服务逻辑编码")
    private String serviceCode;

    /**
     * 服务逻辑编码集合
     */
    @Schema(hidden = true, description = "服务逻辑编码集合")
    private Set<String> serviceCodes;

    /**
     * 服务显示名称
     */
    @Schema(description = "服务显示名称")
    private String name;

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
     * 后端服务说明
     */
    @Schema(description = "后端服务说明")
    private String description;
}
