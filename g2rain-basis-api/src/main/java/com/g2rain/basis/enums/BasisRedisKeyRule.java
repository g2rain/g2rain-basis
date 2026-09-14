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
     * 邀请码消费分布式锁，格式 basis:organ:invite:consume:lock:{inviteCode}
     */
    ORGAN_INVITE_CONSUME_LOCK("basis:organ:invite:consume:lock:%s"),

    /**
     * 某机构 MEMBER 会话 API 权限版本，格式 basis:perm:member:version:{organId}
     */
    MEMBER_PERM_VERSION("basis:perm:member:version:%s");

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
