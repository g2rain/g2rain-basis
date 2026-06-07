package com.g2rain.basis.service.impl;

import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.id.IdGenerator;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.utils.Asserts;
import com.g2rain.common.utils.Moments;
import com.g2rain.basis.converter.IdpEnterpriseOrganConverter;
import com.g2rain.basis.dao.IdpEnterpriseOrganDao;
import com.g2rain.basis.dao.po.IdpEnterpriseOrganPo;
import com.g2rain.basis.dto.IdpEnterpriseOrganDto;
import com.g2rain.basis.dto.IdpEnterpriseOrganSelectDto;
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
    public Long save(IdpEnterpriseOrganDto dto) {
        // 转换DTO为PO
        IdpEnterpriseOrganPo entity = IdpEnterpriseOrganConverter.INSTANCE.dto2po(dto);

        // 判断是新增还是更新
        Long id = entity.getId();
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
}