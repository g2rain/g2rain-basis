package com.g2rain.basis.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

/**
 * <p>控制域与控制单元关系 DTO</p>
 *
 * <p>用于封装一组控制域与控制单元的关联关系数据。</p>
 *
 * <p><strong>注意：</strong>relations 列表不能为空，且列表中的每一项都需通过验证。</p>
 *
 * <p><em>Author:</em> alpha</p>
 * <p><em>Since:</em> 2026/1/18</p>
 */
@Setter
@Getter
@NoArgsConstructor
@Schema(description = "控制域与控制单元关系 DTO")
public class ControlDomainControlUnitRelationsDto {

    /**
     * 控制域标识
     */
    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "控制域标识")
    private Long controlDomainId;

    /**
     * 添加的控制域
     */
    @Schema(description = "添加的控制域")
    private Set<Long> controlUnitIds;

    /**
     * 删除的控制域
     */
    @Schema(description = "删除的控制域")
    private Set<Long> deleteControlUnitIds;
}
