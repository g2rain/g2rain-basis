package com.g2rain.basis.service.impl;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.g2rain.basis.converter.ControlUnitResourceRelationConverter;
import com.g2rain.basis.dao.ControlUnitDao;
import com.g2rain.basis.dao.ControlUnitResourceRelationDao;
import com.g2rain.basis.dao.ResourceApiEndpointDao;
import com.g2rain.basis.dao.ResourceMenuDao;
import com.g2rain.basis.dao.ResourcePageDao;
import com.g2rain.basis.dao.ResourcePageElementDao;
import com.g2rain.basis.dao.po.ControlUnitPo;
import com.g2rain.basis.dao.po.ControlUnitResourceRelationPo;
import com.g2rain.basis.dao.po.ResourceApiEndpointPo;
import com.g2rain.basis.dao.po.ResourceMenuPo;
import com.g2rain.basis.dao.po.ResourcePageElementPo;
import com.g2rain.basis.dao.po.ResourcePagePo;
import com.g2rain.basis.dto.ControlUnitResourceRelationItemDto;
import com.g2rain.basis.dto.ControlUnitResourceRelationSelectDto;
import com.g2rain.basis.dto.ControlUnitResourceRelationsDto;
import com.g2rain.basis.dto.ResourceApiEndpointSelectDto;
import com.g2rain.basis.dto.ResourceMenuSelectDto;
import com.g2rain.basis.dto.ResourcePageElementSelectDto;
import com.g2rain.basis.dto.ResourcePageSelectDto;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.enums.ControlUnitStatus;
import com.g2rain.basis.enums.ResourceStatus;
import com.g2rain.basis.enums.ResourceType;
import com.g2rain.basis.model.ControlUnitPair;
import com.g2rain.basis.service.ControlUnitResourceRelationService;
import com.g2rain.basis.vo.ControlUnitResourceRelationVo;
import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.id.IdGenerator;
import com.g2rain.common.model.BasePo;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 权限点资源关联表服务实现类
 * <p>
 * 对应数据库表: {@code control_unit_resource_relation}
 * <p>
 * 核心功能：
 * <ul>
 *     <li>查询控制单元与资源的关联列表</li>
 *     <li>分页查询关联信息</li>
 *     <li>批量新增或更新关联关系</li>
 *     <li>删除指定关联</li>
 *     <li>跨应用关联及重复关系校验</li>
 * </ul>
 * 支持资源类型包括：菜单(MENU)、页面(PAGE)、页面元素(PAGE_ELEMENT)、接口地址(API_ENDPOINT)
 *
 * @author Alpha
 */
@Service(value = "controlUnitResourceRelationServiceImpl")
public class ControlUnitResourceRelationServiceImpl implements ControlUnitResourceRelationService {

    @Resource(name = "controlUnitResourceRelationDao")
    private ControlUnitResourceRelationDao controlUnitResourceRelationDao;

    @Resource(name = "controlUnitDao")
    private ControlUnitDao controlUnitDao;

    @Resource(name = "resourceMenuDao")
    private ResourceMenuDao resourceMenuDao;

    @Resource(name = "resourcePageDao")
    private ResourcePageDao resourcePageDao;

    @Resource(name = "resourcePageElementDao")
    private ResourcePageElementDao resourcePageElementDao;

    @Resource(name = "resourceApiEndpointDao")
    private ResourceApiEndpointDao resourceApiEndpointDao;

    private IdGenerator idGenerator;

    @Qualifier("idGenerator")
    @Autowired(required = false)
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    /**
     * 查询控制单元资源关联列表
     *
     * @param selectDto 查询条件 DTO
     * @return 控制单元资源关联 VO 列表
     */
    @Override
    public List<ControlUnitResourceRelationVo> selectList(ControlUnitResourceRelationSelectDto selectDto) {
        return controlUnitResourceRelationDao.selectList(selectDto)
            .stream()
            .map(ControlUnitResourceRelationConverter.INSTANCE::po2vo)
            .toList();
    }

    /**
     * 分页查询控制单元资源关联信息
     *
     * @param selectDto 分页查询条件 DTO
     * @return 分页数据封装 PageData
     */
    @Override
    public PageData<ControlUnitResourceRelationVo> selectPage(PageSelectListDto<ControlUnitResourceRelationSelectDto> selectDto) {
        Page<ControlUnitResourceRelationPo> page = PageContext.of(selectDto.getPageNum(), selectDto.getPageSize(), () ->
            controlUnitResourceRelationDao.selectList(selectDto.getQuery())
        );

        List<ControlUnitResourceRelationVo> result = page.getResult()
            .stream()
            .map(ControlUnitResourceRelationConverter.INSTANCE::po2vo)
            .toList();
        return PageData.of(page.getPageNum(), page.getPageSize(), page.getTotal(), result);
    }

