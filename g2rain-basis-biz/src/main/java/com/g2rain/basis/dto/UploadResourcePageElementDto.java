package com.g2rain.basis.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <p>上传的页面元素资源 DTO</p>
 *
 * <p>用于封装页面元素的基本信息，包括元素名称、编码以及所属页面编码。</p>
 *
 * <p><em>Author:</em> alpha</p>
 * <p><em>Since:</em> 2026/1/19</p>
 */
@Setter
@Getter
@NoArgsConstructor
@Schema(description = "上传页面元素资源 DTO")
public class UploadResourcePageElementDto {

    /**
     * <p>页面元素名称</p>
     */
    @Schema(description = "页面元素名称")
    private String pageElementName;

    /**
     * <p>页面元素编码</p>
     */
    @Schema(description = "页面元素编码")
    private String pageElementCode;

    /**
     * <p>所属页面编码</p>
     */
    @Schema(description = "所属页面编码")
    private String pageCode;
}
