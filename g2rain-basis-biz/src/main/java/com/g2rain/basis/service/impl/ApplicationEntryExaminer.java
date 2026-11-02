package com.g2rain.basis.service.impl;

import com.g2rain.basis.dao.ApplicationAuthorizationDao;
import com.g2rain.basis.dao.RoleControlUnitRelationDao;
import com.g2rain.basis.dao.po.CountRoleControlUnitPo;
import com.g2rain.basis.dto.ApplicationAuthorizationSelectDto;
import com.g2rain.basis.enums.AuthorizationStatus;
import com.g2rain.basis.enums.BasisErrorCode;
import com.g2rain.basis.enums.OrganStatus;
import com.g2rain.basis.vo.ApplicationVo;
import com.g2rain.basis.vo.OrganVo;
import com.g2rain.common.exception.BusinessException;
import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.utils.Asserts;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 应用准入校验器：判断用户能否以指定应用进入。
 * <p>
 * 换票（{@code fetchTokenContext}）时校验；可切换身份列表只过滤机构状态，其余由这里拦截：
 * 机构须为 ACTIVE；目标应用不是默认主应用时，机构须已开通该应用（ACTIVATED），且用户有可用的控制单元。
 * </p>
 *
 * @author alpha
 * @since 2026/10/24
 */
@Service(value = "applicationEntryExaminer")
public class ApplicationEntryExaminer {

    /**
     * 应用开通记录。
     */
    @Resource(name = "applicationAuthorizationDao")
    private ApplicationAuthorizationDao applicationAuthorizationDao;

    /**
     * 用户角色关联的控制单元计数。
     */
    @Resource(name = "roleControlUnitRelationDao")
    private RoleControlUnitRelationDao roleControlUnitRelationDao;

    /**
     * 是否为默认主应用：入口应用且不可被集成。
     *
     * @param application 应用
     * @return 默认主应用为 {@code true}
     */
    public static boolean isDefaultMain(ApplicationVo application) {
        return Boolean.TRUE.equals(application.getLanding()) && Boolean.FALSE.equals(application.getCanIntegrate());
    }

    /**
     * 校验用户能否以该应用进入，不满足时抛出对应错误。
     *
     * @param userId      用户 ID
     * @param organ       用户所属机构
     * @param application 目标应用
     * @throws BusinessException 机构不可用、应用未开通、无权限或权限已停用
     */
    public void check(Long userId, OrganVo organ, ApplicationVo application) {
        Asserts.isTrue(OrganStatus.ACTIVE.name().equals(organ.getStatus()),
            BasisErrorCode.ORGAN_UNAVAILABLE
        );

        // 默认主应用只要求机构可用
        if (isDefaultMain(application)) {
            return;
        }

        ApplicationAuthorizationSelectDto authSelect = new ApplicationAuthorizationSelectDto();
        authSelect.setOrganId(organ.getId());
        authSelect.setApplicationId(application.getId());
        authSelect.setStatus(AuthorizationStatus.ACTIVATED.name());
        // 不走数据隔离：换票的目标机构不是当前登录机构
        Long authorized = applicationAuthorizationDao.checkApplicationAuthorizationExists(authSelect);
        Asserts.isTrue(Objects.nonNull(authorized) && authorized > 0,
            BasisErrorCode.APPLICATION_AUTHORIZATION_MISSING, application.getApplicationCode());

        CountRoleControlUnitPo countRoleControlUnit = roleControlUnitRelationDao.countRoleControlUnits(userId);

        // 如果没有授权, 权限不足
        Asserts.greaterThan(Objects.isNull(countRoleControlUnit.getTotalControlUnitCount()) ? -1 :
                countRoleControlUnit.getTotalControlUnitCount(), 0,
            SystemErrorCode.UNAUTHORIZED, organ.getOrganName()
        );

        // 如果没有有效的权限, 请续费
        Asserts.greaterThan(Objects.isNull(countRoleControlUnit.getActiveControlUnitCount()) ? -1 :
                countRoleControlUnit.getActiveControlUnitCount(), 0,
            BasisErrorCode.BUSINESS_CAPABILITY_DISABLED, organ.getOrganName()
        );
    }

    /**
     * 用户能否以该应用进入。
     *
     * @param userId      用户 ID
     * @param organ       用户所属机构
     * @param application 目标应用
     * @return 可进入为 {@code true}
     */
    public boolean permits(Long userId, OrganVo organ, ApplicationVo application) {
        try {
            check(userId, organ, application);
            return true;
        } catch (BusinessException _) {
            return false;
        }
    }
}
