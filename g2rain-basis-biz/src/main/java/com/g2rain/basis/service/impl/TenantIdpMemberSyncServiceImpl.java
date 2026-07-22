package com.g2rain.basis.service.impl;

import com.g2rain.basis.client.IdpSyncClient;
import com.g2rain.basis.dao.PassportIdpBindingDao;
import com.g2rain.basis.dao.UserDao;
import com.g2rain.basis.dao.po.PassportIdpBindingPo;
import com.g2rain.basis.dao.po.UserPo;
import com.g2rain.basis.dto.PassportDto;
import com.g2rain.basis.dto.PassportIdpBindingDto;
import com.g2rain.basis.dto.PassportIdpBindingSelectDto;
import com.g2rain.basis.dto.UserDto;
import com.g2rain.basis.dto.UserSelectDto;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.enums.IdpSyncMode;
import com.g2rain.basis.idp.sync.dto.IdpFetchMemberRequest;
import com.g2rain.basis.idp.sync.dto.IdpMemberNode;
import com.g2rain.basis.model.TenantIdpMemberSyncResult;
import com.g2rain.basis.service.PassportIdpBindingService;
import com.g2rain.basis.service.PassportService;
import com.g2rain.basis.service.TenantIdpMemberSyncService;
import com.g2rain.basis.service.UserService;
import com.g2rain.basis.service.idp.IdpMemberBindingSnapshotSupport;
import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.ExceptionConverter;
import com.g2rain.common.model.Result;
import com.g2rain.common.utils.Moments;
import com.g2rain.common.utils.Strings;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 租户 IdP 成员同步服务实现。
 */
@Service(value = "tenantIdpMemberSyncServiceImpl")
public class TenantIdpMemberSyncServiceImpl implements TenantIdpMemberSyncService {

    private static final String DEFAULT_DISPLAY_NAME = "钉钉用户";
    private static final String AUTO_REGISTER_PASSWORD = "123456";

    @Resource(name = "passportServiceImpl")
    private PassportService passportService;

    @Resource(name = "passportIdpBindingServiceImpl")
    private PassportIdpBindingService passportIdpBindingService;

    @Resource(name = "passportIdpBindingDao")
    private PassportIdpBindingDao passportIdpBindingDao;

    @Resource(name = "userServiceImpl")
    private UserService userService;

    @Resource(name = "userDao")
    private UserDao userDao;

    @Resource
    private IdpSyncClient idpSyncClient;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TenantIdpMemberSyncResult syncMembers(
        Long organId,
        String idpType,
        String bindMode,
        String idpApplicationCode,
        String corpId,
        String syncMode,
        List<IdpMemberNode> members,
        boolean enableDestructiveReconcile
    ) {
        TenantIdpMemberSyncResult result = new TenantIdpMemberSyncResult();
        for (IdpMemberNode member : members) {
            if (Strings.isBlank(member.getUnionId()) && Strings.isBlank(member.getIdpUserId())) {
                continue;
            }
            syncSingleMember(organId, idpType, bindMode, idpApplicationCode, corpId, member, result);
        }
        if (IdpSyncMode.FULL == IdpSyncMode.normalize(syncMode) && enableDestructiveReconcile) {
            reconcileOffboardedMembers(organId, idpType, bindMode, idpApplicationCode, corpId, members, result);
        }
        return result;
    }

    private void reconcileOffboardedMembers(
        Long organId,
        String idpType,
        String bindMode,
        String idpApplicationCode,
        String corpId,
        List<IdpMemberNode> members,
        TenantIdpMemberSyncResult result
    ) {
        PassportIdpBindingSelectDto query = new PassportIdpBindingSelectDto();
        query.setIdpType(idpType);
        query.setIdpApplicationCode(idpApplicationCode);
        query.setCorpId(corpId);
        query.setBindMode(bindMode);
        List<PassportIdpBindingPo> scopeBindings = passportIdpBindingDao.selectListWithoutIsolation(query);
        Set<String> snapshotKeys = IdpMemberBindingSnapshotSupport.buildSnapshotBindingKeys(members);
        for (PassportIdpBindingPo binding : scopeBindings) {
            if (IdpMemberBindingSnapshotSupport.isBindingInSnapshot(binding, snapshotKeys)) {
                continue;
            }
            offboardBindingAndUser(organId, binding, result);
        }
    }

