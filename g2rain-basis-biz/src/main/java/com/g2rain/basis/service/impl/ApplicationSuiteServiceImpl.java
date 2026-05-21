package com.g2rain.basis.service.impl;

import com.g2rain.basis.converter.ApplicationSuiteConverter;
import com.g2rain.basis.dao.ApplicationDao;
import com.g2rain.basis.dao.ApplicationSuiteDao;
import com.g2rain.basis.dao.po.ApplicationPo;
import com.g2rain.basis.dao.po.ApplicationSuitePo;
import com.g2rain.basis.dto.ApplicationSelectDto;
import com.g2rain.basis.dto.ApplicationSuiteDto;
import com.g2rain.basis.dto.ApplicationSuiteSelectDto;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.service.ApplicationSuiteService;
import com.g2rain.basis.vo.ApplicationSuiteVo;
import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.id.IdGenerator;
import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.utils.Asserts;
import com.g2rain.common.utils.Collections;
import com.g2rain.common.utils.Moments;
import com.g2rain.mybatis.pagination.PageContext;
import com.g2rain.mybatis.pagination.model.Page;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 应用归类关系表服务实现类。
 *
 * <p>提供对 `application_suite` 表的操作，包括查询、分页、保存和删除应用归属关系。</p>
 *
 * <p>核心功能：</p>
 * <ol>
 *     <li>查询应用归类列表或分页列表；</li>
 *     <li>保存（新增/更新）应用归类关系；</li>
 *     <li>删除应用归类关系；</li>
 *     <li>设置应用名称到 VO。</li>
 * </ol>
 *
 * @author Alpha
 */
@Service(value = "applicationSuiteServiceImpl")
public class ApplicationSuiteServiceImpl implements ApplicationSuiteService {

    @Resource(name = "applicationSuiteDao")
    private ApplicationSuiteDao applicationSuiteDao;

    @Resource(name = "applicationDao")
    private ApplicationDao applicationDao;

    private IdGenerator idGenerator;

    @Qualifier("idGenerator")
    @Autowired(required = false)
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    /**
     * 查询应用归类列表
     *
     * @param selectDto 查询条件 DTO
     * @return 应用归类 VO 列表
     */
    @Override
    public List<ApplicationSuiteVo> selectList(ApplicationSuiteSelectDto selectDto) {
        return applicationSuiteDao.selectList(selectDto)
            .stream()
            .map(ApplicationSuiteConverter.INSTANCE::po2vo)
            .toList();
    }

    /**
     * 分页查询应用归类
     *
     * @param selectDto 分页查询条件 DTO
     * @return 分页数据封装 PageData
     */
    @Override
    public PageData<ApplicationSuiteVo> selectPage(PageSelectListDto<ApplicationSuiteSelectDto> selectDto) {
        Page<ApplicationSuitePo> page = PageContext.of(selectDto.getPageNum(), selectDto.getPageSize(), () ->
            applicationSuiteDao.selectList(selectDto.getQuery())
        );

        List<ApplicationSuiteVo> result = page.getResult()
            .stream()
            .map(ApplicationSuiteConverter.INSTANCE::po2vo)
            .toList();

        return PageData.of(page.getPageNum(), page.getPageSize(), page.getTotal(), result);
    }

    /**
     * 保存应用归类关系（新增或更新）。
     *
     * <p>规则：</p>
     * <ol>
     *     <li>校验应用 ID 和主应用 ID 是否存在且类型合法；</li>
     *     <li>校验归类关系是否重复；</li>
     *     <li>新增使用 IdGenerator 生成主键；</li>
     *     <li>更新时直接更新记录。</li>
     * </ol>
     *
     * @param dto 应用归类 DTO
     * @return 新增或更新记录的主键 ID
     * @throws BusinessException 数据已存在或参数校验失败
     */
    @Override
    @SuppressWarnings("null")
    public Integer save(ApplicationSuiteDto dto) {
        // 校验参数
        Long applicationId = dto.getApplicationId();
        Set<Long> masterApplicationIds = dto.getMasterApplicationIds();
        Set<Long> deleteMasterApplicationIds = dto.getDeleteMasterApplicationIds();

        if (Collections.isNotEmpty(masterApplicationIds)) {
            Set<Long> applicationIds = new HashSet<>(masterApplicationIds);
            applicationIds.add(applicationId);
            ApplicationSelectDto selectDto = new ApplicationSelectDto();
            selectDto.setIds(applicationIds);
            List<ApplicationPo> applications = applicationDao.selectList(selectDto);
            Map<Long, ApplicationPo> applicationMapping = applications.stream()
                .collect(Collectors.toMap(
                    ApplicationPo::getId,
                    Function.identity(),
                    (existing, _) -> existing
                ));

            ApplicationPo sourceApplication = applicationMapping.get(applicationId);
            Asserts.isTrue(Objects.nonNull(sourceApplication), SystemErrorCode.PARAM_VAL_INVALID, applicationId);
            Asserts.isTrue(sourceApplication.getCanIntegrate(), BasisErrorCode.NON_SUB_APPLICATION_CLASSIFY_ILLEGAL);
            for (Long masterApplicationId : masterApplicationIds) {
                ApplicationPo targetApplication = applicationMapping.get(masterApplicationId);
                Asserts.isTrue(Objects.nonNull(targetApplication), SystemErrorCode.PARAM_VAL_INVALID, masterApplicationId);
                Asserts.isTrue(!targetApplication.getCanIntegrate(), BasisErrorCode.NON_MAIN_APPLICATION_CLASSIFY_ILLEGAL);
            }
        }

        if (Collections.isNotEmpty(masterApplicationIds)) {
            // 校验是否重复设置归属
            ApplicationSuiteSelectDto ass = new ApplicationSuiteSelectDto();
            ass.setApplicationId(applicationId);
            ass.setMasterApplicationIds(masterApplicationIds);
            Set<Long> applicationSuites = applicationSuiteDao.selectList(ass)
                .stream()
                .map(ApplicationSuitePo::getMasterApplicationId)
                .collect(Collectors.toSet());

            masterApplicationIds.removeIf(applicationSuites::contains);
        }

        int result = 0;
        if (Collections.isNotEmpty(masterApplicationIds)) {
            LocalDateTime now = Moments.now();
            List<ApplicationSuitePo> suites = masterApplicationIds.stream().map(ma -> {
                ApplicationSuitePo suite = new ApplicationSuitePo();
                suite.setId(idGenerator.generateId());
                suite.setCreateTime(now);
                suite.setUpdateTime(now);
                suite.setApplicationId(dto.getApplicationId());
                suite.setMasterApplicationId(ma);
                return suite;
            }).toList();

            result += applicationSuiteDao.insertMultiple(suites);
        }

        // 批量删除
        if (Collections.isNotEmpty(deleteMasterApplicationIds)) {
            result += applicationSuiteDao.deleteByMasterApplicationIds(
                applicationId, deleteMasterApplicationIds
            );
        }

        return result;
    }
}
