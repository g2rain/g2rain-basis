package com.g2rain.basis.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.g2rain.common.model.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;

import com.g2rain.common.json.ConditionalJsonIgnore;
import com.g2rain.common.json.AdminCompanyCondition;

/**
 * 个人静态访问令牌表返回VO
 * 关联表名: personal_static_access_token
 * 功能：封装接口返回数据，继承BaseVo复用基础字段逻辑，隔离数据库实体与前端展示层
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "个人静态访问令牌表 VO")
public class PersonalStaticAccessTokenVo extends BaseVo {

    /**
     * 授权记录标识
     */
    @Schema(description = "授权记录标识")
    private Long applicationAuthorizationId;

    /**
     * 应用标识
     */
    @Schema(description = "应用标识")
    private Long applicationId;

    /**
     * 机构标识
     */
    @Schema(description = "机构标识")
    private Long organId;

    /**
     * 用户标识
     */
    @Schema(description = "用户标识")
    private Long userId;

    /**
     * 用户名称
     */
    @Schema(description = "用户名称")
    private String userName;

    /**
     * 访问令牌名称
     */
    @Schema(description = "访问令牌名称")
    private String name;

    /**
     * 脱敏令牌
     */
    @Schema(description = "脱敏令牌")
    private String maskedToken;

    /**
     * 状态[ACTIVATED:已启用, REVOKED:已吊销]
     */
    @Schema(description = "状态[ACTIVATED:已启用, REVOKED:已吊销]")
    private String status;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    @Schema(description = "删除标识（0 未删除，1 已删除）", example = "false")
    @ConditionalJsonIgnore(adminCompany = AdminCompanyCondition.TRUE)
    private Boolean deleteFlag;
}
