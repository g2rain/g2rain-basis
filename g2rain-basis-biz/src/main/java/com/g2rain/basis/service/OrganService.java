package com.g2rain.basis.service;

import com.g2rain.basis.dto.OrganClosureDto;
import com.g2rain.basis.dto.OrganDto;
import com.g2rain.basis.dto.OrganSelectDto;
import com.g2rain.basis.dto.UpdateStatusDto;
import com.g2rain.basis.vo.OrganHierarchicalRelationVo;
import com.g2rain.basis.vo.OrganIdNameVo;
import com.g2rain.basis.vo.OrganVo;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;

import java.util.List;
import java.util.Set;

/**
 * 机构表服务接口
 * 表名: organ
 *
 * @author Alpha
 */
public interface OrganService {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return VO 对象列表
     */
    List<OrganVo> selectList(OrganSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页 VO 数据
     */
    PageData<OrganVo> selectPage(PageSelectListDto<OrganSelectDto> selectDto);

    /**
     * 查询指定机构的层级关系
     *
     * @return 包含指定机构层级关系的列表
     */
    List<OrganHierarchicalRelationVo> getHierarchicalRelations();

    /**
     * 根据机构名称模糊匹配机构
     *
     * @param organName 机构名称
     * @return 实体对象列表
     */
    List<OrganIdNameVo> searchOrgans(String organName);

    /**
     * 根据机构 ID 集合获取对应的 ID–名称映射
     *
     * @param ids 机构 ID 集合
     * @return 包含机构 ID 和名称的列表
     */
    List<OrganIdNameVo> selectOrganIdNameMap(Set<Long> ids);

    /**
     * 检查指定机构是否存在与指定祖先机构的层级关系
     *
     * @param childId  子机构 ID
     * @param parentId 祖先机构 ID
     * @return 如果两个ID都为空, 返回 false;
     * 如果 childId 等于 parentId, 返回 true;
     * 否则查询数据库判断层级关系是否存在
     */
    boolean checkHierarchyRelation(Long childId, Long parentId);

    /**
     * 新增或更新数据
     *
     * @param dto 数据传输对象
     * @return 操作结果（影响行数）
     */
    Long save(OrganDto dto);

    /**
     * 根据 ID 删除数据
     *
     * @param id 主键 ID
     * @return 操作结果（影响行数）
     */
    int delete(Long id);

    /**
     * 更新指定机构的状态。
     *
     * @param id  机构 ID
     * @param dto 包含状态更新信息的 DTO {@link UpdateStatusDto}
     * @return 更新操作影响的记录数
     */
    int updateStatus(Long id, UpdateStatusDto dto);

    /**
     * 调整机构层级关系（支持挂载、迁移、卸载）。
     *
     * @param descendantId 子节点标识
     * @param dto          层级调整参数，包含操作类型与目标父机构信息
     */
    void adjustHierarchy(Long descendantId, OrganClosureDto dto);
}
