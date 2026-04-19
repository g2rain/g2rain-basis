package com.g2rain.basis.dao;

import com.g2rain.basis.dao.po.ControlDomainControlUnitRelationPo;
import com.g2rain.basis.dto.ControlDomainControlUnitRelationSelectDto;
import com.g2rain.basis.model.ControlDomainPair;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 控制域控制单元关联表数据访问接口
 * 表名: control_domain_control_unit_relation
 *
 * @author Alpha
 */
@Mapper
public interface ControlDomainControlUnitRelationDao {

    /**
     * 插入单条记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int insert(ControlDomainControlUnitRelationPo entity);

    /**
     * 批量插入记录
     *
     * @param list 实体对象列表
     * @return 影响行数
     */
    int insertMultiple(List<ControlDomainControlUnitRelationPo> list);

    /**
     * 根据 ID 更新记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int update(ControlDomainControlUnitRelationPo entity);

    /**
     * 根据 ID 删除记录
     *
     * @param id 主键 ID
     * @return 影响行数
     */
    int delete(Long id);

    /**
     * 根据控制域标识删除记录
     *
     * @param controlDomainId 控制域标识
     * @return 影响行数
     */
    int deleteByControlDomainId(Long controlDomainId);

    /**
     * 根据控制域标识删除记录
     *
     * @param controlDomainId 控制域标识
     * @return 影响行数
     */
    int deleteByControlDomainId(@Param("controlDomainId") Long controlDomainId, @Param("controlUnitIds") Set<Long> controlUnitIds);

    /**
     * 根据ID和Version更新记录（乐观锁更新）
     *
     * @param entity 实体对象（必须包含version字段）
     * @return 影响行数
     */
    int updateByVersion(ControlDomainControlUnitRelationPo entity);

    /**
     * 根据 ID 查询记录
     *
     * @param id 主键 ID
     * @return 实体对象
     */
    ControlDomainControlUnitRelationPo selectById(Long id);

    /**
     * 根据查询入参 DTO 筛选列表
     *
     * @param selectDto 查询条件 DTO
     * @return 实体对象列表
     */
    List<ControlDomainControlUnitRelationPo> selectList(ControlDomainControlUnitRelationSelectDto selectDto);

    /**
     * 查询控制域控制单元数量
     *
     * @param selectDto 查询条件
     * @return 控制单元数量
     */
    Long checkControlDomainControlUnitExists(ControlDomainControlUnitRelationSelectDto selectDto);

    /**
     * 批量查询已存在的关联记录
     *
     * @param pairs List<ControlDomainPair> 每个 Pair 包含 controlDomainId 和 controlUnitId
     */
    List<ControlDomainControlUnitRelationPo> selectByControlDomainUnitPairs(@Param("pairs") List<ControlDomainPair> pairs);
}
