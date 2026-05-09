package com.g2rain.basis.vo;

import com.g2rain.common.json.AdminCompanyCondition;
import com.g2rain.common.json.ConditionalJsonIgnore;
import com.g2rain.common.model.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 登录信息表, 记录了当前登录状态的相关信息返回VO
 * 关联表名: login_token
 * 功能：封装接口返回数据，继承BaseVo复用基础字段逻辑，隔离数据库实体与前端展示层
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "登录信息 VO")
public class LoginTokenVo extends BaseVo {

    /**
     * 会话类型
     */
    @Schema(description = "会话类型")
    private String sessionType;

    /**
     * 机构标识
     */
    @Schema(description = "机构标识")
    private Long organId;

    /**
     * 机构类型
     */
    @Schema(description = "机构类型")
    private String organType;

    /**
     * 运营标记
     */
    @Schema(description = "运营标记")
    private Boolean adminCompany;

    /**
     * 账号标识
     */
    @Schema(description = "账号标识")
    private Long passportId;

    /**
     * 用户标识
     */
    @Schema(description = "用户标识")
    private Long userId;

    /**
     * 真实姓名
     */
    @Schema(description = "真实姓名")
    private String realName;

    /**
     * 管理员标记
     */
    @Schema(description = "管理员标记")
    private Boolean adminUser;

    /**
     * 应用标识
     */
    @Schema(description = "应用标识")
    private Long applicationId;

    /**
     * 应用组织标识
     */
    @Schema(description = "应用组织标识")
    private Long applicationOrganId;

    /**
     * 客户端 ID
     */
    @Schema(description = "客户端标识")
    private String clientId;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    @ConditionalJsonIgnore(adminCompany = AdminCompanyCondition.TRUE)
    @Schema(description = "删除标识[0:未删除, 1:已删除]")
    private Boolean deleteFlag;
}
