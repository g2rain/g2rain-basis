package com.g2rain.basis.service;

import com.g2rain.basis.dto.ControlDomainControlUnitRelationSelectDto;
import com.g2rain.basis.dto.ControlDomainControlUnitRelationsDto;
import com.g2rain.basis.vo.ControlDomainControlUnitRelationVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;

import java.util.List;

/**
 * 控制域控制单元关联表服务接口
 * 表名: control_domain_control_unit_relation
 *
 * @author Alpha
 */
public interface ControlDomainControlUnitRelationService {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return VO 对象列表
     */
    List<ControlDomainControlUnitRelationVo> selectList(ControlDomainControlUnitRelationSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页 VO 数据
     */
    PageData<ControlDomainControlUnitRelationVo> selectPage(PageSelectListDto<ControlDomainControlUnitRelationSelectDto> selectDto);

    /**
     * 新增数据
     *
     * @param dto 数据传输对象
     * @return 操作结果（影响行数）
     */
    Integer save(ControlDomainControlUnitRelationsDto dto);
}
