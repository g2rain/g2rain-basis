package com.g2rain.basis.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author alpha
 * @since 2026/1/19
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "接口路径与请求方法对")
public class ApiEndpointPairDto {

    /**
     * 接口路径
     */
    @Schema(description = "接口路径", example = "/user/list")
    private String apiUrl;

    /**
     * 请求方法
     */
    @Schema(description = "请求方法", example = "GET")
    private String requestMethod;
}
