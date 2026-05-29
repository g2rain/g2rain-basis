package com.g2rain.basis.controller;


import com.g2rain.basis.api.OrganInviteApi;
import com.g2rain.basis.dto.OrganInviteGenerateDto;
import com.g2rain.basis.service.OrganInviteService;
import com.g2rain.basis.vo.OrganInviteVo;
import com.g2rain.common.model.Result;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 机构邀请码控制器
 */
@RestController
@RequestMapping("/organ/invite")
public class OrganInviteController implements OrganInviteApi {

    @Resource(name = "organInviteServiceImpl")
    private OrganInviteService organInviteService;

    @Override
    public Result<OrganInviteVo> generate(@RequestBody @Validated OrganInviteGenerateDto dto) {
        return Result.success(organInviteService.generate(dto));
    }
}
