package com.g2rain.basis.api;

import com.g2rain.basis.dto.ControlUnitSelectDto;
import com.g2rain.basis.vo.ControlUnitVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


/**
 * 控制单元表API接口
 * 表名: control_unit
 *
 * @author Alpha
 */
@Tag(name = "控制单元", description = "控制单元表相关接口")
public interface ControlUnitApi {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return 数据列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询控制单元列表", description = "根据查询条件返回控制单元列表")
    Result<List<ControlUnitVo>> selectList(ControlUnitSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询控制单元列表", description = "分页查询控制单元列表")
    Result<PageData<ControlUnitVo>> selectPage(PageSelectListDto<ControlUnitSelectDto> selectDto);
}
