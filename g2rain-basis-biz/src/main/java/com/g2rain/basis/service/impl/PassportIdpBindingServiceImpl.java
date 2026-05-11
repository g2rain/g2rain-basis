package com.g2rain.basis.service.impl;

import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.id.IdGenerator;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.utils.Asserts;
import com.g2rain.common.utils.Moments;
import com.g2rain.basis.converter.PassportIdpBindingConverter;
import com.g2rain.basis.dao.PassportIdpBindingDao;
import com.g2rain.basis.dao.po.PassportIdpBindingPo;
import com.g2rain.basis.dto.PassportIdpBindingDto;
import com.g2rain.basis.dto.PassportIdpBindingSelectDto;
import com.g2rain.basis.service.PassportIdpBindingService;
import com.g2rain.basis.vo.PassportIdpBindingVo;
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
    public int delete(Long id) {
        return passportIdpBindingDao.delete(id);
    }
}