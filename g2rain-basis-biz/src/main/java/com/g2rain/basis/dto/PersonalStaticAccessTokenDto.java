package com.g2rain.basis.dto;

import com.g2rain.common.model.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 个人静态访问令牌表查询DTO
 * 表名: personal_static_access_token
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "个人静态访问令牌表 DTO")
public class PersonalStaticAccessTokenDto extends BaseDto {

    /**
     * 授权记录标识（与 applicationId 二选一）
     */
    @Schema(description = "授权记录标识（与 applicationId 二选一）")
    private Long applicationAuthorizationId;

    /**
     * 应用标识（与 applicationAuthorizationId 二选一）
     */
    @Schema(description = "应用标识（与 applicationAuthorizationId 二选一）")
    private Long applicationId;

    /**
     * 访问令牌名称
     */
    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "访问令牌名称")
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
}
