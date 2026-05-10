package com.g2rain.basis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.g2rain.common.model.BaseSelectListDto;

import java.util.List;


/**
 * 资源接口表查询入参DTO
 * 用于ResourceApiDao.selectList方法的条件筛选
 * 表名: resource_api
 *
 * @author G2rain Generator
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "资源接口查询入参 DTO")
public class ResourceApiSelectDto extends BaseSelectListDto {

    /**
     * 服务逻辑编码
     */
    @Schema(description = "服务逻辑编码")
    private String serviceCode;

    /**
     * 资源接口标签
     */
    @Schema(description = "资源接口标签")
    private String apiTags;

    /**
     * 资源接口名称
     */
    @Schema(description = "资源接口名称")
    private String name;

    /**
     * 接口请求方法
     */
    @Schema(description = "接口请求方法")
    private String method;

    /**
     * 接口请求路径
     */
    @Schema(description = "接口请求路径")
    private String path;

    /**
     * 资源接口说明
     */
    @Schema(description = "资源接口说明")
    private String description;

    /**
     * 请求路径和请求方法集合
     */
    @Schema(hidden = true, description = "请求路径与请求方法集合(用于批量匹配)")
    private List<ApiEndpointPairDto> pairs;

}
