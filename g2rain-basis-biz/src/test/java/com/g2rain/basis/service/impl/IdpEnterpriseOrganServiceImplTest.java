package com.g2rain.basis.service.impl;

import com.g2rain.basis.dao.IdpEnterpriseOrganDao;
import com.g2rain.basis.dao.po.IdpEnterpriseOrganPo;
import com.g2rain.basis.dto.IdpEnterpriseOrganResolveRequest;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.vo.IdpEnterpriseOrganResolveVo;
import com.g2rain.common.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IdpEnterpriseOrganServiceImplTest {

    @Test
    void resolveReturnsUniqueActiveMapping() {
        IdpEnterpriseOrganDao dao = mock(IdpEnterpriseOrganDao.class);
        IdpEnterpriseOrganPo entity = active("WECHAT_WORK", "ww-corp", "THIRD_PARTY", 10001L);
        when(dao.selectList(any())).thenReturn(List.of(entity));
        IdpEnterpriseOrganServiceImpl service = serviceWith(dao);

        IdpEnterpriseOrganResolveVo result = service.resolve(request("WECHAT_WORK", "ww-corp", "THIRD_PARTY"));

        assertEquals(10001L, result.getOrganId());
        assertEquals("WECHAT_WORK", result.getIdpType());
        assertEquals("ww-corp", result.getEnterpriseId());
        assertEquals("THIRD_PARTY", result.getBindMode());
        assertEquals("ACTIVE", result.getStatus());
    }

    @Test
    void resolveThrowsNotFoundWhenEmpty() {
        IdpEnterpriseOrganDao dao = mock(IdpEnterpriseOrganDao.class);
        when(dao.selectList(any())).thenReturn(List.of());
        IdpEnterpriseOrganServiceImpl service = serviceWith(dao);

        BusinessException ex = assertThrows(BusinessException.class,
            () -> service.resolve(request("WECHAT_WORK", "missing", null)));
        assertEquals(BasisErrorCode.IDP_ENTERPRISE_ORGAN_NOT_FOUND.code(), ex.getErrorCode());
    }

    @Test
    void resolveThrowsAmbiguousWhenMultipleActive() {
        IdpEnterpriseOrganDao dao = mock(IdpEnterpriseOrganDao.class);
        when(dao.selectList(any())).thenReturn(List.of(
            active("WECHAT_WORK", "ww-corp", "INTERNAL", 1L),
            active("WECHAT_WORK", "ww-corp", "THIRD_PARTY", 2L)
        ));
        IdpEnterpriseOrganServiceImpl service = serviceWith(dao);

        BusinessException ex = assertThrows(BusinessException.class,
            () -> service.resolve(request("WECHAT_WORK", "ww-corp", null)));
        assertEquals(BasisErrorCode.IDP_ENTERPRISE_ORGAN_AMBIGUOUS.code(), ex.getErrorCode());
    }

    private static IdpEnterpriseOrganResolveRequest request(
        String idpType, String enterpriseId, String bindMode) {
        IdpEnterpriseOrganResolveRequest request = new IdpEnterpriseOrganResolveRequest();
        request.setIdpType(idpType);
        request.setEnterpriseId(enterpriseId);
        request.setBindMode(bindMode);
        return request;
    }

    private static IdpEnterpriseOrganPo active(
        String idpType, String enterpriseId, String bindMode, Long organId) {
        IdpEnterpriseOrganPo entity = new IdpEnterpriseOrganPo();
        entity.setIdpType(idpType);
        entity.setEnterpriseId(enterpriseId);
        entity.setBindMode(bindMode);
        entity.setOrganId(organId);
        entity.setStatus("ACTIVE");
        return entity;
    }

    private static IdpEnterpriseOrganServiceImpl serviceWith(IdpEnterpriseOrganDao dao) {
        IdpEnterpriseOrganServiceImpl service = new IdpEnterpriseOrganServiceImpl();
        ReflectionTestUtils.setField(service, "idpEnterpriseOrganDao", dao);
        return service;
    }
}
