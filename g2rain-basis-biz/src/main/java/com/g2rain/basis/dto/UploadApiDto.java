package com.g2rain.basis.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <p>上传的接口 DTO</p>
 *
 * <p>用于封装需要上传或同步的接口地址及请求方式信息。</p>
 *
 * <p><em>Author:</em> alpha</p>
 * <p><em>Since:</em> 2026/1/19</p>
 */
@Setter
@Getter
@NoArgsConstructor
@Schema(description = "上传的接口 DTO")
public class UploadApiDto {
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

    @Schema(description = "资源接口说明")
    private String description;
}
