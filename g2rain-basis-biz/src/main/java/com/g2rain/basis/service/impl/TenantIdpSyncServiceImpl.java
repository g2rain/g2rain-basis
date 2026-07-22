package com.g2rain.basis.service.impl;

import com.g2rain.basis.client.DepartmentIdpSyncClient;
import com.g2rain.basis.client.IdpSyncClient;
import com.g2rain.basis.dao.PassportIdpBindingDao;
import com.g2rain.basis.dao.RoleDao;
import com.g2rain.basis.dao.po.PassportIdpBindingPo;
import com.g2rain.basis.dao.po.RolePo;
import com.g2rain.basis.dto.IdpEnterpriseOrganSelectDto;
import com.g2rain.basis.dto.PassportIdpBindingSelectDto;
import com.g2rain.basis.dto.RoleAssignUsersDto;
import com.g2rain.basis.dto.TenantIdpSyncDto;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.enums.IdpBindMode;
import com.g2rain.basis.enums.IdpSyncMode;
import com.g2rain.basis.enums.IdpType;
import com.g2rain.basis.idp.sync.dto.IdpDepartmentNode;
import com.g2rain.basis.idp.sync.dto.IdpFetchSnapshotRequest;
import com.g2rain.basis.idp.sync.dto.IdpMemberNode;
import com.g2rain.basis.idp.sync.dto.IdpOrganizationSnapshot;
import com.g2rain.basis.model.IdpSyncSafetyDecision;
import com.g2rain.basis.model.TenantIdpMemberSyncResult;
import com.g2rain.basis.service.IdpEnterpriseOrganService;
import com.g2rain.basis.service.TenantIdpMemberSyncService;
import com.g2rain.basis.service.TenantIdpSyncService;
import com.g2rain.basis.service.UserRoleRelationService;
import com.g2rain.basis.service.idp.IdpSyncSafetyEvaluator;
import com.g2rain.basis.vo.IdpEnterpriseOrganVo;
import com.g2rain.basis.vo.TenantIdpSyncResultVo;
import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.ExceptionConverter;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.model.Result;
import com.g2rain.common.utils.Asserts;
import com.g2rain.common.utils.Collections;
import com.g2rain.common.utils.Strings;
import com.g2rain.common.web.PrincipalContextHolder;
import com.g2rain.department.dto.DepartmentIdpSyncDepartmentNode;
import com.g2rain.department.dto.DepartmentIdpSyncDto;
import com.g2rain.department.dto.DepartmentIdpSyncMemberDepartment;
import com.g2rain.department.vo.DepartmentIdpSyncResultVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 租户 IdP 同步编排服务实现。
 */
@Service(value = "tenantIdpSyncServiceImpl")
public class TenantIdpSyncServiceImpl implements TenantIdpSyncService {

    @Resource(name = "idpEnterpriseOrganServiceImpl")
    private IdpEnterpriseOrganService idpEnterpriseOrganService;

    @Resource(name = "tenantIdpMemberSyncServiceImpl")
    private TenantIdpMemberSyncService tenantIdpMemberSyncService;

    @Resource
    private IdpSyncClient idpSyncClient;

    @Resource
    private DepartmentIdpSyncClient departmentIdpSyncClient;

    @Resource(name = "roleDao")
    private RoleDao roleDao;

    @Resource(name = "userRoleRelationServiceImpl")
    private UserRoleRelationService userRoleRelationService;

    @Resource(name = "passportIdpBindingDao")
    private PassportIdpBindingDao passportIdpBindingDao;

    @Resource(name = "idpSyncSafetyEvaluator")
    private IdpSyncSafetyEvaluator idpSyncSafetyEvaluator;

