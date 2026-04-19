package com.g2rain.basis.dto;

import com.g2rain.basis.utils.BasisUtils;
import com.g2rain.common.model.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 应用表查询DTO
 * 表名: application
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "应用 DTO")
public class ApplicationDto extends BaseDto {

    /**
     * 机构标识
     */
    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "机构标识")
    private Long organId;

    /**
     * 应用名称
     */
    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "应用名称")
    private String applicationName;

    /**
     * 应用编码
     */
    @Schema(description = "应用编码")
    private String applicationCode;

    /**
     * 是否具备集成功能
     */
    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "是否具备集成功能")
    private Boolean canIntegrate;

    /**
     * 应用类型[SUPPORT:支撑, SYSTEM:系统提供, PUBLIC:第三方提供, PRIVATE:私有]
     */
    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "应用类型[SUPPORT:支撑, SYSTEM:系统提供, PUBLIC:第三方提供, PRIVATE:私有]", allowableValues = {"SUPPORT", "SYSTEM", "PUBLIC", "PRIVATE"})
    private String applicationType;

    /**
     * 访问令牌生存时间(秒)
     */
    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "访问令牌生存时间(秒)")
    private Integer accessTokenExpiresIn = BasisUtils.ACCESS_TOKEN_EXPIRES_IN;

    /**
     * 刷新访问令牌生存时间(秒)
     */
    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "刷新访问令牌生存时间(秒)")
    private Integer refreshTokenExpiresIn = BasisUtils.REFRESH_TOKEN_EXPIRES_IN;

    /**
     * 访问地址
     */
    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "访问地址")
    private String endpointUrl;

    /**
     * 应用路径
     */
    @Schema(description = "应用路径")
    private String contextPath;

    /**
     * 业务说明
     */
    @Schema(description = "业务说明")
    private String description;
}
