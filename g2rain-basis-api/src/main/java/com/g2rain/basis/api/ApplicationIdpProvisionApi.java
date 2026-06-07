package com.g2rain.basis.api;

import com.g2rain.basis.dto.ApplicationIdpProvisionSelectDto;
import com.g2rain.basis.vo.ApplicationIdpProvisionVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


/**
 * 外部身份源应用与平台应用的绑定API接口
 * 表名: application_idp_provision
 *
 * @author G2rain Generator
 */
@Tag(name = "外部身份源应用与平台应用的绑定", description = "外部身份源应用与平台应用的绑定相关接口")
public interface ApplicationIdpProvisionApi {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return 数据列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询绑定列表", description = "根据查询条件返回绑定列表")
    Result<List<ApplicationIdpProvisionVo>> selectList(ApplicationIdpProvisionSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页数据
     */
    @GetMapping("/page")
    @Operation(summary = "查询绑定分页", description = "根据查询条件返回绑定列表")
    Result<PageData<ApplicationIdpProvisionVo>> selectPage(PageSelectListDto<ApplicationIdpProvisionSelectDto> selectDto);
}
