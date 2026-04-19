package com.g2rain.basis.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author alpha
 * @since 2026/2/3
 */
@Schema(description = "公钥格式枚举")
public enum PublicKeyFormat {
    @Schema(description = "PEM 格式")
    PEM,
    @Schema(description = "DER 格式")
    DER;
}
