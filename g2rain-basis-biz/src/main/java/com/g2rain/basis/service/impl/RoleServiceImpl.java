package com.g2rain.basis.service.impl;

import com.g2rain.basis.converter.RoleConverter;
import com.g2rain.basis.dao.RoleControlUnitRelationDao;
import com.g2rain.basis.dao.RoleDao;
import com.g2rain.basis.dao.UserRoleRelationDao;
import com.g2rain.basis.dao.po.RolePo;
import com.g2rain.basis.dto.RoleDto;
import com.g2rain.basis.dto.RoleSelectDto;
import com.g2rain.basis.dto.UserRoleRelationSelectDto;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.enums.RoleType;
import com.g2rain.basis.service.RoleService;
import com.g2rain.basis.vo.RoleVo;
import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.id.IdGenerator;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.utils.Asserts;
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

/**
 * 角色表服务实现类
 * 表名: role
 *
 * @author Alpha
 */
@Service(value = "roleServiceImpl")
public class RoleServiceImpl implements RoleService {

    @Resource(name = "roleDao")
    private RoleDao roleDao;

    @Resource(name = "userRoleRelationDao")
    private UserRoleRelationDao userRoleRelationDao;

    @Resource(name = "roleControlUnitRelationDao")
    private RoleControlUnitRelationDao roleControlUnitRelationDao;

    private IdGenerator idGenerator;

    @Qualifier("idGenerator")
    @Autowired(required = false)
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return VO 对象列表
     */
    @Override
    public List<RoleVo> selectList(RoleSelectDto selectDto) {
        return roleDao.selectList(selectDto)
            .stream()
            .map(RoleConverter.INSTANCE::po2vo)
            .toList();
    }

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页 VO 数据
     */
    @Override
    public PageData<RoleVo> selectPage(PageSelectListDto<RoleSelectDto> selectDto) {
        Page<RolePo> page = PageContext.of(selectDto.getPageNum(), selectDto.getPageSize(), () ->
            roleDao.selectList(selectDto.getQuery())
        );

        List<RoleVo> result = page.getResult()
            .stream()
            .map(RoleConverter.INSTANCE::po2vo)
            .toList();
        return PageData.of(page.getPageNum(), page.getPageSize(), page.getTotal(), result);
    }

    /**
     * 新增或更新数据
     *
     * @param dto 数据传输对象
     * @return 操作结果（影响行数）
     */
    @Override
    public Long save(RoleDto dto) {
        return internalSave(RoleType.USER, dto);
    }

    /**
     * 保存角色信息，支持新增和更新操作。
     *
     * <p>内部使用的接口</p>
     * <p>根据传入的角色信息判断是进行角色信息的新增还是更新</p>
     *
     * <p>此外，还会根据传入的角色类型（roleType）设置角色类型，并进行数据库插入或更新。</p>
     *
     * @param roleType 角色类型，指定角色的类型（如管理员、普通用户等）
     * @param dto      角色信息的数据传输对象，包含角色的详细信息
     * @return 返回保存的角色 ID
     * @throws BusinessException 如果角色插入或更新失败，将抛出相应的业务异常
     */
    @Override
    public Long internalSave(RoleType roleType, RoleDto dto) {
        // 转换 DTO 为 PO
        RolePo entity = RoleConverter.INSTANCE.dto2po(dto);

        // 判断是新增还是更新
        Long id = entity.getId();

        // 更新：直接更新
        if (Objects.nonNull(id) && id != 0) {
            // 更新：直接更新
            entity.setUpdateTime(Moments.now());
            int success = roleDao.update(entity);
            Asserts.greaterThan(success, 0, SystemErrorCode.UPDATE_DATA_ERROR, id);
            return entity.getId();
        }

        // 新增：使用IdGenerator生成主键
        entity.setId(idGenerator.generateId());
        LocalDateTime now = Moments.now();
        entity.setUpdateTime(now);
        entity.setCreateTime(now);
        entity.setRoleType(roleType.name());
        int success = roleDao.insert(entity);
        Asserts.greaterThan(success, 0, SystemErrorCode.CREATE_DATA_ERROR);
        return entity.getId();
    }

    /**
     * 根据 ID 删除数据
     *
     * @param id 主键 ID
     * @return 操作结果（影响行数）
     */
    @Override
    @Transactional
    @SuppressWarnings("null")
    public int delete(Long id) {
        RolePo role = roleDao.selectById(id);
        // `角色` 不存在
        if (Objects.isNull(role)) {
            return 0;
        }

        // `超管角色` 不允许删除
        Asserts.isTrue(!RoleType.ADMIN.name().equals(role.getRoleType()),
            BasisErrorCode.DEL_ADMIN_ROLE_ILLEGAL
        );

        // 校验 是否有用户在使用角色, 如果存在不允许删除
        UserRoleRelationSelectDto relationDto = new UserRoleRelationSelectDto();
        relationDto.setRoleId(id);
        long total = userRoleRelationDao.checkUserRoleExists(relationDto);
        Asserts.lessThanOrEqual(total, 0, BasisErrorCode.DEL_ROLE_ILLEGAL);

        // 删除角色和控制单元关联信息
        roleControlUnitRelationDao.deleteByRoleId(id);

        // 删除角色
        return roleDao.delete(id);
    }
}
