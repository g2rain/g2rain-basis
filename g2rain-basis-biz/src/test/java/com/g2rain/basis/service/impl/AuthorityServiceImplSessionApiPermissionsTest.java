package com.g2rain.basis.service.impl;

import com.g2rain.basis.dao.AnonymousEntryDao;
import com.g2rain.basis.dao.ResourceApiDao;
import com.g2rain.basis.dao.po.AnonymousEntryPo;
import com.g2rain.basis.dao.po.AuthorityApiEndpointPo;
import com.g2rain.basis.dto.AnonymousEntrySelectDto;
import com.g2rain.basis.service.SessionPermSyncService;
import com.g2rain.basis.vo.SessionApiPermissionVo;
import com.g2rain.common.enums.SessionType;
import com.g2rain.common.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthorityServiceImplSessionApiPermissionsTest {

    private AuthorityServiceImpl service;
    private ResourceApiDao resourceApiDao;
    private SessionPermSyncService sessionPermSyncService;
    private AnonymousEntryDao anonymousEntryDao;

    @BeforeEach
    void setUp() {
        service = new AuthorityServiceImpl();
        resourceApiDao = mock(ResourceApiDao.class);
        sessionPermSyncService = mock(SessionPermSyncService.class);
        ReflectionTestUtils.setField(service, "resourceApiDao", resourceApiDao);
        ReflectionTestUtils.setField(service, "sessionPermSyncService", sessionPermSyncService);
        anonymousEntryDao = mock(AnonymousEntryDao.class);
        ReflectionTestUtils.setField(service, "anonymousEntryDao", anonymousEntryDao);
    }

    @Test
    void getSessionApiPermissions_member_shouldRequireOrganId() {
        assertThrows(BusinessException.class,
            () -> service.getSessionApiPermissions(SessionType.MEMBER.name(), null));
        assertThrows(BusinessException.class,
            () -> service.getSessionApiPermissions(SessionType.MEMBER.name(), 0L));
    }

    @Test
    void getSessionApiPermissions_member_shouldReturnOrganScopedIdsAndVersion() {
        AuthorityApiEndpointPo api = new AuthorityApiEndpointPo();
        api.setId(101L);
        when(resourceApiDao.listAuthorizedApisBySessionTypeAndOrgan(SessionType.MEMBER.name(), 10001L))
            .thenReturn(List.of(api));
        when(sessionPermSyncService.currentVersion(SessionType.MEMBER, 10001L)).thenReturn(15L);

        SessionApiPermissionVo vo = service.getSessionApiPermissions(SessionType.MEMBER.name(), 10001L);
        assertEquals(SessionType.MEMBER.name(), vo.getSessionType());
        assertEquals(10001L, vo.getOrganId());
        assertEquals(15L, vo.getVersion());
        assertEquals(List.of(101L), vo.getApiIds());
        verify(resourceApiDao).listAuthorizedApisBySessionTypeAndOrgan(SessionType.MEMBER.name(), 10001L);
        assertEquals(null, vo.getEntryIds());
    }

    @Test
    void getSessionApiPermissions_anonymous_shouldRequireOrganId() {
        assertThrows(BusinessException.class,
            () -> service.getSessionApiPermissions(SessionType.ANONYMOUS.name(), null));
    }

    @Test
    void getSessionApiPermissions_anonymous_shouldReturnApiIdsAndActiveEntryIds() {
        AuthorityApiEndpointPo api = new AuthorityApiEndpointPo();
        api.setId(301L);
        when(resourceApiDao.listAuthorizedApisBySessionTypeAndOrgan(SessionType.ANONYMOUS.name(), 10001L))
            .thenReturn(List.of(api));
        AnonymousEntryPo entry = new AnonymousEntryPo();
        entry.setId(401L);
        when(anonymousEntryDao.selectListWithoutIsolation(argThat((AnonymousEntrySelectDto dto) ->
            Long.valueOf(10001L).equals(dto.getOrganId()) && "ACTIVE".equals(dto.getStatus()))))
            .thenReturn(List.of(entry));
        when(sessionPermSyncService.currentVersion(SessionType.ANONYMOUS, 10001L)).thenReturn(3L);

        SessionApiPermissionVo vo = service.getSessionApiPermissions(SessionType.ANONYMOUS.name(), 10001L);
        assertEquals(SessionType.ANONYMOUS.name(), vo.getSessionType());
        assertEquals(10001L, vo.getOrganId());
        assertEquals(3L, vo.getVersion());
        assertEquals(List.of(301L), vo.getApiIds());
        assertEquals(List.of(401L), vo.getEntryIds());
    }

    @Test
    void getSessionApiPermissions_passport_shouldUseCatalogWithoutOrgan() {
        AuthorityApiEndpointPo api = new AuthorityApiEndpointPo();
        api.setId(201L);
        when(resourceApiDao.listAuthorizedApisBySessionType(SessionType.PASSPORT.name()))
            .thenReturn(List.of(api));

        SessionApiPermissionVo vo = service.getSessionApiPermissions(SessionType.PASSPORT.name(), null);
        assertEquals(SessionType.PASSPORT.name(), vo.getSessionType());
        assertEquals(List.of(201L), vo.getApiIds());
        assertEquals(0L, vo.getVersion());
        verify(resourceApiDao).listAuthorizedApisBySessionType(SessionType.PASSPORT.name());
    }

    @Test
    void getSessionApiPermissions_invalidType_shouldFail() {
        assertThrows(BusinessException.class, () -> service.getSessionApiPermissions("NOPE", null));
    }
}
