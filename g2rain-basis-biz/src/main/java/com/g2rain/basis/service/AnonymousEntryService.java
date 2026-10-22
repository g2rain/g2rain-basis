package com.g2rain.basis.service;

import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.basis.dto.AnonymousEntryDto;
import com.g2rain.basis.dto.AnonymousEntrySelectDto;
import com.g2rain.basis.dto.UpdateStatusDto;
import com.g2rain.basis.vo.AnonymousEntryVo;
import com.g2rain.basis.vo.OrganVo;

import java.util.List;

/**
 * 匿名接入码表服务接口
 * 表名: anonymous_entry
 *
 * @author G2rain Generator
 */
public interface AnonymousEntryService {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return VO 对象列表
     */
    List<AnonymousEntryVo> selectList(AnonymousEntrySelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页 VO 数据
     */
    PageData<AnonymousEntryVo> selectPage(PageSelectListDto<AnonymousEntrySelectDto> selectDto);

    /**
     * 新增或更新数据
     *
     * @param dto 数据传输对象
     * @return 操作结果（影响行数）
     */
    Long save(AnonymousEntryDto dto);

    /**
     * 根据 ID 删除数据
     *
     * @param id 主键 ID
     * @return 操作结果（影响行数）
     */
    int delete(Long id);

    /**
     * 修改接入码状态，并通知网关失效该机构的 ANONYMOUS 权限快照
     *
     * @param id  主键 ID
     * @param dto 目标状态
     * @return 影响行数
     */
    int updateStatus(Long id, UpdateStatusDto dto);

    /**
     * 按接入码解析匿名入口：须有效且属于该应用
     *
     * @param entryKey      接入码
     * @param applicationId 应用 ID
     * @return 接入码
     */
    AnonymousEntryVo resolve(String entryKey, Long applicationId);

    /**
     * 按接入码 ID 解析匿名入口（刷新时使用）：须有效且属于该应用
     *
     * @param id            接入码 ID
     * @param applicationId 应用 ID
     * @return 接入码
     */
    AnonymousEntryVo resolve(Long id, Long applicationId);

    /**
     * 校验机构可开放匿名入口：机构有效且已开通该应用（运营机构与租户机构均可）
     *
     * @param organId       机构 ID
     * @param applicationId 应用 ID
     * @return 机构
     */
    OrganVo requireOpenOrgan(Long organId, Long applicationId);
}