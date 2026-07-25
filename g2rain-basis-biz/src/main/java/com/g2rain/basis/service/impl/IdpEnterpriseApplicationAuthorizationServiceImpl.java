package com.g2rain.basis.service.impl;

import com.g2rain.basis.converter.IdpEnterpriseApplicationAuthorizationConverter;
import com.g2rain.basis.dao.IdpEnterpriseApplicationAuthorizationDao;
import com.g2rain.basis.dao.po.IdpEnterpriseApplicationAuthorizationPo;
import com.g2rain.basis.dto.IdpEnterpriseApplicationAuthorizationDto;
import com.g2rain.basis.dto.IdpEnterpriseApplicationAuthorizationResolveRequest;
import com.g2rain.basis.dto.IdpEnterpriseApplicationAuthorizationRevokeDto;
import com.g2rain.basis.dto.IdpEnterpriseApplicationAuthorizationSelectDto;
import com.g2rain.basis.dto.IdpEnterpriseApplicationAuthorizationUpsertDto;
import com.g2rain.basis.enums.IdpApplicationAuthorizationStatus;
import com.g2rain.basis.enums.IdpBindMode;
import com.g2rain.basis.enums.IdpType;
import com.g2rain.basis.service.IdpEnterpriseApplicationAuthorizationService;
import com.g2rain.basis.vo.IdpEnterpriseApplicationAuthorizationResolveVo;
import com.g2rain.basis.vo.IdpEnterpriseApplicationAuthorizationVo;
import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.id.IdGenerator;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.utils.Asserts;
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

