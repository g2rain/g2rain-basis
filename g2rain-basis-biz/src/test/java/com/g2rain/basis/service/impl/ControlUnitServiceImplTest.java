package com.g2rain.basis.service.impl;

import com.g2rain.basis.dao.ApplicationDao;
import com.g2rain.basis.dao.ControlDomainControlUnitRelationDao;
import com.g2rain.basis.dao.ControlUnitDao;
import com.g2rain.basis.dao.ControlUnitResourceRelationDao;
import com.g2rain.basis.dao.RoleControlUnitRelationDao;
import com.g2rain.basis.dao.po.ApplicationPo;
import com.g2rain.basis.dao.po.ControlUnitPo;
import com.g2rain.basis.dto.ControlUnitDto;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.enums.ControlUnitStatus;
import com.g2rain.basis.service.MemberPermSyncService;
import com.g2rain.common.enums.SessionType;
import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.id.IdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ControlUnitServiceImplTest {

    private ControlUnitServiceImpl service;
    private ControlUnitDao controlUnitDao;
    private ApplicationDao applicationDao;
    private MemberPermSyncService memberPermSyncService;
    private IdGenerator idGenerator;

    @BeforeEach
    void setUp() {
        service = new ControlUnitServiceImpl();
        controlUnitDao = mock(ControlUnitDao.class);
        applicationDao = mock(ApplicationDao.class);
        memberPermSyncService = mock(MemberPermSyncService.class);
        idGenerator = mock(IdGenerator.class);

        ReflectionTestUtils.setField(service, "controlUnitDao", controlUnitDao);
        ReflectionTestUtils.setField(service, "applicationDao", applicationDao);
        ReflectionTestUtils.setField(service, "controlUnitResourceRelationDao",
            mock(ControlUnitResourceRelationDao.class));
        ReflectionTestUtils.setField(service, "roleControlUnitRelationDao",
            mock(RoleControlUnitRelationDao.class));
        ReflectionTestUtils.setField(service, "controlDomainControlUnitRelationDao",
            mock(ControlDomainControlUnitRelationDao.class));
        ReflectionTestUtils.setField(service, "memberPermSyncService", memberPermSyncService);
        service.setIdGenerator(idGenerator);

        ApplicationPo app = new ApplicationPo();
        app.setId(7L);
        when(applicationDao.selectById(7L)).thenReturn(app);
        when(controlUnitDao.selectList(any())).thenReturn(List.of());
        when(idGenerator.generateId()).thenReturn(100L);
        when(controlUnitDao.insert(any())).thenReturn(1);
        when(controlUnitDao.update(any())).thenReturn(1);
    }

    @Test
    void save_create_shouldPersistSessionType() {
        ControlUnitDto dto = new ControlUnitDto();
        dto.setApplicationId(7L);
        dto.setSessionType(SessionType.MEMBER.name());
        dto.setControlUnitName("会员自助");
        dto.setControlUnitScope("CUSTOMER");

        Long id = service.save(dto);
        assertEquals(100L, id);
        verify(controlUnitDao).insert(any(ControlUnitPo.class));
    }

    @Test
    void save_update_shouldRejectSessionTypeChange() {
        ControlUnitPo existing = new ControlUnitPo();
        existing.setId(14L);
        existing.setSessionType(SessionType.USER.name());
        existing.setStatus(ControlUnitStatus.UNPUBLISHED.name());
        when(controlUnitDao.selectById(14L)).thenReturn(existing);

        ControlUnitDto dto = new ControlUnitDto();
        dto.setId(14L);
        dto.setApplicationId(7L);
        dto.setSessionType(SessionType.MEMBER.name());
        dto.setControlUnitName("盘古");
        dto.setControlUnitScope("PERPETUAL");

        BusinessException ex = assertThrows(BusinessException.class, () -> service.save(dto));
        assertEquals(BasisErrorCode.SESSION_TYPE_UPDATE_ILLEGAL.code(), ex.getErrorCode());
        verify(controlUnitDao, never()).update(any());
    }

    @Test
    void parseSessionType_shouldRejectBlank() {
        assertThrows(BusinessException.class, () -> ControlUnitServiceImpl.parseSessionType(" "));
    }
}
