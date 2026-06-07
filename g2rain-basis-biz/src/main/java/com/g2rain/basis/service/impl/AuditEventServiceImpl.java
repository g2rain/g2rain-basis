package com.g2rain.basis.service.impl;

import com.g2rain.basis.converter.AuditEventConverter;
import com.g2rain.basis.dao.AuditEventDao;
import com.g2rain.basis.dao.po.AuditEventPo;
import com.g2rain.basis.dto.AuditEventDto;
import com.g2rain.basis.dto.AuditEventSelectDto;
import com.g2rain.basis.service.AuditEventService;
import com.g2rain.basis.vo.AuditEventVo;
import com.g2rain.common.id.IdGenerator;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.utils.Moments;
import com.g2rain.mybatis.pagination.PageContext;
import com.g2rain.mybatis.pagination.model.Page;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * {@link com.g2rain.basis.service.AuditEventService} 的实现，操作对象为审计事件流水表。
 *
 * @author G2rain Generator
 */
@Service(value = "auditEventServiceImpl")
public class AuditEventServiceImpl implements AuditEventService {

    @Resource(name = "auditEventDao")
    private AuditEventDao auditEventDao;

    private IdGenerator idGenerator;

    @Qualifier("idGenerator")
    @Autowired(required = false)
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public List<AuditEventVo> selectList(AuditEventSelectDto selectDto) {
        return auditEventDao.selectList(selectDto)
            .stream()
            .map(AuditEventConverter.INSTANCE::po2vo)
            .toList();
    }

    @Override
    public PageData<AuditEventVo> selectPage(PageSelectListDto<AuditEventSelectDto> selectDto) {
        Page<AuditEventPo> page = PageContext.of(selectDto.getPageNum(), selectDto.getPageSize(), () -> {
            auditEventDao.selectList(selectDto.getQuery());
        });
        List<AuditEventVo> result = page.getResult()
            .stream()
            .map(AuditEventConverter.INSTANCE::po2vo)
            .toList();
        return PageData.of(page.getPageNum(), page.getPageSize(), page.getTotal(), result);
    }

    @Override
    public Long save(AuditEventDto dto) {
        AuditEventPo entity = AuditEventConverter.INSTANCE.dto2po(dto);
        entity.setId(idGenerator.generateId());
        LocalDateTime now = Moments.now();
        entity.setUpdateTime(now);
        entity.setCreateTime(now);
        auditEventDao.insert(entity);
        return entity.getId();
    }

    public int delete(Long id) {
        return auditEventDao.delete(id);
    }
}
