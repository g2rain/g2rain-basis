package com.g2rain.basis.service.impl;

import com.g2rain.basis.dao.ResourceApiDao;
import com.g2rain.basis.dao.po.AuthorityApiEndpointPo;
import com.g2rain.basis.service.MemberPermSyncService;
import com.g2rain.basis.vo.SessionApiPermissionVo;
import com.g2rain.common.enums.SessionType;
import com.g2rain.common.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthorityServiceImplSessionApiPermissionsTest {

    private AuthorityServiceImpl service;
    private ResourceApiDao resourceApiDao;
    private MemberPermSyncService memberPermSyncService;

    @BeforeEach
    void setUp() {
        service = new AuthorityServiceImpl();
        resourceApiDao = mock(ResourceApiDao.class);
        memberPermSyncService = mock(MemberPermSyncService.class);
        ReflectionTestUtils.setField(service, "resourceApiDao", resourceApiDao);
        ReflectionTestUtils.setField(service, "memberPermSyncService", memberPermSyncService);
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
        when(memberPermSyncService.currentVersion(10001L)).thenReturn(15L);

        SessionApiPermissionVo vo = service.getSessionApiPermissions(SessionType.MEMBER.name(), 10001L);
        assertEquals(SessionType.MEMBER.name(), vo.getSessionType());
        assertEquals(10001L, vo.getOrganId());
        assertEquals(15L, vo.getVersion());
        assertEquals(List.of(101L), vo.getApiIds());
        verify(resourceApiDao).listAuthorizedApisBySessionTypeAndOrgan(SessionType.MEMBER.name(), 10001L);
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
