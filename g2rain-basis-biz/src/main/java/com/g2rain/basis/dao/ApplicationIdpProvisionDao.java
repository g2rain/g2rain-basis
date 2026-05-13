package com.g2rain.basis.dao;

import com.g2rain.basis.dao.po.ApplicationIdpProvisionPo;
import com.g2rain.basis.dto.ApplicationIdpProvisionSelectDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 外部身份源应用与平台应用的绑定数据访问接口
 * 表名: application_idp_provision
 *
 * @author G2rain Generator
 */
@Mapper
public interface ApplicationIdpProvisionDao {

    /**
     * 插入单条记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int insert(ApplicationIdpProvisionPo entity);

    /**
     * 批量插入记录
     *
     * @param list 实体对象列表
     * @return 影响行数
     */
    int insertMultiple(List<ApplicationIdpProvisionPo> list);

    /**
     * 根据ID更新记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int update(ApplicationIdpProvisionPo entity);

    /**
     * 根据ID删除记录
     *
     * @param id 主键ID
     * @return 影响行数
     */
    int delete(Long id);

    /**
     * 根据ID和Version更新记录（乐观锁更新）
     *
     * @param entity 实体对象（必须包含version字段）
     * @return 影响行数
     */
    int updateByVersion(ApplicationIdpProvisionPo entity);

    /**
     * 根据ID查询记录
     *
     * @param id 主键ID
     * @return 实体对象
     */
    ApplicationIdpProvisionPo selectById(Long id);

    /**
     * 根据查询入参DTO筛选列表
     *
     * @param selectDto 查询条件DTO
     * @return 实体对象列表
     */
    List<ApplicationIdpProvisionPo> selectList(ApplicationIdpProvisionSelectDto selectDto);

    /**
     * 统计平台应用下是否已为指定身份源应用（IdP 类型 + IdP 侧应用标识）建立 provision。
     */
    int countByApplicationIdAndIdp(
        @Param("applicationId") Long applicationId,
        @Param("idpType") String idpType,
        @Param("idpApplicationCode") String idpApplicationCode
    );
}