package com.g2rain.basis.dto;

import com.g2rain.common.model.BaseSelectListDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;


/**
 * 应用资源页面表查询入参DTO
 * 用于ResourcePageDao.selectList方法的条件筛选
 * 表名: resource_page
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "应用资源页面查询入参 DTO")
public class ResourcePageSelectDto extends BaseSelectListDto {

    /**
     * 应用标识
     */
    @Schema(description = "应用标识")
    private Long applicationId;

    /**
     * 页面名称
     */
    @Schema(description = "页面名称")
    private String pageName;

    /**
     * 页面编码
     */
    @Schema(description = "页面编码")
    private String pageCode;

    /**
     * 页面编码集合
     */
    @Schema(description = "页面编码集合")
    private Set<String> pageCodes;

    /**
     * 链接路径
     */
    @Schema(description = "链接路径")
    private String linkPath;
}
