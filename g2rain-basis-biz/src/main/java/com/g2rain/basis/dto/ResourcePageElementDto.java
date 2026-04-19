package com.g2rain.basis.dto;

import com.g2rain.common.model.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 应用资源页面元素 DTO
 * 表名: resource_page_element
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "应用资源页面元素 DTO")
public class ResourcePageElementDto extends BaseDto {

    /**
     * 应用标识
     */
    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "应用标识")
    private Long applicationId;

    /**
     * 页面编码
     */
    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "页面编码")
    private String pageCode;

    /**
     * 页面元素名称
     */
    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "页面元素名称")
    private String pageElementName;

    /**
     * 页面元素编码
     */
    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "页面元素编码")
    private String pageElementCode;
}
