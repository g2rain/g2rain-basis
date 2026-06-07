package com.g2rain.basis.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <p>用户状态修改 DTO</p>
 *
 * <p>用于封装用户状态更新请求的信息，例如启用或禁用用户。</p>
 *
 * <p><strong>注意：</strong>status 字段不能为空或空白，应为合法状态值。</p>
 *
 * <p><em>Author:</em> alpha</p>
 * <p><em>Since:</em> 2026/1/14</p>
 */
@Setter
@Getter
@NoArgsConstructor
@Schema(description = "状态修改 DTO")
public class UpdateStatusDto {

    /**
     * <p>业务状态</p>
     */
    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "业务状态")
    private String status;
}
