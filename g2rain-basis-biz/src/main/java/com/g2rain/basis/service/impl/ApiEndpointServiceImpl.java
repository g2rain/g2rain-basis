package com.g2rain.basis.service.impl;

import com.g2rain.basis.converter.ApiEndpointConverter;
import com.g2rain.basis.dao.ApiEndpointDao;
import com.g2rain.basis.dao.ResourceApiEndpointDao;
import com.g2rain.basis.dao.po.ApiEndpointPo;
import com.g2rain.basis.dto.ApiEndpointDto;
import com.g2rain.basis.dto.ApiEndpointSelectDto;
import com.g2rain.basis.dto.ResourceApiEndpointSelectDto;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.service.ApiEndpointService;
import com.g2rain.basis.vo.ApiEndpointVo;
import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.id.IdGenerator;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.utils.Asserts;
import com.g2rain.common.utils.Moments;
import com.g2rain.mybatis.pagination.PageContext;
import com.g2rain.mybatis.pagination.model.Page;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 接口地址表服务实现类
 * 表名: api_endpoint
 *
 * @author Alpha
 */
@Service(value = "apiEndpointServiceImpl")
public class ApiEndpointServiceImpl implements ApiEndpointService {

    @Resource(name = "apiEndpointDao")
    private ApiEndpointDao apiEndpointDao;

    @Resource(name = "resourceApiEndpointDao")
    private ResourceApiEndpointDao resourceApiEndpointDao;

    private IdGenerator idGenerator;

    @Qualifier("idGenerator")
    @Autowired(required = false)
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    /**
     * 查询接口地址列表
     *
     * @param selectDto 查询条件 DTO
     * @return 接口地址 VO 列表
     */
    @Override
    public List<ApiEndpointVo> selectList(ApiEndpointSelectDto selectDto) {
        return apiEndpointDao.selectList(selectDto)
            .stream()
            .map(ApiEndpointConverter.INSTANCE::po2vo)
            .toList();
    }

    /**
     * 分页查询接口地址
     *
     * @param selectDto 分页查询条件 DTO
     * @return 分页数据封装 PageData
     */
    @Override
    public PageData<ApiEndpointVo> selectPage(PageSelectListDto<ApiEndpointSelectDto> selectDto) {
        Page<ApiEndpointPo> page = PageContext.of(selectDto.getPageNum(), selectDto.getPageSize(), () ->
            apiEndpointDao.selectList(selectDto.getQuery())
        );

        List<ApiEndpointVo> result = page.getResult()
            .stream()
            .map(ApiEndpointConverter.INSTANCE::po2vo)
            .toList();
        return PageData.of(page.getPageNum(), page.getPageSize(), page.getTotal(), result);
    }

    /**
     * 查询所有接口标签
     *
     * @return 接口标签列表
     */
    @Override
    public List<String> selectApiTags() {
        return apiEndpointDao.selectApiTags();
    }

    /**
     * 保存接口地址（新增或更新）
     * <p>
     * 如果 DTO 中 id 为 null 或 0，则为新增操作；否则为更新操作。
     * 会校验接口地址与请求方法组合的唯一性。
     *
     * @param dto 接口地址 DTO
     * @return 新增或更新后的接口地址 ID
     * @throws BusinessException 当 url+method 已存在时抛出
     */
    @Override
    public Long save(ApiEndpointDto dto) {
        // 判断是新增还是更新
        Long id = dto.getId();

        // 校验 `接口地址和请求方法` 唯一性
        ApiEndpointSelectDto selectDto = new ApiEndpointSelectDto();
        selectDto.setApiUrl(dto.getApiUrl());
        selectDto.setRequestMethod(dto.getRequestMethod());
        List<ApiEndpointPo> apiEndpoints = apiEndpointDao.selectList(selectDto);
        if (apiEndpoints.stream().anyMatch(o -> !o.getId().equals(id))) {
            throw new BusinessException(BasisErrorCode.PARAM_ALREADY_EXISTS, "url+method",
                String.format("%s+%s", dto.getApiUrl(), dto.getRequestMethod())
            );
        }

        // 转换 DTO 为 PO
        ApiEndpointPo entity = ApiEndpointConverter.INSTANCE.dto2po(dto);
        LocalDateTime now = Moments.now();
        entity.setUpdateTime(now);

        // 更新：直接更新
        if (Objects.nonNull(id) && id != 0) {
            int success = apiEndpointDao.update(entity);
            Asserts.greaterThan(success, 0, SystemErrorCode.UPDATE_DATA_ERROR, id);
            return entity.getId();
        }

        // 新增：使用IdGenerator生成主键
        entity.setId(idGenerator.generateId());
        entity.setCreateTime(now);
        int success = apiEndpointDao.insert(entity);
        Asserts.greaterThan(success, 0, SystemErrorCode.CREATE_DATA_ERROR);
        return entity.getId();
    }

    /**
     * 删除接口地址
     *
     * @param id 接口地址 ID
     * @return 删除操作影响的记录数
     */
    @Override
    public int delete(Long id) {
        ResourceApiEndpointSelectDto selectDto = new ResourceApiEndpointSelectDto();
        selectDto.setApiEndpointId(id);
        long total = resourceApiEndpointDao.checkApiEndpointExists(selectDto);
        Asserts.lessThanOrEqual(total, 0, BasisErrorCode.DEL_API_EXIST_RESOURCE_API_UNDELETABLE);
        return apiEndpointDao.delete(id);
    }
}
