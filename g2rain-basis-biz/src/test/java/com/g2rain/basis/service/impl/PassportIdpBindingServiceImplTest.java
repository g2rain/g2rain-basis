package com.g2rain.basis.service.impl;

import com.g2rain.basis.dto.PassportIdpBindingBindDto;
import com.g2rain.common.enums.SessionType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PassportIdpBindingServiceImplTest {

    @Test
    void canAutoProvisionFromTrustedCaller_shouldRequireUserSessionAndAdmin() {
        PassportIdpBindingBindDto dto = new PassportIdpBindingBindDto();
        dto.setSessionType(SessionType.USER.name());
        dto.setAdminUser(true);
        assertTrue(PassportIdpBindingServiceImpl.canAutoProvisionFromTrustedCaller(dto));

        dto.setAdminUser(false);
        assertFalse(PassportIdpBindingServiceImpl.canAutoProvisionFromTrustedCaller(dto));

        dto.setSessionType(SessionType.PASSPORT.name());
        dto.setAdminUser(true);
        assertFalse(PassportIdpBindingServiceImpl.canAutoProvisionFromTrustedCaller(dto));
    }

    @Test
    void resolveSessionTypeFromDto_shouldDefaultToPassportWhenMissing() {
        PassportIdpBindingBindDto dto = new PassportIdpBindingBindDto();
        assertTrue(SessionType.PASSPORT == PassportIdpBindingServiceImpl.resolveSessionTypeFromDto(dto));

        dto.setSessionType(SessionType.USER.name());
        assertTrue(SessionType.USER == PassportIdpBindingServiceImpl.resolveSessionTypeFromDto(dto));
    }
}
