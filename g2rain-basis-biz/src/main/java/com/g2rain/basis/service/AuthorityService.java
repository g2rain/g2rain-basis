package com.g2rain.basis.service;


import com.g2rain.basis.vo.AuthorityApiEndpointVo;
import com.g2rain.basis.vo.AuthorityMenuVo;
import com.g2rain.basis.vo.AuthorityResourceVo;
import com.g2rain.basis.vo.AuthorityUserVo;
import com.g2rain.basis.vo.BaseAuthorityApiVo;

import java.util.List;

/**
 * 权限服务接口
 * <p>
 * 提供获取菜单、资源及接口权限的方法。
 * </p>
 *
 * @author alpha
 * @since 2026/1/11
 */
public interface AuthorityService {

    /**
     * 获取当前用户的菜单权限列表。
     *
     * @return 菜单权限视图对象列表 {@link AuthorityMenuVo}
     */
    List<AuthorityMenuVo> getMenuPermissions();

    /**
     * 获取当前用户的资源权限。
     * 包含 页面、页面元素、接口地址
     *
     * @return 资源权限视图对象 {@link AuthorityResourceVo}
     */
    AuthorityResourceVo getResourcePermissions();

    /**
     * 获取指定用户在指定应用下的接口权限列表。
     *
     * @param userId        用户 ID
     * @param applicationId 应用 ID
     * @return 接口权限视图对象列表 {@link AuthorityApiEndpointVo}
     */
    List<BaseAuthorityApiVo> getApiPermissions(Long userId, List<Long> roleIds, Long applicationId);

    /**
     * 获取当前用户的信息。
     * 用户信息和账号信息
     *
     * @return 用户视图对象 {@link AuthorityUserVo}
     */
    AuthorityUserVo getUser();

    /**
     * 查询账号的接口权限集合
     *
     * @return 账号的接口权限集合
     */
    List<Long> getPassportApiPermissions();
}
