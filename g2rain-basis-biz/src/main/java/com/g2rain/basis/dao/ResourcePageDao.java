package com.g2rain.basis.dao;

import com.g2rain.basis.dao.po.ResourcePagePo;
import com.g2rain.basis.dto.ResourcePageSelectDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 应用资源页面表数据访问接口
 * 表名: resource_page
 *
 * @author Alpha
 */
@Mapper
public interface ResourcePageDao {

    /**
     * 插入单条记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int insert(ResourcePagePo entity);

    /**
     * 批量插入记录
     *
     * @param list 实体对象列表
     * @return 影响行数
     */
    int insertMultiple(List<ResourcePagePo> list);

    /**
     * 根据 ID 更新记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int update(ResourcePagePo entity);

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
    int updateByVersion(ResourcePagePo entity);

    /**
     * 批量更新页面资源（使用 CASE WHEN，一次 SQL 更新多条记录）
     *
     * @param list 页面实体列表
     * @return 受影响行数
     */
    int updateBatchCase(@Param("list") List<ResourcePagePo> list);

    /**
     * 根据 ID 查询记录
     *
     * @param id 主键 ID
     * @return 实体对象
     */
    ResourcePagePo selectById(Long id);

    /**
     * 根据查询入参 DTO 筛选列表
     *
     * @param selectDto 查询条件 DTO
     * @return 实体对象列表
     */
    List<ResourcePagePo> selectList(ResourcePageSelectDto selectDto);

    /**
     * 根据资源标识查询资源数量
     *
     * @param selectDto 查询条件
     * @return 资源数量
     */
    Long checkResourcePageExists(ResourcePageSelectDto selectDto);

    /**
     * 查询指定用户的授权页面(通过角色分配的页面权限)
     * 仅返回用户角色关联控制单元对应的页面资源
     *
     * @param userId        用户 ID
     * @param applicationId 需要查询的应用 ID
     * @return 用户可访问的页面列表
     */
    List<ResourcePagePo> selectAuthorizedPagesWithUserId(@Param("userId") Long userId, @Param("applicationId") Long applicationId);

    /**
     * 查询指定应用的常驻授权页面(landing=1 的控制单元页面)
     * 不依赖用户角色, 常驻功能直接可用
     *
     * @param applicationId 应用 ID
     * @return 常驻页面列表
     */
    List<ResourcePagePo> listAuthorizedPagesWithLanding(Long applicationId);
}
