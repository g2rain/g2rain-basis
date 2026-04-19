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
 * 权限点资源关联表返回VO
 * 关联表名: control_unit_resource_relation
 * 功能：封装接口返回数据，继承BaseVo复用基础字段逻辑，隔离数据库实体与前端展示层
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "权限点资源关联 VO")
public class ControlUnitResourceRelationVo extends BaseVo {

    /**
     * 控制单元标识
     */
    @Schema(description = "控制单元标识")
    private Long controlUnitId;

    /**
     * 资源标识
     */
    @Schema(description = "资源标识")
    private Long resourceId;

    /**
     * 资源类型[MENU:菜单, PAGE:页面, PAGE_ELEMENT:页面元素, API_ENDPOINT:接口地址]
     */
    @Schema(description = "资源类型[MENU:菜单, PAGE:页面, PAGE_ELEMENT:页面元素, API_ENDPOINT:接口地址]")
    private String resourceType;

    /**
     * 激活状态[VISIBLE:显示, ENABLED:可用]
     */
    @Schema(description = "激活状态[VISIBLE:显示, ENABLED:可用]")
    private String status;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    @ConditionalJsonIgnore(adminCompany = AdminCompanyCondition.TRUE)
    @Schema(description = "删除标识[0:未删除, 1:已删除]")
    private Boolean deleteFlag;
}
