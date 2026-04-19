package com.g2rain.basis.api;

import com.g2rain.basis.dto.RoleSelectDto;
import com.g2rain.basis.vo.RoleVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


/**
 * 角色表API接口
 * 表名: role
 *
 * @author Alpha
 */
@Tag(name = "角色", description = "角色表相关接口")
public interface RoleApi {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return 数据列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询角色列表", description = "根据查询条件返回角色列表")
    Result<List<RoleVo>> selectList(RoleSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询角色列表", description = "分页查询角色列表")
    Result<PageData<RoleVo>> selectPage(PageSelectListDto<RoleSelectDto> selectDto);
}
