package com.g2rain.basis.service.impl;

import com.g2rain.basis.dao.AnonymousVisitorDao;
import com.g2rain.basis.dao.po.AnonymousVisitorPo;
import com.g2rain.common.id.IdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AnonymousVisitorServiceImplTest {

    private static final String THUMBPRINT = "0123456789012345678901234567890123456789abc";

    private AnonymousVisitorServiceImpl service;
    private AnonymousVisitorDao anonymousVisitorDao;

    @BeforeEach
    void setUp() {
        service = new AnonymousVisitorServiceImpl();
        anonymousVisitorDao = mock(AnonymousVisitorDao.class);
        IdGenerator idGenerator = mock(IdGenerator.class);
        when(idGenerator.generateSnowflakeId()).thenReturn(77L);
        service.setIdGenerator(idGenerator);
        ReflectionTestUtils.setField(service, "anonymousVisitorDao", anonymousVisitorDao);
    }

    @Test
    void resolve_existing_shouldReturnSameIdAndTouch() {
        when(anonymousVisitorDao.selectListWithoutIsolation(any())).thenReturn(List.of(visitor(66L)));

        assertEquals(66L, service.resolve(100L, THUMBPRINT, 5L));

        ArgumentCaptor<AnonymousVisitorPo> captor = ArgumentCaptor.forClass(AnonymousVisitorPo.class);
        verify(anonymousVisitorDao).updateWithoutIsolation(captor.capture());
        assertEquals(66L, captor.getValue().getId());
        verify(anonymousVisitorDao, never()).insertWithoutIsolation(any());
    }

    @Test
    void resolve_new_shouldInsert() {
        when(anonymousVisitorDao.selectListWithoutIsolation(any())).thenReturn(List.of());

        assertEquals(77L, service.resolve(100L, THUMBPRINT, 5L));

        ArgumentCaptor<AnonymousVisitorPo> captor = ArgumentCaptor.forClass(AnonymousVisitorPo.class);
        verify(anonymousVisitorDao).insertWithoutIsolation(captor.capture());
        AnonymousVisitorPo inserted = captor.getValue();
        assertEquals(100L, inserted.getOrganId());
        assertEquals(THUMBPRINT, inserted.getKeyThumbprint());
        assertEquals(5L, inserted.getEntryId());
    }

    @Test
    void resolve_concurrentInsert_shouldReturnExistingVisitor() {
        when(anonymousVisitorDao.selectListWithoutIsolation(any()))
            .thenReturn(List.of())
            .thenReturn(List.of(visitor(66L)));
        when(anonymousVisitorDao.insertWithoutIsolation(any())).thenThrow(new DuplicateKeyException("dup"));

        assertEquals(66L, service.resolve(100L, THUMBPRINT, 5L));
    }

    private static AnonymousVisitorPo visitor(Long id) {
        AnonymousVisitorPo po = new AnonymousVisitorPo();
        po.setId(id);
        po.setOrganId(100L);
        po.setKeyThumbprint(THUMBPRINT);
        return po;
    }
}
