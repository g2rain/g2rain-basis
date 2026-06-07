package com.g2rain.basis.service.impl;

import com.g2rain.basis.converter.ControlUnitConverter;
import com.g2rain.basis.dao.ApplicationDao;
import com.g2rain.basis.dao.ControlDomainControlUnitRelationDao;
import com.g2rain.basis.dao.ControlUnitDao;
import com.g2rain.basis.dao.ControlUnitResourceRelationDao;
import com.g2rain.basis.dao.RoleControlUnitRelationDao;
import com.g2rain.basis.dao.po.ApplicationPo;
import com.g2rain.basis.dao.po.ControlUnitPo;
import com.g2rain.basis.dto.ControlDomainControlUnitRelationSelectDto;
import com.g2rain.basis.dto.ControlUnitDto;
import com.g2rain.basis.dto.ControlUnitSelectDto;
import com.g2rain.basis.dto.RoleControlUnitRelationSelectDto;
import com.g2rain.basis.dto.UpdateStatusDto;
import com.g2rain.basis.enums.AuthorizationStatus;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.enums.ControlUnitScope;
import com.g2rain.basis.enums.ControlUnitStatus;
import com.g2rain.basis.service.ControlUnitService;
import com.g2rain.basis.vo.ControlUnitVo;
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
 * 控制单元表服务实现类
 * <p>
 * 对应数据库表: {@code control_unit}
 * <p>
 * 核心功能：
 * <ul>
 *     <li>查询控制单元列表</li>
 *     <li>分页查询控制单元信息</li>
 *     <li>新增或更新控制单元</li>
 *     <li>删除控制单元</li>
 *     <li>应用唯一性校验</li>
 * </ul>
 *
 * @author Alpha
 */
@Service(value = "controlUnitServiceImpl")
public class ControlUnitServiceImpl implements ControlUnitService {

    @Resource(name = "controlUnitDao")
    private ControlUnitDao controlUnitDao;

    @Resource(name = "applicationDao")
    private ApplicationDao applicationDao;

    @Resource(name = "controlUnitResourceRelationDao")
    private ControlUnitResourceRelationDao controlUnitResourceRelationDao;

    @Resource(name = "roleControlUnitRelationDao")
    private RoleControlUnitRelationDao roleControlUnitRelationDao;

    @Resource(name = "controlDomainControlUnitRelationDao")
    private ControlDomainControlUnitRelationDao controlDomainControlUnitRelationDao;

    private IdGenerator idGenerator;

    @Qualifier("idGenerator")
    @Autowired(required = false)
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    /**
     * 查询控制单元列表
     *
     * @param selectDto 查询条件 DTO
     * @return 控制单元 VO 列表
     */
    @Override
    public List<ControlUnitVo> selectList(ControlUnitSelectDto selectDto) {
        return controlUnitDao.selectList(selectDto)
            .stream()
            .map(ControlUnitConverter.INSTANCE::po2vo)
            .toList();
    }

    /**
     * 分页查询控制单元信息
     *
     * @param selectDto 分页查询条件 DTO
     * @return 分页数据封装 PageData
     */
    @Override
    public PageData<ControlUnitVo> selectPage(PageSelectListDto<ControlUnitSelectDto> selectDto) {
        Page<ControlUnitPo> page = PageContext.of(selectDto.getPageNum(), selectDto.getPageSize(), () ->
            controlUnitDao.selectList(selectDto.getQuery())
        );

        List<ControlUnitVo> result = page.getResult()
            .stream()
            .map(ControlUnitConverter.INSTANCE::po2vo)
            .toList();
        return PageData.of(page.getPageNum(), page.getPageSize(), page.getTotal(), result);
    }

