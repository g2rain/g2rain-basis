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
 * 用户表返回VO
 * 关联表名: user
 * 功能：封装接口返回数据，继承BaseVo复用基础字段逻辑，隔离数据库实体与前端展示层
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户 VO")
public class UserVo extends BaseVo {

    /**
     * 账号标识
     */
    @Schema(description = "账号标识", format = "int64")
    private Long passportId;

    /**
     * 机构标识
     */
    @Schema(description = "机构标识", format = "int64")
    private Long organId;

    /**
     * 邮箱地址
     */
    @Schema(description = "邮箱地址", example = "user@example.com")
    private String email;

    /**
     * 手机号码
     */
    @Schema(description = "手机号码", example = "13800000000")
    private String mobile;

    /**
     * 真实姓名
     */
    @Schema(description = "真实姓名", example = "张三")
    private String realName;

    /**
     * 管理员标记
     */
    @Schema(description = "管理员标记", example = "false")
    private Boolean admin;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    @ConditionalJsonIgnore(adminCompany = AdminCompanyCondition.TRUE)
    @Schema(description = "删除标识（0 未删除，1 已删除）", example = "false")
    private Boolean deleteFlag;
}
