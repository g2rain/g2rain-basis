package com.g2rain.basis.dto;

import com.g2rain.common.model.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 接口地址表查询DTO
 * 表名: api_endpoint
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "接口地址 DTO")
public class ApiEndpointDto extends BaseDto {

    /**
     * 接口名称
     */
    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "接口名称")
    private String apiName;

    /**
     * 接口路径
     */
    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "接口路径")
    private String apiUrl;

    /**
     * 请求方法
     */
    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "请求方法")
    private String requestMethod;

    /**
     * 接口标签, 接口分类
     */
    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "接口标签, 接口分类")
    private String apiTag;

    /**
     * 业务说明
     */
    @Schema(description = "业务说明")
    private String description;
}
