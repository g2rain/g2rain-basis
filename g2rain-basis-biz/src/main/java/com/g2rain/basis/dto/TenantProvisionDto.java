package com.g2rain.basis.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 租户账号开通所需信息 DTO
 *
 * <p>该 DTO 用于前端向后台传递开通账号所需的基础信息，
 * 供 {@link com.g2rain.basis.service.TenantProvisionService#provisionAccount(TenantProvisionDto)}
 * 接口使用。
 *
 * <p>填写完毕后，后台会自动完成：
 * <ul>
 *     <li>创建机构（如果新机构）</li>
 *     <li>创建用户并关联账号</li>
 *     <li>分配默认管理员角色及基础功能权限</li>
 * </ul>
 *
 * @author alpha
 * @since 2026/1/31
 */
@Setter
@Getter
@NoArgsConstructor
@Schema(description = "租户账号开通所需信息 DTO")
public class TenantProvisionDto {
    /**
     * 机构名称
     */
    @Schema(description = "机构名称")
    private String organName;

    /**
     * 机构类型[服务商、渠道、公司、租户]
     */
    @Schema(description = "机构类型[服务商、渠道、公司、租户]", allowableValues = {"SERVICE_PROVIDER", "SALES_PARTNER", "COMPANY", "TENANT"})
    private String organType;

    /**
     * 真实姓名
     */
    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "真实姓名")
    private String realName;

    /**
     * 邮箱地址
     */
    @Schema(description = "邮箱地址")
    private String email;

    /**
     * 手机号码
     */
    @Schema(description = "手机号码")
    private String mobile;
}