    /**
     * 批量保存控制单元资源关联
     *
     * @param dto 批量关联 DTO
     * @return 成功插入的记录数
     * @throws BusinessException 跨应用关联或重复关系校验失败
     */
    @Override
    @Transactional
    @SuppressWarnings("null")
    public Integer save(ControlUnitResourceRelationsDto dto) {
        // 校验跨应用设置控制单元
        Long controlUnitId = dto.getControlUnitId();
        List<ControlUnitResourceRelationItemDto> createRelations = dto.getCreateRelations();
        List<ControlUnitResourceRelationItemDto> updateRelations = dto.getUpdateRelations();
        List<ControlUnitResourceRelationItemDto> deleteRelations = dto.getDeleteRelations();

        // 控制单元校验
        ControlUnitPo unit = controlUnitDao.selectById(controlUnitId);
        Asserts.isTrue(Objects.nonNull(unit), SystemErrorCode.PARAM_VAL_INVALID, controlUnitId);
        // 已发布的控制单元禁止关联控制单元
        ControlUnitStatus status = ControlUnitStatus.fromName(unit.getStatus());
        Asserts.isTrue(ControlUnitStatus.UNPUBLISHED.equals(status),
            BasisErrorCode.PUB_CONTROL_UNIT_LOCKED, controlUnitId
        );

        // 处理批量添加资源数据
        List<ControlUnitResourceRelationPo> insertRecords = null;
        if (Collections.isNotEmpty(createRelations)) {
            insertRecords = batchInsert(
                unit.getApplicationId(), controlUnitId, createRelations
            );
        }

        // 处理批量修改资源数据
        List<ControlUnitResourceRelationPo> updateRecords = null;
        if (Collections.isNotEmpty(updateRelations)) {
            updateRecords = batchUpdate(
                unit.getApplicationId(), controlUnitId, updateRelations
            );
        }

        // 设置操作数据的数量
        int result = 0;

        // 批量新增资源
        if (CollectionUtils.isNotEmpty(insertRecords)) {
            int success = controlUnitResourceRelationDao.insertMultiple(insertRecords);
            Asserts.greaterThanOrEqual(success, createRelations.size(),
                SystemErrorCode.CREATE_DATA_ERROR
            );
            result += success;
        }

        if (CollectionUtils.isNotEmpty(updateRecords)) {
            int success = controlUnitResourceRelationDao.updateBatch(updateRecords);
            Asserts.greaterThanOrEqual(success, updateRelations.size(),
                SystemErrorCode.CREATE_DATA_ERROR
            );
            result += success;
        }

        // 批量删除资源
        if (Collections.isNotEmpty(deleteRelations)) {
            Set<Long> resourceIds = deleteRelations.stream()
                .map(ControlUnitResourceRelationItemDto::getResourceId)
                .collect(Collectors.toSet());
            result += controlUnitResourceRelationDao.deleteByResourceIds(
                controlUnitId, resourceIds
            );
        }

        return result;
    }

