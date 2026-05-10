package com.g2rain.basis.dto;

import com.g2rain.common.model.BaseSelectListDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 登录信息表, 记录了当前登录状态的相关信息查询入参DTO
 * 用于LoginTokenDao.selectList方法的条件筛选
 * 表名: login_token
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "登录信息查询入参 DTO")
public class LoginTokenSelectDto extends BaseSelectListDto {

    /**
     * 会话类型
     */
    @Schema(description = "会话类型")
    private String sessionType;

    /**
     * 机构标识
     */
    @Schema(description = "机构标识")
    private Long organId;

    /**
     * 机构类型
     */
    @Schema(description = "机构类型")
    private String organType;

    /**
     * 运营标记
     */
    @Schema(description = "运营标记")
    private Boolean adminCompany;

    /**
     * 账号标识
     */
    @Schema(description = "账号标识")
    private Long passportId;

    /**
     * 用户标识
     */
    @Schema(description = "用户标识")
    private Long userId;

    /**
     * 真实姓名
     */
    @Schema(description = "真实姓名")
    private String realName;

    /**
     * 管理员标记
     */
    @Schema(description = "管理员标记")
    private Boolean adminUser;

    /**
     * 应用标识
     */
    @Schema(description = "应用标识")
    private Long applicationId;

    /**
     * 应用组织标识
     */
    @Schema(description = "应用组织标识")
    private Long applicationOrganId;

    /**
     * 客户端 ID
     */
    @Schema(description = "客户端 ID")
    private String clientId;
}
