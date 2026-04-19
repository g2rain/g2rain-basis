package com.g2rain.basis.enums;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * @author alpha
 * @since 2026/1/14
 */
@Getter
@Schema(description = "密钥算法枚举")
public enum KeyAlgorithm {
    @Schema(description = "椭圆曲线算法(EC)")
    EC("EC");

    private final String algorithmName;

    KeyAlgorithm(String algorithmName) {
        this.algorithmName = algorithmName;
    }
}
