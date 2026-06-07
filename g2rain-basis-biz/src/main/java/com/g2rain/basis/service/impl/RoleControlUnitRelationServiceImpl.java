package com.g2rain.basis.service.impl;

import com.g2rain.basis.converter.RoleControlUnitRelationConverter;
import com.g2rain.basis.dao.ControlUnitDao;
import com.g2rain.basis.dao.RoleControlUnitRelationDao;
import com.g2rain.basis.dao.RoleDao;
import com.g2rain.basis.dao.po.ControlUnitPo;
import com.g2rain.basis.dao.po.ControlUnitStatPo;
import com.g2rain.basis.dao.po.RoleControlUnitRelationPo;
import com.g2rain.basis.dao.po.RolePo;
import com.g2rain.basis.dto.ControlUnitSelectDto;
import com.g2rain.basis.dto.RoleControlUnitRelationDto;
import com.g2rain.basis.dto.RoleControlUnitRelationSelectDto;
import com.g2rain.basis.dto.RoleSelectDto;
import com.g2rain.basis.enums.AuthorizationStatus;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.enums.BasisSyncerEnum;
import com.g2rain.basis.enums.RoleType;
import com.g2rain.basis.model.RoleControlUnitRelation;
import com.g2rain.basis.service.RoleControlUnitRelationService;
import com.g2rain.basis.utils.Constants;
import com.g2rain.basis.vo.RoleControlUnitRelationVo;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.id.IdGenerator;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.syncer.EventPublisherHub;
import com.g2rain.common.utils.Asserts;
import com.g2rain.common.utils.Collections;
import com.g2rain.common.utils.Moments;
import com.g2rain.common.utils.Strings;
import com.g2rain.mybatis.pagination.PageContext;
import com.g2rain.mybatis.pagination.model.Page;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色控制单元关联表服务实现类
 * 表名: role_control_unit_relation
 *
 * @author Alpha
 */
@Service(value = "roleControlUnitRelationServiceImpl")
public class RoleControlUnitRelationServiceImpl implements RoleControlUnitRelationService {

    @Resource(name = "roleControlUnitRelationDao")
    private RoleControlUnitRelationDao roleControlUnitRelationDao;

    @Resource(name = "roleDao")
    private RoleDao roleDao;

    @Resource(name = "controlUnitDao")
    private ControlUnitDao controlUnitDao;

    private IdGenerator idGenerator;

    @Resource
    private EventPublisherHub eventPublisherHub;

    @Qualifier("idGenerator")
    @Autowired(required = false)
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return VO 对象列表
     */
    @Override
    public List<RoleControlUnitRelationVo> selectList(RoleControlUnitRelationSelectDto selectDto) {
        return setControlUnitName(roleControlUnitRelationDao.selectList(selectDto)
            .stream()
            .map(RoleControlUnitRelationConverter.INSTANCE::po2vo)
            .toList());
    }

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页 VO 数据
     */
    @Override
    public PageData<RoleControlUnitRelationVo> selectPage(PageSelectListDto<RoleControlUnitRelationSelectDto> selectDto) {
        Page<RoleControlUnitRelationPo> page = PageContext.of(selectDto.getPageNum(), selectDto.getPageSize(), () ->
            roleControlUnitRelationDao.selectList(selectDto.getQuery())
        );

        List<RoleControlUnitRelationVo> result = page.getResult()
            .stream()
            .map(RoleControlUnitRelationConverter.INSTANCE::po2vo)
            .toList();
        return PageData.of(page.getPageNum(), page.getPageSize(), page.getTotal(), setControlUnitName(result));
    }

