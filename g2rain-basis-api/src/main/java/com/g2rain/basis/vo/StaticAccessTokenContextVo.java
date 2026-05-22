package com.g2rain.basis.vo;

import com.g2rain.common.enums.OrganType;
import com.g2rain.common.enums.SessionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 个人静态访问令牌解析后的会话上下文。
 *
 * @author alpha
 * @since 2026/5/21
 */
@Setter
@Getter
@NoArgsConstructor
@Schema(description = "个人静态访问令牌会话上下文 VO")
public class StaticAccessTokenContextVo {

    /**
     * 会话类型
     * <p>标识当前会话类型，例如用户会话、应用会话等。</p>
     */
    @Schema(description = "会话类型")
    protected SessionType sessionType;

    /**
     * 账号 ID
     */
    @Schema(description = "账号 ID")
    protected Long passportId;

    /**
     * 用户 ID
     */
    @Schema(description = "用户 ID")
    protected Long userId;

    /**
     * 真实姓名
     * <p>用于展示或身份确认。</p>
     */
    @Schema(description = "真实姓名")
    protected String name;

    /**
     * 公司内管理员标记位
     * <p>标识当前用户是否为管理员。</p>
     */
    @Schema(description = "公司内管理员标记位")
    protected boolean adminUser;

    /**
     * 组织标识
     * <p>当前用户所属组织的唯一标识。</p>
     */
    @Schema(description = "组织标识")
    protected Long organId;

    /**
     * 组织名称
     * <p>当前用户所属组织的名称。</p>
     */
    @Schema(description = "组织名称")
    protected String organName;

    /**
     * 组织类型
     * <p>标识当前用户所属组织类型。</p>
     */
    @Schema(description = "组织类型")
    protected OrganType organType;

    /**
     * 平台管理组织标记位
     * <p>标识该组织是否为平台管理组织。</p>
     */
    @Schema(description = "平台管理组织标记位")
    protected boolean adminCompany;

    /**
     * 请求来源应用标识
     * <p>表示当前接口调用是由哪个应用发起的。</p>
     */
    @Schema(description = "请求来源应用标识")
    private Long applicationId;

    /**
     * 请求来源应用编码
     * <p>表示当前接口调用是由哪个应用发起的。</p>
     */
    @Schema(description = "请求来源应用编码")
    private String applicationCode;

    /**
     * 请求来源应用所属机构标识
     * <p>表示发起当前接口调用的应用所隶属的机构。</p>
     */
    @Schema(description = "请求来源应用所属机构标识")
    private Long applicationOrganId;
}
