package com.g2rain.basis.dao;

import com.g2rain.basis.dao.po.AuthorityApiEndpointPo;
import com.g2rain.basis.dao.po.ResourceApiPo;
import com.g2rain.basis.dto.ResourceApiSelectDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 资源接口表数据访问接口
 * 表名: resource_api
 *
 * @author G2rain Generator
 */
@Mapper
public interface ResourceApiDao {

    /**
     * 插入单条记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int insert(ResourceApiPo entity);

    /**
     * 批量插入记录
     *
     * @param list 实体对象列表
     * @return 影响行数
     */
    int insertMultiple(List<ResourceApiPo> list);

    /**
     * 根据 ID 更新记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int update(ResourceApiPo entity);

    /**
     * 批量更新接口资源（使用 CASE WHEN，一次 SQL 更新多条记录）
     *
     * @param list 页面实体列表
     * @return 受影响行数
     */
    int updateBatchCase(@Param("list") List<ResourceApiPo> list);

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
    int updateByVersion(ResourceApiPo entity);

    /**
     * 根据 ID 查询记录
     *
     * @param id 主键 ID
     * @return 实体对象
     */
    ResourceApiPo selectById(Long id);

    /**
     * 根据查询入参 DTO 筛选列表
     *
     * @param selectDto 查询条件 DTO
     * @return 实体对象列表
     */
    List<ResourceApiPo> selectList(ResourceApiSelectDto selectDto);


    /**
     * 查询指定用户的授权接口地址(通过角色分配的接口地址权限)
     * 仅返回用户角色关联控制单元对应的接口地址资源
     *
     * @param userId        用户 ID
     * @param applicationId 需要查询的应用 ID
     * @return 用户可访问的接口地址列表
     */
    List<AuthorityApiEndpointPo> selectAuthorizedApisWithUserId(@Param("userId") Long userId, @Param("applicationId") Long applicationId);

    /**
     * 查询指定应用的常驻授权接口地址(landing=1 的控制单元接口地址)
     * 不依赖用户角色, 常驻功能直接可用
     *
     * @param applicationId 应用 ID
     * @return 常驻接口地址列表
     */
    List<AuthorityApiEndpointPo> listAuthorizedApisWithLanding(Long applicationId);
}
