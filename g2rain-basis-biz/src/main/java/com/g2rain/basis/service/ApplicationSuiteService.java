package com.g2rain.basis.service;

import com.g2rain.basis.dto.ApplicationSuiteDto;
import com.g2rain.basis.dto.ApplicationSuiteSelectDto;
import com.g2rain.basis.vo.ApplicationSuiteVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;

import java.util.List;

/**
 * 应用归类关系表服务接口
 * 表名: application_suite
 *
 * @author Alpha
 */
public interface ApplicationSuiteService {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return VO 对象列表
     */
    List<ApplicationSuiteVo> selectList(ApplicationSuiteSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页 VO 数据
     */
    PageData<ApplicationSuiteVo> selectPage(PageSelectListDto<ApplicationSuiteSelectDto> selectDto);

    /**
     * 新增或更新数据
     *
     * @param dto 数据传输对象
     * @return 操作结果（影响行数）
     */
    Integer save(ApplicationSuiteDto dto);
}
