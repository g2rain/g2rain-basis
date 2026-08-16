package com.g2rain.basis.controller;

import com.g2rain.basis.api.IdpEnterpriseApplicationAuthorizationApi;
import com.g2rain.basis.dto.IdpEnterpriseApplicationAuthorizationDto;
import com.g2rain.basis.dto.IdpEnterpriseApplicationAuthorizationResolveRequest;
import com.g2rain.basis.dto.IdpEnterpriseApplicationAuthorizationRevokeDto;
import com.g2rain.basis.dto.IdpEnterpriseApplicationAuthorizationSelectDto;
import com.g2rain.basis.dto.IdpEnterpriseApplicationAuthorizationUpsertDto;
import com.g2rain.basis.service.IdpEnterpriseApplicationAuthorizationService;
import com.g2rain.basis.vo.IdpEnterpriseApplicationAuthorizationResolveVo;
import com.g2rain.basis.vo.IdpEnterpriseApplicationAuthorizationVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class IdpEnterpriseApplicationAuthorizationController
    implements IdpEnterpriseApplicationAuthorizationApi {

    @Resource(name = "idpEnterpriseApplicationAuthorizationServiceImpl")
    private IdpEnterpriseApplicationAuthorizationService service;

    @Override
    public Result<List<IdpEnterpriseApplicationAuthorizationVo>> selectList(
        IdpEnterpriseApplicationAuthorizationSelectDto selectDto) {
        return Result.success(service.selectList(selectDto));
    }

    @Override
    public Result<PageData<IdpEnterpriseApplicationAuthorizationVo>> selectPage(
        PageSelectListDto<IdpEnterpriseApplicationAuthorizationSelectDto> selectDto) {
        return Result.successPage(service.selectPage(selectDto));
    }

    @PostMapping("/idp_enterprise_application_authorization/save")
    @Operation(summary = "新增或更新企业应用授权")
    public Result<Long> save(
        @RequestBody @Validated IdpEnterpriseApplicationAuthorizationDto dto) {
        return Result.success(service.save(dto));
    }

    @DeleteMapping("/idp_enterprise_application_authorization/{id}")
    @Operation(summary = "删除企业应用授权")
    public Result<Integer> delete(
        @Parameter(description = "授权记录标识") @PathVariable Long id) {
        return Result.success(service.delete(id));
    }

    @Override
    public Result<Long> upsert(IdpEnterpriseApplicationAuthorizationUpsertDto dto) {
        return Result.success(service.upsert(dto));
    }

    @Override
    public Result<Integer> revoke(IdpEnterpriseApplicationAuthorizationRevokeDto dto) {
        return Result.success(service.revoke(dto));
    }

    @Override
    public Result<IdpEnterpriseApplicationAuthorizationResolveVo> resolve(
        IdpEnterpriseApplicationAuthorizationResolveRequest request) {
        return Result.success(service.resolve(request));
    }
}
