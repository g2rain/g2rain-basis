package com.g2rain.basis.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 公钥描述信息 VO（View Object）。
 *
 * <p>用于返回指定应用的公钥结构化信息，包括算法、格式及公钥内容。</p>
 *
 * <ul>
 *     <li>{@code publicKeyAlgorithm}：应用公钥算法，如 EC、RSA 等。</li>
 *     <li>{@code publicKeyFormat}：公钥编码格式，如 PEM 或 DER。</li>
 *     <li>{@code publicKey}：公钥内容的字节数组，Feign 调用或 JSON 传输时自动 Base64 编码。</li>
 * </ul>
 *
 * <p>该 VO 主要用于接口返回，不包含私钥信息。</p>
 *
 * @author alpha
 * @since 2026/2/3
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "公钥描述信息 VO")
public class PublicKeyDescriptorVo {

    /**
     * 应用公钥算法[EC]
     */
    @Schema(description = "应用公钥算法[EC]")
    private String publicKeyAlgorithm;

    /**
     * 应用公钥格式
     */
    @Schema(description = "应用公钥格式")
    private String publicKeyFormat;

    /**
     * 应用公钥内容
     */
    @Schema(description = "应用公钥内容")
    private String publicKey;
}
