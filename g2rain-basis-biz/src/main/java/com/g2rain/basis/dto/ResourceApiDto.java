package com.g2rain.basis.dto;

import com.g2rain.common.model.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 资源接口表查询DTO
 * 表名: resource_api
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "控制域 DTO")
public class ResourceApiDto extends BaseDto {

    /**
     * 服务逻辑编码
     */
    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "服务逻辑编码")
    private String serviceCode;

    /**
     * 资源接口标签
     */
    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "资源接口标签")
    private String apiTags;

    /**
     * 资源接口名称
     */
    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "资源接口名称")
    private String name;

    /**
     * 接口请求方法
     */
    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "接口请求方法")
    private String method;

    /**
     * 接口请求路径
     */
    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "接口请求路径")
    private String path;

    /**
     * 资源接口说明
     */
    @Schema(description = "应用标识")
    private String description;
}
