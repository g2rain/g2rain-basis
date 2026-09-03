package com.g2rain.basis.service.impl;

import com.g2rain.basis.dao.OrganDao;
import com.g2rain.basis.dto.OrganDto;
import com.g2rain.basis.enums.BasisSyncerEnum;
import com.g2rain.basis.service.OrganClosureService;
import com.g2rain.basis.utils.Constants;
import com.g2rain.basis.vo.OrganIdNameVo;
import com.g2rain.common.id.IdGenerator;
import com.g2rain.common.syncer.EventPublisherHub;
import com.g2rain.common.web.PrincipalContext;
import com.g2rain.common.web.PrincipalContextHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * OrganServiceImpl ORGAN_NAME 事件：afterCommit / 无事务兜底。
 */
class OrganServiceImplOrganNameEventTest {

    private OrganServiceImpl service;
    private OrganDao organDao;
    private OrganClosureService organClosureService;
    private EventPublisherHub eventPublisherHub;
    private IdGenerator idGenerator;

    @BeforeEach
    void setUp() {
        service = new OrganServiceImpl();
        organDao = mock(OrganDao.class);
        organClosureService = mock(OrganClosureService.class);
        eventPublisherHub = mock(EventPublisherHub.class);
        idGenerator = mock(IdGenerator.class);

        ReflectionTestUtils.setField(service, "organDao", organDao);
        ReflectionTestUtils.setField(service, "organClosureService", organClosureService);
        ReflectionTestUtils.setField(service, "eventPublisherHub", eventPublisherHub);
        service.setIdGenerator(idGenerator);

        when(idGenerator.generateId()).thenReturn(1001L);
        when(organDao.selectList(any())).thenReturn(List.of());
        when(organDao.insert(any())).thenReturn(1);
    }

    @AfterEach
    void tearDown() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    void create_withoutTransaction_shouldSendCreateImmediately() {
        runAsNonAdmin(() -> {
            Long id = invokeDoSave(sampleCreateDto());
            assertEquals(1001L, id);
            verify(eventPublisherHub).sendCreate(
                eq(Constants.SYNC_OUTPUT_BINDING),
                eq(BasisSyncerEnum.ORGAN_NAME.name()),
                any(OrganIdNameVo.class)
            );
        });
    }

    @Test
    void create_withTransaction_shouldSendCreateOnlyAfterCommit() {
        TransactionSynchronizationManager.initSynchronization();
        try {
            runAsNonAdmin(() -> invokeDoSave(sampleCreateDto()));

            verify(eventPublisherHub, never()).sendCreate(any(), any(), any());

            List<TransactionSynchronization> syncs =
                TransactionSynchronizationManager.getSynchronizations();
            assertEquals(1, syncs.size());
            syncs.getFirst().afterCommit();

            verify(eventPublisherHub).sendCreate(
                eq(Constants.SYNC_OUTPUT_BINDING),
                eq(BasisSyncerEnum.ORGAN_NAME.name()),
                any(OrganIdNameVo.class)
            );
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    void create_withTransaction_shouldNotSendCreateOnRollback() {
        TransactionSynchronizationManager.initSynchronization();
        try {
            runAsNonAdmin(() -> invokeDoSave(sampleCreateDto()));

            verify(eventPublisherHub, never()).sendCreate(any(), any(), any());

            // 回滚路径：不调用 afterCommit，仅清理同步回调
            TransactionSynchronizationManager.clearSynchronization();

            verify(eventPublisherHub, never()).sendCreate(any(), any(), any());
        } finally {
            if (TransactionSynchronizationManager.isSynchronizationActive()) {
                TransactionSynchronizationManager.clearSynchronization();
            }
        }
    }

    @Test
    void updateName_withTransaction_shouldSendUpdateOnlyAfterCommit() {
        when(organDao.update(any())).thenReturn(1);

        OrganDto dto = new OrganDto();
        dto.setId(200L);
        dto.setOrganName("Renamed");
        dto.setOrganType("TENANT");

        TransactionSynchronizationManager.initSynchronization();
        try {
            invokeDoSave(dto);

            verify(eventPublisherHub, never()).sendUpdate(any(), any(), any());

            TransactionSynchronizationManager.getSynchronizations()
                .forEach(TransactionSynchronization::afterCommit);

            verify(eventPublisherHub).sendUpdate(
                eq(Constants.SYNC_OUTPUT_BINDING),
                eq(BasisSyncerEnum.ORGAN_NAME.name()),
                any(OrganIdNameVo.class)
            );
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    private Long invokeDoSave(OrganDto dto) {
        return ReflectionTestUtils.invokeMethod(service, "doSave", dto);
    }

    private static OrganDto sampleCreateDto() {
        OrganDto dto = new OrganDto();
        dto.setOrganName("Acme");
        dto.setOrganType("TENANT");
        return dto;
    }

    private void runAsNonAdmin(Runnable task) {
        PrincipalContext context = PrincipalContext.of();
        context.setPassportId(10001L);
        // organId 为空：无上级挂载；非运营公司可创建
        PrincipalContextHolder.runWith(context, task);
    }
}
