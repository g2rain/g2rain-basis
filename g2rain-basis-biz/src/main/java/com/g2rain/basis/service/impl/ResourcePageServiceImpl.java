package com.g2rain.basis.service.impl;

import com.g2rain.basis.converter.ResourcePageConverter;
import com.g2rain.basis.dao.ControlUnitResourceRelationDao;
import com.g2rain.basis.dao.ResourcePageDao;
import com.g2rain.basis.dao.ResourcePageElementDao;
import com.g2rain.basis.dao.po.ResourcePagePo;
import com.g2rain.basis.dto.ControlUnitResourceRelationSelectDto;
import com.g2rain.basis.dto.ResourcePageDto;
import com.g2rain.basis.dto.ResourcePageElementSelectDto;
import com.g2rain.basis.dto.ResourcePageSelectDto;
import com.g2rain.basis.dto.UploadResourcePageDto;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.enums.ResourceType;
import com.g2rain.basis.service.ResourcePageService;
import com.g2rain.basis.vo.ResourcePageVo;
import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.id.IdGenerator;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.utils.Asserts;
import com.g2rain.common.utils.Collections;
import com.g2rain.common.utils.Moments;
import com.g2rain.common.utils.Strings;
import com.g2rain.mybatis.pagination.PageContext;
import com.g2rain.mybatis.pagination.model.Page;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 应用资源页面服务实现类
 * <p>
 * 核心功能：
 * <ul>
 *     <li>提供资源页面的增删改查操作</li>
 *     <li>保证页面编码在同一应用下唯一</li>
 * </ul>
 * <p>
 * 对应数据库表: {@code resource_page}
 *
 * @author Alpha
 * @since 2026/1/19
 */
@Service(value = "resourcePageServiceImpl")
public class ResourcePageServiceImpl implements ResourcePageService {

    @Resource(name = "resourcePageDao")
    private ResourcePageDao resourcePageDao;

    @Resource(name = "controlUnitResourceRelationDao")
    private ControlUnitResourceRelationDao controlUnitResourceRelationDao;

    @Resource(name = "resourcePageElementDao")
    private ResourcePageElementDao resourcePageElementDao;

    private IdGenerator idGenerator;

    @Qualifier("idGenerator")
    @Autowired(required = false)
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    /**
     * 查询资源页面列表
     *
     * @param selectDto 查询条件 DTO
     * @return 页面 VO 列表
     */
    @Override
    public List<ResourcePageVo> selectList(ResourcePageSelectDto selectDto) {
        return resourcePageDao.selectList(selectDto)
            .stream()
            .map(ResourcePageConverter.INSTANCE::po2vo)
            .toList();
    }

    /**
     * 分页查询资源页面列表
     *
     * @param selectDto 分页查询条件 DTO
     * @return 页面分页数据
     */
    @Override
    public PageData<ResourcePageVo> selectPage(PageSelectListDto<ResourcePageSelectDto> selectDto) {
        Page<ResourcePagePo> page = PageContext.of(selectDto.getPageNum(), selectDto.getPageSize(), () ->
            resourcePageDao.selectList(selectDto.getQuery())
        );

        List<ResourcePageVo> result = page.getResult()
            .stream()
            .map(ResourcePageConverter.INSTANCE::po2vo)
            .toList();
        return PageData.of(page.getPageNum(), page.getPageSize(), page.getTotal(), result);
    }

    /**
     * 保存或更新资源页面
     *
     * <p>逻辑说明：
     * <ul>
     *     <li>判断是新增还是更新</li>
     *     <li>页面编码在同一应用下唯一</li>
     *     <li>新增时使用 IdGenerator 生成主键</li>
     * </ul>
     *
     * @param dto 页面 DTO
     * @return 保存或更新后的页面 ID
     * @throws BusinessException 当页面编码重复时抛出
     */
    @Override
    public Long save(ResourcePageDto dto) {
        // 判断是新增还是更新
        Long id = dto.getId();

        // 校验 同一应用 `页面编码` 唯一性
        ResourcePageSelectDto selectDto = new ResourcePageSelectDto();
        selectDto.setApplicationId(dto.getApplicationId());
        selectDto.setPageCode(dto.getPageCode());
        List<ResourcePagePo> pages = resourcePageDao.selectList(selectDto);
        if (pages.stream().anyMatch(o -> !o.getId().equals(id))) {
            throw new BusinessException(BasisErrorCode.PARAM_ALREADY_EXISTS,
                "pageCode", dto.getPageCode()
            );
        }

        // 转换 DTO 为 PO
        ResourcePagePo entity = ResourcePageConverter.INSTANCE.dto2po(dto);

        // 更新：直接更新
        if (Objects.nonNull(id) && id != 0) {
            entity.setUpdateTime(Moments.now());
            int success = resourcePageDao.update(entity);
            Asserts.greaterThan(success, 0, SystemErrorCode.UPDATE_DATA_ERROR, id);
            return entity.getId();
        }

        // 新增：使用IdGenerator生成主键
        entity.setId(idGenerator.generateId());
        LocalDateTime now = Moments.now();
        entity.setUpdateTime(now);
        entity.setCreateTime(now);
        int success = resourcePageDao.insert(entity);
        Asserts.greaterThan(success, 0, SystemErrorCode.CREATE_DATA_ERROR);
        return entity.getId();
    }

