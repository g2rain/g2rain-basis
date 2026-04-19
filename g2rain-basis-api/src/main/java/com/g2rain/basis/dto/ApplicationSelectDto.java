package com.g2rain.basis.dto;

import com.g2rain.common.model.BaseSelectListDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;


/**
 * 应用表查询入参DTO
 * 用于ApplicationDao.selectList方法的条件筛选
 * 表名: application
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "应用查询入参 DTO")
public class ApplicationSelectDto extends BaseSelectListDto {

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
     * 是否具备集成功能
     */
    @Schema(description = "是否具备集成功能")
    private Boolean canIntegrate;

    /**
     * 是否为登录后的默认目标
     */
    @Schema(description = "是否为登录后的默认目标")
    private Boolean landing;

    /**
     * 应用类型[SUPPORT:支撑, SYSTEM:系统提供, PUBLIC:第三方提供, PRIVATE:私有]
     */
    @Schema(description = "应用类型[SUPPORT:支撑, SYSTEM:系统提供, PUBLIC:第三方提供, PRIVATE:私有]", allowableValues = {"SUPPORT", "SYSTEM", "PUBLIC", "PRIVATE"})
    private String applicationType;

    /**
     * 应用类型集合
     */
    @Schema(description = "应用类型集合")
    private Set<String> includeApplicationTypes;

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
    @Schema(description = "应用状态[PUBLISHED:已发布, UNPUBLISHED:未发布]", allowableValues = {"PUBLISHED", "UNPUBLISHED"})
    private String status;

    /**
     * 业务说明
     */
    @Schema(description = "业务说明")
    private String description;
}
