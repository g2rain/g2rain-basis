package com.g2rain.basis.api;

import com.g2rain.basis.dto.UserRoleRelationSelectDto;
import com.g2rain.basis.vo.UserRoleRelationVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


/**
 * 用户角色关联表API接口
 * 表名: user_role_relation
 *
 * @author Alpha
 */
@Tag(name = "用户-角色关联", description = "用户角色关联相关接口")
public interface UserRoleRelationApi {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return 数据列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询用户-角色关联列表", description = "根据查询条件返回用户角色关联列表")
    Result<List<UserRoleRelationVo>> selectList(UserRoleRelationSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询用户-角色关联列表", description = "分页查询用户角色关联列表")
    Result<PageData<UserRoleRelationVo>> selectPage(PageSelectListDto<UserRoleRelationSelectDto> selectDto);
}
