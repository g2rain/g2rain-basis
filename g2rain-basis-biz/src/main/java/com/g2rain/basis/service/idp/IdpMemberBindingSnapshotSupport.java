package com.g2rain.basis.service.idp;

import com.g2rain.basis.dao.po.PassportIdpBindingPo;
import com.g2rain.basis.idp.sync.dto.IdpMemberNode;
import com.g2rain.common.utils.Strings;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * IdP 成员快照与 binding 匹配规则（供安全闸与 FULL reconcile 共用）。
 */
public final class IdpMemberBindingSnapshotSupport {

    private IdpMemberBindingSnapshotSupport() {
    }

    public static Set<String> buildSnapshotBindingKeys(List<IdpMemberNode> members) {
        Set<String> keys = new HashSet<>();
        if (members == null) {
            return keys;
        }
        for (IdpMemberNode member : members) {
            if (Strings.isBlank(member.getUnionId()) && Strings.isBlank(member.getIdpUserId())) {
                continue;
            }
            if (Strings.isNotBlank(member.getUnionId())) {
                keys.add(subjectKey(member.getUnionId()));
            }
            if (Strings.isNotBlank(member.getIdpUserId())) {
                keys.add(userIdKey(member.getIdpUserId()));
            }
        }
        return keys;
    }

    public static boolean isBindingInSnapshot(PassportIdpBindingPo binding, Set<String> snapshotKeys) {
        if (Strings.isNotBlank(binding.getIdpSubject()) && snapshotKeys.contains(subjectKey(binding.getIdpSubject()))) {
            return true;
        }
        return Strings.isNotBlank(binding.getIdpUserId()) && snapshotKeys.contains(userIdKey(binding.getIdpUserId()));
    }

    private static String subjectKey(String idpSubject) {
        return "subject:" + idpSubject.trim();
    }

    private static String userIdKey(String idpUserId) {
        return "userId:" + idpUserId.trim();
    }
}
