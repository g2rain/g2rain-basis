package com.g2rain.basis.service.impl;

import com.g2rain.basis.converter.ResourcePageElementConverter;
import com.g2rain.basis.dao.ControlUnitResourceRelationDao;
import com.g2rain.basis.dao.ResourcePageDao;
import com.g2rain.basis.dao.ResourcePageElementDao;
import com.g2rain.basis.dao.po.ResourcePageElementPo;
import com.g2rain.basis.dto.ControlUnitResourceRelationSelectDto;
import com.g2rain.basis.dto.ResourcePageElementDto;
import com.g2rain.basis.dto.ResourcePageElementSelectDto;
import com.g2rain.basis.dto.ResourcePageSelectDto;
import com.g2rain.basis.dto.UploadResourcePageElementDto;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.enums.ResourceType;
import com.g2rain.basis.service.ResourcePageElementService;
import com.g2rain.basis.vo.ResourcePageElementVo;
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
 * 应用资源页面元素服务实现类
 * <p>
 * 核心功能：
 * <ul>
 *     <li>提供资源页面元素的增删改查操作</li>
 *     <li>确保页面元素编码在同一应用下唯一</li>
 * </ul>
 * <p>
 * 对应数据库表: {@code resource_page_element}
 *
 * @author Alpha
 * @since 2026/1/19
 */
@Service(value = "resourcePageElementServiceImpl")
public class ResourcePageElementServiceImpl implements ResourcePageElementService {

    @Resource(name = "resourcePageElementDao")
    private ResourcePageElementDao resourcePageElementDao;

    @Resource(name = "resourcePageDao")
    private ResourcePageDao resourcePageDao;

    @Resource(name = "controlUnitResourceRelationDao")
    private ControlUnitResourceRelationDao controlUnitResourceRelationDao;

    private IdGenerator idGenerator;

    @Qualifier("idGenerator")
    @Autowired(required = false)
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    /**
     * 查询资源页面元素列表
     *
     * @param selectDto 查询条件 DTO
     * @return 页面元素 VO 列表
     */
    @Override
    public List<ResourcePageElementVo> selectList(ResourcePageElementSelectDto selectDto) {
        return resourcePageElementDao.selectList(selectDto)
            .stream()
            .map(ResourcePageElementConverter.INSTANCE::po2vo)
            .toList();
    }

    /**
     * 分页查询资源页面元素列表
     *
     * @param selectDto 分页查询条件 DTO
     * @return 页面元素分页数据
     */
    @Override
    public PageData<ResourcePageElementVo> selectPage(PageSelectListDto<ResourcePageElementSelectDto> selectDto) {
        Page<ResourcePageElementPo> page = PageContext.of(selectDto.getPageNum(), selectDto.getPageSize(), () ->
            resourcePageElementDao.selectList(selectDto.getQuery())
        );

        List<ResourcePageElementVo> result = page.getResult()
            .stream()
            .map(ResourcePageElementConverter.INSTANCE::po2vo)
            .toList();
        return PageData.of(page.getPageNum(), page.getPageSize(), page.getTotal(), result);
    }

    /**
     * 保存或更新资源页面元素
     *
     * <p>逻辑说明：
     * <ul>
     *     <li>判断是新增还是更新</li>
     *     <li>页面元素编码在同一应用下唯一</li>
     *     <li>新增时使用 IdGenerator 生成主键</li>
     * </ul>
     *
     * @param dto 页面元素 DTO
     * @return 保存或更新后的页面元素 ID
     * @throws BusinessException 当页面元素编码重复时抛出
     */
    @Override
    public Long save(ResourcePageElementDto dto) {
        // 判断是新增还是更新
        Long id = dto.getId();

        // 校验 同一应用 `页面元素编码` 唯一性, 暂时不进行pageCode的校验(错了, 页面会找不到对应的元素, 自会调整数据)
        ResourcePageElementSelectDto selectDto = new ResourcePageElementSelectDto();
        selectDto.setApplicationId(dto.getApplicationId());
        selectDto.setPageElementCode(dto.getPageElementCode());
        List<ResourcePageElementPo> elements = resourcePageElementDao.selectList(selectDto);
        if (elements.stream().anyMatch(o -> !o.getId().equals(id))) {
            throw new BusinessException(BasisErrorCode.PARAM_ALREADY_EXISTS,
                "pageElementCode", dto.getPageElementCode()
            );
        }

        // 校验页面编码是否存在
        ResourcePageSelectDto pageSelectDto = new ResourcePageSelectDto();
        pageSelectDto.setPageCode(dto.getPageCode());
        pageSelectDto.setApplicationId(dto.getApplicationId());
        long total = resourcePageDao.checkResourcePageExists(pageSelectDto);
        Asserts.greaterThan(total, 0, SystemErrorCode.PARAM_VAL_INVALID, "pageCode");

        // 转换 DTO 为 PO
        ResourcePageElementPo entity = ResourcePageElementConverter.INSTANCE.dto2po(dto);
        if (Objects.isNull(id) || id == 0) {
            // 新增：使用IdGenerator生成主键
            entity.setId(idGenerator.generateId());
            LocalDateTime now = Moments.now();
            entity.setUpdateTime(now);
            entity.setCreateTime(now);
            int success = resourcePageElementDao.insert(entity);
            Asserts.greaterThan(success, 0, SystemErrorCode.CREATE_DATA_ERROR);
            return entity.getId();
        }

        // 更新：直接更新
        entity.setUpdateTime(Moments.now());
        int success = resourcePageElementDao.update(entity);
        Asserts.greaterThan(success, 0, SystemErrorCode.UPDATE_DATA_ERROR, id);
        return entity.getId();
    }

