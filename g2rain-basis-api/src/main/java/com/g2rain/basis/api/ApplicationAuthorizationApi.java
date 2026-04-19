package com.g2rain.basis.api;

import com.g2rain.basis.dto.ApplicationAuthorizationSelectDto;
import com.g2rain.basis.vo.ApplicationAuthorizationVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


/**
 * 应用授权记录表API接口
 * 表名: application_authorization
 *
 * @author Alpha
 */
@Tag(name = "应用授权", description = "应用授权记录相关接口")
public interface ApplicationAuthorizationApi {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return 数据列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询应用授权记录列表", description = "根据查询条件返回应用授权记录列表")
    Result<List<ApplicationAuthorizationVo>> selectList(ApplicationAuthorizationSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询应用授权记录列表", description = "分页查询应用授权记录列表")
    Result<PageData<ApplicationAuthorizationVo>> selectPage(PageSelectListDto<ApplicationAuthorizationSelectDto> selectDto);
}
