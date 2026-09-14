package com.g2rain.basis.service.impl;

import com.g2rain.basis.dao.OrganDao;
import com.g2rain.basis.dao.PassportDao;
import com.g2rain.basis.dao.UserDao;
import com.g2rain.basis.dao.po.OrganPo;
import com.g2rain.basis.dao.po.PassportPo;
import com.g2rain.basis.dao.po.UserPo;
import com.g2rain.basis.dto.IdpEmployeeEnsureRequest;
import com.g2rain.basis.dto.UserDto;
import com.g2rain.basis.service.UserService;
import com.g2rain.basis.vo.IdpEmployeeEnsureVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdpEmployeeServiceImplTest {

    @Mock
    private UserDao userDao;
    @Mock
    private PassportDao passportDao;
    @Mock
    private OrganDao organDao;
    @Mock
    private UserService userService;

    private IdpEmployeeServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new IdpEmployeeServiceImpl();
        ReflectionTestUtils.setField(service, "userDao", userDao);
        ReflectionTestUtils.setField(service, "passportDao", passportDao);
        ReflectionTestUtils.setField(service, "organDao", organDao);
        ReflectionTestUtils.setField(service, "userService", userService);
    }

    @Test
    void ensureReturnsExistingUser() {
        PassportPo passport = new PassportPo();
        passport.setId(1L);
        when(passportDao.selectByIdWithoutIsolation(1L)).thenReturn(passport);
        OrganPo organ = new OrganPo();
        organ.setId(10L);
        when(organDao.selectById(10L)).thenReturn(organ);
        UserPo existing = new UserPo();
        existing.setId(99L);
        when(userDao.selectListWithoutIsolation(any())).thenReturn(List.of(existing));

        IdpEmployeeEnsureRequest request = new IdpEmployeeEnsureRequest();
        request.setOrganId(10L);
        request.setPassportId(1L);
        IdpEmployeeEnsureVo vo = service.ensure(request);

        assertEquals(99L, vo.getUserId());
        assertFalse(vo.isCreated());
    }

    @Test
    void ensureCreatesNonAdminUserViaUserService() {
        PassportPo passport = new PassportPo();
        passport.setId(1L);
        passport.setRealName("张三");
        when(passportDao.selectByIdWithoutIsolation(1L)).thenReturn(passport);
        OrganPo organ = new OrganPo();
        organ.setId(10L);
        when(organDao.selectById(10L)).thenReturn(organ);
        when(userDao.selectListWithoutIsolation(any())).thenReturn(List.of());
        when(userService.saveWithoutIsolation(any())).thenReturn(77L);

        IdpEmployeeEnsureRequest request = new IdpEmployeeEnsureRequest();
        request.setOrganId(10L);
        request.setPassportId(1L);
        IdpEmployeeEnsureVo vo = service.ensure(request);

        assertEquals(77L, vo.getUserId());
        assertTrue(vo.isCreated());
        ArgumentCaptor<UserDto> captor = ArgumentCaptor.forClass(UserDto.class);
        verify(userService).saveWithoutIsolation(captor.capture());
        assertEquals(10L, captor.getValue().getOrganId());
        assertEquals(1L, captor.getValue().getPassportId());
        assertEquals("张三", captor.getValue().getRealName());
    }
}
