package com.g2rain.basis.enums;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 管理标记枚举。
 * <p>
 * 用于表示实体（用户或机构）是否具备管理身份或管理权限。
 * 值为 {@link #TRUE} 表示具备管理身份，{@link #FALSE} 表示不具备。
 * 可在多种场景中复用，例如用户管理员标记或运营公司标记。
 * </p>
 *
 * @author alpha
 * @since 2026/1/14
 */
@Getter
@Schema(description = "管理标记枚举(是否具备管理身份)")
public enum ManagerFlag {

    /**
     * 不具备管理身份
     */
    @Schema(description = "不具备管理身份")
    FALSE(false),

    /**
     * 具备管理身份
     */
    @Schema(description = "具备管理身份")
    TRUE(true);

    private final Boolean flag;

    ManagerFlag(Boolean flag) {
        this.flag = flag;
    }

    /**
     * 根据机构当前用户数量计算新用户的管理员标志。
     *
     * <p>如果机构已存在用户，则新用户不具备管理身份（返回 false），
     * 如果机构暂无用户，则新用户具备管理身份（返回 true）。</p>
     *
     * @param userCount 当前机构的用户数量
     * @return 新用户的 admin 字段值（true 表示管理员，false 表示非管理员）
     */
    public static boolean computeUserAdmin(long userCount) {
        return (userCount > 0 ? FALSE : TRUE).getFlag();
    }
}