    /**
     * 获取到角色控制单元列表, 根据角色信息找到归属的机构, 通过机构查询超管角色的所有控制单元集合
     *
     * @param roleId 角色 ID
     * @return 控制单元集合
     */
    @Override
    @SuppressWarnings("null")
    public List<RoleControlUnitRelationVo> selectByRole(Long roleId) {
        RolePo role = roleDao.selectById(roleId);
        Asserts.isTrue(Objects.nonNull(role), SystemErrorCode.PARAM_VAL_INVALID, roleId);
        RoleSelectDto selectDto = new RoleSelectDto();
        selectDto.setOrganId(role.getOrganId());
        selectDto.setRoleType(RoleType.ADMIN.name());
        List<RolePo> roles = roleDao.selectList(selectDto);
        if (Collections.isEmpty(roles)) {
            return List.of();
        }

        RoleControlUnitRelationSelectDto rs = new RoleControlUnitRelationSelectDto();
        rs.setRoleId(roles.getFirst().getId());
        return selectList(rs);
    }

    /**
     * 管理员为用户角色分配控制单元的接口方法。
     *
     * <p>设计理念：</p>
     * <ul>
     *     <li>不做复杂的增删改比对，避免代码逻辑臃肿。</li>
     *     <li>直接清空角色历史控制单元关联，再新增传入的控制单元集合。</li>
     *     <li>仅针对用户角色生效，默认角色不允许通过此接口修改控制单元。</li>
     * </ul>
     *
     * <p>方法流程：</p>
     * <ol>
     *     <li>校验传入角色存在且是用户角色（非 ADMIN 角色）</li>
     *     <li>查找机构的 ADMIN 超管角色，验证其控制单元状态是否与传入控制单元集合匹配，确保管理员不能越权分配</li>
     *     <li>删除目标角色现有的所有控制单元关联</li>
     *     <li>调用 {@link #internalSave(RoleControlUnitRelation)} 批量新增新的控制单元关联记录</li>
     * </ol>
     *
     * <p>注意事项：</p>
     * <ul>
     *     <li>方法使用事务保证原子性</li>
     *     <li>抛出异常的情况包括角色不存在、默认角色不可修改、以及控制单元集合与超管角色不匹配</li>
     *     <li>实际插入的记录数量由 {@link #internalSave(RoleControlUnitRelation)} 返回</li>
     * </ul>
     *
     * @param dto 包含角色 ID 和控制单元集合
     * @return 新增的控制单元记录数量
     */
    @Override
    @Transactional
    @SuppressWarnings("null")
    public Integer save(RoleControlUnitRelationDto dto) {
        // 校验参数合法性
        RolePo role = roleDao.selectById(dto.getRoleId());
        Asserts.isTrue(Objects.nonNull(role), SystemErrorCode.PARAM_VAL_INVALID,
            "roleId"
        );
        Asserts.isTrue(!RoleType.ADMIN.name().equals(role.getRoleType()),
            BasisErrorCode.ADD_ADMIN_ROLE_CONTROL_UNIT_ILLEGAL
        );

        // 校验添加的控制单元必须在超管角色的控制单元范围内
        if (Collections.isNotEmpty(dto.getControlUnitIds())) {
            // 查找 机构超管角色
            RoleSelectDto roleSelect = new RoleSelectDto();
            roleSelect.setOrganId(role.getOrganId());
            roleSelect.setRoleType(RoleType.ADMIN.name());
            List<RolePo> roles = roleDao.selectList(roleSelect);
            Asserts.isTrue(Collections.isNotEmpty(roles),
                BasisErrorCode.ADMIN_ROLE_NOT_EXISTS_ILLEGAL
            );

            // 查询机构超管角色控制单元集合, 如果集合数量比对比匹配说明控制单元缺少, 抛异常
            RoleControlUnitRelationSelectDto selectDto = new RoleControlUnitRelationSelectDto();
            selectDto.setRoleId(roles.getFirst().getId());
            selectDto.setControlUnitIds(dto.getControlUnitIds());
            Set<Long> controlUnitIds = roleControlUnitRelationDao.selectList(selectDto).stream()
                .map(RoleControlUnitRelationPo::getControlUnitId)
                .collect(Collectors.toSet());
            Asserts.isTrue(controlUnitIds.size() == dto.getControlUnitIds().size(),
                BasisErrorCode.CONTROL_UNIT_INVALID_FOR_ROLE
            );
        }

        return doSave(dto);
    }

