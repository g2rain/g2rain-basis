package com.g2rain.basis.service;

import com.g2rain.basis.vo.OrganHierarchicalRelationVo;
import com.g2rain.common.enums.OrganType;
import com.g2rain.common.exception.BusinessException;

import java.util.List;

/**
 * 机构路径关系表服务接口
 * 表名: organ_closure
 *
 * @author Alpha
 */
public interface OrganClosureService {

    /**
     * 查询所有机构的层级关系
     *
     * @return 包含所有机构层级关系的列表
     */
    List<OrganHierarchicalRelationVo> fetchAllHierarchicalRelations();

    /**
     * 查询指定机构的层级关系
     *
     * @param organId 指定的机构 ID
     * @return 包含指定机构层级关系的列表
     */
    List<OrganHierarchicalRelationVo> fetchHierarchicalRelations(Long organId);

    /**
     * 检查指定机构是否存在与指定祖先机构的层级关系
     *
     * @param childId  子机构 ID
     * @param parentId 祖先机构 ID
     * @return 如果两个ID都为空, 返回 false;
     * 如果 childId 等于 parentId, 返回 true;
     * 否则查询数据库判断层级关系是否存在
     */
    boolean checkHierarchyRelation(Long childId, Long parentId);

    /**
     * 断言子机构类型可以挂载到父机构类型(纯校验，无任何副作用)。
     * <p>
     * 如果父机构类型为 {@code null}，或子类型和父类型不允许挂载，则会抛出异常。
     * </p>
     *
     * @param childType  子机构类型，允许为空
     * @param parentType 父机构类型，不能为空
     * @throws BusinessException 当父机构类型为空或挂载不允许时抛出
     */
    void assertAssociate(OrganType childType, OrganType parentType);

    /**
     * 挂载子组织到父组织的层级关系。
     *
     * @param childId    子组织 ID，不能为空
     * @param childType  子组织类型，不能为空
     * @param parentId   父组织 ID，可为空（为空时只创建自关联）
     * @param parentType 父组织类型，当 parentId 非空时不能为空
     * @throws BusinessException 如果存在循环依赖、非法父子类型或非法自关联
     */
    void mount(Long childId, OrganType childType, Long parentId, OrganType parentType);

    /**
     * 卸载子节点与父节点的平铺层级关系。
     *
     * @param childId  子节点 ID
     * @param parentId 父节点 ID
     * @throws BusinessException 当尝试卸载自关联或非直属子节点时抛出
     */
    void unmount(Long childId, Long parentId);

    /**
     * 将子节点从原父节点迁移到目标父节点的平铺层级关系。
     *
     * @param childId          子节点 ID
     * @param childType        子节点类型
     * @param targetParentId   目标父节点 ID
     * @param targetParentType 目标父节点类型
     * @param sourceParentId   原父节点 ID
     * @throws BusinessException 当自关联、非直属或循环依赖校验失败时抛出
     */
    void migrate(Long childId, OrganType childType, Long targetParentId, OrganType targetParentType, Long sourceParentId);

    /**
     * 根据 ID 删除数据
     *
     * @param id 主键 ID
     * @return 操作结果（影响行数）
     */
    int delete(Long id);
}
