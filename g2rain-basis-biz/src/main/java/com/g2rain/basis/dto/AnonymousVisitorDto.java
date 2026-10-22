package com.g2rain.basis.dto;

import com.g2rain.common.model.BaseDto;
import com.g2rain.common.validation.CreateGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 匿名访客表查询DTO
 * 表名: anonymous_visitor
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "匿名访客表 DTO")
public class AnonymousVisitorDto extends BaseDto {

    /**
     * 机构标识
     */
    @NotNull(groups = CreateGroup.class)
    @Schema(description = "机构标识（新增必填）")
    private Long organId;

    /**
     * DPoP 公钥指纹（base64url，区分大小写）
     */
    @NotBlank(groups = CreateGroup.class)
    @Size(max = 43)
    @Schema(description = "DPoP 公钥指纹（base64url，区分大小写）（新增必填）", maxLength = 43)
    private String keyThumbprint;

    /**
     * 首次进入时的接入码标识
     */
    @NotNull(groups = CreateGroup.class)
    @Schema(description = "首次进入时的接入码标识（新增必填）")
    private Long entryId;

    /**
     * 最近换票或刷新时间
     */
    @NotNull(groups = CreateGroup.class)
    @Schema(description = "最近换票或刷新时间（新增必填）")
    private LocalDateTime lastSeenTime;
}
