package com.g2rain.basis.dao;

import com.g2rain.basis.dao.po.ControlDomainPo;
import com.g2rain.basis.dto.ControlDomainSelectDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 控制域表数据访问接口
 * 表名: control_domain
 *
 * @author Alpha
 */
@Mapper
public interface ControlDomainDao {

    /**
     * 插入单条记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int insert(ControlDomainPo entity);

    /**
     * 批量插入记录
     *
     * @param list 实体对象列表
     * @return 影响行数
     */
    int insertMultiple(List<ControlDomainPo> list);

    /**
     * 根据 ID 更新记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int update(ControlDomainPo entity);

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
    int updateByVersion(ControlDomainPo entity);

    /**
     * 根据 ID 查询记录
     *
     * @param id 主键 ID
     * @return 实体对象
     */
    ControlDomainPo selectById(Long id);

    /**
     * 根据查询入参 DTO 筛选列表
     *
     * @param selectDto 查询条件 DTO
     * @return 实体对象列表
     */
    List<ControlDomainPo> selectList(ControlDomainSelectDto selectDto);

    /**
     * 根据应用标识查询控制域数量
     *
     * @param selectDto 应用标识
     * @return 控制域数量
     */
    Long checkControlDomainExists(ControlDomainSelectDto selectDto);
}
