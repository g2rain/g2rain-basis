package com.g2rain.basis.service.impl;

import com.g2rain.basis.converter.UserConverter;
import com.g2rain.basis.dao.OrganDao;
import com.g2rain.basis.dao.PassportDao;
import com.g2rain.basis.dao.RoleDao;
import com.g2rain.basis.dao.UserDao;
import com.g2rain.basis.dao.po.OrganPo;
import com.g2rain.basis.dao.po.PassportPo;
import com.g2rain.basis.dao.po.RolePo;
import com.g2rain.basis.dao.po.UserPo;
import com.g2rain.basis.dto.OrganSelectDto;
import com.g2rain.basis.dto.UserDto;
import com.g2rain.basis.dto.UserSelectDto;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.enums.ManagerFlag;
import com.g2rain.basis.service.UserRoleRelationService;
import com.g2rain.basis.service.UserService;
import com.g2rain.basis.vo.UserOptionVo;
import com.g2rain.basis.vo.UserVo;
import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.id.IdGenerator;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.utils.Asserts;
import com.g2rain.common.utils.Moments;
import com.g2rain.common.web.PrincipalContextHolder;
import com.g2rain.mybatis.pagination.PageContext;
import com.g2rain.mybatis.pagination.model.Page;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户表服务实现类
 * <p>
 * 负责用户信息的管理，包括增删改查操作以及用户与默认角色的关联。
 * 表名: user
 * </p>
 *
 * @author Alpha
 */
@Service(value = "userServiceImpl")
public class UserServiceImpl implements UserService {

    @Resource(name = "userDao")
    private UserDao userDao;

    @Resource(name = "passportDao")
    private PassportDao passportDao;

    @Resource(name = "organDao")
    private OrganDao organDao;

    @Resource(name = "roleDao")
    private RoleDao roleDao;

    @Resource(name = "userRoleRelationServiceImpl")
    private UserRoleRelationService userRoleRelationService;

    private IdGenerator idGenerator;

    @Qualifier("idGenerator")
    @Autowired(required = false)
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    /**
     * 查询用户列表
     *
     * @param selectDto 查询条件 DTO
     * @return 用户 VO 列表
     */
    @Override
    public List<UserVo> selectList(UserSelectDto selectDto) {
        return userDao.selectList(selectDto)
            .stream()
            .map(UserConverter.INSTANCE::po2vo)
            .toList();
    }

    /**
     * 根据 ID 查询用户信息
     *
     * @param id 用户 ID
     * @return 用户 VO 对象，未找到时返回 {@code null}
     */
    @Override
    public UserVo selectById(Long id) {
        return selectById(id, userDao::selectById);
    }

    @Override
    public UserVo selectByIdWithoutIsolation(Long id) {
        return selectById(id, userDao::selectByIdWithoutIsolation);
    }

    /**
     * 分页查询用户列表
     *
     * @param selectDto 分页查询 DTO
     * @return 分页数据对象
     */
    @Override
    public PageData<UserVo> selectPage(PageSelectListDto<UserSelectDto> selectDto) {
        Page<UserPo> page = PageContext.of(selectDto.getPageNum(), selectDto.getPageSize(), () ->
            userDao.selectList(selectDto.getQuery())
        );

        List<UserVo> result = page.getResult()
            .stream()
            .map(UserConverter.INSTANCE::po2vo)
            .toList();
        return PageData.of(page.getPageNum(), page.getPageSize(), page.getTotal(), result);
    }

    /**
     * 获取用于下拉选择的用户列表
     *
     * @return 用户简要信息集合，包含用户 ID 和名称等用于展示的字段
     * @see UserOptionVo
     */
    @Override
    public List<UserOptionVo> getUserOptions() {
        Long passportId = PrincipalContextHolder.getPassportId();
        if (Objects.isNull(passportId)) {
            return List.of();
        }

        UserSelectDto userSelectDto = new UserSelectDto();
        userSelectDto.setPassportId(passportId);
        List<UserPo> users = userDao.selectList(userSelectDto);
        Set<Long> organIds = users.stream().map(UserPo::getOrganId)
            .collect(Collectors.toSet());

        OrganSelectDto organSelectDto = new OrganSelectDto();
        organSelectDto.setIds(organIds);
        Map<Long, String> id2name = organDao.selectList(organSelectDto).stream().collect(Collectors.toMap(
            OrganPo::getId, OrganPo::getOrganName, (e, r) -> e
        ));

        List<UserOptionVo> result = new ArrayList<>();
        for (UserPo user : users) {
            UserOptionVo option = new UserOptionVo();
            option.setUserId(user.getId());
            option.setUserName(user.getRealName());
            option.setOrganId(user.getOrganId());
            option.setOrganName(id2name.get(user.getOrganId()));
            result.add(option);
        }
        return result;
    }

