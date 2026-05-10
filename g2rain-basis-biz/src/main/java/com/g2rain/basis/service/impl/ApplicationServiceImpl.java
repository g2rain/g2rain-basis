package com.g2rain.basis.service.impl;

import com.g2rain.basis.converter.ApplicationConverter;
import com.g2rain.basis.dao.ApplicationAuthorizationDao;
import com.g2rain.basis.dao.ApplicationDao;
import com.g2rain.basis.dao.ApplicationSuiteDao;
import com.g2rain.basis.dao.ControlDomainDao;
import com.g2rain.basis.dao.po.ApplicationPo;
import com.g2rain.basis.dto.ApplicationAuthorizationSelectDto;
import com.g2rain.basis.dto.ApplicationDto;
import com.g2rain.basis.dto.ApplicationSelectDto;
import com.g2rain.basis.dto.ControlDomainSelectDto;
import com.g2rain.basis.dto.UpdateStatusDto;
import com.g2rain.basis.enums.ApplicationStatus;
import com.g2rain.basis.enums.ApplicationType;
import com.g2rain.basis.enums.AuthorizationStatus;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.enums.BasisSyncerEnum;
import com.g2rain.basis.service.ApplicationService;
import com.g2rain.basis.utils.BasisUtils;
import com.g2rain.basis.utils.Constants;
import com.g2rain.basis.vo.ApplicationIdNameVo;
import com.g2rain.basis.vo.ApplicationScopeVo;
import com.g2rain.basis.vo.ApplicationVo;
import com.g2rain.basis.vo.PublicKeyDescriptorVo;
import com.g2rain.common.exception.BusinessException;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 应用表服务实现类。
 *
 * <p>提供对 `application` 表的操作，包括查询、分页、保存、删除、密钥管理及应用作用域查询。</p>
 *
 * <p>核心功能：</p>
 * <ol>
 *     <li>查询应用列表或分页列表；</li>
 *     <li>保存（新增/更新）应用；</li>
 *     <li>删除应用；</li>
 *     <li>获取/生成应用密钥对；</li>
 *     <li>切换应用状态；</li>
 *     <li>查询机构下的应用作用域；</li>
 *     <li>判断应用是否为默认登录应用。</li>
 * </ol>
 *
 * @author Alpha
 */
@Service(value = "applicationServiceImpl")
public class ApplicationServiceImpl implements ApplicationService {

    @Resource(name = "applicationDao")
    private ApplicationDao applicationDao;

    @Resource(name = "applicationSuiteDao")
    private ApplicationSuiteDao applicationSuiteDao;

    @Resource(name = "applicationAuthorizationDao")
    private ApplicationAuthorizationDao applicationAuthorizationDao;

    @Resource(name = "controlDomainDao")
    private ControlDomainDao controlDomainDao;

    @Resource
    private EventPublisherHub eventPublisherHub;

    private IdGenerator idGenerator;

    @Qualifier("idGenerator")
    @Autowired(required = false)
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    /**
     * 查询应用列表
     *
     * @param selectDto 查询条件 DTO
     * @return 应用 VO 列表
     */
    @Override
    public List<ApplicationVo> selectListIsolation(ApplicationSelectDto selectDto) {
        return applicationDao.selectListIsolation(selectDto)
            .stream()
            .map(ApplicationConverter.INSTANCE::po2vo)
            .toList();
    }

    /**
     * 查询应用列表
     *
     * @param selectDto 查询条件 DTO
     * @return 应用 VO 列表
     */
    @Override
    public List<ApplicationVo> selectList(ApplicationSelectDto selectDto) {
        return applicationDao.selectList(selectDto)
            .stream()
            .map(ApplicationConverter.INSTANCE::po2vo)
            .toList();
    }

    /**
     * 查询应用列表
     *
     * @param selectDto 查询条件 DTO
     * @return 应用 VO 列表
     */
    @Override
    public List<ApplicationIdNameVo> selectApplicationIdNameMap(ApplicationSelectDto selectDto) {
        return applicationDao.selectListIsolation(selectDto).stream().map(o ->
            new ApplicationIdNameVo(o.getId(), o.getApplicationName())
        ).toList();
    }