    /**
     * 执行角色控制单元的实际保存操作（落地数据库）。
     *
     * <p>方法职责：</p>
     * <ul>
     *     <li>删除指定角色已有的控制单元关联</li>
     *     <li>新增传入的控制单元集合记录</li>
     * </ul>
     *
     * <p>说明：</p>
     * <ul>
     *     <li>该方法仅负责执行数据库操作，不做权限或合法性校验</li>
     *     <li>调用方需在调用前确保 DTO 合法性</li>
     *     <li>返回值为实际新增的控制单元记录数量</li>
     * </ul>
     *
     * @param dto 包含角色 ID 和控制单元集合
     * @return 新增的控制单元记录数量
     */
    private Integer doSave(RoleControlUnitRelationDto dto) {
        int result = 0;

        // 添加新的关联信息
        if (Collections.isNotEmpty(dto.getControlUnitIds())) {
            result += internalSave(new RoleControlUnitRelation(
                dto.getRoleId(), dto.getControlUnitIds()
            ));
        }

        // 删除所有的关联信息
        if (Collections.isNotEmpty(dto.getDeleteControlUnitIds())) {
            result += roleControlUnitRelationDao.deleteByControlUnitIds(
                dto.getRoleId(), dto.getDeleteControlUnitIds()
            );
        }

        return result;
    }

    /**
     * 为指定角色批量添加控制单元关联记录。
     *
     * <p>此方法兼顾两种典型场景：</p>
     * <ol>
     *     <li>
     *         <strong>应用授权初始化：</strong>
     *         <ul>
     *             <li>当新的应用授权记录生成时，会自动为该授权对应的 ADMIN 角色创建控制单元关联。</li>
     *             <li>控制单元集合来源于授权记录，关联记录状态默认为 ACTIVATED，并绑定应用授权 ID。</li>
     *         </ul>
     *     </li>
     *     <li>
     *         <strong>管理员角色分配：</strong>
     *         <ul>
     *             <li>管理员为普通用户的角色分配控制单元时，传入的控制单元集合即为选择列表。</li>
     *             <li>此时不绑定应用授权 ID，方法会去重已有的控制单元，避免重复插入。</li>
     *         </ul>
     *     </li>
     * </ol>
     *
     * <p>处理流程：</p>
     * <ul>
     *     <li>若 DTO 的 controlUnitIds 为空，直接返回 0。</li>
     *     <li>查询角色已存在的控制单元，剔除重复项，仅对新增控制单元生成关联记录。</li>
     *     <li>每条关联记录设置：</li>
     *     <ul>
     *         <li>唯一主键 ID（使用 IdGenerator 生成）</li>
     *         <li>创建和更新时间</li>
     *         <li>角色 ID 与控制单元 ID</li>
     *         <li>状态为 ACTIVATED</li>
     *         <li>仅在授权初始化场景设置 applicationAuthorizationId</li>
     *     </ul>
     *     <li>批量插入并返回实际新增的记录数量。</li>
     * </ul>
     *
     * @param dto 包含角色 ID、控制单元集合，以及可选应用授权 ID
     * @return 实际新增的角色控制单元记录条数，如果无新增则返回 0
     */
    @Override
    public Integer internalSave(RoleControlUnitRelation dto) {
        if (Collections.isEmpty(dto.getControlUnitIds())) {
            // 推送消息, 可能存在的场景, 控制域删除控制单元, 防止遗漏, 需要推送
            sendPermissionChange(dto.getRoleId());
            return 0;
        }

        // 获取授权记录 ID
        Long authorizationId = dto.getApplicationAuthorizationId();

        // 用户角色是先删除在添加没有问题, 但是默认角色可能存在补充控制单元, 所以需要去重
        RoleControlUnitRelationSelectDto selectDto = new RoleControlUnitRelationSelectDto();
        selectDto.setRoleId(dto.getRoleId());
        selectDto.setControlUnitIds(dto.getControlUnitIds());
        // 如果没有设置授权记录 ID, 查询得时候需要通过 is null 进行过滤
        if (Objects.isNull(authorizationId)) {
            selectDto.setApplicationAuthorizationIdIsNull(true);
        }
        List<RoleControlUnitRelationPo> relations = roleControlUnitRelationDao.selectList(selectDto);
        Set<Long> existingControlUnitIds = relations.stream()
            .map(RoleControlUnitRelationPo::getControlUnitId)
            .collect(Collectors.toSet());

        // 过滤掉已经存在的 controlUnitId，避免重复插入
        List<Long> newControlUnitIds = dto.getControlUnitIds().stream()
            .filter(id -> !existingControlUnitIds.contains(id))
            .toList();

        // 如果没有新控制单元需要插入，返回 0
        if (Collections.isEmpty(newControlUnitIds)) {
            // 推送消息, 可能存在的场景, 控制单元调整接口资源, 防止遗漏, 需要推送
            sendPermissionChange(dto.getRoleId());
            return 0;
        }

        // 增量为角色添加控制单元
        List<RoleControlUnitRelationPo> records = newControlUnitIds.stream().map(o -> {
            RoleControlUnitRelationPo entity = new RoleControlUnitRelationPo();
            LocalDateTime now = Moments.now();
            // 新增：使用IdGenerator生成主键
            entity.setId(idGenerator.generateId());
            entity.setCreateTime(now);
            entity.setUpdateTime(now);
            entity.setControlUnitId(o);
            entity.setRoleId(dto.getRoleId());
            entity.setStatus(AuthorizationStatus.ACTIVATED.name());
            entity.setApplicationAuthorizationId(authorizationId);
            return entity;
        }).toList();

        // 批量保存数据
        int result = roleControlUnitRelationDao.insertMultiple(records);

        // 推送消息
        sendPermissionChange(dto.getRoleId());

        return result;
    }

