package com.g2rain.basis.controller;

import com.g2rain.basis.api.UserRoleRelationApi;
import com.g2rain.basis.dto.RoleAssignUsersDto;
import com.g2rain.basis.dto.UserRoleRelationDto;
import com.g2rain.basis.dto.UserRoleRelationSelectDto;
import com.g2rain.basis.service.UserRoleRelationService;
import com.g2rain.basis.vo.UserRoleRelationVo;
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
 * 用户角色关联表控制器
 * 表名: user_role_relation
 *
 * @author Alpha
 */
@RestController
@RequestMapping("/user_role_relation")
public class UserRoleRelationController implements UserRoleRelationApi {

    @Resource(name = "userRoleRelationServiceImpl")
    private UserRoleRelationService userRoleRelationService;

    /**
     * 查询用户角色关联列表（不分页）
     *
     * @param selectDto 查询条件 DTO
     * @return 用户角色关联视图对象列表
     */
    @Override
    public Result<List<UserRoleRelationVo>> selectList(UserRoleRelationSelectDto selectDto) {
        return Result.success(userRoleRelationService.selectList(selectDto));
    }

    /**
     * 分页查询用户角色关联列表
     *
     * @param selectDto 分页参数及查询条件
     * @return 分页后的用户角色关联视图对象数据
     */
    @Override
    public Result<PageData<UserRoleRelationVo>> selectPage(PageSelectListDto<UserRoleRelationSelectDto> selectDto) {
        return Result.successPage(userRoleRelationService.selectPage(selectDto));
    }

    /**
     * 新增或更新用户角色关联
     *
     * @param dto 用户角色关联数据传输对象（已校验）
     * @return 保存成功后的主键 ID
     */
    @PostMapping("/save")
    @Operation(summary = "新增或更新用户角色关联", description = "新增或更新用户角色关联记录")
    public Result<Long> save(@RequestBody @Validated UserRoleRelationDto dto) {
        return Result.success(userRoleRelationService.save(dto));
    }

    /**
     * 新增或更新用户角色关联
     *
     * @param dto 用户角色关联数据传输对象（已校验）
     * @return 保存成功后的主键 ID
     */
    @PostMapping("/assign_users")
    @Operation(summary = "为角色分配用户", description = "批量为指定角色分配用户关联关系")
    public Result<Integer> assignUsers(@RequestBody @Validated RoleAssignUsersDto dto) {
        return Result.success(userRoleRelationService.assignUsers(dto));
    }
}
