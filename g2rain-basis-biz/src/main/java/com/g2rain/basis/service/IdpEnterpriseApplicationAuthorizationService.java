package com.g2rain.basis.service;

import com.g2rain.basis.dto.IdpEnterpriseApplicationAuthorizationDto;
import com.g2rain.basis.dto.IdpEnterpriseApplicationAuthorizationResolveRequest;
import com.g2rain.basis.dto.IdpEnterpriseApplicationAuthorizationRevokeDto;
import com.g2rain.basis.dto.IdpEnterpriseApplicationAuthorizationSelectDto;
import com.g2rain.basis.dto.IdpEnterpriseApplicationAuthorizationUpsertDto;
import com.g2rain.basis.vo.IdpEnterpriseApplicationAuthorizationResolveVo;
import com.g2rain.basis.vo.IdpEnterpriseApplicationAuthorizationVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;

import java.util.List;

public interface IdpEnterpriseApplicationAuthorizationService {
    List<IdpEnterpriseApplicationAuthorizationVo> selectList(
        IdpEnterpriseApplicationAuthorizationSelectDto selectDto);

    PageData<IdpEnterpriseApplicationAuthorizationVo> selectPage(
        PageSelectListDto<IdpEnterpriseApplicationAuthorizationSelectDto> selectDto);

    Long save(IdpEnterpriseApplicationAuthorizationDto dto);

    int delete(Long id);

    Long upsert(IdpEnterpriseApplicationAuthorizationUpsertDto dto);

    int revoke(IdpEnterpriseApplicationAuthorizationRevokeDto dto);

    IdpEnterpriseApplicationAuthorizationResolveVo resolve(
        IdpEnterpriseApplicationAuthorizationResolveRequest request);
}
