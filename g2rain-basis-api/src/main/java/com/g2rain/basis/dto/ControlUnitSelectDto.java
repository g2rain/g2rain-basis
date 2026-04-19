package com.g2rain.basis.dto;

import com.g2rain.common.model.BaseSelectListDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;


/**
 * 控制单元表查询入参DTO
 * 用于ControlUnitDao.selectList方法的条件筛选
 * 表名: control_unit
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "控制单元查询入参 DTO")
public class ControlUnitSelectDto extends BaseSelectListDto {

    /**
     * 应用标识
     */
    @Schema(description = "应用标识")
    private Long applicationId;

    /**
     * 控制单元名称
     */
    @Schema(description = "控制单元名称")
    private String controlUnitName;

    /**
     * 控制单元类型[OPERATION("运营功能"), CUSTOMER("客户功能"), PERPETUAL("永久有效功能")]
     */
    @Schema(description = "控制单元类型[OPERATION(\"运营功能\"), CUSTOMER(\"客户功能\"), PERPETUAL(\"永久有效功能\")]", allowableValues = {"OPERATION", "CUSTOMER", "PERPETUAL"})
    private String controlUnitScope;

    /**
     * 包含控制单元类型[OPERATION("运营功能"), CUSTOMER("客户功能"), PERPETUAL("永久有效功能")]
     */
    @Schema(description = "包含控制单元类型[OPERATION(\"运营功能\"), CUSTOMER(\"客户功能\"), PERPETUAL(\"永久有效功能\")]", allowableValues = {"OPERATION", "CUSTOMER", "PERPETUAL"})
    private Set<String> includeControlUnitScopes;

    /**
     * 默认控制单元
     */
    @Schema(description = "默认控制单元")
    private Boolean landing;

    /**
     * 控制单元状态
     */
    @Schema(description = "控制单元状态")
    private String status;

    /**
     * 业务说明
     */
    @Schema(description = "业务说明")
    private String description;
}
