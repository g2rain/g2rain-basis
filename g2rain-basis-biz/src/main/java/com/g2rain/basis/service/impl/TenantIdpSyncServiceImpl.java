package com.g2rain.basis.service.impl;

import com.g2rain.basis.client.DepartmentIdpSyncClient;
import com.g2rain.basis.client.IdpSyncClient;
import com.g2rain.basis.dao.ApplicationDao;
import com.g2rain.basis.dao.ApplicationIdpProvisionDao;
import com.g2rain.basis.dao.po.ApplicationPo;
import com.g2rain.basis.dto.ApplicationIdpProvisionSelectDto;
import com.g2rain.basis.dto.ApplicationSelectDto;
import com.g2rain.basis.dto.IdpEnterpriseOrganSelectDto;
import com.g2rain.basis.dto.TenantIdpSyncDto;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.enums.IdpBindMode;
import com.g2rain.basis.enums.IdpSyncMode;
import com.g2rain.basis.enums.IdpType;
import com.g2rain.basis.idp.sync.dto.IdpDepartmentNode;
import com.g2rain.basis.idp.sync.dto.IdpFetchSnapshotRequest;
import com.g2rain.basis.idp.sync.dto.IdpMemberNode;
import com.g2rain.basis.idp.sync.dto.IdpOrganizationSnapshot;
import com.g2rain.basis.model.TenantIdpMemberSyncResult;
import com.g2rain.basis.service.IdpEnterpriseOrganService;
import com.g2rain.basis.service.TenantIdpMemberSyncService;
import com.g2rain.basis.service.TenantIdpSyncService;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

/**
 * 租户 IdP 同步编排服务实现。
 */
@Service(value = "tenantIdpSyncServiceImpl")
public class TenantIdpSyncServiceImpl implements TenantIdpSyncService {

    @Resource(name = "idpEnterpriseOrganServiceImpl")
    private IdpEnterpriseOrganService idpEnterpriseOrganService;

    @Resource(name = "applicationDao")
    private ApplicationDao applicationDao;

    @Resource(name = "applicationIdpProvisionDao")
    private ApplicationIdpProvisionDao applicationIdpProvisionDao;

    @Resource(name = "tenantIdpMemberSyncServiceImpl")
    private TenantIdpMemberSyncService tenantIdpMemberSyncService;

    @Resource
    private IdpSyncClient idpSyncClient;

    @Resource
    private DepartmentIdpSyncClient departmentIdpSyncClient;

