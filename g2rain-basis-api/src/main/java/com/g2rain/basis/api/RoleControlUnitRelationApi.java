package com.g2rain.basis.api;

import com.g2rain.basis.dto.RoleControlUnitRelationSelectDto;
import com.g2rain.basis.vo.RoleControlUnitRelationVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


/**
 * 角色控制单元关联表API接口
 * 表名: role_control_unit_relation
 *
 * @author Alpha
 */
@Tag(name = "角色-控制单元关联", description = "角色控制单元关联相关接口")
public interface RoleControlUnitRelationApi {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return 数据列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询角色-控制单元关联列表", description = "根据查询条件返回角色控制单元关联列表")
    Result<List<RoleControlUnitRelationVo>> selectList(RoleControlUnitRelationSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询角色-控制单元关联列表", description = "分页查询角色控制单元关联列表")
    Result<PageData<RoleControlUnitRelationVo>> selectPage(PageSelectListDto<RoleControlUnitRelationSelectDto> selectDto);
}
