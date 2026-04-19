package com.g2rain.basis.vo;


import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 机构层级关系值对象（VO）。
 * <p>
 * 用于表示机构及其子机构的树形层级结构，每个实例可包含零个或多个子机构，实现递归嵌套。
 * 适用于前后端数据传输或构建机构树形结构。
 * </p>
 *
 * @author alpha
 * @since 2026/1/20
 */
@Setter
@Getter
@NoArgsConstructor
// 只根据 organId 判断是否是同一个机构
@EqualsAndHashCode(of = "organId")
@Schema(description = "机构层级关系 VO")
public class OrganHierarchicalRelationVo {

    /**
     * 机构 ID
     */
    @Schema(description = "机构 ID")
    private Long organId;

    /**
     * 机构名称
     */
    @Schema(description = "机构名称")
    private String organName;

    /**
     * 子机构集合
     */
    @ArraySchema(
        arraySchema = @Schema(description = "子机构集合"),
        schema = @Schema(description = "子机构")
    )
    private List<OrganHierarchicalRelationVo> subOrgans = new ArrayList<>();

    public OrganHierarchicalRelationVo(Long organId, String organName) {
        this.organId = organId;
        this.organName = organName;
    }
}
