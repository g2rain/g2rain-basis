package com.g2rain.basis.enums;


import com.g2rain.common.utils.Strings;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 机构关联类型枚举。
 * <p>
 * 用于表示机构之间的关系类型，包括直属、从属以及自身关联。
 * 提供工具方法判断是否为非直属关系。
 * </p>
 *
 * <ul>
 *   <li>{@link #DIRECT_SUBORDINATE}：直属关系，直接上级或下级。</li>
 *   <li>{@link #INDIRECT_SUBORDINATE}：从属关系，间接上下级关系。</li>
 *   <li>{@link #SELF_ASSOCIATION}：自身关联，机构自关联的特殊关系。</li>
 * </ul>
 *
 * @author alpha
 * @since 2025/11/4
 */
@Schema(description = "机构关联类型枚举")
public enum OrganRelation {
    /**
     * 直属
     */
    @Schema(description = "直属关系(直接上下级)")
    DIRECT_SUBORDINATE,

    /**
     * 从属
     */
    @Schema(description = "从属关系(间接上下级)")
    INDIRECT_SUBORDINATE,

    /**
     * 自身关联
     */
    @Schema(description = "自身关联(自关联的特殊关系)")
    SELF_ASSOCIATION;

    /**
     * 判断给定的关系字符串是否为非直属关系。
     * <p>
     * 如果 {@code relation} 为空或不等于 {@link #DIRECT_SUBORDINATE}，则返回 {@code true}；
     * 否则返回 {@code false}。
     * </p>
     *
     * @param relation 待判断的机构关联关系名称
     * @return {@code true} 表示非直属关系，{@code false} 表示直属关系
     */
    public static boolean nonDirectSubordinate(String relation) {
        return !directSubordinate(relation);
    }

    /**
     * 判断给定的关系字符串是否为直属关系。
     * <p>
     * 如果 {@code relation} 不为空并且等于 {@link #DIRECT_SUBORDINATE}，则返回 {@code true}；
     * 否则返回 {@code false}。
     * </p>
     *
     * @param relation 待判断的机构关联关系名称
     * @return {@code true} 表示直属关系，{@code false} 表示非直属关系
     */
    public static boolean directSubordinate(String relation) {
        if (Strings.isBlank(relation)) {
            return false;
        }

        return DIRECT_SUBORDINATE.name().equals(relation);
    }

    /**
     * 判断给定的关系字符串是否自关联关系。
     * <p>
     * 如果 {@code relation} 不为空空并且等于 {@link #SELF_ASSOCIATION}，则返回 {@code true}；
     * 否则返回 {@code false}。
     * </p>
     *
     * @param relation 待判断的机构关联关系名称
     * @return {@code true} 表示自关联关系，{@code false} 表示非自关联关系
     */
    public static boolean selfAssociation(String relation) {
        if (Strings.isBlank(relation)) {
            return false;
        }

        return SELF_ASSOCIATION.name().equals(relation);
    }
}
