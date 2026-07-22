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
 */
@Tag(name = "IdP 通讯录同步（内部）", description = "服务间 IdP 通讯录拉取接口")
public interface IdpSyncApi {

    /**
     * 拉取钉钉企业通讯录快照。
     */
    @PostMapping("/fetch_snapshot")
    @Operation(summary = "拉取钉钉通讯录快照", description = "递归拉取钉钉部门树与成员列表，仅供服务间调用")
    Result<IdpOrganizationSnapshot> fetchDingTalkSnapshot(
        @RequestBody @Validated IdpFetchSnapshotRequest request
    );

    /**
     * 按 IdP 企业内用户标识拉取成员详情。
     */
    @PostMapping("/fetch_member")
    @Operation(summary = "按 userid 拉取钉钉成员详情", description = "根据钉钉 corp 内 userid 查询 unionId 与基础资料，仅供服务间调用")
    Result<IdpMemberNode> fetchDingTalkMember(
        @RequestBody @Validated IdpFetchMemberRequest request
    );
}
