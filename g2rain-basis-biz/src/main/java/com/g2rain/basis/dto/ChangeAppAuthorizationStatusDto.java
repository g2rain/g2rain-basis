package com.g2rain.basis.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 应用授权状态变更参数对象
 * <p>
 * 用于承载订阅关系中应用授权状态的变更请求数据。
 *
 * @author alpha
 * @since 2026/1/20
 */
@Setter
@Getter
@NoArgsConstructor
@Schema(description = "应用授权状态变更 DTO")
public class ChangeAppAuthorizationStatusDto {

    /**
     * 订阅标识
     */
    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "订阅标识")
    private Long subscriptionId;

    /**
     * 授权状态
     */
    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "授权状态")
    private String status;
}
