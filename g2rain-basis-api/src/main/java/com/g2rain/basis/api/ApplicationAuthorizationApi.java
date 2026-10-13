package com.g2rain.basis.api;

import com.g2rain.basis.dto.ApplicationAuthorizationActivateSelfRequest;
import com.g2rain.basis.dto.ApplicationAuthorizationSelectDto;
import com.g2rain.basis.vo.ApplicationAuthorizationActivateSelfVo;
import com.g2rain.basis.vo.ApplicationAuthorizationVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


/**
 * 应用授权记录表API接口
 * 表名: application_authorization
 *
 * @author Alpha
 */
@Tag(name = "应用授权", description = "应用授权记录相关接口")
public interface ApplicationAuthorizationApi {

    @GetMapping("/list")
    @Operation(summary = "查询应用授权记录列表", description = "根据查询条件返回应用授权记录列表")
    Result<List<ApplicationAuthorizationVo>> selectList(ApplicationAuthorizationSelectDto selectDto);

    @GetMapping("/page")
    @Operation(summary = "分页查询应用授权记录列表", description = "分页查询应用授权记录列表")
    Result<PageData<ApplicationAuthorizationVo>> selectPage(PageSelectListDto<ApplicationAuthorizationSelectDto> selectDto);

    @PostMapping("/activate_self")
    @Operation(summary = "自动开通全部 SELF 控制域", hidden = true,
        description = "自动为该应用全部 SELF 控制域创建或恢复 application_authorization；无 SELF 时仅返回成功。"
            + " IAM consent 页预览由 IAM 本地拼装，不经过本接口。")
    Result<ApplicationAuthorizationActivateSelfVo> activateSelf(
        @Valid @RequestBody ApplicationAuthorizationActivateSelfRequest request);
}