    /**
     * 批量修改
     *
     * @param unitApplicationId 控制单元所属应用序号
     * @param controlUnitId     控制单元序号
     * @param relations         资源集合
     * @return 操作记录数量
     */
    @SuppressWarnings("null")
    private List<ControlUnitResourceRelationPo> batchUpdate(Long unitApplicationId, Long controlUnitId, List<ControlUnitResourceRelationItemDto> relations) {
        // 批量收集 ID
        List<ControlUnitPair> pairs = new ArrayList<>();
        Set<Long> elemIds = new HashSet<>();
        for (ControlUnitResourceRelationItemDto r : relations) {
            ResourceType resourceType = ResourceType.fromName(r.getResourceType());
            if (!ResourceType.PAGE_ELEMENT.equals(resourceType)) {
                continue;
            }

            elemIds.add(r.getResourceId());
            pairs.add(new ControlUnitPair(controlUnitId, r.getResourceId(), r.getResourceType()));
        }

        Map<Long, ResourcePageElementPo> elemMapping = Optional.of(elemIds).filter(ids -> !ids.isEmpty()).map(ids -> {
            ResourcePageElementSelectDto selectDto = new ResourcePageElementSelectDto();
            selectDto.setIds(ids);
            return resourcePageElementDao.selectList(selectDto).stream().collect(Collectors.toMap(
                ResourcePageElementPo::getId, Function.identity(), (e, r) -> e
            ));
        }).orElse(Map.of());

        // 批量校验已存在关系
        List<ControlUnitResourceRelationPo> existingRelations =
            controlUnitResourceRelationDao.selectByControlUnitResourcePairs(pairs);
        Map<String, Long> existingMap = existingRelations.stream().collect(Collectors.toMap(
            r -> r.getControlUnitId() + "-" + r.getResourceId() + "-" + r.getResourceType(),
            BasePo::getId,
            (existing, replacement) -> existing
        ));

        // 循环生成每条记录
        Map<Long, String> updateRecord = new HashMap<>();
        for (ControlUnitResourceRelationItemDto r : relations) {
            // 资源校验 & 获取 applicationId
            ResourcePageElementPo element = elemMapping.get(r.getResourceId());
            Asserts.isTrue(Objects.nonNull(element), SystemErrorCode.PARAM_VAL_INVALID, r.getResourceId());
            ResourceStatus.validate(r.getStatus()); // 校验页面元素状态
            Long applicationId = element.getApplicationId();

            // 跨应用校验
            Asserts.isTrue(Objects.nonNull(applicationId), SystemErrorCode.PARAM_VAL_INVALID, r.getResourceId());
            if (!applicationId.equals(unitApplicationId)) {
                throw new BusinessException(BasisErrorCode.CONTROL_UNIT_CROSS_APP_NOT_ALLOWED);
            }

            // 重复关系校验
            String key = controlUnitId + "-" + r.getResourceId() + "-" + r.getResourceType();
            Long id = existingMap.get(key);
            Asserts.isTrue(Objects.nonNull(id), SystemErrorCode.PARAM_VAL_INVALID, r.getResourceId());
            updateRecord.put(id, r.getStatus());
        }

        // 转换 DTO 为 PO
        LocalDateTime now = Moments.now();
        List<ControlUnitResourceRelationPo> records = new ArrayList<>();
        updateRecord.forEach((key, value) -> {
            ControlUnitResourceRelationPo po = new ControlUnitResourceRelationPo();
            po.setId(key);
            po.setStatus(value);
            po.setUpdateTime(now);
            records.add(po);
        });

        return records;
    }

