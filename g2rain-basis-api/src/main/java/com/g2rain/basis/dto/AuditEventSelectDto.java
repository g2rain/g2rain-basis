package com.g2rain.basis.dto;

import com.g2rain.common.model.BaseSelectListDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 审计事件表查询入参DTO
 * 用于AuditEventDao.selectList方法的条件筛选
 * 表名: audit_event
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "审计事件列表或分页查询条件")
public class AuditEventSelectDto extends BaseSelectListDto {

    /**
     * 网关跟踪标识
     */
    @Schema(description = "按网关链路跟踪号筛选")
    private String traceId;

    /**
     * 客户端标识
     */
    @Schema(description = "按客户端标识筛选")
    private String clientId;

    /**
     * 前端请求标识
     */
    @Schema(description = "按前端请求唯一编号筛选")
    private String requestId;

    /**
     * 前端请求时间
     */
    @Schema(description = "按前端上报的请求时间精确匹配")
    private LocalDateTime requestTime;

    /**
     * 语言偏好
     */
    @Schema(description = "按语言偏好筛选")
    private String acceptLanguage;

    /**
     * 请求路径
     */
    @Schema(description = "按访问路径筛选（等值或模糊以数据访问实现为准）")
    private String path;

    /**
     * 请求方法
     */
    @Schema(description = "按访问方式筛选（协议层动词）")
    private String method;

    /**
     * 客户端标识
     */
    @Schema(description = "按客户端软件与环境说明筛选")
    private String userAgent;

    /**
     * 请求主机
     */
    @Schema(description = "按声明访问的主机名及端口筛选")
    private String host;

    /**
     * 代理链 IP 列表
     */
    @Schema(description = "按代理链地址列表筛选")
    private String xForwardedFor;

    /**
     * 真实客户端 IP
     */
    @Schema(description = "按入口代理认定的客户端地址筛选")
    private String xRealIp;

    /**
     * 请求来源 URL
     */
    @Schema(description = "按来源页地址筛选")
    private String referer;

    /**
     * 会话类型
     */
    @Schema(description = "按会话类型筛选")
    private String sessionType;

    /**
     * 账号标识
     */
    @Schema(description = "按账号主键筛选")
    private Long passportId;

    /**
     * 用户标识
     */
    @Schema(description = "按用户主键筛选")
    private Long userId;

    /**
     * 真实姓名
     */
    @Schema(description = "按用户姓名筛选")
    private String name;

    /**
     * 超级管理员
     */
    @Schema(description = "是否超级管理员：0 否，1 是", allowableValues = {"0", "1"})
    private Byte adminUser;

    /**
     * 组织标识
     */
    @Schema(description = "按当前组织主键筛选")
    private Long organId;

    /**
     * 组织名称
     */
    @Schema(description = "按组织名称筛选")
    private String organName;

    /**
     * 组织类型
     */
    @Schema(description = "按组织类型筛选")
    private String organType;

    /**
     * 平台运营组织
     */
    @Schema(description = "是否平台运营组织：0 否，1 是", allowableValues = {"0", "1"})
    private Byte adminCompany;

    /**
     * 数据操作的目标组织标识
     */
    @Schema(description = "按数据操作目标组织主键筛选")
    private Long targetOrganId;

    /**
     * 请求来源应用标识
     */
    @Schema(description = "按来源应用主键筛选")
    private Long applicationId;

    /**
     * 请求来源应用编码
     */
    @Schema(description = "按来源应用业务编码筛选")
    private String applicationCode;

    /**
     * 请求来源应用所属机构标识
     */
    @Schema(description = "按来源应用所属机构主键筛选")
    private Long applicationOrganId;

    /**
     * 请求/响应摘要
     */
    @Schema(description = "按请求与响应摘要文本关键字筛选（是否支持模糊以数据访问实现为准）")
    private String payload;
}
