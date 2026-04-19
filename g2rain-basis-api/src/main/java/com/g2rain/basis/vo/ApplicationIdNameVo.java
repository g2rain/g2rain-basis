package com.g2rain.basis.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 应用 ID 与名称的值对象（VO）。
 *
 * @author alpha
 * @since 2026/1/20
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "应用名称 VO")
public class ApplicationIdNameVo {

    /**
     * 应用标识
     */
    @Schema(description = "应用标识")
    private Long id;

    /**
     * 应用名称
     */
    @Schema(description = "名称")
    private String applicationName;
}
