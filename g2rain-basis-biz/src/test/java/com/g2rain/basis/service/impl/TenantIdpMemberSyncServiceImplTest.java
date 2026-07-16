package com.g2rain.basis.service.impl;

import com.g2rain.basis.dao.po.PassportIdpBindingPo;
import com.g2rain.basis.idp.sync.dto.IdpMemberNode;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TenantIdpMemberSyncServiceImplTest {

    @Test
    void needsBindingIdpUserIdUpdate_shouldUpdateWhenExistingIsNull() {
        assertTrue(TenantIdpMemberSyncServiceImpl.needsBindingIdpUserIdUpdate(null, "userid-1"));
    }

    @Test
    void needsBindingIdpUserIdUpdate_shouldSkipWhenSnapshotIsBlank() {
        assertFalse(TenantIdpMemberSyncServiceImpl.needsBindingIdpUserIdUpdate(null, null));
        assertFalse(TenantIdpMemberSyncServiceImpl.needsBindingIdpUserIdUpdate(null, "  "));
        assertFalse(TenantIdpMemberSyncServiceImpl.needsBindingIdpUserIdUpdate("userid-1", null));
    }

    @Test
    void needsBindingIdpUserIdUpdate_shouldSkipWhenValueUnchanged() {
        assertFalse(TenantIdpMemberSyncServiceImpl.needsBindingIdpUserIdUpdate("userid-1", "userid-1"));
        assertFalse(TenantIdpMemberSyncServiceImpl.needsBindingIdpUserIdUpdate("userid-1", "  userid-1  "));
    }

    @Test
    void needsBindingIdpUserIdUpdate_shouldUpdateWhenValueChanged() {
        assertTrue(TenantIdpMemberSyncServiceImpl.needsBindingIdpUserIdUpdate("userid-1", "userid-2"));
    }

    @Test
    void mergeMemberSnapshot_shouldFillMissingFieldsFromRemote() {
        IdpMemberNode target = new IdpMemberNode();
        target.setIdpUserId("userid-1");

        IdpMemberNode remote = new IdpMemberNode();
        remote.setUnionId("union-1");
        remote.setIdpUserId("userid-1");
        remote.setName("张三");
        remote.setMobile("13800000000");
        remote.setEmail("zhang@example.com");

        TenantIdpMemberSyncServiceImpl.mergeMemberSnapshot(target, remote);

        assertEquals("union-1", target.getUnionId());
        assertEquals("userid-1", target.getIdpUserId());
        assertEquals("张三", target.getName());
        assertEquals("13800000000", target.getMobile());
        assertEquals("zhang@example.com", target.getEmail());
    }

    @Test
    void mergeMemberSnapshot_shouldNotOverwriteExistingProfileFields() {
        IdpMemberNode target = new IdpMemberNode();
        target.setUnionId("union-1");
        target.setName("已有姓名");

        IdpMemberNode remote = new IdpMemberNode();
        remote.setUnionId("union-2");
        remote.setName("远程姓名");

        TenantIdpMemberSyncServiceImpl.mergeMemberSnapshot(target, remote);

        assertEquals("union-2", target.getUnionId());
        assertEquals("已有姓名", target.getName());
    }

    @Test
    void buildSnapshotBindingKeys_shouldCollectUnionIdAndIdpUserId() {
        IdpMemberNode member = new IdpMemberNode();
        member.setUnionId(" union-1 ");
        member.setIdpUserId("userid-1");

        Set<String> keys = TenantIdpMemberSyncServiceImpl.buildSnapshotBindingKeys(List.of(member));

        assertEquals(Set.of("subject:union-1", "userId:userid-1"), keys);
    }

    @Test
    void buildSnapshotBindingKeys_shouldSkipBlankIdentifiers() {
        IdpMemberNode blank = new IdpMemberNode();
        IdpMemberNode valid = new IdpMemberNode();
        valid.setUnionId("union-1");

        Set<String> keys = TenantIdpMemberSyncServiceImpl.buildSnapshotBindingKeys(List.of(blank, valid));

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

        assertTrue(TenantIdpMemberSyncServiceImpl.isBindingInSnapshot(bySubject, keys));
        assertTrue(TenantIdpMemberSyncServiceImpl.isBindingInSnapshot(byUserId, keys));
        assertFalse(TenantIdpMemberSyncServiceImpl.isBindingInSnapshot(missing, keys));
    }
}
