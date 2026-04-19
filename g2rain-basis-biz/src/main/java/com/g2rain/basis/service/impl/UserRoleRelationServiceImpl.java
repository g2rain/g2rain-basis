package com.g2rain.basis.service.impl;

import com.g2rain.basis.converter.UserRoleRelationConverter;
import com.g2rain.basis.dao.RoleDao;
import com.g2rain.basis.dao.UserDao;
import com.g2rain.basis.dao.UserRoleRelationDao;
import com.g2rain.basis.dao.po.RolePo;
import com.g2rain.basis.dao.po.UserPo;
import com.g2rain.basis.dao.po.UserRoleRelationPo;
import com.g2rain.basis.dto.RoleAssignUsersDto;
import com.g2rain.basis.dto.UserRoleRelationDto;
import com.g2rain.basis.dto.UserRoleRelationSelectDto;
import com.g2rain.basis.dto.UserSelectDto;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.enums.RoleType;
import com.g2rain.basis.service.UserRoleRelationService;
import com.g2rain.basis.vo.UserRoleRelationVo;
import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.id.IdGenerator;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.utils.Asserts;
import com.g2rain.common.utils.Collections;
import com.g2rain.common.utils.Moments;
import com.g2rain.mybatis.pagination.PageContext;
import com.g2rain.mybatis.pagination.model.Page;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户角色关联表服务实现类
 * <p>
 * 负责用户与角色之间的关联管理，包括增删改查操作。
 * 表名: user_role_relation
 * </p>
 *
 * @author Alpha
 */
@Service(value = "userRoleRelationServiceImpl")
public class UserRoleRelationServiceImpl implements UserRoleRelationService {

    @Resource(name = "userRoleRelationDao")
    private UserRoleRelationDao userRoleRelationDao;

    @Resource(name = "roleDao")
    private RoleDao roleDao;

    @Resource(name = "userDao")
    private UserDao userDao;

    private IdGenerator idGenerator;

    @Qualifier("idGenerator")
    @Autowired(required = false)
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    /**
     * 查询用户角色关联列表
     *
     * @param selectDto 查询条件 DTO
     * @return 用户角色关联 VO 列表
     */
    @Override
    public List<UserRoleRelationVo> selectList(UserRoleRelationSelectDto selectDto) {
        return userRoleRelationDao.selectList(selectDto)
            .stream()
            .map(UserRoleRelationConverter.INSTANCE::po2vo)
            .toList();
    }

    /**
     * 分页查询用户角色关联列表
     *
     * @param selectDto 分页查询 DTO
     * @return 分页数据对象
     */
    @Override
    public PageData<UserRoleRelationVo> selectPage(PageSelectListDto<UserRoleRelationSelectDto> selectDto) {
        Page<UserRoleRelationPo> page = PageContext.of(selectDto.getPageNum(), selectDto.getPageSize(), () ->
            userRoleRelationDao.selectList(selectDto.getQuery())
        );

        List<UserRoleRelationVo> result = page.getResult()
            .stream()
            .map(UserRoleRelationConverter.INSTANCE::po2vo)
            .toList();
        return PageData.of(page.getPageNum(), page.getPageSize(), page.getTotal(), result);
    }

    /**
     * 保存用户角色关联
     * <p>若相同用户与角色已存在，则直接返回已有记录 ID；否则新增或更新关联记录</p>
     *
     * @param dto 用户角色关联 DTO
     * @return 保存后的记录 ID
     * @throws BusinessException 数据库操作失败时抛出
     */
    @Override
    public Long save(UserRoleRelationDto dto) {
        UserRoleRelationSelectDto selectDto = new UserRoleRelationSelectDto();
        selectDto.setRoleId(dto.getRoleId());
        selectDto.setUserId(dto.getUserId());
        List<UserRoleRelationPo> userRoleRelations = userRoleRelationDao.selectList(selectDto);
        if (Collections.isNotEmpty(userRoleRelations)) {
            return userRoleRelations.getFirst().getId();
        }

        // 转换 DTO 为 PO
        UserRoleRelationPo entity = UserRoleRelationConverter.INSTANCE.dto2po(dto);
        LocalDateTime now = Moments.now();
        entity.setUpdateTime(now);

        // 判断是新增还是更新
        Long id = entity.getId();

        // 更新：直接更新
        if (Objects.nonNull(id) && id != 0) {
            int success = userRoleRelationDao.update(entity);
            Asserts.greaterThan(success, 0, SystemErrorCode.UPDATE_DATA_ERROR, id);
            return entity.getId();
        }

        // 新增：使用IdGenerator生成主键
        entity.setId(idGenerator.generateId());
        entity.setCreateTime(now);
        int success = userRoleRelationDao.insert(entity);
        Asserts.greaterThan(success, 0, SystemErrorCode.CREATE_DATA_ERROR);
        return entity.getId();
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public Integer assignUsers(RoleAssignUsersDto dto) {
        Long roleId = dto.getRoleId();
        Set<Long> userIds = dto.getUserIds();
        Set<Long> deleteUserIds = dto.getDeleteUserIds();

        // 校验角色是否存在
        RolePo role = roleDao.selectById(roleId);
        Asserts.isTrue(Objects.nonNull(role), SystemErrorCode.PARAM_VAL_INVALID,
            "roleId"
        );

        // 超管角色, 需要额外判断删除的用户是否是管理员, 管理员不允许删除
        if (Collections.isNotEmpty(deleteUserIds) && RoleType.ADMIN.name().equals(role.getRoleType())) {
            UserSelectDto userSelectDto = new UserSelectDto();
            userSelectDto.setAdmin(true);
            userSelectDto.setOrganId(role.getOrganId());
            Long adminUserId = userDao.selectList(userSelectDto).stream()
                .map(UserPo::getId).findFirst().orElse(null);

            Asserts.isTrue(Objects.isNull(adminUserId) || !deleteUserIds.contains(adminUserId),
                BasisErrorCode.DEL_ADMIN_USER_UNDELETABLE
            );
        }

        if (Collections.isNotEmpty(userIds)) {
            UserRoleRelationSelectDto selectDto = new UserRoleRelationSelectDto();
            selectDto.setRoleId(roleId);
            selectDto.setUserIds(userIds);
            Set<Long> associatedUserIds = userRoleRelationDao.selectList(selectDto)
                .stream()
                .map(UserRoleRelationPo::getUserId)
                .collect(Collectors.toSet());

            userIds.removeIf(associatedUserIds::contains);
        }

        int result = 0;

        // 批量添加
        if (Collections.isNotEmpty(userIds)) {
            LocalDateTime now = Moments.now();
            List<UserRoleRelationPo> roleUsers = userIds.stream().map(userId -> {
                UserRoleRelationPo relation = new UserRoleRelationPo();
                relation.setId(idGenerator.generateId());
                relation.setCreateTime(now);
                relation.setUpdateTime(now);
                relation.setUserId(userId);
                relation.setRoleId(roleId);
                return relation;
            }).toList();

            result += userRoleRelationDao.insertMultiple(roleUsers);
        }

        // 批量删除
        if (Collections.isNotEmpty(deleteUserIds)) {
            result += userRoleRelationDao.deleteByUserIds(roleId, deleteUserIds);
        }

        return result;
    }

    /**
     * 根据用户 ID 删除用户的所有角色关联
     *
     * @param userId 用户 ID
     * @return 删除条数
     */
    @Override
    public int deleteByUserId(Long userId) {
        return userRoleRelationDao.deleteByUserId(userId);
    }
}
