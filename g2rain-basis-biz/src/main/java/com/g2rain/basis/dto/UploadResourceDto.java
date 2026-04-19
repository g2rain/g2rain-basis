package com.g2rain.basis.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * <p>上传的资源 DTO</p>
 *
 * <p>用于封装需要上传或同步的完整资源信息，包括页面、页面元素和接口端点。</p>
 *
 * <p><em>Author:</em> alpha</p>
 * <p><em>Since:</em> 2026/1/19</p>
 */
@Setter
@Getter
@NoArgsConstructor
@Schema(description = "上传资源 DTO")
public class UploadResourceDto {

    /**
     * <p>页面资源列表</p>
     */
    @Schema(description = "页面资源列表")
    private List<UploadResourcePageDto> pages;

    /**
     * <p>页面元素资源列表</p>
     */
    @Schema(description = "页面元素资源列表")
    private List<UploadResourcePageElementDto> pageElements;

    /**
     * <p>API 接口端点资源列表</p>
     */
    @Schema(description = "API 接口端点资源列表")
    private List<UploadResourceApiEndpointDto> apiEndpoints;
}
