package com.g2rain.basis.converter;

import com.g2rain.basis.dao.po.RoleControlUnitRelationPo;
import com.g2rain.basis.vo.RoleControlUnitRelationVo;
import com.g2rain.common.converter.CommonConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

/**
 * 角色控制单元关联表转换器
 * 用于Po、Vo、Dto之间的相互转换
 * 表名: role_control_unit_relation
 *
 * @author Alpha
 */
@Mapper(uses = CommonConverter.class)
public interface RoleControlUnitRelationConverter {

    /**
     * 单例实例，通过 {@link Mappers#getMapper(Class)} 获取 MapStruct 自动生成的实现。
     */
    RoleControlUnitRelationConverter INSTANCE = Mappers.getMapper(RoleControlUnitRelationConverter.class);

    /**
     * Po -> Vo
     * 自动将 createTime 和 updateTime 从 {@link LocalDateTime} 转换为 {@link String}
     */
    @Mapping(target = "controlUnitName", ignore = true)
    @Mapping(target = "createTime", source = "createTime", qualifiedByName = "localDateTimeToString")
    @Mapping(target = "updateTime", source = "updateTime", qualifiedByName = "localDateTimeToString")
    RoleControlUnitRelationVo po2vo(RoleControlUnitRelationPo po);
}
