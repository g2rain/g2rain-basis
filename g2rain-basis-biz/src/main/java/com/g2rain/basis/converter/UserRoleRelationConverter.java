package com.g2rain.basis.converter;

import com.g2rain.basis.dao.po.UserRoleRelationPo;
import com.g2rain.basis.dto.UserRoleRelationDto;
import com.g2rain.basis.vo.UserRoleRelationVo;
import com.g2rain.common.converter.CommonConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

/**
 * 用户角色关联表转换器
 * 用于Po、Vo、Dto之间的相互转换
 * 表名: user_role_relation
 *
 * @author Alpha
 */
@Mapper(uses = CommonConverter.class)
public interface UserRoleRelationConverter {

    /**
     * 单例实例，通过 {@link Mappers#getMapper(Class)} 获取 MapStruct 自动生成的实现。
     */
    UserRoleRelationConverter INSTANCE = Mappers.getMapper(UserRoleRelationConverter.class);

    /**
     * Po -> Vo
     * 自动将 createTime 和 updateTime 从 {@link LocalDateTime} 转换为 {@link String}
     */
    @Mapping(target = "createTime", source = "createTime", qualifiedByName = "localDateTimeToString")
    @Mapping(target = "updateTime", source = "updateTime", qualifiedByName = "localDateTimeToString")
    UserRoleRelationVo po2vo(UserRoleRelationPo po);

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
    UserRoleRelationPo dto2po(UserRoleRelationDto dto);
}