    /**
     * 批量新增或更新页面元素数据
     *
     * <p>逻辑说明：
     * <ul>
     *     <li>遍历元素列表，判断是新增还是更新</li>
     *     <li>同一页面下元素编码唯一，重复时抛异常</li>
     *     <li>新增时使用 IdGenerator 生成主键</li>
     *     <li>更新时修改已有记录的属性值</li>
     * </ul>
     *
     * @param applicationId 应用 ID
     * @param pageElements  页面元素数据列表
     * @return 成功操作的记录数
     * @throws BusinessException 当元素编码重复或字段缺失时抛出
     */
    @Override
    public Long batchSave(Long applicationId, List<UploadResourcePageElementDto> pageElements) {
        if (Collections.isEmpty(pageElements)) {
            return 0L;
        }

        // 1. 校验 DTO 必填字段
        Set<String> pageCodes = new HashSet<>();
        Set<String> elementCodes = new HashSet<>();
        for (UploadResourcePageElementDto dto : pageElements) {
            if (Strings.isBlank(dto.getPageCode())) {
                throw new BusinessException(SystemErrorCode.PARAM_VAL_INVALID, "pageCode");
            }

            if (Strings.isBlank(dto.getPageElementCode())) {
                throw new BusinessException(SystemErrorCode.PARAM_VAL_INVALID, "pageElementCode");
            }

            if (Strings.isBlank(dto.getPageElementName())) {
                throw new BusinessException(SystemErrorCode.PARAM_VAL_INVALID, "pageElementName");
            }

            pageCodes.add(dto.getPageCode());
            elementCodes.add(dto.getPageElementCode());
        }

        // 校验页面编码是否存在
        ResourcePageSelectDto pageSelectDto = new ResourcePageSelectDto();
        pageSelectDto.setPageCodes(pageCodes);
        pageSelectDto.setApplicationId(applicationId);
        long total = resourcePageDao.checkResourcePageExists(pageSelectDto);
        Asserts.isTrue(total == pageCodes.size(), SystemErrorCode.PARAM_VAL_INVALID, "pageCode");

        // 2. 查询应用下已有元素，一次性获取
        ResourcePageElementSelectDto selectDto = new ResourcePageElementSelectDto();
        selectDto.setApplicationId(applicationId);
        selectDto.setPageElementCodes(elementCodes); // DAO 支持批量查询
        List<ResourcePageElementPo> existingElements = resourcePageElementDao.selectList(selectDto);

        Map<String, ResourcePageElementPo> existMap = existingElements.stream()
            .collect(Collectors.toMap(ResourcePageElementPo::getPageElementCode, Function.identity(), (e, r) -> e));

        /*
         * 页面元素的名称如果调整, 不需要调整, 可能存在的场景: 名称在后台经过调整, 如果上传后进行调整可能是误操作
         * 页面元素的编码肯定不会调整, 如果调整了, 视为新增
         * 页面编码可能存在调整, 如果调整了需要修改！！！！
         */
        List<ResourcePageElementPo> toInsert = new ArrayList<>();
        List<ResourcePageElementPo> toUpdate = new ArrayList<>();
        LocalDateTime now = Moments.now();
        for (UploadResourcePageElementDto dto : pageElements) {
            ResourcePageElementPo entity = existMap.get(dto.getPageElementCode());
            String pageCode = dto.getPageCode();
            if (Objects.isNull(entity)) {
                // 新增
                entity = new ResourcePageElementPo();
                entity.setId(idGenerator.generateId());
                entity.setApplicationId(applicationId);
                entity.setPageCode(pageCode);
                entity.setPageElementCode(dto.getPageElementCode());
                entity.setPageElementName(dto.getPageElementName());
                entity.setCreateTime(now);
                entity.setUpdateTime(now);
                toInsert.add(entity);
            } else if (!Strings.equals(pageCode, entity.getPageCode())) {
                // 更新
                entity.setPageCode(dto.getPageCode());
                entity.setUpdateTime(now);
                toUpdate.add(entity);
            }
        }

        long count = 0L;
        // 批量插入
        if (!toInsert.isEmpty()) {
            count += resourcePageElementDao.insertMultiple(toInsert);
        }

        // 批量更新
        if (!toUpdate.isEmpty()) {
            count += resourcePageElementDao.updateBatchCase(toUpdate);
        }

        return count;
    }

    /**
     * 删除资源页面元素
     *
     * @param id 页面元素 ID
     * @return 删除记录数
     */
    @Override
    public int delete(Long id) {
        ControlUnitResourceRelationSelectDto selectDto = new ControlUnitResourceRelationSelectDto();
        selectDto.setResourceId(id);
        selectDto.setResourceType(ResourceType.PAGE_ELEMENT.name());
        long total = controlUnitResourceRelationDao.checkResourceExists(selectDto);
        Asserts.lessThanOrEqual(total, 0, BasisErrorCode.DEL_RESOURCE_EXIST_CONTROL_UNIT_UNDELETABLE);
        return resourcePageElementDao.delete(id);
    }
}
