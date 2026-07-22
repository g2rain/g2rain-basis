package com.g2rain.basis.service.idp;

import com.g2rain.basis.dao.po.PassportIdpBindingPo;
import com.g2rain.basis.idp.sync.dto.IdpMemberNode;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IdpMemberBindingSnapshotSupportTest {

    @Test
    void buildSnapshotBindingKeys_shouldCollectUnionIdAndIdpUserId() {
        IdpMemberNode member = new IdpMemberNode();
        member.setUnionId(" union-1 ");
        member.setIdpUserId("userid-1");

        Set<String> keys = IdpMemberBindingSnapshotSupport.buildSnapshotBindingKeys(List.of(member));

        assertEquals(Set.of("subject:union-1", "userId:userid-1"), keys);
    }

    @Test
    void buildSnapshotBindingKeys_shouldSkipBlankIdentifiers() {
        IdpMemberNode blank = new IdpMemberNode();
        IdpMemberNode valid = new IdpMemberNode();
        valid.setUnionId("union-1");

        Set<String> keys = IdpMemberBindingSnapshotSupport.buildSnapshotBindingKeys(List.of(blank, valid));

        assertEquals(Set.of("subject:union-1"), keys);
    }

    @Test
    void isBindingInSnapshot_shouldMatchBySubjectOrUserId() {
        Set<String> keys = Set.of("subject:union-1", "userId:userid-2");

        PassportIdpBindingPo bySubject = new PassportIdpBindingPo();
        bySubject.setIdpSubject("union-1");
        PassportIdpBindingPo byUserId = new PassportIdpBindingPo();
        byUserId.setIdpUserId("userid-2");
        PassportIdpBindingPo missing = new PassportIdpBindingPo();
        missing.setIdpSubject("union-x");

        assertTrue(IdpMemberBindingSnapshotSupport.isBindingInSnapshot(bySubject, keys));
        assertTrue(IdpMemberBindingSnapshotSupport.isBindingInSnapshot(byUserId, keys));
        assertFalse(IdpMemberBindingSnapshotSupport.isBindingInSnapshot(missing, keys));
    }
}
