package com.g2rain.basis.service;

import com.g2rain.basis.dto.IdpEmployeeEnsureRequest;
import com.g2rain.basis.vo.IdpEmployeeEnsureVo;

public interface IdpEmployeeService {

    /**
     * 按 organId + passportId 查找 User；不存在则创建非 ADMIN 用户（不绑定 ADMIN 角色）。
     */
    IdpEmployeeEnsureVo ensure(IdpEmployeeEnsureRequest request);
}
