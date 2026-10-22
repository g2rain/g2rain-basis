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

class SessionPermSyncServiceTest {

    private SessionPermSyncService service;
    private GenericRedisHelper genericRedisHelper;
    private EventPublisherHub eventPublisherHub;
    private RoleControlUnitRelationDao roleControlUnitRelationDao;

    @BeforeEach
    void setUp() {
        service = new SessionPermSyncService();
        genericRedisHelper = mock(GenericRedisHelper.class);
        eventPublisherHub = mock(EventPublisherHub.class);
        roleControlUnitRelationDao = mock(RoleControlUnitRelationDao.class);
        ReflectionTestUtils.setField(service, "genericRedisHelper", genericRedisHelper);
        ReflectionTestUtils.setField(service, "eventPublisherHub", eventPublisherHub);
        ReflectionTestUtils.setField(service, "roleControlUnitRelationDao", roleControlUnitRelationDao);
    }

    @Test
    void notifyOrgan_shouldNotifyMemberAndAnonymous() {
        service.notifyOrgan(10001L);

        verify(genericRedisHelper).increment(BasisRedisKeyRule.MEMBER_PERM_VERSION.format("10001"), 1L);
        verify(genericRedisHelper).increment(BasisRedisKeyRule.ANONYMOUS_PERM_VERSION.format("10001"), 1L);
        verify(eventPublisherHub).sendDelete(
            eq(Constants.SYNC_OUTPUT_BINDING), eq(BasisSyncerEnum.MEMBER_PERM.name()), eq(10001L)
        );
        verify(eventPublisherHub).sendDelete(
            eq(Constants.SYNC_OUTPUT_BINDING), eq(BasisSyncerEnum.ANONYMOUS_PERM.name()), eq(10001L)
        );
    }

    @Test
    void notifyOrgan_withType_shouldOnlyNotifyThatType() {
        service.notifyOrgan(SessionType.ANONYMOUS, 10001L);

        verify(eventPublisherHub).sendDelete(
            Constants.SYNC_OUTPUT_BINDING, BasisSyncerEnum.ANONYMOUS_PERM.name(), 10001L
        );
        verify(eventPublisherHub, never()).sendDelete(
            Constants.SYNC_OUTPUT_BINDING, BasisSyncerEnum.MEMBER_PERM.name(), 10001L
        );
    }

    @Test
    void notifyOrgan_withUserType_shouldSkip() {
        service.notifyOrgan(SessionType.USER, 10001L);
        verify(eventPublisherHub, never()).sendDelete(any(), any(), any());
    }

    @Test
    void currentVersion_shouldReadTypedOrganKey() {
        when(genericRedisHelper.get(
            BasisRedisKeyRule.MEMBER_PERM_VERSION.format("10001"), Number.class
        )).thenReturn(15L);
        when(genericRedisHelper.get(
            BasisRedisKeyRule.ANONYMOUS_PERM_VERSION.format("10001"), Number.class
        )).thenReturn(16);
        assertEquals(15L, service.currentVersion(SessionType.MEMBER, 10001L));
        assertEquals(16L, service.currentVersion(SessionType.ANONYMOUS, 10001L));
    }

    @Test
    void currentVersion_shouldReturnZeroWhenMissingOrNotOrganScoped() {
        when(genericRedisHelper.get(
            BasisRedisKeyRule.MEMBER_PERM_VERSION.format("10001"), Number.class
        )).thenReturn(null);
        assertEquals(0L, service.currentVersion(SessionType.MEMBER, 10001L));
        assertEquals(0L, service.currentVersion(SessionType.USER, 10001L));
    }

    @Test
    void notifyIfOrganScoped_shouldSkipUserUnit() {
        ControlUnitPo unit = new ControlUnitPo();
        unit.setId(9L);
        unit.setSessionType(SessionType.USER.name());
        service.notifyIfOrganScoped(unit);
        verify(roleControlUnitRelationDao, never()).listOrganIdsByControlUnitId(anyLong());
        verify(eventPublisherHub, never()).sendDelete(any(), any(), any());
    }

    @Test
    void notifyIfOrganScoped_anonymousUnit_shouldFanOutAnonymousOnly() {
        ControlUnitPo unit = new ControlUnitPo();
        unit.setId(9L);
        unit.setSessionType(SessionType.ANONYMOUS.name());
        when(roleControlUnitRelationDao.listOrganIdsByControlUnitId(9L))
            .thenReturn(List.of(10001L, 10002L));

        service.notifyIfOrganScoped(unit);

        verify(eventPublisherHub).sendDelete(
            Constants.SYNC_OUTPUT_BINDING, BasisSyncerEnum.ANONYMOUS_PERM.name(), 10001L
        );
        verify(eventPublisherHub).sendDelete(
            Constants.SYNC_OUTPUT_BINDING, BasisSyncerEnum.ANONYMOUS_PERM.name(), 10002L
        );
        verify(eventPublisherHub, never()).sendDelete(
            any(), eq(BasisSyncerEnum.MEMBER_PERM.name()), any()
        );
    }

    @Test
    void notifyOrgansHavingControlUnit_member_shouldFanOut() {
        when(roleControlUnitRelationDao.listOrganIdsByControlUnitId(9L))
            .thenReturn(List.of(10001L, 10002L));

        service.notifyOrgansHavingControlUnit(9L, SessionType.MEMBER);

        verify(eventPublisherHub).sendDelete(
            Constants.SYNC_OUTPUT_BINDING, BasisSyncerEnum.MEMBER_PERM.name(), 10001L
        );
        verify(eventPublisherHub).sendDelete(
            Constants.SYNC_OUTPUT_BINDING, BasisSyncerEnum.MEMBER_PERM.name(), 10002L
        );
    }
}
