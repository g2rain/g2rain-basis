package com.g2rain.basis.dao;

import com.g2rain.basis.dao.po.ControlUnitResourceRelationPo;
import com.g2rain.basis.dto.ControlUnitResourceRelationSelectDto;
import com.g2rain.basis.model.ControlUnitPair;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 权限点资源关联表数据访问接口
 * 表名: control_unit_resource_relation
 *
 * @author Alpha
 */
@Mapper
public interface ControlUnitResourceRelationDao {

    /**
     * 插入单条记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int insert(ControlUnitResourceRelationPo entity);

    /**
     * 批量插入记录
     *
     * @param list 实体对象列表
     * @return 影响行数
     */
    int insertMultiple(List<ControlUnitResourceRelationPo> list);

    /**
     * 根据 ID 更新记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int update(ControlUnitResourceRelationPo entity);

    /**
     * 批量根据 ID 更新记录
     *
     * @param resources 实体对象集合
     * @return 影响行数
     */
    int updateBatch(List<ControlUnitResourceRelationPo> resources);

    /**
     * 根据 ID 删除记录
     *
     * @param id 主键 ID
     * @return 影响行数
     */
    int delete(Long id);

    /**
     * 根据 ID 删除记录
     *
     * @param controlUnitId 控制单元 ID
     * @return 影响行数
     */
    int deleteByControlUnitId(@Param("controlUnitId") Long controlUnitId);

    /**
     * 根据 controlUnitId 删除记录
     *
     * @param controlUnitId 控制单元 ID
     * @param resourceIds   资源集合
     * @return 影响行数
     */
    int deleteByResourceIds(@Param("controlUnitId") Long controlUnitId, @Param("resourceIds") Set<Long> resourceIds);

    /**
     * 根据ID和Version更新记录（乐观锁更新）
     *
     * @param entity 实体对象（必须包含version字段）
     * @return 影响行数
     */
    int updateByVersion(ControlUnitResourceRelationPo entity);

    /**
     * 根据 ID 查询记录
     *
     * @param id 主键 ID
     * @return 实体对象
     */
    ControlUnitResourceRelationPo selectById(Long id);

    /**
     * 根据查询入参 DTO 筛选列表
     *
     * @param selectDto 查询条件 DTO
     * @return 实体对象列表
     */
    List<ControlUnitResourceRelationPo> selectList(ControlUnitResourceRelationSelectDto selectDto);

    /**
     * 根据资源标识查询资源数量
     *
     * @param selectDto 查询条件
     * @return 资源数量
     */
    Long checkResourceExists(ControlUnitResourceRelationSelectDto selectDto);

    /**
     * 批量查询已存在的关联记录
     *
     * @param pairs List<ControlUnitPair> 每个 Pair 包含 controlUnitId 和 resourceId 和 resourceType
     */
    List<ControlUnitResourceRelationPo> selectByControlUnitResourcePairs(@Param("pairs") List<ControlUnitPair> pairs);
}
