package com.g2rain.basis.service.impl;

import com.g2rain.basis.converter.PassportIdpBindingConverter;
import com.g2rain.basis.dao.PassportIdpBindingDao;
import com.g2rain.basis.dao.po.PassportIdpBindingPo;
import com.g2rain.basis.dto.PassportIdpBindingBindDto;
import com.g2rain.basis.dto.PassportIdpBindingDto;
import com.g2rain.basis.dto.PassportIdpBindingSelectDto;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.enums.IdpType;
import com.g2rain.basis.service.IdpEnterpriseOrganService;
import com.g2rain.basis.service.PassportIdpBindingService;
import com.g2rain.basis.vo.PassportIdpBindingVo;
import com.g2rain.common.enums.SessionType;
import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.id.IdGenerator;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 账号与外部身份源绑定表服务实现类
 * 表名: passport_idp_binding
 *
 * @author G2rain Generator
 */
@Service(value = "passportIdpBindingServiceImpl")
public class PassportIdpBindingServiceImpl implements PassportIdpBindingService {

    @Resource(name = "passportIdpBindingDao")
    private PassportIdpBindingDao passportIdpBindingDao;

    @Resource(name = "idpEnterpriseOrganServiceImpl")
    private IdpEnterpriseOrganService idpEnterpriseOrganService;

    private IdGenerator idGenerator;

    @Qualifier("idGenerator")
    @Autowired(required = false)
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public List<PassportIdpBindingVo> selectList(PassportIdpBindingSelectDto selectDto) {
        return passportIdpBindingDao.selectList(selectDto)
                .stream()
                .map(PassportIdpBindingConverter.INSTANCE::po2vo)
                .toList();
    }

    @Override
    public PageData<PassportIdpBindingVo> selectPage(PageSelectListDto<PassportIdpBindingSelectDto> selectDto) {
        Page<PassportIdpBindingPo> page = PageContext.of(selectDto.getPageNum(), selectDto.getPageSize(), () -> {
            passportIdpBindingDao.selectList(selectDto.getQuery());
        });
        List<PassportIdpBindingVo> result = page.getResult()
                .stream()
                .map(PassportIdpBindingConverter.INSTANCE::po2vo)
                .toList();
        return PageData.of(page.getPageNum(), page.getPageSize(), page.getTotal(), result);
    }

    @Override
    public Long save(PassportIdpBindingDto dto) {
        PassportIdpBindingPo entity = PassportIdpBindingConverter.INSTANCE.dto2po(dto);
        if (entity.getIdpApplicationCode() == null) {
            entity.setIdpApplicationCode("");
        }

        Long id = entity.getId();
        if (Objects.isNull(id) || id == 0) {
            entity.setId(idGenerator.generateId());
            LocalDateTime now = Moments.now();
            entity.setUpdateTime(now);
            entity.setCreateTime(now);
            int success = passportIdpBindingDao.insert(entity);
            Asserts.greaterThan(success, 0, SystemErrorCode.CREATE_DATA_ERROR);
        } else {
            entity.setUpdateTime(Moments.now());
            int success = passportIdpBindingDao.update(entity);
            Asserts.greaterThan(success, 0, SystemErrorCode.UPDATE_DATA_ERROR, id);
        }

        return entity.getId();
    }

    @Override
    public Long bind(PassportIdpBindingBindDto dto) {
        validateBindPrincipal(dto);
        return doBind(dto, canAutoProvisionFromPrincipal());
    }

    @Override
    public Long bindInternal(PassportIdpBindingBindDto dto) {
        return doBind(dto, canAutoProvisionFromTrustedCaller(dto));
    }

