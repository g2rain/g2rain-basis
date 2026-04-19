package com.g2rain.basis.converter;

import com.g2rain.basis.dao.po.ResourceMenuPo;
import com.g2rain.basis.dto.ResourceMenuDto;
import com.g2rain.basis.vo.AuthorityMenuVo;
import com.g2rain.basis.vo.ResourceMenuVo;
import com.g2rain.common.converter.CommonConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

/**
 * 应用资源菜单表转换器
 * 用于Po、Vo、Dto之间的相互转换
 * 表名: resource_menu
 *
 * @author Alpha
 */
@Mapper(uses = CommonConverter.class)
public interface ResourceMenuConverter {

    /**
     * 单例实例，通过 {@link Mappers#getMapper(Class)} 获取 MapStruct 自动生成的实现。
     */
    ResourceMenuConverter INSTANCE = Mappers.getMapper(ResourceMenuConverter.class);

    /**
     * Po -> Vo
     * 自动将 createTime 和 updateTime 从 {@link LocalDateTime} 转换为 {@link String}
     */
    @Mapping(target = "createTime", source = "createTime", qualifiedByName = "localDateTimeToString")
    @Mapping(target = "updateTime", source = "updateTime", qualifiedByName = "localDateTimeToString")
    ResourceMenuVo po2vo(ResourceMenuPo po);

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
    ResourceMenuPo dto2po(ResourceMenuDto dto);

    /**
     * 将 Po 对象转换为授权 Vo 对象。
     * <p>
     * 忽略 applicationCode、endpointUrl、contextPath 和 subMenus 字段，生成对应的授权视图对象。
     * </p>
     *
     * @param po {@link ResourceMenuPo} 实体对象
     * @return 转换后的 {@link AuthorityMenuVo} 授权视图对象
     */
    @Mapping(target = "applicationCode", ignore = true)
    @Mapping(target = "endpointUrl", ignore = true)
    @Mapping(target = "contextPath", ignore = true)
    @Mapping(target = "subMenus", ignore = true)
    AuthorityMenuVo po2authVo(ResourceMenuPo po);
}
