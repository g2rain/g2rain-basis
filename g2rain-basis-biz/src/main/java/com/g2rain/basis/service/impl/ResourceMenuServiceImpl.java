package com.g2rain.basis.service.impl;

import com.g2rain.basis.converter.ResourceMenuConverter;
import com.g2rain.basis.dao.ControlUnitResourceRelationDao;
import com.g2rain.basis.dao.ResourceMenuDao;
import com.g2rain.basis.dao.po.ResourceMenuPo;
import com.g2rain.basis.dto.ControlUnitResourceRelationSelectDto;
import com.g2rain.basis.dto.ResourceMenuDto;
import com.g2rain.basis.dto.ResourceMenuSelectDto;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.enums.ResourceType;
import com.g2rain.basis.service.ResourceMenuService;
import com.g2rain.basis.vo.ResourceMenuVo;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 应用资源菜单服务实现类
 * <p>
 * 核心功能：
 * <ul>
 *     <li>提供资源菜单的增删改查操作</li>
 *     <li>确保菜单编码在同一应用下唯一</li>
 *     <li>父子菜单必须属于同一应用，避免跨应用挂载</li>
 *     <li>防止菜单循环依赖</li>
 * </ul>
 * <p>
 * 对应数据库表: {@code resource_menu}
 *
 * @author Alpha
 * @since 2026/1/19
 */
@Service(value = "resourceMenuServiceImpl")
public class ResourceMenuServiceImpl implements ResourceMenuService {

    @Resource(name = "resourceMenuDao")
    private ResourceMenuDao resourceMenuDao;

    @Resource(name = "controlUnitResourceRelationDao")
    private ControlUnitResourceRelationDao controlUnitResourceRelationDao;

    private IdGenerator idGenerator;

    @Qualifier("idGenerator")
    @Autowired(required = false)
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    /**
     * 查询资源菜单列表
     *
     * @param selectDto 查询条件 DTO
     * @return 资源菜单 VO 列表
     */
    @Override
    public List<ResourceMenuVo> selectList(ResourceMenuSelectDto selectDto) {
        return resourceMenuDao.selectList(selectDto)
            .stream()
            .map(ResourceMenuConverter.INSTANCE::po2vo)
            .toList();
    }

    /**
     * 分页查询资源菜单列表
     *
     * @param selectDto 分页查询条件 DTO
     * @return 资源菜单分页数据
     */
    @Override
    public PageData<ResourceMenuVo> selectPage(PageSelectListDto<ResourceMenuSelectDto> selectDto) {
        Page<ResourceMenuPo> page = PageContext.of(selectDto.getPageNum(), selectDto.getPageSize(), () ->
            resourceMenuDao.selectList(selectDto.getQuery())
        );

        List<ResourceMenuVo> result = page.getResult()
            .stream()
            .map(ResourceMenuConverter.INSTANCE::po2vo)
            .toList();
        return PageData.of(page.getPageNum(), page.getPageSize(), page.getTotal(), result);
    }

