package com.g2rain.basis.converter;

import com.g2rain.basis.dao.po.AuthorityPageElementPo;
import com.g2rain.basis.dao.po.ResourcePageElementPo;
import com.g2rain.basis.dto.ResourcePageElementDto;
import com.g2rain.basis.vo.AuthorityPageElementVo;
import com.g2rain.basis.vo.ResourcePageElementVo;
import com.g2rain.common.converter.CommonConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

/**
 * 应用资源页面元素表转换器
 * 用于Po、Vo、Dto之间的相互转换
 * 表名: resource_page_element
 *
 * @author Alpha
 */
@Mapper(uses = CommonConverter.class)
public interface ResourcePageElementConverter {

    /**
     * 单例实例，通过 {@link Mappers#getMapper(Class)} 获取 MapStruct 自动生成的实现。
     */
    ResourcePageElementConverter INSTANCE = Mappers.getMapper(ResourcePageElementConverter.class);

    /**
     * Po -> Vo
     * 自动将 createTime 和 updateTime 从 {@link LocalDateTime} 转换为 {@link String}
     */
    @Mapping(target = "createTime", source = "createTime", qualifiedByName = "localDateTimeToString")
    @Mapping(target = "updateTime", source = "updateTime", qualifiedByName = "localDateTimeToString")
    ResourcePageElementVo po2vo(ResourcePageElementPo po);

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
    ResourcePageElementPo dto2po(ResourcePageElementDto dto);

    /**
     * 将 Po 对象转换为授权 Vo 对象。
     * <p>
     * 用于生成页面元素级别的授权视图对象 {@link AuthorityPageElementVo}。
     * </p>
     *
     * @param po {@link AuthorityPageElementPo} 实体对象
     * @return 转换后的 {@link AuthorityPageElementVo} 授权视图对象
     */
    AuthorityPageElementVo po2authVo(AuthorityPageElementPo po);
}
