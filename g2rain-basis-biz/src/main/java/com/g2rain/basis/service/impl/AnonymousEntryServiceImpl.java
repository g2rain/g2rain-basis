package com.g2rain.basis.service.impl;

import com.g2rain.basis.converter.AnonymousEntryConverter;
import com.g2rain.basis.converter.OrganConverter;
import com.g2rain.basis.dao.AnonymousEntryDao;
import com.g2rain.basis.dao.ApplicationAuthorizationDao;
import com.g2rain.basis.dao.OrganDao;
import com.g2rain.basis.dao.po.AnonymousEntryPo;
import com.g2rain.basis.dao.po.OrganPo;
import com.g2rain.basis.dto.AnonymousEntryDto;
import com.g2rain.basis.dto.AnonymousEntrySelectDto;
import com.g2rain.basis.dto.ApplicationAuthorizationSelectDto;
import com.g2rain.basis.dto.UpdateStatusDto;
import com.g2rain.basis.enums.AuthorizationStatus;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.enums.EntryStatus;
import com.g2rain.basis.enums.OrganStatus;
import com.g2rain.basis.service.AnonymousEntryService;
import com.g2rain.basis.service.SessionPermSyncService;
import com.g2rain.basis.vo.AnonymousEntryVo;
import com.g2rain.basis.vo.OrganVo;
import com.g2rain.common.enums.OrganType;
import com.g2rain.common.enums.SessionType;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.id.IdGenerator;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.utils.Asserts;
import com.g2rain.common.utils.Collections;
import com.g2rain.common.utils.Moments;
import com.g2rain.common.utils.Strings;
import com.g2rain.common.validation.Validations;
import com.g2rain.common.web.PrincipalContextHolder;
import com.g2rain.mybatis.pagination.PageContext;
import com.g2rain.mybatis.pagination.model.Page;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 匿名接入码表服务实现类
 * 表名: anonymous_entry
 *
 * @author G2rain Generator
 */
@Service(value = "anonymousEntryServiceImpl")
public class AnonymousEntryServiceImpl implements AnonymousEntryService {

    /**
     * 接入码前缀
     */
    private static final String KEY_PREFIX = "ek_";

    /**
     * 接入码随机部分长度：62^22 ≈ 2^131，不低于 128 bit 熵
     */
    private static final int KEY_LENGTH = 22;

    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private static final SecureRandom RANDOM = new SecureRandom();

    @Resource(name = "anonymousEntryDao")
    private AnonymousEntryDao anonymousEntryDao;

    @Resource(name = "organDao")
    private OrganDao organDao;

    @Resource(name = "applicationAuthorizationDao")
    private ApplicationAuthorizationDao applicationAuthorizationDao;

    @Resource
    private SessionPermSyncService sessionPermSyncService;

    private IdGenerator idGenerator;

    @Qualifier("idGenerator")
    @Autowired(required = false)
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public List<AnonymousEntryVo> selectList(AnonymousEntrySelectDto selectDto) {
        return anonymousEntryDao.selectList(selectDto)
            .stream()
            .map(AnonymousEntryConverter.INSTANCE::po2vo)
            .toList();
    }

    @Override
    public PageData<AnonymousEntryVo> selectPage(PageSelectListDto<AnonymousEntrySelectDto> selectDto) {
        Page<AnonymousEntryPo> page = PageContext.of(selectDto.getPageNum(), selectDto.getPageSize(), () -> {
            anonymousEntryDao.selectList(selectDto.getQuery());
        });
        List<AnonymousEntryVo> result = page.getResult()
            .stream()
            .map(AnonymousEntryConverter.INSTANCE::po2vo)
            .toList();
        return PageData.of(page.getPageNum(), page.getPageSize(), page.getTotal(), result);
    }

    /**
     * 新增或更新接入码。
     * <p>新增：接入码由服务端生成，状态固定 {@code ACTIVE}；非运营公司只能为本机构创建；机构须为有效租户且已开通该应用。</p>
     * <p>更新：只允许改名称；机构、应用、接入码不可改，启停走 {@link #updateStatus}。</p>
     */
    @Override
    public Long save(AnonymousEntryDto dto) {
        Validations.validateSave(dto);

        AnonymousEntryPo entity = AnonymousEntryConverter.INSTANCE.dto2po(dto);
        Long id = entity.getId();
        if (Objects.isNull(id) || id == 0) {
            // 非运营公司只能为本机构创建
            if (!PrincipalContextHolder.isAdminCompany() && !PrincipalContextHolder.isBackEnd()) {
                entity.setOrganId(PrincipalContextHolder.getOrganId());
            }
            requireOpenOrgan(entity.getOrganId(), entity.getApplicationId());

            entity.setId(idGenerator.generateId());
            entity.setEntryKey(generateKey());
            entity.setStatus(EntryStatus.ACTIVE.name());
            LocalDateTime now = Moments.now();
            entity.setUpdateTime(now);
            entity.setCreateTime(now);
            Asserts.greaterThan(anonymousEntryDao.insert(entity), 0, SystemErrorCode.CREATE_DATA_ERROR);
        } else {
            AnonymousEntryPo existing = anonymousEntryDao.selectById(id);
            Asserts.isTrue(Objects.nonNull(existing), SystemErrorCode.DATA_NOT_EXISTS, id);

            AnonymousEntryPo update = new AnonymousEntryPo();
            update.setId(id);
            update.setEntryName(entity.getEntryName());
            update.setUpdateTime(Moments.now());
            Asserts.greaterThan(anonymousEntryDao.update(update), 0, SystemErrorCode.UPDATE_DATA_ERROR, id);
        }

        return entity.getId();
    }

