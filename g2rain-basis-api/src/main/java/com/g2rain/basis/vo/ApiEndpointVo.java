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
 * 接口地址表返回VO
 * 关联表名: api_endpoint
 * 功能：封装接口返回数据，继承BaseVo复用基础字段逻辑，隔离数据库实体与前端展示层
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "接口地址 VO")
public class ApiEndpointVo extends BaseVo {

    /**
     * 接口名称
     */
    @Schema(description = "接口名称", example = "查询用户列表")
    private String apiName;

    /**
     * 接口路径
     */
    @Schema(description = "接口路径", example = "/user/list")
    private String apiUrl;

    /**
     * 请求方法
     */
    @Schema(description = "请求方法", example = "GET")
    private String requestMethod;

    /**
     * 接口标签, 接口分类
     */
    @Schema(description = "接口标签（接口分类）", example = "用户管理")
    private String apiTag;

    /**
     * 业务说明
     */
    @Schema(description = "业务说明")
    private String description;

    /**
     * 删除标识[0:未删除, 1:已删除]
     */
    @ConditionalJsonIgnore(adminCompany = AdminCompanyCondition.TRUE)
    @Schema(description = "删除标识(0 未删除, 1 已删除)", example = "false")
    private Boolean deleteFlag;
}
