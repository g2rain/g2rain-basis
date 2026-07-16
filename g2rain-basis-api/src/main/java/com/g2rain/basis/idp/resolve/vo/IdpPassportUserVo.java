package com.g2rain.basis.idp.resolve.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * IdP 登录解析返回的用户摘要。
 */
@Setter
@Getter
@NoArgsConstructor
@Schema(description = "IdP 登录解析用户摘要")
public class IdpPassportUserVo {

    @Schema(description = "用户 ID")
    private Long userId;

    @Schema(description = "机构 ID")
    private Long organId;

    @Schema(description = "真实姓名")
    private String realName;
}
