package com.g2rain.basis.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <p>用户修改密码 DTO</p>
 *
 * <p>用于封装用户在修改密码时提交的新密码信息。</p>
 *
 * <p><strong>注意：</strong>password 字段不能为空或空白。</p>
 *
 * <p><em>Author:</em> alpha</p>
 * <p><em>Since:</em> 2026/1/14</p>
 */
@Setter
@Getter
@NoArgsConstructor
@Schema(description = "用户修改密码 DTO")
public class PassportUpdatePasswordDto {
    /**
     * <p>旧密码</p>
     *
     * <p>不能为空或空白。</p>
     */
    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "旧密码")
    private String oldPassword;

    /**
     * <p>新密码</p>
     *
     * <p>不能为空或空白。</p>
     */
    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "新密码")
    private String newPassword;
}
