package com.g2rain.basis.api;

import com.g2rain.basis.dto.UserSelectDto;
import com.g2rain.basis.vo.UserVo;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * 用户内部 API，仅供 IAM 等服务间无隔离查询。
 */
@Tag(name = "用户（内部）", description = "IAM consent 等服务间无隔离用户查询")
public interface UserInternalApi {

    /**
     * 按条件查询用户列表（无隔离语义）。
     */
    @GetMapping("/list")
    @Operation(summary = "无隔离查询用户列表", description = "跨机构按 passportId 查询用户，仅供服务间调用")
    Result<List<UserVo>> selectListWithoutIsolation(UserSelectDto selectDto);
}