    @Override
    public TenantIdpSyncResultVo sync(TenantIdpSyncDto dto) {
        long startedAt = System.currentTimeMillis();
        validatePermission(dto.getOrganId());

        String idpType = normalizeIdpType(dto.getIdpType());
        assertSyncIdpTypeSupported(idpType);
        IdpSyncMode syncMode = IdpSyncMode.normalize(dto.getSyncMode());
        EnterpriseSyncContext enterpriseContext = resolveEnterpriseContext(dto, idpType);
        String bindMode = enterpriseContext.bindMode();

        IdpOrganizationSnapshot snapshot = fetchSnapshot(enterpriseContext.corpId(), bindMode);
        String idpApplicationCode = normalizeIdpApplicationCode(snapshot.getIdpApplicationCode());

        List<PassportIdpBindingPo> scopeBindings = loadScopeBindings(
            idpType, bindMode, idpApplicationCode, enterpriseContext.corpId());
        Set<String> mappedIdpDeptIds = loadMappedIdpDeptIds(dto.getOrganId(), idpType);
        IdpSyncSafetyDecision safetyDecision = idpSyncSafetyEvaluator.evaluate(
            snapshot, syncMode, scopeBindings, mappedIdpDeptIds);
        idpSyncSafetyEvaluator.assertAllowDestructiveReconcile(safetyDecision);
        boolean enableDestructiveReconcile = safetyDecision.isAllowDestructiveReconcile();

        TenantIdpMemberSyncResult memberResult = tenantIdpMemberSyncService.syncMembers(
            dto.getOrganId(),
            idpType,
            bindMode,
            idpApplicationCode,
            enterpriseContext.corpId(),
            syncMode.name(),
            snapshot.getMembers(),
            enableDestructiveReconcile
        );
        int rolesAssigned = assignRoleToSyncedMembers(dto.getOrganId(), dto.getRoleId(), memberResult);
        DepartmentIdpSyncResultVo departmentResult = syncDepartments(
            dto.getOrganId(),
            idpType,
            syncMode.name(),
            snapshot.getDepartments(),
            snapshot.getMembers(),
            memberResult,
            enableDestructiveReconcile
        );

        TenantIdpSyncResultVo result = new TenantIdpSyncResultVo();
        result.setDepartmentsCreated(departmentResult.getDepartmentsCreated());
        result.setDepartmentsUpdated(departmentResult.getDepartmentsUpdated());
        result.setMembersCreated(memberResult.getMembersCreated());
        result.setMembersUpdated(memberResult.getMembersUpdated());
        result.setBindingsCreated(memberResult.getBindingsCreated());
        result.setBindingsUpdated(memberResult.getBindingsUpdated());
        result.setDepartmentRelationsCreated(departmentResult.getRelationsCreated());
        result.setMembersDeleted(memberResult.getMembersDeleted());
        result.setBindingsDeleted(memberResult.getBindingsDeleted());
        result.setDepartmentsDisabled(departmentResult.getDepartmentsDisabled());
        result.setDepartmentRelationsRemoved(departmentResult.getRelationsRemoved());
        result.setRolesAssigned(rolesAssigned);
        result.setElapsedMs(System.currentTimeMillis() - startedAt);
        return result;
    }

    private int assignRoleToSyncedMembers(
        Long organId,
        Long roleId,
        TenantIdpMemberSyncResult memberResult
    ) {
        validateRoleForOrgan(organId, roleId);
        Set<Long> userIds = new HashSet<>(memberResult.getUnionIdToUserId().values());
        if (userIds.isEmpty()) {
            return 0;
        }
        RoleAssignUsersDto assignDto = new RoleAssignUsersDto();
        assignDto.setRoleId(roleId);
        assignDto.setUserIds(userIds);
        Integer assigned = userRoleRelationService.assignUsers(assignDto);
        return assigned == null ? 0 : assigned;
    }

    private void validateRoleForOrgan(Long organId, Long roleId) {
        validateRoleBelongsToOrgan(organId, roleId, roleDao.selectById(roleId));
    }

    static void validateRoleBelongsToOrgan(Long organId, Long roleId, RolePo role) {
        Asserts.notNull(roleId, SystemErrorCode.PARAM_VAL_INVALID, "roleId");
        Asserts.notNull(role, SystemErrorCode.PARAM_VAL_INVALID, "roleId");
        Asserts.isTrue(Objects.equals(role.getOrganId(), organId),
            SystemErrorCode.PARAM_VAL_INVALID, "roleId");
    }

