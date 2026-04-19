package com.g2rain.basis.service;

import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.basis.dto.ControlDomainDto;
import com.g2rain.basis.dto.ControlDomainSelectDto;
import com.g2rain.basis.vo.ControlDomainVo;

import java.util.List;

/**
 * 控制域表服务接口
 * 表名: control_domain
 *
 * @author Alpha
 */
public interface ControlDomainService {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return VO 对象列表
     */
    List<ControlDomainVo> selectList(ControlDomainSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页 VO 数据
     */
    PageData<ControlDomainVo> selectPage(PageSelectListDto<ControlDomainSelectDto> selectDto);

    /**
     * 新增或更新数据
     *
     * @param dto 数据传输对象
     * @return 操作结果（影响行数）
     */
    Long save(ControlDomainDto dto);

    /**
     * 根据 ID 删除数据
     *
     * @param id 主键 ID
     * @return 操作结果（影响行数）
     */
    int delete(Long id);
}
