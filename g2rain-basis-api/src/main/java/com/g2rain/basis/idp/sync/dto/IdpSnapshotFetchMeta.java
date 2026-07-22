package com.g2rain.basis.idp.sync.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * IdP 通讯录快照拉取统计，用于判断快照是否完整可信。
 */
@Setter
@Getter
@NoArgsConstructor
@Schema(description = "IdP 通讯录快照拉取统计")
public class IdpSnapshotFetchMeta {

    @Schema(description = "部门节点数（不含根部门）")
    private int deptNodeCount;

    @Schema(description = "成员数")
    private int memberCount;

    @Schema(description = "子部门 list 接口调用次数")
    private int deptListApiCalls;

    @Schema(description = "部门成员 list 接口调用次数")
    private int userListApiCalls;

    @Schema(description = "成员详情 get 接口调用次数")
    private int userDetailApiCalls;

    @Schema(description = "子部门 list 接口 result 非数组次数")
    private int deptSubListNonArrayCount;

    @Schema(description = "成员 list 分页异常次数（has_more 但 cursor 未前进等）")
    private int userListIncompletePages;

    @Schema(description = "TopAPI 重试次数")
    private int retryCount;
}
