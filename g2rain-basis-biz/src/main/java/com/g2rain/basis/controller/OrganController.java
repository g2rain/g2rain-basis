package com.g2rain.basis.controller;

import com.g2rain.basis.api.OrganApi;
import com.g2rain.basis.dto.OrganClosureDto;
import com.g2rain.basis.dto.OrganDto;
import com.g2rain.basis.dto.OrganIdNameMapSelectDto;
import com.g2rain.basis.dto.OrganSelectDto;
import com.g2rain.basis.dto.UpdateStatusDto;
import com.g2rain.basis.service.OrganService;
import com.g2rain.basis.service.TenantProvisionService;
import com.g2rain.basis.vo.OrganHierarchicalRelationVo;
import com.g2rain.basis.vo.OrganIdNameVo;
import com.g2rain.basis.vo.OrganVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 机构表控制器
 * 表名: organ
 *
 * @author Alpha
 */
@RestController
@RequestMapping("/organ")
public class OrganController implements OrganApi {
    @Resource(name = "tenantProvisionServiceImpl")
    private TenantProvisionService tenantProvisionService;

    @Resource(name = "organServiceImpl")
    private OrganService organService;

    /**
     * 查询机构列表（不分页）
     *
     * @param selectDto 查询条件 DTO
     * @return 机构视图对象列表
     */
    @Override
    public Result<List<OrganVo>> selectList(OrganSelectDto selectDto) {
        return Result.success(organService.selectList(selectDto));
    }

    /**
     * 分页查询机构列表
     *
     * @param selectDto 分页参数及查询条件
     * @return 分页后的机构视图对象数据
     */
    @Override
    public Result<PageData<OrganVo>> selectPage(PageSelectListDto<OrganSelectDto> selectDto) {
        return Result.successPage(organService.selectPage(selectDto));
    }

    /**
     * 获取机构层级关系
     *
     * @return 包含指定机构及其子机构的树形层级结构
     */
    @GetMapping("/hierarchy")
    @Operation(summary = "获取机构层级关系", description = "查询机构及其子机构的树形层级结构")
    public Result<List<OrganHierarchicalRelationVo>> getHierarchicalRelations() {
        return Result.success(organService.getHierarchicalRelations());
    }

    /**
     * 根据机构名称模糊搜索机构
     *
     * @param organName 机构名称关键字
     * @return 匹配的机构 ID 与名称列表
     */
    @GetMapping("/search")
    @Operation(summary = "搜索机构", description = "根据机构名称关键字模糊查询机构列表")
    public Result<List<OrganIdNameVo>> searchOrgans(@Parameter(description = "机构名称关键字") @RequestParam(required = false) String organName) {
        return Result.success(organService.searchOrgans(organName));
    }

    /**
     * 批量获取机构 ID 对应的 ID–名称映射
     *
     * @param dto 请求体，包含机构 ID 集合
     * @return 包含机构 ID 和名称的列表
     */
    @Override
    public Result<List<OrganIdNameVo>> selectOrganIdNameMap(OrganIdNameMapSelectDto dto) {
        return Result.success(organService.selectOrganIdNameMap(dto.getIds()));
    }

    /**
     * 检查指定机构是否存在指定祖先机构的层级关系
     *
     * @param childId  子机构 ID
     * @param parentId 祖先机构 ID
     * @return true 表示存在层级关系；false 表示不存在或参数为空
     */
    @Override
    public Result<Boolean> checkHierarchyRelation(Long childId, Long parentId) {
        return Result.success(organService.checkHierarchyRelation(childId, parentId));
    }

    /**
     * 新增或更新机构信息
     *
     * @param dto 机构数据传输对象（已校验）
     * @return 保存成功后的主键 ID
     */
    @PostMapping("/save")
    @Operation(summary = "新增或更新机构信息", description = "新增或更新机构基础信息")
    public Result<Long> save(@RequestBody @Validated OrganDto dto) {
        return Result.success(tenantProvisionService.createOrgan(dto));
    }

    /**
     * 根据主键删除机构记录
     *
     * @param id 机构主键 ID
     * @return 受影响的记录行数
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除机构记录", description = "根据主键删除机构记录")
    public Result<Integer> delete(@Parameter(description = "机构标识") @PathVariable Long id) {
        return Result.success(organService.delete(id));
    }

    /**
     * 更新机构状态
     *
     * @param id  机构主键 ID
     * @param dto 状态更新 DTO（已校验）
     * @return 更新成功的记录行数
     */
    @PostMapping("/{id}/status")
    @Operation(summary = "更新机构状态", description = "根据主键更新机构启用状态")
    public Result<Integer> updateStatus(@Parameter(description = "机构标识") @PathVariable Long id, @RequestBody @Validated UpdateStatusDto dto) {
        return Result.success(organService.updateStatus(id, dto));
    }

    /**
     * 调整机构层级关系（支持挂载、迁移、卸载）。
     *
     * @param descendantId 子节点标识
     * @param dto          层级调整参数，包含操作类型与目标父机构信息
     * @return 受影响的记录行数
     */
    @PostMapping("/{descendantId}/hierarchy")
    @Operation(summary = "调整机构层级关系", description = "对指定机构执行挂载、迁移或卸载等层级调整操作")
    public Result<Void> adjustHierarchy(@Parameter(description = "待调整机构标识") @PathVariable Long descendantId, @RequestBody OrganClosureDto dto) {
        organService.adjustHierarchy(descendantId, dto);
        return Result.success();
    }
}
