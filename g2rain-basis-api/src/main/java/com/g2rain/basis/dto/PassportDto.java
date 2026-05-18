package com.g2rain.basis.dto;

import com.g2rain.common.model.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 账号表新增或更新DTO
 * 表名: passport
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "账号新增或更新 DTO")
public class PassportDto extends BaseDto {

    /**
     * 登录用户
     */
    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "登录用户")
    private String username;

    /**
     * 登录密码
     */
    @Schema(description = "登录密码")
    private String password;

    /**
     * 真实姓名
     */
    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "真实姓名")
    private String realName;

    /**
     * 性别[MALE:男性, FEMALE:女性]
     */
    @Schema(description = "性别[MALE:男性, FEMALE:女性]", allowableValues = {"MALE", "FEMALE"})
    private String sex;

    /**
     * 生日
     */
    @Schema(description = "出生日期")
    private String birthday;

    /**
     * 身份证号
     */
    @Schema(description = "身份证号")
    private String idNo;

    /**
     * 手机号码
     */
    @Schema(description = "手机号码")
    private String mobile;

    /**
     * 邮箱地址
     */
    @Schema(description = "邮箱地址")
    private String email;

    /**
     * 密码是否可信：{@code false} 为临时/不可信密码，{@code true} 为用户已设置的可信密码。
     * 新增时未传则默认为 {@code true}（IAM 自助注册）；第三方身份源自动建号时应显式传 {@code false}。
     */
    @Schema(description = "密码是否可信")
    private Boolean passwordTrusted;
}
