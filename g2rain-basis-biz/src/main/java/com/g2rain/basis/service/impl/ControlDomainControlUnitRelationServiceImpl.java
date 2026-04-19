package com.g2rain.basis.service.impl;

import com.g2rain.basis.converter.ControlDomainControlUnitRelationConverter;
import com.g2rain.basis.dao.ControlDomainControlUnitRelationDao;
import com.g2rain.basis.dao.ControlDomainDao;
import com.g2rain.basis.dao.ControlUnitDao;
import com.g2rain.basis.dao.po.ControlDomainControlUnitRelationPo;
import com.g2rain.basis.dao.po.ControlDomainPo;
import com.g2rain.basis.dao.po.ControlUnitPo;
import com.g2rain.basis.dto.ControlDomainControlUnitRelationSelectDto;
import com.g2rain.basis.dto.ControlDomainControlUnitRelationsDto;
import com.g2rain.basis.dto.ControlUnitSelectDto;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.enums.ControlUnitStatus;
import com.g2rain.basis.model.ControlDomainPair;
import com.g2rain.basis.service.ControlDomainControlUnitRelationService;
import com.g2rain.basis.vo.ControlDomainControlUnitRelationVo;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 控制域控制单元关联表服务实现类。
 *
 * <p>提供对 `control_domain_control_unit_relation` 表的 CRUD 操作及批量管理。</p>
 *
 * <p>核心功能：</p>
 * <ol>
 *     <li>查询控制域控制单元关联列表或分页列表；</li>
 *     <li>批量保存控制域控制单元关联；</li>
 *     <li>更新单条控制域控制单元关联；</li>
 *     <li>删除控制域控制单元关联；</li>
 *     <li>校验跨应用设置和重复记录。</li>
 * </ol>
 *
 * @author Alpha
 */
@Service(value = "controlDomainControlUnitRelationServiceImpl")
public class ControlDomainControlUnitRelationServiceImpl implements ControlDomainControlUnitRelationService {

    @Resource(name = "controlDomainControlUnitRelationDao")
    private ControlDomainControlUnitRelationDao controlDomainControlUnitRelationDao;

    @Resource(name = "controlDomainDao")
    private ControlDomainDao controlDomainDao;

    @Resource(name = "controlUnitDao")
    private ControlUnitDao controlUnitDao;

    private IdGenerator idGenerator;

    @Qualifier("idGenerator")
    @Autowired(required = false)
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    /**
     * 查询控制域控制单元关联列表
     *
     * @param selectDto 查询条件 DTO
     * @return 控制域控制单元关联 VO 列表
     */
    @Override
    public List<ControlDomainControlUnitRelationVo> selectList(ControlDomainControlUnitRelationSelectDto selectDto) {
        return controlDomainControlUnitRelationDao.selectList(selectDto)
            .stream()
            .map(ControlDomainControlUnitRelationConverter.INSTANCE::po2vo)
            .toList();
    }

    /**
     * 分页查询控制域控制单元关联
     *
     * @param selectDto 分页查询条件 DTO
     * @return 分页数据封装 PageData
     */
    @Override
    public PageData<ControlDomainControlUnitRelationVo> selectPage(PageSelectListDto<ControlDomainControlUnitRelationSelectDto> selectDto) {
        Page<ControlDomainControlUnitRelationPo> page = PageContext.of(selectDto.getPageNum(), selectDto.getPageSize(), () ->
            controlDomainControlUnitRelationDao.selectList(selectDto.getQuery())
        );

        List<ControlDomainControlUnitRelationVo> result = page.getResult()
            .stream()
            .map(ControlDomainControlUnitRelationConverter.INSTANCE::po2vo)
            .toList();
        return PageData.of(page.getPageNum(), page.getPageSize(), page.getTotal(), result);
    }

