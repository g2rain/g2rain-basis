package com.g2rain.basis.dao.po;

import com.g2rain.common.model.BasePo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 接口权限值对象 Po
 *
 * @author Alpha
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AuthorityApiEndpointPo extends BasePo {

    /**
     * 接口名称
     */
    private String apiName;

    /**
     * 接口路径
     */
    private String apiUrl;

    /**
     * 请求方法
     */
    private String requestMethod;

    /**
     * 接口标签, 接口分类
     */
    private String apiTag;

    /**
     * 接口地址状态
     */
    private String status;
}