    /**
     * 分页查询应用
     *
     * @param selectDto 分页查询条件 DTO
     * @return 分页数据封装 PageData
     */
    @Override
    public PageData<ApplicationVo> selectPage(PageSelectListDto<ApplicationSelectDto> selectDto) {
        Page<ApplicationPo> page = PageContext.of(selectDto.getPageNum(), selectDto.getPageSize(), () ->
            applicationDao.selectListIsolation(selectDto.getQuery())
        );

        List<ApplicationVo> result = page.getResult()
            .stream()
            .map(ApplicationConverter.INSTANCE::po2vo)
            .toList();

        return PageData.of(page.getPageNum(), page.getPageSize(), page.getTotal(), result);
    }

    /**
     * 保存应用信息（新增或更新）。
     *
     * <p>规则：</p>
     * <ol>
     *     <li>校验应用类型合法性；</li>
     *     <li>应用编码唯一性校验；</li>
     *     <li>新增应用状态默认为 UNPUBLISHED；</li>
     *     <li>更新应用时检查状态与套件约束。</li>
     * </ol>
     *
     * @param dto 应用 DTO
     * @return 新增或更新应用的主键 ID
     * @throws BusinessException 参数校验失败或状态不允许更新
     */
    @Override
    @SuppressWarnings("null")
    public Long save(ApplicationDto dto) {
        // 校验参数
        ApplicationType type = ApplicationType.fromName(dto.getApplicationType());
        if (ApplicationType.nonMicroApp(type)) {
            Asserts.isTrue(!Boolean.TRUE.equals(dto.getCanIntegrate()),
                SystemErrorCode.PARAM_VAL_INVALID, "canIntegrate"
            );
        }

        // 判断是新增还是更新
        Long id = dto.getId();

        // 校验应用编码唯一性
        Optional.ofNullable(dto.getApplicationCode())
            .filter(Strings::isNotBlank)
            .map(code -> {
                ApplicationSelectDto selectDto = new ApplicationSelectDto();
                selectDto.setApplicationCode(code);
                return applicationDao.selectList(selectDto);
            })
            .filter(Collections::isNotEmpty)
            .ifPresent(apps -> {
                if (apps.stream().anyMatch(o -> !o.getId().equals(id))) {
                    throw new BusinessException(BasisErrorCode.PARAM_ALREADY_EXISTS,
                        "applicationCode", dto.getApplicationCode()
                    );
                }
            });

        // 转换 DTO 为 PO
        ApplicationPo entity = ApplicationConverter.INSTANCE.dto2po(dto);
        LocalDateTime now = Moments.now();
        entity.setUpdateTime(now);
        if (Objects.isNull(id) || id == 0) {
            // 新增：使用IdGenerator生成主键
            entity.setId(idGenerator.generateId());
            entity.setCreateTime(now);
            // 新增的应用数据 状态为 未发布
            entity.setStatus(ApplicationStatus.UNPUBLISHED.name());
            int success = applicationDao.insert(entity);
            Asserts.greaterThan(success, 0, SystemErrorCode.CREATE_DATA_ERROR);

            // 广播新增应用信息
            eventPublisherHub.sendCreate(
                Constants.SYNC_OUTPUT_BINDING,
                BasisSyncerEnum.APP_NAME.name(),
                new ApplicationIdNameVo(
                    entity.getId(),
                    entity.getApplicationName()
                )
            );

            return entity.getId();
        }

        ApplicationPo application = applicationDao.selectById(id);
        Asserts.isTrue(Objects.nonNull(application), SystemErrorCode.PARAM_VAL_INVALID, id);
        Asserts.isTrue(ApplicationStatus.UNPUBLISHED.name().equals(application.getStatus()),
            BasisErrorCode.PUBLISHED_UPDATE_ILLEGAL
        );

        ApplicationPo app = applicationDao.selectById(id);
        Asserts.isTrue(Objects.nonNull(app), SystemErrorCode.PARAM_VAL_INVALID, id);
        if (!dto.getCanIntegrate().equals(app.getCanIntegrate())) {
            long total = applicationSuiteDao.checkApplicationSuiteExists(app.getId());
            Asserts.lessThanOrEqual(total, 0, BasisErrorCode.INTEGRATED_APP_CLASSIFY_ILLEGAL);
        }

        // 更新：直接更新
        int success = applicationDao.update(entity);
        Asserts.greaterThan(success, 0, SystemErrorCode.UPDATE_DATA_ERROR, id);

        // 应用名称修改需要推送消息同步广播
        if (!Strings.equals(app.getApplicationName(), dto.getApplicationName())) {
            // 广播修改应用信息
            eventPublisherHub.sendUpdate(
                Constants.SYNC_OUTPUT_BINDING,
                BasisSyncerEnum.APP_NAME.name(),
                new ApplicationIdNameVo(
                    entity.getId(),
                    entity.getApplicationName()
                )
            );
        }

        return entity.getId();
    }

