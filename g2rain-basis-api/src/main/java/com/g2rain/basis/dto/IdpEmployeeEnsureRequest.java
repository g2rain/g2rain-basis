package com.g2rain.basis.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 服务间：按 organId + passportId 幂等确保机构员工 User（非 ADMIN）。
 */
@Getter
@Setter
@NoArgsConstructor
public class IdpEmployeeEnsureRequest {

    @NotNull
    private Long organId;

    @NotNull
    private Long passportId;

    /** 展示名，可选 */
    private String realName;

    /** 手机号，可选 */
    private String mobile;

    /** 邮箱，可选 */
    private String email;
}
