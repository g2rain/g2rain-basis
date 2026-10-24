package com.g2rain.basis.api;

import com.g2rain.basis.dto.UserSelectDto;
import com.g2rain.basis.vo.UserIdentityVo;
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
     * 当前账号在当前应用中可切换的用户身份
     *
     * <p>按当前 passportId 查名下用户，按换票同一规则过滤：机构可用；当前应用不是默认主应用时，
     * 机构须已开通该应用且用户有可用的控制单元。当前应用取自网关校验后的请求身份。</p>
     *
     * @return 用户身份集合
     * @see UserIdentityVo
     */
    @GetMapping("/identities")
    @Operation(summary = "查询可切换的用户身份", description = "返回当前账号在当前应用中可切换的用户身份")
    Result<List<UserIdentityVo>> selectIdentities();

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
