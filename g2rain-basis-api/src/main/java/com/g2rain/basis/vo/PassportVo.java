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
 * 账号表返回VO
 * 关联表名: passport
 * 功能：封装接口返回数据，继承BaseVo复用基础字段逻辑，隔离数据库实体与前端展示层
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "账号 VO")
public class PassportVo extends BaseVo {

    /**
     * 登录用户
     */
    @Schema(description = "登录用户")
    private String username;

    /**
     * 真实姓名
     */
    @Schema(description = "真实姓名")
    private String realName;

    /**
     * 性别[MALE:男性, FEMALE:女性]
     */
    @Schema(description = "性别[MALE:男性, FEMALE:女性]")
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
     * 状态[NORMAL:正常, FROZEN:冻结]
     */
    @Schema(description = "状态[NORMAL:正常, FROZEN:冻结]")
    private String status;

    /**
     * 密码是否可信：{@code false} 为临时/不可信密码，{@code true} 为用户已设置的可信密码。
     */
    @Schema(description = "密码是否可信")
    private Boolean passwordTrusted;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    @ConditionalJsonIgnore(adminCompany = AdminCompanyCondition.TRUE)
    @Schema(description = "删除标识[0:未删除, 1:已删除]")
    private Boolean deleteFlag;
}