    /**
     * 批量添加
     *
     * @param unitApplicationId 控制单元所属应用序号
     * @param controlUnitId     控制单元序号
     * @param relations         资源集合
     * @return 操作记录数量
     */
    @SuppressWarnings("null")
    private List<ControlUnitResourceRelationPo> batchInsert(Long unitApplicationId, Long controlUnitId, List<ControlUnitResourceRelationItemDto> relations) {
        // 批量收集 ID
        List<ControlUnitPair> pairs = new ArrayList<>();
        Set<Long> menuIds = new HashSet<>();
        Set<Long> pageIds = new HashSet<>();
        Set<Long> elemIds = new HashSet<>();
        Set<Long> apiIds = new HashSet<>();
        for (ControlUnitResourceRelationItemDto r : relations) {
            ResourceType resourceType = ResourceType.fromName(r.getResourceType());
            switch (resourceType) {
                case MENU -> menuIds.add(r.getResourceId());
                case PAGE -> pageIds.add(r.getResourceId());
                case PAGE_ELEMENT -> elemIds.add(r.getResourceId());
                case API_ENDPOINT -> apiIds.add(r.getResourceId());
            }
            pairs.add(new ControlUnitPair(controlUnitId, r.getResourceId(), r.getResourceType()));
        }

        // 批量查询资源
        Map<Long, ResourceMenuPo> menuMapping = Optional.of(menuIds).filter(ids -> !ids.isEmpty()).map(ids -> {
            ResourceMenuSelectDto selectDto = new ResourceMenuSelectDto();
            selectDto.setIds(ids);
            return resourceMenuDao.selectList(selectDto).stream().collect(Collectors.toMap(
                ResourceMenuPo::getId, Function.identity(), (e, r) -> e
            ));
        }).orElse(Map.of());

        Map<Long, ResourcePagePo> pageMapping = Optional.of(pageIds).filter(ids -> !ids.isEmpty()).map(ids -> {
            ResourcePageSelectDto selectDto = new ResourcePageSelectDto();
            selectDto.setIds(ids);
            return resourcePageDao.selectList(selectDto).stream().collect(Collectors.toMap(
                ResourcePagePo::getId, Function.identity(), (e, r) -> e
            ));
        }).orElse(Map.of());

        Map<Long, ResourcePageElementPo> elemMapping = Optional.of(elemIds).filter(ids -> !ids.isEmpty()).map(ids -> {
            ResourcePageElementSelectDto selectDto = new ResourcePageElementSelectDto();
            selectDto.setIds(ids);
            return resourcePageElementDao.selectList(selectDto).stream().collect(Collectors.toMap(
                ResourcePageElementPo::getId, Function.identity(), (e, r) -> e
            ));
        }).orElse(Map.of());

        Map<Long, ResourceApiEndpointPo> apiMapping = Optional.of(apiIds).filter(ids -> !ids.isEmpty()).map(ids -> {
            ResourceApiEndpointSelectDto selectDto = new ResourceApiEndpointSelectDto();
            selectDto.setApiEndpointIds(ids);
            return resourceApiEndpointDao.selectList(selectDto).stream().collect(Collectors.toMap(
                ResourceApiEndpointPo::getApiEndpointId, Function.identity(), (e, r) -> e
            ));
        }).orElse(Map.of());

        // 批量校验已存在关系
        List<ControlUnitResourceRelationPo> existingRelations =
            controlUnitResourceRelationDao.selectByControlUnitResourcePairs(pairs);

        Set<String> existingSet = existingRelations.stream().map(r ->
            r.getControlUnitId() + "-" + r.getResourceId() + "-" + r.getResourceType()
        ).collect(Collectors.toSet());

        // 循环生成每条记录
        for (ControlUnitResourceRelationItemDto r : relations) {
            // 资源校验 & 获取 applicationId
            ResourceType resourceType = ResourceType.fromName(r.getResourceType());
            Long applicationId = switch (resourceType) {
                case MENU -> {
                    ResourceMenuPo menu = menuMapping.get(r.getResourceId());
                    Asserts.isTrue(Objects.nonNull(menu), SystemErrorCode.PARAM_VAL_INVALID, r.getResourceId());
                    r.setStatus(null);// 菜单不需要状态
                    yield menu.getApplicationId();
                }
                case PAGE -> {
                    ResourcePagePo page = pageMapping.get(r.getResourceId());
                    Asserts.isTrue(Objects.nonNull(page), SystemErrorCode.PARAM_VAL_INVALID, r.getResourceId());
                    r.setStatus(null);// 页面元素不需要状态
                    yield page.getApplicationId();
                }
                case PAGE_ELEMENT -> {
                    ResourcePageElementPo element = elemMapping.get(r.getResourceId());
                    Asserts.isTrue(Objects.nonNull(element), SystemErrorCode.PARAM_VAL_INVALID, r.getResourceId());
                    ResourceStatus.validate(r.getStatus());// 校验页面元素状态
                    yield element.getApplicationId();
                }
                case API_ENDPOINT -> {
                    ResourceApiEndpointPo api = apiMapping.get(r.getResourceId());
                    Asserts.isTrue(Objects.nonNull(api), SystemErrorCode.PARAM_VAL_INVALID, r.getResourceId());
                    r.setStatus(null);// 接口地址不需要状态
                    yield api.getApplicationId();
                }
            };

            // 跨应用校验
            Asserts.isTrue(Objects.nonNull(applicationId), SystemErrorCode.PARAM_VAL_INVALID, r.getResourceId());
            if (!applicationId.equals(unitApplicationId)) {
                throw new BusinessException(BasisErrorCode.CONTROL_UNIT_CROSS_APP_NOT_ALLOWED);
            }

            // 重复关系校验
            String key = controlUnitId + "-" + r.getResourceId() + "-" + r.getResourceType();
            if (existingSet.contains(key)) {
                throw new BusinessException(SystemErrorCode.DATA_EXISTS);
            }
        }

        // 转换 DTO 为 PO
        LocalDateTime now = Moments.now();
        List<ControlUnitResourceRelationPo> records = new ArrayList<>();
        for (ControlUnitResourceRelationItemDto o : relations) {
            ControlUnitResourceRelationPo po = ControlUnitResourceRelationConverter.INSTANCE.itemDto2po(o);
            po.setControlUnitId(controlUnitId);
            po.setCreateTime(now);
            po.setUpdateTime(now);
            po.setId(idGenerator.generateId());
            records.add(po);
        }

        return records;
    }
}
