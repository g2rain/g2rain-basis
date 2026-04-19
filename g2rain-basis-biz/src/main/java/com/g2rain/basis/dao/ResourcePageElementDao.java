package com.g2rain.basis.dao;

import com.g2rain.basis.dao.po.AuthorityPageElementPo;
import com.g2rain.basis.dao.po.ResourcePageElementPo;
import com.g2rain.basis.dto.ResourcePageElementSelectDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 应用资源页面元素表数据访问接口
 * 表名: resource_page_element
 *
 * @author Alpha
 */
@Mapper
public interface ResourcePageElementDao {

    /**
     * 插入单条记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int insert(ResourcePageElementPo entity);

    /**
     * 批量插入记录
     *
     * @param list 实体对象列表
     * @return 影响行数
     */
    int insertMultiple(List<ResourcePageElementPo> list);

    /**
     * 根据 ID 更新记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int update(ResourcePageElementPo entity);

    /**
     * 批量更新页面元素（使用 CASE WHEN，一次 SQL 更新多条记录）
     *
     * @param list 页面元素实体列表
     * @return 受影响行数
     */
    int updateBatchCase(@Param("list") List<ResourcePageElementPo> list);

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
    int updateByVersion(ResourcePageElementPo entity);

    /**
     * 根据 ID 查询记录
     *
     * @param id 主键 ID
     * @return 实体对象
     */
    ResourcePageElementPo selectById(Long id);

    /**
     * 根据查询入参 DTO 筛选列表
     *
     * @param selectDto 查询条件 DTO
     * @return 实体对象列表
     */
    List<ResourcePageElementPo> selectList(ResourcePageElementSelectDto selectDto);

    /**
     * 查询页面元素数量
     *
     * @param selectDto 资源页面元素信息
     * @return 页面元素数量
     */
    Long checkPageElementExists(ResourcePageElementSelectDto selectDto);

    /**
     * 查询指定用户的授权页面元素(通过角色分配的页面元素权限)
     * 仅返回用户角色关联控制单元对应的页面元素资源
     *
     * @param userId        用户 ID
     * @param applicationId 需要查询的应用 ID
     * @return 用户可访问的页面元素列表
     */
    List<AuthorityPageElementPo> selectAuthorizedPageElementsWithUserId(@Param("userId") Long userId, @Param("applicationId") Long applicationId);

    /**
     * 查询指定应用的常驻授权页面元素(landing=1 的控制单元页面元素)
     * 不依赖用户角色, 常驻功能直接可用
     *
     * @param applicationId 应用 ID
     * @return 常驻页面元素列表
     */
    List<AuthorityPageElementPo> listAuthorizedPageElementsWithLanding(Long applicationId);
}
