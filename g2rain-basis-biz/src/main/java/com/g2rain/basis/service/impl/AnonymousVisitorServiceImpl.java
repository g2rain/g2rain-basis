package com.g2rain.basis.service.impl;

import com.g2rain.basis.converter.AnonymousVisitorConverter;
import com.g2rain.basis.dao.AnonymousVisitorDao;
import com.g2rain.basis.dao.po.AnonymousVisitorPo;
import com.g2rain.basis.dto.AnonymousVisitorDto;
import com.g2rain.basis.dto.AnonymousVisitorSelectDto;
import com.g2rain.basis.service.AnonymousVisitorService;
import com.g2rain.basis.vo.AnonymousVisitorVo;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.id.IdGenerator;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.utils.Asserts;
import com.g2rain.common.utils.Moments;
import com.g2rain.common.validation.Validations;
import com.g2rain.mybatis.pagination.PageContext;
import com.g2rain.mybatis.pagination.model.Page;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 匿名访客表服务实现类
 * 表名: anonymous_visitor
 *
 * @author G2rain Generator
 */
@Service(value = "anonymousVisitorServiceImpl")
public class AnonymousVisitorServiceImpl implements AnonymousVisitorService {

    @Resource(name = "anonymousVisitorDao")
    private AnonymousVisitorDao anonymousVisitorDao;

    private IdGenerator idGenerator;

    @Qualifier("idGenerator")
    @Autowired(required = false)
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public List<AnonymousVisitorVo> selectList(AnonymousVisitorSelectDto selectDto) {
        return anonymousVisitorDao.selectList(selectDto)
            .stream()
            .map(AnonymousVisitorConverter.INSTANCE::po2vo)
            .toList();
    }

    @Override
    public PageData<AnonymousVisitorVo> selectPage(PageSelectListDto<AnonymousVisitorSelectDto> selectDto) {
        Page<AnonymousVisitorPo> page = PageContext.of(selectDto.getPageNum(), selectDto.getPageSize(), () -> {
            anonymousVisitorDao.selectList(selectDto.getQuery());
        });
        List<AnonymousVisitorVo> result = page.getResult()
            .stream()
            .map(AnonymousVisitorConverter.INSTANCE::po2vo)
            .toList();
        return PageData.of(page.getPageNum(), page.getPageSize(), page.getTotal(), result);
    }

    @Override
    public Long save(AnonymousVisitorDto dto) {
        Validations.validateSave(dto);

        // 转换 DTO 为 PO
        AnonymousVisitorPo entity = AnonymousVisitorConverter.INSTANCE.dto2po(dto);

        // 判断是新增还是更新
        Long id = entity.getId();
        if (Objects.isNull(id) || id == 0) {
            // 新增：使用IdGenerator生成主键
            entity.setId(idGenerator.generateId());
            LocalDateTime now = Moments.now();
            entity.setUpdateTime(now);
            entity.setCreateTime(now);
            int success = anonymousVisitorDao.insert(entity);
            Asserts.greaterThan(success, 0, SystemErrorCode.CREATE_DATA_ERROR);
        } else {
            AnonymousVisitorPo existing = anonymousVisitorDao.selectById(id);
            Asserts.isTrue(Objects.nonNull(existing), SystemErrorCode.DATA_NOT_EXISTS, id);
            // 更新：直接更新
            entity.setUpdateTime(Moments.now());
            int success = anonymousVisitorDao.update(entity);
            Asserts.greaterThan(success, 0, SystemErrorCode.UPDATE_DATA_ERROR, id);
        }

        return entity.getId();
    }

    @Override
    public int delete(Long id) {
        AnonymousVisitorPo existing = anonymousVisitorDao.selectById(id);
        Asserts.isTrue(Objects.nonNull(existing), SystemErrorCode.DATA_NOT_EXISTS, id);
        return anonymousVisitorDao.delete(id);
    }

    /**
     * 同一浏览器并发换票时，插入可能撞唯一键 (organ_id, key_thumbprint)，此时回查已创建的访客。
     */
    @Override
    public Long resolve(Long organId, String thumbprint, Long entryId) {
        AnonymousVisitorPo existing = find(organId, thumbprint);
        LocalDateTime now = Moments.now();
        if (Objects.nonNull(existing)) {
            AnonymousVisitorPo touch = new AnonymousVisitorPo();
            touch.setId(existing.getId());
            touch.setLastSeenTime(now);
            touch.setUpdateTime(now);
            anonymousVisitorDao.updateWithoutIsolation(touch);
            return existing.getId();
        }

        AnonymousVisitorPo visitor = new AnonymousVisitorPo();
        visitor.setId(idGenerator.generateId());
        visitor.setOrganId(organId);
        visitor.setKeyThumbprint(thumbprint);
        visitor.setEntryId(entryId);
        visitor.setLastSeenTime(now);
        visitor.setCreateTime(now);
        visitor.setUpdateTime(now);
        try {
            anonymousVisitorDao.insertWithoutIsolation(visitor);
            return visitor.getId();
        } catch (DuplicateKeyException e) {
            AnonymousVisitorPo created = find(organId, thumbprint);
            Asserts.isTrue(Objects.nonNull(created), SystemErrorCode.CREATE_DATA_ERROR);
            return created.getId();
        }
    }

    private AnonymousVisitorPo find(Long organId, String thumbprint) {
        AnonymousVisitorSelectDto select = new AnonymousVisitorSelectDto();
        select.setOrganId(organId);
        select.setKeyThumbprint(thumbprint);
        List<AnonymousVisitorPo> visitors = anonymousVisitorDao.selectListWithoutIsolation(select);
        return visitors.isEmpty() ? null : visitors.getFirst();
    }
}