    private void validatePermission(Long organId) {
        Asserts.isTrue(PrincipalContextHolder.isAdminUser(), BasisErrorCode.TENANT_IDP_SYNC_FORBIDDEN);
        Long currentOrganId = PrincipalContextHolder.getOrganId();
        Asserts.isTrue(Objects.equals(currentOrganId, organId),
            SystemErrorCode.PARAM_VAL_INVALID, "organId");
    }

    private EnterpriseSyncContext resolveEnterpriseContext(TenantIdpSyncDto dto, String idpType) {
        IdpEnterpriseOrganSelectDto enterpriseQuery = new IdpEnterpriseOrganSelectDto();
        enterpriseQuery.setOrganId(dto.getOrganId());
        enterpriseQuery.setIdpType(idpType);
        enterpriseQuery.setStatus("ACTIVE");
        if (Strings.isNotBlank(dto.getBindMode())) {
            String requestedBindMode = normalizeBindMode(dto.getBindMode());
            enterpriseQuery.setBindMode(requestedBindMode);
        }
        List<IdpEnterpriseOrganVo> enterpriseOrgans = idpEnterpriseOrganService.selectList(enterpriseQuery);
        Asserts.isTrue(Collections.isNotEmpty(enterpriseOrgans),
            BasisErrorCode.TENANT_IDP_SYNC_NOT_BOUND, dto.getOrganId());
        IdpEnterpriseOrganVo enterpriseOrgan = enterpriseOrgans.getFirst();
        String corpId = enterpriseOrgan.getEnterpriseId();
        Asserts.isTrue(Strings.isNotBlank(corpId), BasisErrorCode.TENANT_IDP_SYNC_NOT_BOUND, "corpId");
        String bindMode = normalizeBindMode(enterpriseOrgan.getBindMode());
        if (Strings.isNotBlank(dto.getBindMode())) {
            String requestedBindMode = normalizeBindMode(dto.getBindMode());
            Asserts.isTrue(Objects.equals(requestedBindMode, bindMode),
                SystemErrorCode.PARAM_VAL_INVALID, "bindMode");
        }
        return new EnterpriseSyncContext(bindMode, corpId.trim());
    }

    private IdpOrganizationSnapshot fetchSnapshot(String corpId, String bindMode) {
        IdpFetchSnapshotRequest request = new IdpFetchSnapshotRequest();
        request.setCorpId(corpId);
        request.setBindMode(bindMode);
        Result<IdpOrganizationSnapshot> result;
        try {
            result = idpSyncClient.fetchDingTalkSnapshot(request);
        } catch (Exception ex) {
            throw new BusinessException(BasisErrorCode.TENANT_IDP_SYNC_IDP_FETCH_FAILED);
        }
        if (!result.isSuccess()) {
            throw ExceptionConverter.of(result);
        }
        if (result.getData() == null) {
            throw new BusinessException(BasisErrorCode.TENANT_IDP_SYNC_IDP_FETCH_FAILED);
        }
        return result.getData();
    }

    private List<PassportIdpBindingPo> loadScopeBindings(
        String idpType,
        String bindMode,
        String idpApplicationCode,
        String corpId
    ) {
        PassportIdpBindingSelectDto query = new PassportIdpBindingSelectDto();
        query.setIdpType(idpType);
        query.setIdpApplicationCode(idpApplicationCode);
        query.setCorpId(corpId);
        query.setBindMode(bindMode);
        return passportIdpBindingDao.selectListWithoutIsolation(query);
    }

    private Set<String> loadMappedIdpDeptIds(Long organId, String idpType) {
        Result<List<String>> result;
        try {
            result = departmentIdpSyncClient.listMappedIdpDeptIds(organId, idpType);
        } catch (Exception ex) {
            throw new BusinessException(BasisErrorCode.TENANT_IDP_SYNC_DEPARTMENT_FAILED);
        }
        if (!result.isSuccess()) {
            throw ExceptionConverter.of(result);
        }
        List<String> ids = result.getData();
        if (ids == null || ids.isEmpty()) {
            return Set.of();
        }
        return new HashSet<>(ids);
    }

