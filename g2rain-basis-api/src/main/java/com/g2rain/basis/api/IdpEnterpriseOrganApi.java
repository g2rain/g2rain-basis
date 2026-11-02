package com.g2rain.basis.api;

import com.g2rain.basis.dto.IdpEnterpriseOrganDto;
import com.g2rain.basis.dto.IdpEnterpriseOrganResolveRequest;
import com.g2rain.basis.dto.IdpEnterpriseOrganSelectDto;
import com.g2rain.basis.vo.IdpEnterpriseOrganResolveVo;
import com.g2rain.basis.vo.IdpEnterpriseOrganVo;
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


/**
 * 外部企业/租户与平台机构关联表 API 接口。
 * <p>
 * <b>IAM 集成：</b>IAM 为独立进程，通过 REST 或 OpenFeign 调用本服务路径 {@code /idp_enterprise_organ}
 * 及受信内部 {@code /internal/idp/enterprise-organ/resolve}。
 * 字段 {@code idp_type} 与 {@link com.g2rain.basis.enums.IdpType} 存库值一致。
 * </p>
 * 表名: idp_enterprise_organ
 *
 * @author G2rain Generator
 */
@Tag(name = "外部企业与机构关联", description = "外部企业/租户与平台机构关联表相关接口")
public interface IdpEnterpriseOrganApi {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件DTO
     * @return 数据列表
     */
    @GetMapping("/idp_enterprise_organ/list")
    @Operation(summary = "查询外部企业与机构关联列表", description = "根据查询条件返回关联列表")
    Result<List<IdpEnterpriseOrganVo>> selectList(IdpEnterpriseOrganSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页数据
     */
    @GetMapping("/idp_enterprise_organ/page")
    @Operation(summary = "分页查询外部企业与机构关联列表", description = "分页查询关联列表")
    Result<PageData<IdpEnterpriseOrganVo>> selectPage(PageSelectListDto<IdpEnterpriseOrganSelectDto> selectDto);

    /**
     * 新增或更新外部企业与机构关联信息
     *
     * @param dto 数据传输对象（已校验）
     * @return 保存成功后的主键 ID
     */
    @PostMapping("/idp_enterprise_organ/save")
    @Operation(summary = "新增或更新关联", description = "新增或更新外部企业/租户与平台机构关联信息")
    Result<Long> save(@RequestBody @Validated IdpEnterpriseOrganDto dto);

    /**
     * 按外部企业标识解析唯一 ACTIVE 映射到平台 organId。
     * <p>0 条 → {@code IDP_ENTERPRISE_ORGAN_NOT_FOUND}；多条 → {@code IDP_ENTERPRISE_ORGAN_AMBIGUOUS}。</p>
     */
    @PostMapping("/internal/idp/enterprise-organ/resolve")
    @Operation(summary = "服务间解析外部企业与机构映射", description = "仅供受信服务间调用", hidden = true)
    Result<IdpEnterpriseOrganResolveVo> resolve(
        @RequestBody @Validated IdpEnterpriseOrganResolveRequest request);
}
