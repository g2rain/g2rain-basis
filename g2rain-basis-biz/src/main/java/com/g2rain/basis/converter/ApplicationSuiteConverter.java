package com.g2rain.basis.converter;

import com.g2rain.basis.dao.po.ApplicationSuitePo;
import com.g2rain.basis.vo.ApplicationSuiteVo;
import com.g2rain.common.converter.CommonConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

/**
 * 应用归类关系表转换器
 * 用于Po、Vo、Dto之间的相互转换
 * 表名: application_suite
 *
 * @author Alpha
 */
@Mapper(uses = CommonConverter.class)
public interface ApplicationSuiteConverter {

    /**
     * 单例实例，通过 {@link Mappers#getMapper(Class)} 获取 MapStruct 自动生成的实现。
     */
    ApplicationSuiteConverter INSTANCE = Mappers.getMapper(ApplicationSuiteConverter.class);

    /**
     * Po -> Vo
     * 自动将 createTime 和 updateTime 从 {@link LocalDateTime} 转换为 {@link String}
     */
    @Mapping(target = "createTime", source = "createTime", qualifiedByName = "localDateTimeToString")
    @Mapping(target = "updateTime", source = "updateTime", qualifiedByName = "localDateTimeToString")
    ApplicationSuiteVo po2vo(ApplicationSuitePo po);
}
