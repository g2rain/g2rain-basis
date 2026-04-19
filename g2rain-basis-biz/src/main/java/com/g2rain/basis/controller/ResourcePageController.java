package com.g2rain.basis.controller;

import com.g2rain.basis.api.ResourcePageApi;
import com.g2rain.basis.dto.ResourcePageDto;
import com.g2rain.basis.dto.ResourcePageSelectDto;
import com.g2rain.basis.service.ResourcePageService;
import com.g2rain.basis.vo.ResourcePageVo;
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
 * 应用资源页面表控制器
 * 表名: resource_page
 *
 * @author Alpha
 */
@RestController
@RequestMapping("/resource_page")
public class ResourcePageController implements ResourcePageApi {

    @Resource(name = "resourcePageServiceImpl")
    private ResourcePageService resourcePageService;

    /**
     * 查询应用资源页面列表（不分页）
     *
     * @param selectDto 查询条件 DTO
     * @return 应用资源页面视图对象列表
     */
    @Override
    public Result<List<ResourcePageVo>> selectList(ResourcePageSelectDto selectDto) {
        return Result.success(resourcePageService.selectList(selectDto));
    }

    /**
     * 分页查询应用资源页面列表
     *
     * @param selectDto 分页参数及查询条件
     * @return 分页后的应用资源页面视图对象数据
     */
    @Override
    public Result<PageData<ResourcePageVo>> selectPage(PageSelectListDto<ResourcePageSelectDto> selectDto) {
        return Result.successPage(resourcePageService.selectPage(selectDto));
    }

    /**
     * 新增或更新应用资源页面
     *
     * @param dto 应用资源页面数据传输对象（已校验）
     * @return 保存成功后的主键 ID
     */
    @PostMapping("/save")
    @Operation(summary = "新增或更新资源页面", description = "新增或更新应用资源页面信息")
    public Result<Long> save(@RequestBody @Validated ResourcePageDto dto) {
        return Result.success(resourcePageService.save(dto));
    }

    /**
     * 根据主键删除应用资源页面记录
     *
     * @param id 应用资源页面主键 ID
     * @return 受影响的记录行数
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除资源页面记录", description = "根据主键删除应用资源页面记录")
    public Result<Integer> delete(@Parameter(description = "资源页面标识") @PathVariable Long id) {
        return Result.success(resourcePageService.delete(id));
    }
}
