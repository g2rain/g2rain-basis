package com.g2rain.basis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "登录请求参数")
public class LoginDto {

    /**
     * 用户名
     */
    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "用户名", example = "alpha")
    private String username;

    /**
     * 密码
     */
    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "密码", example = "******")
    private String password;
}
