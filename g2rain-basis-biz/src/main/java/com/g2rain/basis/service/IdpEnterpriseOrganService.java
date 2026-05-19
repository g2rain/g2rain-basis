package com.g2rain.basis.service;

import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.basis.dto.IdpEnterpriseOrganDto;
import com.g2rain.basis.dto.IdpEnterpriseOrganSelectDto;
import com.g2rain.basis.vo.IdpEnterpriseOrganVo;

import java.util.List;

/**
 * 外部企业/租户与平台机构关联表服务接口
 * 表名: idp_enterprise_organ
 *
 * @author G2rain Generator
 */
public interface IdpEnterpriseOrganService {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件DTO
     * @return VO对象列表
     */
    List<IdpEnterpriseOrganVo> selectList(IdpEnterpriseOrganSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页VO数据
     */
    PageData<IdpEnterpriseOrganVo> selectPage(PageSelectListDto<IdpEnterpriseOrganSelectDto> selectDto);

    /**
     * 新增或更新数据
     *
     * @param dto 数据传输对象
     * @return 操作结果（影响行数）
     */
    Long save(IdpEnterpriseOrganDto dto);

    /**
     * 根据ID删除数据
     *
     * @param id 主键ID
     * @return 操作结果（影响行数）
     */
    int delete(Long id);
}