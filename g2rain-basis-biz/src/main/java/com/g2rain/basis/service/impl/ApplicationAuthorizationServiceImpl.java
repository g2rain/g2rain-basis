package com.g2rain.basis.service.impl;

import com.g2rain.basis.converter.ApplicationAuthorizationConverter;
import com.g2rain.basis.dao.ApplicationAuthorizationDao;
import com.g2rain.basis.dao.ApplicationDao;
import com.g2rain.basis.dao.ControlDomainControlUnitRelationDao;
import com.g2rain.basis.dao.ControlDomainDao;
import com.g2rain.basis.dao.OrganDao;
import com.g2rain.basis.dao.RoleDao;
import com.g2rain.basis.dao.po.ApplicationAuthorizationPo;
import com.g2rain.basis.dao.po.ApplicationPo;
import com.g2rain.basis.dao.po.ControlDomainControlUnitRelationPo;
import com.g2rain.basis.dao.po.ControlDomainPo;
import com.g2rain.basis.dao.po.OrganPo;
import com.g2rain.basis.dao.po.RolePo;
import com.g2rain.basis.dto.ApplicationAuthorizationDto;
import com.g2rain.basis.dto.ApplicationAuthorizationSelectDto;
import com.g2rain.basis.dto.ApplicationSelectDto;
import com.g2rain.basis.dto.ControlDomainControlUnitRelationSelectDto;
import com.g2rain.basis.dto.ControlDomainSelectDto;
import com.g2rain.basis.dto.RoleSelectDto;
import com.g2rain.basis.dto.UpdateStatusDto;
import com.g2rain.basis.enums.AuthorizationStatus;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.enums.ControlDomainType;
import com.g2rain.basis.enums.RoleType;
import com.g2rain.basis.model.RoleControlUnitRelation;
import com.g2rain.basis.service.ApplicationAuthorizationService;
import com.g2rain.basis.service.RoleControlUnitRelationService;
import com.g2rain.basis.vo.ApplicationAuthorizationVo;
import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.id.IdGenerator;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.utils.Asserts;
import com.g2rain.common.utils.Collections;
import com.g2rain.common.utils.Moments;
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
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 应用授权记录表服务实现类
 * <p>
 * 提供对 `application_authorization` 表的操作，包括查询、分页、保存和状态变更。
 * 注意：应用授权记录不允许修改已有记录或删除。
 * </p>
 *
 * <p>主要功能：</p>
 * <ol>
 *     <li>查询应用授权记录列表或分页列表</li>
 *     <li>保存（新增）应用授权记录</li>
 *     <li>更改授权状态</li>
 *     <li>删除操作不可用（直接抛出异常）</li>
 * </ol>
 *
 * @author Alpha
 */
@Service(value = "applicationAuthorizationServiceImpl")
public class ApplicationAuthorizationServiceImpl implements ApplicationAuthorizationService {

    @Resource(name = "applicationAuthorizationDao")
    private ApplicationAuthorizationDao applicationAuthorizationDao;

    @Resource(name = "organDao")
    private OrganDao organDao;

    @Resource(name = "roleDao")
    private RoleDao roleDao;

    @Resource(name = "applicationDao")
    private ApplicationDao applicationDao;

    @Resource(name = "controlDomainDao")
    private ControlDomainDao controlDomainDao;

    @Resource(name = "controlDomainControlUnitRelationDao")
    private ControlDomainControlUnitRelationDao controlDomainControlUnitRelationDao;

    @Resource(name = "roleControlUnitRelationServiceImpl")
    private RoleControlUnitRelationService roleControlUnitRelationService;

    private IdGenerator idGenerator;

    @Qualifier("idGenerator")
    @Autowired(required = false)
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    /**
     * 查询应用授权记录列表
     *
     * @param selectDto 查询条件 DTO
     * @return 应用授权 VO 列表
     */
    @Override
    public List<ApplicationAuthorizationVo> selectList(ApplicationAuthorizationSelectDto selectDto) {
        return mergeControlDomains(applicationAuthorizationDao.selectList(selectDto));
    }

    /**
     * 分页查询应用授权记录
     *
     * @param selectDto 分页查询条件 DTO
     * @return 分页数据封装 PageData
     */
    @Override
    public PageData<ApplicationAuthorizationVo> selectPage(PageSelectListDto<ApplicationAuthorizationSelectDto> selectDto) {
        Page<ApplicationAuthorizationPo> page = PageContext.of(selectDto.getPageNum(), selectDto.getPageSize(), () ->
            applicationAuthorizationDao.selectList(selectDto.getQuery())
        );

        return PageData.of(page.getPageNum(), page.getPageSize(), page.getTotal(), mergeControlDomains(page.getResult()));
    }

