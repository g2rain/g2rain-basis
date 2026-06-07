package com.g2rain.basis.dao;

import com.g2rain.basis.dao.po.ApplicationAuthorizationPo;
import com.g2rain.basis.dto.ApplicationAuthorizationSelectDto;
import com.g2rain.data.isolation.annotations.DataIsolation;
import com.g2rain.data.isolation.annotations.IgnoreIsolation;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 应用授权记录表数据访问接口
 * 表名: application_authorization
 *
 * @author Alpha
 */
@Mapper
@DataIsolation(organIdPropertyName = "organId", organIdColumnName = "organ_id")
public interface ApplicationAuthorizationDao {

    /**
     * 插入单条记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int insert(ApplicationAuthorizationPo entity);

    /**
     * 批量插入记录
     *
     * @param list 实体对象列表
     * @return 影响行数
     */
    int insertMultiple(List<ApplicationAuthorizationPo> list);

    /**
     * 根据 ID 更新记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int update(ApplicationAuthorizationPo entity);

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
    int updateByVersion(ApplicationAuthorizationPo entity);

    /**
     * 根据 ID 查询记录
     *
     * @param id 主键 ID
     * @return 实体对象
     */
    @IgnoreIsolation
    ApplicationAuthorizationPo selectById(Long id);

    /**
     * 根据查询入参 DTO 筛选列表
     *
     * @param selectDto 查询条件 DTO
     * @return 实体对象列表
     */
    List<ApplicationAuthorizationPo> selectList(ApplicationAuthorizationSelectDto selectDto);

    /**
     * 根据应用标识查询授权记录数量
     *
     * @param selectDto 应用标识
     * @return 授权记录数量
     */
    @IgnoreIsolation
    Long checkApplicationAuthorizationExists(ApplicationAuthorizationSelectDto selectDto);
}
