package com.g2rain.basis.service.impl;

import com.g2rain.basis.converter.ResourceApiConverter;
import com.g2rain.basis.dao.ControlUnitResourceRelationDao;
import com.g2rain.basis.dao.ResourceApiDao;
import com.g2rain.basis.dao.ServiceRegistryDao;
import com.g2rain.basis.dao.po.ResourceApiPo;
import com.g2rain.basis.dao.po.ServiceRegistryPo;
import com.g2rain.basis.dto.ApiEndpointPairDto;
import com.g2rain.basis.dto.ControlUnitResourceRelationSelectDto;
import com.g2rain.basis.dto.ResourceApiDto;
import com.g2rain.basis.dto.ResourceApiSelectDto;
import com.g2rain.basis.dto.ServiceRegistrySelectDto;
import com.g2rain.basis.dto.UploadResourceApiDto;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.enums.BasisSyncerEnum;
import com.g2rain.basis.enums.ResourceType;
import com.g2rain.basis.service.ResourceApiService;
import com.g2rain.basis.utils.BasisUtils;
import com.g2rain.basis.utils.Constants;
import com.g2rain.basis.utils.OpenApiDocSplitter;
import com.g2rain.basis.vo.ResourceApiVo;
import com.g2rain.basis.vo.RouteDefinitionVo;
import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.id.IdGenerator;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.syncer.EventPublisherHub;
import com.g2rain.common.utils.Asserts;
import com.g2rain.common.utils.Moments;
import com.g2rain.common.utils.Strings;
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
 * 资源接口表服务实现类
 * 表名: resource_api
 *
 * @author G2rain Generator
 */
@Service(value = "resourceApiServiceImpl")
public class ResourceApiServiceImpl implements ResourceApiService {

    @Resource(name = "resourceApiDao")
    private ResourceApiDao resourceApiDao;

    @Resource(name = "serviceRegistryDao")
    private ServiceRegistryDao serviceRegistryDao;

    @Resource(name = "controlUnitResourceRelationDao")
    private ControlUnitResourceRelationDao controlUnitResourceRelationDao;

    @Resource
    private EventPublisherHub eventPublisherHub;

    private IdGenerator idGenerator;

    @Qualifier("idGenerator")
    @Autowired(required = false)
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public List<ResourceApiVo> selectList(ResourceApiSelectDto selectDto) {
        return mergeServiceNames(resourceApiDao.selectList(selectDto));
    }

    @Override
    public List<ResourceApiVo> selectListWithDocument(ResourceApiSelectDto selectDto) {
        return mergeServiceNames(resourceApiDao.selectListWithDocument(selectDto));
    }

    @Override
    public PageData<ResourceApiVo> selectPage(PageSelectListDto<ResourceApiSelectDto> selectDto) {
        Page<ResourceApiPo> page = PageContext.of(selectDto.getPageNum(), selectDto.getPageSize(), () -> {
            resourceApiDao.selectList(selectDto.getQuery());
        });

        return PageData.of(page.getPageNum(), page.getPageSize(), page.getTotal(), mergeServiceNames(page.getResult()));
    }

    @Override
    public List<RouteDefinitionVo> selectRouteDefinitions() {
        return mergeServices(resourceApiDao.selectList(new ResourceApiSelectDto()));
    }

