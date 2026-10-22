package com.g2rain.basis.dto;

import com.g2rain.common.model.BaseSelectListDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 匿名访客表查询入参DTO
 * 用于AnonymousVisitorDao.selectList方法的条件筛选
 * 表名: anonymous_visitor
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "匿名访客表查询入参 DTO")
public class AnonymousVisitorSelectDto extends BaseSelectListDto {

    /**
     * 机构标识
     */
    @Schema(description = "机构标识")
    private Long organId;

    /**
     * DPoP 公钥指纹（base64url，区分大小写）
     */
    @Schema(description = "DPoP 公钥指纹（base64url，区分大小写）")
    private String keyThumbprint;

    /**
     * 首次进入时的接入码标识
     */
    @Schema(description = "首次进入时的接入码标识")
    private Long entryId;

    /**
     * 最近换票或刷新时间
     */
    @Schema(description = "最近换票或刷新时间")
    private LocalDateTime lastSeenTime;
}
