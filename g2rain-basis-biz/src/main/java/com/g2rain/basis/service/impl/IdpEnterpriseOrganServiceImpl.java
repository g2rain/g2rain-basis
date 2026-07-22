package com.g2rain.basis.service.impl;

import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.id.IdGenerator;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.utils.Asserts;
import com.g2rain.common.utils.Moments;
import com.g2rain.common.utils.Strings;
import com.g2rain.basis.converter.IdpEnterpriseOrganConverter;
import com.g2rain.basis.dao.IdpEnterpriseOrganDao;
import com.g2rain.basis.dao.po.IdpEnterpriseOrganPo;
import com.g2rain.basis.dto.IdpEnterpriseOrganDto;
import com.g2rain.basis.dto.IdpEnterpriseOrganSelectDto;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.enums.IdpBindMode;
import com.g2rain.basis.enums.IdpType;
import com.g2rain.basis.service.IdpEnterpriseOrganService;
import com.g2rain.basis.vo.IdpEnterpriseOrganVo;
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
 * 外部企业/租户与平台机构关联表服务实现类
 * 表名: idp_enterprise_organ
 *
 * @author G2rain Generator
 */
@Service(value = "idpEnterpriseOrganServiceImpl")
public class IdpEnterpriseOrganServiceImpl implements IdpEnterpriseOrganService {

    private static final String STATUS_ACTIVE = "ACTIVE";
    private static final String STATUS_INACTIVE = "INACTIVE";

    @Resource(name = "idpEnterpriseOrganDao")
    private IdpEnterpriseOrganDao idpEnterpriseOrganDao;

    private IdGenerator idGenerator;

    @Qualifier("idGenerator")
    @Autowired(required = false)
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public List<IdpEnterpriseOrganVo> selectList(IdpEnterpriseOrganSelectDto selectDto) {
        return idpEnterpriseOrganDao.selectList(selectDto)
                .stream()
                .map(IdpEnterpriseOrganConverter.INSTANCE::po2vo)
                .toList();
    }

    @Override
    public PageData<IdpEnterpriseOrganVo> selectPage(PageSelectListDto<IdpEnterpriseOrganSelectDto> selectDto) {
        Page<IdpEnterpriseOrganPo> page = PageContext.of(selectDto.getPageNum(), selectDto.getPageSize(), () -> {
            idpEnterpriseOrganDao.selectList(selectDto.getQuery());
        });
        List<IdpEnterpriseOrganVo> result = page.getResult()
                .stream()
                .map(IdpEnterpriseOrganConverter.INSTANCE::po2vo)
                .toList();
        return PageData.of(page.getPageNum(), page.getPageSize(), page.getTotal(), result);
    }

    @Override
    public void ensureEnterpriseOrganBound(
        Long organId,
        String idpType,
        String enterpriseId,
        String bindMode,
        boolean autoProvision
    ) {
        requireEnterpriseId(enterpriseId);
        String normalizedBindMode = normalizeBindMode(bindMode);
        if (validateBindingAllowedOrIdempotent(organId, idpType, enterpriseId, null)) {
            return;
        }
        if (!autoProvision) {
            throw new BusinessException(BasisErrorCode.IDP_ENTERPRISE_ORGAN_NOT_BOUND);
        }
        IdpEnterpriseOrganDto saveDto = new IdpEnterpriseOrganDto();
        saveDto.setOrganId(organId);
        saveDto.setIdpType(idpType);
        saveDto.setEnterpriseId(enterpriseId);
        saveDto.setBindMode(normalizedBindMode);
        saveDto.setStatus(STATUS_ACTIVE);
        save(saveDto);
    }

    @Override
    public Long save(IdpEnterpriseOrganDto dto) {
        IdpType idpTypeEnum = IdpType.nameOf(dto.getIdpType());
        if(idpTypeEnum == null) {
            throw new BusinessException(SystemErrorCode.PARAM_VAL_INVALID, "idpType");
        }
        String idpType = dto.getIdpType().trim();
        String enterpriseId = dto.getEnterpriseId() == null ? null : dto.getEnterpriseId().trim();
        requireEnterpriseId(enterpriseId);
        String bindMode = normalizeBindMode(dto.getBindMode());

        // 转换DTO为PO
        IdpEnterpriseOrganPo entity = IdpEnterpriseOrganConverter.INSTANCE.dto2po(dto);
        entity.setIdpType(idpType);
        entity.setEnterpriseId(enterpriseId);
        entity.setBindMode(bindMode);
        if (Strings.isBlank(entity.getStatus())) {
            entity.setStatus(STATUS_ACTIVE);
        }

        Long id = entity.getId();
        Long excludeId = (Objects.isNull(id) || id == 0) ? null : id;
        rejectInactiveRecordRebind(excludeId, entity, enterpriseId);
        boolean alreadyBound = validateBindingAllowedOrIdempotent(
            entity.getOrganId(), idpType, enterpriseId, excludeId);
        if (alreadyBound && (Objects.isNull(id) || id == 0)) {
            return findActiveBindingId(idpType, enterpriseId, entity.getOrganId());
        }

        // 判断是新增还是更新
        if (Objects.isNull(id) || id == 0) {
            // 新增：使用IdGenerator生成主键
            entity.setId(idGenerator.generateId());
            LocalDateTime now = Moments.now();
            entity.setUpdateTime(now);
            entity.setCreateTime(now);
            int success = idpEnterpriseOrganDao.insert(entity);
            Asserts.greaterThan(success, 0, SystemErrorCode.CREATE_DATA_ERROR);
        } else {
            // 更新：直接更新
            entity.setUpdateTime(Moments.now());
            int success = idpEnterpriseOrganDao.update(entity);
            Asserts.greaterThan(success, 0, SystemErrorCode.UPDATE_DATA_ERROR, id);
        }

        return entity.getId();
    }

