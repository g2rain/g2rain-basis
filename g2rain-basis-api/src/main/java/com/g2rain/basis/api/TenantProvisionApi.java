package com.g2rain.basis.api;


import com.g2rain.basis.dto.PassportJoinOrganDto;
import com.g2rain.basis.vo.UserVo;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 通行证租户入驻 API（邀请码加入机构）
 */
@Tag(name = "通行证租户入驻", description = "已登录通行证通过邀请码加入已有机构")
public interface TenantProvisionApi {

    /**
     * 通过邀请码加入已有机构
     */
    @PostMapping("/join_organ")
    @Operation(
        summary = "加入机构",
        description = "已登录通行证提交机构邀请码，在目标机构下创建用户并绑定邀请码指定的角色。"
            + "若当前通行证已在该机构存在用户，则幂等返回已有用户且不消耗邀请码。"
            + "邀请码存储于 Redis，过期或使用后失效。"
    )
    @ApiResponse(
        responseCode = "200",
        description = "加入成功，返回新创建或已存在的用户信息",
        content = @Content(schema = @Schema(implementation = UserVo.class))
    )
    @RequestBody(
        description = "机构邀请码",
        required = true,
        content = @Content(schema = @Schema(implementation = PassportJoinOrganDto.class))
    )
    Result<UserVo> joinOrgan(@org.springframework.web.bind.annotation.RequestBody @Validated PassportJoinOrganDto dto);
}
