package com.g2rain.basis.enums;

import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.utils.Strings;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 企业型身份源（IdP）渠道类型，与表字段 {@code idp_type} 存库值一致（枚举 {@link #name()}）。
 * <p>
 * 枚举内各渠道均支持 {@code idp_enterprise_organ}（外部企业/租户 ↔ organ）与
 * {@code passport_idp_binding}（Passport ↔ IdP 主体）。{@code /passport_idp_binding/bind} 会同步处理二者；
 * 仅需落 Passport 绑定时走 {@code /passport_idp_binding/save}，不经过本枚举校验。
 * </p>
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
     * 是否为企业型 IdP：bind 时需携带外部企业/租户标识并同步建立 {@code idp_enterprise_organ}。
     * 枚举内各常量均为 {@code true}（corpId / tenant_key 等映射至 {@code corp_id}）。
     */
    public boolean requiresEnterpriseId() {
        return true;
    }

    public static IdpType nameOf( String name) {
        for(IdpType type : values()) {
            if(type.name().equals(name)) {
                return type;
            }
        }
        return null;
    }
}
