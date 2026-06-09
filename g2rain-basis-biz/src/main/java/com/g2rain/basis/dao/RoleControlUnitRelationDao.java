package com.g2rain.basis.dao;

import com.g2rain.basis.dao.po.ControlUnitStatPo;
import com.g2rain.basis.dao.po.CountRoleControlUnitPo;
import com.g2rain.basis.dao.po.RoleControlUnitRelationPo;
import com.g2rain.basis.dto.RoleControlUnitRelationSelectDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 角色控制单元关联表数据访问接口
 * 表名: role_control_unit_relation
 *
 * @author Alpha
 */
@Mapper
public interface RoleControlUnitRelationDao {

    /**
     * 插入单条记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int insert(RoleControlUnitRelationPo entity);

    /**
     * 批量插入记录
     *
     * @param list 实体对象列表
     * @return 影响行数
     */
    int insertMultiple(List<RoleControlUnitRelationPo> list);

    /**
     * 根据 ID 更新记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int update(RoleControlUnitRelationPo entity);

    /**
     * 关停统计控制单元数量（cnt 查询）
     */
    List<ControlUnitStatPo> countActivatedUnits(@Param("roleId") Long roleId, @Param("authorizationId") Long authorizationId);

    /**
     * 查询需要激活的控制单元
     */
    List<Long> findUnitsToActivate(@Param("roleId") Long roleId, @Param("authorizationId") Long authorizationId);

    /**
     * 关停 ADMIN 角色控制单元
     */
    int deactivateAdminUnits(@Param("roleId") Long roleId, @Param("authorizationId") Long authorizationId, @Param("controlUnitIds") List<Long> controlUnitIds);

    /**
     * 关停/激活机构下所有角色控制单元
     */
    int updateOrganUnitsStatus(@Param("organId") Long organId, @Param("authorizationId") Long authorizationId, @Param("controlUnitIds") List<Long> controlUnitIds, @Param("status") String status);

    /**
     * 根据 ID 删除记录
     *
     * @param id 主键 ID
     * @return 影响行数
     */
    int delete(Long id);

    /**
     * 根据角色 ID 删除数据
     *
     * @param roleId 角色 ID
     * @return 操作结果（影响行数）
     */
    int deleteByRoleId(Long roleId);

    /**
     * 根据 roleId 删除记录
     *
     * @param roleId         角色 ID
     * @param controlUnitIds 控制单元集合
     * @return 影响行数
     */
    int deleteByControlUnitIds(@Param("roleId") Long roleId, @Param("controlUnitIds") Set<Long> controlUnitIds);

    /**
     * 根据ID和Version更新记录（乐观锁更新）
     *
     * @param entity 实体对象（必须包含version字段）
     * @return 影响行数
     */
    int updateByVersion(RoleControlUnitRelationPo entity);

    /**
     * 根据 ID 查询记录
     *
     * @param id 主键 ID
     * @return 实体对象
     */
    RoleControlUnitRelationPo selectById(Long id);

    /**
     * 根据查询入参 DTO 筛选列表
     *
     * @param selectDto 查询条件 DTO
     * @return 实体对象列表
     */
    List<RoleControlUnitRelationPo> selectList(RoleControlUnitRelationSelectDto selectDto);

    /**
     * 查询角色控制单元数量
     *
     * @param selectDto 查询条件
     * @return 控制单元数量
     */
    Long checkRoleControlUnitExists(RoleControlUnitRelationSelectDto selectDto);

    /**
     * 查询角色控制单元数量
     *
     * @param userId 用户 ID
     * @return 控制单元数量
     */
    CountRoleControlUnitPo countRoleControlUnits(@Param("userId") Long userId);

    /**
     * 按角色 ID 集合统计控制单元数量
     *
     * @param roleIds 角色 ID 集合
     * @return 控制单元数量
     */
    CountRoleControlUnitPo countRoleControlUnitsByRoleIds(@Param("roleIds") List<Long> roleIds);
}
