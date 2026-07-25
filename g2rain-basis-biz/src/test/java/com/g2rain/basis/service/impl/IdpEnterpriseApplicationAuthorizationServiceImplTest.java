package com.g2rain.basis.service.impl;

import com.g2rain.basis.dao.IdpEnterpriseApplicationAuthorizationDao;
import com.g2rain.basis.dao.po.IdpEnterpriseApplicationAuthorizationPo;
import com.g2rain.basis.dto.IdpEnterpriseApplicationAuthorizationRevokeDto;
import com.g2rain.basis.dto.IdpEnterpriseApplicationAuthorizationUpsertDto;
import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.id.IdGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IdpEnterpriseApplicationAuthorizationServiceImplTest {

    @Test
    void activeAuthorizationRequiresAgentId() {
        IdpEnterpriseApplicationAuthorizationDao dao =
            mock(IdpEnterpriseApplicationAuthorizationDao.class);
        when(dao.selectList(any())).thenReturn(List.of());
        IdpEnterpriseApplicationAuthorizationServiceImpl service =
            serviceWith(dao);
        IdpEnterpriseApplicationAuthorizationUpsertDto dto = upsert("ACTIVE");

        assertThrows(BusinessException.class, () -> service.upsert(dto));
    }

    @Test
    void pendingAuthorizationAllowsMissingAgentId() {
        IdpEnterpriseApplicationAuthorizationDao dao =
            mock(IdpEnterpriseApplicationAuthorizationDao.class);
        when(dao.selectList(any())).thenReturn(List.of());
        IdpEnterpriseApplicationAuthorizationServiceImpl service =
            serviceWith(dao);
        IdGenerator idGenerator = mock(IdGenerator.class);
        when(idGenerator.generateId()).thenReturn(100L);
        when(dao.insert(any())).thenReturn(1);
        service.setIdGenerator(idGenerator);

        assertEquals(100L, service.upsert(upsert("PENDING")));
    }

    @Test
    void revokeClearsCredentialsAndRetainsAgentId() {
        IdpEnterpriseApplicationAuthorizationDao dao =
            mock(IdpEnterpriseApplicationAuthorizationDao.class);
        IdpEnterpriseApplicationAuthorizationPo entity =
            new IdpEnterpriseApplicationAuthorizationPo();
        entity.setId(1L);
        entity.setVersion(0);
        entity.setIdpType("WECHAT_WORK");
        entity.setBindMode("THIRD_PARTY");
        entity.setIdpApplicationCode("suite");
        entity.setEnterpriseId("corp");
        entity.setInstalledApplicationId("agent");
        entity.setAuthorizationStatus("ACTIVE");
        entity.setCredentialCiphertext("cipher");
        entity.setCredentialKeyId("key");
        when(dao.selectList(any())).thenReturn(List.of(entity));
        when(dao.updateByVersion(any())).thenAnswer(invocation -> {
            IdpEnterpriseApplicationAuthorizationPo updated =
                invocation.getArgument(0);
            assertEquals("REVOKED", updated.getAuthorizationStatus());
            assertEquals("agent", updated.getInstalledApplicationId());
            assertNull(updated.getCredentialCiphertext());
            assertNull(updated.getCredentialKeyId());
            return 1;
        });
        IdpEnterpriseApplicationAuthorizationServiceImpl service =
            serviceWith(dao);
        IdpEnterpriseApplicationAuthorizationRevokeDto dto =
            new IdpEnterpriseApplicationAuthorizationRevokeDto();
        dto.setIdpType("WECHAT_WORK");
        dto.setIdpApplicationCode("suite");
        dto.setEnterpriseId("corp");

        assertEquals(1, service.revoke(dto));
    }

    private static IdpEnterpriseApplicationAuthorizationServiceImpl serviceWith(
        IdpEnterpriseApplicationAuthorizationDao dao) {
        IdpEnterpriseApplicationAuthorizationServiceImpl service =
            new IdpEnterpriseApplicationAuthorizationServiceImpl();
        ReflectionTestUtils.setField(service, "dao", dao);
        return service;
    }

    private static IdpEnterpriseApplicationAuthorizationUpsertDto upsert(
        String status) {
        IdpEnterpriseApplicationAuthorizationUpsertDto dto =
            new IdpEnterpriseApplicationAuthorizationUpsertDto();
        dto.setIdpType("WECHAT_WORK");
        dto.setBindMode("THIRD_PARTY");
        dto.setIdpApplicationCode("suite");
        dto.setEnterpriseId("corp");
        dto.setAuthorizationStatus(status);
        return dto;
    }
}