    @Override
    public Long save(ResourceApiDto dto) {
        // 判断是新增还是更新
        Long id = dto.getId();

        // 校验资源接口唯一性
        ResourceApiSelectDto selectDto = new ResourceApiSelectDto();
        selectDto.setServiceCode(dto.getServiceCode());
        selectDto.setMethod(dto.getMethod());
        selectDto.setPath(dto.getPath());
        if (resourceApiDao.selectList(selectDto).stream().anyMatch(o -> !o.getId().equals(id))) {
            throw new BusinessException(BasisErrorCode.PARAM_ALREADY_EXISTS,
                "path", dto.getPath()
            );
        }

        // 转换 DTO 为 PO
        ResourceApiPo entity = ResourceApiConverter.INSTANCE.dto2po(dto);
        LocalDateTime now = Moments.now();
        entity.setUpdateTime(now);

        // 判断是新增还是更新
        if (Objects.isNull(id) || id == 0) {
            // 新增：使用IdGenerator生成主键
            entity.setId(idGenerator.generateId());
            entity.setCreateTime(now);
            int success = resourceApiDao.insert(entity);
            Asserts.greaterThan(success, 0, SystemErrorCode.CREATE_DATA_ERROR);

            // 广播新增`资源接口`信息
            eventPublisherHub.sendCreate(
                Constants.SYNC_OUTPUT_BINDING,
                BasisSyncerEnum.API_ROUTE.name(),
                mergeServices(List.of(entity)).getFirst()
            );

            return entity.getId();
        }

        // 更新：直接更新
        entity.setUpdateTime(Moments.now());
        int success = resourceApiDao.update(entity);
        Asserts.greaterThan(success, 0, SystemErrorCode.UPDATE_DATA_ERROR, id);

        // 广播修改`资源接口`信息
        eventPublisherHub.sendUpdate(
            Constants.SYNC_OUTPUT_BINDING,
            BasisSyncerEnum.API_ROUTE.name(),
            mergeServices(List.of(entity)).getFirst()
        );
        return entity.getId();
    }

    /**
     * 导入整份 OpenAPI / Swagger 文档：拆成单接口最小文档后按 method+path 新增或更新。
     *
     * @param serviceCode 服务编码
     * @param resourceApi 含整份文档的上传 DTO
     * @return 成功操作的记录数
     */
    @Override
    @Transactional
    public Long batchSave(String serviceCode, UploadResourceApiDto resourceApi) {
        List<OpenApiDocSplitter.SplitResult> apis = OpenApiDocSplitter.split(resourceApi.getDocument());
        Asserts.isTrue(!apis.isEmpty(), SystemErrorCode.PARAM_VAL_INVALID, "document.paths");

        List<ApiEndpointPairDto> pairs = apis.stream()
            .map(dto -> new ApiEndpointPairDto(dto.method(), dto.path()))
            .toList();

        ResourceApiSelectDto selectDto = new ResourceApiSelectDto();
        selectDto.setServiceCode(serviceCode);
        selectDto.setPairs(pairs);
        List<ResourceApiPo> existingEndpoints = resourceApiDao.selectListWithDocument(selectDto);
        Map<String, ResourceApiPo> methodPathToEntity = existingEndpoints.stream().collect(Collectors.toMap(
            e -> e.getMethod() + "||" + e.getPath(), Function.identity(), (e, r) -> e
        ));

        List<ResourceApiPo> toInsert = new ArrayList<>();
        List<ResourceApiPo> toUpdate = new ArrayList<>();
        LocalDateTime now = Moments.now();
        for (OpenApiDocSplitter.SplitResult api : apis) {
            String apiTags = api.apiTags();
            String name = api.name();
            String description = api.description();
            String openapiDocument = api.openapiDocument();
            String documentDigest = BasisUtils.sha256Hex(Objects.requireNonNullElse(openapiDocument, ""));
            ResourceApiPo ra = methodPathToEntity.get(api.method() + "||" + api.path());
            if (Objects.isNull(ra)) {
                ra = new ResourceApiPo();
                ra.setId(idGenerator.generateId());
                ra.setServiceCode(serviceCode);
                ra.setApiTags(apiTags);
                ra.setName(name);
                ra.setMethod(api.method());
                ra.setPath(api.path());
                ra.setDescription(description);
                ra.setOpenapiDocument(openapiDocument);
                ra.setDocumentDigest(documentDigest);
                ra.setCreateTime(now);
                ra.setUpdateTime(now);
                toInsert.add(ra);
                continue;
            }

            boolean metaChanged = !Strings.equals(apiTags, ra.getApiTags())
                || !Strings.equals(name, ra.getName())
                || !Strings.equals(description, ra.getDescription());
            boolean docChanged = !Strings.equals(documentDigest, ra.getDocumentDigest());
            if (metaChanged || docChanged) {
                ra.setApiTags(apiTags);
                ra.setName(name);
                ra.setDescription(description);
                ra.setOpenapiDocument(openapiDocument);
                ra.setDocumentDigest(documentDigest);
                ra.setUpdateTime(now);
                toUpdate.add(ra);
            }
        }

        long count = 0L;

        if (!toUpdate.isEmpty()) {
            count += resourceApiDao.updateBatchCase(toUpdate);
        }

        if (!toInsert.isEmpty()) {
            count += resourceApiDao.insertMultiple(toInsert);

            List<RouteDefinitionVo> routes = mergeServices(toInsert);
            for (RouteDefinitionVo route : routes) {
                eventPublisherHub.sendCreate(
                    Constants.SYNC_OUTPUT_BINDING,
                    BasisSyncerEnum.API_ROUTE.name(),
                    route
                );
            }
        }

        return count;
    }

