package com.g2rain.basis.controller;

import com.g2rain.basis.api.PassportApi;
import com.g2rain.basis.dto.PassportDto;
import com.g2rain.basis.dto.PassportSelectDto;
import com.g2rain.basis.dto.PassportUpdatePasswordDto;
import com.g2rain.basis.dto.UpdateStatusDto;
import com.g2rain.basis.service.PassportService;
import com.g2rain.basis.vo.PassportVo;
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
 * 账号表控制器
 * 表名: passport
 *
 * @author Alpha
 */
@RestController
@RequestMapping("/passport")
public class PassportController implements PassportApi {

    @Resource(name = "passportServiceImpl")
    private PassportService passportService;

    /**
     * 查询账号列表（不分页）
     *
     * @param selectDto 查询条件 DTO
     * @return 账号视图对象列表
     */
    @Override
    public Result<List<PassportVo>> selectList(PassportSelectDto selectDto) {
        return Result.success(passportService.selectList(selectDto));
    }

    /**
     * 分页查询账号列表
     *
     * @param selectDto 分页参数及查询条件
     * @return 分页后的账号视图对象数据
     */
    @Override
    public Result<PageData<PassportVo>> selectPage(PageSelectListDto<PassportSelectDto> selectDto) {
        return Result.successPage(passportService.selectPage(selectDto));
    }

    /**
     * 新增或更新账号信息
     *
     * @param dto 账号数据传输对象（已校验）
     * @return 保存成功后的主键 ID
     */
    @Override
    public Result<Long> save(PassportDto dto) {
        return Result.success(passportService.save(dto));
    }

    /**
     * 根据主键删除账号记录
     *
     * @param id 账号主键 ID
     * @return 受影响的记录行数
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除账号记录", description = "根据主键删除账号记录")
    public Result<Integer> delete(@Parameter(description = "账号标识") @PathVariable Long id) {
        return Result.success(passportService.delete(id));
    }

    /**
     * 更新账号密码
     *
     * @param id  账号主键 ID
     * @param dto 密码更新 DTO（已校验）
     * @return 更新成功的记录行数
     */
    @PostMapping("/{id}/password")
    @Operation(summary = "更新账号密码", description = "根据主键更新账号登录密码")
    public Result<Integer> updatePassword(@Parameter(description = "账号标识") @PathVariable Long id, @RequestBody @Validated PassportUpdatePasswordDto dto) {
        return Result.success(passportService.updatePassword(id, dto));
    }

    /**
     * 更新账号状态
     *
     * @param id  账号主键 ID
     * @param dto 状态更新 DTO（已校验）
     * @return 更新成功的记录行数
     */
    @PostMapping("/{id}/status")
    @Operation(summary = "更新账号状态", description = "根据主键更新账号启用状态")
    public Result<Integer> updateStatus(@Parameter(description = "账号标识") @PathVariable Long id, @RequestBody @Validated UpdateStatusDto dto) {
        return Result.success(passportService.updateStatus(id, dto));
    }
}