    private DepartmentIdpSyncResultVo syncDepartments(
        Long organId,
        String idpType,
        String syncMode,
        List<IdpDepartmentNode> departments,
        List<IdpMemberNode> members,
        TenantIdpMemberSyncResult memberResult,
        boolean enableDestructiveReconcile
    ) {
        DepartmentIdpSyncDto syncDto = new DepartmentIdpSyncDto();
        syncDto.setOrganId(organId);
        syncDto.setIdpType(idpType);
        syncDto.setSyncMode(syncMode);
        syncDto.setEnableDestructiveReconcile(enableDestructiveReconcile);
        syncDto.setDepartments(convertDepartments(departments));
        syncDto.setMemberDepartments(convertMemberDepartments(members, memberResult));
        Result<DepartmentIdpSyncResultVo> result;
        try {
            result = departmentIdpSyncClient.sync(syncDto);
        } catch (Exception ex) {
            throw new BusinessException(BasisErrorCode.TENANT_IDP_SYNC_DEPARTMENT_FAILED);
        }
        if (!result.isSuccess()) {
            throw ExceptionConverter.of(result);
        }
        return result.getData() == null ? new DepartmentIdpSyncResultVo() : result.getData();
    }

    private static List<DepartmentIdpSyncDepartmentNode> convertDepartments(List<IdpDepartmentNode> departments) {
        List<DepartmentIdpSyncDepartmentNode> nodes = new ArrayList<>();
        if (Collections.isEmpty(departments)) {
            return nodes;
        }
        for (IdpDepartmentNode department : departments) {
            DepartmentIdpSyncDepartmentNode node = new DepartmentIdpSyncDepartmentNode();
            node.setIdpDeptId(department.getIdpDeptId());
            node.setParentIdpDeptId(department.getParentIdpDeptId());
            node.setDeptName(department.getName());
            node.setSortOrder(department.getSortOrder());
            nodes.add(node);
        }
        return nodes;
    }

    private static List<DepartmentIdpSyncMemberDepartment> convertMemberDepartments(
        List<IdpMemberNode> members,
        TenantIdpMemberSyncResult memberResult
    ) {
        List<DepartmentIdpSyncMemberDepartment> relations = new ArrayList<>();
        if (Collections.isEmpty(members)) {
            return relations;
        }
        for (IdpMemberNode member : members) {
            Long userId = memberResult.getUnionIdToUserId().get(member.getUnionId());
            if (userId == null) {
                continue;
            }
            DepartmentIdpSyncMemberDepartment relation = new DepartmentIdpSyncMemberDepartment();
            relation.setUserId(userId);
            relation.setIdpDeptIds(new LinkedHashSet<>(member.getDeptIdpDeptIds()));
            relations.add(relation);
        }
        return relations;
    }

    private static String normalizeIdpType(String idpType) {
        String normalized = Strings.isBlank(idpType) ? IdpType.DINGTALK.name() : idpType.trim();
        Asserts.isTrue(IdpType.nameOf(normalized) != null, SystemErrorCode.PARAM_VAL_INVALID, "idpType");
        return normalized;
    }

    static void assertSyncIdpTypeSupported(String idpType) {
        Asserts.isTrue(Objects.equals(IdpType.DINGTALK.name(), idpType),
            BasisErrorCode.TENANT_IDP_SYNC_IDP_TYPE_UNSUPPORTED);
    }

    private static String normalizeBindMode(String bindMode) {
        String normalized = Strings.isBlank(bindMode) ? IdpBindMode.INTERNAL.name() : bindMode.trim();
        IdpBindMode.validate(normalized);
        return normalized;
    }

    private static String normalizeSyncMode(String syncMode) {
        return IdpSyncMode.normalize(syncMode).name();
    }

    private static String normalizeIdpApplicationCode(String idpApplicationCode) {
        Asserts.isTrue(Strings.isNotBlank(idpApplicationCode),
            BasisErrorCode.TENANT_IDP_SYNC_IDP_FETCH_FAILED);
        return idpApplicationCode.trim();
    }

    private record EnterpriseSyncContext(String bindMode, String corpId) {
    }
}
