package com.g2rain.basis.service;

import com.g2rain.basis.dto.OrganDto;

/**
 * 机构开通编排服务。
 *
 * <p>用于在机构新增时完成初始化能力（默认管理员角色、最小功能开通）。</p>
 */
public interface OrganProvisionService {

    /**
     * 在隔离语义下创建或修改机构，并在新增时初始化默认能力。
     *
     * @param dto 机构信息
     * @return 机构标识
     */
    long createOrganWithIsolation(OrganDto dto);

    /**
     * 在非隔离语义下创建或修改机构，并在新增时初始化默认能力。
     *
     * @param dto 机构信息
     * @return 机构标识
     */
    long createOrganWithoutIsolation(OrganDto dto);
}
