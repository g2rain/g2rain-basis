package com.g2rain.basis.enums;

import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.utils.Strings;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * IdP 通讯录同步模式。
 */
@Schema(description = "IdP 通讯录同步模式")
public enum IdpSyncMode {

    /**
     * 全量对账：以 IdP 快照为权威源，处理快照外成员/部门/关系。
     */
    @Schema(description = "全量对账")
    FULL,

    /**
     * 增量 upsert：仅处理快照内的新增与更新。
     */
    @Schema(description = "增量 upsert")
    INCREMENTAL;

    public static void validate(String name) {
        if (Strings.isNotBlank(name)) {
            for (IdpSyncMode mode : values()) {
                if (mode.name().equals(name)) {
                    return;
                }
            }
        }
        throw new BusinessException(SystemErrorCode.PARAM_VAL_INVALID, name);
    }

    public static IdpSyncMode normalize(String name) {
        if (Strings.isBlank(name)) {
            return FULL;
        }
        validate(name);
        return valueOf(name.trim());
    }
}
