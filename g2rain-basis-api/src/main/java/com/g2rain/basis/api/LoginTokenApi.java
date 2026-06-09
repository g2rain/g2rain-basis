package com.g2rain.basis.api;

import com.g2rain.basis.dto.LoginTokenDto;
import com.g2rain.basis.dto.LoginTokenSelectDto;
import com.g2rain.basis.vo.LoginTokenVo;
import com.g2rain.basis.vo.StaticAccessTokenResolveVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import com.g2rain.common.web.TokenJWTPayload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


/**
 * 登录信息表, 记录了当前登录状态的相关信息API接口
 * 表名: login_token
 *
 * @author Alpha
 */
@Tag(name = "登录令牌", description = "登录令牌相关接口")
public interface LoginTokenApi {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return 数据列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询登录令牌列表", description = "根据查询条件返回登录令牌列表")
    Result<List<LoginTokenVo>> selectList(LoginTokenSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询登录令牌列表", description = "分页查询登录令牌列表")
    Result<PageData<LoginTokenVo>> selectPage(PageSelectListDto<LoginTokenSelectDto> selectDto);

    @PostMapping("/{applicationCode}/save")
    @Operation(summary = "新增或更新登录令牌", hidden = true, description = "新增或更新登录状态令牌信息")
    Result<Long> save(@Parameter(description = "应用编码") @PathVariable String applicationCode, @RequestBody LoginTokenDto dto);

    /**
     * 获取指定用户在某应用下的登录令牌信息
     *
     * @param passportId      发码会话通行证 ID（可选；三方换票且带 userId 时由 IAM 传入）
     * @param userId          用户ID，可选；如果为空则默认获取当前登录用户的令牌信息
     * @param applicationCode 应用编码，用于区分不同应用的令牌上下文
     * @return 返回对应用户在指定应用下的 JWT 载荷信息
     */
    @GetMapping("/token_context")
    @Operation(summary = "获取登录令牌上下文", hidden = true, description = "获取指定用户在某应用下的登录令牌 JWT 载荷信息")
    Result<TokenJWTPayload> fetchTokenContext(
        @Parameter(description = "发码会话中的通行证 ID；为 true 且 userId 非空时必传，且须与用户所属 passport_id 一致，用于 passport_idp_binding 校验") @RequestParam(required = false) Long passportId,
        @Parameter(description = "用户标识") @RequestParam(required = false) Long userId,
        @Parameter(description = "应用编码") @RequestParam String applicationCode,
        @Parameter(description = "是否由外部身份源授权链路发码换票；为 true 且 userId 非空时校验 application_idp_provision 与 passport_idp_binding") @RequestParam(required = false) Boolean thirdPartyIdpLogin,
        @Parameter(description = "身份源类型，与 IdpType 枚举名一致") @RequestParam(required = false) String idpType,
        @Parameter(description = "IdP 稳定主体，如钉钉 unionId") @RequestParam(required = false) String idpSubject,
        @Parameter(description = "三方应用在 IdP 侧的应用标识，如钉钉 OAuth clientId") @RequestParam(required = false) String idpApplicationCode
    );

    /**
     * 获取匿名会话的登录令牌 JWT 载荷信息。
     *
     * @param organId         机构 ID
     * @param applicationCode 应用编码
     * @return 匿名会话的 JWT 载荷信息
     */
    @GetMapping("/anonymous_token_context")
    @Operation(summary = "获取匿名登录令牌上下文", hidden = true, description = "根据机构与应用构建匿名会话 JWT 载荷信息")
    Result<TokenJWTPayload> fetchAnonymousTokenContext(
        @Parameter(description = "机构 ID") @RequestParam Long organId,
        @Parameter(description = "应用编码") @RequestParam String applicationCode
    );

    /**
     * 根据个人静态访问令牌（原始 API Key）解析状态与会话上下文。
     *
     * <p>
     * 网关 {@code ApiKeyFilter} 调用此接口：服务端对 apiKey 做 SHA-256 后查库，不向调用方返回哈希算法细节。
     * </p>
     *
     * @param apiKey Bearer 后的明文 Key（{@code sk-...}，长度 64）
     * @return {@code data==null} 表示不存在；非空见 {@link StaticAccessTokenResolveVo}
     */
    @GetMapping("/static_access_token_context")
    @Operation(summary = "解析个人静态访问令牌", hidden = true, description = "根据原始 API Key 解析令牌状态与会话上下文")
    Result<StaticAccessTokenResolveVo> fetchStaticTokenContext(@Parameter(description = "原始 API Key") @RequestParam String apiKey);
}
