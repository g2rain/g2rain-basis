package com.g2rain.basis.controller;

import com.g2rain.basis.api.UserApi;
import com.g2rain.basis.dto.UserDto;
import com.g2rain.basis.dto.UserSelectDto;
import com.g2rain.basis.service.UserService;
import com.g2rain.basis.vo.UserOptionVo;
import com.g2rain.basis.vo.UserVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户表控制器
 * 表名: user
 *
 * @author Alpha
 */
@RestController
@RequestMapping("/user")
public class UserController implements UserApi {

    @Resource(name = "userServiceImpl")
    private UserService userService;

    /**
     * 查询用户列表（不分页）
     *
     * @param selectDto 查询条件 DTO
     * @return 用户视图对象列表
     */
    @Override
    public Result<List<UserVo>> selectList(UserSelectDto selectDto) {
        return Result.success(userService.selectList(selectDto));
    }

    /**
     * 获取用于下拉选择的用户列表
     *
     * @return 用户简要信息集合，包含用户 ID 和名称等用于展示的字段
     * @see UserOptionVo
     */
    @Override
    public Result<List<UserOptionVo>> getUserOptions() {
        return Result.success(userService.getUserOptions());
    }

    /**
     * 分页查询用户列表
     *
     * @param selectDto 分页参数及查询条件
     * @return 分页后的用户视图对象数据
     */
    @Override
    public Result<PageData<UserVo>> selectPage(PageSelectListDto<UserSelectDto> selectDto) {
        return Result.successPage(userService.selectPage(selectDto));
    }

    /**
     * 查询用户列表
     *
     * @param roleId 角色主键
     * @return 用户视图对象列表
     */
    @GetMapping("/role/{roleId}")
    @Operation(summary = "按角色查询用户列表", description = "根据角色主键查询已关联用户列表")
    public Result<List<UserVo>> selectByRole(@Parameter(description = "角色标识") @PathVariable Long roleId) {
        return Result.success(userService.selectByRole(roleId));
    }

    /**
     * 新增或更新用户信息
     *
     * @param dto 用户数据传输对象（已校验）
     * @return 保存成功后的主键 ID
     */
    @PostMapping("/save")
    @Operation(summary = "新增或更新用户信息", description = "新增或更新用户基础信息")
    public Result<Long> save(@RequestBody @Validated UserDto dto) {
        return Result.success(userService.save(dto));
    }

    /**
     * 根据主键删除用户记录
     *
     * @param id 用户主键 ID
     * @return 受影响的记录行数
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户记录", description = "根据主键删除用户记录")
    public Result<Integer> delete(@Parameter(description = "用户标识") @PathVariable Long id) {
        return Result.success(userService.delete(id));
    }
}
