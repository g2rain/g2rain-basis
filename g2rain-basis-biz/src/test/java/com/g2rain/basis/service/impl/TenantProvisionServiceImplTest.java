package com.g2rain.basis.service.impl;

import com.g2rain.basis.client.TenantProvisionClient;
import com.g2rain.basis.client.dto.VerifyCreateOrganRequest;
import com.g2rain.basis.dao.PassportIdpBindingDao;
import com.g2rain.basis.dao.po.PassportIdpBindingPo;
import com.g2rain.basis.dto.TenantProvisionDto;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.service.IdpEnterpriseOrganService;
import com.g2rain.basis.service.OrganProvisionService;
import com.g2rain.basis.service.RoleService;
import com.g2rain.basis.service.UserRoleRelationService;
import com.g2rain.basis.service.UserService;
import com.g2rain.basis.vo.RoleVo;
import com.g2rain.basis.vo.UserVo;
import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.model.Result;
import com.g2rain.common.web.PrincipalContext;
import com.g2rain.common.web.PrincipalContextHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TenantProvisionServiceImplTest {

    private TenantProvisionServiceImpl service;
    private TenantProvisionClient tenantProvisionClient;
    private PassportIdpBindingDao passportIdpBindingDao;
    private OrganProvisionService organProvisionService;
    private IdpEnterpriseOrganService idpEnterpriseOrganService;

    @BeforeEach
    void setUp() {
        service = new TenantProvisionServiceImpl();
        tenantProvisionClient = mock(TenantProvisionClient.class);
        passportIdpBindingDao = mock(PassportIdpBindingDao.class);
        organProvisionService = mock(OrganProvisionService.class);
        idpEnterpriseOrganService = mock(IdpEnterpriseOrganService.class);

        UserService userService = mock(UserService.class);
        when(userService.saveWithoutIsolation(any())).thenReturn(200L);
        when(userService.checkUserExists(100L)).thenReturn(1L);
        UserVo userVo = new UserVo();
        userVo.setId(200L);
        when(userService.selectByIdWithoutIsolation(200L)).thenReturn(userVo);

        RoleService roleService = mock(RoleService.class);
        RoleVo role = new RoleVo();
        role.setId(300L);
        when(roleService.selectListWithoutIsolation(any())).thenReturn(List.of(role));

        UserRoleRelationService userRoleRelationService = mock(UserRoleRelationService.class);
        when(userRoleRelationService.save(any())).thenReturn(1L);

        ReflectionTestUtils.setField(service, "tenantProvisionClient", tenantProvisionClient);
        ReflectionTestUtils.setField(service, "passportIdpBindingDao", passportIdpBindingDao);
        ReflectionTestUtils.setField(service, "organProvisionService", organProvisionService);
        ReflectionTestUtils.setField(service, "idpEnterpriseOrganService", idpEnterpriseOrganService);
        ReflectionTestUtils.setField(service, "userService", userService);
        ReflectionTestUtils.setField(service, "roleService", roleService);
        ReflectionTestUtils.setField(service, "userRoleRelationService", userRoleRelationService);

        when(organProvisionService.createOrganWithoutIsolation(any())).thenReturn(100L);
    }

    private void runAsPassport(Runnable task) {
        PrincipalContext context = PrincipalContext.of();
        context.setPassportId(10001L);
        context.setName("Test User");
        PrincipalContextHolder.runWith(context, task);
    }

    private static TenantProvisionDto sampleDto() {
        TenantProvisionDto dto = new TenantProvisionDto();
        dto.setOrganName("Acme");
        dto.setOrganType("TENANT");
        dto.setRealName("Admin");
        return dto;
    }

    @Test
    void provisionAccount_withoutIdpBinding_shouldSkipIamVerify() {
        when(passportIdpBindingDao.selectList(any())).thenReturn(List.of());

        runAsPassport(() -> {
            UserVo result = service.provisionAccount(sampleDto());
            assertEquals(200L, result.getId());
        });

        verify(tenantProvisionClient, never()).verifyCreateOrgan(any());
        verify(organProvisionService).createOrganWithoutIsolation(any());
    }

    @Test
    void provisionAccount_withIdpBinding_shouldCallIamBeforeCreateOrgan() {
        PassportIdpBindingPo binding = new PassportIdpBindingPo();
        binding.setIdpType("CUSTOM_IDP");
        binding.setCorpId("corp-1");
        when(passportIdpBindingDao.selectList(any())).thenReturn(List.of(binding));
        when(tenantProvisionClient.verifyCreateOrgan(any(VerifyCreateOrganRequest.class)))
            .thenReturn(Result.success(null));

        runAsPassport(() -> service.provisionAccount(sampleDto()));

        verify(tenantProvisionClient).verifyCreateOrgan(any(VerifyCreateOrganRequest.class));
        verify(organProvisionService).createOrganWithoutIsolation(any());
        verify(idpEnterpriseOrganService).ensureEnterpriseOrganBound(
            eq(100L), eq("CUSTOM_IDP"), eq("corp-1"), eq(null), eq(true));
    }

    @Test
    void provisionAccount_whenIamFeignFails_shouldNotCreateOrgan() {
        PassportIdpBindingPo binding = new PassportIdpBindingPo();
        binding.setIdpType("DINGTALK");
        binding.setCorpId("corp-1");
        when(passportIdpBindingDao.selectList(any())).thenReturn(List.of(binding));
        when(tenantProvisionClient.verifyCreateOrgan(any()))
            .thenThrow(new RuntimeException("connection refused"));

        runAsPassport(() -> {
            BusinessException ex = assertThrows(BusinessException.class,
                () -> service.provisionAccount(sampleDto()));
            assertEquals(BasisErrorCode.TENANT_PROVISION_VERIFY_FAILED.code(), ex.getErrorCode());
        });

        verify(organProvisionService, never()).createOrganWithoutIsolation(any());
    }
}
