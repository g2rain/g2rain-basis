package com.g2rain.basis.converter;

import com.g2rain.basis.dao.po.ResourceApiEndpointPo;
import com.g2rain.basis.dao.po.ResourceApiPo;
import com.g2rain.basis.dto.ResourceApiEndpointDto;
import com.g2rain.basis.vo.ResourceApiEndpointVo;
import com.g2rain.basis.vo.ResourceApiVo;
import com.g2rain.common.converter.CommonConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

/**
 * 应用资源接口地址表转换器
 * 用于Po、Vo、Dto之间的相互转换
 * 表名: resource_api_endpoint
 *
 * @author Alpha
 */
@Mapper(uses = CommonConverter.class)
public interface ResourceApiEndpointConverter {

    /**
     * 单例实例，通过 {@link Mappers#getMapper(Class)} 获取 MapStruct 自动生成的实现。
     */
    ResourceApiEndpointConverter INSTANCE = Mappers.getMapper(ResourceApiEndpointConverter.class);

    /**
     * Po -> Vo
     * 自动将 createTime 和 updateTime 从 {@link LocalDateTime} 转换为 {@link String}
     */
    @Mapping(target = "createTime", source = "createTime", qualifiedByName = "localDateTimeToString")
    @Mapping(target = "updateTime", source = "updateTime", qualifiedByName = "localDateTimeToString")
    ResourceApiEndpointVo po2vo(ResourceApiEndpointPo po);

    /**
     * Dto -> Po
     * 自动将 createTime 和 updateTime 从 {@link String} 转换为 {@link LocalDateTime}
     * 忽略 version 字段
     * 忽略 deleteFlag 字段
     */
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleteFlag", ignore = true)
    @Mapping(target = "createTime", source = "createTime", qualifiedByName = "stringToLocalDateTime")
    @Mapping(target = "updateTime", source = "updateTime", qualifiedByName = "stringToLocalDateTime")
    ResourceApiEndpointPo dto2po(ResourceApiEndpointDto dto);

    /**
     * Po -> Vo
     * 自动将 createTime 和 updateTime 从 {@link LocalDateTime} 转换为 {@link String}
     */
    @Mapping(target = "createTime", source = "createTime", qualifiedByName = "localDateTimeToString")
    @Mapping(target = "updateTime", source = "updateTime", qualifiedByName = "localDateTimeToString")
    ResourceApiVo apiPo2vo(ResourceApiPo po);
}
