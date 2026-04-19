package com.g2rain.basis.service;

import com.g2rain.basis.dto.ControlUnitResourceRelationSelectDto;
import com.g2rain.basis.dto.ControlUnitResourceRelationsDto;
import com.g2rain.basis.vo.ControlUnitResourceRelationVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;

import java.util.List;

/**
 * 权限点资源关联表服务接口
 * 表名: control_unit_resource_relation
 *
 * @author Alpha
 */
public interface ControlUnitResourceRelationService {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return VO 对象列表
     */
    List<ControlUnitResourceRelationVo> selectList(ControlUnitResourceRelationSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页 VO 数据
     */
    PageData<ControlUnitResourceRelationVo> selectPage(PageSelectListDto<ControlUnitResourceRelationSelectDto> selectDto);

    /**
     * 新增或更新数据
     *
     * @param dto 数据传输对象
     * @return 操作结果（影响行数）
     */
    Integer save(ControlUnitResourceRelationsDto dto);
}
