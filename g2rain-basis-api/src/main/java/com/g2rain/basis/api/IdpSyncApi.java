package com.g2rain.basis.api;

import com.g2rain.basis.idp.sync.dto.IdpFetchMemberRequest;
import com.g2rain.basis.idp.sync.dto.IdpFetchSnapshotRequest;
import com.g2rain.basis.idp.sync.dto.IdpMemberNode;
import com.g2rain.basis.idp.sync.dto.IdpOrganizationSnapshot;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * IdP 通讯录同步内部 API，由 g2rain-iam 实现，g2rain-basis 通过 Feign 调用。
 * <p>具体渠道由请求体 {@code idpType} 路由（钉钉、企微等）。</p>
 */
@Tag(name = "IdP 通讯录同步（内部）", description = "服务间 IdP 通讯录拉取接口")
public interface IdpSyncApi {

    /**
     * 拉取企业通讯录快照。
     */
    @PostMapping("/fetch_snapshot")
    @Operation(summary = "拉取 IdP 通讯录快照", description = "按 idpType 拉取部门树与成员列表，仅供服务间调用")
    Result<IdpOrganizationSnapshot> fetchSnapshot(
        @RequestBody @Validated IdpFetchSnapshotRequest request
    );

    /**
     * 按 IdP 企业内用户标识拉取成员详情。
     */
    @PostMapping("/fetch_member")
    @Operation(summary = "按 userid 拉取 IdP 成员详情", description = "根据企业内用户标识查询主体与基础资料，仅供服务间调用")
    Result<IdpMemberNode> fetchMember(
        @RequestBody @Validated IdpFetchMemberRequest request
    );
}
