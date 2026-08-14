package com.g2rain.basis.service.impl;

import com.g2rain.basis.dao.ApplicationAuthorizationDao;
import com.g2rain.basis.dao.ApplicationDao;
import com.g2rain.basis.dao.ControlDomainControlUnitRelationDao;
import com.g2rain.basis.dao.ControlDomainDao;
import com.g2rain.basis.dao.po.ApplicationPo;
import com.g2rain.basis.dao.po.ControlDomainPo;
import com.g2rain.basis.dto.ControlDomainControlUnitRelationSelectDto;
import com.g2rain.basis.dto.ControlDomainDto;
import com.g2rain.basis.dto.ControlDomainSelectDto;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.enums.ControlDomainScope;
import com.g2rain.basis.enums.ControlDomainType;
import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.id.IdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ControlDomainServiceImplTest {

    private ControlDomainServiceImpl service;
    private ControlDomainDao controlDomainDao;
    private ApplicationDao applicationDao;
    private ControlDomainControlUnitRelationDao relationDao;
    private List<ControlDomainPo> landingDomains;

    @BeforeEach
    void setUp() {
        service = new ControlDomainServiceImpl();
        controlDomainDao = mock(ControlDomainDao.class);
        applicationDao = mock(ApplicationDao.class);
        relationDao = mock(ControlDomainControlUnitRelationDao.class);
        landingDomains = new ArrayList<>();

        ReflectionTestUtils.setField(service, "controlDomainDao", controlDomainDao);
        ReflectionTestUtils.setField(service, "applicationDao", applicationDao);
        ReflectionTestUtils.setField(service, "applicationAuthorizationDao", mock(ApplicationAuthorizationDao.class));
        ReflectionTestUtils.setField(service, "controlDomainControlUnitRelationDao", relationDao);

        IdGenerator idGenerator = mock(IdGenerator.class);
        when(idGenerator.generateId()).thenReturn(100L);
        service.setIdGenerator(idGenerator);

        when(applicationDao.selectById(any())).thenReturn(new ApplicationPo());
        when(controlDomainDao.selectList(any(ControlDomainSelectDto.class))).thenAnswer(invocation -> {
            ControlDomainSelectDto selectDto = invocation.getArgument(0);
            if (Boolean.TRUE.equals(selectDto.getLanding())) {
                return landingDomains;
            }
            return List.of();
        });
        when(controlDomainDao.insert(any())).thenReturn(1);
        when(controlDomainDao.update(any())).thenReturn(1);
    }

    @Test
    void save_shouldRejectLandingTradeControlDomain() {
        ControlDomainDto dto = landingDto();
        dto.setControlDomainType(ControlDomainType.TRADE.name());

        BusinessException exception = assertThrows(BusinessException.class, () -> service.save(dto));
        assertEquals(BasisErrorCode.LANDING_CONTROL_DOMAIN_TRADE_ILLEGAL.code(), exception.getErrorCode());
    }

    @Test
    void save_shouldRejectLandingOperationScope() {
        ControlDomainDto dto = landingDto();
        dto.setControlDomainScope(ControlDomainScope.OPERATION.name());

        BusinessException exception = assertThrows(BusinessException.class, () -> service.save(dto));
        assertEquals(BasisErrorCode.LANDING_CONTROL_DOMAIN_SCOPE_ILLEGAL.code(), exception.getErrorCode());
    }

    @Test
    void save_shouldRejectDuplicateLandingInSameApplication() {
        ControlDomainPo existing = new ControlDomainPo();
        existing.setId(99L);
        existing.setApplicationId(10L);
        existing.setLanding(true);
        landingDomains.add(existing);

        BusinessException exception = assertThrows(BusinessException.class, () -> service.save(landingDto()));
        assertEquals(BasisErrorCode.PARAM_ALREADY_EXISTS.code(), exception.getErrorCode());
    }

    @Test
    void save_shouldRejectLandingUpdateWithoutControlUnits() {
        ControlDomainDto dto = landingDto();
        dto.setId(24L);
        when(relationDao.checkControlDomainControlUnitExists(any(ControlDomainControlUnitRelationSelectDto.class)))
            .thenReturn(0L);

        BusinessException exception = assertThrows(BusinessException.class, () -> service.save(dto));
        assertEquals(BasisErrorCode.LANDING_CONTROL_DOMAIN_WITHOUT_UNITS.code(), exception.getErrorCode());
    }

    @Test
    void save_shouldAllowLandingUpdateWhenControlUnitsExist() {
        ControlDomainDto dto = landingDto();
        dto.setId(24L);
        when(relationDao.checkControlDomainControlUnitExists(any(ControlDomainControlUnitRelationSelectDto.class)))
            .thenReturn(1L);

        assertEquals(24L, service.save(dto));
    }

    @Test
    void save_shouldAllowLandingInsertWithoutControlUnits() {
        assertEquals(100L, service.save(landingDto()));
    }

    @Test
    void delete_shouldRejectLandingControlDomain() {
        ControlDomainPo domain = new ControlDomainPo();
        domain.setId(24L);
        domain.setLanding(true);
        when(controlDomainDao.selectById(24L)).thenReturn(domain);

        BusinessException exception = assertThrows(BusinessException.class, () -> service.delete(24L));
        assertEquals(BasisErrorCode.DEL_LANDING_CONTROL_DOMAIN_ILLEGAL.code(), exception.getErrorCode());
    }

    private ControlDomainDto landingDto() {
        ControlDomainDto dto = new ControlDomainDto();
        dto.setApplicationId(10L);
        dto.setControlDomainName("部门管理平台交付");
        dto.setControlDomainType(ControlDomainType.APPLICATION.name());
        dto.setControlDomainScope(ControlDomainScope.CUSTOMER.name());
        dto.setLanding(true);
        return dto;
    }
}
