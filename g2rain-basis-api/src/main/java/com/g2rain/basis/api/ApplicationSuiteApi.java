package com.g2rain.basis.api;

import com.g2rain.basis.dto.ApplicationSuiteSelectDto;
import com.g2rain.basis.vo.ApplicationSuiteVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


/**
 * 应用归类关系表API接口
 * 表名: application_suite
 *
 * @author Alpha
 */
@Tag(name = "应用归类关系", description = "应用归类关系相关接口")
public interface ApplicationSuiteApi {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return 数据列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询应用归类关系列表", description = "根据查询条件返回应用归类关系列表")
    Result<List<ApplicationSuiteVo>> selectList(ApplicationSuiteSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询应用归类关系列表", description = "分页查询应用归类关系列表")
    Result<PageData<ApplicationSuiteVo>> selectPage(PageSelectListDto<ApplicationSuiteSelectDto> selectDto);
}
