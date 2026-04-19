package com.g2rain.basis.enums;


import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.utils.Strings;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 资源类型枚举。
 * <p>
 * 用于区分系统中不同类型的资源，以便在权限控制或访问管理中进行区分。
 * </p>
 *
 * <ul>
 *   <li>{@link #MENU}：表示菜单资源。</li>
 *   <li>{@link #PAGE}：表示页面资源。</li>
 *   <li>{@link #PAGE_ELEMENT}：表示页面上的元素资源，如按钮、表单控件等。</li>
 *   <li>{@link #API_ENDPOINT}：表示接口地址资源，用于权限控制或访问授权。</li>
 * </ul>
 *
 * @author alpha
 * @since 2026/1/15
 */
@Schema(description = "资源类型枚举")
public enum ResourceType {

    /**
     * 菜单
     */
    @Schema(description = "菜单资源")
    MENU,

    /**
     * 页面
     */
    @Schema(description = "页面资源")
    PAGE,

    /**
     * 页面元素
     */
    @Schema(description = "页面元素资源（按钮、表单控件等）")
    PAGE_ELEMENT,

    /**
     * 接口地址
     */
    @Schema(description = "接口地址资源")
    API_ENDPOINT;

    /**
     * 根据给定的字符串名称校验该名称是否存在于枚举中。
     * <p>
     * 如果字符串名称对应的枚举不存在，则抛出参数值无效的异常。
     * </p>
     *
     * @param name 枚举名称字符串，表示应用类型的名称。
     * @throws BusinessException 如果指定的枚举名称无效，抛出此异常，表示参数值无效。
     */
    public static ResourceType fromName(String name) {
        if (Strings.isNotBlank(name)) {
            // 遍历所有枚举值，检查是否与传入的名称匹配
            for (ResourceType type : values()) {
                // 如果找到匹配的枚举，直接返回
                if (type.name().equals(name)) {
                    return type;
                }
            }
        }

        // 如果没有找到匹配的枚举，抛出业务异常
        throw new BusinessException(
            SystemErrorCode.PARAM_VAL_INVALID,
            name
        );
    }
}
