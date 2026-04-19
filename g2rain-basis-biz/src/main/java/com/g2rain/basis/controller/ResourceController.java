package com.g2rain.basis.controller;


import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.service.ResourceService;
import com.g2rain.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 资源管理控制器
 * <p>
 * 提供应用资源上传接口，用于上传资源文件内容并处理。
 * 上传的文件内容会被读取为字符串传递给业务服务进行处理。
 * 主要处理页面、页面元素、接口地址的数据录入
 *
 * @author Alpha
 * @since 2026/1/19
 */
@RestController
@RequestMapping("/resource")
public class ResourceController {
    @Resource(name = "resourceServiceImpl")
    private ResourceService resourceService;

    /**
     * 上传应用资源文件
     *
     * @param applicationId 应用 ID
     * @param file          上传的资源文件
     * @return 操作结果，成功返回空内容，失败返回错误码 {@link BasisErrorCode#RESOURCE_UPLOAD_FAIL}
     */
    @PostMapping("/{applicationId}/upload")
    @Operation(summary = "上传应用资源文件", description = "上传并解析指定应用的资源文件内容")
    public Result<String> resourceUpload(@Parameter(description = "应用标识") @PathVariable Long applicationId, @Parameter(description = "资源文件") @RequestPart("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error(BasisErrorCode.RESOURCE_UPLOAD_FAIL);
        }

        try {
            // 将文件内容读取到字符串
            resourceService.resourceUpload(applicationId, new String(file.getBytes()));
            return Result.success();
        } catch (Exception e) {
            return Result.error(BasisErrorCode.RESOURCE_UPLOAD_FAIL);
        }
    }
}
