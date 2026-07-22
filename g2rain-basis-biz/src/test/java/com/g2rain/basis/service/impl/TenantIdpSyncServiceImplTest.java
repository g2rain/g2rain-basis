package com.g2rain.basis.service.impl;

import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.enums.IdpSyncMode;
import com.g2rain.basis.dao.po.RolePo;
import com.g2rain.basis.idp.sync.dto.IdpDepartmentNode;
import com.g2rain.basis.idp.sync.dto.IdpMemberNode;
import com.g2rain.basis.model.TenantIdpMemberSyncResult;
import com.g2rain.common.exception.BusinessException;
import com.g2rain.department.dto.DepartmentIdpSyncDepartmentNode;
import com.g2rain.department.dto.DepartmentIdpSyncMemberDepartment;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TenantIdpSyncServiceImplTest {

    @Test
    void normalizeSyncMode_shouldDefaultToFull() throws Exception {
        var method = TenantIdpSyncServiceImpl.class.getDeclaredMethod("normalizeSyncMode", String.class);
        method.setAccessible(true);

        assertEquals(IdpSyncMode.FULL.name(), method.invoke(null, new Object[] {null}));
        assertEquals(IdpSyncMode.FULL.name(), method.invoke(null, "FULL"));
        assertEquals(IdpSyncMode.INCREMENTAL.name(), method.invoke(null, "INCREMENTAL"));
    }

    @Test
    void convertMemberDepartments_shouldMapUnionIdToPlatformDepartments() throws Exception {
        IdpMemberNode member = new IdpMemberNode();
        member.setUnionId("union-1");
        member.setDeptIdpDeptIds(List.of("2", "3"));

        TenantIdpMemberSyncResult memberResult = new TenantIdpMemberSyncResult();
        memberResult.getUnionIdToUserId().put("union-1", 100L);

        Method method = TenantIdpSyncServiceImpl.class.getDeclaredMethod(
            "convertMemberDepartments", List.class, TenantIdpMemberSyncResult.class);
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<DepartmentIdpSyncMemberDepartment> relations = (List<DepartmentIdpSyncMemberDepartment>) method.invoke(
            null, List.of(member), memberResult);

        assertEquals(1, relations.size());
        assertEquals(100L, relations.getFirst().getUserId());
        assertEquals(Set.of("2", "3"), relations.getFirst().getIdpDeptIds());
    }

    @Test
    void convertDepartments_shouldCopyIdpFields() throws Exception {
        IdpDepartmentNode department = new IdpDepartmentNode();
        department.setIdpDeptId("2");
        department.setParentIdpDeptId("1");
        department.setName("研发部");
        department.setSortOrder(10);

        Method method = TenantIdpSyncServiceImpl.class.getDeclaredMethod(
            "convertDepartments", List.class);
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<DepartmentIdpSyncDepartmentNode> nodes = (List<DepartmentIdpSyncDepartmentNode>) method.invoke(
            null, List.of(department));

        assertEquals(1, nodes.size());
        assertEquals("2", nodes.getFirst().getIdpDeptId());
        assertEquals("1", nodes.getFirst().getParentIdpDeptId());
        assertEquals("研发部", nodes.getFirst().getDeptName());
        assertEquals(10, nodes.getFirst().getSortOrder());
    }

    @Test
    void assertSyncIdpTypeSupported_shouldAcceptDingTalk() {
        TenantIdpSyncServiceImpl.assertSyncIdpTypeSupported("DINGTALK");
    }

    @Test
    void assertSyncIdpTypeSupported_shouldRejectFeishu() {
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> TenantIdpSyncServiceImpl.assertSyncIdpTypeSupported("FEISHU")
        );
        assertEquals(BasisErrorCode.TENANT_IDP_SYNC_IDP_TYPE_UNSUPPORTED.code(), exception.getErrorCode());
    }

    @Test
    void assertSyncIdpTypeSupported_shouldRejectWechatWork() {
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> TenantIdpSyncServiceImpl.assertSyncIdpTypeSupported("WECHAT_WORK")
        );
        assertEquals(BasisErrorCode.TENANT_IDP_SYNC_IDP_TYPE_UNSUPPORTED.code(), exception.getErrorCode());
    }

    @Test
    void validateRoleBelongsToOrgan_shouldPassWhenOrganMatches() {
        RolePo role = new RolePo();
        role.setId(10L);
        role.setOrganId(100L);
        TenantIdpSyncServiceImpl.validateRoleBelongsToOrgan(100L, 10L, role);
    }

    @Test
    void validateRoleBelongsToOrgan_shouldFailWhenRoleMissing() {
        assertThrows(BusinessException.class,
            () -> TenantIdpSyncServiceImpl.validateRoleBelongsToOrgan(100L, 10L, null));
    }

    @Test
    void validateRoleBelongsToOrgan_shouldFailWhenOrganMismatch() {
        RolePo role = new RolePo();
        role.setId(10L);
        role.setOrganId(200L);
        assertThrows(BusinessException.class,
            () -> TenantIdpSyncServiceImpl.validateRoleBelongsToOrgan(100L, 10L, role));
    }
}
