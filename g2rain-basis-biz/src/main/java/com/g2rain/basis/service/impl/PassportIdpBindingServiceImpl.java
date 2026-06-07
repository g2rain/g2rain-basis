package com.g2rain.basis.service.impl;

import com.g2rain.basis.converter.PassportIdpBindingConverter;
import com.g2rain.basis.dao.IdpEnterpriseOrganDao;
import com.g2rain.basis.dao.PassportIdpBindingDao;
import com.g2rain.basis.dao.po.PassportIdpBindingPo;
import com.g2rain.basis.dto.IdpEnterpriseOrganDto;
import com.g2rain.basis.dto.IdpEnterpriseOrganSelectDto;
import com.g2rain.basis.dto.PassportIdpBindingBindDto;
import com.g2rain.basis.dto.PassportIdpBindingDto;
import com.g2rain.basis.dto.PassportIdpBindingSelectDto;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.enums.IdpBindMode;
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

    @Resource(name = "idpEnterpriseOrganDao")
    private IdpEnterpriseOrganDao idpEnterpriseOrganDao;

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
        // 转换DTO为PO
        PassportIdpBindingPo entity = PassportIdpBindingConverter.INSTANCE.dto2po(dto);
        if (entity.getIdpApplicationCode() == null) {
            entity.setIdpApplicationCode("");
        }

        // 判断是新增还是更新
        Long id = entity.getId();
        if (Objects.isNull(id) || id == 0) {
            // 新增：使用IdGenerator生成主键
            entity.setId(idGenerator.generateId());
            LocalDateTime now = Moments.now();
            entity.setUpdateTime(now);
            entity.setCreateTime(now);
            int success = passportIdpBindingDao.insert(entity);
            Asserts.greaterThan(success, 0, SystemErrorCode.CREATE_DATA_ERROR);
        } else {
            // 更新：直接更新
            entity.setUpdateTime(Moments.now());
            int success = passportIdpBindingDao.update(entity);
            Asserts.greaterThan(success, 0, SystemErrorCode.UPDATE_DATA_ERROR, id);
        }

        return entity.getId();
    }

    @Override
    public Long bind(PassportIdpBindingBindDto dto) {
        IdpType.validate(dto.getIdpType());
        IdpBindMode.validate(dto.getBindMode());

        String idpType = dto.getIdpType().trim();
        String idpSubject = dto.getIdpSubject().trim();
        String idpApplicationCode = dto.getIdpApplicationCode() == null ? "" : dto.getIdpApplicationCode().trim();
        String corpId = Strings.isBlank(dto.getCorpId()) ? null : dto.getCorpId().trim();

        if (IdpType.DINGTALK.name().equals(idpType)) {
            if (Strings.isBlank(corpId)) {
                throw new BusinessException(SystemErrorCode.PARAM_REQUIRED, "corpId");
            }
            ensureIdpEnterpriseOrganBound(dto.getOrganId(), idpType, corpId, canAutoProvisionEnterpriseOrgan(dto));
        }

        PassportIdpBindingSelectDto subjectQuery = new PassportIdpBindingSelectDto();
        subjectQuery.setIdpType(idpType);
        subjectQuery.setIdpSubject(idpSubject);
        subjectQuery.setIdpApplicationCode(idpApplicationCode);
        List<PassportIdpBindingPo> existing = passportIdpBindingDao.selectList(subjectQuery);
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
        saveDto.setIdpApplicationCode(idpApplicationCode);
        saveDto.setBindMode(dto.getBindMode().trim());
        saveDto.setRawProfile(Strings.isBlank(dto.getRawProfile()) ? "{}" : dto.getRawProfile());
        return save(saveDto);
    }

    @Override
    public int delete(Long id) {
        return passportIdpBindingDao.delete(id);
    }

    /**
     * USER 会话且为机构管理员时，可自动建立三方企业与 organ 的绑定关系
     */
    private boolean canAutoProvisionEnterpriseOrgan(PassportIdpBindingBindDto dto) {
        return SessionType.USER.equals(resolveSessionType(dto)) && resolveAdminUser(dto);
    }

    private SessionType resolveSessionType(PassportIdpBindingBindDto dto) {
        if (Strings.isNotBlank(dto.getSessionType())) {
            try {
                return SessionType.valueOf(dto.getSessionType().trim());
            } catch (IllegalArgumentException ignored) {
                // fall through
            }
        }
        Long userId = PrincipalContextHolder.getUserId();
        if (userId != null && userId > 0L) {
            return SessionType.USER;
        }
        return SessionType.PASSPORT;
    }

    private boolean resolveAdminUser(PassportIdpBindingBindDto dto) {
        if (dto.getAdminUser() != null) {
            return Boolean.TRUE.equals(dto.getAdminUser());
        }
        return PrincipalContextHolder.isAdminUser();
    }

    private void ensureIdpEnterpriseOrganBound(
        Long organId,
        String idpType,
        String corpId,
        boolean autoProvision
    ) {
        IdpEnterpriseOrganSelectDto activeQuery = new IdpEnterpriseOrganSelectDto();
        activeQuery.setOrganId(organId);
        activeQuery.setIdpType(idpType);
        activeQuery.setEnterpriseId(corpId);
        activeQuery.setStatus("ACTIVE");
        if (!idpEnterpriseOrganDao.selectList(activeQuery).isEmpty()) {
            return;
        }
        if (!autoProvision) {
            throw new BusinessException(BasisErrorCode.IDP_ENTERPRISE_ORGAN_NOT_BOUND);
        }
        IdpEnterpriseOrganDto saveDto = new IdpEnterpriseOrganDto();
        saveDto.setOrganId(organId);
        saveDto.setIdpType(idpType);
        saveDto.setEnterpriseId(corpId);
        saveDto.setStatus("ACTIVE");
        idpEnterpriseOrganService.save(saveDto);
    }
}