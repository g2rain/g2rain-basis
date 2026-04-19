package com.g2rain.basis.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <p>上传的页面资源 DTO</p>
 *
 * <p>用于封装页面资源的基本信息，包括页面名称、编码和链接路径。</p>
 *
 * <p><em>Author:</em> alpha</p>
 * <p><em>Since:</em> 2026/1/19</p>
 */
@Setter
@Getter
@NoArgsConstructor
@Schema(description = "上传页面资源 DTO")
public class UploadResourcePageDto {

    /**
     * <p>页面名称</p>
     */
    @Schema(description = "页面名称")
    private String pageName;

    /**
     * <p>页面编码</p>
     */
    @Schema(description = "页面编码")
    private String pageCode;

    /**
     * <p>页面链接路径</p>
     */
    @Schema(description = "页面链接路径")
    private String linkPath;
}
