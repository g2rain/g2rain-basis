package com.g2rain.basis.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 接口权限值对象（VO）。
 * <p>
 * 用于前后端交互中传递接口的基本信息及权限标识，
 * 包括接口名称、路径、请求方法和分类标签。
 * </p>
 *
 * @author alpha
 * @since 2026/1/15
 */
@Setter
@Getter
@NoArgsConstructor
@Schema(description = "接口权限基础信息 VO")
public class BaseAuthorityApiVo {

    /**
     * 资源接口标识
     */
    @Schema(description = "资源接口标识")
    private Long id;

    /**
     * 接口地址状态
     */
    @Schema(description = "接口地址状态")
    private String status;
}
