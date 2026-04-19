package com.g2rain.basis.service.impl;

import com.g2rain.basis.converter.ApiEndpointConverter;
import com.g2rain.basis.converter.OrganConverter;
import com.g2rain.basis.converter.ResourceMenuConverter;
import com.g2rain.basis.converter.ResourcePageConverter;
import com.g2rain.basis.converter.ResourcePageElementConverter;
import com.g2rain.basis.converter.UserConverter;
import com.g2rain.basis.dao.ApiEndpointDao;
import com.g2rain.basis.dao.OrganDao;
import com.g2rain.basis.dao.ResourceMenuDao;
import com.g2rain.basis.dao.ResourcePageDao;
import com.g2rain.basis.dao.ResourcePageElementDao;
import com.g2rain.basis.dao.RoleControlUnitRelationDao;
import com.g2rain.basis.dao.po.AuthorityApiEndpointPo;
import com.g2rain.basis.dao.po.AuthorityPageElementPo;
import com.g2rain.basis.dao.po.CountRoleControlUnitPo;
import com.g2rain.basis.dao.po.ResourceMenuPo;
import com.g2rain.basis.dao.po.ResourcePagePo;
import com.g2rain.basis.dto.ApplicationSelectDto;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.service.ApplicationService;
import com.g2rain.basis.service.AuthorityService;
import com.g2rain.basis.service.PassportService;
import com.g2rain.basis.service.UserService;
import com.g2rain.basis.vo.ApplicationScopeVo;
import com.g2rain.basis.vo.ApplicationVo;
import com.g2rain.basis.vo.AuthorityApiEndpointVo;
import com.g2rain.basis.vo.AuthorityMenuVo;
import com.g2rain.basis.vo.AuthorityPageElementVo;
import com.g2rain.basis.vo.AuthorityPageVo;
import com.g2rain.basis.vo.AuthorityResourceVo;
import com.g2rain.basis.vo.AuthorityUserVo;
import com.g2rain.basis.vo.PassportVo;
import com.g2rain.basis.vo.UserVo;
import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.utils.Asserts;
import com.g2rain.common.utils.Collections;
import com.g2rain.common.web.PrincipalContextHolder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * AuthorityServiceImpl
 *
 * <p>权限服务实现类，用于获取当前用户可访问的菜单、页面、页面元素、 API 接口权限和用户信息。</p>
 * <p>包含逻辑：
 * <ul>
 *     <li>超级管理员获取全部权限</li>
 *     <li>普通用户根据应用作用域和用户权限获取对应资源</li>
 *     <li>默认应用（landing application）资源合并去重</li>
 *     <li>构建菜单树并排序</li>
 * </ul>
 * </p>
 *
 * @author Alpha
 */
@Slf4j
@Service
public class AuthorityServiceImpl implements AuthorityService {

    /**
     * 应用服务，用于获取应用信息和应用作用域
     */
    @Resource(name = "userServiceImpl")
    private UserService userService;

    /**
     * 应用服务，用于获取应用信息和应用作用域
     */
    @Resource(name = "passportServiceImpl")
    private PassportService passportService;

    /**
     * 应用服务，用于获取应用信息和应用作用域
     */
    @Resource(name = "applicationServiceImpl")
    private ApplicationService applicationService;

    /**
     * 菜单资源 DAO
     */
    @Resource(name = "resourceMenuDao")
    private ResourceMenuDao resourceMenuDao;

    /**
     * 页面资源 DAO
     */
    @Resource(name = "resourcePageDao")
    private ResourcePageDao resourcePageDao;

    /**
     * 页面元素资源 DAO
     */
    @Resource(name = "resourcePageElementDao")
    private ResourcePageElementDao resourcePageElementDao;

    @Resource(name = "roleControlUnitRelationDao")
    private RoleControlUnitRelationDao roleControlUnitRelationDao;

    /**
     * API 接口资源 DAO
     */
    @Resource(name = "apiEndpointDao")
    private ApiEndpointDao apiEndpointDao;

    /**
     * 机构 DAO
     */
    @Resource(name = "organDao")
    private OrganDao organDao;

