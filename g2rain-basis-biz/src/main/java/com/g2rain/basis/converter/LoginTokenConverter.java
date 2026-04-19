package com.g2rain.basis.converter;

import com.g2rain.basis.dao.po.LoginTokenPo;
import com.g2rain.basis.dto.LoginTokenDto;
import com.g2rain.basis.vo.LoginTokenVo;
import com.g2rain.common.converter.CommonConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

/**
 * 登录信息表, 记录了当前登录状态的相关信息转换器
 * 用于Po、Vo、Dto之间的相互转换
 * 表名: login_token
 *
 * @author Alpha
 */
@Mapper(uses = CommonConverter.class)
public interface LoginTokenConverter {

    /**
     * 单例实例，通过 {@link Mappers#getMapper(Class)} 获取 MapStruct 自动生成的实现。
     */
    LoginTokenConverter INSTANCE = Mappers.getMapper(LoginTokenConverter.class);

    /**
     * Po -> Vo
     * 自动将 createTime 和 updateTime 从 {@link LocalDateTime} 转换为 {@link String}
     */
    @Mapping(target = "createTime", source = "createTime", qualifiedByName = "localDateTimeToString")
    @Mapping(target = "updateTime", source = "updateTime", qualifiedByName = "localDateTimeToString")
    LoginTokenVo po2vo(LoginTokenPo po);

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
    LoginTokenPo dto2po(LoginTokenDto dto);
}
