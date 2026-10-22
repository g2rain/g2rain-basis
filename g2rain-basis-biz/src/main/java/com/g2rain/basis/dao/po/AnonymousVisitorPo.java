package com.g2rain.basis.dao.po;

import com.g2rain.common.model.BasePo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 匿名访客表返回Po
 * 关联表名: anonymous_visitor
 * 功能：封装实体数据，继承BasePo复用基础字段逻辑
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AnonymousVisitorPo extends BasePo {

    /**
     * 机构标识
     */
    private Long organId;

    /**
     * DPoP 公钥指纹（base64url，区分大小写）
     */
    private String keyThumbprint;

    /**
     * 首次进入时的接入码标识
     */
    private Long entryId;

    /**
     * 最近换票或刷新时间
     */
    private LocalDateTime lastSeenTime;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    private Boolean deleteFlag;
}
