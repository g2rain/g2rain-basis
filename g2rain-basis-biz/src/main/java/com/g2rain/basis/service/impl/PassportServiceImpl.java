package com.g2rain.basis.service.impl;

import com.g2rain.basis.converter.PassportConverter;
import com.g2rain.basis.dao.PassportDao;
import com.g2rain.basis.dao.UserDao;
import com.g2rain.basis.dao.po.PassportPo;
import com.g2rain.basis.dto.PassportDto;
import com.g2rain.basis.dto.PassportSelectDto;
import com.g2rain.basis.dto.PassportUpdatePasswordDto;
import com.g2rain.basis.dto.UpdateStatusDto;
import com.g2rain.basis.dto.UserSelectDto;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.enums.PassportStatus;
import com.g2rain.basis.enums.Sex;
import com.g2rain.basis.service.PassportService;
import com.g2rain.basis.utils.BasisUtils;
import com.g2rain.basis.vo.PassportVo;
import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.id.IdGenerator;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.utils.Asserts;
import com.g2rain.common.utils.Moments;
import com.g2rain.common.utils.Strings;
import com.g2rain.mybatis.pagination.PageContext;
import com.g2rain.mybatis.pagination.model.Page;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 账号服务实现类
 * <p>
 * 核心功能：
 * <ul>
 *     <li>提供账号（passport）的增删改查操作</li>
 *     <li>管理账号密码和状态</li>
 *     <li>校验账号唯一性及性别合法性</li>
 * </ul>
 * <p>
 * 对应数据库表: {@code passport}
 *
 * @author Alpha
 * @since 2026/1/19
 */
@Service(value = "passportServiceImpl")
public class PassportServiceImpl implements PassportService {

    @Resource(name = "passportDao")
    private PassportDao passportDao;

    @Resource
    private UserDao userDao;

    private IdGenerator idGenerator;

    @Qualifier("idGenerator")
    @Autowired(required = false)
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    /**
     * 查询账号列表
     *
     * @param selectDto 查询条件 DTO
     * @return 账号 VO 列表
     */
    @Override
    public List<PassportVo> selectList(PassportSelectDto selectDto) {
        return passportDao.selectList(selectDto)
            .stream()
            .map(PassportConverter.INSTANCE::po2vo)
            .toList();
    }

    /**
     * 根据 ID 查询账号信息
     *
     * @param id 账号 ID
     * @return 账号 VO 对象，未找到时返回 {@code null}
     */
    @Override
    public PassportVo selectById(Long id) {
        PassportPo passport = passportDao.selectById(id);
        if (passport == null) {
            return null;
        }
        return PassportConverter.INSTANCE.po2vo(passport);
    }

    /**
     * 分页查询账号列表
     *
     * @param selectDto 分页查询条件 DTO
     * @return 账号分页数据
     */
    @Override
    public PageData<PassportVo> selectPage(PageSelectListDto<PassportSelectDto> selectDto) {
        Page<PassportPo> page = PageContext.of(selectDto.getPageNum(), selectDto.getPageSize(), () ->
            passportDao.selectList(selectDto.getQuery())
        );

        List<PassportVo> result = page.getResult()
            .stream()
            .map(PassportConverter.INSTANCE::po2vo)
            .toList();
        return PageData.of(page.getPageNum(), page.getPageSize(), page.getTotal(), result);
    }

