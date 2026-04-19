package com.g2rain.basis.controller;

import com.g2rain.basis.api.ControlDomainApi;
import com.g2rain.basis.dto.ControlDomainDto;
import com.g2rain.basis.dto.ControlDomainSelectDto;
import com.g2rain.basis.service.ControlDomainService;
import com.g2rain.basis.vo.ControlDomainVo;
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
 * 控制域表控制器
 * 表名: control_domain
 *
 * @author Alpha
 */
@RestController
@RequestMapping("/control_domain")
public class ControlDomainController implements ControlDomainApi {

    @Resource(name = "controlDomainServiceImpl")
    private ControlDomainService controlDomainService;

    /**
     * 查询控制域列表（不分页）
     *
     * @param selectDto 查询条件 DTO
     * @return 控制域视图对象列表
     */
    @Override
    public Result<List<ControlDomainVo>> selectList(ControlDomainSelectDto selectDto) {
        return Result.success(controlDomainService.selectList(selectDto));
    }

    /**
     * 分页查询控制域列表
     *
     * @param selectDto 分页参数及查询条件
     * @return 分页后的控制域视图对象数据
     */
    @Override
    public Result<PageData<ControlDomainVo>> selectPage(PageSelectListDto<ControlDomainSelectDto> selectDto) {
        return Result.successPage(controlDomainService.selectPage(selectDto));
    }

    /**
     * 新增或更新控制域信息
     *
     * @param dto 控制域数据传输对象（已校验）
     * @return 保存成功后的主键 ID
     */
    @PostMapping("/save")
    @Operation(summary = "新增或更新控制域信息", description = "新增或更新控制域基础信息")
    public Result<Long> save(@RequestBody @Validated ControlDomainDto dto) {
        return Result.success(controlDomainService.save(dto));
    }

    /**
     * 根据主键删除控制域记录
     *
     * @param id 控制域主键 ID
     * @return 受影响的记录行数
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除控制域记录", description = "根据主键删除控制域记录")
    public Result<Integer> delete(@Parameter(description = "控制域标识") @PathVariable Long id) {
        return Result.success(controlDomainService.delete(id));
    }
}
