package com.g2rain.basis.dao.po;

import com.g2rain.common.model.BasePo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 审计事件表返回Po
 * 关联表名: audit_event
 * 功能：封装实体数据，继承BasePo复用基础字段逻辑
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AuditEventPo extends BasePo {

    /**
     * 网关跟踪标识
     */
    private String traceId;

    /**
     * 客户端标识
     */
    private String clientId;

    /**
     * 前端请求标识
     */
    private String requestId;

    /**
     * 前端请求时间
     */
    private LocalDateTime requestTime;

    /**
     * 语言偏好
     */
    private String acceptLanguage;

    /**
     * 请求路径
     */
    private String path;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 客户端标识
     */
    private String userAgent;

    /**
     * 请求主机
     */
    private String host;

    /**
     * 代理链IP列表
     */
    private String xForwardedFor;

    /**
     * 真实客户端IP
     */
    private String xRealIp;

    /**
     * 请求来源URL
     */
    private String referer;

    /**
     * 会话类型
     */
    private String sessionType;

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
    private String name;

    /**
     * 超级管理员
     */
    private Byte adminUser;

    /**
     * 组织标识
     */
    private Long organId;

    /**
     * 组织名称
     */
    private String organName;

    /**
     * 组织类型
     */
    private String organType;

    /**
     * 平台运营组织
     */
    private Byte adminCompany;

    /**
     * 数据操作的目标组织标识
     */
    private Long targetOrganId;

    /**
     * 请求来源应用标识
     */
    private Long applicationId;

    /**
     * 请求来源应用编码
     */
    private String applicationCode;

    /**
     * 请求来源应用所属机构标识
     */
    private Long applicationOrganId;

    /**
     * 请求/响应摘要
     */
    private String payload;
}
