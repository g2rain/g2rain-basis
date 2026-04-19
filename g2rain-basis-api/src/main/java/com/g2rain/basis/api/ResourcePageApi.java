package com.g2rain.basis.api;

import com.g2rain.basis.dto.ResourcePageSelectDto;
import com.g2rain.basis.vo.ResourcePageVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


/**
 * 应用资源页面表API接口
 * 表名: resource_page
 *
 * @author Alpha
 */
@Tag(name = "资源页面", description = "应用资源页面相关接口")
public interface ResourcePageApi {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return 数据列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询资源页面列表", description = "根据查询条件返回资源页面列表")
    Result<List<ResourcePageVo>> selectList(ResourcePageSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询资源页面列表", description = "分页查询资源页面列表")
    Result<PageData<ResourcePageVo>> selectPage(PageSelectListDto<ResourcePageSelectDto> selectDto);
}