    @Override
    public int delete(Long id) {
        return idpEnterpriseOrganDao.delete(id);
    }

    private void requireEnterpriseId(String enterpriseId) {
        if (Strings.isBlank(enterpriseId)) {
            throw new BusinessException(SystemErrorCode.PARAM_REQUIRED, "corpId");
        }
    }

    /**
     * 同一 {@code (idp_type, enterprise_id)} 仅允许对应一个 {@code organ_id}；
     * 存在 INACTIVE 记录时须先删除后再新增。
     *
     * @return 若已存在与目标一致的 ACTIVE 绑定则 {@code true}（幂等）；否则 {@code false} 表示可新建
     */
    private boolean validateBindingAllowedOrIdempotent(
        Long organId,
        String idpType,
        String enterpriseId,
        Long excludeRecordId
    ) {
        List<IdpEnterpriseOrganPo> existing = selectByEnterprise(idpType, enterpriseId);
        if (existing.isEmpty()) {
            return false;
        }
        for (IdpEnterpriseOrganPo record : existing) {
            if (excludeRecordId != null && Objects.equals(record.getId(), excludeRecordId)) {
                continue;
            }
            if (STATUS_INACTIVE.equals(record.getStatus())) {
                throw new BusinessException(BasisErrorCode.IDP_ENTERPRISE_BINDING_INACTIVE_EXISTS);
            }
            if (STATUS_ACTIVE.equals(record.getStatus())) {
                if (Objects.equals(record.getOrganId(), organId)) {
                    return true;
                }
                throw new BusinessException(BasisErrorCode.IDP_ENTERPRISE_ALREADY_BOUND_TO_OTHER_ORGAN);
            }
        }
        return false;
    }

    private List<IdpEnterpriseOrganPo> selectByEnterprise(String idpType, String enterpriseId) {
        IdpEnterpriseOrganSelectDto query = new IdpEnterpriseOrganSelectDto();
        query.setIdpType(idpType);
        query.setEnterpriseId(enterpriseId.trim());
        return idpEnterpriseOrganDao.selectList(query);
    }

    private Long findActiveBindingId(String idpType, String enterpriseId, Long organId) {
        IdpEnterpriseOrganSelectDto query = new IdpEnterpriseOrganSelectDto();
        query.setIdpType(idpType);
        query.setEnterpriseId(enterpriseId);
        query.setOrganId(organId);
        query.setStatus(STATUS_ACTIVE);
        List<IdpEnterpriseOrganPo> existing = idpEnterpriseOrganDao.selectList(query);
        if (existing.isEmpty()) {
            return null;
        }
        return existing.getFirst().getId();
    }

    /**
     * INACTIVE 记录仅允许删除；不可直接改绑机构/企业或重新激活。
     */
    private void rejectInactiveRecordRebind(Long recordId, IdpEnterpriseOrganPo entity, String enterpriseId) {
        if (recordId == null) {
            return;
        }
        IdpEnterpriseOrganPo current = idpEnterpriseOrganDao.selectById(recordId);
        if (current == null || !STATUS_INACTIVE.equals(current.getStatus())) {
            return;
        }
        boolean rebind = !Objects.equals(current.getOrganId(), entity.getOrganId())
            || !Objects.equals(current.getEnterpriseId(), enterpriseId)
            || STATUS_ACTIVE.equals(entity.getStatus());
        if (rebind) {
            throw new BusinessException(BasisErrorCode.IDP_ENTERPRISE_BINDING_INACTIVE_EXISTS);
        }
    }

    private static String normalizeBindMode(String bindMode) {
        String normalized = Strings.isBlank(bindMode) ? IdpBindMode.INTERNAL.name() : bindMode.trim();
        IdpBindMode.validate(normalized);
        return normalized;
    }
}
