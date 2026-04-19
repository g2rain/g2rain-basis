package com.g2rain.basis.dao;

import com.g2rain.basis.dao.po.ResourceApiEndpointPo;
import com.g2rain.basis.dao.po.ResourceApiPo;
import com.g2rain.basis.dto.ResourceApiEndpointSelectDto;
import com.g2rain.basis.dto.ResourceApiSelectDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 应用资源接口地址表数据访问接口
 * 表名: resource_api_endpoint
 *
 * @author Alpha
 */
@Mapper
public interface ResourceApiEndpointDao {

    /**
     * 插入单条记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int insert(ResourceApiEndpointPo entity);

    /**
     * 批量插入记录
     *
     * @param list 实体对象列表
     * @return 影响行数
     */
    int insertMultiple(List<ResourceApiEndpointPo> list);

    /**
     * 根据 ID 更新记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int update(ResourceApiEndpointPo entity);

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
    int updateByVersion(ResourceApiEndpointPo entity);

    /**
     * 根据 ID 查询记录
     *
     * @param id 主键 ID
     * @return 实体对象
     */
    ResourceApiEndpointPo selectById(Long id);

    /**
     * 根据查询入参 DTO 筛选列表
     *
     * @param selectDto 查询条件 DTO
     * @return 实体对象列表
     */
    List<ResourceApiEndpointPo> selectList(ResourceApiEndpointSelectDto selectDto);

    /**
     * 根据查询入参 DTO 筛选列表
     *
     * @param selectDto 查询条件 DTO
     * @return 实体对象列表
     */
    List<ResourceApiPo> selectListWithApiEndpoint(ResourceApiSelectDto selectDto);

    /**
     * 根据资源标识查询资源数量
     *
     * @param selectDto 查询条件
     * @return 资源数量
     */
    Long checkApiEndpointExists(ResourceApiEndpointSelectDto selectDto);
}