    private Long doBind(PassportIdpBindingBindDto dto, boolean canAutoProvisionEnterpriseOrgan) {
        String idpType = dto.getIdpType().trim();
        IdpType idpTypeEnum = IdpType.nameOf(idpType);
        String idpSubject = dto.getIdpSubject().trim();
        String enterpriseId = Strings.isBlank(dto.getCorpId()) ? null : dto.getCorpId();
        String idpApplicationCode = dto.getIdpApplicationCode() == null ? "" : dto.getIdpApplicationCode().trim();
        String corpId = Strings.isBlank(dto.getCorpId()) ? null : dto.getCorpId().trim();
        if (idpTypeEnum != null && idpTypeEnum.requiresEnterpriseId()) {
            idpEnterpriseOrganService.ensureEnterpriseOrganBound(
                dto.getOrganId(), idpType, enterpriseId, dto.getBindMode(), canAutoProvisionEnterpriseOrgan);
        }

        PassportIdpBindingSelectDto subjectQuery = new PassportIdpBindingSelectDto();
        subjectQuery.setIdpType(idpType);
        subjectQuery.setIdpSubject(idpSubject);
        subjectQuery.setIdpApplicationCode(idpApplicationCode);
        List<PassportIdpBindingPo> existing = passportIdpBindingDao.selectListWithoutIsolation(subjectQuery);
        if (!existing.isEmpty()) {
            PassportIdpBindingPo bound = existing.getFirst();
            if (!Objects.equals(bound.getPassportId(), dto.getPassportId())) {
                throw new BusinessException(BasisErrorCode.PASSPORT_IDP_SUBJECT_ALREADY_BOUND);
            }
            return bound.getId();
        }

        PassportIdpBindingDto saveDto = new PassportIdpBindingDto();
        saveDto.setPassportId(dto.getPassportId());
        saveDto.setIdpType(idpType);
        saveDto.setIdpSubject(idpSubject);
        saveDto.setCorpId(corpId);
        saveDto.setIdpUserId(Strings.isBlank(dto.getIdpUserId()) ? null : dto.getIdpUserId().trim());
        saveDto.setIdpOpenId(Strings.isBlank(dto.getIdpOpenId()) ? null : dto.getIdpOpenId().trim());
        saveDto.setIdpApplicationCode(idpApplicationCode);
        saveDto.setBindMode(dto.getBindMode().trim());
        saveDto.setRawProfile(Strings.isBlank(dto.getRawProfile()) ? "{}" : dto.getRawProfile());
        return save(saveDto);
    }

    private void validateBindPrincipal(PassportIdpBindingBindDto dto) {
        Long principalPassportId = PrincipalContextHolder.getPassportId();
        Asserts.isTrue(principalPassportId != null && principalPassportId > 0L,
            SystemErrorCode.UNAUTHORIZED);
        Asserts.isTrue(Objects.equals(dto.getPassportId(), principalPassportId),
            SystemErrorCode.PARAM_VAL_INVALID, "passportId");

        if (SessionType.USER.equals(resolveSessionTypeFromPrincipal())) {
            Long principalOrganId = PrincipalContextHolder.getOrganId();
            Asserts.isTrue(principalOrganId != null && principalOrganId > 0L,
                SystemErrorCode.UNAUTHORIZED);
            Asserts.isTrue(Objects.equals(dto.getOrganId(), principalOrganId),
                SystemErrorCode.PARAM_VAL_INVALID, "organId");
        }
    }

    private static boolean canAutoProvisionFromPrincipal() {
        return SessionType.USER.equals(resolveSessionTypeFromPrincipal())
            && PrincipalContextHolder.isAdminUser();
    }

    static boolean canAutoProvisionFromTrustedCaller(PassportIdpBindingBindDto dto) {
        return SessionType.USER.equals(resolveSessionTypeFromDto(dto))
            && Boolean.TRUE.equals(dto.getAdminUser());
    }

    static SessionType resolveSessionTypeFromPrincipal() {
        Long userId = PrincipalContextHolder.getUserId();
        if (userId != null && userId > 0L) {
            return SessionType.USER;
        }
        return SessionType.PASSPORT;
    }

    static SessionType resolveSessionTypeFromDto(PassportIdpBindingBindDto dto) {
        if (Strings.isNotBlank(dto.getSessionType())) {
            try {
                return SessionType.valueOf(dto.getSessionType().trim());
            } catch (IllegalArgumentException ignored) {
                // fall through
            }
        }
        return SessionType.PASSPORT;
    }

    @Override
    public int delete(Long id) {
        return passportIdpBindingDao.delete(id);
    }
}
