package com.g2rain.basis.dto;

import com.g2rain.common.model.BaseSelectListDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 用户表查询入参DTO
 * 用于UserDao.selectList方法的条件筛选
 * 表名: user
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户查询入参 DTO")
public class UserSelectDto extends BaseSelectListDto {

    /**
     * 账号标识
     */
    @Schema(description = "账号标识")
    private Long passportId;

    /**
     * 机构标识
     */
    @Schema(description = "机构标识")
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

    /**
     * 用户名称关键字（模糊匹配姓名、手机号、用户标识）
     */
    @Schema(description = "用户名称关键字（模糊匹配姓名、手机号、用户标识）")
    private String searchName;

    /**
     * 管理员标记
     */
    @Schema(description = "管理员标记")
    private Boolean admin;
}
