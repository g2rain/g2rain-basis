package com.g2rain.basis.converter;

import com.g2rain.basis.dao.po.IdpEnterpriseApplicationAuthorizationPo;
import com.g2rain.basis.dto.IdpEnterpriseApplicationAuthorizationDto;
import com.g2rain.basis.vo.IdpEnterpriseApplicationAuthorizationVo;
import com.g2rain.common.converter.CommonConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = CommonConverter.class)
public interface IdpEnterpriseApplicationAuthorizationConverter {
    IdpEnterpriseApplicationAuthorizationConverter INSTANCE =
        Mappers.getMapper(IdpEnterpriseApplicationAuthorizationConverter.class);

    @Mapping(target = "createTime", source = "createTime",
        qualifiedByName = "localDateTimeToString")
    @Mapping(target = "updateTime", source = "updateTime",
        qualifiedByName = "localDateTimeToString")
    @Mapping(target = "authorizedAt", source = "authorizedAt",
        qualifiedByName = "localDateTimeToString")
    @Mapping(target = "revokedAt", source = "revokedAt",
        qualifiedByName = "localDateTimeToString")
    @Mapping(target = "credentialExpireAt", source = "credentialExpireAt",
        qualifiedByName = "localDateTimeToString")
    IdpEnterpriseApplicationAuthorizationVo po2vo(
        IdpEnterpriseApplicationAuthorizationPo po);

    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleteFlag", ignore = true)
    @Mapping(target = "createTime", source = "createTime",
        qualifiedByName = "stringToLocalDateTime")
    @Mapping(target = "updateTime", source = "updateTime",
        qualifiedByName = "stringToLocalDateTime")
    IdpEnterpriseApplicationAuthorizationPo dto2po(
        IdpEnterpriseApplicationAuthorizationDto dto);
}
