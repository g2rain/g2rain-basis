package com.g2rain.basis.service;

import com.g2rain.basis.dto.AuditEventDto;
import com.g2rain.basis.dto.AuditEventSelectDto;
import com.g2rain.basis.vo.AuditEventVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;

import java.util.List;

/**
 * 审计事件领域服务（表 {@code audit_event}：网关/管道写入与条件查询）。
 *
 * @author G2rain Generator
 */
public interface AuditEventService {

    /**
     * 条件查询审计事件列表（不分页）。
     *
     * @param selectDto 查询条件，含 {@link com.g2rain.common.model.BaseSelectListDto} 通用筛选项
     * @return 审计事件 VO 列表
     */
    List<AuditEventVo> selectList(AuditEventSelectDto selectDto);

    /**
     * 条件分页查询审计事件。
     *
     * @param selectDto 分页参数 + 业务查询条件
     * @return 分页数据（总数 + 当前页列表）
     */
    PageData<AuditEventVo> selectPage(PageSelectListDto<AuditEventSelectDto> selectDto);

    /**
     * 新增或更新数据
     *
     * @param dto 数据传输对象
     * @return 操作结果（影响行数）
     */
    Long save(AuditEventDto dto);

    /**
     * 根据 ID 删除数据
     *
     * @param id 主键 ID
     * @return 操作结果（影响行数）
     */
    int delete(Long id);
}
