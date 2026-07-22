package com.g2rain.basis.idp.resolve.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * IdP 登录 Passport 解析结果。
 */
@Setter
@Getter
@NoArgsConstructor
@Schema(description = "IdP 登录 Passport 解析结果")
public class IdpPassportResolveVo {

    @Schema(description = "账号 ID")
    private Long passportId;

    @Schema(description = "登录用户名")
    private String username;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "IdP 绑定摘要")
    private IdpPassportBindingSummaryVo idpBinding;

    @Schema(description = "该 passport 在各机构下的用户列表")
    private List<IdpPassportUserVo> users;
}
