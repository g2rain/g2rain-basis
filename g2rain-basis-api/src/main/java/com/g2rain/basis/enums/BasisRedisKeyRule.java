package com.g2rain.basis.enums;


import com.g2rain.common.utils.Collections;
import lombok.Getter;

/**
 * Basis 模块 Redis 键规则
 */
@Getter
public enum BasisRedisKeyRule {

    /**
     * 机构邀请码，格式 basis:organ:invite:{inviteCode}
     */
    ORGAN_INVITE("basis:organ:invite:%s"),

    /**
     * 机构当前有效邀请码索引，格式 basis:organ:invite:active:{organId}
     */
    ORGAN_INVITE_ACTIVE("basis:organ:invite:active:%s");

    private final String key;

    BasisRedisKeyRule(String key) {
        this.key = key;
    }

    public String format(String... values) {
        if (Collections.isEmpty(values)) {
            return this.key;
        }
        return String.format(this.key, (Object[]) values);
    }
}
