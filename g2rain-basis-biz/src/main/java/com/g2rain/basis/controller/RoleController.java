package com.g2rain.basis.controller;

import com.g2rain.basis.api.RoleApi;
import com.g2rain.basis.dto.RoleDto;
import com.g2rain.basis.dto.RoleSelectDto;
import com.g2rain.basis.service.RoleService;
import com.g2rain.basis.vo.RoleVo;
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
 * 角色表控制器
 * 表名: role
 *
 * @author Alpha
 */
@RestController
@RequestMapping("/role")
public class RoleController implements RoleApi {

    @Resource(name = "roleServiceImpl")
    private RoleService roleService;

    /**
     * 查询角色列表（不分页）
     *
     * @param selectDto 查询条件 DTO
     * @return 角色视图对象列表
     */
    @Override
    public Result<List<RoleVo>> selectList(RoleSelectDto selectDto) {
        return Result.success(roleService.selectList(selectDto));
    }

    /**
     * 分页查询角色列表
     *
     * @param selectDto 分页参数及查询条件
     * @return 分页后的角色视图对象数据
     */
    @Override
    public Result<PageData<RoleVo>> selectPage(PageSelectListDto<RoleSelectDto> selectDto) {
        return Result.successPage(roleService.selectPage(selectDto));
    }

    /**
     * 新增或更新角色信息
     *
     * @param dto 角色数据传输对象（已校验）
     * @return 保存成功后的主键 ID
     */
    @PostMapping("/save")
    @Operation(summary = "新增或更新角色信息", description = "新增或更新角色基础信息")
    public Result<Long> save(@RequestBody @Validated RoleDto dto) {
        return Result.success(roleService.save(dto));
    }

    /**
     * 根据主键删除角色记录
     *
     * @param id 角色主键 ID
     * @return 受影响的记录行数
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除角色记录", description = "根据主键删除角色记录")
    public Result<Integer> delete(@Parameter(description = "角色标识") @PathVariable Long id) {
        return Result.success(roleService.delete(id));
    }
}
