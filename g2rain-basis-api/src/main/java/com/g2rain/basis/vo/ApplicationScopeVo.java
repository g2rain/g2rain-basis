package com.g2rain.basis.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 应用作用域返回VO
 * 关联表名: application
 * 功能：封装接口返回数据，隔离数据库实体与前端展示层
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@Schema(description = "应用作用域 VO")
public class ApplicationScopeVo {

    /**
     * 应用标识
     */
    @Schema(description = "应用标识")
    private Long id;

    /**
     * 机构标识
     */
    @Schema(description = "机构标识")
    private Long organId;

    /**
     * 应用编码
     */
    @Schema(description = "应用编码")
    private String applicationCode;
}
