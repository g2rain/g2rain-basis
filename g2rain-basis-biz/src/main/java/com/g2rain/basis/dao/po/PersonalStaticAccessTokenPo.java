package com.g2rain.basis.dao.po;

import com.g2rain.common.model.BasePo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 个人静态访问令牌表返回Po
 * 关联表名: personal_static_access_token
 * 功能：封装实体数据，继承BasePo复用基础字段逻辑
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PersonalStaticAccessTokenPo extends BasePo {

    /**
     * 授权记录标识
     */
    private Long applicationAuthorizationId;

    /**
     * 应用标识
     */
    private Long applicationId;

    /**
     * 机构标识
     */
    private Long organId;

    /**
     * 用户标识
     */
    private Long userId;

    /**
     * 访问令牌名称
     */
    private String name;

    /**
     * 静态访问令牌的哈希摘要
     */
    private String tokenHash;

    /**
     * 脱敏令牌
     */
    private String maskedToken;

    /**
     * 状态[ACTIVATED:已启用, REVOKED:已吊销]
     */
    private String status;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    private Boolean deleteFlag;
}