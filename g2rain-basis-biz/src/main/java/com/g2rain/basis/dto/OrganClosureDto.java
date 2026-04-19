package com.g2rain.basis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 机构路径关系 DTO
 * 表名: organ_closure
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@Schema(description = "机构路径关系 DTO")
public class OrganClosureDto {

    /**
     * 目标祖先机构标识[上级]
     */
    @Schema(description = "目标祖先机构标识[上级]")
    private Long targetAncestorId;

    /**
     * 原始祖先机构标识[上级]
     */
    @Schema(description = "原始祖先机构标识[上级]")
    private Long sourceAncestorId;
}
