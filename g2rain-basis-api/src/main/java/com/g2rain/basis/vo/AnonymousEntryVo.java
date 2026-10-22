package com.g2rain.basis.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.g2rain.common.model.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;

import com.g2rain.common.json.ConditionalJsonIgnore;
import com.g2rain.common.json.AdminCompanyCondition;

/**
 * 匿名接入码表返回VO
 * 关联表名: anonymous_entry
 * 功能：封装接口返回数据，继承BaseVo复用基础字段逻辑，隔离数据库实体与前端展示层
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "匿名接入码表 VO")
public class AnonymousEntryVo extends BaseVo {

    /**
     * 机构标识（租户机构）
     */
    @Schema(description = "机构标识（租户机构）")
    private Long organId;

    /**
     * 应用标识
     */
    @Schema(description = "应用标识")
    private Long applicationId;

    /**
     * 接入码名称
     */
    @Schema(description = "接入码名称")
    private String entryName;

    /**
     * 接入码（ek_ 前缀 + base62 随机串，区分大小写）
     */
    @Schema(description = "接入码（ek_ 前缀 + base62 随机串，区分大小写）")
    private String entryKey;

    /**
     * 接入码状态[ACTIVE:有效, INACTIVE:无效]
     */
    @Schema(description = "接入码状态[ACTIVE:有效, INACTIVE:无效]")
    private String status;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    @Schema(description = "删除标识（0 未删除，1 已删除）", example = "false")
    @ConditionalJsonIgnore(adminCompany = AdminCompanyCondition.TRUE)
    private Boolean deleteFlag;
}