package com.g2rain.basis.dao;

import com.g2rain.basis.dao.po.OrganClosurePo;
import com.g2rain.basis.dao.po.OrganHierarchicalRelationPo;
import com.g2rain.basis.dto.OrganClosureSelectDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 机构路径关系表数据访问接口
 * 表名: organ_closure
 *
 * @author Alpha
 */
@Mapper
public interface OrganClosureDao {

    /**
     * 插入单条记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int insert(OrganClosurePo entity);

    /**
     * 批量插入记录
     *
     * @param list 实体对象列表
     * @return 影响行数
     */
    int insertMultiple(List<OrganClosurePo> list);

    /**
     * 根据 ID 更新记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int update(OrganClosurePo entity);

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
    int updateByVersion(OrganClosurePo entity);

    /**
     * 根据 ID 查询记录
     *
     * @param id 主键 ID
     * @return 实体对象
     */
    OrganClosurePo selectById(Long id);

    /**
     * 根据查询入参 DTO 筛选列表
     *
     * @param selectDto 查询条件 DTO
     * @return 实体对象列表
     */
    List<OrganClosurePo> selectList(OrganClosureSelectDto selectDto);

    /**
     * 根据查询入参 DTO 查询直属关系的父节点数量
     *
     * @param selectDto 查询条件 DTO
     * @return 直属关系的父节点数量
     */
    Long checkOrganRelationExists(OrganClosureSelectDto selectDto);

    /**
     * 查询所有机构的层级关系
     *
     * @return 包含所有机构层级关系的列表
     */
    List<OrganHierarchicalRelationPo> fetchAllHierarchicalRelations();

    /**
     * 查询指定机构的层级关系
     *
     * @param organId 指定的机构 ID
     * @return 包含指定机构层级关系的列表
     */
    List<OrganHierarchicalRelationPo> fetchHierarchicalRelations(@Param("organId") Long organId);

    /**
     * 查询指定子节点与父节点之间是否存在平铺层级关系。
     *
     * @param descendantId 子节点 ID
     * @param ancestorId   父节点 ID
     * @return 匹配的组织层级关系集合，如果不存在返回空列表
     */
    List<OrganClosurePo> hasFlatRelation(@Param("descendantId") Long descendantId, @Param("ancestorId") Long ancestorId);

    /**
     * 查询涉及指定子节点及目标/源父节点的组织层级关系，并对结果加锁。
     * <p>
     * 主要用于挂载、迁移、卸载或删除操作前，确保读取数据的一致性。
     * </p>
     *
     * @param descendantId     子节点 ID
     * @param targetAncestorId 目标父节点ID，可为空
     * @param sourceAncestorId 原父节点ID，可为空
     * @return 匹配的组织层级关系集合，如果不存在返回空列表
     */
    List<OrganClosurePo> selectRelationsWithLock(@Param("descendantId") Long descendantId, @Param("targetAncestorId") Long targetAncestorId, @Param("sourceAncestorId") Long sourceAncestorId);

    /**
     * 查询已存在的组织层级关系，用于检查挂载/迁移操作中父子关系的存在性。
     *
     * @param superiors    父节点 ID 集合
     * @param subordinates 子节点 ID 集合
     * @return 匹配的组织层级关系集合，如果不存在返回空列表
     */
    List<OrganClosurePo> selectRelationsForCheck(@Param("superiors") Set<Long> superiors, @Param("subordinates") Set<Long> subordinates);

    /**
     * 批量递增 path_count，用于挂载、迁移操作增加引用次数。
     *
     * @param ids 待递增 path_count 的记录ID集合
     * @return 影响的行数
     */
    int incrementPathCountBatch(List<Long> ids);

    /**
     * 批量递减 path_count，用于卸载、迁移操作减少引用次数。
     *
     * @param ids 待递减 path_count 的记录ID集合
     * @return 影响的行数
     */
    int decrementPathCountBatch(List<Long> ids);

    /**
     * 批量删除指定的层级关系记录（逻辑删除）。
     * <p>
     * 该方法主要应用于“卸载（unmount）”与“迁移（migrate）”操作中，用于移除不再存在的父子层级关系。
     * 具体表现为将 delete_flag 设置为 1，并更新记录时间。
     * <br/>
     * 在卸载操作中：会删除 child 与其旧的父节点及所有祖先之间的平铺关系。
     * <br/>
     * 在迁移操作中：会删除 child 在旧父节点链路上的所有平铺关系，为重新建立新父节点链路腾出空间。
     * </p>
     *
     * @param ids 需要逻辑删除的平铺层级关系记录 ID 集合
     * @return 影响的行数
     */
    int deleteBatch(List<Long> ids);

    /**
     * 删除指定子节点对应的所有层级关系（逻辑删除）。
     * <p>
     * 该方法用于删除组织机构（organ）实体时的清理操作。
     * <br/>
     * 当组织节点被永久删除（非卸载）时，需要移除该节点在平铺层级表中的所有记录，包括：
     * <ul>
     *   <li>该节点与其所有父节点的层级关系</li>
     *   <li>该节点的自关联关系（SELF_ASSOCIATION）</li>
     * </ul>
     * 此方法不会影响该节点的子节点，因为删除组织机构必须是叶子节点，因而不会产生悬挂数据。
     * </p>
     *
     * @param descendantId 被删除的组织节点ID（必须为叶子节点）
     * @return 影响的行数
     */
    int deleteByOrganId(Long descendantId);
}
