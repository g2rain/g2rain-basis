package com.g2rain.basis.api;

import com.g2rain.basis.dto.ApiEndpointSelectDto;
import com.g2rain.basis.vo.ApiEndpointVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


/**
 * 接口地址表API接口
 * 表名: api_endpoint
 *
 * @author Alpha
 */
@Tag(name = "后端接口地址", description = "后端接口地址接口")
public interface ApiEndpointApi {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return 数据列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询后端接口地址列表", description = "根据查询条件返回后端接口地址列表")
    Result<List<ApiEndpointVo>> selectList(ApiEndpointSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询后端接口地址列表", description = "分页查询后端接口地址列表")
    Result<PageData<ApiEndpointVo>> selectPage(PageSelectListDto<ApiEndpointSelectDto> selectDto);
}
