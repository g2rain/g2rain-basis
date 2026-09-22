package com.g2rain.basis.service.impl;

import com.g2rain.basis.dao.ApplicationDao;
import com.g2rain.basis.dao.ControlDomainDao;
import com.g2rain.basis.dao.OrganDao;
import com.g2rain.basis.dao.UserDao;
import com.g2rain.basis.dao.po.ApplicationPo;
import com.g2rain.basis.dao.po.ControlDomainPo;
import com.g2rain.basis.dao.po.OrganPo;
import com.g2rain.basis.dao.po.UserPo;
import com.g2rain.basis.dto.ApplicationAuthorizationActivateSelfRequest;
import com.g2rain.basis.dto.ApplicationAuthorizationDto;
import com.g2rain.basis.dto.ApplicationSelectDto;
import com.g2rain.basis.dto.ControlDomainSelectDto;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.enums.ControlDomainType;
import com.g2rain.basis.enums.OrganStatus;
import com.g2rain.basis.vo.ApplicationAuthorizationActivateSelfVo;
import com.g2rain.common.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ApplicationAuthorizationActivateSelfTest {

    private ApplicationAuthorizationServiceImpl service;
    private ApplicationDao applicationDao;
    private UserDao userDao;
    private OrganDao organDao;
    private ControlDomainDao controlDomainDao;

    @BeforeEach
    void setUp() {
        service = spy(new ApplicationAuthorizationServiceImpl());
        applicationDao = mock(ApplicationDao.class);
        userDao = mock(UserDao.class);
        organDao = mock(OrganDao.class);
        controlDomainDao = mock(ControlDomainDao.class);

        ReflectionTestUtils.setField(service, "applicationDao", applicationDao);
        ReflectionTestUtils.setField(service, "userDao", userDao);
        ReflectionTestUtils.setField(service, "organDao", organDao);
        ReflectionTestUtils.setField(service, "controlDomainDao", controlDomainDao);
    }

    @Test
    void activateSelf_rejectsNonAdmin() {
        stubApplicationAndOrgan();
        when(userDao.selectByIdWithoutIsolation(7L)).thenReturn(adminUser(false));

        ApplicationAuthorizationActivateSelfRequest request = new ApplicationAuthorizationActivateSelfRequest();
        request.setApplicationCode("app-a");
        request.setUserId(7L);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.activateSelf(request));
        assertEquals(BasisErrorCode.APPLICATION_SELF_ACTIVATE_ADMIN_REQUIRED.code(), ex.getErrorCode().code());
    }

    @Test
    void activateSelf_savesAllSelfDomains() {
        stubApplicationAndOrgan();
        when(userDao.selectByIdWithoutIsolation(7L)).thenReturn(adminUser(true));
        when(controlDomainDao.selectList(any(ControlDomainSelectDto.class)))
            .thenReturn(List.of(selfDomain(11L), selfDomain(12L)));
        doReturn(101L, 102L).when(service).save(any(ApplicationAuthorizationDto.class));

        ApplicationAuthorizationActivateSelfRequest request = new ApplicationAuthorizationActivateSelfRequest();
        request.setApplicationCode("app-a");
        request.setUserId(7L);

        ApplicationAuthorizationActivateSelfVo result = service.activateSelf(request);
        assertEquals(List.of(101L, 102L), result.getAuthorizationIds());

        ArgumentCaptor<ApplicationAuthorizationDto> captor =
            ArgumentCaptor.forClass(ApplicationAuthorizationDto.class);
        verify(service, times(2)).save(captor.capture());
        assertEquals(11L, captor.getAllValues().get(0).getControlDomainId());
        assertEquals(12L, captor.getAllValues().get(1).getControlDomainId());
    }

    @Test
    void activateSelf_withNoSelfDomains_succeeds() {
        stubApplicationAndOrgan();
        when(userDao.selectByIdWithoutIsolation(7L)).thenReturn(adminUser(true));
        when(controlDomainDao.selectList(any(ControlDomainSelectDto.class))).thenReturn(List.of());

        ApplicationAuthorizationActivateSelfRequest request = new ApplicationAuthorizationActivateSelfRequest();
        request.setApplicationCode("app-a");
        request.setUserId(7L);

        ApplicationAuthorizationActivateSelfVo result = service.activateSelf(request);
        assertTrue(result.getAuthorizationIds().isEmpty());
        verify(service, never()).save(any());
    }

    private void stubApplicationAndOrgan() {
        ApplicationPo application = new ApplicationPo();
        application.setId(88L);
        application.setApplicationCode("app-a");
        application.setApplicationName("开放应用");
        application.setDescription("应用说明");
        when(applicationDao.selectList(any(ApplicationSelectDto.class))).thenReturn(List.of(application));

        OrganPo organ = new OrganPo();
        organ.setId(100L);
        organ.setOrganName("租户甲");
        organ.setStatus(OrganStatus.ACTIVE.name());
        when(organDao.selectById(100L)).thenReturn(organ);
    }

    private static UserPo adminUser(boolean admin) {
        UserPo user = new UserPo();
        user.setId(7L);
        user.setOrganId(100L);
        user.setAdmin(admin);
        return user;
    }

    private static ControlDomainPo selfDomain(Long id) {
        ControlDomainPo domain = new ControlDomainPo();
        domain.setId(id);
        domain.setApplicationId(88L);
        domain.setControlDomainType(ControlDomainType.SELF.name());
        return domain;
    }
}
