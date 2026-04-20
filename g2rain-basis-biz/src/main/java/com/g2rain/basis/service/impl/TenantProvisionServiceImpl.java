package com.g2rain.basis.service.impl;


import com.g2rain.basis.dto.OrganDto;
import com.g2rain.basis.dto.RoleSelectDto;
import com.g2rain.basis.dto.TenantProvisionDto;
import com.g2rain.basis.dto.UserDto;
import com.g2rain.basis.dto.UserRoleRelationDto;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.enums.RoleType;
import com.g2rain.basis.service.OrganProvisionService;
import com.g2rain.basis.service.RoleService;
import com.g2rain.basis.service.TenantProvisionService;
import com.g2rain.basis.service.UserRoleRelationService;
import com.g2rain.basis.service.UserService;
import com.g2rain.basis.vo.RoleVo;
import com.g2rain.basis.vo.UserVo;
import com.g2rain.common.enums.OrganType;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.utils.Asserts;
import com.g2rain.common.utils.Collections;
import com.g2rain.common.utils.Strings;
import com.g2rain.common.web.PrincipalContextHolder;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * 租户账号开通服务实现类
 *
 * <p>该实现类负责在指定租户下完成账号开通的最小可用能力，主要流程包括：
 * <ul>
 *     <li>创建机构及默认管理员角色</li>
 *     <li>为机构开通基础功能权限</li>
 *     <li>创建用户并关联账号</li>
 *     <li>为机构第一个用户分配默认管理员角色</li>
 * </ul>
 *
 * <p>注意事项：
 * <ul>
 *     <li>事务由 {@link org.springframework.transaction.annotation.Transactional} 保证，隔离级别为 READ_COMMITTED</li>
 *     <li>方法内部调用同类事务方法时，事务传播默认参与调用方事务</li>
 *     <li>保证幂等性：若机构已有用户，则不会重复分配管理员角色</li>
 * </ul>
 *
 * @author alpha
 * @since 2026/1/30
 */
@Service(value = "tenantProvisionServiceImpl")
public class TenantProvisionServiceImpl implements TenantProvisionService {
    @Resource(name = "organProvisionServiceImpl")
    private OrganProvisionService organProvisionService;

    @Resource(name = "userServiceImpl")
    private UserService userService;

    @Resource(name = "roleServiceImpl")
    private RoleService roleService;

    @Resource(name = "userRoleRelationServiceImpl")
    private UserRoleRelationService userRoleRelationService;

    /**
     * 为账号在租户下开通最小可用功能。
     *
     * <p>主要流程：
     * <ol>
     *     <li>创建机构（含默认管理员角色）并开通基础功能</li>
     *     <li>创建用户</li>
     *     <li>判断机构是否已有用户，若无则为第一个用户分配默认管理员角色</li>
     *     <li>用户与默认管理员角色关联</li>
     * </ol>
     *
     * <p>事务保证：方法在单一事务中执行，抛异常则整个事务回滚。
     *
     * @param dto 租户开通账号所需信息
     * @return 成功标记 1:成功; 0:失败
     */
    @Override
    @SuppressWarnings("java:S6809")
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public UserVo provisionAccount(TenantProvisionDto dto) {
        // 0. 参数校验
        OrganType.fromName(dto.getOrganType());
        Asserts.isTrue(Strings.isNotBlank(dto.getOrganName()),
            SystemErrorCode.PARAM_VAL_INVALID, "organName"
        );

        // 1. 创建机构、机构角色、开通功能
        OrganDto organDto = new OrganDto();
        organDto.setOrganName(dto.getOrganName());
        organDto.setOrganType(dto.getOrganType());
        Long organId = organProvisionService.createOrganWithoutIsolation(organDto);

        // 2. 新增用户
        UserDto userDto = new UserDto();
        userDto.setOrganId(organId);
        userDto.setEmail(dto.getEmail());
        userDto.setMobile(dto.getMobile());
        userDto.setRealName(Objects.toString(dto.getRealName(),
            PrincipalContextHolder.getName()
        ));
        userDto.setPassportId(PrincipalContextHolder.getPassportId());
        long userId = userService.saveWithoutIsolation(userDto);

        // 新增的时候, 需要知道当前是否存在用户, 机构第一个用户通常都是管理员, 这个地方需要用分布式锁锁住可以, 不然并发会导致出现多个管理员
        if (userService.checkUserExists(organId) > 1) {
            return userService.selectByIdWithoutIsolation(userId);
        }

        // 通过机构查找默认角色
        RoleSelectDto roleSelect = new RoleSelectDto();
        roleSelect.setOrganId(organId);
        roleSelect.setRoleType(RoleType.ADMIN.name());
        List<RoleVo> roles = roleService.selectListWithoutIsolation(roleSelect);
        Asserts.isTrue(Collections.isNotEmpty(roles),
            BasisErrorCode.ADMIN_ROLE_NOT_EXISTS_ILLEGAL
        );

        // 3. 关联超管角色
        UserRoleRelationDto userRole = new UserRoleRelationDto();
        userRole.setUserId(userId);
        userRole.setRoleId(roles.getFirst().getId());
        userRoleRelationService.save(userRole);

        // 返回 user 信息给调用者
        return userService.selectByIdWithoutIsolation(userId);
    }
}
