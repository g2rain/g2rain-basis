package com.g2rain.basis.model;


/**
 * <p>控制单元与资源的关联记录</p>
 *
 * <p>用于封装控制单元 ID 与资源的对应关系，包含资源 ID 和资源类型。</p>
 *
 * <p><em>Author:</em> alpha</p>
 * <p><em>Since:</em> 2026/1/18</p>
 */
public record ControlUnitPair(Long controlUnitId, Long resourceId, String resourceType) {
}
