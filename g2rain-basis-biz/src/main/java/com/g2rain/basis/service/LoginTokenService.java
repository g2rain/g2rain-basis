package com.g2rain.basis.service;

import com.g2rain.basis.dto.LoginTokenDto;
import com.g2rain.basis.dto.LoginTokenSelectDto;
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
     * @param userId          用户 ID，可为 null（表示 Passport 会话）
     * @param applicationCode 应用编码
     * @return 构建完成的 {@link TokenJWTPayload}，包含用户、机构、应用及应用作用域信息
     * @throws BusinessException 当用户、机构或应用不存在，或机构不可用时抛出
     */
    TokenJWTPayload fetchTokenContext(Long userId, String applicationCode);
}
