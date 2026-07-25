package com.g2rain.basis.api;

import com.g2rain.basis.dto.IdpEnterpriseApplicationAuthorizationResolveRequest;
import com.g2rain.basis.dto.IdpEnterpriseApplicationAuthorizationRevokeDto;
import com.g2rain.basis.dto.IdpEnterpriseApplicationAuthorizationSelectDto;
import com.g2rain.basis.dto.IdpEnterpriseApplicationAuthorizationUpsertDto;
import com.g2rain.basis.vo.IdpEnterpriseApplicationAuthorizationResolveVo;
import com.g2rain.basis.vo.IdpEnterpriseApplicationAuthorizationVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "外部 IdP 企业应用授权")
public interface IdpEnterpriseApplicationAuthorizationApi {

    @GetMapping("/idp_enterprise_application_authorization/list")
    @Operation(summary = "查询企业应用授权列表")
    Result<List<IdpEnterpriseApplicationAuthorizationVo>> selectList(
        IdpEnterpriseApplicationAuthorizationSelectDto selectDto);

    @GetMapping("/idp_enterprise_application_authorization/page")
    @Operation(summary = "分页查询企业应用授权")
    Result<PageData<IdpEnterpriseApplicationAuthorizationVo>> selectPage(
        PageSelectListDto<IdpEnterpriseApplicationAuthorizationSelectDto> selectDto);

    @PostMapping("/internal/idp/enterprise-application-authorization/upsert")
    @Operation(summary = "服务间幂等写入企业应用授权", hidden = true)
    Result<Long> upsert(
        @RequestBody @Validated IdpEnterpriseApplicationAuthorizationUpsertDto dto);

    @PostMapping("/internal/idp/enterprise-application-authorization/revoke")
    @Operation(summary = "服务间撤销企业应用授权", hidden = true)
    Result<Integer> revoke(
        @RequestBody @Validated IdpEnterpriseApplicationAuthorizationRevokeDto dto);

    @PostMapping("/internal/idp/enterprise-application-authorization/resolve")
    @Operation(summary = "服务间解析企业应用授权", hidden = true)
    Result<IdpEnterpriseApplicationAuthorizationResolveVo> resolve(
        @RequestBody @Validated IdpEnterpriseApplicationAuthorizationResolveRequest request);
}
