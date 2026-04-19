package com.g2rain.basis.dto;

import com.g2rain.common.model.BaseSelectListDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


/**
 * 接口地址表查询入参DTO
 * 用于ApiEndpointDao.selectList方法的条件筛选
 * 表名: api_endpoint
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "接口地址查询入参 DTO")
public class ApiEndpointSelectDto extends BaseSelectListDto {

    /**
     * 接口名称
     */
    @Schema(description = "接口名称")
    private String apiName;

    /**
     * 接口路径
     */
    @Schema(description = "接口路径")
    private String apiUrl;

    /**
     * 请求方法
     */
    @Schema(description = "请求方法", example = "GET")
    private String requestMethod;

    /**
     * 请求路径和请求方法集合
     */
    @Schema(description = "请求路径与请求方法集合(用于批量匹配)")
    private List<ApiEndpointPairDto> pairs;

    /**
     * 接口标签, 接口分类
     */
    @Schema(description = "接口标签(接口分类)")
    private String apiTag;

    /**
     * 业务说明
     */
    @Schema(description = "业务说明")
    private String description;
}
