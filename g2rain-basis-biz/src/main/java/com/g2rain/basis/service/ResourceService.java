package com.g2rain.basis.service;


/**
 * 资源服务接口
 * <p>
 * 提供资源上传等操作的服务接口。
 * </p>
 *
 * @author alpha
 * @since 2026/1/19
 */
public interface ResourceService {

    /**
     * 上传资源内容。
     *
     * @param applicationId 应用 ID，用于标识资源所属应用
     * @param content       资源内容字符串
     */
    void resourceUpload(Long applicationId, String content);
}
