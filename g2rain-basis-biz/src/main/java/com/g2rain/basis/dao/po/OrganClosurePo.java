package com.g2rain.basis.dao.po;

import com.g2rain.common.model.BasePo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 机构路径关系表返回Po
 * 关联表名: organ_closure
 * 功能：封装实体数据，继承BasePo复用基础字段逻辑
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrganClosurePo extends BasePo {

    /**
     * 祖先机构标识[上级]
     */
    private Long ancestorId;

    /**
     * 后代机构标识[下级]
     */
    private Long descendantId;

    /**
     * 后代机构类型[服务商、渠道、公司、租户]
     */
    private String descendantType;

    /**
     * 关系类型[SELF_ASSOCIATION:自身关联, DIRECT_SUBORDINATE:直属, INDIRECT_SUBORDINATE:从属]
     */
    private String relationType;

    /**
     * 路径引用次数[用于DAG交叉挂载维护]
     */
    private Integer pathCount;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    private Boolean deleteFlag;
}
