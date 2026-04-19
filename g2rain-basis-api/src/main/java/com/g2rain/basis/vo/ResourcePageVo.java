package com.g2rain.basis.vo;

import com.g2rain.common.json.AdminCompanyCondition;
import com.g2rain.common.json.ConditionalJsonIgnore;
import com.g2rain.common.model.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 应用资源页面表返回VO
 * 关联表名: resource_page
 * 功能：封装接口返回数据，继承BaseVo复用基础字段逻辑，隔离数据库实体与前端展示层
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "应用资源页面 VO")
public class ResourcePageVo extends BaseVo {

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
     * 链接路径
     */
    @Schema(description = "链接路径")
    private String linkPath;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    @ConditionalJsonIgnore(adminCompany = AdminCompanyCondition.TRUE)
    @Schema(description = "删除标识[0:未删除, 1:已删除]")
    private Boolean deleteFlag;
}
