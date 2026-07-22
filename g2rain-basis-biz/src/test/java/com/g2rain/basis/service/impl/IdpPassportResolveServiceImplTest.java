package com.g2rain.basis.service.impl;

import com.g2rain.basis.dao.PassportIdpBindingDao;
import com.g2rain.basis.dao.po.PassportIdpBindingPo;
import com.g2rain.basis.dto.PassportIdpBindingSelectDto;
import com.g2rain.basis.idp.resolve.dto.IdpPassportResolveRequest;
import com.g2rain.basis.vo.UserVo;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class IdpPassportResolveServiceImplTest {

    @Test
    void buildPrimaryBindingQuery_shouldUseUnionIdKeys() {
        IdpPassportResolveRequest request = new IdpPassportResolveRequest();
        request.setIdpType("DINGTALK");
        request.setIdpSubject(" union-1 ");
        request.setIdpApplicationCode(" client-1 ");

        PassportIdpBindingSelectDto query = IdpPassportResolveServiceImpl.buildPrimaryBindingQuery(request);

        assertEquals("DINGTALK", query.getIdpType());
        assertEquals("union-1", query.getIdpSubject());
        assertEquals("client-1", query.getIdpApplicationCode());
        assertNull(query.getIdpUserId());
    }

    @Test
    void buildFallbackBindingQuery_shouldUseIdpUserId() {
        IdpPassportResolveRequest request = new IdpPassportResolveRequest();
        request.setIdpType("DINGTALK");
        request.setIdpSubject("union-1");
        request.setIdpApplicationCode("client-1");
        request.setIdpUserId(" userid-1 ");

        PassportIdpBindingSelectDto query = IdpPassportResolveServiceImpl.buildFallbackBindingQuery(request);

        assertEquals("DINGTALK", query.getIdpType());
        assertEquals("userid-1", query.getIdpUserId());
        assertEquals("client-1", query.getIdpApplicationCode());
        assertNull(query.getIdpSubject());
    }

    @Test
    void findBinding_shouldReturnPrimaryMatchWithoutFallback() {
        PassportIdpBindingDao dao = mock(PassportIdpBindingDao.class);
        PassportIdpBindingPo primary = binding(100L, "union-1");
        when(dao.selectListWithoutIsolation(any())).thenReturn(List.of(primary));

        IdpPassportResolveRequest request = new IdpPassportResolveRequest();
        request.setIdpType("DINGTALK");
        request.setIdpSubject("union-1");
        request.setIdpApplicationCode("client-1");
        request.setIdpUserId("userid-1");

        PassportIdpBindingPo found = IdpPassportResolveServiceImpl.findBinding(dao, request);

        assertSame(primary, found);
        verify(dao, times(1)).selectListWithoutIsolation(any());
    }

    @Test
    void findBinding_shouldFallbackToIdpUserIdWhenPrimaryMisses() {
        PassportIdpBindingDao dao = mock(PassportIdpBindingDao.class);
        PassportIdpBindingPo fallback = binding(200L, "userid-2");
        when(dao.selectListWithoutIsolation(any()))
            .thenReturn(List.of())
            .thenReturn(List.of(fallback));

        IdpPassportResolveRequest request = new IdpPassportResolveRequest();
        request.setIdpType("DINGTALK");
        request.setIdpSubject("union-missing");
        request.setIdpApplicationCode("client-1");
        request.setIdpUserId("userid-2");

        PassportIdpBindingPo found = IdpPassportResolveServiceImpl.findBinding(dao, request);

        assertSame(fallback, found);
        verify(dao, times(2)).selectListWithoutIsolation(any());
    }

    @Test
    void findBinding_shouldReturnNullWhenNoMatchAndNoIdpUserId() {
        PassportIdpBindingDao dao = mock(PassportIdpBindingDao.class);
        when(dao.selectListWithoutIsolation(any())).thenReturn(List.of());

        IdpPassportResolveRequest request = new IdpPassportResolveRequest();
        request.setIdpType("DINGTALK");
        request.setIdpSubject("union-missing");
        request.setIdpApplicationCode("client-1");

        assertNull(IdpPassportResolveServiceImpl.findBinding(dao, request));
    }

    @Test
    void toUserSummaries_shouldMapUserFields() {
        UserVo user = new UserVo();
        user.setId(11L);
        user.setOrganId(22L);
        user.setRealName("张三");

        var summaries = IdpPassportResolveServiceImpl.toUserSummaries(List.of(user));

        assertEquals(1, summaries.size());
        assertEquals(11L, summaries.getFirst().getUserId());
        assertEquals(22L, summaries.getFirst().getOrganId());
        assertEquals("张三", summaries.getFirst().getRealName());
    }

    private static PassportIdpBindingPo binding(Long passportId, String idpSubject) {
        PassportIdpBindingPo binding = new PassportIdpBindingPo();
        binding.setPassportId(passportId);
        binding.setIdpSubject(idpSubject);
        binding.setIdpType("DINGTALK");
        binding.setIdpApplicationCode("client-1");
        return binding;
    }
}