    /**
     * 运营保存应用授权记录
     *
     * <p>规则：</p>
     * <ol>
     *     <li>前置校验参数合法性</li>
     *     <li>不存在授权记录, 创建一个授权记录</li>
     *     <li>存在授权记录, 同步控制单元到默认角色</li>
     *     <li>新增记录状态默认为 ACTIVATED</li>
     *     <li>会将控制域的控制单元复制到角色控制单元关系表</li>
     * </ol>
     *
     * @param dto 应用授权 DTO
     * @return 新增记录的主键 ID
     * @throws BusinessException 已存在记录无法修改或参数校验失败
     */
    @Override
    @Transactional
    @SuppressWarnings("null")
    public Long save(ApplicationAuthorizationDto dto) {
        // 校验参数合法性
        Long roleId = validate(dto.getOrganId(), dto.getApplicationId(), dto.getControlDomainId(), dto.getSubscriptionId());

        // 校验是否存在, 存在设置一个主键
        ApplicationAuthorizationSelectDto appAuthSelectDto = new ApplicationAuthorizationSelectDto();
        appAuthSelectDto.setOrganId(dto.getOrganId());
        appAuthSelectDto.setApplicationId(dto.getApplicationId());
        appAuthSelectDto.setControlDomainId(dto.getControlDomainId());
        List<ApplicationAuthorizationPo> appAuthorizations = applicationAuthorizationDao.selectList(appAuthSelectDto);
        if (Collections.isNotEmpty(appAuthorizations)) {
            ApplicationAuthorizationPo appAuthorization = appAuthorizations.getFirst();
            Asserts.isTrue(AuthorizationStatus.ACTIVATED.name().equals(appAuthorization.getStatus()),
                BasisErrorCode.NON_LEAF_DELETE_ILLEGAL
            );
            dto.setId(appAuthorization.getId());
        }

        return activateFeatures(roleId, dto);
    }

    /**
     * 添加应用授权记录前校验参数
     *
     * @param organId         机构
     * @param applicationId   所属应用
     * @param controlDomainId 控制域
     * @param subscriptionId  订阅 ID
     * @return 默认角色 ID
     */
    @SuppressWarnings("null")
    private Long validate(Long organId, Long applicationId, Long controlDomainId, Long subscriptionId) {
        OrganPo organ = organDao.selectById(organId);
        Asserts.isTrue(Objects.nonNull(organ), SystemErrorCode.PARAM_VAL_INVALID,
            organId
        );

        RoleSelectDto selectDto = new RoleSelectDto();
        selectDto.setRoleType(RoleType.ADMIN.name());
        selectDto.setOrganId(organ.getId());
        List<RolePo> roles = roleDao.selectList(selectDto);
        Asserts.isTrue(Collections.isNotEmpty(roles), SystemErrorCode.PARAM_VAL_INVALID,
            organId
        );

        ApplicationPo application = applicationDao.selectById(applicationId);
        Asserts.isTrue(Objects.nonNull(application), SystemErrorCode.PARAM_VAL_INVALID,
            applicationId
        );

        ControlDomainPo controlDomain = controlDomainDao.selectById(controlDomainId);
        Asserts.isTrue(Objects.nonNull(controlDomain), SystemErrorCode.PARAM_VAL_INVALID,
            controlDomainId
        );

        // 交易类型的控制域, 需要校验订阅标识必填
        if (ControlDomainType.TRADE.name().equals(controlDomain.getControlDomainType())) {
            Asserts.isTrue(Objects.nonNull(subscriptionId), SystemErrorCode.PARAM_VAL_INVALID,
                subscriptionId
            );
        }

        Asserts.isTrue(application.getId().equals(controlDomain.getApplicationId()),
            SystemErrorCode.PARAM_VAL_INVALID, controlDomainId
        );

        return roles.getFirst().getId();
    }

