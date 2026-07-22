package com.g2rain.basis.model;

import com.g2rain.basis.enums.BasisErrorCode;
import lombok.Getter;
import lombok.Setter;

/**
 * IdP FULL 同步安全闸评估结果。
 */
@Getter
@Setter
public class IdpSyncSafetyDecision {

    private boolean allowDestructiveReconcile = true;

    private BasisErrorCode blockReason;

    private int plannedMemberDeletes;

    private int plannedDeptDisables;

    public static IdpSyncSafetyDecision allowed(int plannedMemberDeletes, int plannedDeptDisables) {
        IdpSyncSafetyDecision decision = new IdpSyncSafetyDecision();
        decision.setAllowDestructiveReconcile(true);
        decision.setPlannedMemberDeletes(plannedMemberDeletes);
        decision.setPlannedDeptDisables(plannedDeptDisables);
        return decision;
    }

    public static IdpSyncSafetyDecision blocked(
        BasisErrorCode blockReason,
        int plannedMemberDeletes,
        int plannedDeptDisables
    ) {
        IdpSyncSafetyDecision decision = new IdpSyncSafetyDecision();
        decision.setAllowDestructiveReconcile(false);
        decision.setBlockReason(blockReason);
        decision.setPlannedMemberDeletes(plannedMemberDeletes);
        decision.setPlannedDeptDisables(plannedDeptDisables);
        return decision;
    }
}
