package com.g2rain.basis.service.impl;

import com.g2rain.basis.dto.ApplicationAuthorizationDto;
import com.g2rain.basis.dto.ControlUnitSelectDto;
import com.g2rain.basis.dto.OrganDto;
import com.g2rain.basis.dto.RoleDto;
import com.g2rain.basis.enums.ControlDomainScope;
import com.g2rain.basis.enums.ControlDomainType;
import com.g2rain.basis.enums.ControlUnitScope;
import com.g2rain.basis.enums.RoleType;
import com.g2rain.basis.model.RoleControlUnitRelation;
import com.g2rain.basis.service.ApplicationAuthorizationService;
import com.g2rain.basis.service.ControlDomainService;
import com.g2rain.basis.service.ControlUnitService;
import com.g2rain.basis.service.OrganService;
import com.g2rain.basis.service.RoleControlUnitRelationService;
import com.g2rain.basis.service.RoleService;
import com.g2rain.basis.vo.ControlDomainVo;
import com.g2rain.basis.vo.ControlUnitVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrganProvisionServiceImplTest {

    private OrganProvisionServiceImpl service;
    private ApplicationAuthorizationService applicationAuthorizationService;
    private ControlDomainService controlDomainService;
    private AtomicReference<ApplicationAuthorizationDto> savedAuth;
    private List<ControlDomainVo> landingDomains;

    @BeforeEach
    void setUp() {
        service = new OrganProvisionServiceImpl();
        savedAuth = new AtomicReference<>();
        landingDomains = new ArrayList<>();

        OrganService organService = mock(OrganService.class);
        when(organService.saveWithoutIsolation(any())).thenReturn(100L);

        RoleService roleService = mock(RoleService.class);
        when(roleService.saveWithoutIsolation(eq(RoleType.ADMIN), any(RoleDto.class))).thenReturn(200L);

        ControlUnitService controlUnitService = mock(ControlUnitService.class);
        when(controlUnitService.selectList(any(ControlUnitSelectDto.class))).thenAnswer(invocation -> {
            ControlUnitSelectDto selectDto = invocation.getArgument(0);
            if (Boolean.TRUE.equals(selectDto.getLanding())
                && ControlUnitScope.CUSTOMER.name().equals(selectDto.getControlUnitScope())) {
                ControlUnitVo unit = new ControlUnitVo();
                unit.setId(18L);
                return List.of(unit);
            }
            return List.of();
        });

        RoleControlUnitRelationService roleControlUnitRelationService = mock(RoleControlUnitRelationService.class);
        when(roleControlUnitRelationService.internalSave(any(RoleControlUnitRelation.class))).thenReturn(1);

        controlDomainService = mock(ControlDomainService.class);
        when(controlDomainService.selectList(any())).thenAnswer(invocation -> {
            Object arg = invocation.getArgument(0);
            if (arg instanceof com.g2rain.basis.dto.ControlDomainSelectDto selectDto
                && Boolean.TRUE.equals(selectDto.getLanding())
                && ControlDomainScope.CUSTOMER.name().equals(selectDto.getControlDomainScope())) {
                return landingDomains;
            }
            return List.of();
        });

        applicationAuthorizationService = mock(ApplicationAuthorizationService.class);
        when(applicationAuthorizationService.save(any(ApplicationAuthorizationDto.class))).thenAnswer(invocation -> {
            savedAuth.set(invocation.getArgument(0));
            return 1L;
        });

        ReflectionTestUtils.setField(service, "organService", organService);
        ReflectionTestUtils.setField(service, "roleService", roleService);
        ReflectionTestUtils.setField(service, "controlUnitService", controlUnitService);
        ReflectionTestUtils.setField(service, "roleControlUnitRelationService", roleControlUnitRelationService);
        ReflectionTestUtils.setField(service, "controlDomainService", controlDomainService);
        ReflectionTestUtils.setField(service, "applicationAuthorizationService", applicationAuthorizationService);
    }

    @Test
    void createOrganWithoutIsolation_shouldAuthorizeLandingCustomerControlDomains() {
        ControlDomainVo domain = new ControlDomainVo();
        domain.setId(24L);
        domain.setApplicationId(10L);
        domain.setControlDomainType(ControlDomainType.APPLICATION.name());
        domain.setControlDomainScope(ControlDomainScope.CUSTOMER.name());
        domain.setLanding(true);
        landingDomains.add(domain);

        long organId = service.createOrganWithoutIsolation(new OrganDto());

        assertEquals(100L, organId);
        ApplicationAuthorizationDto authDto = savedAuth.get();
        assertEquals(100L, authDto.getOrganId());
        assertEquals(10L, authDto.getApplicationId());
        assertEquals(24L, authDto.getControlDomainId());
        assertNull(authDto.getSubscriptionId());
        verify(applicationAuthorizationService).save(any(ApplicationAuthorizationDto.class));
    }

    @Test
    void createOrganWithoutIsolation_shouldSkipTradeLandingControlDomains() {
        ControlDomainVo domain = new ControlDomainVo();
        domain.setId(30L);
        domain.setApplicationId(10L);
        domain.setControlDomainType(ControlDomainType.TRADE.name());
        domain.setControlDomainScope(ControlDomainScope.CUSTOMER.name());
        domain.setLanding(true);
        landingDomains.add(domain);

        service.createOrganWithoutIsolation(new OrganDto());

        assertNull(savedAuth.get());
        verify(applicationAuthorizationService, never()).save(any(ApplicationAuthorizationDto.class));
    }

    @Test
    void createOrganWithoutIsolation_shouldSkipWhenNoLandingDomains() {
        service.createOrganWithoutIsolation(new OrganDto());
        assertNull(savedAuth.get());
        verify(applicationAuthorizationService, never()).save(any(ApplicationAuthorizationDto.class));
    }
}
