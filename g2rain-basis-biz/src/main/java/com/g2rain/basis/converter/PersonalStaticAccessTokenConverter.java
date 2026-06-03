package com.g2rain.basis.converter;

import com.g2rain.common.converter.CommonConverter;
import com.g2rain.basis.dao.po.PersonalStaticAccessTokenPo;
import com.g2rain.basis.dto.PersonalStaticAccessTokenDto;
import com.g2rain.basis.vo.PersonalStaticAccessTokenVo;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

/**
 * 个人静态访问令牌表转换器
 * 用于Po、Vo、Dto之间的相互转换
 * 表名: personal_static_access_token
 *
 * @author G2rain Generator
 */
@Mapper(uses = CommonConverter.class)
public interface PersonalStaticAccessTokenConverter {

    /**
     * 单例实例，通过 {@link Mappers#getMapper(Class)} 获取 MapStruct 自动生成的实现。
     */
    PersonalStaticAccessTokenConverter INSTANCE = Mappers.getMapper(PersonalStaticAccessTokenConverter.class);

    /**
     * Po -> Vo
     * 自动将 createTime 和 updateTime 从 {@link LocalDateTime} 转换为 {@link String}
     */
    @Mapping(target = "userName", ignore = true)
    @BeanMapping(ignoreUnmappedSourceProperties = {"tokenHash"})
    @Mapping(target = "createTime", source = "createTime", qualifiedByName = "localDateTimeToString")
    @Mapping(target = "updateTime", source = "updateTime", qualifiedByName = "localDateTimeToString")
    PersonalStaticAccessTokenVo po2vo(PersonalStaticAccessTokenPo po);

    /**
     * Dto -> Po
     * 自动将 createTime 和 updateTime 从 {@link String} 转换为 {@link LocalDateTime}
     * 忽略 version 字段
     * 忽略 deleteFlag 字段
     */
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleteFlag", ignore = true)
    @Mapping(target = "applicationId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "organId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createTime", source = "createTime", qualifiedByName = "stringToLocalDateTime")
    @Mapping(target = "updateTime", source = "updateTime", qualifiedByName = "stringToLocalDateTime")
    PersonalStaticAccessTokenPo dto2po(PersonalStaticAccessTokenDto dto);
}
