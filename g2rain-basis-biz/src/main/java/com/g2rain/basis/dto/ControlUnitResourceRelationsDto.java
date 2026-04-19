package com.g2rain.basis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


/**
 * <p>权限点资源关联查询 DTO</p>
 *
 * <p>用于封装一组权限点（控制单元）与资源的关联关系，用于查询或更新操作。</p>
 *
 * <p><strong>数据库表:</strong> control_unit_resource_relation</p>
 *
 * <p><strong>注意：</strong>relations 列表不能为空，每一项都必须有效。</p>
 *
 * <p><em>Author:</em> Alpha</p>
 */
@Setter
@Getter
@NoArgsConstructor
@Schema(description = "权限点资源关联 DTO")
public class ControlUnitResourceRelationsDto {

    /**
     * 控制单元标识
     */
    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "控制单元标识")
    private Long controlUnitId;

    /**
     * <p>权限点与资源的关联关系列表</p>
     *
     * <p>每一项必须有效且列表不能为空。</p>
     */
    @Valid
    @Schema(description = "添加权限点与资源的关联关系列表")
    private List<ControlUnitResourceRelationItemDto> createRelations;

    /**
     * <p>权限点与资源的关联关系列表</p>
     *
     * <p>每一项必须有效且列表不能为空。</p>
     */
    @Valid
    @Schema(description = "修改权限点与资源的关联关系列表")
    private List<ControlUnitResourceRelationItemDto> updateRelations;

    /**
     * <p>权限点与资源的关联关系列表</p>
     *
     * <p>每一项必须有效且列表不能为空。</p>
     */
    @Valid
    @Schema(description = "删除权限点与资源的关联关系列表")
    private List<ControlUnitResourceRelationItemDto> deleteRelations;
}
