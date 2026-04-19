package com.g2rain.basis.service;

import com.g2rain.basis.dto.PassportDto;
import com.g2rain.basis.dto.PassportSelectDto;
import com.g2rain.basis.dto.PassportUpdatePasswordDto;
import com.g2rain.basis.dto.UpdateStatusDto;
import com.g2rain.basis.vo.PassportVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;

import java.util.List;

/**
 * 账号表服务接口
 * 表名: passport
 *
 * @author Alpha
 */
public interface PassportService {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return VO 对象列表
     */
    List<PassportVo> selectList(PassportSelectDto selectDto);

    /**
     * 根据 ID 查询账号信息
     *
     * @param id 主键 ID
     * @return 账号 VO 对象
     */
    PassportVo selectById(Long id);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页 VO 数据
     */
    PageData<PassportVo> selectPage(PageSelectListDto<PassportSelectDto> selectDto);

    /**
     * 新增或更新数据
     *
     * @param dto 数据传输对象
     * @return 操作结果（影响行数）
     */
    Long save(PassportDto dto);

    /**
     * 根据 ID 删除数据
     *
     * @param id 主键 ID
     * @return 操作结果（影响行数）
     */
    int delete(Long id);

    /**
     * 更新用户密码。
     *
     * @param id  用户的ID，指定要更新密码的用户。
     * @param dto 包含新密码信息的数据传输对象。
     * @return 数据库中受影响的行数（通常为 1 表示成功，0 表示没有变化）。
     */
    int updatePassword(Long id, PassportUpdatePasswordDto dto);

    /**
     * 更新用户状态。
     *
     * @param id  用户的ID，指定要更新状态的用户。
     * @param dto 包含新状态信息的数据传输对象。
     * @return 数据库中受影响的行数（通常为 1 表示成功，0 表示没有变化）。
     */
    int updateStatus(Long id, UpdateStatusDto dto);
}
