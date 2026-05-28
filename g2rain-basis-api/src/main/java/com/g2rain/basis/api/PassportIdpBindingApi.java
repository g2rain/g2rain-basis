package com.g2rain.basis.api;

import com.g2rain.basis.dto.PassportIdpBindingBindDto;
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
 * 账号与外部身份源绑定表 API 接口。
 * <p>
 * <b>IAM 集成：</b>IAM 为独立进程，通过 REST 或 OpenFeign（由 IAM 工程声明 {@code @FeignClient} 指向本服务路径
 * {@code /passport_idp_binding}）调用本接口完成落库。字段 {@code idp_type}、{@code idp_application_code}、{@code bind_mode} 与枚举
 * {@link com.g2rain.basis.enums.IdpType}、{@link com.g2rain.basis.enums.IdpBindMode} 存库值一致（均为枚举名）。
 * 唯一约束为 {@code (idp_type, idp_subject, idp_application_code)}。
 * </p>
 * <p>
 * <b>多渠道与换票语义：</b>支持多种身份源渠道；当前阶段 IAM 仅实现钉钉（{@link com.g2rain.basis.enums.IdpType#DINGTALK}）。
 * {@code bind_mode} 表示钉钉「企业内部应用」与「第三方企业应用」两条换票/用户解析链路，与 OAuth 授权码、
 * 访问令牌两跳无对应关系。
 * </p>
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

    /**
     * 已登录通行证绑定外部身份源（幂等）
     *
     * @param dto 绑定请求（已校验）
     * @return 绑定记录主键 ID
     */
    @PostMapping("/bind")
    @Operation(summary = "绑定外部身份源", description = "已登录用户扫码绑定钉钉；USER 会话且机构管理员可自动建立 idp_enterprise_organ，"
        + "普通用户须已有企业-机构绑定；校验主体冲突")
    Result<Long> bind(@RequestBody @Validated PassportIdpBindingBindDto dto);
}
