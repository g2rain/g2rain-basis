package com.g2rain.basis.dto;

import com.g2rain.common.model.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 服务注册表查询DTO
 * 表名: service_registry
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "服务注册 DTO")
public class ServiceRegistryDto extends BaseDto {

    /**
     * 服务逻辑编码
     */
    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "服务逻辑编码")
    private String serviceCode;

    /**
     * 服务显示名称
     */
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "服务显示名称")
    private String name;

    /**
     * 服务目标地址
     */
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "服务目标地址")
    private String endpoint;

    /**
     * 网关路由前缀
     */
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "网关路由前缀")
    private String routePrefix;

    /**
     * 后端服务说明
     */
    @Schema(description = "后端服务说明")
    private String description;
}
