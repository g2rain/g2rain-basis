package com.g2rain.basis.vo;

import com.g2rain.common.model.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 审计事件表返回VO
 * 关联表名: audit_event
 * 功能：封装接口返回数据，继承BaseVo复用基础字段逻辑，隔离数据库实体与前端展示层
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "审计事件对外展示对象")
public class AuditEventVo extends BaseVo {

    /**
     * 网关跟踪标识
     */
    @Schema(description = "网关侧链路跟踪号，用于串联一次调用全链路")
    private String traceId;

    /**
     * 客户端标识
     */
    @Schema(description = "客户端侧标识（如设备证明密钥编号、应用客户端编号等）")
    private String clientId;

    /**
     * 前端请求标识
     */
    @Schema(description = "前端单次请求唯一编号")
    private String requestId;

    /**
     * 前端请求时间
     */
    @Schema(description = "前端上报的请求发生时间")
    private LocalDateTime requestTime;

    /**
     * 语言偏好
     */
    @Schema(description = "客户端期望的响应语言偏好（自然语言区域标识）")
    private String acceptLanguage;

    /**
     * 请求路径
     */
    @Schema(description = "本次访问在应用内的路径")
    private String path;

    /**
     * 请求方法
     */
    @Schema(description = "访问方式（协议层动词，如只读、提交表单等）")
    private String method;

    /**
     * 客户端标识
     */
    @Schema(description = "客户端软件与运行环境说明（浏览器类型、版本及操作系统等）")
    private String userAgent;

    /**
     * 请求主机
     */
    @Schema(description = "客户端声明要访问的站点主机名及端口")
    private String host;

    /**
     * 代理链 IP 列表
     */
    @Schema(description = "经各级代理追加后的客户端与转发端地址列表，逗号分隔")
    private String xForwardedFor;

    /**
     * 真实客户端 IP
     */
    @Schema(description = "入口代理认定的客户端真实地址（单一）")
    private String xRealIp;

    /**
     * 请求来源 URL
     */
    @Schema(description = "用户从哪个页面跳转或引用进入本次访问的地址")
    private String referer;

    /**
     * 会话类型
     */
    @Schema(description = "当前登录或调用会话的类型名称或内部编码")
    private String sessionType;

    /**
     * 账号标识
     */
    @Schema(description = "账号（通行证）主键")
    private Long passportId;

    /**
     * 用户标识
     */
    @Schema(description = "用户主键")
    private Long userId;

    /**
     * 真实姓名
     */
    @Schema(description = "用户姓名")
    private String name;

    /**
     * 超级管理员
     */
    @Schema(description = "是否超级管理员：0 表示否，1 表示是", allowableValues = {"0", "1"})
    private Byte adminUser;

    /**
     * 组织标识
     */
    @Schema(description = "当前组织主键")
    private Long organId;

    /**
     * 组织名称
     */
    @Schema(description = "组织名称")
    private String organName;

    /**
     * 组织类型
     */
    @Schema(description = "组织类型")
    private String organType;

    /**
     * 平台运营组织
     */
    @Schema(description = "是否平台运营组织：0 表示否，1 表示是", allowableValues = {"0", "1"})
    private Byte adminCompany;

    /**
     * 数据操作的目标组织标识
     */
    @Schema(description = "数据操作目标组织主键")
    private Long targetOrganId;

    /**
     * 请求来源应用标识
     */
    @Schema(description = "发起调用的来源应用主键")
    private Long applicationId;

    /**
     * 请求来源应用编码
     */
    @Schema(description = "发起调用的来源应用在系统中的业务编码")
    private String applicationCode;

    /**
     * 请求来源应用所属机构标识
     */
    @Schema(description = "来源应用所属机构主键")
    private Long applicationOrganId;

    /**
     * 请求/响应摘要
     */
    @Schema(description = "请求参数、请求体与响应内容的合并摘要（一般为结构化文本）")
    private String payload;
}
