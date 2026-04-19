package com.g2rain.basis.service.impl;


import com.g2rain.basis.service.OrganService;
import com.g2rain.common.web.PrincipalContextHolder;
import com.g2rain.data.isolation.DataScopeExaminer;
import com.g2rain.mybatis.pagination.model.PagingEscape;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * 基于本地组织服务实现的数据范围校验器。
 * <p>
 * 用于判断目标机构是否属于当前登录机构的组织层级范围内，
 * 常用于数据隔离、组织维度权限过滤等场景。
 * </p>
 *
 * @author alpha
 * @since 2026/4/15
 */
@Service(value = "localDataScopeExaminer")
public class LocalDataScopeExaminer implements DataScopeExaminer {

    /**
     * 机构服务，用于查询组织层级关系。
     *
     * <p>通过该服务判断两个机构之间是否存在上下级或归属关系。</p>
     *
     * <p>@Lazy 表示延迟初始化该依赖，避免在 Bean 创建阶段立即加载：
     * 常用于解决循环依赖或避免不必要的启动期初始化成本。</p>
     */
    @Lazy
    @Resource(name = "organServiceImpl")
    private OrganService organService;

    /**
     * 判断指定机构是否在当前登录机构的组织层级范围内。
     *
     * @param tenantId 目标机构 ID
     * @return {@code true} 表示在范围内，{@code false} 表示不在范围内
     */
    @Override
    public boolean isOrganInScope(Long tenantId) {
        Boolean bool = PagingEscape.run(() -> organService.checkHierarchyRelation(
            tenantId, PrincipalContextHolder.getOrganId()
        ));

        return Boolean.TRUE.equals(bool);
    }
}