    @Override
    public int delete(Long id) {
        ResourceApiPo resourceApi = resourceApiDao.selectById(id);
        // `资源接口` 不存在
        Asserts.isTrue(Objects.nonNull(resourceApi), SystemErrorCode.PARAM_VAL_INVALID, id);

        ControlUnitResourceRelationSelectDto selectDto = new ControlUnitResourceRelationSelectDto();
        selectDto.setResourceId(id);
        selectDto.setResourceType(ResourceType.API_ENDPOINT.name());
        long total = controlUnitResourceRelationDao.checkResourceExists(selectDto);
        Asserts.lessThanOrEqual(total, 0, BasisErrorCode.DEL_RESOURCE_EXIST_CONTROL_UNIT_UNDELETABLE);

        // 删除 `资源接口信息`
        int result = resourceApiDao.delete(id);

        // 广播删除`资源接口`信息
        eventPublisherHub.sendDelete(
            Constants.SYNC_OUTPUT_BINDING,
            BasisSyncerEnum.API_ROUTE.name(),
            mergeServices(List.of(resourceApi)).getFirst()
        );

        return result;
    }

    /**
     * 设置服务注册名称
     *
     * @param resourceApis 资源接口 PO
     * @return 资源接口 VO
     */
    private List<ResourceApiVo> mergeServiceNames(List<ResourceApiPo> resourceApis) {
        Set<String> codes = resourceApis.stream().map(ResourceApiPo::getServiceCode).collect(Collectors.toSet());
        Map<String, ServiceRegistryPo> srMap = findServiceRegistryMap(codes);

        List<ResourceApiVo> result = new ArrayList<>(resourceApis.size());
        for (ResourceApiPo resourceApi : resourceApis) {
            ResourceApiVo vo = ResourceApiConverter.INSTANCE.po2vo(resourceApi);
            ServiceRegistryPo serviceRegistry = srMap.get(resourceApi.getServiceCode());
            if (Objects.nonNull(serviceRegistry)) {
                vo.setServiceName(serviceRegistry.getName());
            }

            result.add(vo);
        }

        return result;
    }

    /**
     * 设置服务注册相关信息
     *
     * @param resourceApis 资源接口 PO
     * @return 资源接口 VO
     */
    private List<RouteDefinitionVo> mergeServices(List<ResourceApiPo> resourceApis) {
        Set<String> codes = resourceApis.stream().map(ResourceApiPo::getServiceCode).collect(Collectors.toSet());
        Map<String, ServiceRegistryPo> srMap = findServiceRegistryMap(codes);

        List<RouteDefinitionVo> result = new ArrayList<>(resourceApis.size());
        for (ResourceApiPo resourceApi : resourceApis) {
            RouteDefinitionVo vo = ResourceApiConverter.INSTANCE.po2route(resourceApi);
            ServiceRegistryPo serviceRegistry = srMap.get(resourceApi.getServiceCode());
            if (Objects.nonNull(serviceRegistry)) {
                vo.setEndpoint(serviceRegistry.getEndpoint());
                vo.setRoutePrefix(serviceRegistry.getRoutePrefix());
            }

            result.add(vo);
        }

        return result;
    }

    private Map<String, ServiceRegistryPo> findServiceRegistryMap(Set<String> serviceCodes) {
        ServiceRegistrySelectDto selectDto = new ServiceRegistrySelectDto();
        selectDto.setServiceCodes(serviceCodes);
        return serviceRegistryDao.selectList(selectDto).stream().collect(
            Collectors.toMap(ServiceRegistryPo::getServiceCode, Function.identity(), (e1, e2) -> e1)
        );
    }
}
