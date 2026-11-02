package com.g2rain.basis.dao.po;

import com.g2rain.common.model.BasePo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 匿名接入码表返回Po
 * 关联表名: anonymous_entry
 * 功能：封装实体数据，继承BasePo复用基础字段逻辑
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AnonymousEntryPo extends BasePo {

    /**
     * 机构标识（租户机构）
     */
    private Long organId;

    /**
     * 应用标识
     */
    private Long applicationId;

    /**
     * 接入码名称
     */
    private String entryName;

    /**
     * 接入码（ek_ 前缀 + base62 随机串，区分大小写）
     */
    private String entryKey;

    /**
     * 接入码状态[ACTIVE:有效, INACTIVE:无效]
     */
    private String status;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    private Boolean deleteFlag;
}