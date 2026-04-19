package com.g2rain.basis.service.impl;

import com.g2rain.basis.converter.ResourceApiEndpointConverter;
import com.g2rain.basis.dao.ApiEndpointDao;
import com.g2rain.basis.dao.ControlUnitResourceRelationDao;
import com.g2rain.basis.dao.ResourceApiEndpointDao;
import com.g2rain.basis.dao.po.ApiEndpointPo;
import com.g2rain.basis.dao.po.ResourceApiEndpointPo;
import com.g2rain.basis.dao.po.ResourceApiPo;
import com.g2rain.basis.dto.ApiEndpointPairDto;
import com.g2rain.basis.dto.ApiEndpointSelectDto;
import com.g2rain.basis.dto.ControlUnitResourceRelationSelectDto;
import com.g2rain.basis.dto.ResourceApiEndpointDto;
import com.g2rain.basis.dto.ResourceApiEndpointSelectDto;
import com.g2rain.basis.dto.ResourceApiSelectDto;
import com.g2rain.basis.dto.UploadResourceApiEndpointDto;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.enums.ResourceType;
import com.g2rain.basis.service.ResourceApiEndpointService;
import com.g2rain.basis.vo.ResourceApiVo;
import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.id.IdGenerator;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.utils.Asserts;
import com.g2rain.common.utils.Collections;
import com.g2rain.common.utils.Moments;
import com.g2rain.common.utils.Strings;
import com.g2rain.mybatis.pagination.PageContext;
import com.g2rain.mybatis.pagination.model.Page;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 应用资源接口地址服务实现类
 * <p>
 * 核心功能：
 * <ul>
 *     <li>提供应用接口地址（API Endpoint）的增删改查操作</li>
 *     <li>确保同一应用下接口地址唯一性</li>
 * </ul>
 * <p>
 * 对应数据库表: {@code resource_api_endpoint}
 *
 * @author Alpha
 * @since 2026/1/19
 */
@Service(value = "resourceApiEndpointServiceImpl")
public class ResourceApiEndpointServiceImpl implements ResourceApiEndpointService {

    @Resource(name = "resourceApiEndpointDao")
    private ResourceApiEndpointDao resourceApiEndpointDao;

    @Resource(name = "apiEndpointDao")
    private ApiEndpointDao apiEndpointDao;

    @Resource(name = "controlUnitResourceRelationDao")
    private ControlUnitResourceRelationDao controlUnitResourceRelationDao;

    private IdGenerator idGenerator;

    @Qualifier("idGenerator")
    @Autowired(required = false)
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    /**
     * 查询应用接口地址列表
     *
     * @param selectDto 查询条件 DTO
     * @return 应用接口地址 VO 列表
     */
    @Override
    public List<ResourceApiVo> selectList(ResourceApiSelectDto selectDto) {
        return resourceApiEndpointDao.selectListWithApiEndpoint(selectDto)
            .stream()
            .map(ResourceApiEndpointConverter.INSTANCE::apiPo2vo)
            .toList();
    }

    /**
     * 分页查询应用接口地址列表
     *
     * @param selectDto 分页查询条件 DTO
     * @return 应用接口地址分页数据
     */
    @Override
    public PageData<ResourceApiVo> selectPage(PageSelectListDto<ResourceApiSelectDto> selectDto) {
        Page<ResourceApiPo> page = PageContext.of(selectDto.getPageNum(), selectDto.getPageSize(), () ->
            resourceApiEndpointDao.selectListWithApiEndpoint(selectDto.getQuery())
        );

        List<ResourceApiVo> result = page.getResult()
            .stream()
            .map(ResourceApiEndpointConverter.INSTANCE::apiPo2vo)
            .toList();
        return PageData.of(page.getPageNum(), page.getPageSize(), page.getTotal(), result);
    }

    /**
     * 保存或更新应用接口地址
     *
     * <p>逻辑说明：
     * <ul>
     *     <li>判断是新增还是更新</li>
     *     <li>校验同一应用下接口地址唯一性</li>
     *     <li>新增时使用 IdGenerator 生成主键</li>
     * </ul>
     *
     * @param dto 应用接口地址 DTO
     * @return 保存或更新后的接口地址 ID
     * @throws BusinessException 当接口地址已存在或操作失败时抛出
     */
    @Override
    public Long save(ResourceApiEndpointDto dto) {
        // 判断是新增还是更新
        Long id = dto.getId();

        // 校验 同一应用 `接口地址` 唯一性
        ResourceApiEndpointSelectDto selectDto = new ResourceApiEndpointSelectDto();
        selectDto.setApplicationId(dto.getApplicationId());
        selectDto.setApiEndpointId(dto.getApiEndpointId());
        List<ResourceApiEndpointPo> endpoints = resourceApiEndpointDao.selectList(selectDto);
        if (endpoints.stream().anyMatch(o -> !o.getId().equals(id))) {
            throw new BusinessException(BasisErrorCode.PARAM_ALREADY_EXISTS,
                "apiEndpointId", dto.getApiEndpointId()
            );
        }

        // 转换 DTO 为 PO
        ResourceApiEndpointPo entity = ResourceApiEndpointConverter.INSTANCE.dto2po(dto);

        // 更新：直接更新
        if (Objects.nonNull(id) && id != 0) {
            entity.setUpdateTime(Moments.now());
            int success = resourceApiEndpointDao.update(entity);
            Asserts.greaterThan(success, 0, SystemErrorCode.UPDATE_DATA_ERROR, id);
            return entity.getId();
        }

        // 新增：使用IdGenerator生成主键
        entity.setId(idGenerator.generateId());
        LocalDateTime now = Moments.now();
        entity.setUpdateTime(now);
        entity.setCreateTime(now);
        int success = resourceApiEndpointDao.insert(entity);
        Asserts.greaterThan(success, 0, SystemErrorCode.CREATE_DATA_ERROR);
        return entity.getId();
    }