    /**
     * 保存或更新资源菜单
     *
     * <p>逻辑说明：
     * <ul>
     *     <li>判断是新增还是更新</li>
     *     <li>父菜单与子菜单必须属于同一应用</li>
     *     <li>菜单编码在同一应用下唯一</li>
     *     <li>防止菜单循环依赖</li>
     *     <li>新增时使用 IdGenerator 生成主键</li>
     * </ul>
     *
     * @param dto 资源菜单 DTO
     * @return 保存或更新后的菜单 ID
     * @throws BusinessException 当菜单编码重复、跨应用挂载或循环依赖时抛出
     */
    @Override
    @SuppressWarnings("null")
    public Long save(ResourceMenuDto dto) {
        // 判断是新增还是更新
        Long id = dto.getId();

        // 校验 `父菜单` 和 `子菜单` 是否在同一个应用
        Optional.ofNullable(dto.getParentId()).ifPresent(parentId -> {
            ResourceMenuPo menu = resourceMenuDao.selectById(parentId);
            Asserts.isTrue(Objects.nonNull(menu), SystemErrorCode.PARAM_VAL_INVALID, parentId);
            if (!menu.getApplicationId().equals(dto.getApplicationId())) {
                throw new BusinessException(BasisErrorCode.MENU_CROSS_APP_NOT_ALLOWED);
            }
        });

        // 校验在同一个应用 `菜单编码` 唯一性
        ResourceMenuSelectDto selectDto = new ResourceMenuSelectDto();
        selectDto.setApplicationId(dto.getApplicationId());
        selectDto.setMenuCode(dto.getMenuCode());
        List<ResourceMenuPo> menus = resourceMenuDao.selectList(selectDto);
        if (menus.stream().anyMatch(o -> !o.getId().equals(id))) {
            throw new BusinessException(BasisErrorCode.PARAM_ALREADY_EXISTS,
                "menuCode", dto.getMenuCode()
            );
        }

        // 转换 DTO 为 PO
        ResourceMenuPo entity = ResourceMenuConverter.INSTANCE.dto2po(dto);
        if (Objects.isNull(id) || id == 0) {
            // 新增：使用IdGenerator生成主键
            entity.setId(idGenerator.generateId());
            LocalDateTime now = Moments.now();
            entity.setUpdateTime(now);
            entity.setCreateTime(now);
            int success = resourceMenuDao.insert(entity);
            Asserts.greaterThan(success, 0, SystemErrorCode.CREATE_DATA_ERROR);
            return entity.getId();
        }

        Optional.ofNullable(dto.getParentId()).ifPresent(parentId -> {
            ResourceMenuPo rm = resourceMenuDao.selectById(id);
            Asserts.isTrue(Objects.nonNull(rm), SystemErrorCode.PARAM_VAL_INVALID, id);

            // 父ID和当前菜单ID相同直接返回，不做循环校验
            if (parentId.equals(rm.getParentId())) {
                return;
            }

            Asserts.isTrue(!parentId.equals(dto.getId()), BasisErrorCode.MENU_SELF_RELATION_ILLEGAL);

            Long cursor = parentId;
            while (Objects.nonNull(cursor)) {
                if (cursor.equals(id)) {
                    throw new BusinessException(BasisErrorCode.CIRCULAR_DEPENDENCY_ILLEGAL);
                }
                rm = resourceMenuDao.selectById(cursor);
                if (Objects.isNull(rm)) break;
                cursor = rm.getParentId();
            }
        });

        // 更新：直接更新
        entity.setUpdateTime(Moments.now());
        int success = resourceMenuDao.update(entity);
        Asserts.greaterThan(success, 0, SystemErrorCode.UPDATE_DATA_ERROR, id);
        return entity.getId();
    }

    /**
     * 删除资源菜单
     *
     * @param id 资源菜单 ID
     * @return 删除记录数
     */
    @Override
    public int delete(Long id) {
        ResourceMenuSelectDto peSelectDto = new ResourceMenuSelectDto();
        peSelectDto.setParentId(id);
        long total = resourceMenuDao.checkMenuExists(peSelectDto);
        Asserts.lessThanOrEqual(total, 0, BasisErrorCode.DEL_MENU_EXIST_CHILD_MENU_UNDELETABLE);

        ControlUnitResourceRelationSelectDto selectDto = new ControlUnitResourceRelationSelectDto();
        selectDto.setResourceId(id);
        selectDto.setResourceType(ResourceType.MENU.name());
        total = controlUnitResourceRelationDao.checkResourceExists(selectDto);
        Asserts.lessThanOrEqual(total, 0, BasisErrorCode.DEL_RESOURCE_EXIST_CONTROL_UNIT_UNDELETABLE);
        return resourceMenuDao.delete(id);
    }
}
