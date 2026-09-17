package com.g2rain.basis.service;

import com.g2rain.basis.dao.RoleControlUnitRelationDao;
import com.g2rain.basis.dao.po.ControlUnitPo;
import com.g2rain.basis.enums.BasisRedisKeyRule;
import com.g2rain.basis.enums.BasisSyncerEnum;
import com.g2rain.basis.utils.Constants;
import com.g2rain.common.enums.SessionType;
import com.g2rain.common.syncer.EventPublisherHub;
import com.g2rain.data.redis.GenericRedisHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MemberPermSyncServiceTest {

    private MemberPermSyncService service;
    private GenericRedisHelper genericRedisHelper;
    private EventPublisherHub eventPublisherHub;
    private RoleControlUnitRelationDao roleControlUnitRelationDao;

    @BeforeEach
    void setUp() {
        service = new MemberPermSyncService();
        genericRedisHelper = mock(GenericRedisHelper.class);
        eventPublisherHub = mock(EventPublisherHub.class);
        roleControlUnitRelationDao = mock(RoleControlUnitRelationDao.class);
        ReflectionTestUtils.setField(service, "genericRedisHelper", genericRedisHelper);
        ReflectionTestUtils.setField(service, "eventPublisherHub", eventPublisherHub);
        ReflectionTestUtils.setField(service, "roleControlUnitRelationDao", roleControlUnitRelationDao);
    }

    @Test
    void notifyOrgan_shouldIncrementAndSendDelete() {
        when(genericRedisHelper.increment(
            BasisRedisKeyRule.MEMBER_PERM_VERSION.format("10001"), 1L
        )).thenReturn(16L);

        service.notifyOrgan(10001L);

        verify(eventPublisherHub).sendDelete(
            eq(Constants.SYNC_OUTPUT_BINDING),
            eq(BasisSyncerEnum.MEMBER_PERM.name()),
            eq(10001L)
        );
    }

    @Test
    void currentVersion_shouldReadOrganKey() {
        when(genericRedisHelper.get(
            BasisRedisKeyRule.MEMBER_PERM_VERSION.format("10001"), Number.class
        )).thenReturn(15L);
        assertEquals(15L, service.currentVersion(10001L));
    }

    @Test
    void currentVersion_shouldAcceptIntegerFromRedis() {
        when(genericRedisHelper.get(
            BasisRedisKeyRule.MEMBER_PERM_VERSION.format("10001"), Number.class
        )).thenReturn(16);
        assertEquals(16L, service.currentVersion(10001L));
    }

    @Test
    void currentVersion_shouldReturnZeroWhenMissing() {
        when(genericRedisHelper.get(
            BasisRedisKeyRule.MEMBER_PERM_VERSION.format("10001"), Number.class
        )).thenReturn(null);
        assertEquals(0L, service.currentVersion(10001L));
    }

    @Test
    void notifyIfMember_shouldSkipNonMember() {
        ControlUnitPo unit = new ControlUnitPo();
        unit.setId(9L);
        unit.setSessionType(SessionType.USER.name());
        service.notifyIfMember(unit);
        verify(roleControlUnitRelationDao, never()).listOrganIdsByControlUnitId(anyLong());
        verify(eventPublisherHub, never()).sendDelete(any(), any(), any());
    }

    @Test
    void notifyOrgansHavingControlUnit_shouldFanOut() {
        when(roleControlUnitRelationDao.listOrganIdsByControlUnitId(9L))
            .thenReturn(List.of(10001L, 10002L));
        when(genericRedisHelper.increment(any(), eq(1L))).thenReturn(1L);

        service.notifyOrgansHavingControlUnit(9L);

        verify(eventPublisherHub).sendDelete(
            Constants.SYNC_OUTPUT_BINDING, BasisSyncerEnum.MEMBER_PERM.name(), 10001L
        );
        verify(eventPublisherHub).sendDelete(
            Constants.SYNC_OUTPUT_BINDING, BasisSyncerEnum.MEMBER_PERM.name(), 10002L
        );
    }
}
