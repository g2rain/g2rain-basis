package com.g2rain.basis.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 用户对象（VO）。
 * <p>
 * 用于应用根据授权信息获取当前用户信息
 * </p>
 *
 * @author jagger
 * @since 2026/3/11-20:39
 */
@Setter
@Getter
@NoArgsConstructor
@Schema(description = "用户 VO")
public class AuthorityUserVo extends UserVo {

    /**
     * 账号信息
     */
    @Schema(description = "账号信息")
    private PassportVo passport;

    /**
     * 机构信息
     */
    @Schema(description = "机构信息")
    private OrganVo organ;
}
