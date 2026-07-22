package com.g2rain.basis.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * IdP FULL 同步安全闸配置。
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "g2rain.basis.idp-sync.safety")
public class IdpSyncSafetyProperties {

    /**
     * 计划删除成员绑定数 / 现有绑定数 上限（超过则阻断 FULL 对账）。
     */
    private double maxMemberDeleteRatio = 0.3;

    /**
     * 计划停用部门映射数 / 现有部门映射数 上限。
     */
    private double maxDepartmentDisableRatio = 0.3;

    /**
     * 快照不完整时阻断 FULL 对账。
     */
    private boolean blockIncompleteSnapshot = true;

    /**
     * 快照为空且租户已有同步数据时阻断 FULL 对账。
     */
    private boolean blockEmptySnapshotWhenExisting = true;
}
