package com.g2rain.basis.controller;

import com.g2rain.basis.api.AuditEventApi;
import com.g2rain.basis.dto.AuditEventSelectDto;
import com.g2rain.basis.service.AuditEventService;
import com.g2rain.basis.vo.AuditEventVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 审计事件对外接口控制器，实现 {@link AuditEventApi}，根路径为「/audit_event」。
 *
 * @author G2rain Generator
 */
@RestController
@RequestMapping("/audit_event")
public class AuditEventController implements AuditEventApi {

    @Resource(name = "auditEventServiceImpl")
    private AuditEventService auditEventService;

    @Override
    public Result<List<AuditEventVo>> selectList(AuditEventSelectDto selectDto) {
        return Result.success(auditEventService.selectList(selectDto));
    }

    @Override
    public Result<PageData<AuditEventVo>> selectPage(PageSelectListDto<AuditEventSelectDto> selectDto) {
        return Result.successPage(auditEventService.selectPage(selectDto));
    }
}
