package com.g2rain.basis.api;


import com.g2rain.basis.vo.BaseAuthorityApiVo;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author alpha
 * @since 2026/5/5
 */
@Tag(name = "资源授权", description = "资源授权相关接口")
public interface AuthorityApi {
    /**
     * 查询指定用户在指定应用下的接口权限列表
     *
     * @param userId        用户 ID
     * @param applicationId 应用 ID
     * @return 接口权限视图对象列表
     */
    @GetMapping("/apis")
    @Operation(summary = "查询接口权限列表", hidden = true, description = "查询指定用户在指定应用下的接口权限列表")
    Result<List<BaseAuthorityApiVo>> getApiPermissions(@Parameter(description = "用户标识") @RequestParam Long userId, @Parameter(description = "应用标识") @RequestParam Long applicationId);

    /**
     * 查询账号的接口权限集合
     *
     * @return 账号的接口权限集合
     */
    @GetMapping("/passport_api_permissions")
    @Operation(summary = "查询账号的接口权限集合", hidden = true, description = "查询账号的接口权限集合")
    Result<List<Long>> getPassportApiPermissions();
}
