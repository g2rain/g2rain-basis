package com.g2rain.basis.vo;

import com.g2rain.common.json.AdminCompanyCondition;
import com.g2rain.common.json.ConditionalJsonIgnore;
import com.g2rain.common.model.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 机构路径关系表返回VO
 * 关联表名: organ_closure
 * 功能：封装接口返回数据，继承BaseVo复用基础字段逻辑，隔离数据库实体与前端展示层
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "机构路径关系 VO")
public class OrganClosureVo extends BaseVo {

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
    @Schema(description = "后代机构类型[服务商、渠道、公司、租户]")
    private String descendantType;

    /**
     * 关系类型[SELF_ASSOCIATION:自身关联, DIRECT_SUBORDINATE:直属, INDIRECT_SUBORDINATE:从属]
     */
    @Schema(description = "关系类型[SELF_ASSOCIATION:自身关联, DIRECT_SUBORDINATE:直属, INDIRECT_SUBORDINATE:从属]")
    private String relationType;

    /**
     * 路径引用次数[用于DAG交叉挂载维护]
     */
    @Schema(description = "路径引用次数[用于DAG交叉挂载维护]")
    private Integer pathCount;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    @ConditionalJsonIgnore(adminCompany = AdminCompanyCondition.TRUE)
    @Schema(description = "删除标识[0:未删除, 1:已删除]")
    private Boolean deleteFlag;
}
