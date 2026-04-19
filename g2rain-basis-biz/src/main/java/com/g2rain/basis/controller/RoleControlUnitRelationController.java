package com.g2rain.basis.controller;

import com.g2rain.basis.api.RoleControlUnitRelationApi;
import com.g2rain.basis.dto.RoleControlUnitRelationDto;
import com.g2rain.basis.dto.RoleControlUnitRelationSelectDto;
import com.g2rain.basis.service.RoleControlUnitRelationService;
import com.g2rain.basis.vo.RoleControlUnitRelationVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 角色控制单元关联表控制器
 * 表名: role_control_unit_relation
 *
 * @author Alpha
 */
@RestController
@RequestMapping("/role_control_unit_relation")
public class RoleControlUnitRelationController implements RoleControlUnitRelationApi {

    @Resource(name = "roleControlUnitRelationServiceImpl")
    private RoleControlUnitRelationService roleControlUnitRelationService;

    /**
     * 查询角色控制单元关联列表（不分页）
     *
     * @param selectDto 查询条件 DTO
     * @return 角色控制单元关联视图对象列表
     */
    @Override
    public Result<List<RoleControlUnitRelationVo>> selectList(RoleControlUnitRelationSelectDto selectDto) {
        return Result.success(roleControlUnitRelationService.selectList(selectDto));
    }

    /**
     * 分页查询角色控制单元关联列表
     *
     * @param selectDto 分页参数及查询条件
     * @return 分页后的角色控制单元关联视图对象数据
     */
    @Override
    public Result<PageData<RoleControlUnitRelationVo>> selectPage(PageSelectListDto<RoleControlUnitRelationSelectDto> selectDto) {
        return Result.successPage(roleControlUnitRelationService.selectPage(selectDto));
    }

    /**
     * 查询角色控制单元关联列表
     *
     * @param roleId 角色主键
     * @return 用户视图对象列表
     */
    @GetMapping("/role/{roleId}")
    @Operation(summary = "按角色查询控制单元关联", description = "根据角色主键查询角色控制单元关联列表")
    public Result<List<RoleControlUnitRelationVo>> selectByRole(@Parameter(description = "角色标识") @PathVariable Long roleId) {
        return Result.success(roleControlUnitRelationService.selectByRole(roleId));
    }

    /**
     * 新增角色与控制单元关联记录
     *
     * @param dto 角色控制单元关联数据传输对象（已校验）
     * @return 受影响的记录行数
     */

    @PostMapping("/save")
    @Operation(summary = "新增角色控制单元关联", description = "新增角色与控制单元的关联记录")
    public Result<Integer> save(@RequestBody @Validated RoleControlUnitRelationDto dto) {
        return Result.success(roleControlUnitRelationService.save(dto));
    }
}
