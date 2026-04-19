package com.g2rain.basis.dto;

import com.g2rain.common.model.BaseSelectListDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 应用资源接口地址表查询入参DTO
 * 用于ResourceApiEndpointDao.selectList方法的条件筛选
 * 表名: resource_api_endpoint
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "应用资源接口地址查询入参 DTO")
public class ResourceApiSelectDto extends BaseSelectListDto {

    /**
     * 应用标识
     */
    @Schema(description = "应用标识")
    private Long applicationId;

    /**
     * 接口地址标识
     */
    @Schema(description = "接口地址标识")
    private Long apiEndpointId;

    /**
     * 接口标签, 接口分类
     */
    @Schema(description = "接口标签, 接口分类")
    private String apiTag;

    /**
     * 接口名称
     */
    @Schema(description = "接口名称")
    private String apiName;

    /**
     * 请求方法
     */
    @Schema(description = "请求方法")
    private String requestMethod;

    /**
     * 接口路径
     */
    @Schema(description = "接口路径")
    private String apiUrl;
}
