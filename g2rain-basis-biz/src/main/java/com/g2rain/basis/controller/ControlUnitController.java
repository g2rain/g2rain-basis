package com.g2rain.basis.controller;

import com.g2rain.basis.api.ControlUnitApi;
import com.g2rain.basis.dto.ControlUnitDto;
import com.g2rain.basis.dto.ControlUnitSelectDto;
import com.g2rain.basis.dto.UpdateStatusDto;
import com.g2rain.basis.service.ControlUnitService;
import com.g2rain.basis.vo.ControlUnitVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 控制单元表控制器
 * 表名: control_unit
 *
 * @author Alpha
 */
@RestController
@RequestMapping("/control_unit")
public class ControlUnitController implements ControlUnitApi {

    @Resource(name = "controlUnitServiceImpl")
    private ControlUnitService controlUnitService;

    /**
     * 查询控制单元列表（不分页）
     *
     * @param selectDto 查询条件 DTO
     * @return 控制单元视图对象列表
     */
    @Override
    public Result<List<ControlUnitVo>> selectList(ControlUnitSelectDto selectDto) {
        return Result.success(controlUnitService.selectList(selectDto));
    }

    /**
     * 分页查询控制单元列表
     *
     * @param selectDto 分页参数及查询条件
     * @return 分页后的控制单元视图对象数据
     */
    @Override
    public Result<PageData<ControlUnitVo>> selectPage(PageSelectListDto<ControlUnitSelectDto> selectDto) {
        return Result.successPage(controlUnitService.selectPage(selectDto));
    }

    /**
     * 新增或更新控制单元信息
     *
     * @param dto 控制单元数据传输对象（已校验）
     * @return 保存成功后的主键 ID
     */
    @PostMapping("/save")
    @Operation(summary = "新增或更新控制单元", description = "新增或更新控制单元基础信息")
    public Result<Long> save(@RequestBody @Validated ControlUnitDto dto) {
        return Result.success(controlUnitService.save(dto));
    }

    /**
     * 根据主键删除控制单元记录
     *
     * @param id 控制单元主键 ID
     * @return 受影响的记录行数
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除控制单元记录", description = "根据主键删除控制单元记录")
    public Result<Integer> delete(@Parameter(description = "控制单元标识") @PathVariable Long id) {
        return Result.success(controlUnitService.delete(id));
    }

    /**
     * 修改控制单元状态
     *
     * @param id 控制单元主键 ID
     * @return 修改后记录数量
     */
    @PostMapping("/{id}/status")
    @Operation(summary = "更新控制单元状态", description = "根据主键更新控制单元启用状态")
    public Result<Integer> updateStatus(@Parameter(description = "控制单元标识") @PathVariable Long id, @RequestBody @Validated UpdateStatusDto dto) {
        return Result.success(controlUnitService.updateStatus(id, dto));
    }

}
