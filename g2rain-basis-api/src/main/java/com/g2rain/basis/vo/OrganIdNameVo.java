package com.g2rain.basis.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 机构 ID 与名称的值对象（VO）。
 * 用于在前后端交互或业务逻辑中传递机构的基本信息。
 *
 * @author alpha
 * @since 2026/1/20
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "机构名称 VO")
public class OrganIdNameVo {

    /**
     * 机构 ID
     */
    @Schema(description = "机构标识")
    private Long organId;

    /**
     * 机构名称
     */
    @Schema(description = "机构名称")
    private String organName;
}
