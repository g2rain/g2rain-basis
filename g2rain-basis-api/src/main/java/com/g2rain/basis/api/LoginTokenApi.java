package com.g2rain.basis.api;

import com.g2rain.basis.dto.LoginTokenDto;
import com.g2rain.basis.dto.LoginTokenSelectDto;
import com.g2rain.basis.vo.LoginTokenVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import com.g2rain.common.web.TokenJWTPayload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


/**
 * 登录信息表, 记录了当前登录状态的相关信息API接口
 * 表名: login_token
 *
 * @author Alpha
 */
@Tag(name = "登录令牌", description = "登录令牌相关接口")
public interface LoginTokenApi {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return 数据列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询登录令牌列表", description = "根据查询条件返回登录令牌列表")
    Result<List<LoginTokenVo>> selectList(LoginTokenSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询登录令牌列表", description = "分页查询登录令牌列表")
    Result<PageData<LoginTokenVo>> selectPage(PageSelectListDto<LoginTokenSelectDto> selectDto);

    @PostMapping("/{applicationCode}/save")
    @Operation(summary = "新增或更新登录令牌", hidden = true, description = "新增或更新登录状态令牌信息")
    Result<Long> save(@Parameter(description = "应用编码") @PathVariable String applicationCode, @RequestBody LoginTokenDto dto);

    /**
     * 获取指定用户在某应用下的登录令牌信息
     *
     * @param userId          用户ID，可选；如果为空则默认获取当前登录用户的令牌信息
     * @param applicationCode 应用编码，用于区分不同应用的令牌上下文
     * @return 返回对应用户在指定应用下的 JWT 载荷信息
     */
    @GetMapping("/token_context")
    @Operation(summary = "获取登录令牌上下文", hidden = true, description = "获取指定用户在某应用下的登录令牌 JWT 载荷信息")
    Result<TokenJWTPayload> fetchTokenContext(@Parameter(description = "用户标识") @RequestParam(required = false) Long userId, @Parameter(description = "应用编码") @RequestParam String applicationCode);
}