    /**
     * 删除应用信息。
     *
     * <p>删除前进行校验：</p>
     * <ol>
     *     <li>应用是否存在；</li>
     *     <li>是否为默认回调应用；</li>
     *     <li>是否已发布。</li>
     * </ol>
     *
     * @param id 应用标识
     * @return 删除数量
     * @throws BusinessException 校验不通过时抛出异常
     */
    @Override
    @Transactional
    @SuppressWarnings("null")
    public int delete(Long id) {
        ApplicationPo application = applicationDao.selectById(id);
        // `应用` 不存在
        Asserts.isTrue(Objects.nonNull(application), SystemErrorCode.PARAM_VAL_INVALID, id);
        // 如果`应用` 是默认回调应用, 不允许删除
        Asserts.isTrue(!Boolean.TRUE.equals(application.getLanding()),
            BasisErrorCode.DEL_LANDING_CALLBACK_APPLICATION_ILLEGAL
        );
        // 如果`应用` 是已发布状态, 不允许删除
        Asserts.isTrue(ApplicationStatus.UNPUBLISHED.name().equals(application.getStatus()),
            BasisErrorCode.PUBLISHED_UPDATE_ILLEGAL
        );

        // 存在控制域, 不允许删除
        ControlDomainSelectDto selectDomainDto = new ControlDomainSelectDto();
        selectDomainDto.setApplicationId(id);
        long total = controlDomainDao.checkControlDomainExists(selectDomainDto);
        Asserts.lessThanOrEqual(total, 0, BasisErrorCode.DEL_APP_EXIST_DOMAIN_UNDELETABLE);

        // 存在有效的授权记录, 不允许删除
        ApplicationAuthorizationSelectDto selectAuthDto = new ApplicationAuthorizationSelectDto();
        selectAuthDto.setApplicationId(id);
        selectAuthDto.setStatus(AuthorizationStatus.ACTIVATED.name());
        total = applicationAuthorizationDao.checkApplicationAuthorizationExists(selectAuthDto);
        Asserts.lessThanOrEqual(total, 0, BasisErrorCode.DEL_APP_EXIST_AUTH_UNDELETABLE);

        // 删除应用归类数据
        applicationSuiteDao.deleteByApplicationId(id);

        // 删除应用记录
        int result = applicationDao.delete(id);

        // 广播删除应用信息
        eventPublisherHub.sendDelete(
            Constants.SYNC_OUTPUT_BINDING,
            BasisSyncerEnum.APP_NAME.name(),
            new ApplicationIdNameVo(
                application.getId(),
                application.getApplicationName()
            )
        );

        return result;
    }

    /**
     * 获取指定应用的公钥信息
     *
     * @param id 应用标识
     * @return 应用 DER 密钥对 VO
     */
    @Override
    @SuppressWarnings("null")
    public PublicKeyDescriptorVo getPublicKeyDescriptor(Long id) {
        ApplicationPo application = applicationDao.selectById(id);
        Asserts.isTrue(Objects.nonNull(application), SystemErrorCode.PARAM_VAL_INVALID, id);
        return ApplicationConverter.INSTANCE.po2PublicKey(application);
    }

    public boolean hasPublicKey(Long id) {
        ApplicationPo application = applicationDao.selectById(id);
        if (Objects.isNull(application)) {
            return false;
        }

        return Strings.isNotBlank(application.getPublicKey());
    }

