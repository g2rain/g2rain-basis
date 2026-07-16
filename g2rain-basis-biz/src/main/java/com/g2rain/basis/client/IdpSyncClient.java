package com.g2rain.basis.client;

import com.g2rain.basis.api.IdpSyncApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * IAM IdP 通讯录同步 Feign 客户端。
 */
@FeignClient(name = "g2rain-iam", contextId = "idpSyncClient", path = "/internal/idp_sync/dingtalk")
public interface IdpSyncClient extends IdpSyncApi {
}
