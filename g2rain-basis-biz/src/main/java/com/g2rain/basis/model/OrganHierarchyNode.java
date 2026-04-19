package com.g2rain.basis.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/**
 * <p>机构层级节点</p>
 *
 * <p>表示机构层级结构中的一个节点，包含子机构的 ID 和类型。</p>
 *
 * <p><strong>注意：</strong>该节点用于构建和表示组织架构的层级关系。</p>
 *
 * <p><em>Author:</em> alpha</p>
 * <p><em>Since:</em> 2025/12/8</p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrganHierarchyNode {

    /**
     * 后代机构标识[下级]
     */
    private Long descendantId;

    /**
     * 后代机构类型[服务商、渠道、公司、租户]
     */
    private String descendantType;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (Objects.isNull(o) || getClass() != o.getClass()) return false;
        OrganHierarchyNode that = (OrganHierarchyNode) o;
        return Objects.equals(descendantId, that.descendantId) &&
            Objects.equals(descendantType, that.descendantType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(descendantId, descendantType);
    }
}