    /**
     * 保存控制单元信息（新增或更新）
     *
     * @param dto 控制单元 DTO
     * @return 保存后记录 ID
     * @throws BusinessException 应用不存在或名称重复时抛出
     */
    @Override
    public Long save(ControlUnitDto dto) {
        // 校验参数
        ControlUnitScope.fromName(dto.getControlUnitScope());

        // 验证应用是否存在
        ApplicationPo application = applicationDao.selectById(dto.getApplicationId());
        Asserts.isTrue(Objects.nonNull(application), SystemErrorCode.PARAM_VAL_INVALID,
            dto.getApplicationId()
        );

        // 判断是新增还是更新
        Long id = dto.getId();

        // 校验同一个应用控制单元名称唯一性
        ControlUnitSelectDto selectDto = new ControlUnitSelectDto();
        selectDto.setControlUnitName(dto.getControlUnitName());
        selectDto.setApplicationId(dto.getApplicationId());
        List<ControlUnitPo> units = controlUnitDao.selectList(selectDto);
        if (units.stream().anyMatch(o -> !o.getId().equals(id))) {
            throw new BusinessException(BasisErrorCode.PARAM_ALREADY_EXISTS,
                "controlUnitName", dto.getControlUnitName()
            );
        }

        // 转换 DTO 为 PO
        ControlUnitPo entity = ControlUnitConverter.INSTANCE.dto2po(dto);

        // 更新：直接更新
        if (Objects.nonNull(id) && id != 0) {
            entity.setUpdateTime(Moments.now());
            int success = controlUnitDao.update(entity);
            Asserts.greaterThan(success, 0, SystemErrorCode.UPDATE_DATA_ERROR, id);
            return entity.getId();
        }

        // 新增：使用IdGenerator生成主键
        entity.setId(idGenerator.generateId());
        LocalDateTime now = Moments.now();
        entity.setUpdateTime(now);
        entity.setCreateTime(now);
        entity.setStatus(ControlUnitStatus.UNPUBLISHED.name());
        int success = controlUnitDao.insert(entity);
        Asserts.greaterThan(success, 0, SystemErrorCode.CREATE_DATA_ERROR);
        return entity.getId();
    }

    /**
     * 删除指定控制单元
     *
     * @param id 控制单元 ID
     * @return 删除数量
     */
    @Override
    @Transactional
    @SuppressWarnings("null")
    public int delete(Long id) {
        // 控制单元校验
        ControlUnitPo unit = controlUnitDao.selectById(id);
        Asserts.isTrue(Objects.nonNull(unit), SystemErrorCode.PARAM_VAL_INVALID, id);
        // 已发布的控制单元禁止关联控制单元
        ControlUnitStatus status = ControlUnitStatus.fromName(unit.getStatus());
        Asserts.isTrue(ControlUnitStatus.UNPUBLISHED.equals(status),
            BasisErrorCode.PUB_CONTROL_UNIT_LOCKED, id
        );

        // 删除控制单元和资源的关联关系
        controlUnitResourceRelationDao.deleteByControlUnitId(id);
        // 删除控制单元
        return controlUnitDao.delete(id);
    }

    /**
     * 修改控制单元状态
     *
     * @param id 控制单元标识
     * @return 数据库中受影响的行数（通常为 1 表示成功，0 表示没有变化）
     * @throws BusinessException 校验失败时抛出异常
     */
    @Override
    @SuppressWarnings("null")
    public int updateStatus(Long id, UpdateStatusDto dto) {
        /*
         * 校验状态参数: 如果要修改控制单元状态为 `未发布`,
         * 需要确认[角色和控制单元的关联表是否存在激活状态 && 控制域是否关联了控制单元]
         */
        ControlUnitStatus status = ControlUnitStatus.fromName(dto.getStatus());
        if (ControlUnitStatus.UNPUBLISHED.equals(status)) {
            RoleControlUnitRelationSelectDto rcSelectDto = new RoleControlUnitRelationSelectDto();
            rcSelectDto.setControlUnitId(id);
            rcSelectDto.setStatus(AuthorizationStatus.ACTIVATED.name());
            long total = roleControlUnitRelationDao.checkRoleControlUnitExists(rcSelectDto);
            Asserts.lessThanOrEqual(total, 0, BasisErrorCode.PUB_CONTROL_UNIT_READONLY);

            ControlDomainControlUnitRelationSelectDto duSelectDto = new ControlDomainControlUnitRelationSelectDto();
            duSelectDto.setControlUnitId(id);
            total = controlDomainControlUnitRelationDao.checkControlDomainControlUnitExists(duSelectDto);
            Asserts.lessThanOrEqual(total, 0, BasisErrorCode.PUB_CONTROL_UNIT_READONLY);
        }

        ControlUnitPo entity = new ControlUnitPo();
        entity.setId(id);
        entity.setUpdateTime(Moments.now());
        entity.setStatus(dto.getStatus());
        return controlUnitDao.updateSelective(entity);
    }
}
