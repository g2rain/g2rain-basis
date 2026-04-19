package com.g2rain.basis.dao.po;

import com.g2rain.common.model.BasePo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 应用表返回Po
 * 关联表名: application
 * 功能：封装实体数据，继承BasePo复用基础字段逻辑
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ApplicationPo extends BasePo {

    /**
     * 机构标识
     */
    private Long organId;

    /**
     * 应用名称
     */
    private String applicationName;

    /**
     * 应用编码
     */
    private String applicationCode;

    /**
     * 是否具备集成功能
     */
    private Boolean canIntegrate;

    /**
     * 默认应用
     */
    private Boolean landing;

    /**
     * 应用类型[SUPPORT:支撑, SYSTEM:系统提供, PUBLIC:第三方提供, PRIVATE:私有]
     */
    private String applicationType;

    /**
     * 访问令牌生存时间(秒)
     */
    private Integer accessTokenExpiresIn;

    /**
     * 刷新访问令牌生存时间(秒)
     */
    private Integer refreshTokenExpiresIn;

    /**
     * 访问地址
     */
    private String endpointUrl;

    /**
     * 应用路径
     */
    private String contextPath;

    /**
     * 应用状态[PUBLISHED:已发布, UNPUBLISHED:未发布]
     */
    private String status;

    /**
     * 业务说明
     */
    private String description;

    /**
     * 应用公钥算法[EC]
     */
    private String publicKeyAlgorithm;

    /**
     * 应用公钥格式
     */
    private String publicKeyFormat;

    /**
     * 应用公钥内容
     */
    private String publicKey;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    private Boolean deleteFlag;
}
