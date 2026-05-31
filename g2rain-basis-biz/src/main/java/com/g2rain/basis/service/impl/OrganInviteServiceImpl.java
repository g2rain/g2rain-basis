package com.g2rain.basis.service.impl;


import com.g2rain.basis.dao.OrganDao;
import com.g2rain.basis.dao.RoleDao;
import com.g2rain.basis.dao.po.OrganPo;
import com.g2rain.basis.dao.po.RolePo;
import com.g2rain.basis.dto.OrganInviteGenerateDto;
import com.g2rain.basis.dto.OrganInviteRedisDto;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.enums.OrganStatus;
import com.g2rain.basis.service.OrganInviteRedisService;
import com.g2rain.basis.service.OrganInviteService;
import com.g2rain.basis.vo.OrganInviteVo;
import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.utils.Asserts;
import com.g2rain.common.web.PrincipalContextHolder;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 机构邀请码服务实现
 */
@Service(value = "organInviteServiceImpl")
public class OrganInviteServiceImpl implements OrganInviteService {

    private static final int DEFAULT_VALID_DAYS = 7;

    @Resource(name = "organDao")
    private OrganDao organDao;

    @Resource(name = "roleDao")
    private RoleDao roleDao;

    @Resource
    private OrganInviteRedisService organInviteRedisService;

    @Override
    public OrganInviteVo generate(OrganInviteGenerateDto dto) {
        Long organId = dto.getOrganId();
        Long roleId = dto.getRoleId();
        Asserts.isTrue(organId != null && organId > 0, SystemErrorCode.PARAM_VAL_INVALID, "organId");
        Asserts.isTrue(roleId != null && roleId > 0, SystemErrorCode.PARAM_VAL_INVALID, "roleId");

        OrganPo organ = organDao.selectById(organId);
        Asserts.isTrue(organ != null, SystemErrorCode.PARAM_VAL_INVALID, organId);
        Asserts.isTrue(OrganStatus.ACTIVE.name().equals(organ.getStatus()),
            BasisErrorCode.ORGAN_INVITE_TARGET_UNAVAILABLE
        );
        Asserts.isTrue(!Boolean.TRUE.equals(organ.getAdmin()),
            BasisErrorCode.ORGAN_INVITE_TARGET_UNAVAILABLE
        );
        assertCanGenerateForOrgan(organId);

        RolePo role = roleDao.selectById(roleId);
        Asserts.isTrue(role != null, BasisErrorCode.ORGAN_INVITE_ROLE_INVALID);
        Asserts.isTrue(Objects.equals(role.getOrganId(), organId),
            BasisErrorCode.ORGAN_INVITE_ROLE_INVALID
        );

        int validDays = dto.getValidDays() == null || dto.getValidDays() <= 0
            ? DEFAULT_VALID_DAYS
            : dto.getValidDays();
        Duration ttl = Duration.ofDays(validDays);
        LocalDateTime expireAt = LocalDateTime.now().plus(ttl);

        String inviteCode = OrganInviteRedisService.generateInviteCode();
        OrganInviteRedisDto payload = new OrganInviteRedisDto();
        payload.setOrganId(organId);
        payload.setOrganName(organ.getOrganName());
        payload.setRoleId(roleId);
        payload.setRoleName(role.getRoleName());
        payload.setRoleType(role.getRoleType());
        payload.setCreatedBy(PrincipalContextHolder.getUserId());
        payload.setMaxUses(1);
        payload.setUsedCount(0);

        organInviteRedisService.saveInvite(inviteCode, payload, ttl);

        OrganInviteVo vo = new OrganInviteVo();
        vo.setInviteCode(inviteCode);
        vo.setOrganId(organId);
        vo.setOrganName(organ.getOrganName());
        vo.setRoleId(roleId);
        vo.setRoleName(role.getRoleName());
        vo.setRoleType(role.getRoleType());
        vo.setExpireAt(expireAt);
        return vo;
    }

    private void assertCanGenerateForOrgan(Long organId) {
        if (PrincipalContextHolder.isAdminCompany()) {
            return;
        }
        Long currentOrganId = PrincipalContextHolder.getOrganId();
        boolean sameOrgan = Objects.equals(currentOrganId, organId);
        if (!sameOrgan || !PrincipalContextHolder.isAdminUser()) {
            throw new BusinessException(BasisErrorCode.ORGAN_INVITE_GENERATE_FORBIDDEN);
        }
    }
}
