package com.g2rain.basis.service.impl;

import com.g2rain.basis.converter.OrganConverter;
import com.g2rain.basis.dao.OrganDao;
import com.g2rain.basis.dao.po.OrganPo;
import com.g2rain.basis.dto.OrganClosureDto;
import com.g2rain.basis.dto.OrganDto;
import com.g2rain.basis.dto.OrganSelectDto;
import com.g2rain.basis.dto.UpdateStatusDto;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.enums.BasisSyncerEnum;
import com.g2rain.basis.enums.OrganStatus;
import com.g2rain.basis.service.OrganClosureService;
import com.g2rain.basis.service.OrganService;
import com.g2rain.basis.utils.Constants;
import com.g2rain.basis.vo.OrganHierarchicalRelationVo;
import com.g2rain.basis.vo.OrganIdNameVo;
import com.g2rain.basis.vo.OrganVo;
import com.g2rain.common.enums.OrganType;
import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.id.IdGenerator;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.syncer.EventPublisherHub;
import com.g2rain.common.utils.Asserts;
import com.g2rain.common.utils.Moments;
import com.g2rain.common.web.PrincipalContextHolder;
import com.g2rain.mybatis.pagination.PageContext;
import com.g2rain.mybatis.pagination.model.Page;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 机构服务实现类
 * <p>
 * 核心功能：
 * <ul>
 *     <li>提供机构的增删改查操作</li>
 *     <li>处理机构层级挂载关系</li>
 *     <li>管理机构默认角色</li>
 * </ul>
 * <p>
 * 对应数据库表: {@code organ}
 *
 * @author Alpha
 * @since 2026/1/19
 */
@Service(value = "organServiceImpl")
public class OrganServiceImpl implements OrganService {

    @Resource(name = "organDao")
    private OrganDao organDao;

    @Resource(name = "organClosureServiceImpl")
    private OrganClosureService organClosureService;

    @Resource
    private EventPublisherHub eventPublisherHub;

    private IdGenerator idGenerator;

    @Qualifier("idGenerator")
    @Autowired(required = false)
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    /**
     * 查询机构列表
     *
     * @param selectDto 查询条件 DTO
     * @return 机构 VO 列表
     */
    @Override
    public List<OrganVo> selectList(OrganSelectDto selectDto) {
        // 如果非运营公司需要设置数据隔离标识
        if (!PrincipalContextHolder.isAdminCompany() && !PrincipalContextHolder.isBackEnd()) {
            Long organId = PrincipalContextHolder.getOrganId();
            if (Objects.isNull(organId)) {
                return List.of();
            }

            selectDto.setOrganId(organId);
        }

        return organDao.selectListIsolation(selectDto)
            .stream()
            .map(OrganConverter.INSTANCE::po2vo)
            .toList();
    }

    /**
     * 分页查询机构列表
     *
     * @param selectDto 分页查询条件 DTO
     * @return 机构分页数据
     */
    @Override
    public PageData<OrganVo> selectPage(PageSelectListDto<OrganSelectDto> selectDto) {
        // 如果非运营公司需要设置数据隔离标识
        if (!PrincipalContextHolder.isAdminCompany() && !PrincipalContextHolder.isBackEnd()) {
            Long organId = PrincipalContextHolder.getOrganId();
            if (Objects.isNull(organId)) {
                return PageData.of(selectDto.getPageNum(), selectDto.getPageSize(), 0, List.of());
            }

            selectDto.getQuery().setOrganId(organId);
        }

        Page<OrganPo> page = PageContext.of(selectDto.getPageNum(), selectDto.getPageSize(), () ->
            organDao.selectListIsolation(selectDto.getQuery())
        );

        List<OrganVo> result = page.getResult()
            .stream()
            .map(OrganConverter.INSTANCE::po2vo)
            .toList();
        return PageData.of(page.getPageNum(), page.getPageSize(), page.getTotal(), result);
    }

    /**
     * 查询指定机构的层级关系
     *
     * @return 包含指定机构层级关系的列表
     */
    @Override
    public List<OrganHierarchicalRelationVo> getHierarchicalRelations() {
        if (PrincipalContextHolder.isAdminCompany()) {
            return organClosureService.fetchAllHierarchicalRelations();
        }

        Long organId = PrincipalContextHolder.getOrganId();
        if (Objects.isNull(organId)) {
            return List.of();
        }

        return organClosureService.fetchHierarchicalRelations(organId);
    }

