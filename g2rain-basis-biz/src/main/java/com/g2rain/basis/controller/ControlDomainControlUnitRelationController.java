package com.g2rain.basis.controller;

import com.g2rain.basis.api.ControlDomainControlUnitRelationApi;
import com.g2rain.basis.dto.ControlDomainControlUnitRelationSelectDto;
import com.g2rain.basis.dto.ControlDomainControlUnitRelationsDto;
import com.g2rain.basis.service.ControlDomainControlUnitRelationService;
import com.g2rain.basis.vo.ControlDomainControlUnitRelationVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 控制域控制单元关联表控制器
 * 表名: control_domain_control_unit_relation
 *
 * @author Alpha
 */
@RestController
@RequestMapping("/control_domain_control_unit_relation")
public class ControlDomainControlUnitRelationController implements ControlDomainControlUnitRelationApi {

    @Resource(name = "controlDomainControlUnitRelationServiceImpl")
    private ControlDomainControlUnitRelationService controlDomainControlUnitRelationService;

    /**
     * 查询控制域-控制单元关联列表（不分页）
     *
     * @param selectDto 查询条件 DTO
     * @return 控制域-控制单元关联视图对象列表
     */
    @Override
    public Result<List<ControlDomainControlUnitRelationVo>> selectList(ControlDomainControlUnitRelationSelectDto selectDto) {
        return Result.success(controlDomainControlUnitRelationService.selectList(selectDto));
    }

    /**
     * 分页查询控制域-控制单元关联列表
     *
     * @param selectDto 分页参数及查询条件
     * @return 分页后的控制域-控制单元关联视图对象数据
     */
    @Override
    public Result<PageData<ControlDomainControlUnitRelationVo>> selectPage(PageSelectListDto<ControlDomainControlUnitRelationSelectDto> selectDto) {
        return Result.successPage(controlDomainControlUnitRelationService.selectPage(selectDto));
    }

    /**
     * 新增控制域-控制单元关联记录
     *
     * @param dto 控制域-控制单元关联数据传输对象（已校验，多条记录）
     * @return 受影响的记录行数
     */
    @PostMapping("/save")
    @Operation(summary = "新增控制域控制单元关联", description = "批量新增控制域与控制单元关联记录")
    public Result<Integer> save(@RequestBody @Validated ControlDomainControlUnitRelationsDto dto) {
        return Result.success(controlDomainControlUnitRelationService.save(dto));
    }
}
