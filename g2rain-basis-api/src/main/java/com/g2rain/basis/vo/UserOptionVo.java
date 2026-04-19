package com.g2rain.basis.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 用户表返回VO
 * 关联表名: user
 * 功能：封装接口返回数据，继承BaseVo复用基础字段逻辑，隔离数据库实体与前端展示层
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@Schema(description = "用户 VO")
public class UserOptionVo {

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
