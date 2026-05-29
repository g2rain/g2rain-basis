package com.g2rain.basis.api;


import com.g2rain.basis.dto.OrganInviteGenerateDto;
import com.g2rain.basis.vo.OrganInviteVo;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 机构邀请码 API
 */
@Tag(name = "机构邀请码", description = "机构邀请码生成相关接口")
public interface OrganInviteApi {

    /**
     * 为指定机构生成邀请码（Redis 存储，带有效期）
     */
    @PostMapping("/generate")
    @Operation(summary = "生成机构邀请码", description = "为指定机构生成带有效期的邀请码，并绑定加入后分配的角色")
    Result<OrganInviteVo> generate(@RequestBody @Validated OrganInviteGenerateDto dto);
}
