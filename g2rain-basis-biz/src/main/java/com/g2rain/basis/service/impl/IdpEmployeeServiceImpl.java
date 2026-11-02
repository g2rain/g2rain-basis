package com.g2rain.basis.service.impl;

import com.g2rain.basis.dao.OrganDao;
import com.g2rain.basis.dao.PassportDao;
import com.g2rain.basis.dao.UserDao;
import com.g2rain.basis.dao.po.OrganPo;
import com.g2rain.basis.dao.po.PassportPo;
import com.g2rain.basis.dao.po.UserPo;
import com.g2rain.basis.dto.IdpEmployeeEnsureRequest;
import com.g2rain.basis.dto.UserDto;
import com.g2rain.basis.dto.UserSelectDto;
import com.g2rain.basis.service.IdpEmployeeService;
import com.g2rain.basis.service.UserService;
import com.g2rain.basis.vo.IdpEmployeeEnsureVo;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.utils.Asserts;
import com.g2rain.common.utils.Strings;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * IdP 员工登录 JIT：确保 organ User 存在；新建时不授予 ADMIN。
 */
@Service(value = "idpEmployeeServiceImpl")
public class IdpEmployeeServiceImpl implements IdpEmployeeService {

    private static final int REAL_NAME_MAX = 128;

    @Resource(name = "userDao")
    private UserDao userDao;

    @Resource(name = "passportDao")
    private PassportDao passportDao;

    @Resource(name = "organDao")
    private OrganDao organDao;

    @Resource
    private UserService userService;

    @Override
    @Transactional
    public IdpEmployeeEnsureVo ensure(IdpEmployeeEnsureRequest request) {
        Long organId = request.getOrganId();
        Long passportId = request.getPassportId();
        Asserts.isTrue(organId != null && organId > 0, SystemErrorCode.PARAM_REQUIRED, "organId");
        Asserts.isTrue(passportId != null && passportId > 0, SystemErrorCode.PARAM_REQUIRED, "passportId");

        PassportPo passport = passportDao.selectByIdWithoutIsolation(passportId);
        Asserts.isTrue(Objects.nonNull(passport), SystemErrorCode.PARAM_VAL_INVALID, passportId);
        OrganPo organ = organDao.selectById(organId);
        Asserts.isTrue(Objects.nonNull(organ), SystemErrorCode.PARAM_VAL_INVALID, organId);

        UserSelectDto selectDto = new UserSelectDto();
        selectDto.setPassportId(passportId);
        selectDto.setOrganId(organId);
        List<UserPo> users = userDao.selectListWithoutIsolation(selectDto);
        if (!users.isEmpty()) {
            UserPo existing = users.getFirst();
            IdpEmployeeEnsureVo vo = new IdpEmployeeEnsureVo();
            vo.setOrganId(organId);
            vo.setPassportId(passportId);
            vo.setUserId(existing.getId());
            vo.setCreated(false);
            return vo;
        }

        UserDto userDto = new UserDto();
        userDto.setOrganId(organId);
        userDto.setPassportId(passportId);
        userDto.setRealName(resolveRealName(request.getRealName(), passport.getRealName()));
        userDto.setMobile(blankToNull(request.getMobile()));
        userDto.setEmail(blankToNull(request.getEmail()));
        Long userId = userService.saveWithoutIsolation(userDto);

        IdpEmployeeEnsureVo vo = new IdpEmployeeEnsureVo();
        vo.setOrganId(organId);
        vo.setPassportId(passportId);
        vo.setUserId(userId);
        vo.setCreated(true);
        return vo;
    }

    private static String resolveRealName(String requestName, String passportName) {
        String name = Strings.isNotBlank(requestName) ? requestName.trim()
            : (Strings.isNotBlank(passportName) ? passportName.trim() : "员工");
        if (name.length() > REAL_NAME_MAX) {
            return name.substring(0, REAL_NAME_MAX);
        }
        return name;
    }

    private static String blankToNull(String value) {
        return Strings.isBlank(value) ? null : value.trim();
    }
}
