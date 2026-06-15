package com.g2rain.basis.dao;

import com.g2rain.basis.dao.po.ApplicationPo;
import com.g2rain.basis.dto.ApplicationSelectDto;
import com.g2rain.data.isolation.annotations.DataIsolation;
import com.g2rain.data.isolation.annotations.IgnoreIsolation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 应用表数据访问接口
 * 表名: application
 *
 * @author Alpha
 */
@Mapper
@DataIsolation(organIdPropertyName = "organId", organIdColumnName = "organ_id")
public interface ApplicationDao {

    /**
     * 插入单条记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int insert(ApplicationPo entity);

    /**
     * 批量插入记录
     *
     * @param list 实体对象列表
     * @return 影响行数
     */
    int insertMultiple(List<ApplicationPo> list);

    /**
     * 根据应用标识更新记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int update(ApplicationPo entity);

    /**
     * 根据应用标识局部更新记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int updateSelective(ApplicationPo entity);

    /**
     * 根据应用标识删除记录
     *
     * @param id 主键应用标识
     * @return 影响行数
     */
    int delete(Long id);

    /**
     * 根据应用标识和版本更新记录（乐观锁更新）
     *
     * @param entity 实体对象（必须包含version字段）
     * @return 影响行数
     */
    int updateByVersion(ApplicationPo entity);

    /**
     * 根据应用标识查询记录
     *
     * @param id 应用标识
     * @return 实体对象
     */
    @IgnoreIsolation
    ApplicationPo selectById(Long id);

    /**
     * 根据查询入参 DTO 筛选列表
     *
     * @param selectDto 查询条件 DTO
     * @return 实体对象列表
     */
    @IgnoreIsolation
    List<ApplicationPo> selectList(ApplicationSelectDto selectDto);

    /**
     * 根据查询入参 DTO 筛选列表
     *
     * @param selectDto 查询条件 DTO
     * @return 实体对象列表
     */
    List<ApplicationPo> selectListIsolation(ApplicationSelectDto selectDto);

    /**
     * 查询入口应用可见的机构激活应用作用域
     *
     * @param userId        用户标识
     * @param applicationId 入口应用标识
     * @return 实体对象列表
     */
    @IgnoreIsolation
    List<ApplicationPo> selectApplicationScope(@Param("userId") Long userId, @Param("applicationId") Long applicationId);

    /**
     * 按角色 ID 集合查询入口应用可见的机构激活应用作用域
     *
     * @param roleIds         角色 ID 集合
     * @param applicationId   入口应用标识
     * @return 实体对象列表
     */
    @IgnoreIsolation
    List<ApplicationPo> selectApplicationScopeByRoleIds(@Param("roleIds") List<Long> roleIds,
                                                        @Param("applicationId") Long applicationId);
}