    private void offboardBindingAndUser(Long organId, PassportIdpBindingPo binding, TenantIdpMemberSyncResult result) {
        if (passportIdpBindingDao.delete(binding.getId()) > 0) {
            result.setBindingsDeleted(result.getBindingsDeleted() + 1);
        }
        UserSelectDto userQuery = new UserSelectDto();
        userQuery.setPassportId(binding.getPassportId());
        userQuery.setOrganId(organId);
        for (UserPo user : userDao.selectListWithoutIsolation(userQuery)) {
            if (userService.deleteWithoutIsolation(user.getId()) > 0) {
                result.setMembersDeleted(result.getMembersDeleted() + 1);
            }
        }
    }

    private void syncSingleMember(
        Long organId,
        String idpType,
        String bindMode,
        String idpApplicationCode,
        String corpId,
        IdpMemberNode member,
        TenantIdpMemberSyncResult result
    ) {
        Long passportId = resolvePassportId(idpType, idpApplicationCode, bindMode, corpId, member, result);
        Long userId = resolveUserId(organId, passportId, member, result);
        result.getUnionIdToUserId().put(member.getUnionId(), userId);
    }

    private Long resolvePassportId(
        String idpType,
        String idpApplicationCode,
        String bindMode,
        String corpId,
        IdpMemberNode member,
        TenantIdpMemberSyncResult result
    ) {
        PassportIdpBindingPo existing = findExistingBinding(idpType, idpApplicationCode, member);
        if (existing != null) {
            if (Strings.isBlank(member.getUnionId())) {
                member.setUnionId(existing.getIdpSubject());
            }
            if (updateBindingIfNeeded(existing, member)) {
                result.setBindingsUpdated(result.getBindingsUpdated() + 1);
            }
            return existing.getPassportId();
        }

        ensureMemberInitializedFromIam(bindMode, idpApplicationCode, corpId, member);
        if (Strings.isBlank(member.getUnionId())) {
            throw new BusinessException(BasisErrorCode.TENANT_IDP_SYNC_IDP_FETCH_FAILED);
        }

        PassportDto passportDto = new PassportDto();
        passportDto.setUsername(dingTalkPassportUsername(member.getUnionId()));
        passportDto.setPassword(AUTO_REGISTER_PASSWORD);
        passportDto.setRealName(resolveRealName(member.getName()));
        passportDto.setMobile(member.getMobile());
        passportDto.setEmail(member.getEmail());
        passportDto.setPasswordTrusted(false);
        Long passportId = passportService.save(passportDto);

        PassportIdpBindingDto bindingDto = new PassportIdpBindingDto();
        bindingDto.setPassportId(passportId);
        bindingDto.setIdpType(idpType);
        bindingDto.setIdpSubject(member.getUnionId());
        bindingDto.setCorpId(corpId);
        bindingDto.setIdpUserId(member.getIdpUserId());
        bindingDto.setIdpApplicationCode(idpApplicationCode);
        bindingDto.setBindMode(bindMode);
        bindingDto.setRawProfile("{}");
        passportIdpBindingService.save(bindingDto);
        result.setBindingsCreated(result.getBindingsCreated() + 1);
        return passportId;
    }

    private PassportIdpBindingPo findExistingBinding(
        String idpType,
        String idpApplicationCode,
        IdpMemberNode member
    ) {
        if (Strings.isNotBlank(member.getUnionId())) {
            PassportIdpBindingSelectDto query = new PassportIdpBindingSelectDto();
            query.setIdpType(idpType);
            query.setIdpSubject(member.getUnionId());
            query.setIdpApplicationCode(idpApplicationCode);
            List<PassportIdpBindingPo> bindings = passportIdpBindingDao.selectListWithoutIsolation(query);
            if (!bindings.isEmpty()) {
                return bindings.getFirst();
            }
        }
        if (Strings.isNotBlank(member.getIdpUserId())) {
            PassportIdpBindingSelectDto query = new PassportIdpBindingSelectDto();
            query.setIdpType(idpType);
            query.setIdpUserId(member.getIdpUserId());
            query.setIdpApplicationCode(idpApplicationCode);
            List<PassportIdpBindingPo> bindings = passportIdpBindingDao.selectListWithoutIsolation(query);
            if (!bindings.isEmpty()) {
                return bindings.getFirst();
            }
        }
        return null;
    }

