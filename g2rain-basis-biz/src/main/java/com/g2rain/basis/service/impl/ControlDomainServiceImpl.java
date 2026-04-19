package com.g2rain.basis.service.impl;

import com.g2rain.basis.converter.ControlDomainConverter;
import com.g2rain.basis.dao.ApplicationAuthorizationDao;
import com.g2rain.basis.dao.ApplicationDao;
import com.g2rain.basis.dao.ControlDomainControlUnitRelationDao;
import com.g2rain.basis.dao.ControlDomainDao;
import com.g2rain.basis.dao.po.ApplicationPo;
import com.g2rain.basis.dao.po.ControlDomainPo;
import com.g2rain.basis.dto.ApplicationAuthorizationSelectDto;
import com.g2rain.basis.dto.ControlDomainDto;
import com.g2rain.basis.dto.ControlDomainSelectDto;
import com.g2rain.basis.enums.AuthorizationStatus;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.enums.ControlDomainScope;
import com.g2rain.basis.enums.ControlDomainType;
import com.g2rain.basis.service.ControlDomainService;
import com.g2rain.basis.vo.ControlDomainVo;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 控制域表服务实现类
 * <p>
 * 提供对 <code>control_domain</code> 表的 CRUD 操作及控制域管理功能。
 * </p>
 *
 * <p>核心功能：</p>
 * <ul>
 *     <li>查询控制域列表或分页列表</li>
 *     <li>新增或更新控制域信息</li>
 *     <li>删除控制域及其关联的控制单元关系</li>
 *     <li>校验控制域名称唯一性及交付范围合法性</li>
 * </ul>
 *
 * @author Alpha
 */
@Service(value = "controlDomainServiceImpl")
public class ControlDomainServiceImpl implements ControlDomainService {

    @Resource(name = "controlDomainDao")
    private ControlDomainDao controlDomainDao;

    @Resource(name = "applicationDao")
    private ApplicationDao applicationDao;

    @Resource(name = "applicationAuthorizationDao")
    private ApplicationAuthorizationDao applicationAuthorizationDao;

    @Resource(name = "controlDomainControlUnitRelationDao")
    private ControlDomainControlUnitRelationDao controlDomainControlUnitRelationDao;

    private IdGenerator idGenerator;

    @Qualifier("idGenerator")
    @Autowired(required = false)
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    /**
     * 查询控制域列表
     *
     * @param selectDto 查询条件 DTO
     * @return 控制域 VO 列表
     */
    @Override
    public List<ControlDomainVo> selectList(ControlDomainSelectDto selectDto) {
        return controlDomainDao.selectList(selectDto)
            .stream()
            .map(ControlDomainConverter.INSTANCE::po2vo)
            .toList();
    }

    /**
     * 分页查询控制域
     *
     * @param selectDto 分页查询条件 DTO
     * @return 分页数据封装 PageData
     */
    @Override
    public PageData<ControlDomainVo> selectPage(PageSelectListDto<ControlDomainSelectDto> selectDto) {
        Page<ControlDomainPo> page = PageContext.of(selectDto.getPageNum(), selectDto.getPageSize(), () ->
            controlDomainDao.selectList(selectDto.getQuery())
        );

        List<ControlDomainVo> result = page.getResult()
            .stream()
            .map(ControlDomainConverter.INSTANCE::po2vo)
            .toList();
        return PageData.of(page.getPageNum(), page.getPageSize(), page.getTotal(), result);
    }

    /**
     * 新增或更新控制域
     *
     * @param dto 控制域 DTO
     * @return 新增或更新后的控制域 ID
     * @throws BusinessException 校验失败或控制域名称重复
     */
    @Override
    public Long save(ControlDomainDto dto) {
        // 校验参数
        ControlDomainType type = ControlDomainType.fromName(dto.getControlDomainType());
        ControlDomainScope scope = ControlDomainScope.fromName(dto.getControlDomainScope());

        // 校验交付范围
        if (ControlDomainType.TRADE.equals(type) && ControlDomainScope.OPERATION.equals(scope)) {
            throw new BusinessException(BasisErrorCode.DELIVERY_SCOPE_RESTRICTED_ILLEGAL);
        }

        // 验证应用是否存在
        ApplicationPo application = applicationDao.selectById(dto.getApplicationId());
        Asserts.isTrue(Objects.nonNull(application), SystemErrorCode.PARAM_VAL_INVALID,
            dto.getApplicationId()
        );

        // 判断是新增还是更新
        Long id = dto.getId();

        // 校验同一个应用控制域名称唯一性
        ControlDomainSelectDto selectDto = new ControlDomainSelectDto();
        selectDto.setControlDomainName(dto.getControlDomainName());
        selectDto.setApplicationId(dto.getApplicationId());
        List<ControlDomainPo> domains = controlDomainDao.selectList(selectDto);
        if (domains.stream().anyMatch(o -> !o.getId().equals(id))) {
            throw new BusinessException(BasisErrorCode.PARAM_ALREADY_EXISTS,
                "controlDomainName", dto.getControlDomainName()
            );
        }

        // 转换 DTO 为 PO
        ControlDomainPo entity = ControlDomainConverter.INSTANCE.dto2po(dto);
        LocalDateTime now = Moments.now();
        entity.setUpdateTime(now);

        // 更新：直接更新
        if (Objects.nonNull(id) && id != 0) {
            int success = controlDomainDao.update(entity);
            Asserts.greaterThan(success, 0, SystemErrorCode.UPDATE_DATA_ERROR, id);
            return entity.getId();
        }

        entity.setId(idGenerator.generateId());
        entity.setCreateTime(now);
        int success = controlDomainDao.insert(entity);
        Asserts.greaterThan(success, 0, SystemErrorCode.CREATE_DATA_ERROR);
        return entity.getId();
    }

    /**
     * 删除控制域及其关联控制单元关系
     *
     * @param id 控制域 ID
     * @return 删除数量
     */
    @Override
    @Transactional
    public int delete(Long id) {
        // 存在有效的授权记录, 不允许删除
        ApplicationAuthorizationSelectDto selectAuthDto = new ApplicationAuthorizationSelectDto();
        selectAuthDto.setApplicationId(id);
        selectAuthDto.setStatus(AuthorizationStatus.ACTIVATED.name());
        long total = applicationAuthorizationDao.checkApplicationAuthorizationExists(selectAuthDto);
        Asserts.lessThanOrEqual(total, 0, BasisErrorCode.DEL_APP_EXIST_AUTH_UNDELETABLE);

        // 删除控制域控制单元关联数据
        controlDomainControlUnitRelationDao.deleteByControlDomainId(id);
        // 删除控制域
        return controlDomainDao.delete(id);
    }
}
