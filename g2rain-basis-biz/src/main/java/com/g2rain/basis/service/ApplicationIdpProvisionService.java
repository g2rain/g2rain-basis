package com.g2rain.basis.service;

import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.basis.dto.ApplicationIdpProvisionDto;
import com.g2rain.basis.dto.ApplicationIdpProvisionSelectDto;
import com.g2rain.basis.vo.ApplicationIdpProvisionVo;

import java.util.List;

/**
 * 外部身份源应用与平台应用的绑定服务接口
 * 表名: application_idp_provision
 *
 * @author G2rain Generator
 */
public interface ApplicationIdpProvisionService {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件DTO
     * @return VO对象列表
     */
    List<ApplicationIdpProvisionVo> selectList(ApplicationIdpProvisionSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页VO数据
     */
    PageData<ApplicationIdpProvisionVo> selectPage(PageSelectListDto<ApplicationIdpProvisionSelectDto> selectDto);

    /**
     * 新增或更新数据
     *
     * @param dto 数据传输对象
     * @return 操作结果（影响行数）
     */
    Long save(ApplicationIdpProvisionDto dto);

    /**
     * 根据ID删除数据
     *
     * @param id 主键ID
     * @return 操作结果（影响行数）
     */
    int delete(Long id);
}