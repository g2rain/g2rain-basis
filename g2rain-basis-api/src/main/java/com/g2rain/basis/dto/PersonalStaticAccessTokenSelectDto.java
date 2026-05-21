package com.g2rain.basis.dto;

import com.g2rain.common.model.BaseSelectListDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 个人静态访问令牌表查询入参DTO
 * 用于PersonalStaticAccessTokenDao.selectList方法的条件筛选
 * 表名: personal_static_access_token
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "个人静态访问令牌表查询入参 DTO")
public class PersonalStaticAccessTokenSelectDto extends BaseSelectListDto {

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
     * 访问令牌名称
     */
    @Schema(description = "访问令牌名称")
    private String name;

    /**
     * 静态访问令牌的哈希摘要
     */
    @Schema(description = "静态访问令牌的哈希摘要")
    private String tokenHash;

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
}
