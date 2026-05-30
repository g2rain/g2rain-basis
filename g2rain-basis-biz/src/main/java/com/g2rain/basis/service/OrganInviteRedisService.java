package com.g2rain.basis.service;


import com.g2rain.basis.dto.OrganInviteRedisDto;
import com.g2rain.basis.enums.BasisRedisKeyRule;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.utils.Strings;
import com.g2rain.data.redis.GenericRedisHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

/**
 * 机构邀请码 Redis 读写
 */
@Service
@RequiredArgsConstructor
public class OrganInviteRedisService {

    private static final int DEFAULT_MAX_USES = 1;

    private static final Duration CONSUME_LOCK_TTL = Duration.ofSeconds(30);

    private final GenericRedisHelper genericRedisHelper;

    /**
     * 写入邀请码（同机构可同时存在多个有效码）
     */
    public void saveInvite(String inviteCode, OrganInviteRedisDto payload, Duration ttl) {
        String inviteKey = BasisRedisKeyRule.ORGAN_INVITE.format(inviteCode);
        genericRedisHelper.set(inviteKey, payload, ttl);
    }

    /**
     * 读取并一次性消费邀请码（按邀请码加锁，并发下仅一次成功）
     */
    public OrganInviteRedisDto consumeInvite(String inviteCode) {
        String normalized = normalizeInviteCode(inviteCode);
        if (Strings.isBlank(normalized)) {
            throw new BusinessException(BasisErrorCode.ORGAN_INVITE_INVALID);
        }

        String lockKey = BasisRedisKeyRule.ORGAN_INVITE_CONSUME_LOCK.format(normalized);
        String lockToken = UUID.randomUUID().toString();
        Boolean locked = genericRedisHelper.genericRedisTemplate()
            .opsForValue()
            .setIfAbsent(lockKey, lockToken, CONSUME_LOCK_TTL);
        if (!Boolean.TRUE.equals(locked)) {
            throw new BusinessException(BasisErrorCode.ORGAN_INVITE_INVALID);
        }

        try {
            return consumeInviteUnderLock(normalized);
        } finally {
            genericRedisHelper.delete(lockKey);
        }
    }

    private OrganInviteRedisDto consumeInviteUnderLock(String normalized) {
        String inviteKey = BasisRedisKeyRule.ORGAN_INVITE.format(normalized);
        OrganInviteRedisDto payload = genericRedisHelper.get(inviteKey, OrganInviteRedisDto.class);
        if (payload == null) {
            throw new BusinessException(BasisErrorCode.ORGAN_INVITE_INVALID);
        }

        int maxUses = payload.getMaxUses() == null ? DEFAULT_MAX_USES : payload.getMaxUses();
        int usedCount = payload.getUsedCount() == null ? 0 : payload.getUsedCount();
        if (maxUses > 0 && usedCount >= maxUses) {
            throw new BusinessException(BasisErrorCode.ORGAN_INVITE_INVALID);
        }

        genericRedisHelper.delete(inviteKey);
        return payload;
    }

    /**
     * 仅读取邀请码（不消费）
     */
    public OrganInviteRedisDto peekInvite(String inviteCode) {
        String normalized = normalizeInviteCode(inviteCode);
        if (Strings.isBlank(normalized)) {
            return null;
        }
        return genericRedisHelper.get(
            BasisRedisKeyRule.ORGAN_INVITE.format(normalized),
            OrganInviteRedisDto.class
        );
    }

    public static String generateInviteCode() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String normalizeInviteCode(String inviteCode) {
        if (Strings.isBlank(inviteCode)) {
            return null;
        }
        return inviteCode.trim().replace("-", "").toLowerCase();
    }
}
