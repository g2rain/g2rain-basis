package com.g2rain.basis.dao;

import com.g2rain.basis.dao.po.ApplicationSuitePo;
import com.g2rain.basis.dto.ApplicationSuiteSelectDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 应用归类关系表数据访问接口
 * 表名: application_suite
 *
 * @author Alpha
 */
@Mapper
public interface ApplicationSuiteDao {

    /**
     * 插入单条记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int insert(ApplicationSuitePo entity);

    /**
     * 批量插入记录
     *
     * @param list 实体对象列表
     * @return 影响行数
     */
    int insertMultiple(List<ApplicationSuitePo> list);

    /**
     * 根据应用归类标识更新记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int update(ApplicationSuitePo entity);

    /**
     * 根据应用归类标识删除记录
     *
     * @param id 应用归类标识
     * @return 影响行数
     */
    int delete(Long id);

    /**
     * 根据 应用ID 删除记录
     *
     * @param id 应用标识
     * @return 影响行数
     */
    int deleteByApplicationId(Long id);

    /**
     * 根据应用归类标识删除记录
     *
     * @param applicationId        应用标识
     * @param masterApplicationIds 主应用标识集合
     * @return 影响行数
     */
    int deleteByMasterApplicationIds(@Param("applicationId") Long applicationId, @Param("masterApplicationIds") Set<Long> masterApplicationIds);

    /**
     * 根据ID和Version更新记录（乐观锁更新）
     *
     * @param entity 实体对象（必须包含version字段）
     * @return 影响行数
     */
    int updateByVersion(ApplicationSuitePo entity);

    /**
     * 根据应用归类标识查询记录
     *
     * @param id 应用归类标识
     * @return 实体对象
     */
    ApplicationSuitePo selectById(Long id);

    /**
     * 根据查询入参 DTO 筛选列表
     *
     * @param selectDto 查询条件 DTO
     * @return 实体对象列表
     */
    List<ApplicationSuitePo> selectList(ApplicationSuiteSelectDto selectDto);

    /**
     * 根据应用标识查询应用归类数量
     *
     * @param id 应用归类标识
     * @return 集成应用数量
     */
    Long checkApplicationSuiteExists(Long id);
}
