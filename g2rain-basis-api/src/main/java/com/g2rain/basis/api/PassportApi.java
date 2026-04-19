package com.g2rain.basis.api;

import com.g2rain.basis.dto.PassportDto;
import com.g2rain.basis.dto.PassportSelectDto;
import com.g2rain.basis.vo.PassportVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


/**
 * 账号表API接口
 * 表名: passport
 *
 * @author Alpha
 */
@Tag(name = "账号", description = "账号表相关接口")
public interface PassportApi {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return 数据列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询账号列表", description = "根据查询条件返回账号列表")
    Result<List<PassportVo>> selectList(PassportSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询账号列表", description = "分页查询账号列表")
    Result<PageData<PassportVo>> selectPage(PageSelectListDto<PassportSelectDto> selectDto);

    /**
     * 新增或更新账号信息
     *
     * @param dto 账号数据传输对象（已校验）
     * @return 保存成功后的主键 ID
     */
    @PostMapping("/save")
    @Operation(summary = "新增或更新账号", description = "新增或更新账号信息")
    Result<Long> save(@RequestBody @Validated PassportDto dto);
}
