package com.g2rain.basis.controller;

import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import com.g2rain.basis.api.AnonymousVisitorApi;
import com.g2rain.basis.dto.AnonymousVisitorDto;
import com.g2rain.basis.dto.AnonymousVisitorSelectDto;
import com.g2rain.basis.service.AnonymousVisitorService;
import com.g2rain.basis.vo.AnonymousVisitorVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 匿名访客表控制器
 * 表名: anonymous_visitor
 *
 * @author G2rain Generator
 */
@RestController
@RequestMapping("/anonymous_visitor")
public class AnonymousVisitorController implements AnonymousVisitorApi {

    @Resource(name = "anonymousVisitorServiceImpl")
    private AnonymousVisitorService anonymousVisitorService;

    @Override
    public Result<List<AnonymousVisitorVo>> selectList(AnonymousVisitorSelectDto selectDto) {
        return Result.success(anonymousVisitorService.selectList(selectDto));
    }

    @Override
    public Result<PageData<AnonymousVisitorVo>> selectPage(PageSelectListDto<AnonymousVisitorSelectDto> selectDto) {
        return Result.successPage(anonymousVisitorService.selectPage(selectDto));
    }

    @PostMapping("/save")
    @Operation(summary = "新增或更新匿名访客表信息", description = "新增或更新匿名访客表基础信息")
    public Result<Long> save(@RequestBody AnonymousVisitorDto dto) {
        return Result.success(anonymousVisitorService.save(dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除匿名访客表记录", description = "根据主键删除匿名访客表记录")
    public Result<Integer> delete(@Parameter(description = "匿名访客表标识") @PathVariable Long id) {
        return Result.success(anonymousVisitorService.delete(id));
    }
}