package com.g2rain.basis.service;


import com.g2rain.basis.dto.OrganInviteGenerateDto;
import com.g2rain.basis.vo.OrganInviteVo;

/**
 * 机构邀请码服务
 */
public interface OrganInviteService {

    /**
     * 生成机构邀请码
     */
    OrganInviteVo generate(OrganInviteGenerateDto dto);
}
