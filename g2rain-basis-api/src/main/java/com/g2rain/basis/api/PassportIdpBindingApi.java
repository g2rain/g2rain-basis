package com.g2rain.basis.api;

import com.g2rain.basis.dto.PassportIdpBindingDto;
import com.g2rain.basis.dto.PassportIdpBindingSelectDto;
import com.g2rain.basis.vo.PassportIdpBindingVo;
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
 * 账号与外部身份源绑定表API接口
 * 表名: passport_idp_binding
 *
 * @author G2rain Generator
 */
@Tag(name = "账号与外部身份源绑定", description = "账号与外部身份源绑定表相关接口")
public interface PassportIdpBindingApi {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件DTO
     * @return 数据列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询账号与外部身份源绑定列表", description = "根据查询条件返回绑定列表")
    Result<List<PassportIdpBindingVo>> selectList(PassportIdpBindingSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询账号与外部身份源绑定列表", description = "分页查询绑定列表")
    Result<PageData<PassportIdpBindingVo>> selectPage(PageSelectListDto<PassportIdpBindingSelectDto> selectDto);

    /**
     * 新增或更新账号与外部身份源绑定信息
     *
     * @param dto 数据传输对象（已校验）
     * @return 保存成功后的主键 ID
     */
    @PostMapping("/save")
    @Operation(summary = "新增或更新绑定", description = "新增或更新账号与外部身份源绑定信息")
    Result<Long> save(@RequestBody @Validated PassportIdpBindingDto dto);
}