    /**
     * 为角色开通控制单元
     *
     * @param roleId 默认角色 ID
     * @param dto    开通的参数
     * @return 授权记录 ID
     */
    private Long activateFeatures(Long roleId, ApplicationAuthorizationDto dto) {
        // 判断是新增还是更新; 如果存在, 等同于同步控制域的控制单元(控制域可能新增控制单元)
        Long id = dto.getId();
        if (Objects.isNull(id)) {
            // 转换 DTO 为 PO
            ApplicationAuthorizationPo entity = ApplicationAuthorizationConverter.INSTANCE.dto2po(dto);
            // 新增：使用IdGenerator生成主键
            entity.setId(idGenerator.generateId());
            LocalDateTime now = Moments.now();
            entity.setUpdateTime(now);
            entity.setCreateTime(now);
            // 新增的记录都是 `激活` 状态
            entity.setStatus(AuthorizationStatus.ACTIVATED.name());
            int success = applicationAuthorizationDao.insert(entity);
            Asserts.greaterThan(success, 0, SystemErrorCode.CREATE_DATA_ERROR);
            id = entity.getId();
        }

        // 将控制域的控制单元复制到角色控制单元关系表
        ControlDomainControlUnitRelationSelectDto select = new ControlDomainControlUnitRelationSelectDto();
        select.setControlDomainId(dto.getControlDomainId());
        Set<Long> controlUnitIds = controlDomainControlUnitRelationDao.selectList(select)
            .stream().map(ControlDomainControlUnitRelationPo::getControlUnitId).collect(Collectors.toSet());
        roleControlUnitRelationService.internalSave(new RoleControlUnitRelation(
            roleId, controlUnitIds, id
        ));

        return id;
    }

    /**
     * 修改应用授权状态
     * <p>
     * 如果状态未变更，则直接返回成功。
     *
     * @param dto 修改状态参数
     * @return 更新记录数
     * @throws BusinessException 参数校验失败
     */
    @Transactional
    @SuppressWarnings("null")
    public int updateStatus(Long id, UpdateStatusDto dto) {
        String status = dto.getStatus();
        ApplicationAuthorizationPo authorization = applicationAuthorizationDao.selectById(id);
        Asserts.isTrue(Objects.nonNull(authorization), SystemErrorCode.PARAM_VAL_INVALID, id);
        // 状态没变更, 不需要修改, 直接提示成功即可
        if (authorization.getStatus().equals(status)) {
            return 1;
        }

        ApplicationAuthorizationPo aa = new ApplicationAuthorizationPo();
        aa.setId(authorization.getId());
        aa.setStatus(status);
        aa.setUpdateTime(Moments.now());
        int total = applicationAuthorizationDao.update(aa);
        total += roleControlUnitRelationService.changeStatus(authorization.getOrganId(),
            authorization.getId(), status
        );
        return total;
    }

    /**
     * 删除操作不可用
     * <p>
     * 应用授权记录不允许删除，调用会抛出异常。
     *
     * @param id 主键 ID
     * @return 永远不会返回
     * @throws BusinessException 总是抛出不可删除异常
     */
    @Override
    public int delete(Long id) {
        throw new BusinessException(BasisErrorCode.APP_AUTHORIZATION_UNDELETABLE);
    }

    /**
     * 设置控制域名称+控制域默认表示
     *
     * @param authorizations 授权记录 PO
     * @return 授权记录 VO
     */
    private List<ApplicationAuthorizationVo> mergeControlDomains(List<ApplicationAuthorizationPo> authorizations) {
        Set<Long> ids = authorizations.stream().map(ApplicationAuthorizationPo::getControlDomainId).collect(Collectors.toSet());
        ControlDomainSelectDto selectDto = new ControlDomainSelectDto();
        selectDto.setIds(ids);
        Map<Long, ControlDomainPo> cdMap = controlDomainDao.selectList(selectDto).stream().collect(
            Collectors.toMap(ControlDomainPo::getId, Function.identity(), (e1, _) -> e1)
        );

        Set<Long> appIds = authorizations.stream().map(ApplicationAuthorizationPo::getApplicationId).collect(Collectors.toSet());
        ApplicationSelectDto  applicationSelectDto = new ApplicationSelectDto();
        applicationSelectDto.setIds(appIds);
        Map<Long, Boolean> apiKeySupportedMap = applicationDao.selectList(applicationSelectDto).stream().collect(
            Collectors.toMap(ApplicationPo::getId, ApplicationPo::getApiKeySupported, (e1, _) -> e1)
        );

        List<ApplicationAuthorizationVo> result = new ArrayList<>(authorizations.size());
        for (ApplicationAuthorizationPo authorization : authorizations) {
            ApplicationAuthorizationVo vo = ApplicationAuthorizationConverter.INSTANCE.po2vo(authorization);
            ControlDomainPo controlDomain = cdMap.get(authorization.getControlDomainId());
            if (Objects.nonNull(controlDomain)) {
                vo.setControlDomainName(controlDomain.getControlDomainName());
                vo.setControlDomainDesc(controlDomain.getDescription());
            }

            Boolean apiKeySupported = apiKeySupportedMap.get(authorization.getApplicationId());
            vo.setApiKeySupported(Boolean.TRUE.equals(apiKeySupported));
            result.add(vo);
        }

        return result;
    }
}
