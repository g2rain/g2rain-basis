package com.g2rain.basis.service.impl;

import com.g2rain.basis.dao.ApplicationAuthorizationDao;
import com.g2rain.basis.dao.PersonalStaticAccessTokenDao;
import com.g2rain.basis.dao.UserDao;
import com.g2rain.basis.dao.po.UserPo;
import com.g2rain.basis.dto.UserSelectDto;
import com.g2rain.basis.dao.po.ApplicationAuthorizationPo;
import com.g2rain.basis.dao.po.PersonalStaticAccessTokenPo;
import com.g2rain.basis.dto.ApplicationAuthorizationSelectDto;
import com.g2rain.basis.dto.PersonalStaticAccessTokenDto;
import com.g2rain.basis.dto.PersonalStaticAccessTokenSelectDto;
import com.g2rain.basis.enums.AuthorizationStatus;
import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.web.PrincipalContext;
import com.g2rain.common.web.PrincipalContextHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PersonalStaticAccessTokenServiceImplTest {

    private PersonalStaticAccessTokenServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PersonalStaticAccessTokenServiceImpl();
    }

    @Test
    void resolveTargetUserId_shouldAllowTenantAdminToSpecifyUser() throws Exception {
        UserPo targetUser = new UserPo();
        targetUser.setId(2002L);
        targetUser.setOrganId(100L);
        StubUserDao userDao = new StubUserDao();
        userDao.selectByIdResult = targetUser;
        injectDao("userDao", userDao);

        PersonalStaticAccessTokenDto dto = new PersonalStaticAccessTokenDto();
        dto.setUserId(2002L);

        runWithPrincipal(true, () -> {
            try {
                assertEquals(2002L, invokeResolveTargetUserId(dto, 100L));
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        });
    }

    @Test
    void resolveTargetUserId_shouldRejectUserFromOtherOrgan() throws Exception {
        UserPo targetUser = new UserPo();
        targetUser.setId(2002L);
        targetUser.setOrganId(999L);
        StubUserDao userDao = new StubUserDao();
        userDao.selectByIdResult = targetUser;
        injectDao("userDao", userDao);

        PersonalStaticAccessTokenDto dto = new PersonalStaticAccessTokenDto();
        dto.setUserId(2002L);

        runWithPrincipal(true, () ->
            assertThrows(BusinessException.class, () -> invokeResolveTargetUserId(dto, 100L)));
    }

    @Test
    void resolveTargetUserId_shouldRejectNonAdminPassingOtherUserId() {
        PersonalStaticAccessTokenDto dto = new PersonalStaticAccessTokenDto();
        dto.setUserId(2002L);

        runWithPrincipal(false, () ->
            assertThrows(BusinessException.class, () -> invokeResolveTargetUserId(dto, 100L)));
    }

    @Test
    void resolveTargetUserId_shouldUseCurrentUserWhenNotSpecified() {
        runWithPrincipal(false, () -> {
            try {
                assertEquals(1001L, invokeResolveTargetUserId(new PersonalStaticAccessTokenDto(), 100L));
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        });
    }

    @Test
    void selectList_shouldAcceptQueryWithoutApplicationScope() throws Exception {
        injectDao("personalStaticAccessTokenDao", new StubPersonalStaticAccessTokenDao());
        injectDao("userDao", new StubUserDao());

        runWithPrincipal(false, () -> {
            try {
                assertEquals(0, service.selectList(new PersonalStaticAccessTokenSelectDto()).size());
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        });
    }

    @Test
    void selectList_shouldAcceptApplicationIdOnly() throws Exception {
        injectDao("personalStaticAccessTokenDao", new StubPersonalStaticAccessTokenDao());
        injectDao("userDao", new StubUserDao());

        PersonalStaticAccessTokenSelectDto selectDto = new PersonalStaticAccessTokenSelectDto();
        selectDto.setApplicationId(200L);
        runWithPrincipal(false, () ->
            assertEquals(0, service.selectList(selectDto).size()));
    }

    @Test
    void resolveApplicationAuthorization_shouldLoadByAuthorizationId() throws Exception {
        ApplicationAuthorizationPo authorization = new ApplicationAuthorizationPo();
        authorization.setId(10L);
        authorization.setApplicationId(200L);

        StubApplicationAuthorizationDao authorizationDao = new StubApplicationAuthorizationDao();
        authorizationDao.selectByIdResult = authorization;
        injectDao("applicationAuthorizationDao", authorizationDao);

        PersonalStaticAccessTokenDto dto = new PersonalStaticAccessTokenDto();
        dto.setApplicationAuthorizationId(10L);

        ApplicationAuthorizationPo resolved = invokeResolveApplicationAuthorization(dto);
        assertEquals(10L, resolved.getId());
    }

    @Test
    void resolveApplicationAuthorization_shouldResolveByApplicationIdAndOrganId() throws Exception {
        ApplicationAuthorizationPo authorization = new ApplicationAuthorizationPo();
        authorization.setId(10L);
        authorization.setApplicationId(200L);
        authorization.setOrganId(100L);

        StubApplicationAuthorizationDao authorizationDao = new StubApplicationAuthorizationDao();
        authorizationDao.selectListResult = List.of(authorization);
        injectDao("applicationAuthorizationDao", authorizationDao);

        PersonalStaticAccessTokenDto dto = new PersonalStaticAccessTokenDto();
        dto.setApplicationId(200L);

        runWithPrincipal(false, () -> {
            try {
                ApplicationAuthorizationPo resolved = invokeResolveApplicationAuthorization(dto);
                assertEquals(10L, resolved.getId());
                assertEquals(200L, authorizationDao.lastSelect.getApplicationId());
                assertEquals(AuthorizationStatus.ACTIVATED.name(), authorizationDao.lastSelect.getStatus());
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        });
    }

    @Test
    void resolveApplicationAuthorization_shouldRejectMismatchedApplicationId() throws Exception {
        ApplicationAuthorizationPo authorization = new ApplicationAuthorizationPo();
        authorization.setId(10L);
        authorization.setApplicationId(200L);

        StubApplicationAuthorizationDao authorizationDao = new StubApplicationAuthorizationDao();
        authorizationDao.selectByIdResult = authorization;
        injectDao("applicationAuthorizationDao", authorizationDao);

        PersonalStaticAccessTokenDto dto = new PersonalStaticAccessTokenDto();
        dto.setApplicationAuthorizationId(10L);
        dto.setApplicationId(999L);

        assertThrows(BusinessException.class, () -> invokeResolveApplicationAuthorization(dto));
    }

    private void injectDao(String fieldName, Object dao) throws Exception {
        Field field = PersonalStaticAccessTokenServiceImpl.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(service, dao);
    }

    private void runWithPrincipal(boolean adminUser, Runnable action) {
        PrincipalContext context = PrincipalContext.of();
        context.setOrganId(100L);
        context.setUserId(1001L);
        context.setAdminUser(adminUser);
        PrincipalContextHolder.runWith(context, action);
    }

    private ApplicationAuthorizationPo invokeResolveApplicationAuthorization(PersonalStaticAccessTokenDto dto)
        throws Exception {
        Method method = PersonalStaticAccessTokenServiceImpl.class.getDeclaredMethod(
            "resolveApplicationAuthorization", PersonalStaticAccessTokenDto.class);
        method.setAccessible(true);
        try {
            return (ApplicationAuthorizationPo) method.invoke(service, dto);
        } catch (InvocationTargetException ex) {
            if (ex.getCause() instanceof Exception exception) {
                throw exception;
            }
            throw ex;
        }
    }

    private Long invokeResolveTargetUserId(PersonalStaticAccessTokenDto dto, Long organId) throws Exception {
        Method method = PersonalStaticAccessTokenServiceImpl.class.getDeclaredMethod(
            "resolveTargetUserId", PersonalStaticAccessTokenDto.class, Long.class);
        method.setAccessible(true);
        try {
            return (Long) method.invoke(service, dto, organId);
        } catch (InvocationTargetException ex) {
            if (ex.getCause() instanceof Exception exception) {
                throw exception;
            }
            throw ex;
        }
    }

    private static final class StubPersonalStaticAccessTokenDao implements PersonalStaticAccessTokenDao {

        @Override
        public List<PersonalStaticAccessTokenPo> selectList(PersonalStaticAccessTokenSelectDto selectDto) {
            return Collections.emptyList();
        }

        @Override
        public int insert(PersonalStaticAccessTokenPo entity) {
            return 0;
        }

        @Override
        public int insertMultiple(List<PersonalStaticAccessTokenPo> list) {
            return 0;
        }

        @Override
        public int update(PersonalStaticAccessTokenPo entity) {
            return 0;
        }

        @Override
        public int delete(Long id) {
            return 0;
        }

        @Override
        public int updateByVersion(PersonalStaticAccessTokenPo entity) {
            return 0;
        }

        @Override
        public PersonalStaticAccessTokenPo selectById(Long id) {
            return null;
        }

        @Override
        public PersonalStaticAccessTokenPo selectByTokenHash(String tokenHash) {
            return null;
        }

        @Override
        public Long checkStaticAccessTokenExists(PersonalStaticAccessTokenSelectDto selectDto) {
            return 0L;
        }
    }

    private static final class StubApplicationAuthorizationDao implements ApplicationAuthorizationDao {

        private ApplicationAuthorizationPo selectByIdResult;
        private List<ApplicationAuthorizationPo> selectListResult = List.of();
        private ApplicationAuthorizationSelectDto lastSelect;

        @Override
        public ApplicationAuthorizationPo selectById(Long id) {
            return selectByIdResult;
        }

        @Override
        public List<ApplicationAuthorizationPo> selectList(ApplicationAuthorizationSelectDto selectDto) {
            lastSelect = selectDto;
            return selectListResult;
        }

        @Override
        public int insert(ApplicationAuthorizationPo entity) {
            return 0;
        }

        @Override
        public int insertMultiple(List<ApplicationAuthorizationPo> list) {
            return 0;
        }

        @Override
        public int update(ApplicationAuthorizationPo entity) {
            return 0;
        }

        @Override
        public int delete(Long id) {
            return 0;
        }

        @Override
        public int updateByVersion(ApplicationAuthorizationPo entity) {
            return 0;
        }

        @Override
        public Long checkApplicationAuthorizationExists(ApplicationAuthorizationSelectDto selectDto) {
            return 0L;
        }
    }

    private static final class StubUserDao implements UserDao {

        private UserPo selectByIdResult;

        @Override
        public UserPo selectById(Long id) {
            return selectByIdResult;
        }

        @Override
        public List<UserPo> selectListWithoutIsolation(UserSelectDto selectDto) {
            return Collections.emptyList();
        }

        @Override
        public int insert(UserPo entity) {
            return 0;
        }

        @Override
        public int insertWithoutIsolation(UserPo entity) {
            return 0;
        }

        @Override
        public int insertMultiple(List<UserPo> list) {
            return 0;
        }

        @Override
        public int update(UserPo entity) {
            return 0;
        }

        @Override
        public int updateWithoutIsolation(UserPo entity) {
            return 0;
        }

        @Override
        public int delete(Long id) {
            return 0;
        }

        @Override
        public int updateByVersion(UserPo entity) {
            return 0;
        }

        @Override
        public UserPo selectByIdWithoutIsolation(Long id) {
            return null;
        }

        @Override
        public List<UserPo> selectList(UserSelectDto selectDto) {
            return Collections.emptyList();
        }

        @Override
        public Long checkUserExists(UserSelectDto selectDto) {
            return 0L;
        }
    }
}
