package com.g2rain.basis.enums;

import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;

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
            throw new BusinessException(SystemErrorCode.PARAM_VAL_INVALID, "authorizationStatus");
        }
    }
}
