package com.g2rain.basis.dao;

import com.g2rain.basis.dao.po.ApiEndpointPo;
import com.g2rain.basis.dao.po.AuthorityApiEndpointPo;
import com.g2rain.basis.dto.ApiEndpointSelectDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 接口地址表数据访问接口
 * 表名: api_endpoint
 *
 * @author Alpha
 */
@Mapper
public interface ApiEndpointDao {

    /**
     * 插入单条记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int insert(ApiEndpointPo entity);

    /**
     * 批量插入记录
     *
     * @param list 实体对象列表
     * @return 影响行数
     */
    int insertMultiple(List<ApiEndpointPo> list);

    /**
     * 根据 ID 更新记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int update(ApiEndpointPo entity);

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
    int updateByVersion(ApiEndpointPo entity);

    /**
     * 根据 ID 查询记录
     *
     * @param id 主键 ID
     * @return 实体对象
     */
    ApiEndpointPo selectById(Long id);

    /**
     * 根据查询入参 DTO 筛选列表
     *
     * @param selectDto 查询条件 DTO
     * @return 实体对象列表
     */
    List<ApiEndpointPo> selectList(ApiEndpointSelectDto selectDto);

    /**
     * 根据接口地址的标签
     *
     * @return 标签集合
     */
    List<String> selectApiTags();

    /**
     * 查询指定用户的授权接口地址(通过角色分配的接口地址权限)
     * 仅返回用户角色关联控制单元对应的接口地址资源
     *
     * @param userId        用户 ID
     * @param applicationId 需要查询的应用 ID
     * @return 用户可访问的接口地址列表
     */
    List<AuthorityApiEndpointPo> selectAuthorizedApiEndpointsWithUserId(@Param("userId") Long userId, @Param("applicationId") Long applicationId);

    /**
     * 查询指定应用的常驻授权接口地址(landing=1 的控制单元接口地址)
     * 不依赖用户角色, 常驻功能直接可用
     *
     * @param applicationId 应用 ID
     * @return 常驻接口地址列表
     */
    List<AuthorityApiEndpointPo> listAuthorizedApiEndpointsWithLanding(Long applicationId);
}