    /**
     * 上传 PEM/DER 格式公钥
     *
     * @param id                 应用标识
     * @param publicKeyAlgorithm 公钥算法
     * @param keyBytes           公钥内容
     * @return 保存数据成功的记录数量
     */
    @Override
    public Integer upsertPublicKey(Long id, String publicKeyAlgorithm, byte[] keyBytes) {
        // 校验公钥并返回公钥格式
        PublicKeyDescriptorVo descriptor = BasisUtils.validateAndNormalizePublicKey(
            publicKeyAlgorithm, keyBytes
        );

        // 保存公钥
        ApplicationPo entity = new ApplicationPo();
        entity.setId(id);
        entity.setUpdateTime(Moments.now());
        entity.setPublicKeyAlgorithm(descriptor.getPublicKeyAlgorithm());
        entity.setPublicKeyFormat(descriptor.getPublicKeyFormat());
        entity.setPublicKey(descriptor.getPublicKey());
        int success = applicationDao.updateSelective(entity);
        Asserts.greaterThan(success, 0, SystemErrorCode.UPDATE_DATA_ERROR, id);
        return success;
    }

    /**
     * 修改应用状态
     *
     * @param id 应用标识
     * @return 数据库中受影响的行数（通常为 1 表示成功，0 表示没有变化）
     * @throws BusinessException 校验失败时抛出异常
     */
    @Override
    @SuppressWarnings("null")
    public int updateStatus(Long id, UpdateStatusDto dto) {
        // 校验状态参数
        ApplicationStatus.validate(dto.getStatus());
        ApplicationPo entity = new ApplicationPo();
        entity.setId(id);
        entity.setUpdateTime(Moments.now());
        entity.setStatus(dto.getStatus());
        return applicationDao.updateSelective(entity);
    }

    /**
     * 查询机构下的应用作用域（ApplicationScopeVo 列表）。
     *
     * <p>核心逻辑：</p>
     * <ol>
     *     <li>如果 organId 为 null，返回空列表；</li>
     *     <li>如果 applicationId 为 null，则返回默认登录应用（landing=true）；</li>
     *     <li>否则根据机构 ID 和入口应用 ID 查询可见应用集合，并转换为 ApplicationScopeVo。</li>
     * </ol>
     *
     * @param userId        用户标识
     * @param applicationId 入口应用标识
     * @return 可见的应用作用域列表
     */
    @Override
    public List<ApplicationScopeVo> selectApplicationScope(Long userId, Long applicationId) {
        // 如果机构 ID 为空，无法查询任何应用，直接返回空列表
        if (Objects.isNull(userId)) {
            return List.of();
        }

        // 如果入口应用 ID 为空，返回默认主应用（landing=true）
        if (Objects.isNull(applicationId)) {
            ApplicationSelectDto selectDto = new ApplicationSelectDto();
            selectDto.setLanding(true);
            selectDto.setCanIntegrate(true);
            List<ApplicationPo> applications = applicationDao.selectList(selectDto);
            // 没有默认登录应用，返回空列表
            if (Collections.isEmpty(applications)) {
                return List.of();
            }

            // 将查询到的默认应用 PO 转换为 ScopeVo 并返回单条记录
            return List.of(ApplicationConverter.INSTANCE.po2scopeVo(
                applications.getFirst()
            ));
        }

        // 正常情况：根据机构 ID 和入口应用 ID 查询应用集合，并转换为 ScopeVo 列表
        return applicationDao.selectApplicationScope(userId, applicationId).stream().map(
            ApplicationConverter.INSTANCE::po2scopeVo
        ).toList();
    }

    /**
     * 判断指定应用是否为默认登录应用（landing=true）。
     *
     * <p>逻辑说明：</p>
     * <ol>
     *     <li>如果应用不存在，返回 false；</li>
     *     <li>如果应用存在但 landing 字段不为 true，返回 false；</li>
     *     <li>如果应用存在且 landing 字段为 true，返回 true。</li>
     * </ol>
     *
     * @param id 应用标识
     * @return 如果应用存在且为默认登录应用返回 true，否则返回 false
     */
    public boolean hasLandingApplication(Long id) {
        if (Objects.isNull(id)) {
            return false;
        }

        ApplicationPo app = applicationDao.selectById(id);
        if (Objects.isNull(app)) {
            return false;
        }

        Boolean canIntegrate = app.getCanIntegrate();
        Boolean landing = app.getLanding();
        return Boolean.TRUE.equals(landing) && Boolean.TRUE.equals(canIntegrate);
    }

    /**
     * 查询单个应用
     *
     * @param id 应用标识
     * @return 应用 VO
     */
    public ApplicationVo selectOne(Long id) {
        return ApplicationConverter.INSTANCE.po2vo(applicationDao.selectById(id));
    }
}
