package com.g2rain.basis.controller;

import com.g2rain.basis.api.IdpEmployeeApi;
import com.g2rain.basis.dto.IdpEmployeeEnsureRequest;
import com.g2rain.basis.service.IdpEmployeeService;
import com.g2rain.basis.vo.IdpEmployeeEnsureVo;
import com.g2rain.common.model.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IdpEmployeeController implements IdpEmployeeApi {

    @Resource(name = "idpEmployeeServiceImpl")
    private IdpEmployeeService idpEmployeeService;

    @Override
    public Result<IdpEmployeeEnsureVo> ensure(IdpEmployeeEnsureRequest request) {
        return Result.success(idpEmployeeService.ensure(request));
    }
}
