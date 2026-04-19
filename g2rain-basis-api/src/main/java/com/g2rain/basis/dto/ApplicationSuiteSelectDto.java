package com.g2rain.basis.dto;

import com.g2rain.common.model.BaseSelectListDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;


/**
 * 应用归类关系表查询入参DTO
 * 用于ApplicationSuiteDao.selectList方法的条件筛选
 * 表名: application_suite
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "应用归类关系查询入参 DTO")
public class ApplicationSuiteSelectDto extends BaseSelectListDto {

    /**
     * 应用标识
     */
    @Schema(description = "应用标识")
    private Long applicationId;

    /**
     * 主应用标识
     */
    @Schema(description = "主应用标识")
    private Long masterApplicationId;

    /**
     * 主应用标识
     */
    @Schema(description = "主应用标识")
    private Set<Long> masterApplicationIds;
}
