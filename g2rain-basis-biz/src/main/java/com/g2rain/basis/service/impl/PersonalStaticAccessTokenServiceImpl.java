package com.g2rain.basis.service.impl;

import com.g2rain.basis.converter.PersonalStaticAccessTokenConverter;
import com.g2rain.basis.dao.ApplicationAuthorizationDao;
import com.g2rain.basis.dao.ApplicationDao;
import com.g2rain.basis.dao.PersonalStaticAccessTokenDao;
import com.g2rain.basis.dao.UserDao;
import com.g2rain.basis.dao.po.ApplicationAuthorizationPo;
import com.g2rain.basis.dao.po.ApplicationPo;
import com.g2rain.basis.dao.po.PersonalStaticAccessTokenPo;
import com.g2rain.basis.dao.po.UserPo;
import com.g2rain.basis.dto.PersonalStaticAccessTokenDto;
import com.g2rain.basis.dto.PersonalStaticAccessTokenSelectDto;
import com.g2rain.basis.dto.UpdateStatusDto;
import com.g2rain.basis.dto.UserSelectDto;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.enums.BasisSyncerEnum;
import com.g2rain.basis.enums.StaticTokenStatus;
import com.g2rain.basis.service.PersonalStaticAccessTokenService;
import com.g2rain.basis.utils.BasisUtils;
import com.g2rain.basis.utils.Constants;
import com.g2rain.basis.vo.PersonalStaticAccessTokenVo;
import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.id.IdGenerator;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.syncer.EventPublisherHub;
import com.g2rain.common.utils.Asserts;
import com.g2rain.common.utils.Moments;
import com.g2rain.common.utils.Strings;
import com.g2rain.common.web.PrincipalContextHolder;
import com.g2rain.mybatis.pagination.PageContext;
import com.g2rain.mybatis.pagination.model.Page;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 个人静态访问令牌表服务实现类
 * 表名: personal_static_access_token
 *
 * @author G2rain Generator
 */
@Service(value = "personalStaticAccessTokenServiceImpl")
public class PersonalStaticAccessTokenServiceImpl implements PersonalStaticAccessTokenService {

    @Resource(name = "personalStaticAccessTokenDao")
    private PersonalStaticAccessTokenDao personalStaticAccessTokenDao;

    @Resource(name = "applicationAuthorizationDao")
    private ApplicationAuthorizationDao applicationAuthorizationDao;

    @Resource(name = "applicationDao")
    private ApplicationDao applicationDao;

    @Resource(name = "userDao")
    private UserDao userDao;

    @Resource
    private EventPublisherHub eventPublisherHub;

    private IdGenerator idGenerator;

    @Qualifier("idGenerator")
    @Autowired(required = false)
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public List<PersonalStaticAccessTokenVo> selectList(PersonalStaticAccessTokenSelectDto selectDto) {
        Asserts.isTrue(Objects.nonNull(selectDto.getApplicationAuthorizationId()),
            SystemErrorCode.PARAM_REQUIRED, "applicationAuthorizationId"
        );

        if (!(PrincipalContextHolder.isAdminCompany() || PrincipalContextHolder.isAdminUser())) {
            selectDto.setUserId(PrincipalContextHolder.getUserId());
        }

        return mergeUsers(personalStaticAccessTokenDao.selectList(selectDto));
    }

    @Override
    public PageData<PersonalStaticAccessTokenVo> selectPage(PageSelectListDto<PersonalStaticAccessTokenSelectDto> selectDto) {
        boolean required = Objects.nonNull(selectDto) && Objects.nonNull(selectDto.getQuery())
            && Objects.nonNull(selectDto.getQuery().getApplicationAuthorizationId());
        Asserts.isTrue(required, SystemErrorCode.PARAM_REQUIRED, "applicationAuthorizationId");

        Page<PersonalStaticAccessTokenPo> page = PageContext.of(selectDto.getPageNum(), selectDto.getPageSize(), () -> {
            PersonalStaticAccessTokenSelectDto query = selectDto.getQuery();
            if (!(PrincipalContextHolder.isAdminCompany() || PrincipalContextHolder.isAdminUser())) {
                query.setUserId(PrincipalContextHolder.getUserId());
            }

            personalStaticAccessTokenDao.selectList(query);
        });

        return PageData.of(page.getPageNum(), page.getPageSize(), page.getTotal(), mergeUsers(page.getResult()));
    }

