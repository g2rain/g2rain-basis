package com.g2rain.basis.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 按会话主体类型发布的 API 权限快照。
 */
@Setter
@Getter
@NoArgsConstructor
@Schema(description = "会话 API 权限快照 VO")
public class SessionApiPermissionVo {

    @Schema(description = "会话主体类型")
    private String sessionType;

    @Schema(description = "机构标识；MEMBER 快照必填")
    private Long organId;

    @Schema(description = "该租户 MEMBER 权限版本（单调递增）；非 MEMBER 可为 0")
    private Long version;

    @Schema(description = "resource_api.id 列表")
    private List<Long> apiIds;
}
