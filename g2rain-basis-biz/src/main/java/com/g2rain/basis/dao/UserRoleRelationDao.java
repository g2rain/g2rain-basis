package com.g2rain.basis.dao;

import com.g2rain.basis.dao.po.UserRoleRelationPo;
import com.g2rain.basis.dto.UserRoleRelationSelectDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 用户角色关联表数据访问接口
 * 表名: user_role_relation
 *
 * @author Alpha
 */
@Mapper
public interface UserRoleRelationDao {

    /**
     * 插入单条记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int insert(UserRoleRelationPo entity);

    /**
     * 批量插入记录
     *
     * @param list 实体对象列表
     * @return 影响行数
     */
    int insertMultiple(List<UserRoleRelationPo> list);

    /**
     * 根据 ID 更新记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int update(UserRoleRelationPo entity);

    /**
     * 根据 ID 删除记录
     *
     * @param id 主键 ID
     * @return 影响行数
     */
    int delete(Long id);

    /**
     * 根据用户 ID 删除记录
     *
     * @param userId 用户 ID
     * @return 影响行数
     */
    int deleteByUserId(Long userId);

    /**
     * 根据用户 ID 集合批量删除记录
     *
     * @param roleId  角色 ID
     * @param userIds 用户 ID 集合
     * @return 影响行数
     */
    int deleteByUserIds(@Param("roleId") Long roleId, @Param("userIds") Set<Long> userIds);

    /**
     * 根据ID和Version更新记录（乐观锁更新）
     *
     * @param entity 实体对象（必须包含version字段）
     * @return 影响行数
     */
    int updateByVersion(UserRoleRelationPo entity);

    /**
     * 根据 ID 查询记录
     *
     * @param id 主键 ID
     * @return 实体对象
     */
    UserRoleRelationPo selectById(Long id);

    /**
     * 根据查询入参 DTO 筛选列表
     *
     * @param selectDto 查询条件 DTO
     * @return 实体对象列表
     */
    List<UserRoleRelationPo> selectList(UserRoleRelationSelectDto selectDto);

    /**
     * 根据角色 ID 查询用户关联梳理
     *
     * @param selectDto 用户角色信息
     * @return 用户关联梳理
     */
    Long checkUserRoleExists(UserRoleRelationSelectDto selectDto);
}
