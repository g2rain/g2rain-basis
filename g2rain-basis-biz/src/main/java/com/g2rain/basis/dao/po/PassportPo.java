package com.g2rain.basis.dao.po;

import com.g2rain.common.model.BasePo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 账号表返回Po
 * 关联表名: passport
 * 功能：封装实体数据，继承BasePo复用基础字段逻辑
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PassportPo extends BasePo {

    /**
     * 登录用户
     */
    private String username;

    /**
     * 登录密码
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 性别[MALE:男性, FEMALE:女性]
     */
    private String sex;

    /**
     * 生日
     */
    private String birthday;

    /**
     * 身份证号
     */
    private String idNo;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 状态[NORMAL:正常, FROZEN:冻结]
     */
    private String status;

    /**
     * 密码是否可信[0:不可信/临时密码, 1:可信/用户已设置]
     */
    private Integer passwordTrusted;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    private Boolean deleteFlag;
}
