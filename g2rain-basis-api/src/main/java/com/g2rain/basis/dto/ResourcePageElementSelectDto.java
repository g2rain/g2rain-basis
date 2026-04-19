package com.g2rain.basis.dto;

import com.g2rain.common.model.BaseSelectListDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;


/**
 * 应用资源页面元素表查询入参DTO
 * 用于ResourcePageElementDao.selectList方法的条件筛选
 * 表名: resource_page_element
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "应用资源页面元素查询入参 DTO")
public class ResourcePageElementSelectDto extends BaseSelectListDto {

    /**
     * 应用标识
     */
    @Schema(description = "应用标识")
    private Long applicationId;

    /**
     * 页面编码
     */
    @Schema(description = "页面编码")
    private String pageCode;

    /**
     * 页面元素名称
     */
    @Schema(description = "页面元素名称")
    private String pageElementName;

    /**
     * 页面元素编码
     */
    @Schema(description = "页面元素编码")
    private String pageElementCode;

    /**
     * 页面元素编码集合
     */
    @Schema(description = "页面元素编码集合")
    private Set<String> pageElementCodes;
}
