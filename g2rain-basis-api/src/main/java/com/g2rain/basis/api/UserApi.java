package com.g2rain.basis.api;

import com.g2rain.basis.dto.UserSelectDto;
import com.g2rain.basis.vo.UserIdentityVo;
import com.g2rain.basis.vo.UserVo;
import com.g2rain.common.enums.OrganType;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
     * 当前账号可切换的用户身份
     *
     * <p>按当前 passportId 查名下用户，只保留机构为 ACTIVE 的身份；能否以当前应用进入由换票校验。
     * organType 由 shell 的 nginx 转发时固定写入，客户端传值会被丢弃。</p>
     *
     * @param organType 机构类型；为空时不按类型过滤
     * @return 用户身份集合
     * @see UserIdentityVo
     */
    @GetMapping("/identities")
    @Operation(summary = "查询可切换的用户身份", description = "返回当前账号名下机构可用的用户身份，可按机构类型过滤")
    Result<List<UserIdentityVo>> selectIdentities(
        @Parameter(description = "机构类型") @RequestParam(required = false) OrganType organType
    );

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
