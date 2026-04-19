package com.g2rain.basis.service;


import com.g2rain.basis.dto.OrganDto;
import com.g2rain.basis.dto.TenantProvisionDto;
import com.g2rain.basis.vo.UserVo;

/**
 * 租户账号开通服务接口
 *
 * <p>该接口定义了在租户下开通账号及机构相关最小功能的操作。
 * 实现类应保证事务一致性和幂等性：
 * <ul>
 *     <li>provisionAccount - 为账号开通最小可用功能，包括创建机构、用户及管理员角色</li>
 *     <li>createOrgan - 创建机构并添加默认管理员角色，同时开通基础功能</li>
 * </ul>
 *
 * <p>实现类可根据具体业务需求扩展方法的事务和功能逻辑。
 *
 * @author alpha
 * @since 2026/1/30
 */
public interface TenantProvisionService {

    /**
     * 为账号在指定租户下开通最小可用功能。
     *
     * <p>主要流程包括：
     * <ul>
     *     <li>创建机构（如新机构）及默认管理员角色</li>
     *     <li>为机构开通基础功能</li>
     *     <li>新增用户并关联账号</li>
     *     <li>为机构第一个用户分配默认管理员角色（保证幂等性）</li>
     * </ul>
     *
     * @param dto 租户开通账号所需信息
     * @return 成功标记 1:成功; 0:失败
     */
    UserVo provisionAccount(TenantProvisionDto dto);

    /**
     * 创建机构、添加默认管理员角色，并开通最小功能。
     *
     * <p>该方法可被其他接口独立调用，内部调用时参与调用方事务。
     *
     * @param dto 机构信息
     * @return 创建的机构 ID
     */

    long createOrgan(OrganDto dto);
}
