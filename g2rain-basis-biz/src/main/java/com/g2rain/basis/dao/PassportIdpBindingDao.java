package com.g2rain.basis.dao;

import com.g2rain.basis.dao.po.PassportIdpBindingPo;
import com.g2rain.basis.dto.PassportIdpBindingSelectDto;
import com.g2rain.data.isolation.annotations.IgnoreIsolation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 账号与外部身份源绑定表数据访问接口
 * 表名: passport_idp_binding
 *
 * @author G2rain Generator
 */
@Mapper
public interface PassportIdpBindingDao {

    /**
     * 插入单条记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int insert(PassportIdpBindingPo entity);

    /**
     * 批量插入记录
     *
     * @param list 实体对象列表
     * @return 影响行数
     */
    int insertMultiple(List<PassportIdpBindingPo> list);

    /**
     * 根据ID更新记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int update(PassportIdpBindingPo entity);

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
    int updateByVersion(PassportIdpBindingPo entity);

    /**
     * 根据ID查询记录
     *
     * @param id 主键ID
     * @return 实体对象
     */
    PassportIdpBindingPo selectById(Long id);

    /**
     * 根据查询入参DTO筛选列表
     *
     * @param selectDto 查询条件DTO
     * @return 实体对象列表
     */
    List<PassportIdpBindingPo> selectList(PassportIdpBindingSelectDto selectDto);

    /**
     * 根据查询入参 DTO 筛选列表（非隔离语义，供 IdP 登录解析使用）。
     */
    @IgnoreIsolation
    List<PassportIdpBindingPo> selectListWithoutIsolation(PassportIdpBindingSelectDto selectDto);

    /**
     * 统计通行证在指定身份源主体与应用下的有效绑定行数。
     */
    int countByPassportIdAndIdpKeys(
        @Param("passportId") Long passportId,
        @Param("idpType") String idpType,
        @Param("idpSubject") String idpSubject,
        @Param("idpApplicationCode") String idpApplicationCode,
        @Param("bindMode") String bindMode
    );
}