    @Override
    public TenantIdpSyncResultVo sync(TenantIdpSyncDto dto) {
        long startedAt = System.currentTimeMillis();
        validatePermission(dto.getOrganId());

        String idpType = normalizeIdpType(dto.getIdpType());
        String bindMode = normalizeBindMode(dto.getBindMode());
        String syncMode = normalizeSyncMode(dto.getSyncMode());
        SyncContext context = resolveSyncContext(dto, idpType);

        IdpOrganizationSnapshot snapshot = fetchSnapshot(context, bindMode);
        TenantIdpMemberSyncResult memberResult = tenantIdpMemberSyncService.syncMembers(
            dto.getOrganId(),
            idpType,
            bindMode,
            context.idpApplicationCode(),
            context.corpId(),
            syncMode,
            snapshot.getMembers()
        );
        DepartmentIdpSyncResultVo departmentResult = syncDepartments(
            dto.getOrganId(),
            idpType,
            syncMode,
            snapshot.getDepartments(),
            snapshot.getMembers(),
            memberResult
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
        result.setElapsedMs(System.currentTimeMillis() - startedAt);
        return result;
    }

    private void validatePermission(Long organId) {
        Asserts.isTrue(PrincipalContextHolder.isAdminUser(), BasisErrorCode.TENANT_IDP_SYNC_FORBIDDEN);
        Long currentOrganId = PrincipalContextHolder.getOrganId();
        Asserts.isTrue(Objects.equals(currentOrganId, organId),
            SystemErrorCode.PARAM_VAL_INVALID, "organId");
    }

    private SyncContext resolveSyncContext(TenantIdpSyncDto dto, String idpType) {
        ApplicationPo application = resolveApplication(dto.getApplicationCode());
        ApplicationIdpProvisionSelectDto provisionQuery = new ApplicationIdpProvisionSelectDto();
        provisionQuery.setApplicationId(application.getId());
        provisionQuery.setIdpType(idpType);
        var provisions = applicationIdpProvisionDao.selectList(provisionQuery);
        Asserts.isTrue(Collections.isNotEmpty(provisions),
            BasisErrorCode.TENANT_IDP_SYNC_NOT_BOUND, dto.getApplicationCode());
        String idpApplicationCode = provisions.getFirst().getIdpApplicationCode();

        IdpEnterpriseOrganSelectDto enterpriseQuery = new IdpEnterpriseOrganSelectDto();
        enterpriseQuery.setOrganId(dto.getOrganId());
        enterpriseQuery.setIdpType(idpType);
        enterpriseQuery.setStatus("ACTIVE");
        List<IdpEnterpriseOrganVo> enterpriseOrgans = idpEnterpriseOrganService.selectList(enterpriseQuery);
        Asserts.isTrue(Collections.isNotEmpty(enterpriseOrgans),
            BasisErrorCode.TENANT_IDP_SYNC_NOT_BOUND, dto.getOrganId());
        String corpId = enterpriseOrgans.getFirst().getEnterpriseId();
        Asserts.isTrue(Strings.isNotBlank(corpId), BasisErrorCode.TENANT_IDP_SYNC_NOT_BOUND, "corpId");
        return new SyncContext(idpApplicationCode, corpId.trim());
    }

    private ApplicationPo resolveApplication(String applicationCode) {
        ApplicationSelectDto selectDto = new ApplicationSelectDto();
        selectDto.setApplicationCode(applicationCode);
        List<ApplicationPo> applications = applicationDao.selectList(selectDto);
        Asserts.isTrue(Collections.isNotEmpty(applications),
            SystemErrorCode.PARAM_VAL_INVALID, applicationCode);
        return applications.getFirst();
    }

    private IdpOrganizationSnapshot fetchSnapshot(SyncContext context, String bindMode) {
        IdpFetchSnapshotRequest request = new IdpFetchSnapshotRequest();
        request.setCorpId(context.corpId());
        request.setBindMode(bindMode);
        request.setIdpApplicationCode(context.idpApplicationCode());
        Result<IdpOrganizationSnapshot> result;
        try {
            result = idpSyncClient.fetchDingTalkSnapshot(request);
        } catch (Exception ex) {
            throw new BusinessException(BasisErrorCode.TENANT_IDP_SYNC_IDP_FETCH_FAILED);
        }
        if (!result.isSuccess()) {
            throw ExceptionConverter.of(result);
        }
        return result.getData() == null ? new IdpOrganizationSnapshot() : result.getData();
    }

    private DepartmentIdpSyncResultVo syncDepartments(
        Long organId,
        String idpType,
        String syncMode,
        List<IdpDepartmentNode> departments,
        List<IdpMemberNode> members,
        TenantIdpMemberSyncResult memberResult
    ) {
        DepartmentIdpSyncDto syncDto = new DepartmentIdpSyncDto();
        syncDto.setOrganId(organId);
        syncDto.setIdpType(idpType);
        syncDto.setSyncMode(syncMode);
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

    private static String normalizeBindMode(String bindMode) {
        String normalized = Strings.isBlank(bindMode) ? IdpBindMode.INTERNAL.name() : bindMode.trim();
        IdpBindMode.validate(normalized);
        return normalized;
    }

    private static String normalizeSyncMode(String syncMode) {
        return IdpSyncMode.normalize(syncMode).name();
    }

    private record SyncContext(String idpApplicationCode, String corpId) {
    }
}
