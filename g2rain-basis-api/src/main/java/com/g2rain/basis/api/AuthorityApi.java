package com.g2rain.basis.api;


import com.g2rain.basis.vo.BaseAuthorityApiVo;
import com.g2rain.basis.vo.SessionApiPermissionVo;
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
    Result<List<BaseAuthorityApiVo>> getApiPermissions(
        @Parameter(description = "用户标识") @RequestParam(required = false) Long userId,
        @Parameter(description = "角色 ID 集合；非空时优先按角色查询") @RequestParam(required = false) List<Long> roleIds,
        @Parameter(description = "应用标识") @RequestParam Long applicationId
    );

    /**
     * 查询账号的接口权限集合
     *
     * @return 账号的接口权限集合
     */
    @GetMapping("/passport_api_permissions")
    @Operation(summary = "查询账号的接口权限集合", hidden = true, description = "查询账号的接口权限集合")
    Result<List<Long>> getPassportApiPermissions();

    /**
     * 按会话主体类型查询 API 权限快照。
     * <p>MEMBER 必须传 organId，按租户已开通的 MEMBER 控制单元计算；禁止退回全平台清单。</p>
     *
     * @param sessionType 会话主体类型
     * @param organId     机构 ID；sessionType=MEMBER 时必填
     * @return 权限快照
     */
    @GetMapping("/session_api_permissions")
    @Operation(summary = "查询会话 API 权限快照", hidden = true,
        description = "MEMBER 必须带 organId，返回该租户已开通 MEMBER 控制单元关联的 resource_api.id")
    Result<SessionApiPermissionVo> getSessionApiPermissions(
        @Parameter(description = "会话主体类型") @RequestParam String sessionType,
        @Parameter(description = "机构标识；MEMBER 必填") @RequestParam(required = false) Long organId
    );
}
