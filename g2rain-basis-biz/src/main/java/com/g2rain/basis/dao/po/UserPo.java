package com.g2rain.basis.dao.po;

import com.g2rain.common.model.BasePo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 用户表返回Po
 * 关联表名: user
 * 功能：封装实体数据，继承BasePo复用基础字段逻辑
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserPo extends BasePo {

    /**
     * 账号标识
     */
    private Long passportId;

    /**
     * 机构标识
     */
    private Long organId;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 管理员标记
     */
    private Boolean admin;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    private Boolean deleteFlag;
}
