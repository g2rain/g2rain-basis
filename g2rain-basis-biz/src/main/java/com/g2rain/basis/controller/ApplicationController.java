package com.g2rain.basis.controller;

import com.g2rain.basis.api.ApplicationApi;
import com.g2rain.basis.dto.AppIdNameMapSelectDto;
import com.g2rain.basis.dto.ApplicationDto;
import com.g2rain.basis.dto.ApplicationSelectDto;
import com.g2rain.basis.dto.UpdateStatusDto;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.enums.PublicKeyFormat;
import com.g2rain.basis.service.ApplicationService;
import com.g2rain.basis.utils.BasisUtils;
import com.g2rain.basis.vo.ApplicationIdNameVo;
import com.g2rain.basis.vo.ApplicationVo;
import com.g2rain.basis.vo.PublicKeyDescriptorVo;
import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import com.g2rain.common.utils.Asserts;
import com.g2rain.common.utils.Collections;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 应用表控制器
 * 表名: application
 *
 * @author Alpha
 */
@RestController
@RequestMapping("/application")
public class ApplicationController implements ApplicationApi {

    @Resource(name = "applicationServiceImpl")
    private ApplicationService applicationService;

    /**
     * 查询应用列表（不分页）
     *
     * @param selectDto 查询条件 DTO
     * @return 应用视图对象列表
     */
    @Override
    public Result<List<ApplicationVo>> selectList(ApplicationSelectDto selectDto) {
        return Result.success(applicationService.selectListIsolation(selectDto));
    }

    /**
     * 分页查询应用列表
     *
     * @param selectDto 分页参数及查询条件
     * @return 分页后的应用视图对象数据
     */
    @Override
    public Result<PageData<ApplicationVo>> selectPage(PageSelectListDto<ApplicationSelectDto> selectDto) {
        return Result.successPage(applicationService.selectPage(selectDto));
    }

    /**
     * 新增或更新应用信息
     *
     * @param dto 应用数据传输对象（已校验）
     * @return 保存成功后的主键 ID
     */
    @PostMapping("/save")
    @Operation(summary = "新增或更新应用信息", description = "新增或更新应用基础信息")
    public Result<Long> save(@RequestBody @Validated ApplicationDto dto) {
        return Result.success(applicationService.save(dto));
    }

    /**
     * 根据主键删除应用记录
     *
     * @param id 应用主键 ID
     * @return 受影响的记录行数
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除应用记录", description = "根据主键删除应用记录")
    public Result<Integer> delete(@Parameter(description = "应用标识") @PathVariable Long id) {
        return Result.success(applicationService.delete(id));
    }

    /**
     * 获取指定应用的公钥信息（结构化描述）。
     *
     * <p>返回对象 {@link com.g2rain.basis.vo.PublicKeyDescriptorVo} 包含：</p>
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
    @Override
    public Result<PublicKeyDescriptorVo> getPublicKeyDescriptor(String applicationCode) {
        ApplicationSelectDto selectDto = new ApplicationSelectDto();
        selectDto.setApplicationCode(applicationCode);
        List<ApplicationVo> applications = applicationService.selectListIsolation(selectDto);
        Asserts.isTrue(Collections.isNotEmpty(applications), SystemErrorCode.PARAM_VAL_INVALID, "applicationCode");
        return Result.success(applicationService.getPublicKeyDescriptor(applications.getFirst().getId()));
    }

    /**
     * 下载应用对应的 PEM 格式公钥
     *
     * @param id 应用主键 ID
     * @return PEM 格式的公私钥对信息
     */
    @GetMapping("/{id}/public_key")
    @Operation(summary = "下载应用公钥", description = "下载指定应用的 PEM 或 DER 格式公钥文件")
    public ResponseEntity<byte[]> getPublicKey(@Parameter(description = "应用标识") @PathVariable Long id) {
        PublicKeyDescriptorVo publicKey = applicationService.getPublicKeyDescriptor(id);
        byte[] body;
        String filename;
        MediaType contentType;
        if (PublicKeyFormat.DER.name().equalsIgnoreCase(publicKey.getPublicKeyFormat())) {
            body = BasisUtils.toDer(publicKey.getPublicKey());
            filename = "public_key.der";
            contentType = MediaType.APPLICATION_OCTET_STREAM;
        } else {
            body = publicKey.getPublicKey().getBytes(StandardCharsets.US_ASCII);
            filename = "public_key.pem";
            contentType = MediaType.TEXT_PLAIN;
        }

        return ResponseEntity.ok()
            .contentType(contentType)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .body(body);
    }

    /**
     * 检查指定应用是否已配置公钥
     *
     * @param id 应用标识
     * @return 是否已配置公钥
     */
    @GetMapping("/{id}/has_public_key")
    @Operation(summary = "检查应用公钥是否存在", description = "检查指定应用是否已配置公钥")
    public Result<Boolean> hasPublicKey(@Parameter(description = "应用标识") @PathVariable Long id) {
        return Result.success(applicationService.hasPublicKey(id));
    }

    /**
     * 上传 PEM/DER 格式公钥
     *
     * @param id                 应用标识
     * @param publicKeyAlgorithm 公钥算法
     * @param file               公钥内容
     * @return 保存数据成功的记录数量
     */
    @PostMapping("/{id}/public_key")
    @Operation(summary = "上传或更新应用公钥", description = "上传 PEM/DER 公钥文件并更新应用公钥配置")
    public Result<Integer> upsertPublicKey(@Parameter(description = "应用标识") @PathVariable Long id, @Parameter(description = "公钥算法") @RequestPart("publicKeyAlgorithm") String publicKeyAlgorithm, @Parameter(description = "公钥文件") @RequestPart("file") MultipartFile file) {
        try {
            return Result.success(applicationService.upsertPublicKey(id, publicKeyAlgorithm, file.getBytes()));
        } catch (IOException e) {
            throw new BusinessException(BasisErrorCode.PUB_KEY_INVALID_KEY);
        }
    }

    /**
     * 修改应用状态
     *
     * @param id 应用主键 ID
     * @return 修改后记录数量
     */
    @PostMapping("/{id}/status")
    @Operation(summary = "更新应用状态", description = "根据主键更新应用启用状态")
    public Result<Integer> updateStatus(@Parameter(description = "应用标识") @PathVariable Long id, @RequestBody @Validated UpdateStatusDto dto) {
        return Result.success(applicationService.updateStatus(id, dto));
    }

    /**
     * 批量获取应用 ID 对应的 ID–名称映射
     *
     * @param selectDto 查询条件
     * @return 包含应用 ID 和名称的列表
     */
    @GetMapping("/id_name_map")
    @Operation(summary = "查询应用名称映射", description = "根据查询条件获取应用 ID 与名称映射列表")
    public Result<List<ApplicationIdNameVo>> selectApplicationIdNameMap(ApplicationSelectDto selectDto) {
        return Result.success(applicationService.selectApplicationIdNameMap(selectDto));
    }

    /**
     * 批量获取应用 ID 对应的 ID–名称映射
     *
     * @param dto 查询条件
     * @return 包含应用 ID 和名称的列表
     */
    @Override
    public Result<List<ApplicationIdNameVo>> selectAppIdNameMap(AppIdNameMapSelectDto dto) {
        ApplicationSelectDto selectDto = new ApplicationSelectDto();
        selectDto.setIds(dto.getIds());
        return Result.success(applicationService.selectApplicationIdNameMap(selectDto));
    }
}
