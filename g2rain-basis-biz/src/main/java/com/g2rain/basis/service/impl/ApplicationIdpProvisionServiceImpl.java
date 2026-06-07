package com.g2rain.basis.service.impl;

import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.id.IdGenerator;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.utils.Asserts;
import com.g2rain.common.utils.Moments;
import com.g2rain.basis.converter.ApplicationIdpProvisionConverter;
import com.g2rain.basis.dao.ApplicationIdpProvisionDao;
import com.g2rain.basis.dao.po.ApplicationIdpProvisionPo;
import com.g2rain.basis.dto.ApplicationIdpProvisionDto;
import com.g2rain.basis.dto.ApplicationIdpProvisionSelectDto;
import com.g2rain.basis.service.ApplicationIdpProvisionService;
import com.g2rain.basis.vo.ApplicationIdpProvisionVo;
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
 * 外部身份源应用与平台应用的绑定服务实现类
 * 表名: application_idp_provision
 *
 * @author G2rain Generator
 */
@Service(value = "applicationIdpProvisionServiceImpl")
public class ApplicationIdpProvisionServiceImpl implements ApplicationIdpProvisionService {

    @Resource(name = "applicationIdpProvisionDao")
    private ApplicationIdpProvisionDao applicationIdpProvisionDao;

    private IdGenerator idGenerator;

    @Qualifier("idGenerator")
    @Autowired(required = false)
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public List<ApplicationIdpProvisionVo> selectList(ApplicationIdpProvisionSelectDto selectDto) {
        return applicationIdpProvisionDao.selectList(selectDto)
                .stream()
                .map(ApplicationIdpProvisionConverter.INSTANCE::po2vo)
                .toList();
    }

    @Override
    public PageData<ApplicationIdpProvisionVo> selectPage(PageSelectListDto<ApplicationIdpProvisionSelectDto> selectDto) {
        Page<ApplicationIdpProvisionPo> page = PageContext.of(selectDto.getPageNum(), selectDto.getPageSize(), () -> {
            applicationIdpProvisionDao.selectList(selectDto.getQuery());
        });
        List<ApplicationIdpProvisionVo> result = page.getResult()
                .stream()
                .map(ApplicationIdpProvisionConverter.INSTANCE::po2vo)
                .toList();
        return PageData.of(page.getPageNum(), page.getPageSize(), page.getTotal(), result);
    }

    @Override
    public Long save(ApplicationIdpProvisionDto dto) {
        // 转换DTO为PO
        ApplicationIdpProvisionPo entity = ApplicationIdpProvisionConverter.INSTANCE.dto2po(dto);

        // 判断是新增还是更新
        Long id = entity.getId();
        if (Objects.isNull(id) || id == 0) {
            // 新增：使用IdGenerator生成主键
            entity.setId(idGenerator.generateId());
            LocalDateTime now = Moments.now();
            entity.setUpdateTime(now);
            entity.setCreateTime(now);
            int success = applicationIdpProvisionDao.insert(entity);
            Asserts.greaterThan(success, 0, SystemErrorCode.CREATE_DATA_ERROR);
        } else {
            // 更新：直接更新
            entity.setUpdateTime(Moments.now());
            int success = applicationIdpProvisionDao.update(entity);
            Asserts.greaterThan(success, 0, SystemErrorCode.UPDATE_DATA_ERROR, id);
        }

        return entity.getId();
    }

    @Override
    public int delete(Long id) {
        return applicationIdpProvisionDao.delete(id);
    }
}