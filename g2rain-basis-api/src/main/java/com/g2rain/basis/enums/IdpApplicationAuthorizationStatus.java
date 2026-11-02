package com.g2rain.basis.enums;


import lombok.extern.slf4j.Slf4j;
import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;

@Slf4j
public enum IdpApplicationAuthorizationStatus {
    PENDING,
    ACTIVE,
    REVOKED,
    EXPIRED;

    public static IdpApplicationAuthorizationStatus require(String value) {
        if (value == null) {
            throw new BusinessException(SystemErrorCode.PARAM_REQUIRED, "authorizationStatus");
        }
        try {
            return valueOf(value.trim());
        } catch (IllegalArgumentException exception) {
            log.error("failed to parse IdpApplicationAuthorizationStatus", exception);
            throw new BusinessException(SystemErrorCode.PARAM_VAL_INVALID, "authorizationStatus");
        }
    }
}
