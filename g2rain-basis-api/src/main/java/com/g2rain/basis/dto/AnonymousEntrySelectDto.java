package com.g2rain.basis.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.g2rain.common.model.BaseSelectListDto;
import io.swagger.v3.oas.annotations.media.Schema;


/**
 * 匿名接入码表查询入参DTO
 * 用于AnonymousEntryDao.selectList方法的条件筛选
 * 表名: anonymous_entry
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "匿名接入码表查询入参 DTO")
public class AnonymousEntrySelectDto extends BaseSelectListDto {

    /**
     * 机构标识（租户机构）
     */
    @Schema(description = "机构标识（租户机构）")
    private Long organId;

    /**
     * 应用标识
     */
    @Schema(description = "应用标识")
    private Long applicationId;

    /**
     * 接入码名称
     */
    @Schema(description = "接入码名称")
    private String entryName;

    /**
     * 接入码（ek_ 前缀 + base62 随机串，区分大小写）
     */
    @Schema(description = "接入码（ek_ 前缀 + base62 随机串，区分大小写）")
    private String entryKey;

    /**
     * 接入码状态[ACTIVE:有效, INACTIVE:无效]
     */
    @Schema(description = "接入码状态[ACTIVE:有效, INACTIVE:无效]")
    private String status;
}