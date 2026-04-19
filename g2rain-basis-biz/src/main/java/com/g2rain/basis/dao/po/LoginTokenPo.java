package com.g2rain.basis.dao.po;

import com.g2rain.common.model.BasePo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalDate;

/**
 * 登录信息表, 记录了当前登录状态的相关信息返回Po
 * 关联表名: login_token
 * 功能：封装实体数据，继承BasePo复用基础字段逻辑
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LoginTokenPo extends BasePo {

    /**
     * 会话类型
     */
    private String sessionType;

    /**
     * 机构标识
     */
    private Long organId;

    /**
     * 机构类型
     */
    private String organType;

    /**
     * 运营标记
     */
    private Boolean adminCompany;

    /**
     * 账号标识
     */
    private Long passportId;

    /**
     * 用户标识
     */
    private Long userId;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 管理员标记
     */
    private Boolean adminUser;

    /**
     * 应用标识
     */
    private Long applicationId;

    /**
     * 应用组织标识
     */
    private Long applicationOrganId;

    /**
     * 客户端 ID
     */
    private String clientId;

    /**
     * 最近一次刷新时间
     */
    private LocalDateTime lastestRefreshTime;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    private Boolean deleteFlag;
}
