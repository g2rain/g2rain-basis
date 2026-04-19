package com.g2rain.basis.controller;

import com.g2rain.basis.api.ResourcePageElementApi;
import com.g2rain.basis.dto.ResourcePageElementDto;
import com.g2rain.basis.dto.ResourcePageElementSelectDto;
import com.g2rain.basis.service.ResourcePageElementService;
import com.g2rain.basis.vo.ResourcePageElementVo;
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
 * 应用资源页面元素表控制器
 * 表名: resource_page_element
 *
 * @author Alpha
 */
@RestController
@RequestMapping("/resource_page_element")
public class ResourcePageElementController implements ResourcePageElementApi {

    @Resource(name = "resourcePageElementServiceImpl")
    private ResourcePageElementService resourcePageElementService;

    /**
     * 查询应用资源页面元素列表（不分页）
     *
     * @param selectDto 查询条件 DTO
     * @return 应用资源页面元素视图对象列表
     */
    @Override
    public Result<List<ResourcePageElementVo>> selectList(ResourcePageElementSelectDto selectDto) {
        return Result.success(resourcePageElementService.selectList(selectDto));
    }

    /**
     * 分页查询应用资源页面元素列表
     *
     * @param selectDto 分页参数及查询条件
     * @return 分页后的应用资源页面元素视图对象数据
     */
    @Override
    public Result<PageData<ResourcePageElementVo>> selectPage(PageSelectListDto<ResourcePageElementSelectDto> selectDto) {
        return Result.successPage(resourcePageElementService.selectPage(selectDto));
    }

    /**
     * 新增或更新应用资源页面元素
     *
     * @param dto 应用资源页面元素数据传输对象（已校验）
     * @return 保存成功后的主键 ID
     */
    @PostMapping("/save")
    @Operation(summary = "新增或更新页面元素", description = "新增或更新应用资源页面元素信息")
    public Result<Long> save(@RequestBody @Validated ResourcePageElementDto dto) {
        return Result.success(resourcePageElementService.save(dto));
    }

    /**
     * 根据主键删除应用资源页面元素记录
     *
     * @param id 应用资源页面元素主键 ID
     * @return 受影响的记录行数
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除页面元素记录", description = "根据主键删除应用资源页面元素记录")
    public Result<Integer> delete(@Parameter(description = "页面元素标识") @PathVariable Long id) {
        return Result.success(resourcePageElementService.delete(id));
    }
}