    /**
     * 批量保存控制域控制单元关联关系。
     *
     * <p>校验规则：</p>
     * <ol>
     *     <li>控制域与控制单元必须在同一应用内；</li>
     *     <li>已存在的记录不可重复添加。</li>
     * </ol>
     *
     * @param dto 批量关联 DTO
     * @return 成功插入的记录数量
     * @throws BusinessException 校验失败或数据已存在
     */
    @Override
    @SuppressWarnings("null")
    public Integer save(ControlDomainControlUnitRelationsDto dto) {
        Long controlDomainId = dto.getControlDomainId();
        Set<Long> controlUnitIdSet = dto.getControlUnitIds();
        Set<Long> deleteControlUnitIds = dto.getDeleteControlUnitIds();

        // 校验跨应用设置控制单元
        Set<Long> controlUnitIds = new HashSet<>();
        List<ControlDomainPair> pairs = new ArrayList<>();
        for (Long controlUnitId : controlUnitIdSet) {
            controlUnitIds.add(controlUnitId);
            pairs.add(new ControlDomainPair(controlDomainId, controlUnitId));
        }

        // 查询控制域
        ControlDomainPo domain = controlDomainDao.selectById(controlDomainId);
        Asserts.isTrue(Objects.nonNull(domain), SystemErrorCode.PARAM_VAL_INVALID,
            controlDomainId
        );

        // 查询控制单元的集合
        ControlUnitSelectDto unitSelect = new ControlUnitSelectDto();
        unitSelect.setIds(controlUnitIds);
        List<ControlUnitPo> units = controlUnitDao.selectList(unitSelect);
        Map<Long, ControlUnitPo> unitMapping = units.stream()
            .collect(Collectors.toMap(ControlUnitPo::getId, Function.identity(), (e1, e2) -> e1));

        // 查询控制域控制单元关联集合
        List<ControlDomainControlUnitRelationPo> existingRelations =
            controlDomainControlUnitRelationDao.selectByControlDomainUnitPairs(pairs);

        // 做成唯一值需要校验是否重复添加
        Set<String> existingSet = existingRelations.stream()
            .map(r -> r.getControlDomainId() + "-" + r.getControlUnitId())
            .collect(Collectors.toSet());

        // 批量校验
        for (Long controlUnitId : controlUnitIdSet) {
            ControlUnitPo unit = unitMapping.get(controlUnitId);
            Asserts.isTrue(Objects.nonNull(unit), SystemErrorCode.PARAM_VAL_INVALID, controlUnitId);

            // 未发布的控制单元不允许关联
            if (ControlUnitStatus.UNPUBLISHED.name().equals(unit.getStatus())) {
                throw new BusinessException(BasisErrorCode.ADD_CONTROL_UNIT_READONLY_ILLEGAL);
            }

            // 校验是否在同一个应用
            if (!domain.getApplicationId().equals(unit.getApplicationId())) {
                throw new BusinessException(BasisErrorCode.CONTROL_UNIT_CROSS_APP_NOT_ALLOWED);
            }

            // 校验已存在记录
            if (existingSet.contains(controlDomainId + "-" + controlUnitId)) {
                throw new BusinessException(SystemErrorCode.DATA_EXISTS);
            }
        }

        // 转换 DTO 为 PO
        LocalDateTime now = Moments.now();
        List<ControlDomainControlUnitRelationPo> records = controlUnitIdSet.stream().map(o -> {
            ControlDomainControlUnitRelationPo po = new ControlDomainControlUnitRelationPo();
            po.setCreateTime(now);
            po.setUpdateTime(now);
            po.setId(idGenerator.generateId());
            po.setControlDomainId(controlDomainId);
            po.setControlUnitId(o);
            return po;
        }).toList();

        int result = 0;

        int success = controlDomainControlUnitRelationDao.insertMultiple(records);
        Asserts.greaterThanOrEqual(success, controlUnitIdSet.size(), SystemErrorCode.CREATE_DATA_ERROR);
        result += success;

        // 删除所有的关联信息
        if (Collections.isNotEmpty(deleteControlUnitIds)) {
            result += controlDomainControlUnitRelationDao.deleteByControlDomainId(
                controlDomainId, deleteControlUnitIds
            );
        }

        return result;
    }
}