    @Override
    public int updateStatus(Long id, UpdateStatusDto dto) {
        EntryStatus.validate(dto.getStatus());

        AnonymousEntryPo existing = anonymousEntryDao.selectById(id);
        Asserts.isTrue(Objects.nonNull(existing), SystemErrorCode.DATA_NOT_EXISTS, id);

        AnonymousEntryPo entity = new AnonymousEntryPo();
        entity.setId(id);
        entity.setStatus(dto.getStatus());
        entity.setUpdateTime(Moments.now());
        int success = anonymousEntryDao.update(entity);
        Asserts.greaterThan(success, 0, SystemErrorCode.UPDATE_DATA_ERROR, id);

        sessionPermSyncService.notifyOrgan(SessionType.ANONYMOUS, existing.getOrganId());
        return success;
    }

    @Override
    public int delete(Long id) {
        AnonymousEntryPo existing = anonymousEntryDao.selectById(id);
        Asserts.isTrue(Objects.nonNull(existing), SystemErrorCode.DATA_NOT_EXISTS, id);
        int success = anonymousEntryDao.delete(id);

        sessionPermSyncService.notifyOrgan(SessionType.ANONYMOUS, existing.getOrganId());
        return success;
    }

    @Override
    public AnonymousEntryVo resolve(String entryKey, Long applicationId) {
        Asserts.isTrue(Strings.isNotBlank(entryKey), BasisErrorCode.ANONYMOUS_ENTRY_UNAVAILABLE);
        AnonymousEntrySelectDto select = new AnonymousEntrySelectDto();
        select.setEntryKey(entryKey);
        List<AnonymousEntryPo> entries = anonymousEntryDao.selectListWithoutIsolation(select);
        return requireAvailable(Collections.isEmpty(entries) ? null : entries.getFirst(), applicationId);
    }

    @Override
    public AnonymousEntryVo resolve(Long id, Long applicationId) {
        Asserts.isTrue(Objects.nonNull(id) && id > 0, BasisErrorCode.ANONYMOUS_ENTRY_UNAVAILABLE);
        return requireAvailable(anonymousEntryDao.selectByIdWithoutIsolation(id), applicationId);
    }

    @Override
    public OrganVo requireOpenOrgan(Long organId, Long applicationId) {
        OrganPo organ = organDao.selectById(organId);
        Asserts.isTrue(Objects.nonNull(organ), SystemErrorCode.PARAM_VAL_INVALID, organId);
        Asserts.isTrue(OrganStatus.ACTIVE.name().equals(organ.getStatus()), BasisErrorCode.ORGAN_UNAVAILABLE);
        Asserts.isTrue(OrganType.isTenant(organ.getOrganType()),
            SystemErrorCode.PARAM_VAL_INVALID, organ.getOrganType());

        ApplicationAuthorizationSelectDto authSelect = new ApplicationAuthorizationSelectDto();
        authSelect.setOrganId(organId);
        authSelect.setApplicationId(applicationId);
        authSelect.setStatus(AuthorizationStatus.ACTIVATED.name());
        Long authorized = applicationAuthorizationDao.checkApplicationAuthorizationExists(authSelect);
        Asserts.isTrue(Objects.nonNull(authorized) && authorized > 0,
            BasisErrorCode.APPLICATION_AUTHORIZATION_MISSING, applicationId);
        return OrganConverter.INSTANCE.po2vo(organ);
    }

    /**
     * 接入码存在、有效且属于该应用，否则统一返回"接入码不可用"。
     */
    private static AnonymousEntryVo requireAvailable(AnonymousEntryPo entry, Long applicationId) {
        boolean available = Objects.nonNull(entry)
            && EntryStatus.ACTIVE.name().equals(entry.getStatus())
            && Objects.equals(entry.getApplicationId(), applicationId);
        Asserts.isTrue(available, BasisErrorCode.ANONYMOUS_ENTRY_UNAVAILABLE);
        return AnonymousEntryConverter.INSTANCE.po2vo(entry);
    }

    private static String generateKey() {
        StringBuilder key = new StringBuilder(KEY_PREFIX.length() + KEY_LENGTH).append(KEY_PREFIX);
        for (int i = 0; i < KEY_LENGTH; i++) {
            key.append(BASE62.charAt(RANDOM.nextInt(BASE62.length())));
        }

        return key.toString();
    }
}
