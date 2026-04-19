package com.g2rain.basis.api;

import com.g2rain.basis.dto.UserSelectDto;
import com.g2rain.basis.vo.UserOptionVo;
import com.g2rain.basis.vo.UserVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


/**
 * 用户表API接口
 * 表名: user
 *
 * @author Alpha
 */
@Tag(name = "用户", description = "用户表相关接口")
public interface UserApi {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return 数据列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询用户列表", description = "根据查询条件返回用户列表")
    Result<List<UserVo>> selectList(UserSelectDto selectDto);

    /**
     * 获取用于下拉选择的用户列表
     *
     * @return 用户简要信息集合，包含用户 ID 和名称等用于展示的字段
     * @see UserOptionVo
     */
    @GetMapping("/user_options")
    @Operation(summary = "获取用户下拉选项", description = "返回用于下拉选择的用户简要信息集合")
    Result<List<UserOptionVo>> getUserOptions();

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询用户列表", description = "分页查询用户列表")
    Result<PageData<UserVo>> selectPage(PageSelectListDto<UserSelectDto> selectDto);
}