    /**
     * 批量新增或更新页面资源
     *
     * <p>逻辑说明：
     * <ul>
     *     <li>统一校验页面 DTO 必填字段</li>
     *     <li>同一应用下页面编码唯一，重复抛异常</li>
     *     <li>批量新增和批量更新分别收集，再统一操作，减少数据库 IO</li>
     *     <li>新增时使用 IdGenerator 生成主键</li>
     * </ul>
     *
     * @param applicationId 应用 ID
     * @param pages         页面数据列表
     * @return 成功操作的记录数
     * @throws BusinessException 当页面编码重复或字段缺失时抛出
     */
    @Override
    public Long batchSave(Long applicationId, List<UploadResourcePageDto> pages) {
        if (Collections.isEmpty(pages)) {
            return 0L;
        }

        // 1. 校验 DTO 必填字段
        Set<String> pageCodes = new HashSet<>();
        for (UploadResourcePageDto dto : pages) {
            if (Strings.isBlank(dto.getPageName())) {
                throw new BusinessException(SystemErrorCode.PARAM_VAL_INVALID, "pageName");
            }
            if (Strings.isBlank(dto.getPageCode())) {
                throw new BusinessException(SystemErrorCode.PARAM_VAL_INVALID, "pageCode");
            }
            if (Strings.isBlank(dto.getLinkPath())) {
                throw new BusinessException(SystemErrorCode.PARAM_VAL_INVALID, "linkPath");
            }

            pageCodes.add(dto.getPageCode());
        }

        // 2. 查询应用下所有已有 pageCode，一次性获取
        ResourcePageSelectDto selectDto = new ResourcePageSelectDto();
        selectDto.setApplicationId(applicationId);
        selectDto.setPageCodes(pageCodes); // DAO 支持批量查询
        List<ResourcePagePo> existingPages = resourcePageDao.selectList(selectDto);
        Map<String, ResourcePagePo> existMap = existingPages.stream()
            .collect(Collectors.toMap(ResourcePagePo::getPageCode, Function.identity(), (e, r) -> e));

        /*
         * 页面名称如果调整, 不需要调整, 可能存在的场景: 名称在后台经过调整, 如果上传后进行调整可能是误操作
         * 页面编码肯定不会调整, 如果调整了, 视为新增
         * 页面链接路径可能存在调整, 如果调整了需要修改！！！！
         */
        List<ResourcePagePo> toInsert = new ArrayList<>();
        List<ResourcePagePo> toUpdate = new ArrayList<>();
        LocalDateTime now = Moments.now();
        for (UploadResourcePageDto dto : pages) {
            String pageName = dto.getPageName();
            String linkPath = dto.getLinkPath();
            ResourcePagePo entity = existMap.get(dto.getPageCode());
            if (Objects.isNull(entity)) {
                // 新增
                entity = new ResourcePagePo();
                entity.setId(idGenerator.generateId());
                entity.setApplicationId(applicationId);
                entity.setPageCode(dto.getPageCode());
                entity.setPageName(pageName);
                entity.setLinkPath(linkPath);
                entity.setCreateTime(now);
                entity.setUpdateTime(now);
                toInsert.add(entity);
            } else if (!Strings.equals(linkPath, entity.getLinkPath())) {
                // 更新
                entity.setPageName(pageName);
                entity.setLinkPath(dto.getLinkPath());
                entity.setUpdateTime(now);
                toUpdate.add(entity);
            }
        }

        long count = 0L;
        // DAO 批量插入
        if (!toInsert.isEmpty()) {
            count += resourcePageDao.insertMultiple(toInsert);
        }

        // DAO 批量更新
        if (!toUpdate.isEmpty()) {
            count += resourcePageDao.updateBatchCase(toUpdate);
        }

        return count;
    }

    /**
     * 删除资源页面
     *
     * @param id 页面 ID
     * @return 删除记录数
     */
    @Override
    @SuppressWarnings("null")
    public int delete(Long id) {
        ResourcePagePo resourcePage = resourcePageDao.selectById(id);
        Asserts.isTrue(Objects.nonNull(resourcePage), SystemErrorCode.PARAM_VAL_INVALID, "id");

        ResourcePageElementSelectDto peSelectDto = new ResourcePageElementSelectDto();
        peSelectDto.setPageCode(resourcePage.getPageCode());
        long total = resourcePageElementDao.checkPageElementExists(peSelectDto);
        Asserts.lessThanOrEqual(total, 0, BasisErrorCode.DEL_RESOURCE_EXIST_ELEMENT_UNDELETABLE);

        ControlUnitResourceRelationSelectDto urSelectDto = new ControlUnitResourceRelationSelectDto();
        urSelectDto.setResourceId(id);
        urSelectDto.setResourceType(ResourceType.PAGE.name());
        total = controlUnitResourceRelationDao.checkResourceExists(urSelectDto);
        Asserts.lessThanOrEqual(total, 0, BasisErrorCode.DEL_RESOURCE_EXIST_CONTROL_UNIT_UNDELETABLE);
        return resourcePageDao.delete(id);
    }
}
