package com.g2rain.basis.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 自动开通 SELF 控制域结果。
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "自动开通 SELF 结果")
public class ApplicationAuthorizationActivateSelfVo {

    @Schema(description = "应用 ID")
    private Long applicationId;

    @Schema(description = "应用编码")
    private String applicationCode;

    @Schema(description = "目标机构 ID")
    private Long organId;

    @Schema(description = "本次创建或恢复的授权记录 ID")
    private List<Long> authorizationIds = new ArrayList<>();
}
