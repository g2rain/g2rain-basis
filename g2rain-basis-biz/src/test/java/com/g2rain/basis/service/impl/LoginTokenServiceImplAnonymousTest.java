package com.g2rain.basis.service.impl;

import com.g2rain.basis.service.AnonymousEntryService;
import com.g2rain.basis.service.AnonymousVisitorService;
import com.g2rain.basis.service.ApplicationService;
import com.g2rain.basis.vo.AnonymousEntryVo;
import com.g2rain.basis.vo.ApplicationVo;
import com.g2rain.basis.vo.OrganVo;
import com.g2rain.common.enums.OrganType;
import com.g2rain.common.enums.SessionType;
import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.web.TokenJWTPayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LoginTokenServiceImplAnonymousTest {

    private static final String THUMBPRINT = "0123456789012345678901234567890123456789abc";

    private LoginTokenServiceImpl service;
    private AnonymousEntryService anonymousEntryService;
    private AnonymousVisitorService anonymousVisitorService;

    @BeforeEach
    void setUp() {
        service = new LoginTokenServiceImpl();
        ApplicationService applicationService = mock(ApplicationService.class);
        anonymousEntryService = mock(AnonymousEntryService.class);
        anonymousVisitorService = mock(AnonymousVisitorService.class);
        ReflectionTestUtils.setField(service, "applicationService", applicationService);
        ReflectionTestUtils.setField(service, "anonymousEntryService", anonymousEntryService);
        ReflectionTestUtils.setField(service, "anonymousVisitorService", anonymousVisitorService);

        ApplicationVo application = new ApplicationVo();
        application.setId(200L);
        application.setOrganId(1L);
        application.setApplicationCode("g2rain-chat-shell");
        application.setAccessTokenExpiresIn(1800);
        application.setRefreshTokenExpiresIn(3600);
        when(applicationService.selectList(any())).thenReturn(List.of(application));

        AnonymousEntryVo entry = new AnonymousEntryVo();
        entry.setId(5L);
        entry.setOrganId(100L);
        when(anonymousEntryService.resolve("ek_x", 200L)).thenReturn(entry);
        when(anonymousEntryService.resolve(5L, 200L)).thenReturn(entry);

        OrganVo organ = new OrganVo();
        organ.setId(100L);
        organ.setOrganName("租户 A");
        organ.setOrganType(OrganType.TENANT.name());
        when(anonymousEntryService.requireOpenOrgan(100L, 200L)).thenReturn(organ);
        when(anonymousVisitorService.resolve(100L, THUMBPRINT, 5L)).thenReturn(77L);
    }

    @Test
    void fetchAnonymousTokenContext_shouldBuildVisitorPayload() {
        TokenJWTPayload payload = service.fetchAnonymousTokenContext("ek_x", "g2rain-chat-shell", THUMBPRINT);

        assertEquals(SessionType.ANONYMOUS, payload.getSessionType());
        assertEquals(77L, payload.getUserId());
        assertNull(payload.getPassportId());
        assertNull(payload.getMemberId());
        assertNull(payload.getRoleIds());
        assertFalse(payload.isAdminUser());
        assertFalse(payload.isAdminCompany());
        assertEquals(100L, payload.getOrganId());
        assertEquals(OrganType.TENANT, payload.getOrganType());
        assertEquals(5L, payload.getEntryId());
        assertEquals(1, payload.getApplicationScopes().size());
        assertEquals("g2rain-chat-shell", payload.getApplicationScopes().getFirst().getApplicationCode());
    }

    @Test
    void fetchAnonymousRefreshContext_shouldResolveByEntryId() {
        TokenJWTPayload payload = service.fetchAnonymousRefreshContext(5L, "g2rain-chat-shell", THUMBPRINT);

        assertEquals(77L, payload.getUserId());
        verify(anonymousEntryService).resolve(5L, 200L);
    }

    @Test
    void fetchAnonymousTokenContext_invalidThumbprint_shouldReject() {
        assertThrows(BusinessException.class,
            () -> service.fetchAnonymousTokenContext("ek_x", "g2rain-chat-shell", "short"));
    }
}
