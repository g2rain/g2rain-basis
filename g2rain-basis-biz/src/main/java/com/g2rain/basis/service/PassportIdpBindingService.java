package com.g2rain.basis.service;

import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.basis.dto.PassportIdpBindingDto;
import com.g2rain.basis.dto.PassportIdpBindingSelectDto;
import com.g2rain.basis.vo.PassportIdpBindingVo;

import java.util.List;

/**
 * 账号与外部身份源绑定表服务接口
 * 表名: passport_idp_binding
 *
 * @author G2rain Generator
 */
public interface PassportIdpBindingService {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件DTO
     * @return VO对象列表
     */
    List<PassportIdpBindingVo> selectList(PassportIdpBindingSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页VO数据
     */
    PageData<PassportIdpBindingVo> selectPage(PageSelectListDto<PassportIdpBindingSelectDto> selectDto);

    /**
     * 新增或更新数据
     *
     * @param dto 数据传输对象
     * @return 操作结果（影响行数）
     */
    Long save(PassportIdpBindingDto dto);

    /**
     * 根据ID删除数据
     *
     * @param id 主键ID
     * @return 操作结果（影响行数）
     */
    int delete(Long id);
}