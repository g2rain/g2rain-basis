package com.g2rain.basis.dto;

import com.g2rain.common.model.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 用户 DTO
 * 表名: user
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户 DTO")
public class UserDto extends BaseDto {

    /**
     * 账号标识
     */
    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "账号标识")
    private Long passportId;

    /**
     * 机构标识
     */
    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "机构标识")
    private Long organId;

    /**
     * 邮箱地址
     */
    @Schema(description = "邮箱地址")
    private String email;

    /**
     * 手机号码
     */
    @Schema(description = "手机号码")
    private String mobile;

    /**
     * 真实姓名
     */
    @Schema(description = "真实姓名")
    private String realName;
}
