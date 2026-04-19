package com.g2rain.basis.api;

import com.g2rain.basis.dto.OrganIdNameMapSelectDto;
import com.g2rain.basis.dto.OrganSelectDto;
import com.g2rain.basis.vo.OrganIdNameVo;
import com.g2rain.basis.vo.OrganVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


/**
 * 机构表API接口
 * 表名: organ
 *
 * @author Alpha
 */
@Tag(name = "机构", description = "机构表相关接口")
public interface OrganApi {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return 数据列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询机构列表", description = "根据查询条件返回机构列表")
    Result<List<OrganVo>> selectList(OrganSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询机构列表", description = "分页查询机构列表")
    Result<PageData<OrganVo>> selectPage(PageSelectListDto<OrganSelectDto> selectDto);

    /**
     * 批量获取机构 ID 对应的 ID–名称映射
     *
     * @param dto 请求体，包含机构 ID 集合
     * @return 包含机构 ID 和名称的列表
     */
    @PostMapping("/id_name_map")
    @Operation(summary = "查询机构名称映射", hidden = true, description = "根据机构标识集合查询机构标识与名称映射")
    Result<List<OrganIdNameVo>> selectOrganIdNameMap(@RequestBody @Validated OrganIdNameMapSelectDto dto);

    /**
     * 检查指定机构是否存在指定祖先机构的层级关系
     *
     * @param childId  子机构 ID
     * @param parentId 祖先机构 ID
     * @return true 表示存在层级关系；false 表示不存在或参数为空
     */
    @GetMapping("/hierarchy/exists")
    @Operation(summary = "检查机构层级关系是否存在", hidden = true, description = "检查指定机构是否存在指定祖先机构的层级关系")
    Result<Boolean> checkHierarchyRelation(@Parameter(description = "子机构标识") @RequestParam Long childId, @Parameter(description = "祖先机构标识") @RequestParam Long parentId);
}
