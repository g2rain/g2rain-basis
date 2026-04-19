package com.g2rain.basis.dao;

import com.g2rain.basis.dao.po.ControlUnitPo;
import com.g2rain.basis.dto.ControlUnitSelectDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 控制单元表数据访问接口
 * 表名: control_unit
 *
 * @author Alpha
 */
@Mapper
public interface ControlUnitDao {

    /**
     * 插入单条记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int insert(ControlUnitPo entity);

    /**
     * 批量插入记录
     *
     * @param list 实体对象列表
     * @return 影响行数
     */
    int insertMultiple(List<ControlUnitPo> list);

    /**
     * 根据 ID 更新记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int update(ControlUnitPo entity);

    /**
     * 根据 ID 更新记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int updateSelective(ControlUnitPo entity);

    /**
     * 根据 ID 删除记录
     *
     * @param id 主键 ID
     * @return 影响行数
     */
    int delete(Long id);

    /**
     * 根据ID和Version更新记录（乐观锁更新）
     *
     * @param entity 实体对象（必须包含version字段）
     * @return 影响行数
     */
    int updateByVersion(ControlUnitPo entity);

    /**
     * 根据 ID 查询记录
     *
     * @param id 主键 ID
     * @return 实体对象
     */
    ControlUnitPo selectById(Long id);

    /**
     * 根据查询入参 DTO 筛选列表
     *
     * @param selectDto 查询条件 DTO
     * @return 实体对象列表
     */
    List<ControlUnitPo> selectList(ControlUnitSelectDto selectDto);

    /**
     * 查询控制单元数量
     *
     * @param selectDto 控制单元信息
     * @return 控制单元数量
     */
    Long checkControlUnitExists(ControlUnitSelectDto selectDto);
}
