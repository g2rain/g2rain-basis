package com.g2rain.basis.controller;

import com.g2rain.basis.api.ControlUnitResourceRelationApi;
import com.g2rain.basis.dto.ControlUnitResourceRelationSelectDto;
import com.g2rain.basis.dto.ControlUnitResourceRelationsDto;
import com.g2rain.basis.service.ControlUnitResourceRelationService;
import com.g2rain.basis.vo.ControlUnitResourceRelationVo;
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
 * 权限点资源关联表控制器
 * 表名: control_unit_resource_relation
 *
 * @author Alpha
 */
@RestController
@RequestMapping("/control_unit_resource_relation")
public class ControlUnitResourceRelationController implements ControlUnitResourceRelationApi {

    @Resource(name = "controlUnitResourceRelationServiceImpl")
    private ControlUnitResourceRelationService controlUnitResourceRelationService;

    /**
     * 查询控制单元-资源关联列表（不分页）
     *
     * @param selectDto 查询条件 DTO
     * @return 控制单元-资源关联视图对象列表
     */
    @Override
    public Result<List<ControlUnitResourceRelationVo>> selectList(ControlUnitResourceRelationSelectDto selectDto) {
        return Result.success(controlUnitResourceRelationService.selectList(selectDto));
    }

    /**
     * 分页查询控制单元-资源关联列表
     *
     * @param selectDto 分页参数及查询条件
     * @return 分页后的控制单元-资源关联视图对象数据
     */
    @Override
    public Result<PageData<ControlUnitResourceRelationVo>> selectPage(PageSelectListDto<ControlUnitResourceRelationSelectDto> selectDto) {
        return Result.successPage(controlUnitResourceRelationService.selectPage(selectDto));
    }

    /**
     * 新增控制单元-资源关联记录
     *
     * @param dto 控制单元-资源关联数据传输对象（已校验，多条记录）
     * @return 受影响的记录行数
     */
    @PostMapping("/save")
    @Operation(summary = "新增控制单元资源关联", description = "批量新增控制单元与资源关联记录")
    public Result<Integer> save(@RequestBody @Validated ControlUnitResourceRelationsDto dto) {
        return Result.success(controlUnitResourceRelationService.save(dto));
    }
}
