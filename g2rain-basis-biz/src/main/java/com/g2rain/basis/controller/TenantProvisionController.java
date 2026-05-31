package com.g2rain.basis.controller;


import com.g2rain.basis.api.TenantProvisionApi;
import com.g2rain.basis.dto.PassportJoinOrganDto;
import com.g2rain.basis.dto.TenantProvisionDto;
import com.g2rain.basis.service.TenantProvisionService;
import com.g2rain.basis.vo.UserVo;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 租户账号开通接口控制器
 *
 * <p>该控制器提供租户账号开通相关的 REST 接口，主要负责接收前端请求并调用
 * {@link TenantProvisionService} 完成账号及机构开通操作。
 *
 * <p>接口说明：
 * <ul>
 *     <li>POST /tenant_provision/provision_account - 为指定租户开通账号最小功能</li>
 * </ul>
 *
 * <p>返回值使用统一结果包装 {@link Result}，包含创建成功标记或失败信息。
 * 事务和业务逻辑均由 Service 层保证。
 * </p>
 *
 * @author alpha
 * @since 2026/1/30
 */
@RestController
@RequestMapping("/tenant_provision")
@Tag(name = "租户初始化", description = "租户初始化相关接口")
public class TenantProvisionController implements TenantProvisionApi {

    @Resource(name = "tenantProvisionServiceImpl")
    private TenantProvisionService tenantProvisionService;

    /**
     * 为账号在指定租户下开通最小可用功能。
     *
     * <p>请求参数：
     * <ul>
     *     <li>{@link TenantProvisionDto} - 租户开通账号所需信息，包括机构信息和用户信息</li>
     * </ul>
     *
     * <p>返回值：
     * <ul>
     *     <li>{@link Result<Long>} - 成功标记 1:成功; 0:失败</li>
     * </ul>
     *
     * <p>该接口直接调用 Service 层方法，业务逻辑和事务由 Service 层保证。
     *
     * @param dto 租户开通账号信息
     * @return 包装在 Result 中的操作结果
     */
    @PostMapping("/provision_account")
    @Operation(summary = "开通租户账号", description = "为指定租户开通账号并初始化最小可用功能")
    public Result<UserVo> provisionAccount(@RequestBody TenantProvisionDto dto) {
        return Result.success(tenantProvisionService.provisionAccount(dto));
    }

    /**
     * 通过邀请码加入已有机构
     */
    @Override
    public Result<UserVo> joinOrgan(@RequestBody @Validated PassportJoinOrganDto dto) {
        return Result.success(tenantProvisionService.joinOrganByInvite(dto));
    }
}
