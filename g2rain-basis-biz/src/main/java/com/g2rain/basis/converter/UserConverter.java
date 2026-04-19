package com.g2rain.basis.converter;

import com.g2rain.basis.dao.po.UserPo;
import com.g2rain.basis.dto.UserDto;
import com.g2rain.basis.vo.AuthorityUserVo;
import com.g2rain.basis.vo.UserVo;
import com.g2rain.common.converter.CommonConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

/**
 * 用户表转换器
 * 用于Po、Vo、Dto之间的相互转换
 * 表名: user
 *
 * @author Alpha
 */
@Mapper(uses = CommonConverter.class)
public interface UserConverter {

    /**
     * 单例实例，通过 {@link Mappers#getMapper(Class)} 获取 MapStruct 自动生成的实现。
     */
    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);

    /**
     * UserVo -> AuthorityUserVo
     */
    @Mapping(target = "passport", ignore = true)
    @Mapping(target = "organ", ignore = true)
    AuthorityUserVo vo2authorityUserVo(UserVo vo);

    /**
     * Po -> Vo
     * 自动将 createTime 和 updateTime 从 {@link LocalDateTime} 转换为 {@link String}
     */
    @Mapping(target = "createTime", source = "createTime", qualifiedByName = "localDateTimeToString")
    @Mapping(target = "updateTime", source = "updateTime", qualifiedByName = "localDateTimeToString")
    UserVo po2vo(UserPo po);

    /**
     * Dto -> Po
     * 自动将 createTime 和 updateTime 从 {@link String} 转换为 {@link LocalDateTime}
     * 忽略 version 字段
     * 忽略 deleteFlag 字段
     */
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleteFlag", ignore = true)
    @Mapping(target = "admin", ignore = true)
    @Mapping(target = "createTime", source = "createTime", qualifiedByName = "stringToLocalDateTime")
    @Mapping(target = "updateTime", source = "updateTime", qualifiedByName = "stringToLocalDateTime")
    UserPo dto2po(UserDto dto);
}
