package com.g2rain.basis.enums;

/**
 * basis 侧 cache-sync 数据源标识。
 *
 * <p>
 * 与网关 {@code SyncerEnum} 名称保持一致，经消息总线通知各网关节点失效本地缓存。
 * 各枚举常量的载荷类型由消费方（网关侧缓存同步实现）约定。
 * </p>
 *
 * @author alpha
 * @since 2026/5/22
 */
public enum BasisSyncerEnum {
    ORGAN_NAME,
    APP_NAME,
    ORGAN_HIERARCHY,
    INTERNAL_ROUTE,
    API_ROUTE,
    PASSPORT_PERM,
    USER_PERM,
    /**
     * 个人静态访问令牌：载荷为 {@code tokenHash}（原始 API Key 的 SHA-256 十六进制），
     * 在令牌吊销或删除时推送，驱动网关 {@code ApiKeyCache} 失效。
     */
    STATIC_ACCESS_TOKEN
}
