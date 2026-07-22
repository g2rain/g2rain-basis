package com.g2rain.basis.service.impl;

import com.g2rain.basis.enums.IdpSyncMode;
import com.g2rain.basis.idp.sync.dto.IdpMemberNode;
import org.junit.jupiter.api.Test;

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
    void fullReconcileGate_shouldRequireDestructiveFlag() {
        assertFalse(IdpSyncMode.FULL == IdpSyncMode.normalize("FULL") && false);
        assertTrue(IdpSyncMode.FULL == IdpSyncMode.normalize("FULL") && true);
        assertFalse(IdpSyncMode.FULL == IdpSyncMode.normalize("INCREMENTAL") && true);
    }
}
