package com.g2rain.basis.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 当前账号可切换的用户身份
 * 关联表名: user
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@Schema(description = "用户身份 VO")
public class UserIdentityVo {

    /**
     * 用户标识
     */
    @Schema(description = "用户标识")
    private Long userId;

    /**
     * 用户昵称
     */
    @Schema(description = "用户昵称")
    private String userName;

    /**
     * 机构标识
     */
    @Schema(description = "机构标识")
    private Long organId;

    /**
     * 机构名称
     */
    @Schema(description = "机构名称")
    private String organName;
}
