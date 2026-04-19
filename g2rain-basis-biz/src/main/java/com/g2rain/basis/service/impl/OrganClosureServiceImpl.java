package com.g2rain.basis.service.impl;

import com.g2rain.basis.dao.OrganClosureDao;
import com.g2rain.basis.dao.po.OrganClosurePo;
import com.g2rain.basis.dao.po.OrganHierarchicalRelationPo;
import com.g2rain.basis.dto.OrganClosureSelectDto;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.enums.OrganRelation;
import com.g2rain.basis.model.OrganHierarchyNode;
import com.g2rain.basis.service.OrganClosureService;
import com.g2rain.basis.vo.OrganHierarchicalRelationVo;
import com.g2rain.common.enums.OrganType;
import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.id.IdGenerator;
import com.g2rain.common.utils.Asserts;
import com.g2rain.common.utils.Moments;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 机构路径关系表服务实现类
 *
 * <p>本类负责组织层级关系的挂载、卸载、迁移及删除操作，核心表为 {@code organ_closure}。
 * 使用自反闭包 (Reflexive Closure) 技术存储节点与自身的关系，以支持：
 * <ul>
 *     <li>多层级结构查询</li>
 *     <li>交叉挂载（同一个节点可被多个祖先引用）</li>
 *     <li>循环依赖防护</li>
 *     <li>并发操作一致性</li>
 * </ul>
 *
 * <p>主要技术点说明：
 * <ul>
 *     <li><b>自关联 (Self Association)</b>：每个节点与自身都有一条记录，保证无父节点时仍可查询。</li>
 *     <li><b>path_count</b>：表示节点被引用次数，用于交叉挂载和安全删除逻辑。</li>
 *     <li><b>循环依赖防护</b>：挂载和迁移前通过查询判断，禁止形成父子循环。</li>
 *     <li><b>事务隔离与锁</b>：涉及层级变更的方法 {@code mount, unmount, migrate, delete}
 *         必须在 {@link org.springframework.transaction.annotation.Isolation#READ_COMMITTED} 隔离级别下执行。
 *         通过 {@code selectRelationsWithLock} + {@code FOR UPDATE} 锁住相关记录，保证并发安全。
 *         <ul>
 *             <li>第一次查询：锁住相关节点，防止其他事务修改。</li>
 *             <li>第二次查询：获取最新数据，确保 path_count 与实际树结构一致。</li>
 *         </ul>
 *     </li>
 *     <li><b>批量处理</b>：BATCH_SIZE 控制每次批量插入或更新数量，避免单次操作压力过大。</li>
 *     <li><b>删除策略</b>：只允许删除叶子节点，避免树结构碎片化。</li>
 * </ul>
 *
 * <p>方法职责：
 * <ul>
 *     <li>{@link #mount(Long, OrganType, Long, OrganType)}：挂载子节点到父节点。</li>
 *     <li>{@link #unmount(Long, Long)}：卸载子节点与父节点的关联。</li>
 *     <li>{@link #migrate(Long, OrganType, Long, OrganType, Long)}：迁移子节点从源父节点到目标父节点。</li>
 *     <li>{@link #delete(Long)}：删除叶子节点及其平铺关系。</li>
 * </ul>
 *
 * <p>注意事项：
 * <ul>
 *     <li>所有 `调用方` 变更方法必须在 {@code READ_COMMITTED} 事务下调用。</li>
 *     <li>重复查询逻辑用于确保锁定后的数据是最新状态，防止并发修改导致 path_count 错乱或树结构破坏。</li>
 * </ul>
 *
 * @author Alpha
 */
@Service(value = "organClosureServiceImpl")
public class OrganClosureServiceImpl implements OrganClosureService {

    /**
     * 批量处理大小，用于 insert/update/delete 批量提交，防止一次性操作过大
     */
    private static final int BATCH_SIZE = 2000;

    /**
     * DAO 层对象，操作 organ_closure 表
     */
    @Resource(name = "organClosureDao")
    private OrganClosureDao organClosureDao;

    /**
     * ID 生成器，用于生成主键
     */
    private IdGenerator idGenerator;

    /**
     * 设置 ID 生成器
     *
     * @param idGenerator ID 生成器实例
     */
    @Qualifier("idGenerator")
    @Autowired(required = false)
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    /**
     * 查询所有机构的层级关系
     *
     * @return 包含所有机构层级关系的列表
     */
    @Override
    public List<OrganHierarchicalRelationVo> fetchAllHierarchicalRelations() {
        return buildTrees(organClosureDao.fetchAllHierarchicalRelations());
    }

    /**
     * 查询指定机构的层级关系
     *
     * @param organId 指定的机构 ID
     * @return 包含指定机构层级关系的列表
     */
    @Override
    public List<OrganHierarchicalRelationVo> fetchHierarchicalRelations(Long organId) {
        return buildTrees(organClosureDao.fetchHierarchicalRelations(organId));
    }

    /**
     * 检查指定机构是否存在与指定祖先机构的层级关系
     *
     * @param childId  子机构 ID
     * @param parentId 祖先机构 ID
     * @return 如果两个ID都为空, 返回 false;
     * 如果 childId 等于 parentId, 返回 true;
     * 否则查询数据库判断层级关系是否存在
     */
    @Override
    public boolean checkHierarchyRelation(Long childId, Long parentId) {
        if (Objects.isNull(childId) || Objects.isNull(parentId)) {
            return false;
        }

        if (parentId.equals(childId)) {
            return true;
        }

        OrganClosureSelectDto select = new OrganClosureSelectDto();
        select.setAncestorId(parentId);
        select.setDescendantId(childId);
        return organClosureDao.checkOrganRelationExists(select) > 0;
    }

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
    @Override
    public void assertAssociate(OrganType childType, OrganType parentType) {
        Asserts.notNull(parentType, SystemErrorCode.PARAM_REQUIRED, "parentType");
        boolean allowed = parentType.canAssociate(childType);
        Asserts.isTrue(allowed, BasisErrorCode.HIERARCHY_ATTACHMENT_ILLEGAL);
    }

    /**
     * 挂载子组织到父组织的层级关系。
     *
     * <p>方法核心设计思想与技术要点：
     * <ul>
     *     <li><b>事务隔离：</b>此方法必须在 READ_COMMITTED 隔离级别下执行，以保证在并发场景中不会出现脏读或重复写入。</li>
     *     <li><b>加锁策略：</b>
     *         <ul>
     *             <li>通过 <code>selectRelationsWithLock</code> 使用 <code>FOR UPDATE</code> 锁住子节点及相关祖先链。</li>
     *             <li>第一次查询仅用于加锁，第二次查询用于获取最新数据，确保并发安全。</li>
     *         </ul>
     *     </li>
     *     <li><b>循环依赖检查：</b>挂载前先查询是否存在父子关系，防止出现循环依赖。</li>
     *     <li><b>父子类型合法性：</b>验证子组织类型是否可以挂载到指定父组织类型，保证层级关系合法。</li>
     *     <li><b>自关联处理：</b>如果子组织没有父节点，会创建自关联记录，确保自身可查询。</li>
     *     <li><b>交叉挂载与 path_count 管理：</b>
     *         <ul>
     *             <li>允许子节点被多个祖先引用。</li>
     *             <li>已有关系批量递增 path_count，新关系批量插入，确保树结构完整且不会误删。</li>
     *         </ul>
     *     </li>
     *     <li><b>批量处理：</b>使用 BATCH_SIZE 分批提交，保证性能和事务一致性。</li>
     *     <li><b>方法执行顺序：</b>
     *         <ol>
     *             <li>非空和自身关联校验</li>
     *             <li>循环依赖检查</li>
     *             <li>确保子节点自关联</li>
     *             <li>加锁并获取最新父子集合</li>
     *             <li>收集父节点和子节点集合</li>
     *             <li>检查已有关系，决定新增或递增 path_count</li>
     *             <li>批量执行 path_count 增加或批量插入新关系</li>
     *         </ol>
     *     </li>
     * </ul>
     *
     * @param childId    子组织 ID，不能为空
     * @param childType  子组织类型，不能为空
     * @param parentId   父组织 ID，可为空（为空时只创建自关联）
     * @param parentType 父组织类型，当 parentId 非空时不能为空
     * @throws BusinessException 如果存在循环依赖、非法父子类型或非法自关联
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void mount(Long childId, OrganType childType, Long parentId, OrganType parentType) {
        // 1. 前置断言（不会产生任何副作用）
        assertMountable(childId, childType, parentId, parentType);

        // 2. 创建子节点自关联记录
        ensureSelfRelation(childId, childType);

        // 3. 如果父节点为空，仅创建子节点自关联
        if (Objects.isNull(parentId)) {
            return;
        }

        // 4. 第一次查询并加锁，用于锁住子节点及相关父链（FOR UPDATE）
        organClosureDao.selectRelationsWithLock(childId, parentId, null);

        // 5. 判断是否已挂载，避免重复操作
        List<OrganClosurePo> existingDirectRelation = organClosureDao.hasFlatRelation(
            childId, parentId
        );

        if (!existingDirectRelation.isEmpty()) {
            return;
        }

        // 6. 第二次查询获取最新数据，确保加锁后拿到最新父子关系
        List<OrganClosurePo> lockedRelations = organClosureDao.selectRelationsWithLock(
            childId, parentId, null
        );

        // 7. 收集父节点集合
        Set<Long> parentIds = new HashSet<>();
        // 8. 收集子节点集合
        Set<OrganHierarchyNode> childNodes = new HashSet<>();
        for (OrganClosurePo relationPo : lockedRelations) {
            // 查找父节点
            if (relationPo.getDescendantId().equals(parentId)) {
                parentIds.add(relationPo.getAncestorId());
                continue;
            }

            childNodes.add(new OrganHierarchyNode(
                relationPo.getDescendantId(),
                relationPo.getDescendantType()
            ));
        }

        // 9. 如果没有父节点集合，则无需挂载
        if (parentIds.isEmpty()) {
            return;
        }

        // 10. 检查已有关系，避免重复插入
        List<OrganClosurePo> existingAllRelations = organClosureDao.selectRelationsForCheck(parentIds,
            childNodes.stream().map(OrganHierarchyNode::getDescendantId).collect(Collectors.toSet())
        );

        Map<String, Long> existingRelationIdMap = existingAllRelations.stream().collect(Collectors.toMap(
            po -> po.getAncestorId() + "-" + po.getDescendantId(), OrganClosurePo::getId, (e1, e2) -> e1
        ));

        // 11. 构建新增挂载关系
        List<OrganClosurePo> newRelationsToInsert = buildTargetRelations(
            parentIds, childNodes, existingRelationIdMap, parentId, childId
        );

        // 12. 批量递增已有关系的 path_count
        List<Long> ids = new ArrayList<>(existingRelationIdMap.values());
        Collections.sort(ids); // [新增：规避死锁的关键]强制物理加锁顺序一致
        batchProcessPathCount(ids, true);

        // 13. 批量插入新的挂载关系
        batchProcess(newRelationsToInsert);
    }

    /**
     * 卸载子节点与父节点的平铺层级关系。
     *
     * <p>方法逻辑说明：
     * <ol>
     *   <li>参数非空校验，保证 childId 与 parentId 必须提供。</li>
     *   <li>禁止自关联卸载（即子节点不能卸载自己）。</li>
     *   <li>使用 {@code selectRelationsWithLock} 查询子节点集合及祖节点集合并加锁
     *       <ul>
     *           <li>加锁目的是保证并发环境下，当前事务处理时其他线程无法修改这些平铺关系。</li>
     *           <li>加锁后再执行查询可以确保获取的数据是最新的（防止脏读或重复写入）。</li>
     *       </ul>
     *   </li>
     *   <li>检查是否存在挂载关系：
     *       <ul>
     *           <li>不存在挂载直接返回，无需卸载操作。</li>
     *           <li>如果存在挂载，且不是直属关系，则抛出异常，避免错误删除非直接子节点。</li>
     *       </ul>
     *   </li>
     *   <li>再次查询锁定后的关系，确保获取最新数据用于 path_count 处理和删除操作。</li>
     *   <li>收集父节点集合（superiors）和子节点集合（subordinates）用于后续操作。</li>
     *   <li>查询 {@code selectRelationsForCheck} 获取交叉挂载的平铺关系记录，用于判断哪些需要减 path_count，哪些需要逻辑删除。</li>
     *   <li>根据 path_count 处理：
     *       <ul>
     *           <li>path_count > 1：批量执行 {@code decrementPathCountBatch} 减少引用次数。</li>
     *           <li>path_count = 1：批量执行 {@code deleteBatch} 执行逻辑删除。</li>
     *       </ul>
     *   </li>
     *   <li>批量操作使用 {@code BATCH_SIZE} 分批处理，提升性能，避免一次性操作过多导致数据库压力过大。</li>
     * </ol>
     *
     * <p>技术要点：
     * <ul>
     *   <li>事务隔离级别使用 {@link Isolation#READ_COMMITTED}，确保读取到的数据是已提交的数据，避免脏读。</li>
     *   <li>使用 FOR UPDATE 对涉及的记录加锁，保证并发环境下数据一致性。</li>
     *   <li>path_count 机制保证多父挂载的节点不会被误删。</li>
     *   <li>重复查询锁定后的数据是为了确保操作基于最新状态，防止并发冲突。</li>
     * </ul>
     *
     * @param childId  子节点 ID
     * @param parentId 父节点 ID
     * @throws BusinessException 当尝试卸载自关联或非直属子节点时抛出
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void unmount(Long childId, Long parentId) {
        // 参数非空校验
        Asserts.notNull(childId, SystemErrorCode.PARAM_REQUIRED, "childId");
        Asserts.notNull(parentId, SystemErrorCode.PARAM_REQUIRED, "parentId");

        // 禁止自关联卸载
        if (Objects.equals(childId, parentId)) {
            throw new BusinessException(BasisErrorCode.SELF_RELATION_ILLEGAL);
        }

        // 第一次查询并加锁，获取子节点及祖节点集合
        organClosureDao.selectRelationsWithLock(childId, parentId, null);

        // 判断是否挂载
        List<OrganClosurePo> exist = organClosureDao.hasFlatRelation(childId, parentId);
        if (exist.isEmpty()) {
            return;
        }

        // 直属关系校验
        if (OrganRelation.nonDirectSubordinate(exist.getFirst().getRelationType())) {
            throw new BusinessException(BasisErrorCode.NON_DIRECT_DELETE_ILLEGAL);
        }

        // 再次查询锁定后的关系，确保获取最新数据
        List<OrganClosurePo> lockedRelations = organClosureDao.selectRelationsWithLock(
            childId, parentId, null
        );

        // 收集父节点集合和子节点集合
        Set<Long> superiors = new HashSet<>();
        Set<Long> subordinates = new HashSet<>();
        for (OrganClosurePo o : lockedRelations) {
            // 查找父节点
            if (o.getDescendantId().equals(parentId)) {
                superiors.add(o.getAncestorId());
                continue;
            }

            subordinates.add(o.getDescendantId());
        }

        // 如果不存在祖节点集合, 没必要挂载, 直接返回
        if (superiors.isEmpty()) {
            return;
        }

        // 查询现有关系，判断 path_count
        List<OrganClosurePo> existRelations = organClosureDao.selectRelationsForCheck(
            superiors, subordinates
        );

        List<Long> toDecrement = new ArrayList<>();
        List<Long> toDelete = new ArrayList<>();
        for (OrganClosurePo po : existRelations) {
            // 说明存在2个以上路径, 进行减法即可
            if (po.getPathCount() > 1) {
                toDecrement.add(po.getId());
            } else {
                toDelete.add(po.getId());
            }
        }

        // 批量减 path_count
        Collections.sort(toDecrement);
        batchProcessPathCount(toDecrement, false);

        // 批量逻辑删除
        Collections.sort(toDelete);
        batchDelete(toDelete);
    }

    /**
     * 将子节点从原父节点迁移到目标父节点的平铺层级关系。
     *
     * <p>方法整体逻辑：
     * <ol>
     *   <li>参数非空校验，保证 childId、childType、sourceParentId、targetParentId、targetParentType 都被提供。</li>
     *   <li>判断是否真正迁移：如果 sourceParentId 与 targetParentId 相同，直接返回，无需迁移。</li>
     *   <li>禁止自关联迁移：子节点不能迁移到自身作为父节点。</li>
     *   <li>挂载关系校验：检查目标父节点类型是否允许关联当前子节点。</li>
     *   <li>循环依赖校验：使用 {@code hasFlatRelation} 查询目标父节点与子节点关系，防止形成循环依赖。</li>
     *   <li>确保子节点自关联存在，保证闭包完整性。</li>
     *   <li>首次查询并加锁（FOR UPDATE）：查询子节点、原父节点祖先和目标父节点祖先
     *       <ul>
     *           <li>加锁保证当前事务修改这些关系时，其他事务无法并发修改。</li>
     *           <li>通过锁定后的数据再查询，可确保后续操作基于最新状态。</li>
     *       </ul>
     *   </li>
     *   <li>判断目标父节点是否已经挂载，如果已经挂载，直接返回。</li>
     *   <li>判断原父节点是否挂载，如果未挂载或非直属关系，则抛出异常。</li>
     *   <li>再次查询锁定后的关系，收集：
     *       <ul>
     *           <li>原父节点的祖先集合（sourceSuperiors）</li>
     *           <li>目标父节点的祖先集合（targetSuperiors）</li>
     *           <li>子节点集合（subordinateNodes）</li>
     *       </ul>
     *   </li>
     *   <li>移除交集公共祖节点：公共祖节点无需迁移或删除。</li>
     *   <li>查询存在的平铺关系 {@code selectRelationsForCheck}，用于区分需要减少 path_count、逻辑删除或新增的关系。</li>
     *   <li>处理原父节点：
     *       <ul>
     *           <li>path_count > 1 → 调用 {@code decrementPathCountBatch} 批量减少引用次数</li>
     *           <li>path_count = 1 → 调用 {@code deleteBatch} 批量逻辑删除</li>
     *       </ul>
     *   </li>
     *   <li>处理目标父节点：
     *       <ul>
     *           <li>已存在的平铺关系 → 调用 {@code incrementPathCountBatch} 批量增加引用次数</li>
     *           <li>不存在的平铺关系 → 构建新的 PO 并调用 {@code insertMultiple} 批量插入</li>
     *       </ul>
     *   </li>
     *   <li>批量操作使用 {@code BATCH_SIZE} 分批处理，提高数据库操作性能。</li>
     * </ol>
     *
     * <p>技术要点：
     * <ul>
     *   <li>事务隔离级别 {@link Isolation#READ_COMMITTED} 保证读取已提交的数据，避免脏读。</li>
     *   <li>FOR UPDATE 锁定子节点和祖先节点集合，确保并发环境下一致性。</li>
     *   <li>path_count 机制保障多父挂载的节点不会被误删。</li>
     *   <li>重复查询锁定后的数据，保证操作基于最新状态。</li>
     *   <li>逻辑删除（delete_flag = 1）保证历史记录可审计。</li>
     * </ul>
     *
     * @param childId          子节点 ID
     * @param childType        子节点类型
     * @param targetParentId   目标父节点 ID
     * @param targetParentType 目标父节点类型
     * @param sourceParentId   原父节点 ID
     * @throws BusinessException 当自关联、非直属或循环依赖校验失败时抛出
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void migrate(Long childId, OrganType childType, Long targetParentId, OrganType targetParentType, Long sourceParentId) {
        // 1. 参数校验
        if (assertMigratable(childId, childType, targetParentId, targetParentType, sourceParentId)) {
            return;
        }

        // 2. 确保子节点自关联存在
        ensureSelfRelation(childId, childType);

        // 3. 第一次查询并锁定, 查询子节点集合和祖节点集合
        organClosureDao.selectRelationsWithLock(childId, targetParentId, sourceParentId);

        // 4. 判断目标父节点是否挂载
        List<OrganClosurePo> existTarget = organClosureDao.hasFlatRelation(childId, targetParentId);
        if (!existTarget.isEmpty()) {
            return;
        }

        // 5. 判断原父节点是否挂载
        List<OrganClosurePo> existSource = organClosureDao.hasFlatRelation(childId, sourceParentId);
        if (existSource.isEmpty()) {
            return;
        }

        // 6. 判断原父节点直属关系
        if (OrganRelation.nonDirectSubordinate(existSource.getFirst().getRelationType())) {
            throw new BusinessException(BasisErrorCode.NON_DIRECT_DELETE_ILLEGAL);
        }

        // 7. 在挂载前，重复查询以确保获取最新数据
        List<OrganClosurePo> lockedRelations = organClosureDao.selectRelationsWithLock(
            childId, targetParentId, sourceParentId
        );

        // 收集原父节点祖先、目标父节点祖先和子节点集合
        Set<Long> sourceSuperiors = new HashSet<>();
        Set<Long> targetSuperiors = new HashSet<>();
        Set<OrganHierarchyNode> subordinateNodes = new HashSet<>();
        collectSuperiorsAndSubordinates(lockedRelations, sourceParentId, targetParentId,
            sourceSuperiors, targetSuperiors, subordinateNodes
        );

        // 移除交集公共祖节点
        Set<Long> intersection = new HashSet<>(sourceSuperiors);
        intersection.retainAll(targetSuperiors);
        sourceSuperiors.removeAll(intersection);
        targetSuperiors.removeAll(intersection);

        // 合并成一个新的集合，用于 selectRelationsForCheck
        Set<Long> merged = new HashSet<>(sourceSuperiors);
        merged.addAll(targetSuperiors);
        if (merged.isEmpty()) {
            return;
        }

        // 查询现有平铺关系
        List<OrganClosurePo> existRelations = organClosureDao.selectRelationsForCheck(merged,
            subordinateNodes.stream().map(OrganHierarchyNode::getDescendantId).collect(Collectors.toSet())
        );

        // 处理原父节点：path_count 或逻辑删除
        List<Long> toSourceDecrement = new ArrayList<>();
        List<Long> toSourceDelete = new ArrayList<>();
        Map<String, Long> targetExistedMap = new HashMap<>();
        processSourceRelations(existRelations, sourceSuperiors, toSourceDecrement, toSourceDelete, targetExistedMap);

        // 构建目标父节点新的关系
        List<OrganClosurePo> result = buildTargetRelations(
            targetSuperiors, subordinateNodes, targetExistedMap, targetParentId, childId
        );

        // 批量处理原父节点 path_count
        Collections.sort(toSourceDecrement);
        batchProcessPathCount(toSourceDecrement, false);

        // 批量逻辑删除原父节点关系
        Collections.sort(toSourceDelete);
        batchDelete(toSourceDelete);

        // 批量增加目标父节点 path_count
        ArrayList<Long> ids = new ArrayList<>(targetExistedMap.values());
        Collections.sort(ids);
        batchProcessPathCount(ids, true);

        // 批量插入目标父节点新关系
        batchProcess(result);
    }

    /**
     * 删除组织节点及其平铺层级关系。
     *
     * <p>方法整体逻辑：
     * <ol>
     *   <li>参数非空校验，确保 organId 被提供。</li>
     *   <li>查询锁定该节点的所有子节点和平铺关系，使用 {@code selectRelationsWithLock} 进行 FOR UPDATE 加锁：
     *       <ul>
     *           <li>保证在并发环境下其他事务无法修改或删除同一节点的平铺关系，确保一致性。</li>
     *       </ul>
     *   </li>
     *   <li>判断节点是否为叶子节点：
     *       <ul>
     *           <li>如果 relations.size() > 1，说明该节点存在子节点，抛出 {@code NON_LEAF_DELETE_ILLEGAL} 异常，禁止删除非叶子节点。</li>
     *       </ul>
     *   </li>
     *   <li>逻辑删除叶子节点：
     *       <ul>
     *           <li>调用 {@code deleteByOrganId}，将 delete_flag 置为 1，保留历史数据用于审计。</li>
     *           <li>不涉及 path_count 扣减，因为整个节点被删除，所有引用同时失效。</li>
     *       </ul>
     *   </li>
     *   <li>事务隔离级别为 {@link Isolation#READ_COMMITTED}：
     *       <ul>
     *           <li>保证读取的数据为已提交状态，避免脏读，同时与 FOR UPDATE 配合确保删除操作安全。</li>
     *       </ul>
     *   </li>
     * </ol>
     *
     * <p>技术要点：
     * <ul>
     *   <li>只允许删除叶子节点，避免破坏层级树结构。</li>
     *   <li>使用 FOR UPDATE 锁定相关记录，确保并发删除操作安全。</li>
     *   <li>逻辑删除而非物理删除，保证审计追踪能力。</li>
     *   <li>方法内部事务隔离级别为 READ_COMMITTED，符合业务操作的安全性要求。</li>
     * </ul>
     *
     * @param organId 待删除组织节点 ID
     * @return 删除的记录数
     * @throws BusinessException 当节点非叶子节点或参数为空时抛出
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public int delete(Long organId) {
        // 参数非空校验
        Asserts.notNull(organId, SystemErrorCode.PARAM_REQUIRED, "organId");

        // 查询并锁定子节点及平铺关系，FOR UPDATE 保证事务安全
        List<OrganClosurePo> relations = organClosureDao.selectRelationsWithLock(
            organId, null, null
        );

        // 非叶子节点禁止删除
        if (relations.size() > 1) {
            throw new BusinessException(BasisErrorCode.NON_LEAF_DELETE_ILLEGAL);
        }

        // 逻辑删除叶子节点
        return organClosureDao.deleteByOrganId(organId);
    }

    /**
     * 校验子节点参数的非空性。
     *
     * @param childId   子节点ID，不能为空
     * @param childType 子节点类型，不能为空
     */
    private void assertChildNode(Long childId, OrganType childType) {
        Asserts.notNull(childId, SystemErrorCode.PARAM_REQUIRED, "childId");
        Asserts.notNull(childType, SystemErrorCode.PARAM_REQUIRED, "childType");
    }

    /**
     * 断言一次机构挂载行为在业务规则上是合法的（纯校验，无任何副作用）。
     *
     * <p>该方法只负责校验“是否允许挂载”，不进行任何数据写入或关系修复，
     * 用于在挂载流程开始前提前失败，避免产生无效的分布式 ID 或中间状态。</p>
     *
     * <p>校验内容包括：</p>
     * <ul>
     *   <li>子节点及其类型合法性校验</li>
     *   <li>禁止子节点挂载到自身</li>
     *   <li>父子类型是否允许建立层级关系</li>
     *   <li>禁止产生循环依赖（父节点已位于子节点下级）</li>
     * </ul>
     *
     * <p>注意：当 {@code parentId} 为空时，表示仅进行自挂载语义的前置校验，
     * 方法直接返回，不触发任何副作用。</p>
     *
     * @param childId    子节点机构 ID
     * @param childType  子节点机构类型
     * @param parentId   父节点机构 ID，可为空
     * @param parentType 父节点机构类型，当 {@code parentId} 不为空时必填
     * @throws BusinessException 当挂载违反业务规则时抛出
     */
    private void assertMountable(Long childId, OrganType childType, Long parentId, OrganType parentType) {
        // 1. 判断非空属性
        assertChildNode(childId, childType);

        // 2. 判断自身关联，子节点不能挂在自己身上
        if (Objects.equals(childId, parentId)) {
            throw new BusinessException(BasisErrorCode.SELF_RELATION_ILLEGAL);
        }

        // 3. 如果父节点为空，仅创建子节点自关联
        if (Objects.isNull(parentId)) {
            return;
        }

        // 4. 检查父子类型能否允许挂载
        assertAssociate(childType, parentType);

        // 5. 循环依赖检查：查询父节点是否已经在子节点的下级
        List<OrganClosurePo> hasCycle = organClosureDao.hasFlatRelation(parentId, childId);
        Asserts.lessThanOrEqual(hasCycle.size(), 0, BasisErrorCode.CIRCULAR_DEPENDENCY_ILLEGAL);
    }

    /**
     * 断言一次机构迁移行为在业务规则上是合法的（纯校验，无任何副作用）。
     * <p>
     * 包括以下校验：
     * <ol>
     *     <li>参数非空</li>
     *     <li>判断是否真正迁移（源父节点和目标父节点不同）</li>
     *     <li>禁止自关联迁移</li>
     *     <li>目标父节点是否可以挂载子节点</li>
     *     <li>防止形成循环依赖</li>
     * </ol>
     *
     * @param childId          子节点ID，不能为空
     * @param childType        子节点类型，不能为空
     * @param targetParentId   目标父节点ID，不能为空
     * @param targetParentType 目标父节点类型，不能为空
     * @param sourceParentId   源父节点ID，不能为空
     * @throws BusinessException 如果存在自关联迁移或循环依赖
     */
    private boolean assertMigratable(Long childId, OrganType childType, Long targetParentId, OrganType targetParentType, Long sourceParentId) {
        // 1. 判断非空属性
        Asserts.notNull(targetParentId, SystemErrorCode.PARAM_REQUIRED, "targetParentId");
        Asserts.notNull(sourceParentId, SystemErrorCode.PARAM_REQUIRED, "sourceParentId");

        // 2. 判断是否真正迁移
        if (Objects.equals(sourceParentId, targetParentId)) {
            return true;
        }

        // 3. 断言是否允许挂载
        assertMountable(childId, childType, targetParentId, targetParentType);
        return false;
    }

    /**
     * 从锁定的平铺关系列表中收集原父节点祖先、目标父节点祖先和子节点集合。
     *
     * @param lockedRelations 已锁定的平铺关系列表
     * @param sourceParentId  原父节点 ID
     * @param targetParentId  目标父节点 ID
     * @param sourceSuperiors 输出参数：原父节点祖先集合
     * @param targetSuperiors 输出参数：目标父节点祖先集合
     * @param subordinates    输出参数：子节点集合
     */
    private void collectSuperiorsAndSubordinates(List<OrganClosurePo> lockedRelations,
                                                 Long sourceParentId,
                                                 Long targetParentId,
                                                 Set<Long> sourceSuperiors,
                                                 Set<Long> targetSuperiors,
                                                 Set<OrganHierarchyNode> subordinates) {
        for (OrganClosurePo o : lockedRelations) {
            if (o.getDescendantId().equals(sourceParentId)) {
                sourceSuperiors.add(o.getAncestorId());
            } else if (o.getDescendantId().equals(targetParentId)) {
                targetSuperiors.add(o.getAncestorId());
            } else {
                subordinates.add(new OrganHierarchyNode(o.getDescendantId(), o.getDescendantType()));
            }
        }
    }

    /**
     * 处理原父节点的平铺关系，区分 path_count > 1 和 path_count = 1 的情况。
     * <p>
     * 将结果分别放入 toDecrement 和 toDelete 列表，并同时收集目标父节点已存在关系。
     *
     * @param existRelations   查询到的现有平铺关系列表
     * @param sourceSuperiors  原父节点祖先集合
     * @param toDecrement      输出参数：path_count > 1 的ID集合，待递减
     * @param toDelete         输出参数：path_count = 1 的ID集合，待逻辑删除
     * @param targetExistedMap 输出参数：目标父节点已存在的关系Map，key格式为 parentId-childId, value 为平铺关系ID
     */
    private void processSourceRelations(List<OrganClosurePo> existRelations, Set<Long> sourceSuperiors,
                                        List<Long> toDecrement, List<Long> toDelete,
                                        Map<String, Long> targetExistedMap) {
        for (OrganClosurePo po : existRelations) {
            if (!sourceSuperiors.contains(po.getDescendantId())) {
                targetExistedMap.put(po.getAncestorId() + "-" + po.getDescendantId(), po.getId());
                continue;
            }

            if (po.getPathCount() > 1) {
                toDecrement.add(po.getId());
            } else {
                toDelete.add(po.getId());
            }
        }
    }

    /**
     * 确保子节点存在自关联记录。
     *
     * <p>方法整体逻辑：
     * <ol>
     *   <li>查询 childId 是否已经存在自身的平铺关系（自关联），避免重复插入。</li>
     *   <li>如果不存在自关联：
     *       <ul>
     *           <li>生成新的唯一 ID（使用 {@link IdGenerator}）。</li>
     *           <li>设置创建时间和更新时间为当前时间。</li>
     *           <li>parentOrganId 和 childOrganId 都为 childId，标记该节点与自身关联。</li>
     *           <li>childOrganType 设置为传入节点类型。</li>
     *           <li>organRelation 设置为 {@link OrganRelation#SELF_ASSOCIATION}。</li>
     *           <li>PathCount 初始为 1，表示被引用次数至少为自身。</li>
     *           <li>调用 DAO 插入记录。</li>
     *       </ul>
     *   </li>
     *   <li>保证即使节点没有父节点，也可以通过自关联查询到自身，实现闭包策略。</li>
     * </ol>
     *
     * <p>技术要点：
     * <ul>
     *   <li>自反闭包策略：通过在表中显式存储节点自身与自身的关系，实现层级闭包。</li>
     *   <li>确保挂载、迁移等操作前，子节点至少存在一条自关联记录。</li>
     *   <li>避免重复插入自关联，提高性能和数据安全性。</li>
     * </ul>
     *
     * @param childId   子节点 ID
     * @param childType 子节点类型
     */
    private void ensureSelfRelation(Long childId, OrganType childType) {
        // 查询自身关联的记录，避免重复插入
        List<OrganClosurePo> hasSelfRelation = organClosureDao.hasFlatRelation(childId, childId);

        // 已存在自关联，直接返回
        if (!hasSelfRelation.isEmpty()) {
            return;
        }

        // 当前时间
        LocalDateTime now = Moments.now();
        // 创建自关联记录
        OrganClosurePo po = new OrganClosurePo();
        // 生成唯一 ID
        po.setId(idGenerator.generateId());
        po.setCreateTime(now);
        po.setUpdateTime(now);
        // 自关联父节点
        po.setAncestorId(childId);
        // 自关联子节点
        po.setDescendantId(childId);
        // 设置节点类型
        po.setDescendantType(childType.name());
        // 标记自关联关系
        po.setRelationType(OrganRelation.SELF_ASSOCIATION.name());
        // 初始引用计数为1
        po.setPathCount(1);
        // 插入自关联记录
        organClosureDao.insert(po);
    }

    /**
     * 构建一个新的平铺关系对象（OrganFlatRelationPo）。
     *
     * <p>该方法用于 mount 和 migrate 时创建新的子节点挂载记录。
     *
     * @param parentId  父节点 ID
     * @param childNode 子节点信息对象
     * @param isDirect  是否为直属关系（直接子节点）
     * @return 构建好的 OrganFlatRelationPo 对象，包含 ID、创建/更新时间、父子关系类型及 PathCount
     */
    private OrganClosurePo buildFlatRelation(Long parentId, OrganHierarchyNode childNode, boolean isDirect) {
        LocalDateTime now = Moments.now();
        OrganClosurePo po = new OrganClosurePo();
        po.setId(idGenerator.generateId());
        po.setCreateTime(now);
        po.setUpdateTime(now);
        po.setAncestorId(parentId);
        po.setDescendantId(childNode.getDescendantId());
        po.setDescendantType(childNode.getDescendantType());
        po.setRelationType(isDirect ? OrganRelation.DIRECT_SUBORDINATE.name() : OrganRelation.INDIRECT_SUBORDINATE.name());
        po.setPathCount(1);
        return po;
    }

    /**
     * 构建目标父节点与其下级节点之间的扁平关系集合。
     * <p>
     * 遍历所有目标父节点与下级节点的组合，如果该父子关系在
     * {@code targetExistedMap} 中不存在，则创建新的扁平关系记录。
     * 当父节点等于 {@code targetParentId} 且子节点等于 {@code childId} 时，
     * 标记为直系关系。
     *
     * @param targetSuperiors  目标父节点 ID 集合
     * @param subordinateNodes 下级节点集合
     * @param targetExistedMap 已存在的父子关系映射，key 形式为 "parentId-childId"
     * @param targetParentId   当前目标父节点ID，用于判断直系关系
     * @param childId          当前子节点ID，用于判断直系关系
     * @return 新构建的扁平关系列表
     */
    private List<OrganClosurePo> buildTargetRelations(Set<Long> targetSuperiors,
                                                      Set<OrganHierarchyNode> subordinateNodes,
                                                      Map<String, Long> targetExistedMap,
                                                      Long targetParentId,
                                                      Long childId) {
        List<OrganClosurePo> result = new ArrayList<>();
        for (Long superior : targetSuperiors) {
            for (OrganHierarchyNode node : subordinateNodes) {
                Long nodeId = node.getDescendantId();
                // 如果该父子关系在目标中不存在，则构建新关系
                if (!targetExistedMap.containsKey(superior + "-" + nodeId)) {
                    // 当 superior 等于目标父节点且 nodeId 等于当前子节点时，标记为直系关系
                    boolean direct = Objects.equals(superior, targetParentId)
                        && Objects.equals(nodeId, childId);
                    result.add(buildFlatRelation(superior, node, direct));
                }
            }
        }
        return result;
    }

    /**
     * 批量处理 path_count 的增减操作。
     *
     * <p>在 mount/migrate/unmount 等方法中用于批量增加或减少已有关系的引用次数。
     * 使用 BATCH_SIZE 分批处理，避免单次操作过大影响数据库性能。
     *
     * @param ids       平铺关系记录的 ID 列表
     * @param increment true 表示批量增加 path_count，false 表示批量减少 path_count
     */
    private void batchProcessPathCount(List<Long> ids, boolean increment) {
        IntStream.range(0, (ids.size() + BATCH_SIZE - 1) / BATCH_SIZE).forEach(i -> {
            List<Long> batch = ids.subList(i * BATCH_SIZE, Math.min((i + 1) * BATCH_SIZE, ids.size()));
            if (increment) {
                organClosureDao.incrementPathCountBatch(batch);
            } else {
                organClosureDao.decrementPathCountBatch(batch);
                organClosureDao.decrementPathCountBatch(batch);
            }
        });
    }

    /**
     * 批量逻辑删除平铺关系记录。
     *
     * <p>适用于卸载子节点、删除节点或迁移原父节点时删除 path_count 为 1 的记录。
     * 使用 BATCH_SIZE 分批处理，提高性能。
     *
     * @param ids 待删除的平铺关系记录 ID 列表
     */
    private void batchDelete(List<Long> ids) {
        IntStream.range(0, (ids.size() + BATCH_SIZE - 1) / BATCH_SIZE).forEach(i -> {
            List<Long> batch = ids.subList(i * BATCH_SIZE, Math.min((i + 1) * BATCH_SIZE, ids.size()));
            organClosureDao.deleteBatch(batch);
        });
    }

    /**
     * 批量插入新的平铺关系记录。
     *
     * <p>适用于 mount 或 migrate 创建新的子节点挂载关系。
     * 使用 BATCH_SIZE 分批插入，保证性能和事务安全。
     *
     * @param result 待插入的平铺关系 PO 列表
     */
    private void batchProcess(List<OrganClosurePo> result) {
        IntStream.range(0, (result.size() + BATCH_SIZE - 1) / BATCH_SIZE).forEach(i -> {
            List<OrganClosurePo> batch = result.subList(
                i * BATCH_SIZE,
                Math.min((i + 1) * BATCH_SIZE, result.size())
            );
            organClosureDao.insertMultiple(batch);
        });
    }

    /**
     * 将机构层级关系列表构建为树形结
     *
     * @param rows 包含机构层级关系的平铺列表，每条记录包含父节点 ID、子节点 ID 等信息
     * @return 机构树的根节点列表
     */
    private List<OrganHierarchicalRelationVo> buildTrees(List<OrganHierarchicalRelationPo> rows) {
        if (com.g2rain.common.utils.Collections.isEmpty(rows)) {
            return List.of();
        }

        // 1. 第一遍：初始化节点池, 将所有子机构存入 Map; Key 是 organ_id, Value 是对应的节点对象
        Map<Long, OrganHierarchicalRelationVo> nodeMap = rows.stream()
            .filter(r -> OrganRelation.selfAssociation(r.getRelationType()))
            .collect(Collectors.toMap(
                OrganHierarchicalRelationPo::getOrganId,
                r -> new OrganHierarchicalRelationVo(r.getOrganId(), r.getOrganName()),
                (e, r) -> e
            ));

        // 2. 第二遍：连线，并记录谁是子节点
        Set<Long> hasParentSet = new HashSet<>();
        rows.stream().filter(r -> OrganRelation.directSubordinate(r.getRelationType())).forEach(r -> {
            Long parentId = r.getParentId();
            Long childId = r.getOrganId();

            OrganHierarchicalRelationVo parent = nodeMap.get(parentId);
            OrganHierarchicalRelationVo child = nodeMap.get(childId);

            // 只有当父子节点都在当前的 nodeMap 中时才连线
            if (parentId.equals(childId) || Objects.isNull(parent) || Objects.isNull(child)) {
                return;
            }

            // 只要它是别人的直接下级，它就不是整棵树的根
            hasParentSet.add(child.getOrganId());

            // 关键点：防止重复添加同一个孩子
            if (parent.getSubOrgans().contains(child)) {
                return;
            }

            parent.getSubOrgans().add(child);
        });

        // 3. 第三遍：找出所有没有爸爸的节点，它们就是“森林”里的所有树根
        return nodeMap.values().stream().filter(node -> !hasParentSet.contains(node.getOrganId())).toList();
    }
}
