package com.g2rain.basis.dao.po;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 控制单元统计信息实体类，用于记录控制单元的相关统计数据。
 *
 * <p>包含控制单元标识及其激活数量等信息。</p>
 *
 * @author alpha
 * @since 2026/1/20
 */
@Setter
@Getter
@NoArgsConstructor
public class ControlUnitStatPo {

    /**
     * 控制单元标识
     */
    private Long controlUnitId;

    /**
     * 控制单元被激活的次数
     */
    private Integer activatedCount;
}
