package com.g2rain.basis.controller;

import com.g2rain.basis.api.IdpEnterpriseOrganApi;
import com.g2rain.basis.dto.IdpEnterpriseOrganDto;
import com.g2rain.basis.dto.IdpEnterpriseOrganSelectDto;
import com.g2rain.basis.service.IdpEnterpriseOrganService;
import com.g2rain.basis.vo.IdpEnterpriseOrganVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 外部企业/租户与平台机构关联表控制器
 * 表名: idp_enterprise_organ
 *
 * @author G2rain Generator
 */
@RestController
@RequestMapping("/idp_enterprise_organ")
public class IdpEnterpriseOrganController implements IdpEnterpriseOrganApi {

    @Resource(name = "idpEnterpriseOrganServiceImpl")
    private IdpEnterpriseOrganService idpEnterpriseOrganService;

    @Override
    public Result<List<IdpEnterpriseOrganVo>> selectList(IdpEnterpriseOrganSelectDto selectDto) {
        return Result.success(idpEnterpriseOrganService.selectList(selectDto));
    }

    @Override
    public Result<PageData<IdpEnterpriseOrganVo>> selectPage(PageSelectListDto<IdpEnterpriseOrganSelectDto> selectDto) {
        return Result.successPage(idpEnterpriseOrganService.selectPage(selectDto));
    }

    @Override
    public Result<Long> save(IdpEnterpriseOrganDto dto) {
        return Result.success(idpEnterpriseOrganService.save(dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除关联记录", description = "根据主键删除外部企业与机构关联记录")
    public Result<Integer> delete(@Parameter(description = "关联记录标识") @PathVariable Long id) {
        return Result.success(idpEnterpriseOrganService.delete(id));
    }
}
