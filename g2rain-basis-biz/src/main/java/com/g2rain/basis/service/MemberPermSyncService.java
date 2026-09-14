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

/**
 * MEMBER 会话 API 权限同步：按 organId 失效 Gateway 缓存。
 */
@Service
public class MemberPermSyncService {

    @Resource
    private GenericRedisHelper genericRedisHelper;

    @Resource
    private EventPublisherHub eventPublisherHub;

    @Resource
    private RoleControlUnitRelationDao roleControlUnitRelationDao;

    /**
     * 读取某机构 MEMBER 权限版本；键不存在时返回 0。
     */
    public long currentVersion(Long organId) {
        if (Objects.isNull(organId) || organId <= 0) {
            return 0L;
        }
        Long version = genericRedisHelper.get(
            BasisRedisKeyRule.MEMBER_PERM_VERSION.format(String.valueOf(organId)),
            Long.class
        );
        return Objects.requireNonNullElse(version, 0L);
    }

    /**
     * 递增机构版本并广播 MEMBER_PERM 按 organ 失效。
     */
    public void notifyOrgan(Long organId) {
        if (Objects.isNull(organId) || organId <= 0) {
            return;
        }
        genericRedisHelper.increment(
            BasisRedisKeyRule.MEMBER_PERM_VERSION.format(String.valueOf(organId)),
            1L
        );
        eventPublisherHub.sendDelete(
            Constants.SYNC_OUTPUT_BINDING,
            BasisSyncerEnum.MEMBER_PERM.name(),
            organId
        );
    }

    /**
     * 批量按机构失效。
     */
    public void notifyOrgans(Collection<Long> organIds) {
        if (Collections.isEmpty(organIds)) {
            return;
        }
        for (Long organId : organIds) {
            notifyOrgan(organId);
        }
    }

    /**
     * 控制单元变更时，失效所有持有该单元开通事实的机构。
     */
    public void notifyOrgansHavingControlUnit(Long controlUnitId) {
        if (Objects.isNull(controlUnitId) || controlUnitId <= 0) {
            return;
        }
        List<Long> organIds = roleControlUnitRelationDao.listOrganIdsByControlUnitId(controlUnitId);
        notifyOrgans(organIds);
    }

    /**
     * 控制单元为 MEMBER 时通知受影响机构。
     */
    public void notifyIfMember(ControlUnitPo unit) {
        if (unit == null || Strings.isBlank(unit.getSessionType()) || Objects.isNull(unit.getId())) {
            return;
        }
        if (SessionType.MEMBER.name().equals(unit.getSessionType())) {
            notifyOrgansHavingControlUnit(unit.getId());
        }
    }

    /**
     * 控制单元会话类型变入或变出 MEMBER 时通知。
     */
    public void notifyIfMemberAffected(Long controlUnitId, String beforeSessionType, String afterSessionType) {
        boolean beforeMember = SessionType.MEMBER.name().equals(beforeSessionType);
        boolean afterMember = SessionType.MEMBER.name().equals(afterSessionType);
        if (beforeMember || afterMember) {
            notifyOrgansHavingControlUnit(controlUnitId);
        }
    }
}
