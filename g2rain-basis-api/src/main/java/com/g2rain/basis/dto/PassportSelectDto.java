package com.g2rain.basis.dto;

import com.g2rain.common.model.BaseSelectListDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 账号表查询入参DTO
 * 用于PassportDao.selectList方法的条件筛选
 * 表名: passport
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "账号查询入参 DTO")
public class PassportSelectDto extends BaseSelectListDto {

    /**
     * 登录用户
     */
    @Schema(description = "登录用户")
    private String username;

    /**
     * 登录密码
     */
    @Schema(description = "登录密码")
    private String password;

    /**
     * 真实姓名
     */
    @Schema(description = "真实姓名")
    private String realName;

    /**
     * 性别[MALE:男性, FEMALE:女性]
     */
    @Schema(description = "性别[MALE:男性, FEMALE:女性]", allowableValues = {"MALE", "FEMALE"})
    private String sex;

    /**
     * 生日
     */
    @Schema(description = "生日")
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
     * 状态[NORMAL:正常, FROZEN:冻结]
     */
    @Schema(description = "状态[NORMAL:正常, FROZEN:冻结]", allowableValues = {"NORMAL", "FROZEN"})
    private String status;
}
