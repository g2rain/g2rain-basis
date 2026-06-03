package com.g2rain.basis.service.impl;

import com.g2rain.basis.converter.LoginTokenConverter;
import com.g2rain.basis.dao.ApplicationDao;
import com.g2rain.basis.dao.ApplicationIdpProvisionDao;
import com.g2rain.basis.dao.LoginTokenDao;
import com.g2rain.basis.dao.OrganDao;
import com.g2rain.basis.dao.PassportIdpBindingDao;
import com.g2rain.basis.dao.RoleControlUnitRelationDao;
import com.g2rain.basis.dao.UserDao;
import com.g2rain.basis.dao.po.ApplicationPo;
import com.g2rain.basis.dao.po.CountRoleControlUnitPo;
import com.g2rain.basis.dao.po.LoginTokenPo;
import com.g2rain.basis.dao.po.OrganPo;
import com.g2rain.basis.dao.po.UserPo;
import com.g2rain.basis.dto.ApplicationSelectDto;
import com.g2rain.basis.dto.LoginTokenDto;
import com.g2rain.basis.dto.LoginTokenSelectDto;
import com.g2rain.basis.dto.OrganSelectDto;
import com.g2rain.basis.dto.UserSelectDto;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.enums.OrganStatus;
import com.g2rain.basis.enums.StaticTokenStatus;
import com.g2rain.basis.service.ApplicationService;
import com.g2rain.basis.service.LoginTokenService;
import com.g2rain.basis.service.OrganService;
import com.g2rain.basis.service.PersonalStaticAccessTokenService;
import com.g2rain.basis.service.UserService;
import com.g2rain.basis.vo.ApplicationScopeVo;
import com.g2rain.basis.vo.ApplicationVo;
import com.g2rain.basis.vo.LoginTokenVo;
import com.g2rain.basis.vo.OrganVo;
import com.g2rain.basis.vo.PersonalStaticAccessTokenVo;
import com.g2rain.basis.vo.StaticAccessTokenContextVo;
import com.g2rain.basis.vo.StaticAccessTokenResolveVo;
import com.g2rain.basis.vo.UserVo;
import com.g2rain.common.enums.OrganType;
import com.g2rain.common.enums.SessionType;
import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.id.IdGenerator;
import com.g2rain.common.json.JsonCodec;
import com.g2rain.common.json.JsonCodecFactory;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.utils.Asserts;
import com.g2rain.common.utils.Collections;
import com.g2rain.common.utils.Moments;
import com.g2rain.common.utils.Strings;
import com.g2rain.common.web.ApplicationScope;
import com.g2rain.common.web.PrincipalEnricher;
import com.g2rain.common.web.TokenJWTPayload;
import com.g2rain.mybatis.pagination.PageContext;
import com.g2rain.mybatis.pagination.model.Page;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 登录信息服务实现类
 * <p>
 * 核心功能：
 * <ul>
 *     <li>记录和管理当前登录状态的相关信息</li>
 *     <li>提供登录信息的增删改查操作</li>
 * </ul>
 * <p>
 * 对应数据库表: {@code login_token}
 *
 * @author Alpha
 * @since 2026/1/19
 */
@Slf4j
@Service(value = "loginTokenServiceImpl")
public class LoginTokenServiceImpl implements LoginTokenService {

    @Resource(name = "loginTokenDao")
    private LoginTokenDao loginTokenDao;

    @Resource
    private ApplicationService applicationService;

    @Resource(name = "roleControlUnitRelationDao")
    private RoleControlUnitRelationDao roleControlUnitRelationDao;

    @Resource(name = "applicationDao")
    private ApplicationDao applicationDao;

    @Resource(name = "organDao")
    private OrganDao organDao;

    @Resource(name = "userDao")
    private UserDao userDao;

    @Resource
    private UserService userService;

    @Resource
    private OrganService organService;

    @Resource(name = "personalStaticAccessTokenServiceImpl")
    private PersonalStaticAccessTokenService personalStaticAccessTokenService;

    private List<PrincipalEnricher> principalEnrichers = List.of();

    @Resource
    private ApplicationIdpProvisionDao applicationIdpProvisionDao;

    @Resource
    private PassportIdpBindingDao passportIdpBindingDao;

    private IdGenerator idGenerator;

    @Qualifier("idGenerator")
    @Autowired(required = false)
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Autowired(required = false)
    public void setPrincipalEnrichers(List<PrincipalEnricher> principalEnrichers) {
        this.principalEnrichers = Objects.requireNonNullElse(principalEnrichers, List.of());
    }

