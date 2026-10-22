package com.g2rain.basis.api;

import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import com.g2rain.basis.dto.AnonymousEntrySelectDto;
import com.g2rain.basis.vo.AnonymousEntryVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


/**
 * 匿名接入码表API接口
 * 表名: anonymous_entry
 *
 * @author G2rain Generator
 */
@Tag(name = "匿名接入码表", description = "匿名接入码表相关接口")
public interface AnonymousEntryApi {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return 数据列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询匿名接入码表列表", description = "根据查询条件返回匿名接入码表列表")
    Result<List<AnonymousEntryVo>> selectList(AnonymousEntrySelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询匿名接入码表列表", description = "分页查询匿名接入码表列表")
    Result<PageData<AnonymousEntryVo>> selectPage(PageSelectListDto<AnonymousEntrySelectDto> selectDto);
}