    /**
     * 批量新增或更新 API 接口数据
     *
     * <p>逻辑说明：
     * <ul>
     *     <li>遍历接口列表，判断是新增还是更新</li>
     *     <li>同一应用下接口编码唯一，重复时抛异常</li>
     *     <li>新增时使用 IdGenerator 生成主键</li>
     *     <li>更新时修改已有记录的属性值</li>
     * </ul>
     *
     * @param applicationId 应用 ID
     * @param apiEndpoints  API 接口数据列表
     * @return 成功操作的记录数
     * @throws BusinessException 当接口编码重复或字段缺失时抛出
     */
    @Override
    public Long batchSave(Long applicationId, List<UploadResourceApiEndpointDto> apiEndpoints) {
        if (Collections.isEmpty(apiEndpoints)) {
            return 0L;
        }

        // 1. 校验 DTO 必填字段
        for (UploadResourceApiEndpointDto dto : apiEndpoints) {
            if (Strings.isBlank(dto.getApiUrl())) {
                throw new BusinessException(SystemErrorCode.PARAM_VAL_INVALID, "apiUrl");
            }

            if (Strings.isBlank(dto.getRequestMethod())) {
                throw new BusinessException(SystemErrorCode.PARAM_VAL_INVALID, "requestMethod");
            }
        }

        // 2. 查询应用下已有接口，一次性获取
        List<ApiEndpointPairDto> pairs = apiEndpoints.stream()
            .map(dto -> new ApiEndpointPairDto(dto.getApiUrl(), dto.getRequestMethod()))
            .toList();

        ApiEndpointSelectDto selectDto = new ApiEndpointSelectDto();
        selectDto.setPairs(pairs); // DAO 支持批量查询
        List<ApiEndpointPo> existingEndpoints = apiEndpointDao.selectList(selectDto);
        if (Collections.isEmpty(existingEndpoints)) {
            return 0L; // 如果接口表中没有对应的接口，则无需插入
        }

        Set<Long> apiEndpointIds = existingEndpoints.stream().map(ApiEndpointPo::getId).collect(Collectors.toSet());

        // 3. 查询关联表，获取已存在的 applicationId + apiEndpointId
        ResourceApiEndpointSelectDto select = new ResourceApiEndpointSelectDto();
        select.setApplicationId(applicationId);
        select.setApiEndpointIds(apiEndpointIds); // DAO 支持批量查询
        List<ResourceApiEndpointPo> existingResourceEndpoints = resourceApiEndpointDao.selectList(select);

        // 已关联 map
        Set<Long> existEndpointIds = existingResourceEndpoints.stream()
            .map(ResourceApiEndpointPo::getApiEndpointId)
            .collect(Collectors.toSet());

        Map<String, Long> urlMethodToId = existingEndpoints.stream()
            .collect(Collectors.toMap(
                e -> e.getApiUrl() + "||" + e.getRequestMethod(),
                ApiEndpointPo::getId, (e1, e2) -> e1
            ));

        List<ResourceApiEndpointPo> toInsert = new ArrayList<>();
        LocalDateTime now = Moments.now();

        for (UploadResourceApiEndpointDto dto : apiEndpoints) {
            Long endpointId = urlMethodToId.get(dto.getApiUrl() + "||" + dto.getRequestMethod());
            if (Objects.isNull(endpointId) || existEndpointIds.contains(endpointId)) {
                continue; // 接口不存在或已关联，跳过
            }

            ResourceApiEndpointPo entity = new ResourceApiEndpointPo();
            entity.setId(idGenerator.generateId());
            entity.setCreateTime(now);
            entity.setUpdateTime(now);
            entity.setApplicationId(applicationId);
            entity.setApiEndpointId(endpointId);
            toInsert.add(entity);
        }

        long count = 0L;
        // 批量插入
        if (!toInsert.isEmpty()) {
            count += resourceApiEndpointDao.insertMultiple(toInsert);
        }

        return count;
    }

    /**
     * 删除接口地址
     *
     * @param id 接口地址 ID
     * @return 删除记录数
     */
    @Override
    public int delete(Long id) {
        ControlUnitResourceRelationSelectDto selectDto = new ControlUnitResourceRelationSelectDto();
        selectDto.setResourceId(id);
        selectDto.setResourceType(ResourceType.API_ENDPOINT.name());
        long total = controlUnitResourceRelationDao.checkResourceExists(selectDto);
        Asserts.lessThanOrEqual(total, 0, BasisErrorCode.DEL_RESOURCE_EXIST_CONTROL_UNIT_UNDELETABLE);
        return resourceApiEndpointDao.delete(id);
    }
}