    /**
     * 推送当前机构的权限失效
     *
     * @param roleId 角色标识
     */
    private void sendPermissionChange(Long roleId) {
        // 找到对应的机构, 对当前的机构的接口权限置为失效
        RoleSelectDto roleSelectDto = new RoleSelectDto();
        roleSelectDto.setId(roleId);
        List<RolePo> roles = roleDao.selectListWithoutIsolation(roleSelectDto);
        if (Collections.isEmpty(roles)) {
            return;
        }

        // 广播删除`接口权限`信息
        eventPublisherHub.sendDelete(
            Constants.SYNC_OUTPUT_BINDING,
            BasisSyncerEnum.USER_PERM.name(),
            roles.getFirst().getOrganId()
        );
    }


    /**
     * 根据机构 ID 和应用授权 ID，批量修改该机构控制单元的状态（激活或关停）。
     *
     * <p>处理规则：</p>
     * <ul>
     *     <li>激活场景（status = ACTIVATED）：
     *         <ul>
     *             <li>查询 ADMIN 角色下所有被关停的非永久控制单元</li>
     *             <li>批量将这些控制单元在该机构下所有角色中设置为激活</li>
     *         </ul>
     *     </li>
     *     <li>关停场景（status = DEACTIVATED）：
     *         <ul>
     *             <li>统计 ADMIN 角色下各控制单元的激活数量</li>
     *             <li>cnt > 1：仅关停 ADMIN 角色对应的控制单元</li>
     *             <li>cnt <= 1：关停该机构下所有角色对应的该控制单元</li>
     *         </ul>
     *     </li>
     * </ul>
     *
     * <p>注意：</p>
     * <ul>
     *     <li>只处理 delete_flag = 0 的记录</li>
     *     <li>PERPETUAL 类型的控制单元不会被关停</li>
     *     <li>方法在事务内执行，确保多条 SQL 原子性</li>
     * </ul>
     *
     * @param organId            机构 ID
     * @param appAuthorizationId 应用授权 ID
     * @param status             目标状态，ACTIVATED 或 DEACTIVATED
     * @return 受影响的控制单元记录数量
     */
    @Override
    @Transactional
    public int changeStatus(Long organId, Long appAuthorizationId, String status) {
        if (Objects.isNull(organId) || Objects.isNull(appAuthorizationId) || Strings.isBlank(status)) {
            return 0;
        }

        // 查找 机构超管角色
        RoleSelectDto roleSelect = new RoleSelectDto();
        roleSelect.setOrganId(organId);
        roleSelect.setRoleType(RoleType.ADMIN.name());
        List<RolePo> roles = roleDao.selectListWithoutIsolation(roleSelect);
        if (Collections.isEmpty(roles)) {
            return 0;
        }

        // 默认取第一个 ADMIN 角色
        Long adminRoleId = roles.getFirst().getId();

        // 处理激活的场景
        if (AuthorizationStatus.ACTIVATED.name().equals(status)) {
            List<Long> controlUnitIds = roleControlUnitRelationDao.findUnitsToActivate(
                adminRoleId, appAuthorizationId
            );

            if (Collections.isEmpty(controlUnitIds)) {
                return 0;
            }

            return roleControlUnitRelationDao.updateOrganUnitsStatus(
                organId, appAuthorizationId, controlUnitIds, status
            );
        }

        // 处理关停的场景 -- 统计 ADMIN 角色下各 control_unit 激活数量
        List<ControlUnitStatPo> stats = roleControlUnitRelationDao.countActivatedUnits(
            adminRoleId, appAuthorizationId
        );

        if (Collections.isEmpty(stats)) {
            return 0;
        }

        // 分离 cnt>1 与 cnt<=1 的 control_unit_id
        List<Long> cntGt1Ids = new ArrayList<>();
        List<Long> cntLe1Ids = new ArrayList<>();
        for (ControlUnitStatPo stat : stats) {
            if (stat.getActivatedCount() > 1) {
                cntGt1Ids.add(stat.getControlUnitId());
            } else {
                cntLe1Ids.add(stat.getControlUnitId());
            }
        }

        int affected = 0;

        // cnt > 1 → 只关停 ADMIN 角色控制单元
        if (!cntGt1Ids.isEmpty()) {
            affected += roleControlUnitRelationDao.deactivateAdminUnits(
                adminRoleId, appAuthorizationId, cntGt1Ids
            );
        }

        // cnt <= 1 → 关停/激活机构下所有角色
        if (!cntLe1Ids.isEmpty()) {
            affected += roleControlUnitRelationDao.updateOrganUnitsStatus(
                organId, appAuthorizationId, cntLe1Ids, status
            );
        }

        // 广播删除`接口权限`信息
        eventPublisherHub.sendDelete(
            Constants.SYNC_OUTPUT_BINDING,
            BasisSyncerEnum.USER_PERM.name(),
            organId
        );

        return affected;
    }

    /**
     * 设置控制单元名称到 VO 列表
     *
     * @param relations 角色控制单元关联 VO 列表
     */
    private List<RoleControlUnitRelationVo> setControlUnitName(List<RoleControlUnitRelationVo> relations) {
        if (Collections.isEmpty(relations)) {
            return relations;
        }

        Set<Long> controlUnitIds = relations.stream()
            .map(RoleControlUnitRelationVo::getControlUnitId)
            .collect(Collectors.toSet());

        ControlUnitSelectDto selectDto = new ControlUnitSelectDto();
        selectDto.setIds(controlUnitIds);
        Map<Long, String> id2name = controlUnitDao.selectList(selectDto).stream()
            .collect(Collectors.toMap(
                ControlUnitPo::getId,
                ControlUnitPo::getControlUnitName,
                (existing, r) -> existing
            ));

        for (RoleControlUnitRelationVo relation : relations) {
            relation.setControlUnitName(id2name.get(relation.getControlUnitId()));
        }

        return relations;
    }
}