    /**
     * 获取当前用户可访问的菜单权限列表。
     * <p>返回菜单树结构，已按菜单排序字段排序，每个菜单包含其子菜单。</p>
     *
     * @return 用户可访问菜单的树状列表
     */
    @Override
    public List<AuthorityMenuVo> getMenuPermissions() {
        // 获取当前请求归属的 `应用ID`(通过 `Token` 的应用编码转换 `应用ID`, 写入到 `ScopedValue`)
        Long applicationId = PrincipalContextHolder.getApplicationId();
        // 查询应用信息
        ApplicationVo application = applicationService.selectOne(applicationId);
        // 没有有效的应用, 直接返回
        Asserts.isTrue(Collections.isNotEmpty(application),
            SystemErrorCode.UNAUTHORIZED, applicationId
        );

        Boolean canIntegrate = application.getCanIntegrate();
        Boolean landing = application.getLanding();
        boolean isDefaultMain = Boolean.TRUE.equals(landing) && Boolean.TRUE.equals(canIntegrate);
        Long userId = PrincipalContextHolder.getUserId();

        // 如果不存在用户, 说明账号登录, 只返回当前应用的默认控制单元的菜单
        if (Objects.isNull(userId)) {
            // 检查当前应用是否为落地应用
            Asserts.isTrue(isDefaultMain, SystemErrorCode.UNAUTHORIZED, applicationId);
            // 返回当前 landing 应用的授权菜单
            List<ResourceMenuPo> menus = resourceMenuDao.listAuthorizedMenusWithLanding(applicationId);
            return buildMenuTree(menus);
        }

        // 如果入口应用不是 `默认应用`, 需要校验应用是否做过授权
        if (!isDefaultMain) {
            CountRoleControlUnitPo countRoleControlUnit = roleControlUnitRelationDao
                .countRoleControlUnits(userId);

            // 如果没有授权, 权限不足
            Asserts.greaterThan(countRoleControlUnit.getTotalControlUnitCount(), 0,
                SystemErrorCode.UNAUTHORIZED, userId
            );

            // 如果没有有效的权限, 请续费
            Asserts.greaterThan(countRoleControlUnit.getActiveControlUnitCount(), 0,
                BasisErrorCode.BUSINESS_CAPABILITY_DISABLED, userId
            );
        }

        // 获取用户可访问的应用集合
        List<ApplicationScopeVo> applicationScopes = applicationService.selectApplicationScope(
            userId, applicationId
        );
        Set<Long> ids = HashSet.newHashSet(applicationScopes.size() + 1);
        ids.add(application.getId());
        applicationScopes.forEach(obj -> ids.add(obj.getId()));
        // 获取用户可访问的菜单权限
        List<ResourceMenuPo> menus = resourceMenuDao.selectAuthorizedMenusWithUserId(userId, ids);
        if (isDefaultMain) {
            // 针对 默认控制单元, 再追加 landing 菜单(去重)
            List<ResourceMenuPo> landingMenus = resourceMenuDao.listAuthorizedMenusWithLanding(
                application.getId()
            );
            // 合并菜单
            mergeWithLanding(menus, landingMenus, ResourceMenuPo::getId);
        }

        // 构建菜单树
        return buildMenuTree(menus);
    }

    /**
     * 获取当前用户可访问的页面、页面元素和接口权限集合。
     *
     * @return AuthorityResourceVo 包含页面、元素和接口权限列表
     */
    @Override
    public AuthorityResourceVo getResourcePermissions() {
        Long userId = PrincipalContextHolder.getUserId();
        Long applicationId = PrincipalContextHolder.getApplicationId();
        boolean isDefaultMain = applicationService.hasLandingApplication(applicationId);

        // 构建页面权限
        List<AuthorityPageVo> pages = buildResourcePages(isDefaultMain, userId, applicationId).stream()
            .map(ResourcePageConverter.INSTANCE::po2authVo).toList();

        // 构建页面元素权限
        List<AuthorityPageElementVo> elements = buildResourcePageElements(isDefaultMain, userId, applicationId)
            .stream().map(ResourcePageElementConverter.INSTANCE::po2authVo).toList();

        // 构建接口地址权限
        List<AuthorityApiEndpointVo> apiEndpoints = buildResourceApiEndpoints(isDefaultMain, userId, applicationId)
            .stream().map(ApiEndpointConverter.INSTANCE::po2authVo).toList();

        // 构建权限集合
        return new AuthorityResourceVo(pages, elements, apiEndpoints);
    }

