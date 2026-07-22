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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IdpSyncSafetyEvaluatorTest {

    private IdpSyncSafetyEvaluator evaluator;
    private IdpSyncSafetyProperties properties;

    @BeforeEach
    void setUp() {
        properties = new IdpSyncSafetyProperties();
        evaluator = new IdpSyncSafetyEvaluator(properties);
    }

    @Test
    void evaluate_shouldAllowIncrementalRegardlessOfSnapshot() {
        IdpOrganizationSnapshot snapshot = incompleteEmptySnapshot();
        IdpSyncSafetyDecision decision = evaluator.evaluate(
            snapshot,
            IdpSyncMode.INCREMENTAL,
            List.of(binding("union-1")),
            Set.of("2")
        );
        assertTrue(decision.isAllowDestructiveReconcile());
    }

    @Test
    void evaluate_shouldBlockIncompleteSnapshotOnFull() {
        IdpSyncSafetyDecision decision = evaluator.evaluate(
            incompleteEmptySnapshot(),
            IdpSyncMode.FULL,
            List.of(),
            Set.of()
        );
        assertEquals(BasisErrorCode.TENANT_IDP_SYNC_SNAPSHOT_INCOMPLETE, decision.getBlockReason());
    }

    @Test
    void evaluate_shouldBlockEmptySnapshotWhenBindingsExist() {
        IdpOrganizationSnapshot snapshot = completeSnapshot(List.of(), List.of());
        IdpSyncSafetyDecision decision = evaluator.evaluate(
            snapshot,
            IdpSyncMode.FULL,
            List.of(binding("union-1")),
            Set.of()
        );
        assertEquals(BasisErrorCode.TENANT_IDP_SYNC_SNAPSHOT_EMPTY, decision.getBlockReason());
    }

    @Test
    void evaluate_shouldBlockWhenMemberDeleteRatioExceeded() {
        IdpMemberNode member = new IdpMemberNode();
        member.setUnionId("union-keep");
        IdpOrganizationSnapshot snapshot = completeSnapshot(List.of(dept("2")), List.of(member));

        PassportIdpBindingPo keep = binding("union-keep");
        PassportIdpBindingPo drop1 = binding("union-drop-1");
        PassportIdpBindingPo drop2 = binding("union-drop-2");
        PassportIdpBindingPo drop3 = binding("union-drop-3");
        PassportIdpBindingPo drop4 = binding("union-drop-4");

        IdpSyncSafetyDecision decision = evaluator.evaluate(
            snapshot,
            IdpSyncMode.FULL,
            List.of(keep, drop1, drop2, drop3, drop4),
            Set.of("2")
        );
        assertEquals(BasisErrorCode.TENANT_IDP_SYNC_RECONCILE_RATIO_EXCEEDED, decision.getBlockReason());
        assertEquals(4, decision.getPlannedMemberDeletes());
    }

    @Test
    void evaluate_shouldAllowSafeFullSnapshot() {
        IdpMemberNode member = new IdpMemberNode();
        member.setUnionId("union-1");
        IdpOrganizationSnapshot snapshot = completeSnapshot(List.of(dept("2")), List.of(member));

        IdpSyncSafetyDecision decision = evaluator.evaluate(
            snapshot,
            IdpSyncMode.FULL,
            List.of(binding("union-1")),
            Set.of("2")
        );
        assertTrue(decision.isAllowDestructiveReconcile());
    }

    @Test
    void assertAllowDestructiveReconcile_shouldThrowWhenBlocked() {
        IdpSyncSafetyDecision decision = IdpSyncSafetyDecision.blocked(
            BasisErrorCode.TENANT_IDP_SYNC_SNAPSHOT_INCOMPLETE, 0, 0);
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> evaluator.assertAllowDestructiveReconcile(decision)
        );
        assertEquals(BasisErrorCode.TENANT_IDP_SYNC_SNAPSHOT_INCOMPLETE.code(), exception.getErrorCode());
    }

    private static IdpOrganizationSnapshot incompleteEmptySnapshot() {
        IdpOrganizationSnapshot snapshot = new IdpOrganizationSnapshot();
        snapshot.setComplete(false);
        return snapshot;
    }

    private static IdpOrganizationSnapshot completeSnapshot(
        List<IdpDepartmentNode> departments,
        List<IdpMemberNode> members
    ) {
        IdpOrganizationSnapshot snapshot = new IdpOrganizationSnapshot();
        snapshot.setComplete(true);
        snapshot.setDepartments(departments);
        snapshot.setMembers(members);
        return snapshot;
    }

    private static IdpDepartmentNode dept(String id) {
        IdpDepartmentNode node = new IdpDepartmentNode();
        node.setIdpDeptId(id);
        return node;
    }

    private static PassportIdpBindingPo binding(String subject) {
        PassportIdpBindingPo binding = new PassportIdpBindingPo();
        binding.setIdpSubject(subject);
        return binding;
    }
}
