package com.g2rain.basis.service.impl;


import com.g2rain.basis.dto.UploadResourceDto;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.service.ResourceApiEndpointService;
import com.g2rain.basis.service.ResourcePageElementService;
import com.g2rain.basis.service.ResourcePageService;
import com.g2rain.basis.service.ResourceService;
import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.json.JsonCodec;
import com.g2rain.common.json.JsonCodecFactory;
import com.g2rain.common.utils.Asserts;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * 应用资源上传服务实现类
 * <p>
 * 核心功能：
 * <ul>
 *     <li>提供资源批量上传功能，包括页面、页面元素和接口地址</li>
 * </ul>
 *
 * @author Alpha
 * @since 2026/1/19
 */
@Service(value = "resourceServiceImpl")
public class ResourceServiceImpl implements ResourceService {

    private static final JsonCodec jsonCodec = JsonCodecFactory.instance();

    @Resource(name = "resourcePageServiceImpl")
    private ResourcePageService resourcePageService;

    @Resource(name = "resourcePageElementServiceImpl")
    private ResourcePageElementService resourcePageElementService;

    @Resource(name = "resourceApiEndpointServiceImpl")
    private ResourceApiEndpointService resourceApiEndpointService;

    /**
     * 上传应用资源
     *
     * <p>逻辑说明：
     * <ul>
     *     <li>解析 JSON 内容为 UploadResourceDto</li>
     *     <li>校验解析结果是否为空</li>
     *     <li>调用各子服务批量保存页面、页面元素和接口地址</li>
     * </ul>
     *
     * @param applicationId 应用 ID
     * @param content       JSON 字符串，包含页面、页面元素和接口信息
     * @throws BusinessException 当资源解析失败时抛出
     */
    @Override
    @Transactional
    @SuppressWarnings("null")
    public void resourceUpload(Long applicationId, String content) {
        UploadResourceDto resource = jsonCodec.str2obj(content, UploadResourceDto.class);
        Asserts.isTrue(Objects.nonNull(resource), BasisErrorCode.RESOURCE_UPLOAD_FAIL);
        resourcePageService.batchSave(applicationId, resource.getPages());
        resourcePageElementService.batchSave(applicationId, resource.getPageElements());
        resourceApiEndpointService.batchSave(applicationId, resource.getApiEndpoints());
    }
}