    /**
     * 获取到角色信息, 根据角色信息找到归属的机构, 通过机构查询用户列表
     *
     * @param roleId 角色 ID
     * @return 查询用户列表
     */
    @Override
    @SuppressWarnings("null")
    public List<UserVo> selectByRole(Long roleId) {
        RolePo role = roleDao.selectById(roleId);
        Asserts.isTrue(Objects.nonNull(role), SystemErrorCode.PARAM_VAL_INVALID, roleId);
        UserSelectDto selectDto = new UserSelectDto();
        selectDto.setOrganId(role.getOrganId());
        return selectList(selectDto);
    }

    /**
     * 保存用户信息
     * 先不考虑太多场景, 如果因为并发导致存在多个管理员, 在完善分布式锁的功能
     * <p>
     * 1. 校验账号与机构是否存在；<br>
     * 2. 校验用户是否重复注册；<br>
     * 3. 新增用户时，若机构第一个用户，则自动赋予管理员身份及默认角色。
     * </p>
     *
     * @param dto 用户 DTO
     * @return 保存后的用户 ID
     * @throws BusinessException 数据库操作失败或重复注册时抛出
     */
    @Override
    @Transactional
    @SuppressWarnings("null")
    public Long save(UserDto dto) {
        return doSave(dto, userDao::selectList, userDao::update, userDao::insert);
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public Long saveWithoutIsolation(UserDto dto) {
        return doSave(dto, userDao::selectListWithoutIsolation, userDao::updateWithoutIsolation, userDao::insertWithoutIsolation);
    }

    private UserVo selectById(Long id, Function<Long, UserPo> selectFn) {
        UserPo user = selectFn.apply(id);
        if (Objects.isNull(user)) {
            return null;
        }

        return UserConverter.INSTANCE.po2vo(user);
    }

    private Long doSave(UserDto dto, Function<UserSelectDto, List<UserPo>> selectListFn, Function<UserPo, Integer> updateFn, Function<UserPo, Integer> insertFn) {
        // 校验参数
        PassportPo passport = passportDao.selectById(dto.getPassportId());
        Asserts.isTrue(Objects.nonNull(passport), SystemErrorCode.PARAM_VAL_INVALID, dto.getPassportId());
        OrganPo organ = organDao.selectById(dto.getOrganId());
        Asserts.isTrue(Objects.nonNull(organ), SystemErrorCode.PARAM_VAL_INVALID, dto.getOrganId());

        // 校验是否重复注册用户
        UserSelectDto selectDto = new UserSelectDto();
        selectDto.setPassportId(dto.getPassportId());
        selectDto.setOrganId(dto.getOrganId());
        List<UserPo> users = selectListFn.apply(selectDto);
        if (users.stream().anyMatch(o -> !Objects.equals(o.getId(), dto.getId()))) {
            throw new BusinessException(SystemErrorCode.DATA_EXISTS);
        }

        // 转换 DTO 为 PO
        UserPo entity = UserConverter.INSTANCE.dto2po(dto);

        // 判断是新增还是更新
        Long id = entity.getId();

        // 判断是新增还是更新
        if (Objects.nonNull(id) && id != 0) {
            // 更新：直接更新
            entity.setUpdateTime(Moments.now());
            int success = updateFn.apply(entity);
            Asserts.greaterThan(success, 0, SystemErrorCode.UPDATE_DATA_ERROR, id);
            return entity.getId();
        }

        // 新增的时候, 需要知道当前是否存在用户, 机构第一个用户通常都是管理员
        long total = checkUserExists(dto.getOrganId());
        // 新增：使用IdGenerator生成主键
        entity.setId(idGenerator.generateId());
        LocalDateTime now = Moments.now();
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
        entity.setAdmin(ManagerFlag.computeUserAdmin(total));
        int success = insertFn.apply(entity);
        Asserts.greaterThan(success, 0, SystemErrorCode.CREATE_DATA_ERROR);
        return entity.getId();
    }

    /**
     * 检查机构用户是否存在
     *
     * @param organId 机构 ID
     * @return 用户数量
     */
    public long checkUserExists(Long organId) {
        // 新增的时候, 需要知道当前是否存在用户, 机构第一个用户通常都是管理员
        UserSelectDto userSelect = new UserSelectDto();
        userSelect.setOrganId(organId);
        return userDao.checkUserExists(userSelect);
    }

    /**
     * 删除用户及其关联角色
     *
     * @param id 用户 ID
     * @return 删除条数
     */
    @Override
    @Transactional
    @SuppressWarnings("null")
    public int delete(Long id) {
        UserPo user = userDao.selectById(id);
        Asserts.isTrue(Objects.nonNull(user), SystemErrorCode.PARAM_VAL_INVALID, id);
        // 禁止删除管理员
        Asserts.isTrue(!Boolean.TRUE.equals(user.getAdmin()),
            BasisErrorCode.DEL_ADMIN_USER_UNDELETABLE
        );

        // 删除用户角色
        userRoleRelationService.deleteByUserId(id);
        // 删除用户
        return userDao.delete(id);
    }
}
