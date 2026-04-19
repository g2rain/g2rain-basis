package com.g2rain.basis.controller;

import com.g2rain.basis.api.ApplicationAuthorizationApi;
import com.g2rain.basis.dto.ApplicationAuthorizationDto;
import com.g2rain.basis.dto.ApplicationAuthorizationSelectDto;
import com.g2rain.basis.dto.UpdateStatusDto;
import com.g2rain.basis.service.ApplicationAuthorizationService;
import com.g2rain.basis.vo.ApplicationAuthorizationVo;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 应用授权记录表控制器
 * 表名: application_authorization
 *
 * @author Alpha
 */
@RestController
@RequestMapping("/application_authorization")
public class ApplicationAuthorizationController implements ApplicationAuthorizationApi {

    @Resource(name = "applicationAuthorizationServiceImpl")
    private ApplicationAuthorizationService applicationAuthorizationService;

    /**
     * 查询应用授权记录列表（不分页）
     *
     * @param selectDto 查询条件 DTO
     * @return 应用授权记录视图对象列表
     */
    @Override
    public Result<List<ApplicationAuthorizationVo>> selectList(ApplicationAuthorizationSelectDto selectDto) {
        return Result.success(applicationAuthorizationService.selectList(selectDto));
    }

    /**
     * 分页查询应用授权记录列表
     *
     * @param selectDto 分页参数及查询条件
     * @return 分页后的应用授权记录视图对象数据
     */
    @Override
    public Result<PageData<ApplicationAuthorizationVo>> selectPage(PageSelectListDto<ApplicationAuthorizationSelectDto> selectDto) {
        return Result.successPage(applicationAuthorizationService.selectPage(selectDto));
    }

    /**
     * 新增或更新应用授权记录
     *
     * @param dto 应用授权记录数据传输对象（已校验）
     * @return 保存成功后的主键 ID
     */
    @PostMapping("/save")
    @Operation(summary = "新增或更新应用授权记录", description = "新增或更新应用授权记录")
    public Result<Long> save(@RequestBody @Validated ApplicationAuthorizationDto dto) {
        return Result.success(applicationAuthorizationService.save(dto));
    }

    /**
     * 修改应用授权记录状态
     *
     * @param id  授权记录 ID
     * @param dto 修改状态参数
     * @return 受影响的记录行数
     */
    @PostMapping("/{id}/status")
    @Operation(summary = "修改应用授权记录状态", description = "修改应用授权记录状态")
    public Result<Integer> updateStatus(@Parameter(description = "应用授权记录标识") @PathVariable Long id, @RequestBody @Validated UpdateStatusDto dto) {
        return Result.success(applicationAuthorizationService.updateStatus(id, dto));
    }

    /**
     * 根据主键删除应用授权记录
     *
     * @param id 应用授权记录主键 ID
     * @return 受影响的记录行数
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "根据主键删除应用授权记录", description = "根据主键删除应用授权记录")
    public Result<Integer> delete(@Parameter(description = "应用授权记录标识") @PathVariable Long id) {
        return Result.success(applicationAuthorizationService.delete(id));
    }
}
