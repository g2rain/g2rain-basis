package com.g2rain.basis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 自动开通全部 SELF 控制域请求。
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "自动开通 SELF 控制域请求")
public class ApplicationAuthorizationActivateSelfRequest {

    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "目标应用编码")
    private String applicationCode;

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "当前选择的用户 ID")
    private Long userId;
}
