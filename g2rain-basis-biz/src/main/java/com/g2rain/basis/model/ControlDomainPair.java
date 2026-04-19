package com.g2rain.basis.model;


/**
 * <p>控制域与控制单元的关联记录</p>
 *
 * <p>用于封装控制域 ID 与控制单元 ID 的对应关系。</p>
 *
 * <p><em>Author:</em> alpha</p>
 * <p><em>Since:</em> 2026/1/18</p>
 */
public record ControlDomainPair(Long controlDomainId, Long controlUnitId) {
}
