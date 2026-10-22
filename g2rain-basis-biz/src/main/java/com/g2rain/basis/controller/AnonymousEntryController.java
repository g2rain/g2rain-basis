package com.g2rain.basis.controller;

import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import com.g2rain.basis.api.AnonymousEntryApi;
import com.g2rain.basis.dto.AnonymousEntryDto;
import com.g2rain.basis.dto.AnonymousEntrySelectDto;
import com.g2rain.basis.dto.UpdateStatusDto;
import com.g2rain.basis.service.AnonymousEntryService;
import com.g2rain.basis.vo.AnonymousEntryVo;
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
 * 匿名接入码表控制器
 * 表名: anonymous_entry
 *
 * @author G2rain Generator
 */
@RestController
@RequestMapping("/anonymous_entry")
public class AnonymousEntryController implements AnonymousEntryApi {

    @Resource(name = "anonymousEntryServiceImpl")
    private AnonymousEntryService anonymousEntryService;

    @Override
    public Result<List<AnonymousEntryVo>> selectList(AnonymousEntrySelectDto selectDto) {
        return Result.success(anonymousEntryService.selectList(selectDto));
    }

    @Override
    public Result<PageData<AnonymousEntryVo>> selectPage(PageSelectListDto<AnonymousEntrySelectDto> selectDto) {
        return Result.successPage(anonymousEntryService.selectPage(selectDto));
    }

    @PostMapping("/save")
    @Operation(summary = "新增或更新匿名接入码表信息", description = "新增或更新匿名接入码表基础信息")
    public Result<Long> save(@RequestBody AnonymousEntryDto dto) {
        return Result.success(anonymousEntryService.save(dto));
    }

    @PostMapping("/{id}/status")
    @Operation(summary = "修改接入码状态", description = "仅允许改状态")
    public Result<Integer> updateStatus(@Parameter(description = "接入码标识") @PathVariable Long id,
                                        @RequestBody @Validated UpdateStatusDto dto) {
        return Result.success(anonymousEntryService.updateStatus(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除匿名接入码表记录", description = "根据主键删除匿名接入码表记录")
    public Result<Integer> delete(@Parameter(description = "匿名接入码表标识") @PathVariable Long id) {
        return Result.success(anonymousEntryService.delete(id));
    }
}