    @Override
    public Long save(PersonalStaticAccessTokenDto dto) {
        Long applicationAuthorizationId = dto.getApplicationAuthorizationId();
        ApplicationAuthorizationPo applicationAuthorization = applicationAuthorizationDao.selectById(applicationAuthorizationId);
        Asserts.isTrue(Objects.nonNull(applicationAuthorization), SystemErrorCode.PARAM_VAL_INVALID, applicationAuthorizationId);

        // 当前用户不属于当前的应用授权记录, 禁止生成, 防止运营乱创建
        Long userOrganId = PrincipalContextHolder.getOrganId();
        Long appAuthorizationOrganId = applicationAuthorization.getOrganId();
        Asserts.isTrue(Objects.equals(userOrganId, appAuthorizationOrganId), BasisErrorCode.ONLY_OWN_ORG_APIKEY_ALLOWED);

        ApplicationPo application = applicationDao.selectById(applicationAuthorization.getApplicationId());
        Asserts.isTrue(Objects.nonNull(application), SystemErrorCode.PARAM_VAL_INVALID, applicationAuthorizationId);
        Asserts.isTrue(Boolean.TRUE.equals(application.getApiKeySupported()), SystemErrorCode.PARAM_VAL_INVALID, applicationAuthorizationId);
        Long userId = PrincipalContextHolder.getUserId();

        // 判断是新增还是更新
        Long id = dto.getId();
        if (Objects.nonNull(id) && id > 0) {
            // 更新：直接更新
            PersonalStaticAccessTokenPo entity = new PersonalStaticAccessTokenPo();
            entity.setId(id);
            entity.setName(dto.getName());
            entity.setUpdateTime(Moments.now());
            int success = personalStaticAccessTokenDao.update(entity);
            Asserts.greaterThan(success, 0, SystemErrorCode.UPDATE_DATA_ERROR, id);
            return id;
        }

        Asserts.isTrue(Objects.nonNull(dto.getTokenHash()), SystemErrorCode.PARAM_REQUIRED, "tokenHash");
        Asserts.isTrue(Objects.nonNull(dto.getMaskedToken()), SystemErrorCode.PARAM_REQUIRED, "maskedToken");

        // 校验 token hash 是否重复, 因为他是全局唯一
        PersonalStaticAccessTokenSelectDto selectDto = new PersonalStaticAccessTokenSelectDto();
        selectDto.setTokenHash(dto.getTokenHash());
        Long total = personalStaticAccessTokenDao.checkStaticAccessTokenExists(selectDto);
        Asserts.lessThan(total, 1, SystemErrorCode.UPDATE_DATA_ERROR);

        // 转换 DTO 为PO
        PersonalStaticAccessTokenPo entity = PersonalStaticAccessTokenConverter.INSTANCE.dto2po(dto);
        // 新增：使用IdGenerator生成主键
        entity.setId(idGenerator.generateId());
        LocalDateTime now = Moments.now();
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
        entity.setUserId(userId);
        entity.setApplicationId(application.getId());
        entity.setStatus(StaticTokenStatus.ACTIVATED.name());
        entity.setOrganId(applicationAuthorization.getOrganId());
        int success = personalStaticAccessTokenDao.insert(entity);
        Asserts.greaterThan(success, 0, SystemErrorCode.CREATE_DATA_ERROR);
        return entity.getId();
    }

    /**
     * 按明文 API Key 查询：内部 SHA-256 后与表字段 {@code token_hash} 匹配，不记录明文。
     */
    @Override
    public PersonalStaticAccessTokenVo selectByApiKey(String apiKey) {
        if (Strings.isBlank(apiKey)) {
            return null;
        }

        PersonalStaticAccessTokenPo sat = personalStaticAccessTokenDao.selectByTokenHash(
            BasisUtils.sha256Hex(apiKey)
        );
        if (Objects.isNull(sat)) {
            return null;
        }

        return PersonalStaticAccessTokenConverter.INSTANCE.po2vo(sat);
    }

