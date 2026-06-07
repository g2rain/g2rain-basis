package com.g2rain.basis.enums;

import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.utils.Strings;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * IdP 接入形态，与表字段 {@code bind_mode} 存库值一致（枚举 {@link #name()}）。
 * <p>
 * 与「换票链路」的对应关系（非 OAuth 授权码/令牌两跳语义）：
 * </p>
 * <ul>
 *   <li>{@link #INTERNAL} — 企业内部应用形态下，钉钉企业内部应用换票与用户解析链路；</li>
 *   <li>{@link #THIRD_PARTY} — 第三方（ISV）企业应用形态下，钉钉第三方应用换票与用户解析链路。</li>
 * </ul>
 * <p>
 * 其他渠道（飞书、企业微信）若区分内部/第三方应用，复用本枚举与字段语义。
 * </p>
 *
 * @author G2rain
 */
@Schema(description = "IdP 接入形态（与 bind_mode 一致）")
public enum IdpBindMode {

    /**
     * 企业内部应用
     */
    @Schema(description = "企业内部应用")
    INTERNAL,

    /**
     * 第三方企业应用（ISV 等）
     */
    @Schema(description = "第三方企业应用")
    THIRD_PARTY;

    /**
     * 校验字符串是否为合法 {@link IdpBindMode} 名称（与存库值一致）。
     *
     * @param name 待校验值
     * @throws BusinessException 非空且不合法时抛出
     */
    public static void validate(String name) {
        if (Strings.isNotBlank(name)) {
            for (IdpBindMode mode : values()) {
                if (mode.name().equals(name)) {
                    return;
                }
            }
        }
        throw new BusinessException(SystemErrorCode.PARAM_VAL_INVALID, name);
    }
}
