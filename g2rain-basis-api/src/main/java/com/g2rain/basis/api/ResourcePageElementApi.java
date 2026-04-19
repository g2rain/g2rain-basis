package com.g2rain.basis.api;

import com.g2rain.basis.dto.ResourcePageElementSelectDto;
import com.g2rain.basis.vo.ResourcePageElementVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


/**
 * 应用资源页面元素表API接口
 * 表名: resource_page_element
 *
 * @author Alpha
 */
@Tag(name = "资源页面元素", description = "应用资源页面元素相关接口")
public interface ResourcePageElementApi {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return 数据列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询资源页面元素列表", description = "根据查询条件返回资源页面元素列表")
    Result<List<ResourcePageElementVo>> selectList(ResourcePageElementSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询资源页面元素列表", description = "分页查询资源页面元素列表")
    Result<PageData<ResourcePageElementVo>> selectPage(PageSelectListDto<ResourcePageElementSelectDto> selectDto);
}
