package com.g2rain.basis.client;

import com.g2rain.department.api.DepartmentIdpSyncApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * Department IdP 同步 Feign 客户端。
 */
@FeignClient(name = "g2rain-department", contextId = "departmentIdpSyncClient", path = "/department_idp_sync")
public interface DepartmentIdpSyncClient extends DepartmentIdpSyncApi {
}
