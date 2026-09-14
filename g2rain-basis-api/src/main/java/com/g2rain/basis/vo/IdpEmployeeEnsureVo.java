package com.g2rain.basis.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 服务间 ensure 员工 User 结果。
 */
@Getter
@Setter
@NoArgsConstructor
public class IdpEmployeeEnsureVo {

    private Long organId;
    private Long passportId;
    private Long userId;
    /** true 表示本次新建；false 表示已存在 */
    private boolean created;
}
