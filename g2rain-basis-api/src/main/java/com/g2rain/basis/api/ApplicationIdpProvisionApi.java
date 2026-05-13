package com.g2rain.basis.api;

import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import com.g2rain.basis.dto.ApplicationIdpProvisionSelectDto;
import com.g2rain.basis.vo.ApplicationIdpProvisionVo;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


/**
 * 外部身份源应用与平台应用的绑定API接口
 * 表名: application_idp_provision
 *
 * @author G2rain Generator
 */
public interface ApplicationIdpProvisionApi {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件DTO
     * @return 数据列表
     */
    @GetMapping("/list")
    Result<List<ApplicationIdpProvisionVo>> selectList(ApplicationIdpProvisionSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页数据
     */
    @GetMapping("/page")
    Result<PageData<ApplicationIdpProvisionVo>> selectPage(PageSelectListDto<ApplicationIdpProvisionSelectDto> selectDto);
}