@Service("idpEnterpriseApplicationAuthorizationServiceImpl")
public class IdpEnterpriseApplicationAuthorizationServiceImpl
    implements IdpEnterpriseApplicationAuthorizationService {

    @Resource(name = "idpEnterpriseApplicationAuthorizationDao")
    private IdpEnterpriseApplicationAuthorizationDao dao;

    private IdGenerator idGenerator;

    @Qualifier("idGenerator")
    @Autowired(required = false)
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public List<IdpEnterpriseApplicationAuthorizationVo> selectList(
        IdpEnterpriseApplicationAuthorizationSelectDto selectDto) {
        return dao.selectList(selectDto).stream()
            .map(IdpEnterpriseApplicationAuthorizationConverter.INSTANCE::po2vo)
            .toList();
    }

    @Override
    public PageData<IdpEnterpriseApplicationAuthorizationVo> selectPage(
        PageSelectListDto<IdpEnterpriseApplicationAuthorizationSelectDto> selectDto) {
        Page<IdpEnterpriseApplicationAuthorizationPo> page =
            PageContext.of(selectDto.getPageNum(), selectDto.getPageSize(),
                () -> dao.selectList(selectDto.getQuery()));
        List<IdpEnterpriseApplicationAuthorizationVo> result = page.getResult().stream()
            .map(IdpEnterpriseApplicationAuthorizationConverter.INSTANCE::po2vo)
            .toList();
        return PageData.of(page.getPageNum(), page.getPageSize(), page.getTotal(), result);
    }

    @Override
    @Transactional
    public Long save(IdpEnterpriseApplicationAuthorizationDto dto) {
        IdpEnterpriseApplicationAuthorizationPo entity =
            IdpEnterpriseApplicationAuthorizationConverter.INSTANCE.dto2po(dto);
        normalizeAndValidate(entity);
        LocalDateTime now = Moments.now();
        if (entity.getId() == null || entity.getId() == 0) {
            entity.setId(nextId());
            entity.setCreateTime(now);
            entity.setUpdateTime(now);
            Asserts.greaterThan(dao.insert(entity), 0, SystemErrorCode.CREATE_DATA_ERROR);
        } else {
            entity.setUpdateTime(now);
            Asserts.greaterThan(dao.update(entity), 0,
                SystemErrorCode.UPDATE_DATA_ERROR, entity.getId());
        }
        return entity.getId();
    }

    @Override
    public int delete(Long id) {
        return dao.delete(id);
    }

    @Override
    @Transactional
    public Long upsert(IdpEnterpriseApplicationAuthorizationUpsertDto dto) {
        IdpEnterpriseApplicationAuthorizationPo existing =
            find(dto.getIdpType(), dto.getIdpApplicationCode(), dto.getEnterpriseId());
        IdpEnterpriseApplicationAuthorizationPo entity =
            existing == null ? new IdpEnterpriseApplicationAuthorizationPo() : existing;
        entity.setIdpType(dto.getIdpType());
        entity.setBindMode(dto.getBindMode());
        entity.setIdpApplicationCode(dto.getIdpApplicationCode());
        entity.setEnterpriseId(dto.getEnterpriseId());
        entity.setInstalledApplicationId(dto.getInstalledApplicationId());
        entity.setAuthorizationStatus(dto.getAuthorizationStatus());
        entity.setCredentialCiphertext(dto.getCredentialCiphertext());
        entity.setCredentialKeyId(dto.getCredentialKeyId());
        entity.setAuthorizedAt(dto.getAuthorizedAt());
        entity.setCredentialExpireAt(dto.getCredentialExpireAt());
        entity.setRawAuthorization(dto.getRawAuthorization());
        if (IdpApplicationAuthorizationStatus.ACTIVE.name()
            .equals(dto.getAuthorizationStatus())) {
            entity.setRevokedAt(null);
        }
        normalizeAndValidate(entity);
        LocalDateTime now = Moments.now();
        entity.setUpdateTime(now);
        if (existing == null) {
            entity.setId(nextId());
            entity.setCreateTime(now);
            Asserts.greaterThan(dao.insert(entity), 0, SystemErrorCode.CREATE_DATA_ERROR);
        } else {
            Asserts.greaterThan(dao.updateByVersion(entity), 0,
                SystemErrorCode.UPDATE_DATA_ERROR, entity.getId());
        }
        return entity.getId();
    }

    @Override
    @Transactional
    public int revoke(IdpEnterpriseApplicationAuthorizationRevokeDto dto) {
        IdpEnterpriseApplicationAuthorizationPo entity =
            find(dto.getIdpType(), dto.getIdpApplicationCode(), dto.getEnterpriseId());
        if (entity == null) {
            return 0;
        }
        entity.setAuthorizationStatus(IdpApplicationAuthorizationStatus.REVOKED.name());
        entity.setCredentialCiphertext(null);
        entity.setCredentialKeyId(null);
        entity.setCredentialExpireAt(null);
        entity.setRevokedAt(Objects.requireNonNullElseGet(dto.getRevokedAt(), Moments::now));
        entity.setUpdateTime(Moments.now());
        return dao.updateByVersion(entity);
    }

    @Override
    public IdpEnterpriseApplicationAuthorizationResolveVo resolve(
        IdpEnterpriseApplicationAuthorizationResolveRequest request) {
        String bindMode = normalizeBindMode(request.getBindMode());
        IdpEnterpriseApplicationAuthorizationPo entity =
            find(request.getIdpType(), request.getIdpApplicationCode(),
                request.getEnterpriseId());
        if (entity == null || !bindMode.equals(entity.getBindMode())) {
            return null;
        }
        IdpEnterpriseApplicationAuthorizationResolveVo result =
            new IdpEnterpriseApplicationAuthorizationResolveVo();
        result.setAuthorizationId(entity.getId());
        result.setIdpType(entity.getIdpType());
        result.setBindMode(entity.getBindMode());
        result.setIdpApplicationCode(entity.getIdpApplicationCode());
        result.setEnterpriseId(entity.getEnterpriseId());
        result.setInstalledApplicationId(entity.getInstalledApplicationId());
        result.setAuthorizationStatus(entity.getAuthorizationStatus());
        return result;
    }

    private IdpEnterpriseApplicationAuthorizationPo find(
        String idpType, String applicationCode, String enterpriseId) {
        IdpEnterpriseApplicationAuthorizationSelectDto query =
            new IdpEnterpriseApplicationAuthorizationSelectDto();
        query.setIdpType(normalizeIdpType(idpType));
        query.setIdpApplicationCode(requireText(applicationCode, "idpApplicationCode"));
        query.setEnterpriseId(requireText(enterpriseId, "enterpriseId"));
        List<IdpEnterpriseApplicationAuthorizationPo> records = dao.selectList(query);
        return records.isEmpty() ? null : records.getFirst();
    }

    private void normalizeAndValidate(IdpEnterpriseApplicationAuthorizationPo entity) {
        entity.setIdpType(normalizeIdpType(entity.getIdpType()));
        entity.setBindMode(normalizeBindMode(entity.getBindMode()));
        entity.setIdpApplicationCode(
            requireText(entity.getIdpApplicationCode(), "idpApplicationCode"));
        entity.setEnterpriseId(requireText(entity.getEnterpriseId(), "enterpriseId"));
        IdpApplicationAuthorizationStatus status =
            IdpApplicationAuthorizationStatus.require(entity.getAuthorizationStatus());
        entity.setAuthorizationStatus(status.name());
        entity.setInstalledApplicationId(trimToNull(entity.getInstalledApplicationId()));
        if (status == IdpApplicationAuthorizationStatus.ACTIVE
            && Strings.isBlank(entity.getInstalledApplicationId())) {
            throw new BusinessException(SystemErrorCode.PARAM_REQUIRED,
                "installedApplicationId");
        }
    }

    private static String normalizeIdpType(String value) {
        String normalized = requireText(value, "idpType");
        if (IdpType.nameOf(normalized) == null) {
            throw new BusinessException(SystemErrorCode.PARAM_VAL_INVALID, "idpType");
        }
        return normalized;
    }

    private static String normalizeBindMode(String value) {
        String normalized = requireText(value, "bindMode");
        IdpBindMode.validate(normalized);
        return normalized;
    }

    private static String requireText(String value, String field) {
        if (Strings.isBlank(value)) {
            throw new BusinessException(SystemErrorCode.PARAM_REQUIRED, field);
        }
        return value.trim();
    }

    private static String trimToNull(String value) {
        return Strings.isBlank(value) ? null : value.trim();
    }

    private Long nextId() {
        if (idGenerator == null) {
            throw new BusinessException(SystemErrorCode.CREATE_DATA_ERROR);
        }
        return idGenerator.generateId();
    }
}
