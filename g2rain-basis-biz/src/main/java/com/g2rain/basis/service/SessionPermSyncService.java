package com.g2rain.basis.service;

import com.g2rain.basis.dao.RoleControlUnitRelationDao;
import com.g2rain.basis.dao.po.ControlUnitPo;
import com.g2rain.basis.enums.BasisRedisKeyRule;
import com.g2rain.basis.enums.BasisSyncerEnum;
import com.g2rain.basis.utils.Constants;
import com.g2rain.common.enums.SessionType;
import com.g2rain.common.syncer.EventPublisherHub;
import com.g2rain.common.utils.Collections;
import com.g2rain.common.utils.Strings;
import com.g2rain.data.redis.GenericRedisHelper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 按机构生效的会话（MEMBER、ANONYMOUS）API 权限同步：按 organId 递增权限版本并失效 Gateway 缓存。
 */
@Service
public class SessionPermSyncService {

    /**
     * 权限按机构计算的会话类型。
     */
    private static final Set<SessionType> ORGAN_SCOPED = Set.of(SessionType.MEMBER, SessionType.ANONYMOUS);

    @Resource
    private GenericRedisHelper genericRedisHelper;

    @Resource
    private EventPublisherHub eventPublisherHub;

    @Resource
    private RoleControlUnitRelationDao roleControlUnitRelationDao;

    /**
     * 读取某机构指定会话类型的权限版本；键不存在时返回 0。
     * <p>
     * Redis {@code INCR} 写入后，反序列化常为 {@link Integer}，不能直接按 {@link Long} 强转。
     */
    public long currentVersion(SessionType type, Long organId) {
        if (!ORGAN_SCOPED.contains(type) || Objects.isNull(organId) || organId <= 0) {
            return 0L;
        }

        Number version = genericRedisHelper.get(versionKey(type, organId), Number.class);
        return Objects.isNull(version) ? 0L : version.longValue();
    }

    /**
     * 通知某机构所有按机构生效的会话类型（应用授权、角色控制单元等变化同时影响 MEMBER 与 ANONYMOUS）。
     */
    public void notifyOrgan(Long organId) {
        ORGAN_SCOPED.forEach(type -> notifyOrgan(type, organId));
    }

    /**
     * 通知某机构指定会话类型的权限变化。
     */
    public void notifyOrgan(SessionType type, Long organId) {
        if (!ORGAN_SCOPED.contains(type) || Objects.isNull(organId) || organId <= 0) {
            return;
        }

        genericRedisHelper.increment(versionKey(type, organId), 1L);
        eventPublisherHub.sendDelete(Constants.SYNC_OUTPUT_BINDING, syncer(type).name(), organId);
    }

    public void notifyOrgans(Collection<Long> organIds) {
        if (Collections.isEmpty(organIds)) {
            return;
        }
        organIds.forEach(this::notifyOrgan);
    }

    /**
     * 通知开通了该控制单元的所有机构。
     */
    public void notifyOrgansHavingControlUnit(Long controlUnitId, SessionType type) {
        if (!ORGAN_SCOPED.contains(type) || Objects.isNull(controlUnitId) || controlUnitId <= 0) {
            return;
        }

        List<Long> organIds = roleControlUnitRelationDao.listOrganIdsByControlUnitId(controlUnitId);
        if (Collections.isEmpty(organIds)) {
            return;
        }

        organIds.forEach(organId -> notifyOrgan(type, organId));
    }

    /**
     * 控制单元属于按机构生效的会话类型时，通知开通了它的机构。
     */
    public void notifyIfOrganScoped(ControlUnitPo unit) {
        if (Objects.isNull(unit) || Objects.isNull(unit.getId()) || Strings.isBlank(unit.getSessionType())) {
            return;
        }

        notifyOrgansHavingControlUnit(unit.getId(), SessionType.valueOf(unit.getSessionType()));
    }

    /**
     * 判断会话类型的权限是否按机构计算。
     */
    public static boolean isOrganScoped(String sessionType) {
        return Strings.isNotBlank(sessionType) && ORGAN_SCOPED.stream().anyMatch(type -> type.name().equals(sessionType));
    }

    private static String versionKey(SessionType type, Long organId) {
        BasisRedisKeyRule rule = switch (type) {
            case MEMBER -> BasisRedisKeyRule.MEMBER_PERM_VERSION;
            case ANONYMOUS -> BasisRedisKeyRule.ANONYMOUS_PERM_VERSION;
            default -> throw new IllegalArgumentException(type.name());
        };

        return rule.format(String.valueOf(organId));
    }

    private static BasisSyncerEnum syncer(SessionType type) {
        return switch (type) {
            case MEMBER -> BasisSyncerEnum.MEMBER_PERM;
            case ANONYMOUS -> BasisSyncerEnum.ANONYMOUS_PERM;
            default -> throw new IllegalArgumentException(type.name());
        };
    }
}