    /**
     * 根据机构名称模糊匹配机构
     *
     * @param organName 机构名称
     * @return 实体对象列表
     */
    @Override
    public List<OrganIdNameVo> searchOrgans(String organName) {
        Long organId = null;
        // 如果非运营公司, 非用户登陆, 禁止查看
        if (!PrincipalContextHolder.isAdminCompany() && !PrincipalContextHolder.isBackEnd()) {
            organId = PrincipalContextHolder.getOrganId();
            if (Objects.isNull(organId)) {
                return List.of();
            }
        }

        return organDao.searchOrgans(organId, organName).stream().map(o ->
            new OrganIdNameVo(o.getId(), o.getOrganName())
        ).toList();
    }

    /**
     * 根据机构 ID 集合获取对应的 ID–名称映射
     *
     * @param ids 机构 ID 集合
     * @return 包含机构 ID 和名称的列表
     */
    @Override
    public List<OrganIdNameVo> selectOrganIdNameMap(Set<Long> ids) {
        OrganSelectDto select = new OrganSelectDto();
        select.setIds(ids);

        // 如果非运营公司需要设置数据隔离标识
        if (!PrincipalContextHolder.isAdminCompany() && !PrincipalContextHolder.isBackEnd()) {
            Long organId = PrincipalContextHolder.getOrganId();
            if (Objects.isNull(organId)) {
                return List.of();
            }

            select.setOrganId(organId);
        }

        return organDao.selectListIsolation(select).stream().map(o ->
            new OrganIdNameVo(o.getId(), o.getOrganName())
        ).toList();
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
        return organClosureService.checkHierarchyRelation(childId, parentId);
    }

    /**
     * 保存或更新机构信息
     *
     * <p>逻辑说明：
     * <ol>
     *     <li>校验参数及机构类型</li>
     *     <li>判断是新增还是更新</li>
     *     <li>新增时挂载机构层级关系并生成默认角色</li>
     *     <li>更新时直接修改更新时间</li>
     * </ol>
     *
     * @param dto 机构 DTO
     * @return 保存或更新后的机构 ID
     * @throws BusinessException 新增或更新失败时抛出
     */
    @Override
    @SuppressWarnings("null")
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Long save(OrganDto dto) {
        return saveWithIsolation(dto);
    }

    @Override
    @SuppressWarnings("null")
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Long saveWithIsolation(OrganDto dto) {
        return doSave(dto);
    }

    @Override
    @SuppressWarnings("null")
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Long saveWithoutIsolation(OrganDto dto) {
        return doSave(dto);
    }

    private Long doSave(OrganDto dto) {
        // 参数校验
        OrganType childOrganType = OrganType.fromName(dto.getOrganType());

        // 判断是新增还是更新
        Long id = dto.getId();

        // 校验 同一机构类型 机构名 是否重复
        OrganSelectDto selectDto = new OrganSelectDto();
        selectDto.setOrganName(dto.getOrganName());
        selectDto.setOrganType(dto.getOrganType());
        List<OrganPo> organs = organDao.selectList(selectDto);
        if (organs.stream().anyMatch(o -> !o.getId().equals(id))) {
            throw new BusinessException(BasisErrorCode.PARAM_ALREADY_EXISTS,
                "organName", dto.getOrganName()
            );
        }

        // 转换 DTO 为 PO
        OrganPo entity = OrganConverter.INSTANCE.dto2po(dto);

        // 更新：直接更新
        if (Objects.nonNull(id) && id != 0) {
            entity.setUpdateTime(Moments.now());
            int success = organDao.update(entity);
            Asserts.greaterThan(success, 0, SystemErrorCode.UPDATE_DATA_ERROR, id);

            // 广播修改机构
            eventPublisherHub.sendUpdate(
                Constants.SYNC_OUTPUT_BINDING,
                BasisSyncerEnum.ORGAN_NAME.name(),
                new OrganIdNameVo(
                    entity.getId(),
                    entity.getOrganName()
                )
            );

            return entity.getId();
        }

        // 运营公司不允许创建机构
        Asserts.isTrue(!PrincipalContextHolder.isAdminCompany(),
            BasisErrorCode.OPERATION_ORG_CREATE_DENIED
        );

        Long parentId = null;
        OrganType parentOrganType = null;
        // 如果存在上级机构, 需要进行校验并进行挂载
        Long parentOrganId = PrincipalContextHolder.getOrganId();
        if (Objects.nonNull(parentOrganId)) {
            OrganPo organ = organDao.selectById(parentOrganId);
            Asserts.isTrue(Objects.nonNull(organ), SystemErrorCode.PARAM_VAL_INVALID,
                parentOrganId
            );
            parentId = organ.getId();
            parentOrganType = OrganType.fromName(organ.getOrganType());
            // 提前重复断言子机构类型可以挂载到父机构类型, 防止浪费机构序列, 因为是分布式发号, 发出后不用就算浪费
            organClosureService.assertAssociate(childOrganType, parentOrganType);
        }

        // 新增：使用IdGenerator生成主键
        entity.setId(idGenerator.generateId());
        LocalDateTime now = Moments.now();
        entity.setUpdateTime(now);
        entity.setCreateTime(now);
        entity.setStatus(OrganStatus.ACTIVE.name());
        int success = organDao.insert(entity);
        Asserts.greaterThan(success, 0, SystemErrorCode.CREATE_DATA_ERROR);

        // 关联机构层级关系
        organClosureService.mount(entity.getId(), childOrganType,
            parentId, parentOrganType
        );

        // 广播添加机构
        eventPublisherHub.sendCreate(
            Constants.SYNC_OUTPUT_BINDING,
            BasisSyncerEnum.ORGAN_NAME.name(),
            new OrganIdNameVo(
                entity.getId(),
                entity.getOrganName()
            )
        );

        return entity.getId();
    }

