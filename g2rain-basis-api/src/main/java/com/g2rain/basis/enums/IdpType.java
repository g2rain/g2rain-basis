package com.g2rain.basis.enums;

import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.utils.Strings;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 身份源（IdP）渠道类型，与表字段 {@code idp_type} 存库值一致（枚举 {@link #name()}）。
 * <p>
 * IAM 为独立进程，通过 REST/Feign 调用 Basis 写入绑定与企业-机构映射；当前迭代 IAM 侧仅实现钉钉，
 * 飞书、企业微信为预留渠道，Basis 表结构与枚举已对齐，便于后续扩展。
 * </p>
 *
 * @author G2rain
 */
@Schema(description = "身份源渠道类型（与 idp_type 一致）")
public enum IdpType {

    /**
     * 钉钉（本阶段 IAM 实现）
     */
    @Schema(description = "钉钉")
    DINGTALK,

    /**
     * 飞书（预留）
     */
    @Schema(description = "飞书")
    FEISHU,

    /**
     * 企业微信（预留）
     */
    @Schema(description = "企业微信")
    WECHAT_WORK;

    /**
     * 校验字符串是否为合法 {@link IdpType} 名称（与存库值一致）。
     *
     * @param name 待校验值
     * @throws BusinessException 非空且不合法时抛出
     */
    public static void validate(String name) {
        if (Strings.isNotBlank(name)) {
            for (IdpType type : values()) {
                if (type.name().equals(name)) {
                    return;
                }
            }
        }
        throw new BusinessException(SystemErrorCode.PARAM_VAL_INVALID, name);
    }
}
