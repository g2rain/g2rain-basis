package com.g2rain.basis.api;


import com.g2rain.basis.dto.OrganInviteGenerateDto;
import com.g2rain.basis.vo.OrganInviteVo;
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
 * 机构邀请码 API
 */
@Tag(name = "机构邀请码", description = "机构邀请码生成相关接口（Redis 存储，带有效期）")
public interface OrganInviteApi {

    /**
     * 为指定机构生成邀请码
     */
    @PostMapping("/generate")
    @Operation(
        summary = "生成机构邀请码",
        description = "为指定机构生成带有效期的邀请码，并绑定加入后分配的角色。"
            + "同一机构重新生成时，此前未使用的邀请码立即失效。"
            + "需机构管理员或平台运营权限。"
    )
    @ApiResponse(
        responseCode = "200",
        description = "生成成功，返回邀请码及过期时间",
        content = @Content(schema = @Schema(implementation = OrganInviteVo.class))
    )
    @RequestBody(
        description = "目标机构、分配角色及有效天数",
        required = true,
        content = @Content(schema = @Schema(implementation = OrganInviteGenerateDto.class))
    )
    Result<OrganInviteVo> generate(@org.springframework.web.bind.annotation.RequestBody @Validated OrganInviteGenerateDto dto);
}
