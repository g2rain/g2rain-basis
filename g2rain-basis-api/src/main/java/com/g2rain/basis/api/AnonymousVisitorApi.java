package com.g2rain.basis.api;

import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import com.g2rain.basis.dto.AnonymousVisitorSelectDto;
import com.g2rain.basis.vo.AnonymousVisitorVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


/**
 * 匿名访客表API接口
 * 表名: anonymous_visitor
 *
 * @author G2rain Generator
 */
@Tag(name = "匿名访客表", description = "匿名访客表相关接口")
public interface AnonymousVisitorApi {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return 数据列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询匿名访客表列表", description = "根据查询条件返回匿名访客表列表")
    Result<List<AnonymousVisitorVo>> selectList(AnonymousVisitorSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询匿名访客表列表", description = "分页查询匿名访客表列表")
    Result<PageData<AnonymousVisitorVo>> selectPage(PageSelectListDto<AnonymousVisitorSelectDto> selectDto);
}