package com.g2rain.basis.api;

import com.g2rain.basis.dto.ControlUnitResourceRelationSelectDto;
import com.g2rain.basis.vo.ControlUnitResourceRelationVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


/**
 * 权限点资源关联表API接口
 * 表名: control_unit_resource_relation
 *
 * @author Alpha
 */
@Tag(name = "权限点资源关联", description = "权限点资源关联相关接口")
public interface ControlUnitResourceRelationApi {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return 数据列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询权限点资源关联列表", description = "根据查询条件返回权限点资源关联列表")
    Result<List<ControlUnitResourceRelationVo>> selectList(ControlUnitResourceRelationSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询权限点资源关联列表", description = "分页查询权限点资源关联列表")
    Result<PageData<ControlUnitResourceRelationVo>> selectPage(PageSelectListDto<ControlUnitResourceRelationSelectDto> selectDto);
}