    /**
     * 修改个人静态访问令牌状态
     * <p>
     * 如果状态未变更，则直接返回成功。
     *
     * @param dto 修改状态参数
     * @return 更新记录数
     * @throws BusinessException 参数校验失败
     */
    @Transactional
    public int updateStatus(Long id, UpdateStatusDto dto) {
        // 非运营公司不允许修改状态
        if (!PrincipalContextHolder.isAdminCompany()) {
            return 0;
        }

        String status = dto.getStatus();
        // 校验状态参数
        StaticTokenStatus.validate(status);

        // 查看记录是否存在
        PersonalStaticAccessTokenPo personalStaticAccessToken = personalStaticAccessTokenDao.selectById(id);
        Asserts.isTrue(Objects.nonNull(personalStaticAccessToken), SystemErrorCode.PARAM_VAL_INVALID, id);

        // 状态没变更, 不需要修改, 直接提示成功即可
        if (personalStaticAccessToken.getStatus().equals(status)) {
            return 1;
        }

        PersonalStaticAccessTokenPo staticAccessToken = new PersonalStaticAccessTokenPo();
        staticAccessToken.setId(personalStaticAccessToken.getId());
        staticAccessToken.setStatus(status);
        staticAccessToken.setUpdateTime(Moments.now());
        int updated = personalStaticAccessTokenDao.update(staticAccessToken);
        if (updated == 0) {
            return 0;
        }

        publishTokenCacheSync(personalStaticAccessToken.getTokenHash());
        return updated;
    }

    @Override
    @Transactional
    public int delete(Long id) {
        PersonalStaticAccessTokenPo existing = personalStaticAccessTokenDao.selectById(id);
        if (Objects.isNull(existing)) {
            return 0;
        }

        int deleted = personalStaticAccessTokenDao.delete(id);
        if (deleted == 0) {
            return 0;
        }

        publishTokenCacheSync(existing.getTokenHash());
        return deleted;
    }

    /**
     * 令牌状态变更或物理删除后，向 cache-sync 广播 tokenHash，通知各网关节点失效 API Key 本地缓存。
     */
    private void publishTokenCacheSync(String tokenHash) {
        if (Strings.isBlank(tokenHash)) {
            return;
        }

        eventPublisherHub.sendUpdate(
            Constants.SYNC_OUTPUT_BINDING,
            BasisSyncerEnum.STATIC_ACCESS_TOKEN.name(),
            tokenHash
        );
    }

    /**
     * 设置用户信息
     *
     * @param staticAccessTokens 个人静态访问令牌 PO
     * @return 个人静态访问令牌 VO
     */
    private List<PersonalStaticAccessTokenVo> mergeUsers(List<PersonalStaticAccessTokenPo> staticAccessTokens) {
        Set<Long> ids = staticAccessTokens.stream().map(PersonalStaticAccessTokenPo::getUserId).collect(Collectors.toSet());
        UserSelectDto selectDto = new UserSelectDto();
        selectDto.setIds(ids);
        Map<Long, UserPo> userMap = userDao.selectListWithoutIsolation(selectDto).stream().collect(
            Collectors.toMap(UserPo::getId, Function.identity(), (e1, _) -> e1)
        );

        List<PersonalStaticAccessTokenVo> result = new ArrayList<>(staticAccessTokens.size());
        for (PersonalStaticAccessTokenPo staticAccessToken : staticAccessTokens) {
            PersonalStaticAccessTokenVo vo = PersonalStaticAccessTokenConverter.INSTANCE.po2vo(staticAccessToken);
            UserPo User = userMap.get(staticAccessToken.getUserId());
            if (Objects.nonNull(User)) {
                vo.setUserName(User.getRealName());
            }

            result.add(vo);
        }

        return result;
    }
}
