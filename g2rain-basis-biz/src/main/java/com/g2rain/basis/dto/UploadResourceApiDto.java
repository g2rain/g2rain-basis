package com.g2rain.basis.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <p>上传的资源接口 DTO</p>
 *
 * <p>提交整份 OpenAPI / Swagger JSON，由服务端拆成单接口最小文档后整体替换。</p>
 *
 * <p><em>Author:</em> alpha</p>
 * <p><em>Since:</em> 2026/1/19</p>
 */
@Setter
@Getter
@NoArgsConstructor
@Schema(description = "上传的资源接口 DTO")
public class UploadResourceApiDto {
    /**
     * 整份 OpenAPI / Swagger JSON 文本
     */
    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "整份 OpenAPI / Swagger JSON")
    private String document;
}
