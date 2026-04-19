package com.g2rain.basis.converter;

import com.g2rain.basis.dao.po.ApplicationPo;
import com.g2rain.basis.dto.ApplicationDto;
import com.g2rain.basis.vo.ApplicationScopeVo;
import com.g2rain.basis.vo.ApplicationVo;
import com.g2rain.basis.vo.PublicKeyDescriptorVo;
import com.g2rain.common.converter.CommonConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

/**
 * 应用表转换器
 * 用于Po、Vo、Dto之间的相互转换
 * 表名: application
 *
 * @author Alpha
 */
@Mapper(uses = CommonConverter.class)
public interface ApplicationConverter {

    /**
     * 单例实例，通过 {@link Mappers#getMapper(Class)} 获取 MapStruct 自动生成的实现。
     */
    ApplicationConverter INSTANCE = Mappers.getMapper(ApplicationConverter.class);

    /**
     * Po -> Vo
     * 自动将 createTime 和 updateTime 从 {@link LocalDateTime} 转换为 {@link String}
     */
    @Mapping(target = "createTime", source = "createTime", qualifiedByName = "localDateTimeToString")
    @Mapping(target = "updateTime", source = "updateTime", qualifiedByName = "localDateTimeToString")
    ApplicationVo po2vo(ApplicationPo po);

    /**
     * Dto -> Po
     * 自动将 createTime 和 updateTime 从 {@link String} 转换为 {@link LocalDateTime}
     * 忽略 version 字段
     * 忽略 deleteFlag 字段
     */
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleteFlag", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "landing", ignore = true)
    @Mapping(target = "publicKeyAlgorithm", ignore = true)
    @Mapping(target = "publicKeyFormat", ignore = true)
    @Mapping(target = "publicKey", ignore = true)
    @Mapping(target = "createTime", source = "createTime", qualifiedByName = "stringToLocalDateTime")
    @Mapping(target = "updateTime", source = "updateTime", qualifiedByName = "stringToLocalDateTime")
    ApplicationPo dto2po(ApplicationDto dto);

    /**
     * 从 {@link ApplicationPo} 提取 PEM/DER 格式的公钥封装为 {@link PublicKeyDescriptorVo}。
     *
     * @param po 持久化对象
     * @return 获取指定应用的公钥信息
     */
    PublicKeyDescriptorVo po2PublicKey(ApplicationPo po);

    /**
     * 将 Po 对象转换为 scopeVo 对象。
     *
     * @param po {@link ApplicationPo} 实体对象
     * @return 转换后的 {@link ApplicationScopeVo} 范围视图对象
     */
    ApplicationScopeVo po2scopeVo(ApplicationPo po);
}
