package com.g2rain.basis.controller;

import com.g2rain.basis.api.ApplicationSuiteApi;
import com.g2rain.basis.dto.ApplicationSuiteDto;
import com.g2rain.basis.dto.ApplicationSuiteSelectDto;
import com.g2rain.basis.service.ApplicationSuiteService;
import com.g2rain.basis.vo.ApplicationSuiteVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 应用归类关系表控制器
 * 表名: application_suite
 *
 * @author Alpha
 */
@RestController
@RequestMapping("/application_suite")
public class ApplicationSuiteController implements ApplicationSuiteApi {

    @Resource(name = "applicationSuiteServiceImpl")
    private ApplicationSuiteService applicationSuiteService;

    /**
     * 查询应用归类关系列表（不分页）
     *
     * @param selectDto 查询条件 DTO
     * @return 应用归类关系视图对象列表
     */
    @Override
    public Result<List<ApplicationSuiteVo>> selectList(ApplicationSuiteSelectDto selectDto) {
        return Result.success(applicationSuiteService.selectList(selectDto));
    }

    /**
     * 分页查询应用归类关系列表
     *
     * @param selectDto 分页参数及查询条件
     * @return 分页后的应用归类关系视图对象数据
     */
    @Override
    public Result<PageData<ApplicationSuiteVo>> selectPage(PageSelectListDto<ApplicationSuiteSelectDto> selectDto) {
        return Result.successPage(applicationSuiteService.selectPage(selectDto));
    }

    /**
     * 新增或更新应用归类关系
     *
     * @param dto 应用归类关系数据传输对象（已校验）
     * @return 保存成功后的主键 ID
     */
    @PostMapping("/save")
    @Operation(summary = "新增或更新应用归类关系", description = "新增或更新应用与归类的关联关系")
    public Result<Integer> save(@RequestBody @Validated ApplicationSuiteDto dto) {
        return Result.success(applicationSuiteService.save(dto));
    }
}
