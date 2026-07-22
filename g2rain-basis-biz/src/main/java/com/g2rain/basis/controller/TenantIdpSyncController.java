package com.g2rain.basis.controller;

import com.g2rain.basis.api.TenantIdpSyncApi;
import com.g2rain.basis.dto.TenantIdpSyncDto;
import com.g2rain.basis.service.TenantIdpSyncService;
import com.g2rain.basis.vo.TenantIdpSyncResultVo;
import com.g2rain.common.model.Result;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 租户 IdP 同步控制器。
 */
@RestController
@RequestMapping("/tenant_idp_sync")
public class TenantIdpSyncController implements TenantIdpSyncApi {

    @Resource(name = "tenantIdpSyncServiceImpl")
    private TenantIdpSyncService tenantIdpSyncService;

    @Override
    public Result<TenantIdpSyncResultVo> sync(@RequestBody @Validated TenantIdpSyncDto dto) {
        return Result.success(tenantIdpSyncService.sync(dto));
    }
}
