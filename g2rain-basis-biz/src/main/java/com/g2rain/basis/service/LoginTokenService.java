package com.g2rain.basis.service;

import com.g2rain.basis.dto.LoginTokenDto;
import com.g2rain.basis.dto.LoginTokenSelectDto;
import com.g2rain.basis.vo.StaticAccessTokenResolveVo;
import com.g2rain.basis.vo.LoginTokenVo;
import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.web.ApplicationScope;
import com.g2rain.common.web.TokenJWTPayload;

import java.util.List;

/**
 * 登录信息表, 记录了当前登录状态的相关信息服务接口
 * 表名: login_token
 *
 * @author Alpha
 */
public interface LoginTokenService {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return VO 对象列表
     */
    List<LoginTokenVo> selectList(LoginTokenSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页 V O数据
     */
    PageData<LoginTokenVo> selectPage(PageSelectListDto<LoginTokenSelectDto> selectDto);

    /**
     * 新增或更新数据
     *
     * @param applicationCode 应用编码
     * @param dto             数据传输对象
     * @return 操作结果（影响行数）
     */
    Long save(String applicationCode, LoginTokenDto dto);

    /**
     * 根据 ID 删除数据
     *
     * @param id 主键 ID
     * @return 操作结果（影响行数）
     */
    int delete(Long id);

    /**
     * 构建 Token JWT 的载荷信息。
     *
     * <p>根据用户 ID 和应用编码一次性查询用户信息、机构信息、应用信息及应用作用域，
     * 并封装到 {@link TokenJWTPayload} 中，用于生成登录后的 Token。</p>
     *
     * <p>逻辑说明：</p>
     * <ol>
     *     <li>如果 userId 为 null，则返回默认 Passport 类型的 Token 载荷。</li>
     *     <li>查询用户信息，并设置用户相关字段（ID、姓名、管理员标识）。</li>
     *     <li>查询机构信息，并校验机构状态是否激活，同时设置机构相关字段（ID、名称、类型、是否管理员公司）。</li>
     *     <li>查询应用信息及应用作用域，并封装到 {@link ApplicationScope} 列表中。</li>
     * </ol>
     *
     * @param passportId           发码侧会话中的通行证 ID；三方发码换票且 {@code userId} 非空时必传，且须与用户 {@code passport_id} 一致
     * @param userId               用户 ID，可为 null（表示 Passport 会话）
     * @param applicationCode      应用编码
     * @param thirdPartyIdpLogin   是否外部身份源授权链路发码；为 {@link Boolean#TRUE} 且 {@code userId} 非空时校验
     *                               {@code application_idp_provision} 与 {@code passport_idp_binding}
     * @param idpType              身份源类型（与授权码中快照一致）
     * @param idpSubject           IdP 稳定主体
     * @param idpApplicationCode   三方应用在 IdP 侧的标识（如钉钉 OAuth clientId）
     * @return 构建完成的 {@link TokenJWTPayload}，包含用户、机构、应用及应用作用域信息
     * @throws BusinessException 当用户、机构或应用不存在，或机构不可用时抛出
     */
    TokenJWTPayload fetchTokenContext(Long passportId, Long userId, String applicationCode, Boolean thirdPartyIdpLogin,
                                      String idpType, String idpSubject, String idpApplicationCode);

    /**
     * 构建匿名会话的 Token JWT 载荷信息。
     *
     * @param organId         机构 ID
     * @param applicationCode 应用编码
     * @param roleIds         可选；非空时使用指定角色，为空时回退机构 ADMIN 角色
     * @return 匿名会话的 {@link TokenJWTPayload}
     * @throws BusinessException 当机构或应用不存在，或机构不可用时抛出
     */
    TokenJWTPayload fetchAnonymousTokenContext(Long organId, String applicationCode, List<Long> roleIds);

    /**
     * 根据个人静态访问令牌解析状态与会话上下文。
     *
     * @param apiKey 原始 API Key（明文）
     * @return {@code null} 表示不存在；吊销态仅含 status；激活态含 {@link com.g2rain.basis.vo.StaticAccessTokenContextVo}
     */
    StaticAccessTokenResolveVo fetchStaticTokenContext(String apiKey);
}