    /**
     * 删除机构信息
     *
     * <p>逻辑说明：
     * <ol>
     *     <li>删除机构的层级关系</li>
     *     <li>删除机构记录</li>
     * </ol>
     *
     * @param id 机构 ID
     * @return 删除记录数
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public int delete(Long id) {
        throw new BusinessException(BasisErrorCode.DEL_ORGAN_UNDELETABLE);
    }

    /**
     * 更新机构状态
     *
     * @param id  机构 ID
     * @param dto 包含新的状态的 DTO
     * @return 更新记录数
     * @throws BusinessException 当状态不合法时抛出
     */
    @Override
    public int updateStatus(Long id, UpdateStatusDto dto) {
        // 校验状态参数
        OrganStatus.validate(dto.getStatus());
        OrganPo organ = new OrganPo();
        organ.setId(id);
        organ.setUpdateTime(Moments.now());
        organ.setStatus(dto.getStatus());
        return organDao.update(organ);
    }

    /**
     * 调整机构层级关系（支持挂载、迁移、卸载）
     *
     * @param descendantId 子节点标识
     * @param dto          层级调整参数，包含操作类型与目标父机构信息
     */
    @Override
    @SuppressWarnings("null")
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void adjustHierarchy(Long descendantId, OrganClosureDto dto) {
        Long sourceAncestorId = dto.getSourceAncestorId();
        Long targetAncestorId = dto.getTargetAncestorId();

        // 如果两个都为空, 那么他是要做什么呢？目前好像没意识到能做什么!!!
        if (Objects.isNull(targetAncestorId) && Objects.isNull(sourceAncestorId)) {
            return;
        }

        // 如果非运营公司, 验证原始祖节点和目标祖节点是否与当前的登录的机构存在归属关系
        if (!PrincipalContextHolder.isAdminCompany()) {
            Long organId = PrincipalContextHolder.getOrganId();
            if (Objects.isNull(organId)) {
                return;
            }

            if (Objects.nonNull(sourceAncestorId)) {
                boolean bool = organClosureService.checkHierarchyRelation(sourceAncestorId, organId);
                Asserts.isTrue(bool, SystemErrorCode.PARAM_VAL_INVALID,
                    sourceAncestorId
                );
            }

            if (Objects.nonNull(targetAncestorId)) {
                boolean bool = organClosureService.checkHierarchyRelation(targetAncestorId, organId);
                Asserts.isTrue(bool, SystemErrorCode.PARAM_VAL_INVALID,
                    targetAncestorId
                );
            }
        }

        OrganPo organ = organDao.selectById(descendantId);
        Asserts.isTrue(Objects.nonNull(organ), SystemErrorCode.PARAM_VAL_INVALID,
            descendantId
        );
        OrganType descendantType = OrganType.fromName(organ.getOrganType());

        // 如果原始父节点是空, 说明需要挂载
        if (Objects.isNull(sourceAncestorId)) {
            organ = organDao.selectById(targetAncestorId);
            Asserts.isTrue(Objects.nonNull(organ), SystemErrorCode.PARAM_VAL_INVALID,
                targetAncestorId
            );
            OrganType targetAncestorType = OrganType.fromName(organ.getOrganType());
            organClosureService.mount(descendantId, descendantType, targetAncestorId,
                targetAncestorType
            );
            return;
        }

        // 如果目标父节点是空, 说明是卸载
        if (Objects.isNull(targetAncestorId)) {
            organClosureService.unmount(descendantId, sourceAncestorId);
            return;
        }

        // 执行到这里说明是迁移操作
        organ = organDao.selectById(targetAncestorId);
        Asserts.isTrue(Objects.nonNull(organ), SystemErrorCode.PARAM_VAL_INVALID,
            targetAncestorId
        );
        OrganType targetAncestorType = OrganType.fromName(organ.getOrganType());
        organClosureService.migrate(descendantId, descendantType, targetAncestorId,
            targetAncestorType, sourceAncestorId
        );
    }
}
