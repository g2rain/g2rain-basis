package com.g2rain.basis.service;

import com.g2rain.basis.dto.ControlUnitDto;
import com.g2rain.basis.dto.ControlUnitSelectDto;
import com.g2rain.basis.dto.UpdateStatusDto;
import com.g2rain.basis.vo.ControlUnitVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;

import java.util.List;

/**
 * 控制单元表服务接口
 * 表名: control_unit
 *
 * @author Alpha
 */
public interface ControlUnitService {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return VO 对象列表
     */
    List<ControlUnitVo> selectList(ControlUnitSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页 VO 数据
     */
    PageData<ControlUnitVo> selectPage(PageSelectListDto<ControlUnitSelectDto> selectDto);

    /**
     * 新增或更新数据
     *
     * @param dto 数据传输对象
     * @return 操作结果（影响行数）
     */
    Long save(ControlUnitDto dto);

    /**
     * 根据 ID 删除数据
     *
     * @param id 主键 ID
     * @return 操作结果（影响行数）
     */
    int delete(Long id);

    /**
     * 修改控制单元发布状态
     *
     * @param id  控制单元标识
     * @param dto 包含新状态信息的数据传输对象
     * @return 数据库中受影响的行数（通常为 1 表示成功，0 表示没有变化）
     */
    int updateStatus(Long id, UpdateStatusDto dto);
}
