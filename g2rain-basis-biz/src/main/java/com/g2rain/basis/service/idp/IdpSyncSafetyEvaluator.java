package com.g2rain.basis.service.idp;

import com.g2rain.basis.config.IdpSyncSafetyProperties;
import com.g2rain.basis.dao.po.PassportIdpBindingPo;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.enums.IdpSyncMode;
import com.g2rain.basis.idp.sync.dto.IdpDepartmentNode;
import com.g2rain.basis.idp.sync.dto.IdpMemberNode;
import com.g2rain.basis.idp.sync.dto.IdpOrganizationSnapshot;
import com.g2rain.basis.model.IdpSyncSafetyDecision;
import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.utils.Collections;
import com.g2rain.common.utils.Strings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * IdP FULL 同步安全闸：评估是否允许 destructive reconcile。
 */
@Component(value = "idpSyncSafetyEvaluator")
@RequiredArgsConstructor
public class IdpSyncSafetyEvaluator {

    private static final String ROOT_IDP_DEPT_ID = "1";

    private final IdpSyncSafetyProperties safetyProperties;

    public IdpSyncSafetyDecision evaluate(
        IdpOrganizationSnapshot snapshot,
        IdpSyncMode syncMode,
        List<PassportIdpBindingPo> scopeBindings,
        Set<String> mappedIdpDeptIds
    ) {
        int existingBindings = scopeBindings == null ? 0 : scopeBindings.size();
        int existingMappings = mappedIdpDeptIds == null ? 0 : mappedIdpDeptIds.size();
        int plannedMemberDeletes = countPlannedMemberDeletes(scopeBindings, snapshot.getMembers());
        int plannedDeptDisables = countPlannedDeptDisables(mappedIdpDeptIds, snapshot.getDepartments());

        if (syncMode != IdpSyncMode.FULL) {
            return IdpSyncSafetyDecision.allowed(plannedMemberDeletes, plannedDeptDisables);
        }

        if (safetyProperties.isBlockIncompleteSnapshot() && snapshot != null && !snapshot.isComplete()) {
            return IdpSyncSafetyDecision.blocked(
                BasisErrorCode.TENANT_IDP_SYNC_SNAPSHOT_INCOMPLETE,
                plannedMemberDeletes,
                plannedDeptDisables
            );
        }

        if (safetyProperties.isBlockEmptySnapshotWhenExisting()) {
            boolean membersEmpty = Collections.isEmpty(snapshot == null ? null : snapshot.getMembers());
            boolean departmentsEmpty = Collections.isEmpty(snapshot == null ? null : snapshot.getDepartments());
            if ((membersEmpty && existingBindings > 0) || (departmentsEmpty && existingMappings > 0)) {
                return IdpSyncSafetyDecision.blocked(
                    BasisErrorCode.TENANT_IDP_SYNC_SNAPSHOT_EMPTY,
                    plannedMemberDeletes,
                    plannedDeptDisables
                );
            }
        }

        if (existingBindings > 0 && ratio(plannedMemberDeletes, existingBindings) > safetyProperties.getMaxMemberDeleteRatio()) {
            return IdpSyncSafetyDecision.blocked(
                BasisErrorCode.TENANT_IDP_SYNC_RECONCILE_RATIO_EXCEEDED,
                plannedMemberDeletes,
                plannedDeptDisables
            );
        }

        if (existingMappings > 0 && ratio(plannedDeptDisables, existingMappings) > safetyProperties.getMaxDepartmentDisableRatio()) {
            return IdpSyncSafetyDecision.blocked(
                BasisErrorCode.TENANT_IDP_SYNC_RECONCILE_RATIO_EXCEEDED,
                plannedMemberDeletes,
                plannedDeptDisables
            );
        }

        return IdpSyncSafetyDecision.allowed(plannedMemberDeletes, plannedDeptDisables);
    }

    public void assertAllowDestructiveReconcile(IdpSyncSafetyDecision decision) {
        if (decision == null || decision.isAllowDestructiveReconcile()) {
            return;
        }
        throw new BusinessException(decision.getBlockReason());
    }

    static int countPlannedMemberDeletes(List<PassportIdpBindingPo> scopeBindings, List<IdpMemberNode> members) {
        if (Collections.isEmpty(scopeBindings)) {
            return 0;
        }
        Set<String> snapshotKeys = IdpMemberBindingSnapshotSupport.buildSnapshotBindingKeys(members);
        int planned = 0;
        for (PassportIdpBindingPo binding : scopeBindings) {
            if (!IdpMemberBindingSnapshotSupport.isBindingInSnapshot(binding, snapshotKeys)) {
                planned++;
            }
        }
        return planned;
    }

    static int countPlannedDeptDisables(Set<String> mappedIdpDeptIds, List<IdpDepartmentNode> departments) {
        if (Collections.isEmpty(mappedIdpDeptIds)) {
            return 0;
        }
        Set<String> snapshotIds = buildSnapshotIdpDeptIds(departments);
        int planned = 0;
        for (String idpDeptId : mappedIdpDeptIds) {
            if (ROOT_IDP_DEPT_ID.equals(idpDeptId) || snapshotIds.contains(idpDeptId)) {
                continue;
            }
            planned++;
        }
        return planned;
    }

    static Set<String> buildSnapshotIdpDeptIds(List<IdpDepartmentNode> departments) {
        Set<String> snapshotIds = new HashSet<>();
        if (Collections.isEmpty(departments)) {
            return snapshotIds;
        }
        for (IdpDepartmentNode department : departments) {
            if (Strings.isNotBlank(department.getIdpDeptId())) {
                snapshotIds.add(department.getIdpDeptId().trim());
            }
        }
        return snapshotIds;
    }

    private static double ratio(int numerator, int denominator) {
        if (denominator <= 0) {
            return 0;
        }
        return (double) numerator / denominator;
    }
}
