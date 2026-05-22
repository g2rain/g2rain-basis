package com.g2rain.basis.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 静态访问令牌 cache-sync 载荷：tokenHash（API Key 的 SHA-256 十六进制）。
 *
 * @author alpha
 * @since 2026/5/22
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "静态访问令牌哈希同步 VO")
public class StaticAccessTokenHashVo {

    /**
     * 与 {@code personal_static_access_token.token_hash} 一致
     */
    @Schema(description = "令牌哈希（SHA-256 hex）")
    private String tokenHash;
}
