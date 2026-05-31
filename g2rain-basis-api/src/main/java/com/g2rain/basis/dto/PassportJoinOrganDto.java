package com.g2rain.basis.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 通行证通过邀请码加入机构请求 DTO
 */
@Setter
@Getter
@NoArgsConstructor
@Schema(name = "PassportJoinOrganDto", description = "通行证通过邀请码加入机构请求")
public class PassportJoinOrganDto {

    @NotBlank
    @Schema(
        description = "机构邀请码（32 位十六进制字符串，无横线）",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "a1b2c3d4e5f6789012345678abcdef01",
        minLength = 32,
        maxLength = 64
    )
    private String inviteCode;
}
