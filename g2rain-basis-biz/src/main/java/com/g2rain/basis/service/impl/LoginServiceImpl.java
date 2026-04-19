package com.g2rain.basis.service.impl;


import com.g2rain.basis.converter.PassportConverter;
import com.g2rain.basis.dao.PassportDao;
import com.g2rain.basis.dao.po.PassportPo;
import com.g2rain.basis.dto.LoginDto;
import com.g2rain.basis.dto.PassportSelectDto;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.enums.PassportStatus;
import com.g2rain.basis.service.LoginService;
import com.g2rain.basis.utils.BasisUtils;
import com.g2rain.basis.vo.PassportVo;
import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.utils.Asserts;
import com.g2rain.common.utils.Collections;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 登录服务实现类
 * <p>
 * 核心功能：
 * <ul>
 *     <li>用户登录校验</li>
 *     <li>验证用户名和密码</li>
 *     <li>验证账户状态（是否冻结）</li>
 *     <li>返回用户 Passport 信息 VO</li>
 * </ul>
 * <p>
 * 对应数据库表: {@code passport}
 *
 * @author alpha
 * @since 2026/1/15
 */

@Service(value = "loginServiceImpl")
public class LoginServiceImpl implements LoginService {

    @Resource(name = "passportDao")
    private PassportDao passportDao;

    /**
     * 用户登录
     *
     * <p>核心逻辑：
     * <ol>
     *     <li>查询所有账户信息</li>
     *     <li>校验账户存在</li>
     *     <li>校验账户状态为正常</li>
     *     <li>校验密码正确性</li>
     *     <li>返回 Passport VO</li>
     * </ol>
     *
     * @param dto 登录 DTO，包含用户名和密码
     * @return 登录成功的 Passport VO
     * @throws BusinessException 用户名或密码错误，或账户被冻结时抛出
     */
    @Override
    public PassportVo passportLogin(LoginDto dto) {
        PassportSelectDto selectDto = new PassportSelectDto();
        selectDto.setUsername(dto.getUsername());
        List<PassportPo> passports = passportDao.selectList(selectDto);
        Asserts.isTrue(Collections.isNotEmpty(passports),
            BasisErrorCode.USERNAME_OR_PASSWORD_INCORRECT
        );
        PassportPo passport = passports.getFirst();
        Asserts.isTrue(PassportStatus.NORMAL.name().equals(passport.getStatus()),
            BasisErrorCode.ACCOUNT_FROZEN
        );
        String password = passport.getPassword();
        boolean success = BasisUtils.verifyPassword(dto.getPassword(), password);
        Asserts.isTrue(success, BasisErrorCode.USERNAME_OR_PASSWORD_INCORRECT);
        return PassportConverter.INSTANCE.po2vo(passport);
    }
}
