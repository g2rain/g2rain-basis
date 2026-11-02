package com.g2rain.basis.api;

import com.g2rain.basis.dto.PersonalStaticAccessTokenSelectDto;
import com.g2rain.basis.vo.PersonalStaticAccessTokenVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


/**
 * 个人静态访问令牌表API接口
 * 表名: personal_static_access_token
 *
 * @author G2rain Generator
 */
@Tag(name = "个人静态访问令牌", description = "个人静态访问令牌相关接口")
public interface PersonalStaticAccessTokenApi {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return 数据列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询个人静态访问令牌列表", description = "根据查询条件返回个人静态访问令牌列表")
    Result<List<PersonalStaticAccessTokenVo>> selectList(PersonalStaticAccessTokenSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询个人静态访问令牌列表", description = "分页查询个人静态访问令牌列表")
    Result<PageData<PersonalStaticAccessTokenVo>> selectPage(PageSelectListDto<PersonalStaticAccessTokenSelectDto> selectDto);

    /**
     * 根据条件统计数量
     *
     * @param selectDto 查询条件 DTO
     * @return 命中总数
     */
    @GetMapping("/count")
    @Operation(summary = "统计个人静态访问令牌数量", description = "根据查询条件统计个人静态访问令牌命中总数，不强制当前用户")
    Result<Long> selectCount(PersonalStaticAccessTokenSelectDto selectDto);

    /**
     * 查询本租户全部个人静态访问令牌列表（不强制当前用户）。
     * 权限由 Gateway 控制单元鉴权，服务端仅做租户数据隔离。
     *
     * @param selectDto 查询条件 DTO
     * @return 数据列表
     */
    @GetMapping("/list_all")
    @Operation(summary = "查询全部个人静态访问令牌列表", description = "按条件查询本租户全部个人静态访问令牌，不强制当前用户")
    Result<List<PersonalStaticAccessTokenVo>> selectListAll(PersonalStaticAccessTokenSelectDto selectDto);

    /**
     * 分页查询本租户全部个人静态访问令牌（不强制当前用户）。
     * 权限由 Gateway 控制单元鉴权，服务端仅做租户数据隔离。
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页数据
     */
    @GetMapping("/page_all")
    @Operation(summary = "分页查询全部个人静态访问令牌列表", description = "分页查询本租户全部个人静态访问令牌，不强制当前用户")
    Result<PageData<PersonalStaticAccessTokenVo>> selectPageAll(
        PageSelectListDto<PersonalStaticAccessTokenSelectDto> selectDto);
}
