package com.g2rain.basis.service;

import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.basis.dto.AnonymousVisitorDto;
import com.g2rain.basis.dto.AnonymousVisitorSelectDto;
import com.g2rain.basis.vo.AnonymousVisitorVo;

import java.util.List;

/**
 * 匿名访客表服务接口
 * 表名: anonymous_visitor
 *
 * @author G2rain Generator
 */
public interface AnonymousVisitorService {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return VO 对象列表
     */
    List<AnonymousVisitorVo> selectList(AnonymousVisitorSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页 VO 数据
     */
    PageData<AnonymousVisitorVo> selectPage(PageSelectListDto<AnonymousVisitorSelectDto> selectDto);

    /**
     * 新增或更新数据
     *
     * @param dto 数据传输对象
     * @return 操作结果（影响行数）
     */
    Long save(AnonymousVisitorDto dto);

    /**
     * 根据 ID 删除数据
     *
     * @param id 主键 ID
     * @return 操作结果（影响行数）
     */
    int delete(Long id);

    /**
     * 按机构 + 公钥指纹解析访客：已存在则刷新最近访问时间，不存在则创建
     *
     * @param organId    机构 ID
     * @param thumbprint DPoP 公钥指纹（RFC 7638，由 IAM 计算）
     * @param entryId    本次使用的接入码 ID（仅首次创建时记录）
     * @return 访客 ID（visitorId）
     */
    Long resolve(Long organId, String thumbprint, Long entryId);
}