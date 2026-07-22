package com.g2rain.basis.service.impl;

import com.g2rain.basis.dao.PassportDao;
import com.g2rain.basis.dao.PassportIdpBindingDao;
import com.g2rain.basis.dao.po.PassportIdpBindingPo;
import com.g2rain.basis.dao.po.PassportPo;
import com.g2rain.basis.dto.PassportIdpBindingSelectDto;
import com.g2rain.basis.dto.UserSelectDto;
import com.g2rain.basis.idp.resolve.dto.IdpPassportResolveRequest;
import com.g2rain.basis.idp.resolve.vo.IdpPassportBindingSummaryVo;
import com.g2rain.basis.idp.resolve.vo.IdpPassportResolveVo;
import com.g2rain.basis.idp.resolve.vo.IdpPassportUserVo;
import com.g2rain.basis.service.IdpPassportResolveService;
import com.g2rain.basis.service.UserService;
import com.g2rain.basis.vo.UserVo;
import com.g2rain.common.utils.Collections;
import com.g2rain.common.utils.Strings;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * IdP 登录 Passport 无隔离解析服务实现。
 */
@Service(value = "idpPassportResolveServiceImpl")
public class IdpPassportResolveServiceImpl implements IdpPassportResolveService {

    @Resource(name = "passportIdpBindingDao")
    private PassportIdpBindingDao passportIdpBindingDao;

    @Resource(name = "passportDao")
    private PassportDao passportDao;

    @Resource(name = "userServiceImpl")
    private UserService userService;

    @Override
    public IdpPassportResolveVo resolve(IdpPassportResolveRequest request) {
        PassportIdpBindingPo binding = findBinding(passportIdpBindingDao, request);
        if (binding == null || binding.getPassportId() == null) {
            return null;
        }

        Long passportId = binding.getPassportId();
        PassportPo passport = passportDao.selectByIdWithoutIsolation(passportId);

        UserSelectDto userSelect = new UserSelectDto();
        userSelect.setPassportId(passportId);
        List<UserVo> users = userService.selectListWithoutIsolation(userSelect);

        IdpPassportResolveVo vo = new IdpPassportResolveVo();
        vo.setPassportId(passportId);
        if (passport != null) {
            vo.setUsername(passport.getUsername());
            vo.setRealName(passport.getRealName());
        }
        vo.setIdpBinding(toBindingSummary(binding));
        vo.setUsers(toUserSummaries(users));
        return vo;
    }

    static PassportIdpBindingPo findBinding(PassportIdpBindingDao dao, IdpPassportResolveRequest request) {
        List<PassportIdpBindingPo> bindings = dao.selectListWithoutIsolation(buildPrimaryBindingQuery(request));
        if (Collections.isNotEmpty(bindings)) {
            return bindings.getFirst();
        }
        if (Strings.isBlank(request.getIdpUserId())) {
            return null;
        }
        List<PassportIdpBindingPo> fallback = dao.selectListWithoutIsolation(buildFallbackBindingQuery(request));
        return Collections.isEmpty(fallback) ? null : fallback.getFirst();
    }

    static PassportIdpBindingSelectDto buildPrimaryBindingQuery(IdpPassportResolveRequest request) {
        PassportIdpBindingSelectDto query = new PassportIdpBindingSelectDto();
        query.setIdpType(trimToNull(request.getIdpType()));
        query.setIdpSubject(trimToNull(request.getIdpSubject()));
        query.setIdpApplicationCode(normalizeIdpApplicationCode(request.getIdpApplicationCode()));
        return query;
    }

    static PassportIdpBindingSelectDto buildFallbackBindingQuery(IdpPassportResolveRequest request) {
        PassportIdpBindingSelectDto query = new PassportIdpBindingSelectDto();
        query.setIdpType(trimToNull(request.getIdpType()));
        query.setIdpUserId(trimToNull(request.getIdpUserId()));
        query.setIdpApplicationCode(normalizeIdpApplicationCode(request.getIdpApplicationCode()));
        return query;
    }

    static IdpPassportBindingSummaryVo toBindingSummary(PassportIdpBindingPo binding) {
        IdpPassportBindingSummaryVo summary = new IdpPassportBindingSummaryVo();
        summary.setId(binding.getId());
        summary.setPassportId(binding.getPassportId());
        summary.setIdpType(binding.getIdpType());
        summary.setIdpSubject(binding.getIdpSubject());
        summary.setCorpId(binding.getCorpId());
        summary.setIdpUserId(binding.getIdpUserId());
        summary.setIdpOpenId(binding.getIdpOpenId());
        summary.setIdpApplicationCode(binding.getIdpApplicationCode());
        summary.setBindMode(binding.getBindMode());
        return summary;
    }

    static List<IdpPassportUserVo> toUserSummaries(List<UserVo> users) {
        if (Collections.isEmpty(users)) {
            return List.of();
        }
        return users.stream().map(IdpPassportResolveServiceImpl::toUserSummary).toList();
    }

    static IdpPassportUserVo toUserSummary(UserVo user) {
        IdpPassportUserVo summary = new IdpPassportUserVo();
        summary.setUserId(user.getId());
        summary.setOrganId(user.getOrganId());
        summary.setRealName(user.getRealName());
        return summary;
    }

    static String normalizeIdpApplicationCode(String idpApplicationCode) {
        return idpApplicationCode == null ? "" : idpApplicationCode.trim();
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
