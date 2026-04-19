package com.g2rain.basis.dao.po;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 机构层级关系
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
public class OrganHierarchicalRelationPo {

    /**
     * 上级机构 ID
     */
    private Long parentId;

    /**
     * 机构 ID
     */
    private Long organId;

    /**
     * 机构名称
     */
    private String organName;

    /**
     * 后代机构类型[服务商、渠道、公司、租户]
     */
    private String organType;

    /**
     * 关系类型[SELF_ASSOCIATION:自身关联, DIRECT_SUBORDINATE:直属, INDIRECT_SUBORDINATE:从属]
     */
    private String relationType;
}