    /**
     * 查询登录信息列表
     *
     * @param selectDto 查询条件 DTO
     * @return 登录信息 VO 列表
     */
    @Override
    public List<LoginTokenVo> selectList(LoginTokenSelectDto selectDto) {
        return loginTokenDao.selectList(selectDto)
            .stream()
            .map(LoginTokenConverter.INSTANCE::po2vo)
            .toList();
    }

    /**
     * 分页查询登录信息
     *
     * @param selectDto 分页查询条件 DTO
     * @return 登录信息分页数据
     */
    @Override
    public PageData<LoginTokenVo> selectPage(PageSelectListDto<LoginTokenSelectDto> selectDto) {
        Page<LoginTokenPo> page = PageContext.of(selectDto.getPageNum(), selectDto.getPageSize(), () ->
            loginTokenDao.selectList(selectDto.getQuery())
        );

        List<LoginTokenVo> result = page.getResult()
            .stream()
            .map(LoginTokenConverter.INSTANCE::po2vo)
            .toList();
        return PageData.of(page.getPageNum(), page.getPageSize(), page.getTotal(), result);
    }

    /**
     * 保存或更新登录信息
     *
     * <p>逻辑说明：
     * <ol>
     *     <li>将 DTO 转换为 PO</li>
     *     <li>如果主键为空或为 0，则执行新增，使用 IdGenerator 生成主键</li>
     *     <li>否则执行更新，更新修改时间</li>
     * </ol>
     *
     * @param applicationCode 应用编码
     * @param dto             登录信息 DTO
     * @return 保存或更新后的登录信息 ID
     * @throws BusinessException 新增或更新失败时抛出
     */
    @Override
    public Long save(String applicationCode, LoginTokenDto dto) {
        ApplicationSelectDto selectDto = new ApplicationSelectDto();
        selectDto.setApplicationCode(applicationCode);
        List<ApplicationVo> applications = applicationService.selectList(selectDto);
        if (Collections.isEmpty(applications)) {
            return 0L;
        }

        ApplicationVo application = applications.getFirst();
        LoginTokenPo entity = LoginTokenConverter.INSTANCE.dto2po(dto);
        entity.setId(idGenerator.generateId());
        entity.setApplicationId(application.getId());
        entity.setApplicationOrganId(application.getOrganId());
        LocalDateTime now = Moments.now();
        entity.setUpdateTime(now);
        entity.setCreateTime(now);
        loginTokenDao.insert(entity);
        return entity.getId();
    }

    @Override
    public int delete(Long id) {
        return loginTokenDao.delete(id);
    }