    /**
     * 获取指定用户可访问的接口权限列表。
     *
     * @param userId        用户 ID
     * @param applicationId 应用 ID
     * @return AuthorityApiEndpointVo 列表
     */
    @Override
    public List<AuthorityApiEndpointVo> getApiPermissions(Long userId, Long applicationId) {
        boolean isDefaultMain = applicationService.hasLandingApplication(applicationId);
        return buildResourceApiEndpoints(isDefaultMain, userId, applicationId).stream()
            .map(ApiEndpointConverter.INSTANCE::po2authVo).toList();
    }

    /**
     * 获取token对应的用户信息。
     *
     * @return 用户信息视图对象
     */
    @Override
    public AuthorityUserVo getUser() {
        Long userId = PrincipalContextHolder.getUserId();

        AuthorityUserVo authorityUserVo = null;
        Long passportId = null;
        if (userId != null && userId > 0) {
            // 查询 user
            UserVo userVo = Optional.ofNullable(userService.selectById(userId))
                .orElseThrow(() -> new BusinessException(BasisErrorCode.USER_NOT_EXISTS_ILLEGAL));

            authorityUserVo =
                UserConverter.INSTANCE.vo2authorityUserVo(userVo);

            // 查询 organ 信息
            Optional.ofNullable(organDao.selectById(userVo.getOrganId()))
                .map(OrganConverter.INSTANCE::po2vo)
                .ifPresent(authorityUserVo::setOrgan);
            passportId = userVo.getPassportId();
        } else {
            // user id 不存在， 创建空AuthorityUserVo对象
            passportId = PrincipalContextHolder.getPassportId();
            authorityUserVo = new AuthorityUserVo();
        }

        // 查询 passport
        PassportVo passportVo = passportService.selectById(passportId);
        authorityUserVo.setPassport(passportVo);

        return authorityUserVo;
    }

