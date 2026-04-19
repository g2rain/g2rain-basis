package com.g2rain.basis.converter;

import com.g2rain.basis.dao.po.ApiEndpointPo;
import com.g2rain.basis.dao.po.AuthorityApiEndpointPo;
import com.g2rain.basis.dto.ApiEndpointDto;
import com.g2rain.basis.vo.ApiEndpointVo;
import com.g2rain.basis.vo.AuthorityApiEndpointVo;
import com.g2rain.common.converter.CommonConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

/**
 * 接口地址表转换器
 * 用于Po、Vo、Dto之间的相互转换
 * 表名: api_endpoint
 *
 * @author Alpha
 */
@Mapper(uses = CommonConverter.class)
public interface ApiEndpointConverter {

    /**
     * 单例实例，通过 {@link Mappers#getMapper(Class)} 获取 MapStruct 自动生成的实现。
     */
    ApiEndpointConverter INSTANCE = Mappers.getMapper(ApiEndpointConverter.class);

    /**
     * Po -> Vo
     * 自动将 createTime 和 updateTime 从 {@link LocalDateTime} 转换为 {@link String}
     */
    @Mapping(target = "createTime", source = "createTime", qualifiedByName = "localDateTimeToString")
    @Mapping(target = "updateTime", source = "updateTime", qualifiedByName = "localDateTimeToString")
    ApiEndpointVo po2vo(ApiEndpointPo po);

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
    ApiEndpointPo dto2po(ApiEndpointDto dto);

    /**
     * 将 Po 对象转换为授权 Vo 对象。
     *
     * @param po {@link AuthorityApiEndpointPo} 实体对象
     * @return 转换后的 {@link AuthorityApiEndpointVo} 授权视图对象
     */
    AuthorityApiEndpointVo po2authVo(AuthorityApiEndpointPo po);
}
