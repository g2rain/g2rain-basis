package com.g2rain.basis.service.impl;

import com.g2rain.basis.dao.ApplicationAuthorizationDao;
import com.g2rain.basis.dao.po.ApplicationAuthorizationPo;
import com.g2rain.basis.dto.UpdateStatusDto;
import com.g2rain.basis.enums.AuthorizationStatus;
import com.g2rain.basis.service.SessionPermSyncService;
import com.g2rain.basis.service.RoleControlUnitRelationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ApplicationAuthorizationServiceImplSessionPermNotifyTest {

    private ApplicationAuthorizationServiceImpl service;
    private ApplicationAuthorizationDao applicationAuthorizationDao;
    private RoleControlUnitRelationService roleControlUnitRelationService;
    private SessionPermSyncService sessionPermSyncService;

    @BeforeEach
    void setUp() {
        service = new ApplicationAuthorizationServiceImpl();
        applicationAuthorizationDao = mock(ApplicationAuthorizationDao.class);
        roleControlUnitRelationService = mock(RoleControlUnitRelationService.class);
        sessionPermSyncService = mock(SessionPermSyncService.class);
        ReflectionTestUtils.setField(service, "applicationAuthorizationDao", applicationAuthorizationDao);
        ReflectionTestUtils.setField(service, "roleControlUnitRelationService", roleControlUnitRelationService);
        ReflectionTestUtils.setField(service, "sessionPermSyncService", sessionPermSyncService);
    }

    @Test
    void updateStatus_shouldNotifyMemberPermForOrgan() {
        ApplicationAuthorizationPo existing = new ApplicationAuthorizationPo();
        existing.setId(55L);
        existing.setOrganId(10001L);
        existing.setStatus(AuthorizationStatus.ACTIVATED.name());
        when(applicationAuthorizationDao.selectById(55L)).thenReturn(existing);
        when(applicationAuthorizationDao.update(org.mockito.ArgumentMatchers.any())).thenReturn(1);
        when(roleControlUnitRelationService.changeStatus(10001L, 55L, AuthorizationStatus.DEACTIVATED.name()))
            .thenReturn(2);

        UpdateStatusDto dto = new UpdateStatusDto();
        dto.setStatus(AuthorizationStatus.DEACTIVATED.name());
        service.updateStatus(55L, dto);

        verify(sessionPermSyncService).notifyOrgan(eq(10001L));
    }
}
