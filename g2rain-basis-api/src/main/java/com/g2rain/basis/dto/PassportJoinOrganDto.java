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
@Schema(description = "通行证通过邀请码加入机构请求 DTO")
public class PassportJoinOrganDto {

    @NotBlank
    @Schema(description = "机构邀请码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String inviteCode;
}