    /**
     * 构建 Token JWT 的载荷信息。
     *
     * <p>根据用户 ID 和应用编码一次性查询用户信息、机构信息、应用信息及应用作用域，
     * 并封装到 {@link TokenJWTPayload} 中，用于生成登录后的 Token。</p>
     *
     * <p>逻辑说明：</p>
     * <ol>
     *     <li>如果 userId 为 null，则返回默认 Passport 类型的 Token 载荷。</li>
     *     <li>查询用户信息，并设置用户相关字段（ID、姓名、管理员标识）。</li>
     *     <li>查询机构信息，并校验机构状态是否激活，同时设置机构相关字段（ID、名称、类型、是否管理员公司）。</li>
     *     <li>查询应用信息及应用作用域，并封装到 {@link ApplicationScope} 列表中。</li>
     * </ol>
     *
     * @param passportId         发码侧通行证 ID；三方换票且 {@code userId} 非空时必传，且须与用户的 {@code passport_id} 一致
     * @param userId             用户 ID，可为 null（表示 Passport 会话）
     * @param applicationCode    应用编码
     * @param thirdPartyIdpLogin 是否外部身份源授权链路发码；为 {@link Boolean#TRUE} 且 {@code userId} 非空时校验
     *                           {@code application_idp_provision} 与 {@code passport_idp_binding}
     * @param idpType            身份源类型
     * @param idpSubject         IdP 稳定主体
     * @param idpApplicationCode 三方应用在 IdP 侧的标识
     * @return 构建完成的 {@link TokenJWTPayload}，包含用户、机构、应用及应用作用域信息
     * @throws BusinessException 当用户、机构或应用不存在，或机构不可用时抛出
     */
    public TokenJWTPayload fetchTokenContext(Long passportId, Long userId, String applicationCode,
                                             Boolean thirdPartyIdpLogin, String idpType, String idpSubject,
                                             String idpApplicationCode) {
        ApplicationSelectDto appSelect = new ApplicationSelectDto();
        appSelect.setApplicationCode(applicationCode);
        List<ApplicationVo> applications = applicationService.selectList(appSelect);
        Asserts.isTrue(Collections.isNotEmpty(applications),
            SystemErrorCode.UNAUTHORIZED, applicationCode
        );
        ApplicationVo application = applications.getFirst();
        Boolean canIntegrate = application.getCanIntegrate();
        Boolean landing = application.getLanding();
        boolean isDefaultMain = Boolean.TRUE.equals(landing) && Boolean.FALSE.equals(canIntegrate);

        TokenJWTPayload payload = new TokenJWTPayload();
        if (Objects.isNull(userId)) {
            // 设置账号类型登陆
            payload.setSessionType(SessionType.PASSPORT);

            // 如果入口应用不是 `默认应用`, 不允许登陆
            Asserts.isTrue(isDefaultMain, SystemErrorCode.UNAUTHORIZED, applicationCode);

            Instant issuedAt = Instant.now();
            payload.setIssuedAt(issuedAt.getEpochSecond());

            payload.setExpireAt(issuedAt.plus(Duration.ofSeconds(
                application.getAccessTokenExpiresIn()
            )).getEpochSecond());

            payload.setRefreshExpireAt(issuedAt.plus(Duration.ofSeconds(
                application.getRefreshTokenExpiresIn()
            )).getEpochSecond());

            payload.setApplicationScopes(List.of(new ApplicationScope(
                application.getId(), applicationCode, application.getOrganId()
            )));

            return payload;
        }

        // 1. 查询用户信息
        UserSelectDto userSelect = new UserSelectDto();
        userSelect.setId(userId);
        List<UserVo> users = userService.selectList(userSelect);
        Asserts.isTrue(Collections.isNotEmpty(users),
            SystemErrorCode.PARAM_VAL_INVALID, userId
        );

        UserVo user = users.getFirst();

        if (Boolean.TRUE.equals(thirdPartyIdpLogin)) {
            Asserts.isTrue(Strings.isNotBlank(idpType) && Strings.isNotBlank(idpSubject)
                    && Strings.isNotBlank(idpApplicationCode),
                SystemErrorCode.PARAM_VAL_INVALID, "idpType,idpSubject,idpApplicationCode");
            Asserts.isTrue(passportId != null && passportId > 0L,
                SystemErrorCode.PARAM_VAL_INVALID, "passportId");
            Asserts.isTrue(Objects.equals(passportId, user.getPassportId()),
                SystemErrorCode.PARAM_VAL_INVALID, userId);
            String idpTypeTrim = idpType.trim();
            String idpSubjectTrim = idpSubject.trim();
            String idpAppTrim = idpApplicationCode.trim();
            int provisionCount = applicationIdpProvisionDao.countByApplicationIdAndIdp(
                application.getId(), idpTypeTrim, idpAppTrim);
            Asserts.isTrue(provisionCount > 0,
                BasisErrorCode.APPLICATION_IDP_PROVISION_MISSING, applicationCode);
            int bindingCount = passportIdpBindingDao.countByPassportIdAndIdpKeys(
                passportId, idpTypeTrim, idpSubjectTrim, idpAppTrim);
            Asserts.isTrue(bindingCount > 0,
                BasisErrorCode.PASSPORT_IDP_BINDING_MISMATCH, applicationCode);
        }

        payload.setSessionType(SessionType.USER);
        payload.setUserId(user.getId());
        payload.setName(user.getRealName());
        payload.setAdminUser(Boolean.TRUE.equals(user.getAdmin()));

        // 2. 查询机构信息
        OrganSelectDto organSelect = new OrganSelectDto();
        organSelect.setId(user.getOrganId());
        List<OrganVo> organs = organService.selectList(organSelect);
        Asserts.isTrue(Collections.isNotEmpty(organs),
            SystemErrorCode.PARAM_VAL_INVALID, userId
        );

        OrganVo organ = organs.getFirst();
        Asserts.isTrue(OrganStatus.ACTIVE.name().equals(organ.getStatus()),
            BasisErrorCode.ORGAN_UNAVAILABLE
        );
        payload.setOrganType(OrganType.fromName(organ.getOrganType()));
        payload.setOrganId(organ.getId());
        payload.setOrganName(organ.getOrganName());
        payload.setAdminCompany(Boolean.TRUE.equals(organ.getAdmin()));

        // 需要外部的Starter 提供能力, 没有实现也没关系
        principalEnrichers.forEach(enricher -> {
            enricher.enrich(payload);
            log.info("enrich payload:{}", JsonCodecFactory.instance().obj2str(payload));
        });

        // 如果入口应用不是 `默认应用`, 需要校验应用是否做过授权
        if (!isDefaultMain) {
            CountRoleControlUnitPo countRoleControlUnit = roleControlUnitRelationDao
                .countRoleControlUnits(user.getId());

            // 如果没有授权, 权限不足
            Asserts.greaterThan(Objects.isNull(countRoleControlUnit.getTotalControlUnitCount()) ? -1 :
                    countRoleControlUnit.getTotalControlUnitCount(), 0,
                SystemErrorCode.UNAUTHORIZED, organ.getOrganName()
            );

            // 如果没有有效的权限, 请续费
            Asserts.greaterThan(Objects.isNull(countRoleControlUnit.getActiveControlUnitCount()) ? -1 :
                    countRoleControlUnit.getActiveControlUnitCount(), 0,
                BasisErrorCode.BUSINESS_CAPABILITY_DISABLED, organ.getOrganName()
            );
        }

        Instant issuedAt = Instant.now();
        payload.setIssuedAt(issuedAt.getEpochSecond());

        payload.setExpireAt(issuedAt.plus(Duration.ofSeconds(
            application.getAccessTokenExpiresIn()
        )).getEpochSecond());

        payload.setRefreshExpireAt(issuedAt.plus(Duration.ofSeconds(
            application.getRefreshTokenExpiresIn()
        )).getEpochSecond());

        // 4. 查询应用作用域
        List<ApplicationScopeVo> applicationScopes = applicationService.selectApplicationScope(
            user.getId(), application.getId()
        );

        List<ApplicationScope> scopes = new ArrayList<>(applicationScopes.size() + 1);
        scopes.add(new ApplicationScope(application.getId(), applicationCode,
            application.getOrganId())
        );
        applicationScopes.forEach(obj -> scopes.add(new ApplicationScope(
            obj.getId(), obj.getApplicationCode(), obj.getOrganId()
        )));
        payload.setApplicationScopes(scopes);
        return payload;
    }

