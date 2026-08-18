package com.g2rain.basis.service.impl;

import com.g2rain.basis.dto.ApplicationAuthorizationDto;
import com.g2rain.basis.dto.ControlDomainSelectDto;
import com.g2rain.basis.dto.ControlUnitSelectDto;
import com.g2rain.basis.dto.OrganDto;
import com.g2rain.basis.dto.RoleDto;
import com.g2rain.basis.enums.ControlDomainScope;
import com.g2rain.basis.enums.ControlDomainType;
import com.g2rain.basis.enums.ControlUnitScope;
import com.g2rain.basis.enums.RoleType;
import com.g2rain.basis.model.RoleControlUnitRelation;
import com.g2rain.basis.service.ApplicationAuthorizationService;
import com.g2rain.basis.service.ControlDomainService;
import com.g2rain.basis.service.ControlUnitService;
import com.g2rain.basis.service.OrganProvisionService;
import com.g2rain.basis.service.OrganService;
import com.g2rain.basis.service.RoleControlUnitRelationService;
import com.g2rain.basis.service.RoleService;
import com.g2rain.basis.utils.Constants;
import com.g2rain.basis.vo.ControlDomainVo;
import com.g2rain.basis.vo.ControlUnitVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 机构开通编排实现。
 */
@Service(value = "organProvisionServiceImpl")
public class OrganProvisionServiceImpl implements OrganProvisionService {

    @Resource(name = "organServiceImpl")
    private OrganService organService;

    @Resource(name = "roleServiceImpl")
    private RoleService roleService;

    @Resource(name = "controlUnitServiceImpl")
    private ControlUnitService controlUnitService;

    @Resource(name = "roleControlUnitRelationServiceImpl")
    private RoleControlUnitRelationService roleControlUnitRelationService;

    @Resource(name = "controlDomainServiceImpl")
    private ControlDomainService controlDomainService;

    @Resource(name = "applicationAuthorizationServiceImpl")
    private ApplicationAuthorizationService applicationAuthorizationService;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public long createOrganWithIsolation(OrganDto dto) {
        return doCreateOrgan(dto, organService::saveWithIsolation, role -> roleService.saveWithIsolation(RoleType.ADMIN, role));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public long createOrganWithoutIsolation(OrganDto dto) {
        return doCreateOrgan(dto, organService::saveWithoutIsolation, role -> roleService.saveWithoutIsolation(RoleType.ADMIN, role));
    }

    private long doCreateOrgan(OrganDto organDto, Function<OrganDto, Long> saveOrganFn, Function<RoleDto, Long> saveRoleFn) {
        Long id = organDto.getId();

        // 1. 创建机构
        Long organId = saveOrganFn.apply(organDto);

        // 1.1 修改机构, 不执行后续逻辑
        if (Objects.nonNull(id) && id != 0L) {
            return id;
        }

        // 2. 添加机构默认角色
        RoleDto role = new RoleDto();
        role.setRoleName(Constants.ADMIN_ROLE_NAME);
        role.setOrganId(organId);
        Long roleId = saveRoleFn.apply(role);

        // 3. 为 landing 控制域自动开通应用授权（同步该域下控制单元到默认角色）
        ControlDomainSelectDto domainSelect = new ControlDomainSelectDto();
        domainSelect.setLanding(true);
        domainSelect.setControlDomainScope(ControlDomainScope.CUSTOMER.name());
        for (ControlDomainVo domain : controlDomainService.selectList(domainSelect)) {
            if (ControlDomainType.TRADE.name().equals(domain.getControlDomainType())) {
                continue;
            }
            ApplicationAuthorizationDto authDto = new ApplicationAuthorizationDto();
            authDto.setOrganId(organId);
            authDto.setApplicationId(domain.getApplicationId());
            authDto.setControlDomainId(domain.getId());
            applicationAuthorizationService.save(authDto);
        }

        // 4. 查找客户的默认控制单元集合并补全最小功能
        ControlUnitSelectDto select = new ControlUnitSelectDto();
        select.setLanding(true);
        select.setControlUnitScope(ControlUnitScope.CUSTOMER.name());
        Set<Long> ids = controlUnitService.selectList(select).stream()
            .map(ControlUnitVo::getId).collect(Collectors.toSet());
        RoleControlUnitRelation relation = new RoleControlUnitRelation();
        relation.setRoleId(roleId);
        relation.setControlUnitIds(ids);
        roleControlUnitRelationService.internalSave(relation);

        return organId;
    }
}