    private void ensureMemberInitializedFromIam(
        String bindMode,
        String idpApplicationCode,
        String corpId,
        IdpMemberNode member
    ) {
        if (Strings.isBlank(member.getIdpUserId())) {
            return;
        }
        IdpFetchMemberRequest request = new IdpFetchMemberRequest();
        request.setCorpId(corpId);
        request.setBindMode(bindMode);
        request.setIdpApplicationCode(idpApplicationCode);
        request.setIdpUserId(member.getIdpUserId().trim());
        Result<IdpMemberNode> remoteResult;
        try {
            remoteResult = idpSyncClient.fetchDingTalkMember(request);
        } catch (Exception ex) {
            throw new BusinessException(BasisErrorCode.TENANT_IDP_SYNC_IDP_FETCH_FAILED);
        }
        if (!remoteResult.isSuccess()) {
            throw ExceptionConverter.of(remoteResult);
        }
        if (remoteResult.getData() == null) {
            throw new BusinessException(BasisErrorCode.TENANT_IDP_SYNC_IDP_FETCH_FAILED);
        }
        mergeMemberSnapshot(member, remoteResult.getData());
    }

    static void mergeMemberSnapshot(IdpMemberNode target, IdpMemberNode remote) {
        if (Strings.isNotBlank(remote.getUnionId())) {
            target.setUnionId(remote.getUnionId().trim());
        }
        if (Strings.isNotBlank(remote.getIdpUserId())) {
            target.setIdpUserId(remote.getIdpUserId().trim());
        }
        if (Strings.isBlank(target.getName()) && Strings.isNotBlank(remote.getName())) {
            target.setName(remote.getName());
        }
        if (Strings.isBlank(target.getMobile()) && Strings.isNotBlank(remote.getMobile())) {
            target.setMobile(remote.getMobile());
        }
        if (Strings.isBlank(target.getEmail()) && Strings.isNotBlank(remote.getEmail())) {
            target.setEmail(remote.getEmail());
        }
    }

    private Long resolveUserId(Long organId, Long passportId, IdpMemberNode member, TenantIdpMemberSyncResult result) {
        UserSelectDto selectDto = new UserSelectDto();
        selectDto.setPassportId(passportId);
        selectDto.setOrganId(organId);
        List<UserPo> users = userDao.selectListWithoutIsolation(selectDto);
        if (!users.isEmpty()) {
            UserPo existing = users.getFirst();
            if (updateUserIfNeeded(existing, member)) {
                result.setMembersUpdated(result.getMembersUpdated() + 1);
            }
            return existing.getId();
        }

        UserDto userDto = new UserDto();
        userDto.setPassportId(passportId);
        userDto.setOrganId(organId);
        userDto.setRealName(resolveRealName(member.getName()));
        userDto.setMobile(member.getMobile());
        userDto.setEmail(member.getEmail());
        Long userId = userService.saveWithoutIsolation(userDto);
        result.setMembersCreated(result.getMembersCreated() + 1);
        return userId;
    }

    private boolean updateBindingIfNeeded(PassportIdpBindingPo existing, IdpMemberNode member) {
        if (!needsBindingIdpUserIdUpdate(existing.getIdpUserId(), member.getIdpUserId())) {
            return false;
        }
        PassportIdpBindingPo update = new PassportIdpBindingPo();
        update.setId(existing.getId());
        update.setIdpUserId(member.getIdpUserId().trim());
        update.setUpdateTime(Moments.now());
        passportIdpBindingDao.update(update);
        return true;
    }

    static boolean needsBindingIdpUserIdUpdate(String existingIdpUserId, String snapshotIdpUserId) {
        if (Strings.isBlank(snapshotIdpUserId)) {
            return false;
        }
        return !Objects.equals(existingIdpUserId, snapshotIdpUserId.trim());
    }

    private boolean updateUserIfNeeded(UserPo existing, IdpMemberNode member) {
        UserPo update = new UserPo();
        update.setId(existing.getId());
        boolean changed = false;
        if (Strings.isNotBlank(member.getName()) && !Objects.equals(existing.getRealName(), member.getName())) {
            update.setRealName(member.getName());
            changed = true;
        }
        if (Strings.isNotBlank(member.getMobile()) && !Objects.equals(existing.getMobile(), member.getMobile())) {
            update.setMobile(member.getMobile());
            changed = true;
        }
        if (Strings.isNotBlank(member.getEmail()) && !Objects.equals(existing.getEmail(), member.getEmail())) {
            update.setEmail(member.getEmail());
            changed = true;
        }
        if (!changed) {
            return false;
        }
        userDao.updateWithoutIsolation(update);
        return true;
    }

    private static String resolveRealName(String displayName) {
        return Strings.isBlank(displayName) ? DEFAULT_DISPLAY_NAME : displayName.trim();
    }

    private static String dingTalkPassportUsername(String unionId) {
        String prefix = "dt_";
        if (prefix.length() + unionId.length() <= 64) {
            return prefix + unionId;
        }
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(unionId.getBytes(StandardCharsets.UTF_8));
            return prefix + HexFormat.of().formatHex(digest, 0, 28);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
