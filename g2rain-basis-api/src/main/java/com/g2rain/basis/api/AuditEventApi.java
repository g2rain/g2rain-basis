package com.g2rain.basis.api;

import com.g2rain.basis.dto.AuditEventSelectDto;
import com.g2rain.basis.vo.AuditEventVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


/**
 * 审计事件对外查询接口（审计流水落库后的按条件检索与分页）。
 *
 * @author G2rain Generator
 */
@Tag(name = "审计事件", description = "审计流水查询：列表与分页")
public interface AuditEventApi {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return 数据列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询审计事件列表", description = "按条件筛选审计事件，不分页返回列表")
    Result<List<AuditEventVo>> selectList(AuditEventSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询审计事件", description = "按条件筛选审计事件并分页，含总数与当前页数据")
    Result<PageData<AuditEventVo>> selectPage(PageSelectListDto<AuditEventSelectDto> selectDto);
}
