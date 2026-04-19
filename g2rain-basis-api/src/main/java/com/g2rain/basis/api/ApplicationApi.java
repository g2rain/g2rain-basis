package com.g2rain.basis.api;

import com.g2rain.basis.dto.AppIdNameMapSelectDto;
import com.g2rain.basis.dto.ApplicationSelectDto;
import com.g2rain.basis.vo.ApplicationIdNameVo;
import com.g2rain.basis.vo.ApplicationVo;
import com.g2rain.basis.vo.PublicKeyDescriptorVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


/**
 * 应用表API接口
 * 表名: application
 *
 * @author Alpha
 */
@Tag(name = "应用", description = "应用表相关接口")
public interface ApplicationApi {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return 数据列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询应用列表", description = "根据查询条件返回应用列表")
    Result<List<ApplicationVo>> selectList(ApplicationSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询应用列表", description = "分页查询应用列表")
    Result<PageData<ApplicationVo>> selectPage(PageSelectListDto<ApplicationSelectDto> selectDto);

    /**
     * 获取指定应用的公钥信息（结构化描述）。
     *
     * <p>返回对象 {@link PublicKeyDescriptorVo} 包含：</p>
     * <ul>
     *     <li>公钥算法 {@code publicKeyAlgorithm}（如 EC、RSA 等）</li>
     *     <li>公钥格式 {@code publicKeyFormat}（PEM 或 DER）</li>
     *     <li>公钥内容 {@code publicKey}（字节数组，Feign 调用时自动 Base64 编码）</li>
     * </ul>
     *
     * <p>该接口主要用于系统间调用（Feign 调用），
     * 不触发文件下载，仅返回结构化公钥信息。</p>
     *
     * @param applicationCode 应用编码
     * @return 指定应用的公钥描述对象封装在 {@link Result} 中
     */
    @GetMapping("/{applicationCode}/public_key/descriptor")
    @Operation(summary = "获取应用公钥描述信息", hidden = true, description = "用于系统间调用（如 Feign），返回结构化公钥描述对象，不触发文件下载。")
    Result<PublicKeyDescriptorVo> getPublicKeyDescriptor(@Parameter(description = "应用编码") @PathVariable String applicationCode);

    /**
     * 批量获取应用 ID 对应的 ID–名称映射
     *
     * @param dto 查询条件
     * @return 包含应用 ID 和名称的列表
     */
    @PostMapping("/id_name_map")
    @Operation(summary = "查询应用名称映射", hidden = true, description = "根据应用标识集合查询应用标识与名称映射")
    Result<List<ApplicationIdNameVo>> selectAppIdNameMap(@RequestBody @Validated AppIdNameMapSelectDto dto);
}
