package com.g2rain.basis.service.impl;

import com.g2rain.basis.converter.LoginTokenConverter;
import com.g2rain.basis.dao.LoginTokenDao;
import com.g2rain.basis.dao.RoleControlUnitRelationDao;
import com.g2rain.basis.dao.po.CountRoleControlUnitPo;
import com.g2rain.basis.dao.po.LoginTokenPo;
import com.g2rain.basis.dto.ApplicationSelectDto;
import com.g2rain.basis.dto.LoginTokenDto;
import com.g2rain.basis.dto.LoginTokenSelectDto;
import com.g2rain.basis.dto.OrganSelectDto;
import com.g2rain.basis.dto.UserSelectDto;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.enums.OrganStatus;
import com.g2rain.basis.service.ApplicationService;
import com.g2rain.basis.service.LoginTokenService;
import com.g2rain.basis.service.OrganService;
import com.g2rain.basis.service.UserService;
import com.g2rain.basis.vo.ApplicationScopeVo;
import com.g2rain.basis.vo.ApplicationVo;
import com.g2rain.basis.vo.LoginTokenVo;
import com.g2rain.basis.vo.OrganVo;
import com.g2rain.basis.vo.UserVo;
import com.g2rain.common.enums.OrganType;
import com.g2rain.common.enums.SessionType;
import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.id.IdGenerator;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.utils.Asserts;
import com.g2rain.common.utils.Collections;
import com.g2rain.common.utils.Moments;
import com.g2rain.common.web.ApplicationScope;
import com.g2rain.common.web.TokenJWTPayload;
import com.g2rain.mybatis.pagination.PageContext;
import com.g2rain.mybatis.pagination.model.Page;
import jakarta.annotation.Resource;
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
@Service(value = "loginTokenServiceImpl")
public class LoginTokenServiceImpl implements LoginTokenService {

    @Resource(name = "loginTokenDao")
    private LoginTokenDao loginTokenDao;

    @Resource
    private ApplicationService applicationService;

    @Resource(name = "roleControlUnitRelationDao")
    private RoleControlUnitRelationDao roleControlUnitRelationDao;

    @Resource
    private UserService userService;

    @Resource
    private OrganService organService;

    private IdGenerator idGenerator;

    @Qualifier("idGenerator")
    @Autowired(required = false)
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
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
     * @param dto 登录信息 DTO
     * @return 保存或更新后的登录信息 ID
     * @throws BusinessException 新增或更新失败时抛出
     */
    @Override
    public Long save(LoginTokenDto dto) {
        // 转换 DTO 为 PO
        LoginTokenPo entity = LoginTokenConverter.INSTANCE.dto2po(dto);

        // 判断是新增还是更新
        Long id = entity.getId();
        if (Objects.isNull(id) || id == 0) {
            // 新增：使用IdGenerator生成主键
            entity.setId(idGenerator.generateId());
            LocalDateTime now = Moments.now();
            entity.setUpdateTime(now);
            entity.setCreateTime(now);
            int success = loginTokenDao.insert(entity);
            Asserts.greaterThan(success, 0, SystemErrorCode.CREATE_DATA_ERROR);
            return entity.getId();
        }

        // 更新：直接更新
        entity.setUpdateTime(Moments.now());
        int success = loginTokenDao.update(entity);
        Asserts.greaterThan(success, 0, SystemErrorCode.UPDATE_DATA_ERROR, id);
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
     * @param userId          用户 ID，可为 null（表示 Passport 会话）
     * @param applicationCode 应用编码
     * @return 构建完成的 {@link TokenJWTPayload}，包含用户、机构、应用及应用作用域信息
     * @throws BusinessException 当用户、机构或应用不存在，或机构不可用时抛出
     */
    public TokenJWTPayload fetchTokenContext(Long userId, String applicationCode) {
        ApplicationSelectDto appSelect = new ApplicationSelectDto();
        appSelect.setApplicationCode(applicationCode);
        List<ApplicationVo> applications = applicationService.selectList(appSelect);
        Asserts.isTrue(Collections.isNotEmpty(applications),
            SystemErrorCode.UNAUTHORIZED, applicationCode
        );
        ApplicationVo application = applications.getFirst();
        Boolean canIntegrate = application.getCanIntegrate();
        Boolean landing = application.getLanding();
        boolean isDefaultMain = Boolean.TRUE.equals(landing) && Boolean.TRUE.equals(canIntegrate);

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
}
