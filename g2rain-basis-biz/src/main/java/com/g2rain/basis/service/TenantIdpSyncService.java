package com.g2rain.basis.service;

import com.g2rain.basis.dto.TenantIdpSyncDto;
import com.g2rain.basis.vo.TenantIdpSyncResultVo;

/**
 * 租户 IdP 同步服务。
 */
public interface TenantIdpSyncService {

    TenantIdpSyncResultVo sync(TenantIdpSyncDto dto);
}
