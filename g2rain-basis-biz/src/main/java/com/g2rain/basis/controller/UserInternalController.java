package com.g2rain.basis.controller;

import com.g2rain.basis.api.UserInternalApi;
import com.g2rain.basis.dto.UserSelectDto;
import com.g2rain.basis.service.UserService;
import com.g2rain.basis.vo.UserVo;
import com.g2rain.common.model.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户内部控制器。
 */
@RestController
@RequestMapping("/internal/user")
public class UserInternalController implements UserInternalApi {

    @Resource(name = "userServiceImpl")
    private UserService userService;

    @Override
    public Result<List<UserVo>> selectListWithoutIsolation(UserSelectDto selectDto) {
        return Result.success(userService.selectListWithoutIsolation(selectDto));
    }
}
