package com.g2rain.basis.converter;

import com.g2rain.basis.dao.po.ControlUnitResourceRelationPo;
import com.g2rain.basis.dto.ControlUnitResourceRelationDto;
import com.g2rain.basis.dto.ControlUnitResourceRelationItemDto;
import com.g2rain.basis.vo.ControlUnitResourceRelationVo;
import com.g2rain.common.converter.CommonConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

/**
 * 权限点资源关联表转换器
 * 用于Po、Vo、Dto之间的相互转换
 * 表名: control_unit_resource_relation
 *
 * @author Alpha
 */
@Mapper(uses = CommonConverter.class)
public interface ControlUnitResourceRelationConverter {

    /**
     * 单例实例，通过 {@link Mappers#getMapper(Class)} 获取 MapStruct 自动生成的实现。
     */
    ControlUnitResourceRelationConverter INSTANCE = Mappers.getMapper(ControlUnitResourceRelationConverter.class);

    /**
     * Po -> Vo
     * 自动将 createTime 和 updateTime 从 {@link LocalDateTime} 转换为 {@link String}
     */
    @Mapping(target = "createTime", source = "createTime", qualifiedByName = "localDateTimeToString")
    @Mapping(target = "updateTime", source = "updateTime", qualifiedByName = "localDateTimeToString")
    ControlUnitResourceRelationVo po2vo(ControlUnitResourceRelationPo po);

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
    ControlUnitResourceRelationPo dto2po(ControlUnitResourceRelationDto dto);

    /**
     * ItemDto -> Po
     * 自动将 createTime 和 updateTime 从 {@link String} 转换为 {@link LocalDateTime}
     * 忽略 version 字段
     * 忽略 deleteFlag 字段
     */
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleteFlag", ignore = true)
    @Mapping(target = "controlUnitId", ignore = true)
    @Mapping(target = "createTime", source = "createTime", qualifiedByName = "stringToLocalDateTime")
    @Mapping(target = "updateTime", source = "updateTime", qualifiedByName = "stringToLocalDateTime")
    ControlUnitResourceRelationPo itemDto2po(ControlUnitResourceRelationItemDto dto);
}
