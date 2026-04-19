package com.g2rain.basis.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * <p>角色或用户的权限资源视图对象</p>
 *
 * <p>包含页面、页面元素和接口端点的权限信息，用于前端或服务端权限控制展示。</p>
 *
 * <p><strong>注意：</strong>此对象用于封装用户或角色的完整权限资源，包括页面权限、页面元素权限和API接口权限。</p>
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "权限资源 VO")
public class AuthorityResourceVo {

    /**
     * <p>拥有权限的页面列表</p>
     */
    @Schema(description = "权限的页面列表")
    private List<AuthorityPageVo> pages;

    /**
     * <p>拥有权限的页面元素列表</p>
     */
    @Schema(description = "权限的页面元素列表")
    private List<AuthorityPageElementVo> pageElements;

    /**
     * <p>拥有权限的 API 接口端点列表</p>
     */
    @Schema(description = "权限的 API 接口端点列表")
    private List<AuthorityApiEndpointVo> apiEndpoints;
}
