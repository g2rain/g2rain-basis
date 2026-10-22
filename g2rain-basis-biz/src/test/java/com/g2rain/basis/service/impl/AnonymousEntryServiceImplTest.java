package com.g2rain.basis.service.impl;

import com.g2rain.basis.dao.AnonymousEntryDao;
import com.g2rain.basis.dao.ApplicationAuthorizationDao;
import com.g2rain.basis.dao.OrganDao;
import com.g2rain.basis.dao.po.AnonymousEntryPo;
import com.g2rain.basis.dao.po.OrganPo;
import com.g2rain.basis.dto.AnonymousEntryDto;
import com.g2rain.basis.dto.UpdateStatusDto;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.service.SessionPermSyncService;
import com.g2rain.basis.vo.AnonymousEntryVo;
import com.g2rain.common.enums.OrganType;
import com.g2rain.common.enums.SessionType;
import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.id.IdGenerator;
import com.g2rain.common.web.PrincipalContext;
import com.g2rain.common.web.PrincipalContextHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AnonymousEntryServiceImplTest {

    private static final Long ORGAN_ID = 100L;
    private static final Long APP_ID = 200L;

    private AnonymousEntryServiceImpl service;
    private AnonymousEntryDao anonymousEntryDao;
    private OrganDao organDao;
    private ApplicationAuthorizationDao applicationAuthorizationDao;
    private SessionPermSyncService sessionPermSyncService;

    @BeforeEach
    void setUp() {
        service = new AnonymousEntryServiceImpl();
        anonymousEntryDao = mock(AnonymousEntryDao.class);
        organDao = mock(OrganDao.class);
        applicationAuthorizationDao = mock(ApplicationAuthorizationDao.class);
        sessionPermSyncService = mock(SessionPermSyncService.class);
        IdGenerator idGenerator = mock(IdGenerator.class);
        when(idGenerator.generateId()).thenReturn(1L);
        service.setIdGenerator(idGenerator);
        ReflectionTestUtils.setField(service, "anonymousEntryDao", anonymousEntryDao);
        ReflectionTestUtils.setField(service, "organDao", organDao);
        ReflectionTestUtils.setField(service, "applicationAuthorizationDao", applicationAuthorizationDao);
        ReflectionTestUtils.setField(service, "sessionPermSyncService", sessionPermSyncService);
    }

    @Test
    void save_create_shouldGenerateKeyAndForceActive() {
        givenOpenOrgan(OrganType.TENANT);
        when(anonymousEntryDao.insert(any())).thenReturn(1);

        runAs(true, () -> service.save(newDto()));

        ArgumentCaptor<AnonymousEntryPo> captor = ArgumentCaptor.forClass(AnonymousEntryPo.class);
        verify(anonymousEntryDao).insert(captor.capture());
        AnonymousEntryPo saved = captor.getValue();
        assertTrue(saved.getEntryKey().matches("ek_[0-9A-Za-z]{22}"));
        assertEquals("ACTIVE", saved.getStatus());
    }

    @Test
    void save_create_byTenant_shouldBindOwnOrgan() {
        givenOpenOrgan(OrganType.TENANT);
        when(anonymousEntryDao.insert(any())).thenReturn(1);
        AnonymousEntryDto dto = newDto();
        dto.setOrganId(999L);

        runAs(false, () -> service.save(dto));

        ArgumentCaptor<AnonymousEntryPo> captor = ArgumentCaptor.forClass(AnonymousEntryPo.class);
        verify(anonymousEntryDao).insert(captor.capture());
        assertEquals(ORGAN_ID, captor.getValue().getOrganId());
    }

    @Test
    void save_create_forAdminOrgan_shouldReject() {
        givenOpenOrgan(OrganType.COMPANY);
        runAs(true, () -> assertThrows(BusinessException.class, () -> service.save(newDto())));
        verify(anonymousEntryDao, never()).insert(any());
    }

    @Test
    void save_create_withoutAuthorization_shouldReject() {
        givenOpenOrgan(OrganType.TENANT);
        when(applicationAuthorizationDao.checkApplicationAuthorizationExists(any())).thenReturn(0L);
        runAs(true, () -> {
            BusinessException e = assertThrows(BusinessException.class, () -> service.save(newDto()));
            assertEquals(BasisErrorCode.APPLICATION_AUTHORIZATION_MISSING.code(), e.getErrorCode());
        });
    }

    @Test
    void save_update_shouldOnlyChangeName() {
        AnonymousEntryPo existing = entry("ACTIVE");
        when(anonymousEntryDao.selectById(5L)).thenReturn(existing);
        when(anonymousEntryDao.update(any())).thenReturn(1);
        AnonymousEntryDto dto = newDto();
        dto.setId(5L);
        dto.setEntryName("官网");

        runAs(true, () -> service.save(dto));

        ArgumentCaptor<AnonymousEntryPo> captor = ArgumentCaptor.forClass(AnonymousEntryPo.class);
        verify(anonymousEntryDao).update(captor.capture());
        AnonymousEntryPo update = captor.getValue();
        assertEquals("官网", update.getEntryName());
        assertNull(update.getOrganId());
        assertNull(update.getApplicationId());
        assertNull(update.getEntryKey());
        assertNull(update.getStatus());
    }

    @Test
    void updateStatus_shouldNotifyAnonymousPerm() {
        when(anonymousEntryDao.selectById(5L)).thenReturn(entry("ACTIVE"));
        when(anonymousEntryDao.update(any())).thenReturn(1);
        UpdateStatusDto dto = new UpdateStatusDto();
        dto.setStatus("INACTIVE");

        service.updateStatus(5L, dto);

        verify(sessionPermSyncService).notifyOrgan(SessionType.ANONYMOUS, ORGAN_ID);
    }

    @Test
    void updateStatus_invalidStatus_shouldReject() {
        UpdateStatusDto dto = new UpdateStatusDto();
        dto.setStatus("ENABLED");
        assertThrows(BusinessException.class, () -> service.updateStatus(5L, dto));
        verify(anonymousEntryDao, never()).update(any());
    }

    @Test
    void delete_shouldNotifyAnonymousPerm() {
        when(anonymousEntryDao.selectById(5L)).thenReturn(entry("ACTIVE"));
        when(anonymousEntryDao.delete(5L)).thenReturn(1);

        service.delete(5L);

        verify(sessionPermSyncService).notifyOrgan(SessionType.ANONYMOUS, ORGAN_ID);
    }

    @Test
    void resolve_byKey_shouldReturnActiveEntryOfApplication() {
        when(anonymousEntryDao.selectListWithoutIsolation(any())).thenReturn(List.of(entry("ACTIVE")));
        AnonymousEntryVo vo = service.resolve("ek_x", APP_ID);
        assertEquals(5L, vo.getId());
    }

    @Test
    void resolve_inactiveOrMissingOrOtherApplication_shouldBeUnavailable() {
        when(anonymousEntryDao.selectListWithoutIsolation(any())).thenReturn(List.of(entry("INACTIVE")));
        assertUnavailable(() -> service.resolve("ek_x", APP_ID));

        when(anonymousEntryDao.selectListWithoutIsolation(any())).thenReturn(List.of());
        assertUnavailable(() -> service.resolve("ek_x", APP_ID));

        when(anonymousEntryDao.selectByIdWithoutIsolation(5L)).thenReturn(entry("ACTIVE"));
        assertUnavailable(() -> service.resolve(5L, 999L));

        assertUnavailable(() -> service.resolve(" ", APP_ID));
    }

    private void assertUnavailable(Runnable action) {
        BusinessException e = assertThrows(BusinessException.class, action::run);
        assertEquals(BasisErrorCode.ANONYMOUS_ENTRY_UNAVAILABLE.code(), e.getErrorCode());
    }

    private void givenOpenOrgan(OrganType type) {
        OrganPo organ = new OrganPo();
        organ.setId(ORGAN_ID);
        organ.setStatus("ACTIVE");
        organ.setOrganType(type.name());
        when(organDao.selectById(ORGAN_ID)).thenReturn(organ);
        when(applicationAuthorizationDao.checkApplicationAuthorizationExists(any())).thenReturn(1L);
    }

    private static AnonymousEntryDto newDto() {
        AnonymousEntryDto dto = new AnonymousEntryDto();
        dto.setOrganId(ORGAN_ID);
        dto.setApplicationId(APP_ID);
        dto.setEntryName("官网客服");
        return dto;
    }

    private static AnonymousEntryPo entry(String status) {
        AnonymousEntryPo po = new AnonymousEntryPo();
        po.setId(5L);
        po.setOrganId(ORGAN_ID);
        po.setApplicationId(APP_ID);
        po.setEntryKey("ek_x");
        po.setStatus(status);
        return po;
    }

    private static void runAs(boolean adminCompany, Runnable action) {
        PrincipalContext context = PrincipalContext.of();
        // applicationId 为空会被视为后端调用（isBackEnd），这里模拟经网关的前端请求
        context.setApplicationId(APP_ID);
        context.setOrganId(ORGAN_ID);
        context.setUserId(1001L);
        context.setAdminCompany(adminCompany);
        PrincipalContextHolder.runWith(context, action);
    }
}
