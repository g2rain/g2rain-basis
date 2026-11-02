package com.g2rain.basis.vo;

import com.g2rain.common.json.AdminCompanyCondition;
import com.g2rain.common.json.ConditionalJsonIgnore;
import com.g2rain.common.model.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 匿名访客表返回VO
 * 关联表名: anonymous_visitor
 * 功能：封装接口返回数据，继承BaseVo复用基础字段逻辑，隔离数据库实体与前端展示层
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "匿名访客表 VO")
public class AnonymousVisitorVo extends BaseVo {

    /**
     * 机构标识
     */
    @Schema(description = "机构标识")
    private Long organId;

    /**
     * DPoP 公钥指纹（base64url，区分大小写）
     */
    @Schema(description = "DPoP 公钥指纹（base64url，区分大小写）")
    private String keyThumbprint;

    /**
     * 首次进入时的接入码标识
     */
    @Schema(description = "首次进入时的接入码标识")
    private Long entryId;

    /**
     * 最近换票或刷新时间
     */
    @Schema(description = "最近换票或刷新时间")
    private LocalDateTime lastSeenTime;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    @Schema(description = "删除标识（0 未删除，1 已删除）", example = "false")
    @ConditionalJsonIgnore(adminCompany = AdminCompanyCondition.TRUE)
    private Boolean deleteFlag;
}
