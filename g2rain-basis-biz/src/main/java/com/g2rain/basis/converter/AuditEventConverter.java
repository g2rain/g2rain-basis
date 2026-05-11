package com.g2rain.basis.converter;

import com.g2rain.basis.dao.po.AuditEventPo;
import com.g2rain.basis.dto.AuditEventDto;
import com.g2rain.basis.vo.AuditEventVo;
import com.g2rain.common.converter.CommonConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 审计事件表转换器
 * 用于Po、Vo、Dto之间的相互转换
 * 表名: audit_event
 *
 * @author G2rain Generator
 */
@Mapper(uses = CommonConverter.class)
public interface AuditEventConverter {

    /**
     * 单例实例，通过 {@link Mappers#getMapper(Class)} 获取 MapStruct 自动生成的实现。
     */
    AuditEventConverter INSTANCE = Mappers.getMapper(AuditEventConverter.class);

    /**
     * Po -> Vo
     * 自动将 createTime 和 updateTime 从 {@link LocalDateTime} 转换为 {@link String}
     */
    @Mapping(target = "createTime", source = "createTime", qualifiedByName = "localDateTimeToString")
    @Mapping(target = "updateTime", source = "updateTime", qualifiedByName = "localDateTimeToString")
    AuditEventVo po2vo(AuditEventPo po);

    /**
     * Dto -> Po
     * 自动将 createTime 和 updateTime 从 {@link String} 转换为 {@link LocalDateTime}
     * 忽略 version 字段
     */
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createTime", source = "createTime", qualifiedByName = "stringToLocalDateTime")
    @Mapping(target = "updateTime", source = "updateTime", qualifiedByName = "stringToLocalDateTime")
    @Mapping(target = "adminUser", source = "adminUser", qualifiedByName = "booleanToByte")
    @Mapping(target = "adminCompany", source = "adminCompany", qualifiedByName = "booleanToByte")
    AuditEventPo dto2po(AuditEventDto dto);

    /**
     * DTO 布尔字段 → 表 TINYINT（0/1）。定义在本接口上，避免 MapStruct 跨模块解析 {@code uses} 类时找不到 {@link Named}。
     */
    @Named("booleanToByte")
    default Byte booleanToByte(Boolean value) {
        if (Objects.isNull(value)) {
            return null;
        }

        return value ? (byte) 1 : (byte) 0;
    }
}
