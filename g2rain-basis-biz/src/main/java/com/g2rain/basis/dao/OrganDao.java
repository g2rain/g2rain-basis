package com.g2rain.basis.dao;

import com.g2rain.basis.dao.po.OrganPo;
import com.g2rain.basis.dto.OrganSelectDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 机构表数据访问接口
 * 表名: organ
 *
 * @author Alpha
 */
@Mapper
public interface OrganDao {

    /**
     * 插入单条记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int insert(OrganPo entity);

    /**
     * 批量插入记录
     *
     * @param list 实体对象列表
     * @return 影响行数
     */
    int insertMultiple(List<OrganPo> list);

    /**
     * 根据 ID 更新记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int update(OrganPo entity);

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
    int updateByVersion(OrganPo entity);

    /**
     * 根据 ID 查询记录
     *
     * @param id 主键 ID
     * @return 实体对象
     */
    OrganPo selectById(Long id);

    /**
     * 根据查询入参 DTO 筛选列表
     *
     * @param selectDto 查询条件 DTO
     * @return 实体对象列表
     */
    List<OrganPo> selectList(OrganSelectDto selectDto);

    /**
     * 根据查询入参 DTO 筛选列表
     *
     * @param selectDto 查询条件 DTO
     * @return 实体对象列表
     */
    List<OrganPo> selectListIsolation(OrganSelectDto selectDto);

    /**
     * 根据机构名称模糊匹配机构
     *
     * @param organId   机构标识
     * @param organName 机构名称
     * @return 实体对象列表
     */
    List<OrganPo> searchOrgans(@Param("organId") Long organId, @Param("organName") String organName);
}