    /**
     * 解析静态 API Key：查库 → 组装 status；激活态再查用户/机构/应用拼 {@link StaticAccessTokenContextVo}。
     */
    @Override
    public StaticAccessTokenResolveVo fetchStaticTokenContext(String apiKey) {
        PersonalStaticAccessTokenVo sat = personalStaticAccessTokenService.selectByApiKey(apiKey);
        if (Objects.isNull(sat)) {
            return null;
        }

        StaticAccessTokenResolveVo resolve = new StaticAccessTokenResolveVo();
        resolve.setStatus(StaticTokenStatus.fromName(sat.getStatus()));
        if (StaticTokenStatus.REVOKED.equals(resolve.getStatus())) {
            return resolve;
        }

        resolve.setContext(buildStaticAccessTokenContext(sat));
        return resolve;
    }

    /**
     * 由令牌记录关联的用户、机构、应用 PO 构建网关可用的会话 VO；任一主数据缺失则返回 null。
     */
    private StaticAccessTokenContextVo buildStaticAccessTokenContext(PersonalStaticAccessTokenVo sat) {
        UserPo user = userDao.selectByIdWithoutIsolation(sat.getUserId());
        if (Objects.isNull(user)) {
            return null;
        }

        OrganPo organ = organDao.selectById(sat.getOrganId());
        if (Objects.isNull(organ)) {
            return null;
        }

        ApplicationPo application = applicationDao.selectById(sat.getApplicationId());
        if (Objects.isNull(application)) {
            return null;
        }

        StaticAccessTokenContextVo context = new StaticAccessTokenContextVo();
        context.setSessionType(SessionType.USER);
        context.setPassportId(user.getPassportId());
        context.setUserId(user.getId());
        context.setName(user.getRealName());
        context.setAdminUser(Boolean.TRUE.equals(user.getAdmin()));
        context.setOrganId(organ.getId());
        context.setOrganName(organ.getOrganName());
        context.setOrganType(OrganType.fromName(organ.getOrganType()));
        context.setAdminCompany(Boolean.TRUE.equals(organ.getAdmin()));
        context.setApplicationId(application.getId());
        context.setApplicationCode(application.getApplicationCode());
        context.setApplicationOrganId(application.getOrganId());
        return context;
    }
}
