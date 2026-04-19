package com.g2rain.basis.dao;

import com.g2rain.basis.dao.po.ResourceMenuPo;
import com.g2rain.basis.dto.ResourceMenuSelectDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 应用资源菜单表数据访问接口
 * 表名: resource_menu
 *
 * @author Alpha
 */
@Mapper
public interface ResourceMenuDao {

    /**
     * 插入单条记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int insert(ResourceMenuPo entity);

    /**
     * 批量插入记录
     *
     * @param list 实体对象列表
     * @return 影响行数
     */
    int insertMultiple(List<ResourceMenuPo> list);

    /**
     * 根据 ID 更新记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int update(ResourceMenuPo entity);

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
    int updateByVersion(ResourceMenuPo entity);

    /**
     * 根据 ID 查询记录
     *
     * @param id 主键 ID
     * @return 实体对象
     */
    ResourceMenuPo selectById(Long id);

    /**
     * 根据查询入参 DTO 筛选列表
     *
     * @param selectDto 查询条件 DTO
     * @return 实体对象列表
     */
    List<ResourceMenuPo> selectList(ResourceMenuSelectDto selectDto);

    /**
     * 查询菜单数量
     *
     * @param selectDto 菜单信息
     * @return 菜单数量
     */
    Long checkMenuExists(ResourceMenuSelectDto selectDto);

    /**
     * 查询指定用户的授权菜单(通过角色分配的菜单权限)
     * 仅返回用户角色关联控制单元对应的菜单资源(扁平列表)
     * 层级与排序在应用层组装。
     *
     * @param userId         用户 ID
     * @param applicationIds 需要查询的应用 ID 集合
     * @return 用户可访问的菜单列表（扁平结构）
     */
    List<ResourceMenuPo> selectAuthorizedMenusWithUserId(@Param("userId") Long userId, @Param("applicationIds") Set<Long> applicationIds);

    /**
     * 查询指定应用的常驻授权菜单(landing=1 的控制单元菜单)
     * 不依赖用户角色，常驻功能直接可用(扁平列表)
     * 层级与排序在应用层组装。
     *
     * @param applicationId 应用 ID
     * @return 常驻菜单列表（扁平结构）
     */
    List<ResourceMenuPo> listAuthorizedMenusWithLanding(@Param("applicationId") Long applicationId);
}
