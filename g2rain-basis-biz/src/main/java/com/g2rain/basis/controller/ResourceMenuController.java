package com.g2rain.basis.controller;

import com.g2rain.basis.api.ResourceMenuApi;
import com.g2rain.basis.dto.ResourceMenuDto;
import com.g2rain.basis.dto.ResourceMenuSelectDto;
import com.g2rain.basis.service.ResourceMenuService;
import com.g2rain.basis.vo.ResourceMenuVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 应用资源菜单表控制器
 * 表名: resource_menu
 *
 * @author Alpha
 */
@RestController
@RequestMapping("/resource_menu")
public class ResourceMenuController implements ResourceMenuApi {

    @Resource(name = "resourceMenuServiceImpl")
    private ResourceMenuService resourceMenuService;

    /**
     * 查询应用资源菜单列表（不分页）
     *
     * @param selectDto 查询条件 DTO
     * @return 应用资源菜单视图对象列表
     */
    @Override
    public Result<List<ResourceMenuVo>> selectList(ResourceMenuSelectDto selectDto) {
        return Result.success(resourceMenuService.selectList(selectDto));
    }

    /**
     * 分页查询应用资源菜单列表
     *
     * @param selectDto 分页参数及查询条件
     * @return 分页后的应用资源菜单视图对象数据
     */
    @Override
    public Result<PageData<ResourceMenuVo>> selectPage(PageSelectListDto<ResourceMenuSelectDto> selectDto) {
        return Result.successPage(resourceMenuService.selectPage(selectDto));
    }

    /**
     * 新增或更新应用资源菜单
     *
     * @param dto 应用资源菜单数据传输对象（已校验）
     * @return 保存成功后的主键 ID
     */
    @PostMapping("/save")
    @Operation(summary = "新增或更新资源菜单", description = "新增或更新应用资源菜单信息")
    public Result<Long> save(@RequestBody @Validated ResourceMenuDto dto) {
        return Result.success(resourceMenuService.save(dto));
    }

    /**
     * 根据主键删除应用资源菜单记录
     *
     * @param id 应用资源菜单主键 ID
     * @return 受影响的记录行数
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除资源菜单记录", description = "根据主键删除应用资源菜单记录")
    public Result<Integer> delete(@Parameter(description = "资源菜单标识") @PathVariable Long id) {
        return Result.success(resourceMenuService.delete(id));
    }
}
