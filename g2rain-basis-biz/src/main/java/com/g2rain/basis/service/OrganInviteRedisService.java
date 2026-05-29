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

    private final GenericRedisHelper genericRedisHelper;

    /**
     * 写入邀请码并维护机构当前有效码索引
     */
    public void saveInvite(String inviteCode, OrganInviteRedisDto payload, Duration ttl) {
        String inviteKey = BasisRedisKeyRule.ORGAN_INVITE.format(inviteCode);
        String activeKey = BasisRedisKeyRule.ORGAN_INVITE_ACTIVE.format(String.valueOf(payload.getOrganId()));

        String previousCode = genericRedisHelper.get(activeKey, String.class);
        if (Strings.isNotBlank(previousCode)) {
            genericRedisHelper.delete(BasisRedisKeyRule.ORGAN_INVITE.format(previousCode.trim()));
        }

        genericRedisHelper.set(inviteKey, payload, ttl);
        genericRedisHelper.set(activeKey, inviteCode, ttl);
    }

    /**
     * 读取并一次性消费邀请码（默认单次使用）
     */
    public OrganInviteRedisDto consumeInvite(String inviteCode) {
        String normalized = normalizeInviteCode(inviteCode);
        if (Strings.isBlank(normalized)) {
            throw new BusinessException(BasisErrorCode.ORGAN_INVITE_INVALID);
        }

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

        if (maxUses <= 1) {
            genericRedisHelper.delete(inviteKey);
            if (payload.getOrganId() != null) {
                genericRedisHelper.delete(
                    BasisRedisKeyRule.ORGAN_INVITE_ACTIVE.format(String.valueOf(payload.getOrganId()))
                );
            }
            return payload;
        }

        payload.setUsedCount(usedCount + 1);
        if (payload.getUsedCount() >= maxUses) {
            genericRedisHelper.delete(inviteKey);
            if (payload.getOrganId() != null) {
                genericRedisHelper.delete(
                    BasisRedisKeyRule.ORGAN_INVITE_ACTIVE.format(String.valueOf(payload.getOrganId()))
                );
            }
        } else {
            genericRedisHelper.set(inviteKey, payload, null);
        }
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
