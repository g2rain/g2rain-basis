package com.g2rain.basis.service.impl;


import com.g2rain.basis.dto.ControlUnitSelectDto;
import com.g2rain.basis.dto.OrganDto;
import com.g2rain.basis.dto.RoleDto;
import com.g2rain.basis.dto.RoleSelectDto;
import com.g2rain.basis.dto.TenantProvisionDto;
import com.g2rain.basis.dto.UserDto;
import com.g2rain.basis.dto.UserRoleRelationDto;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.enums.ControlUnitScope;
import com.g2rain.basis.enums.RoleType;
import com.g2rain.basis.model.RoleControlUnitRelation;
import com.g2rain.basis.service.ApplicationAuthorizationService;
import com.g2rain.basis.service.ControlDomainService;
import com.g2rain.basis.service.ControlUnitService;
import com.g2rain.basis.service.OrganService;
import com.g2rain.basis.service.RoleControlUnitRelationService;
import com.g2rain.basis.service.RoleService;
import com.g2rain.basis.service.TenantProvisionService;
import com.g2rain.basis.service.UserRoleRelationService;
import com.g2rain.basis.service.UserService;
import com.g2rain.basis.utils.Constants;
import com.g2rain.basis.vo.ControlUnitVo;
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
import java.util.Set;
import java.util.stream.Collectors;

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
 * <p>依赖服务：
 * <ul>
 *     <li>{@link OrganService} - 机构相关操作</li>
 *     <li>{@link UserService} - 用户相关操作</li>
 *     <li>{@link RoleService} - 角色相关操作</li>
 *     <li>{@link UserRoleRelationService} - 用户与角色关联操作</li>
 *     <li>{@link ControlDomainService} - 控制域相关操作</li>
 *     <li>{@link ApplicationAuthorizationService} - 应用权限下发操作</li>
 * </ul>
 *
 * @author alpha
 * @since 2026/1/30
 */
@Service(value = "tenantProvisionServiceImpl")
public class TenantProvisionServiceImpl implements TenantProvisionService {
    @Resource(name = "organServiceImpl")
    private OrganService organService;

    @Resource(name = "userServiceImpl")
    private UserService userService;

    @Resource(name = "roleServiceImpl")
    private RoleService roleService;

    @Resource(name = "userRoleRelationServiceImpl")
    private UserRoleRelationService userRoleRelationService;

    @Resource(name = "controlUnitServiceImpl")
    private ControlUnitService controlUnitService;

    @Resource(name = "roleControlUnitRelationServiceImpl")
    private RoleControlUnitRelationService roleControlUnitRelationService;

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
        Long organId = createOrgan(organDto);

        // 2. 新增用户
        UserDto userDto = new UserDto();
        userDto.setOrganId(organId);
        userDto.setEmail(dto.getEmail());
        userDto.setMobile(dto.getMobile());
        userDto.setRealName(Objects.toString(dto.getRealName(),
            PrincipalContextHolder.getName()
        ));
        userDto.setPassportId(PrincipalContextHolder.getPassportId());
        long userId = userService.save(userDto);

        // 新增的时候, 需要知道当前是否存在用户, 机构第一个用户通常都是管理员, 这个地方需要用分布式锁锁住可以, 不然并发会导致出现多个管理员
        if (userService.checkUserExists(organId) > 1) {
            return userService.selectById(userId);
        }

        // 通过机构查找默认角色
        RoleSelectDto roleSelect = new RoleSelectDto();
        roleSelect.setOrganId(organId);
        roleSelect.setRoleType(RoleType.ADMIN.name());
        List<RoleVo> roles = roleService.selectList(roleSelect);
        Asserts.isTrue(Collections.isNotEmpty(roles),
            BasisErrorCode.ADMIN_ROLE_NOT_EXISTS_ILLEGAL
        );

        // 3. 关联超管角色
        UserRoleRelationDto userRole = new UserRoleRelationDto();
        userRole.setUserId(userId);
        userRole.setRoleId(roles.getFirst().getId());
        userRoleRelationService.save(userRole);

        // 返回 user 信息给调用者
        return userService.selectById(userId);
    }

    /**
     * 创建机构、添加默认管理员角色，并开通最小功能。
     *
     * <p>该方法可被其他接口独立调用，内部调用时参与调用方事务。
     *
     * @param organDto 机构信息
     * @return 创建的机构 ID
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public long createOrgan(OrganDto organDto) {
        // 1. 创建机构
        Long organId = organService.save(organDto);

        // 2. 添加机构默认角色
        RoleDto role = new RoleDto();
        role.setRoleName(Constants.ADMIN_ROLE_NAME);
        role.setOrganId(organId);
        Long roleId = roleService.internalSave(RoleType.ADMIN, role);

        // 3. 查找客户的默认控制单元集合
        ControlUnitSelectDto select = new ControlUnitSelectDto();
        select.setLanding(true);
        select.setControlUnitScope(ControlUnitScope.CUSTOMER.name());
        Set<Long> ids = controlUnitService.selectList(select).stream()
            .map(ControlUnitVo::getId).collect(Collectors.toSet());
        // 4. 为机构开通最小功能
        RoleControlUnitRelation relation = new RoleControlUnitRelation();
        relation.setRoleId(roleId);
        relation.setControlUnitIds(ids);
        roleControlUnitRelationService.internalSave(relation);

        return organId;
    }
}
