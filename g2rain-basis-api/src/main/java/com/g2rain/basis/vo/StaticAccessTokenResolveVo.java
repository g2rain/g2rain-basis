package com.g2rain.basis.vo;

import com.g2rain.basis.enums.StaticTokenStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 个人静态访问令牌解析结果（供网关 {@code ApiKeyCache} 回源）。
 *
 * <p>语义约定：</p>
 * <ul>
 *     <li>接口返回 {@code Result.success(null)} — 数据库无此 Key（按 tokenHash 查询）。</li>
 *     <li>{@link #status} == {@link StaticTokenStatus#REVOKED} — 已吊销，{@link #context} 必须为 null。</li>
 *     <li>{@link #status} == {@link StaticTokenStatus#ACTIVATED} — 已启用，{@link #context} 携带用户/机构/应用维度。</li>
 * </ul>
 *
 * <p>网关据此映射为 invalid / revoked / active，不额外用错误码区分「不存在」与「不可用」。</p>
 */
@Setter
@Getter
@NoArgsConstructor
@Schema(description = "静态访问令牌解析结果")
public class StaticAccessTokenResolveVo {

    @Schema(description = "令牌状态")
    private StaticTokenStatus status;

    @Schema(description = "激活态会话上下文，吊销时为 null")
    private StaticAccessTokenContextVo context;
}
