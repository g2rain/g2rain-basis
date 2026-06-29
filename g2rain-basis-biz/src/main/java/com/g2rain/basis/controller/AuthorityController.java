package com.g2rain.basis.controller;

import com.g2rain.basis.api.AuthorityApi;
import com.g2rain.basis.service.AuthorityService;
import com.g2rain.basis.vo.AuthorityMenuVo;
import com.g2rain.basis.vo.AuthorityResourceVo;
import com.g2rain.basis.vo.AuthorityUserVo;
import com.g2rain.basis.vo.BaseAuthorityApiVo;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 权限控制器
 * <p>
 * 提供当前用户在菜单、资源及接口维度上的权限查询接口。
 *
 * @author Alpha
 */
@RestController
@RequestMapping("/authority")
public class AuthorityController implements AuthorityApi {

    @Resource(name = "authorityServiceImpl")
    private AuthorityService authorityService;

    /**
     * 查询指定用户在指定应用下的接口权限列表
     *
     * @param userId        用户 ID
     * @param applicationId 应用 ID
     * @return 接口权限视图对象列表
     */
    @Override
    public Result<List<BaseAuthorityApiVo>> getApiPermissions(Long userId, List<Long> roleIds, Long applicationId) {
        return Result.success(authorityService.getApiPermissions(userId, roleIds, applicationId));
    }

    /**
     * 查询账号的接口权限集合
     *
     * @return 账号的接口权限集合
     */
    @Override
    public Result<List<Long>> getPassportApiPermissions() {
        return Result.success(authorityService.getPassportApiPermissions());
    }

    /**
     * 查询当前用户的菜单权限列表
     *
     * @return 菜单权限视图对象列表
     */
    @GetMapping("/menus")
    @Operation(summary = "查询菜单权限列表", description = "查询当前用户可访问的菜单权限列表")
    public Result<List<AuthorityMenuVo>> getMenuPermissions() {
        return Result.success(authorityService.getMenuPermissions());
    }

    /**
     * 查询当前用户的资源权限信息
     *
     * @return 资源权限视图对象
     */
    @GetMapping("/resources")
    @Operation(summary = "查询资源权限信息", description = "查询当前用户的资源访问权限信息")
    public Result<AuthorityResourceVo> getResourcePermissions() {
        return Result.success(authorityService.getResourcePermissions());
    }

    /**
     * 查询当前用户的用户信息
     *
     * @return 用户信息对象
     */
    @GetMapping("/user")
    @Operation(summary = "查询当前用户信息", description = "查询当前登录用户的权限相关用户信息")
    public Result<AuthorityUserVo> getUser() {
        return Result.success(authorityService.getUser());
    }
}
