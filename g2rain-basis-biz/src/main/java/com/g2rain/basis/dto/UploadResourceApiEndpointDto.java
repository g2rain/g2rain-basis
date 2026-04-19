package com.g2rain.basis.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <p>上传的 API 接口端点 DTO</p>
 *
 * <p>用于封装需要上传或同步的接口地址及请求方式信息。</p>
 *
 * <p><em>Author:</em> alpha</p>
 * <p><em>Since:</em> 2026/1/19</p>
 */
@Setter
@Getter
@NoArgsConstructor
@Schema(description = "上传的 API 接口端点 DTO")
public class UploadResourceApiEndpointDto {

    /**
     * <p>接口地址 URL</p>
     */
    @Schema(description = "接口地址 URL")
    private String apiUrl;

    /**
     * <p>请求方式</p>
     * <p>例如 GET、POST、PUT、DELETE 等</p>
     */
    @Schema(description = "请求方式")
    private String requestMethod;
}
