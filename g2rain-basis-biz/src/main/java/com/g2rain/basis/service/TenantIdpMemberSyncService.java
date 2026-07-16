package com.g2rain.basis.service;

import com.g2rain.basis.idp.sync.dto.IdpMemberNode;
import com.g2rain.basis.model.TenantIdpMemberSyncResult;

import java.util.List;

/**
 * 租户 IdP 成员同步服务。
 */
public interface TenantIdpMemberSyncService {

    TenantIdpMemberSyncResult syncMembers(
        Long organId,
        String idpType,
        String bindMode,
        String idpApplicationCode,
        String corpId,
        String syncMode,
        List<IdpMemberNode> members
    );
}
