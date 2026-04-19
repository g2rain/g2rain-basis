package com.g2rain.basis.vo;

import com.g2rain.common.json.AdminCompanyCondition;
import com.g2rain.common.json.ConditionalJsonIgnore;
import com.g2rain.common.model.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 应用表返回VO
 * 关联表名: application
 * 功能：封装接口返回数据，继承BaseVo复用基础字段逻辑，隔离数据库实体与前端展示层
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "应用 VO")
public class ApplicationVo extends BaseVo {

    /**
     * 机构标识
     */
    @Schema(description = "机构标识")
    private Long organId;

    /**
     * 应用名称
     */
    @Schema(description = "应用名称")
    private String applicationName;

    /**
     * 应用编码
     */
    @Schema(description = "应用编码")
    private String applicationCode;

    /**
     * 默认应用
     */
    @Schema(description = "默认应用")
    private Boolean landing;

    /**
     * 是否具备集成功能
     */
    @Schema(description = "是否具备集成功能")
    private Boolean canIntegrate;

    /**
     * 应用类型[SUPPORT:支撑, SYSTEM:系统提供, PUBLIC:第三方提供, PRIVATE:私有]
     */
    @Schema(description = "应用类型[SUPPORT:支撑, SYSTEM:系统提供, PUBLIC:第三方提供, PRIVATE:私有]")
    private String applicationType;

    /**
     * 访问令牌生存时间(秒)
     */
    @Schema(description = "访问令牌生存时间(秒)")
    private Integer accessTokenExpiresIn;

    /**
     * 刷新访问令牌生存时间(秒)
     */
    @Schema(description = "刷新访问令牌生存时间(秒)")
    private Integer refreshTokenExpiresIn;

    /**
     * 访问地址
     */
    @Schema(description = "访问地址")
    private String endpointUrl;

    /**
     * 应用路径
     */
    @Schema(description = "应用路径")
    private String contextPath;

    /**
     * 应用状态[PUBLISHED:已发布, UNPUBLISHED:未发布]
     */
    @Schema(description = "应用状态[PUBLISHED:已发布, UNPUBLISHED:未发布]")
    private String status;

    /**
     * 业务说明
     */
    @Schema(description = "业务说明")
    private String description;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    @ConditionalJsonIgnore(adminCompany = AdminCompanyCondition.TRUE)
    @Schema(description = "删除标识[0:未删除, 1:已删除]")
    private Boolean deleteFlag;
}
