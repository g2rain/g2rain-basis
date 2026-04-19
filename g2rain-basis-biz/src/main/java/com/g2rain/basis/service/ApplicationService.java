package com.g2rain.basis.service;

import com.g2rain.basis.dto.ApplicationDto;
import com.g2rain.basis.dto.ApplicationSelectDto;
import com.g2rain.basis.dto.UpdateStatusDto;
import com.g2rain.basis.vo.ApplicationIdNameVo;
import com.g2rain.basis.vo.ApplicationScopeVo;
import com.g2rain.basis.vo.ApplicationVo;
import com.g2rain.basis.vo.PublicKeyDescriptorVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;

import java.util.List;

/**
 * 应用表服务接口
 * 表名: application
 *
 * @author Alpha
 */
public interface ApplicationService {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return VO 对象列表
     */
    List<ApplicationVo> selectListIsolation(ApplicationSelectDto selectDto);

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return VO 对象列表
     */
    List<ApplicationVo> selectList(ApplicationSelectDto selectDto);

    /**
     * 根据条件查询 ID 和 Name 映射列表
     *
     * @param selectDto 查询条件 DTO
     * @return VO 对象列表
     */
    List<ApplicationIdNameVo> selectApplicationIdNameMap(ApplicationSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页 VO 数据
     */
    PageData<ApplicationVo> selectPage(PageSelectListDto<ApplicationSelectDto> selectDto);

    /**
     * 新增或更新数据
     *
     * @param dto 数据传输对象
     * @return 操作结果（影响行数）
     */
    Long save(ApplicationDto dto);

    /**
     * 根据应用标识删除数据
     *
     * @param id 应用标识
     * @return 操作结果（影响行数）
     */
    int delete(Long id);

    /**
     * 获取指定应用的公钥信息
     *
     * @param id 应用标识
     * @return PEM 格式公私钥对 VO
     */
    PublicKeyDescriptorVo getPublicKeyDescriptor(Long id);

    boolean hasPublicKey(Long id);

    /**
     * 上传 PEM/DER 格式公钥
     *
     * @param id                 应用标识
     * @param publicKeyAlgorithm 公钥算法
     * @param keyBytes           公钥内容
     * @return 保存数据成功的记录数量
     */
    Integer upsertPublicKey(Long id, String publicKeyAlgorithm, byte[] keyBytes);

    /**
     * 修改应用发布状态
     *
     * @param id  应用标识
     * @param dto 包含新状态信息的数据传输对象
     * @return 数据库中受影响的行数（通常为 1 表示成功，0 表示没有变化）
     */
    int updateStatus(Long id, UpdateStatusDto dto);

    /**
     * 查询机构下的应用作用域（ApplicationScopeVo 列表）。
     * <p>
     * 核心逻辑：
     * 1. 如果 organId 为 null，返回空列表。
     * 2. 如果 applicationId 为 null，则返回默认登录应用（landing=true）。
     * 3. 否则根据机构 ID 和入口应用 ID 查询可见应用集合，并转换为 ApplicationScopeVo。
     *
     * @param userId        用户标识
     * @param applicationId 入口应用标识
     * @return 可见的应用作用域列表
     */
    List<ApplicationScopeVo> selectApplicationScope(Long userId, Long applicationId);

    /**
     * 判断指定应用是否为默认登录应用（landing=true）。
     *
     * <p>逻辑说明：
     * 1. 如果应用不存在，返回 false；
     * 2. 如果应用存在但 landing 字段不为 true，返回 false；
     * 3. 如果应用存在且 landing 字段为 true，返回 true。
     *
     * @param id 应用标识
     * @return 如果应用存在且为默认登录应用返回 true，否则返回 false
     */
    boolean hasLandingApplication(Long id);

    /**
     * 查询应用明显
     *
     * @param id 应用标识
     * @return 应用明显
     */
    ApplicationVo selectOne(Long id);
}
