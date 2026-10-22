package com.g2rain.basis.enums;

import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

/**
 * 匿名接入码状态枚举。
 */
@Schema(description = "匿名接入码状态枚举")
public enum EntryStatus {

    @Schema(description = "有效")
    ACTIVE,

    @Schema(description = "无效")
    INACTIVE;

    /**
     * 校验状态名称合法。
     *
     * @param name 状态名称
     * @throws BusinessException 名称为空或不是合法状态时抛出
     */
    public static void validate(String name) {
        if (Arrays.stream(values()).noneMatch(status -> status.name().equals(name))) {
            throw new BusinessException(SystemErrorCode.PARAM_VAL_INVALID, name);
        }
    }
}