    /**
     * 保存或更新账号信息
     *
     * <p>逻辑说明：
     * <ol>
     *     <li>判断是新增还是更新</li>
     *     <li>新增时校验用户名唯一性并生成密码 hash</li>
     *     <li>更新时不允许修改用户名和密码，仅更新其他字段</li>
     * </ol>
     *
     * @param dto 账号 DTO
     * @return 保存或更新后的账号 ID
     * @throws BusinessException 新增或更新失败时抛出
     */
    @Override
    public Long save(PassportDto dto) {
        // 判断是新增还是更新
        Long id = dto.getId();
        boolean isNew = Objects.isNull(id) || id == 0;

        // 校验 新增场景 用户名是否重复
        if (isNew) {
            PassportSelectDto selectDto = new PassportSelectDto();
            selectDto.setUsername(dto.getUsername());
            long total = passportDao.checkFieldsUnique(selectDto);
            Asserts.lessThanOrEqual(total, 0, BasisErrorCode.PARAM_ALREADY_EXISTS,
                "username", dto.getUsername()
            );
        }

        // 校验性别
        Sex.validate(dto.getSex());

        // 转换DTO 为 PO
        PassportPo entity = PassportConverter.INSTANCE.dto2po(dto);

        if (!isNew) {
            // 更新：直接更新
            entity.setUpdateTime(Moments.now());
            // 不允许修改用户名
            entity.setUsername(null);
            // 不允许修改密码
            entity.setPassword(null);
            int success = passportDao.update(entity);
            Asserts.greaterThan(success, 0, SystemErrorCode.UPDATE_DATA_ERROR, id);
            return entity.getId();
        }

        // 密码必填
        Asserts.notEmpty(dto.getPassword(), SystemErrorCode.PARAM_REQUIRED, "passport");

        // 新增：使用IdGenerator生成主键
        entity.setId(idGenerator.generateId());
        LocalDateTime now = Moments.now();
        entity.setUpdateTime(now);
        entity.setCreateTime(now);
        entity.setStatus(PassportStatus.NORMAL.name());
        // 设置密码 hash 值
        entity.setPassword(BasisUtils.hashPassword(entity.getPassword()));
        int success = passportDao.insert(entity);
        Asserts.greaterThan(success, 0, SystemErrorCode.CREATE_DATA_ERROR);
        return entity.getId();
    }

    /**
     * 删除账号
     *
     * <p>逻辑说明：
     * <ul>
     *     <li>检查账号是否存在用户关联，不允许删除</li>
     *     <li>删除账号记录</li>
     * </ul>
     *
     * @param id 账号 ID
     * @return 删除记录数
     * @throws BusinessException 当账号存在用户关联时抛出
     */
    @Override
    public int delete(Long id) {
        // 存在用户的情况, 不允许删除账号
        UserSelectDto selectDto = new UserSelectDto();
        selectDto.setPassportId(id);
        long total = userDao.checkUserExists(selectDto);
        Asserts.lessThanOrEqual(total, 0, BasisErrorCode.DEL_ACCOUNT_ILLEGAL);
        return passportDao.delete(id);
    }

    /**
     * 更新账号密码
     *
     * @param id  账号 ID
     * @param dto 包含新密码的 DTO
     * @return 更新记录数
     */
    @Override
    public int updatePassword(Long id, PassportUpdatePasswordDto dto) {
        PassportPo passport = passportDao.selectById(id);
        Asserts.isTrue(Objects.nonNull(passport), SystemErrorCode.PARAM_VAL_INVALID, id);
        boolean isSame = BasisUtils.verifyPassword(dto.getOldPassword(), passport.getPassword());
        Asserts.isTrue(isSame, BasisErrorCode.OLD_PASSWORD_ILLEGAL);

        passport = new PassportPo();
        passport.setId(id);
        passport.setUpdateTime(Moments.now());
        passport.setPassword(BasisUtils.hashPassword(dto.getNewPassword()));
        return passportDao.updateSelective(passport);
    }

    /**
     * 更新账号状态
     *
     * @param id  账号 ID
     * @param dto 包含新状态的 DTO
     * @return 更新记录数
     * @throws BusinessException 当状态不合法时抛出
     */
    @Override
    public int updateStatus(Long id, UpdateStatusDto dto) {
        // 校验状态参数
        PassportStatus.validate(dto.getStatus());
        PassportPo passport = new PassportPo();
        passport.setId(id);
        passport.setUpdateTime(Moments.now());
        passport.setStatus(dto.getStatus());
        return passportDao.updateSelective(passport);
    }
}
