package com.g2rain.basis.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * <p>上传的资源接口 DTO</p>
 *
 * <p>用于封装需要上传或同步的接口地址及请求方式信息。</p>
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
     * 接口集合
     */
    @Valid
    @NotEmpty
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "接口集合")
    private List<UploadApiDto> apis;
}
