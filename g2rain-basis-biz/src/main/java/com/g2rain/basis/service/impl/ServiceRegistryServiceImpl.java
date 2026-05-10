package com.g2rain.basis.service.impl;

import com.g2rain.basis.converter.ResourceApiConverter;
import com.g2rain.basis.converter.ServiceRegistryConverter;
import com.g2rain.basis.dao.ResourceApiDao;
import com.g2rain.basis.dao.ServiceRegistryDao;
import com.g2rain.basis.dao.po.ResourceApiPo;
import com.g2rain.basis.dao.po.ServiceRegistryPo;
import com.g2rain.basis.dto.ResourceApiSelectDto;
import com.g2rain.basis.dto.ServiceRegistryDto;
import com.g2rain.basis.dto.ServiceRegistrySelectDto;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.enums.BasisSyncerEnum;
import com.g2rain.basis.service.ServiceRegistryService;
import com.g2rain.basis.utils.Constants;
import com.g2rain.basis.vo.RouteDefinitionVo;
import com.g2rain.basis.vo.ServiceRegistryVo;
import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.id.IdGenerator;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.syncer.EventPublisherHub;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 服务注册表服务实现类
 * 表名: service_registry
 *
 * @author G2rain Generator
 */
@Service(value = "serviceRegistryServiceImpl")
public class ServiceRegistryServiceImpl implements ServiceRegistryService {

    @Resource(name = "serviceRegistryDao")
    private ServiceRegistryDao serviceRegistryDao;

    @Resource
    private ResourceApiDao resourceApiDao;

    @Resource
    private EventPublisherHub eventPublisherHub;

    private IdGenerator idGenerator;

    @Qualifier("idGenerator")
    @Autowired(required = false)
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public List<ServiceRegistryVo> selectList(ServiceRegistrySelectDto selectDto) {
        return serviceRegistryDao.selectList(selectDto)
            .stream()
            .map(ServiceRegistryConverter.INSTANCE::po2vo)
            .toList();
    }

    @Override
    public PageData<ServiceRegistryVo> selectPage(PageSelectListDto<ServiceRegistrySelectDto> selectDto) {
        Page<ServiceRegistryPo> page = PageContext.of(selectDto.getPageNum(), selectDto.getPageSize(), () -> {
            serviceRegistryDao.selectList(selectDto.getQuery());
        });
        List<ServiceRegistryVo> result = page.getResult()
            .stream()
            .map(ServiceRegistryConverter.INSTANCE::po2vo)
            .toList();
        return PageData.of(page.getPageNum(), page.getPageSize(), page.getTotal(), result);
    }

    @Override
    public Long save(ServiceRegistryDto dto) {
        // 判断是新增还是更新
        Long id = dto.getId();

        // 校验服务注册编码唯一性
        Optional.ofNullable(dto.getServiceCode())
            .filter(Strings::isNotBlank)
            .map(code -> {
                ServiceRegistrySelectDto selectDto = new ServiceRegistrySelectDto();
                selectDto.setServiceCode(code);
                return serviceRegistryDao.selectList(selectDto);
            })
            .filter(Collections::isNotEmpty)
            .ifPresent(apps -> {
                if (apps.stream().anyMatch(o -> !o.getId().equals(id))) {
                    throw new BusinessException(BasisErrorCode.PARAM_ALREADY_EXISTS,
                        "serviceCode", dto.getServiceCode()
                    );
                }
            });

        // 校验网关路由前缀唯一性
        ServiceRegistrySelectDto selectDto = new ServiceRegistrySelectDto();
        selectDto.setRoutePrefix(dto.getRoutePrefix());
        if (serviceRegistryDao.selectList(selectDto).stream().anyMatch(o -> !o.getId().equals(id))) {
            throw new BusinessException(BasisErrorCode.PARAM_ALREADY_EXISTS,
                "routePrefix", dto.getRoutePrefix()
            );
        }

        // 转换 DTO 为 PO
        ServiceRegistryPo entity = ServiceRegistryConverter.INSTANCE.dto2po(dto);
        LocalDateTime now = Moments.now();
        entity.setUpdateTime(now);

        // 判断是新增还是更新
        if (Objects.isNull(id) || id == 0) {
            // 新增：使用IdGenerator生成主键
            entity.setId(idGenerator.generateId());
            entity.setCreateTime(now);
            int success = serviceRegistryDao.insert(entity);
            Asserts.greaterThan(success, 0, SystemErrorCode.CREATE_DATA_ERROR);

            // 广播新增`服务注册`信息
            eventPublisherHub.sendCreate(
                Constants.SYNC_OUTPUT_BINDING,
                BasisSyncerEnum.INTERNAL_ROUTE.name(),
                ServiceRegistryConverter.INSTANCE.po2vo(entity)
            );

            return entity.getId();
        }

        ServiceRegistryPo srvRegistry = serviceRegistryDao.selectById(id);

        // 更新：直接更新
        int success = serviceRegistryDao.update(entity);
        Asserts.greaterThan(success, 0, SystemErrorCode.UPDATE_DATA_ERROR, id);

        boolean endpointSame = Strings.equals(srvRegistry.getEndpoint(), dto.getEndpoint());
        boolean prefixSame = Strings.equals(srvRegistry.getRoutePrefix(), dto.getRoutePrefix());
        if (!endpointSame || !prefixSame) {
            // 广播修改`服务注册`信息
            eventPublisherHub.sendUpdate(
                Constants.SYNC_OUTPUT_BINDING,
                BasisSyncerEnum.INTERNAL_ROUTE.name(),
                ServiceRegistryConverter.INSTANCE.po2vo(entity)
            );

            // 广播修改`资源接口`信息
            ResourceApiSelectDto select = new ResourceApiSelectDto();
            select.setServiceCode(srvRegistry.getServiceCode());
            List<ResourceApiPo> apis = resourceApiDao.selectList(select);
            for (ResourceApiPo api : apis) {
                RouteDefinitionVo routeDefinition = ResourceApiConverter.INSTANCE.po2route(api);
                routeDefinition.setEndpoint(srvRegistry.getEndpoint());
                routeDefinition.setRoutePrefix(srvRegistry.getRoutePrefix());
                eventPublisherHub.sendUpdate(
                    Constants.SYNC_OUTPUT_BINDING,
                    BasisSyncerEnum.API_ROUTE.name(),
                    routeDefinition
                );
            }
        }

        return entity.getId();
    }

    @Override
    public int delete(Long id) {
        ServiceRegistryPo serviceRegistry = serviceRegistryDao.selectById(id);
        // `服务注册` 不存在
        Asserts.isTrue(Objects.nonNull(serviceRegistry), SystemErrorCode.PARAM_VAL_INVALID, id);

        ResourceApiSelectDto selectDto = new ResourceApiSelectDto();
        selectDto.setServiceCode(serviceRegistry.getServiceCode());
        List<ResourceApiPo> resourceApis = resourceApiDao.selectList(selectDto);
        // `资源接口` 已存在
        Asserts.isTrue(Collections.isEmpty(resourceApis), BasisErrorCode.RESOURCE_API_EXISTS_ILLEGAL);

        // 删除 `服务注册信息`
        int result = serviceRegistryDao.delete(id);

        // 广播删除`服务注册`信息
        eventPublisherHub.sendDelete(
            Constants.SYNC_OUTPUT_BINDING,
            BasisSyncerEnum.INTERNAL_ROUTE.name(),
            ServiceRegistryConverter.INSTANCE.po2vo(serviceRegistry)
        );

        return result;
    }
}