    /**
     * 构建菜单树，并对每一层按照菜单排序字段进行排序。
     *
     * @param menus 菜单列表
     * @return 排序后的菜单树
     */
    private List<AuthorityMenuVo> buildMenuTree(List<ResourceMenuPo> menus) {
        if (Collections.isEmpty(menus)) {
            return List.of();
        }

        // 构建应用映射（仅查询当前菜单涉及的应用）
        Set<Long> applicationIds = menus.stream()
            .map(ResourceMenuPo::getApplicationId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        ApplicationSelectDto appSelectDto = new ApplicationSelectDto();
        appSelectDto.setIds(applicationIds);
        Map<Long, ApplicationVo> id2app = applicationService.selectList(appSelectDto).stream()
            .collect(Collectors.toMap(ApplicationVo::getId, Function.identity(), (e1, e2) -> e1));

        // 扁平 VO 转换，顺序保持原有
        List<AuthorityMenuVo> flatMenus = menus.stream().map(menu -> {
            AuthorityMenuVo vo = ResourceMenuConverter.INSTANCE.po2authVo(menu);
            Optional.ofNullable(id2app.get(menu.getApplicationId())).ifPresent(app -> {
                vo.setApplicationCode(app.getApplicationCode());
                vo.setEndpointUrl(app.getEndpointUrl());
                vo.setContextPath(app.getContextPath());
            });
            return vo;
        }).toList();

        Map<Long, AuthorityMenuVo> id2menu = new HashMap<>();
        List<AuthorityMenuVo> roots = new ArrayList<>();

        // 先把所有菜单放入 Map
        for (AuthorityMenuVo menu : flatMenus) {
            id2menu.put(menu.getId(), menu);
        }

        // 遍历扁平列表，组装树
        for (AuthorityMenuVo menu : flatMenus) {
            // 根节点
            if (Objects.isNull(menu.getParentId())) {
                roots.add(menu);
                continue;
            }

            AuthorityMenuVo parent = id2menu.get(menu.getParentId());
            if (Objects.nonNull(parent)) {
                parent.getSubMenus().add(menu);
            }
        }

        // 非递归排序每一层
        Queue<List<AuthorityMenuVo>> queue = new LinkedList<>();
        queue.add(roots);
        while (!queue.isEmpty()) {
            List<AuthorityMenuVo> level = queue.poll();
            level.sort(Comparator.comparingInt(AuthorityMenuVo::getMenuSortOrder));
            for (AuthorityMenuVo m : level) {
                if (!m.getSubMenus().isEmpty()) {
                    queue.add(m.getSubMenus());
                }
            }
        }

        // 根节点也排序
        roots.sort(Comparator.comparingInt(AuthorityMenuVo::getMenuSortOrder));
        return roots;
    }

    /**
     * 构建用户可访问的页面列表，包括默认应用页面。
     *
     * @param userId        用户 ID
     * @param applicationId 应用 ID
     * @return ResourcePagePo 列表
     */
    private List<ResourcePagePo> buildResourcePages(boolean isDefaultMain, Long userId, Long applicationId) {
        List<ResourcePagePo> pages = new ArrayList<>();
        // 如果用户ID和 应用列表存在, 查询菜单权限
        if (Objects.nonNull(userId) && Objects.nonNull(applicationId)) {
            pages.addAll(resourcePageDao.selectAuthorizedPagesWithUserId(userId, applicationId));
        }

        // 针对 默认控制单元, 再追加 landing 页面(去重)
        if (isDefaultMain) {
            List<ResourcePagePo> landingPages = resourcePageDao.listAuthorizedPagesWithLanding(applicationId);
            mergeWithLanding(pages, landingPages, ResourcePagePo::getId);
        }

        return pages;
    }

    /**
     * 构建用户可访问的页面元素列表，包括默认应用元素。
     *
     * @param userId        用户 ID
     * @param applicationId 应用 ID
     * @return ResourcePageElementPo 列表
     */
    private List<AuthorityPageElementPo> buildResourcePageElements(boolean isDefaultMain, Long userId, Long applicationId) {
        List<AuthorityPageElementPo> pageElements = new ArrayList<>();
        // 如果用户ID和 应用列表存在, 查询菜单权限
        if (Objects.nonNull(userId) && Objects.nonNull(applicationId)) {
            pageElements.addAll(resourcePageElementDao.selectAuthorizedPageElementsWithUserId(userId, applicationId));
        }

        // 针对 默认控制单元, 再追加 landing 页面元素(去重)
        if (isDefaultMain) {
            List<AuthorityPageElementPo> landingPageElements = resourcePageElementDao.
                listAuthorizedPageElementsWithLanding(applicationId);
            mergeWithLanding(pageElements, landingPageElements, AuthorityPageElementPo::getId);
        }

        return pageElements;
    }

    /**
     * 构建用户可访问的 API 接口列表，包括默认应用接口。
     *
     * @param userId        用户 ID
     * @param applicationId 应用 ID
     * @return ApiEndpointPo 列表
     */
    private List<AuthorityApiEndpointPo> buildResourceApiEndpoints(boolean isDefaultMain, Long userId, Long applicationId) {
        List<AuthorityApiEndpointPo> apiEndpoints = new ArrayList<>();
        // 如果用户ID和 应用列表存在, 查询菜单权限
        if (Objects.nonNull(userId) && Objects.nonNull(applicationId)) {
            apiEndpoints.addAll(apiEndpointDao.selectAuthorizedApiEndpointsWithUserId(userId, applicationId));
        }

        // 针对 默认控制单元, 再追加 landing 接口(去重)
        if (isDefaultMain) {
            List<AuthorityApiEndpointPo> landingApiEndpoints = apiEndpointDao.
                listAuthorizedApiEndpointsWithLanding(applicationId);
            mergeWithLanding(apiEndpoints, landingApiEndpoints, AuthorityApiEndpointPo::getId);
        }

        return apiEndpoints;
    }

    /**
     * 将默认应用资源合并到用户资源列表中，并根据 ID 去重。
     *
     * @param list        当前用户资源列表
     * @param landingList 默认应用资源列表
     * @param idGetter    获取资源 ID 的函数
     * @param <T>         资源类型
     */
    private <T> void mergeWithLanding(List<T> list, List<T> landingList, Function<T, Long> idGetter) {
        if (Collections.isEmpty(landingList)) {
            return;
        }

        Set<Long> seen = list.stream().map(idGetter).collect(Collectors.toCollection(HashSet::new));
        landingList.stream().filter(e -> seen.add(idGetter.apply(e))).forEach(list::add);
    }
}
