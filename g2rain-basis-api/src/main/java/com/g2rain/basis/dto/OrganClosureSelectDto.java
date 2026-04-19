package com.g2rain.basis.dto;

import com.g2rain.common.model.BaseSelectListDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 机构路径关系表查询入参DTO
 * 用于OrganClosureDao.selectList方法的条件筛选
 * 表名: organ_closure
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "机构路径关系查询入参 DTO")
public class OrganClosureSelectDto extends BaseSelectListDto {

    /**
     * 祖先机构标识[上级]
     */
    @Schema(description = "祖先机构标识[上级]")
    private Long ancestorId;

    /**
     * 后代机构标识[下级]
     */
    @Schema(description = "后代机构标识[下级]")
    private Long descendantId;

    /**
     * 后代机构类型[服务商、渠道、公司、租户]
     */
    @Schema(description = "后代机构类型[服务商、渠道、公司、租户]", allowableValues = {"SERVICE_PROVIDER", "SALES_PARTNER", "COMPANY", "TENANT"})
    private String descendantType;

    /**
     * 关系类型[SELF_ASSOCIATION:自身关联, DIRECT_SUBORDINATE:直属, INDIRECT_SUBORDINATE:从属]
     */
    @Schema(description = "关系类型[SELF_ASSOCIATION:自身关联, DIRECT_SUBORDINATE:直属, INDIRECT_SUBORDINATE:从属]", allowableValues = {"SELF_ASSOCIATION", "DIRECT_SUBORDINATE", "INDIRECT_SUBORDINATE"})
    private String relationType;

    /**
     * 路径引用次数[用于DAG交叉挂载维护]
     */
    @Schema(description = "路径引用次数[用于DAG交叉挂载维护]")
    private Integer pathCount;
}
