-- =============================================
-- g2rain_basis 数据库表结构
-- MySQL 8.0 版本
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `g2rain_basis` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `g2rain_basis`;

-- =============================================
-- 1. 账号表 (passport)
-- =============================================
DROP TABLE IF EXISTS `passport`;

CREATE TABLE `passport` (
    `id` BIGINT NOT NULL COMMENT 													                    '账号标识',
    `username` VARCHAR(64) NOT NULL UNIQUE COMMENT 											            '登录用户',
    `password` VARCHAR(256) NOT NULL DEFAULT '' COMMENT 												'登录密码',
    `real_name` VARCHAR(128) DEFAULT NULL COMMENT 														'真实姓名',
    `sex` VARCHAR(12) DEFAULT NULL COMMENT 															    '性别[MALE:男性, FEMALE:女性]',
    `birthday` VARCHAR(16) DEFAULT NULL COMMENT 														'生日',
    `id_no` VARCHAR(32) DEFAULT NULL COMMENT 															'身份证号',
    `mobile` VARCHAR(32) DEFAULT '' COMMENT 															'手机号码',
    `email` VARCHAR(128) DEFAULT NULL COMMENT 														    '邮箱地址',
    `status` VARCHAR(32) NOT NULL DEFAULT 'NORMAL' COMMENT 												'状态[NORMAL:正常, FROZEN:冻结]',
    `password_trusted` TINYINT NOT NULL DEFAULT 1 COMMENT                                               '密码是否可信[0:不可信/临时密码, 1:可信/用户已设置]',
    `create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT                                      '创建时间',
    `update_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT          '更新时间',
    `version` INT NOT NULL DEFAULT 0 COMMENT                                                            '记录版本',
    `delete_flag` TINYINT NOT NULL DEFAULT 0 COMMENT                                                    '删除标识[0:未删除, 1:已删除]',
    PRIMARY KEY (`id`),
    INDEX `idx_username` (`username`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=                             '账号表';

-- =============================================
-- 2. 身份源绑定：passport <-> 钉钉等 IdP（不自动建 passport）
-- =============================================
DROP TABLE IF EXISTS `passport_idp_binding`;
CREATE TABLE `passport_idp_binding` (
    `id` BIGINT NOT NULL COMMENT                                                                        '主键标识',
    `passport_id` BIGINT NOT NULL COMMENT                                                               '账号标识，关联 passport.id',
    `idp_type` VARCHAR(32) NOT NULL COMMENT                                                             '身份源类型[IdpType: DINGTALK|FEISHU|WECHAT_WORK；当前 IAM 仅钉钉]',
    `idp_subject` VARCHAR(128) NOT NULL COMMENT                                                         'IdP 侧稳定主体标识，建议存钉钉 unionId',
    `corp_id` VARCHAR(64) DEFAULT NULL COMMENT                                                          '钉钉企业 corpId；企业内部模式可由 IAM 写入默认 corp',
    `idp_user_id` VARCHAR(128) DEFAULT NULL COMMENT                                                     '钉钉 userid（corp 内），可选，便于审计与运营排查',
    `idp_application_code` VARCHAR(128) NOT NULL DEFAULT '' COMMENT                                     '三方应用在 IdP 侧的应用标识（如钉钉 OAuth clientId），与 application_idp_provision.idp_application_code 对齐',
    `bind_mode` VARCHAR(32) DEFAULT NULL COMMENT                                                        '接入形态[IdpBindMode: INTERNAL企业内部应用|THIRD_PARTY第三方应用；与钉钉换票链路对应，非OAuth两跳]',
    `raw_profile` JSON DEFAULT NULL COMMENT                                                             'IdP 返回的原始用户信息快照（可选）',
    `create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT                                      '创建时间',
    `update_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT          '更新时间',
    `version` INT NOT NULL DEFAULT 0 COMMENT                                                            '记录版本',
    `delete_flag` TINYINT NOT NULL DEFAULT 0 COMMENT                                                    '删除标识[0:未删除, 1:已删除]',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_idp_type_subject_app` (`idp_type`, `idp_subject`, `idp_application_code`),
    KEY `idx_passport_id` (`passport_id`),
    KEY `idx_corp_idp` (`corp_id`, `idp_type`),
    KEY `idx_delete_flag` (`delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=                             '账号与外部身份源绑定表';

-- =============================================
-- 3. 外部身份源应用 ↔ 平台应用（换票后 access 的 g2rain application）
-- 通行证与 IdP 的关系见 passport_idp_binding（含 idp_application_code）
-- =============================================
DROP TABLE IF EXISTS `application_idp_provision`;
CREATE TABLE `application_idp_provision` (
    `id` BIGINT NOT NULL COMMENT                                                                        '主键标识',
    `application_id` BIGINT NOT NULL COMMENT                                                            '平台应用标识，关联 application.id',
    `idp_type` VARCHAR(32) NOT NULL COMMENT                                                             '身份源类型，与 IdpType 枚举名一致',
    `idp_application_code` VARCHAR(128) NOT NULL COMMENT                                                '三方应用在 IdP 侧的标识（如钉钉 OAuth clientId）',
    `remark` VARCHAR(512) DEFAULT NULL COMMENT                                                          '备注',
    `create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT                                      '创建时间',
    `update_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT          '更新时间',
    `version` INT NOT NULL DEFAULT 0 COMMENT                                                            '记录版本',
    `delete_flag` TINYINT NOT NULL DEFAULT 0 COMMENT                                                    '删除标识[0:未删除, 1:已删除]',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_idp_application` (`idp_type`, `idp_application_code`),
    KEY `idx_application_id` (`application_id`),
    KEY `idx_delete_flag` (`delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=                             '外部身份源应用与平台应用的绑定';

-- =============================================
-- 4. 用户表 (user)
-- =============================================
DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
    `id` BIGINT NOT NULL COMMENT 													                    '用户标识',
    `passport_id` BIGINT NOT NULL COMMENT 														        '账号标识',
    `organ_id` BIGINT NOT NULL COMMENT 												                    '机构标识',
    `email` VARCHAR(128) DEFAULT NULL COMMENT 														    '邮箱地址',
    `mobile` VARCHAR(32) DEFAULT '' COMMENT 															'手机号码',
    `real_name` VARCHAR(128) DEFAULT NULL COMMENT 														'真实姓名',
    `admin` TINYINT NOT NULL DEFAULT 0 COMMENT 													        '管理员标记[0:否, 1:是]',
    `create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT                                      '创建时间',
    `update_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT          '更新时间',
    `version` INT NOT NULL DEFAULT 0 COMMENT                                                            '记录版本',
    `delete_flag` TINYINT NOT NULL DEFAULT 0 COMMENT                                                    '删除标识[0:未删除, 1:已删除]',
    PRIMARY KEY (`id`),
    INDEX `idx_passport_id` (`passport_id`),
    INDEX `idx_organ_id` (`organ_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=                             '用户表';

-- =============================================
-- 5. 机构表 (organ)
-- =============================================
DROP TABLE IF EXISTS `organ`;

CREATE TABLE `organ` (
    `id` BIGINT NOT NULL COMMENT 													                    '机构标识',
    `organ_name` VARCHAR(128) NOT NULL COMMENT 															'机构名称',
    `organ_type` VARCHAR(32) NOT NULL COMMENT 															'机构类型[服务商、渠道、公司、租户]',
    `status` VARCHAR(32) NOT NULL DEFAULT 'ACTIVE' COMMENT 											    '机构状态[ACTIVE:有效, INACTIVE:无效]',
    `admin` TINYINT NOT NULL DEFAULT 0 COMMENT 													        '运营标记[0:否, 1:是]',
    `create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT                                      '创建时间',
    `update_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT          '更新时间',
    `version` INT NOT NULL DEFAULT 0 COMMENT                                                            '记录版本',
    `delete_flag` TINYINT NOT NULL DEFAULT 0 COMMENT                                                    '删除标识[0:未删除, 1:已删除]',
    PRIMARY KEY (`id`),
    INDEX `idx_organ_name` (`organ_name`),
    INDEX `idx_organ_type` (`organ_type`),
    INDEX `idx_organ_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=                             '机构表';

-- =============================================
-- 6. 外部企业/租户 ↔ 平台机构（organ）多对多
-- 同一 (idp_type, enterprise_id) 可对应多个 organ_id（多租户）
-- =============================================
DROP TABLE IF EXISTS `idp_enterprise_organ`;
CREATE TABLE `idp_enterprise_organ` (
    `id` BIGINT NOT NULL COMMENT                                                                        '主键标识',
    `idp_type` VARCHAR(32) NOT NULL COMMENT                                                             '身份源类型[DINGTALK, WECHAT_WORK, FEISHU, ...]',
    `enterprise_id` VARCHAR(64) NOT NULL COMMENT                                                        '外部企业/租户标识（与 passport_idp_binding.enterprise_id 一致）',
    `organ_id` BIGINT NOT NULL COMMENT                                                                  '机构标识，关联 organ.id（业务上应为租户类型机构）',
    `status` VARCHAR(32) NOT NULL DEFAULT 'ACTIVE' COMMENT                                              '状态[ACTIVE:有效, INACTIVE:停用]',
    `remark` VARCHAR(512) DEFAULT NULL COMMENT                                                          '备注',
    `create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT                                      '创建时间',
    `update_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT          '更新时间',
    `version` INT NOT NULL DEFAULT 0 COMMENT                                                            '记录版本',
    `delete_flag` TINYINT NOT NULL DEFAULT 0 COMMENT                                                    '删除标识[0:未删除, 1:已删除]',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_idp_enterprise_organ` (`idp_type`, `enterprise_id`, `organ_id`),
    KEY `idx_organ_id` (`organ_id`),
    KEY `idx_idp_enterprise` (`idp_type`, `enterprise_id`),
    KEY `idx_delete_flag` (`delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=                             '外部企业/租户与平台机构关联表';

-- =============================================
-- 7. 机构路径关系表 (organ_closure)
-- =============================================
DROP TABLE IF EXISTS `organ_closure`;

CREATE TABLE `organ_closure` (
    `id` BIGINT NOT NULL COMMENT 													                    '主键标识',
    `ancestor_id` BIGINT NOT NULL COMMENT  													            '祖先机构标识[上级]',
    `descendant_id` BIGINT NOT NULL COMMENT   													        '后代机构标识[下级]',
    `descendant_type` VARCHAR(32) NOT NULL COMMENT 													    '后代机构类型[服务商、渠道、公司、租户]',
    `relation_type` VARCHAR(32) NOT NULL COMMENT                                                        '关系类型[SELF_ASSOCIATION:自身关联, DIRECT_SUBORDINATE:直属, INDIRECT_SUBORDINATE:从属]',
    `path_count` INT NOT NULL DEFAULT 1 COMMENT                                                         '路径引用次数[用于DAG交叉挂载维护]',
    `create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT                                      '创建时间',
    `update_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT          '更新时间',
    `version` INT NOT NULL DEFAULT 0 COMMENT                                                            '记录版本',
    `delete_flag` TINYINT NOT NULL DEFAULT 0 COMMENT                                                    '删除标识[0:未删除, 1:已删除]',
    PRIMARY KEY (`id`),
    INDEX idx_ancestor_id (ancestor_id),
    INDEX idx_descendant_id (descendant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=                             '机构路径关系表';

-- =============================================
-- 8. 应用资源菜单表 (resource_menu)
-- =============================================
DROP TABLE IF EXISTS `resource_menu`;

CREATE TABLE `resource_menu` (
    `id` BIGINT NOT NULL COMMENT 													                    '菜单标识',
    `parent_id` BIGINT DEFAULT NULL COMMENT 												            '父菜单标识',
    `application_id` BIGINT NOT NULL COMMENT 													        '应用标识',
    `menu_name` VARCHAR(128) NOT NULL COMMENT													        '菜单名称',
    `menu_code` VARCHAR(64) NOT NULL COMMENT 														    '菜单编码',
    `link_path` VARCHAR(128) DEFAULT NULL COMMENT 													    '链接路径',
    `icon` VARCHAR(32) DEFAULT NULL COMMENT 															'展示图标',
    `menu_sort_order` INT NOT NULL DEFAULT 0 COMMENT 												    '菜单排序',
    `create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT                                      '创建时间',
    `update_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT          '更新时间',
    `version` INT NOT NULL DEFAULT 0 COMMENT                                                            '记录版本',
    `delete_flag` TINYINT NOT NULL DEFAULT 0 COMMENT                                                    '删除标识[0:未删除, 1:已删除]',
    PRIMARY KEY (`id`),
    INDEX `idx_app_del_id` (`application_id`, `delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=                             '应用资源菜单表';

-- =============================================
-- 9. 应用资源页面表 (resource_page)
-- =============================================
DROP TABLE IF EXISTS `resource_page`;

CREATE TABLE `resource_page` (
     `id` BIGINT NOT NULL COMMENT 													                    '页面标识',
     `application_id` BIGINT NOT NULL COMMENT 													        '应用标识',
     `page_name` VARCHAR(128) NOT NULL COMMENT												            '页面名称',
     `page_code` VARCHAR(128) NOT NULL COMMENT												            '页面编码',
     `link_path` VARCHAR(128) NOT NULL COMMENT 													        '链接路径',
     `create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT                                     '创建时间',
     `update_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT         '更新时间',
     `version` INT NOT NULL DEFAULT 0 COMMENT                                                           '记录版本',
     `delete_flag` TINYINT NOT NULL DEFAULT 0 COMMENT                                                   '删除标识[0:未删除, 1:已删除]',
     PRIMARY KEY (`id`),
     INDEX `idx_app_del_id` (`application_id`, `delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=                             '应用资源页面表';

-- =============================================
-- 10. 应用资源页面元素表 (resource_page_element)
-- =============================================
DROP TABLE IF EXISTS `resource_page_element`;

CREATE TABLE `resource_page_element` (
    `id` BIGINT NOT NULL COMMENT 													                    '页面元素标识',
    `application_id` BIGINT NOT NULL COMMENT 													        '应用标识',
    `page_code` VARCHAR(128) NOT NULL COMMENT														    '页面编码',
    `page_element_name` VARCHAR(128) NOT NULL COMMENT												    '页面元素名称',
    `page_element_code` VARCHAR(64) NOT NULL COMMENT												    '页面元素编码',
    `create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT                                      '创建时间',
    `update_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT          '更新时间',
    `version` INT NOT NULL DEFAULT 0 COMMENT                                                            '记录版本',
    `delete_flag` TINYINT NOT NULL DEFAULT 0 COMMENT                                                    '删除标识[0:未删除, 1:已删除]',
    PRIMARY KEY (`id`),
    INDEX `idx_app_del_id` (`application_id`, `delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=                             '应用资源页面元素表';

-- =============================================
-- 11. 服务注册表 (service_registry)
-- =============================================
DROP TABLE IF EXISTS `service_registry`;

CREATE TABLE `service_registry` (
   `id` BIGINT NOT NULL COMMENT 													                   '后端服务标识',
   `service_code` VARCHAR(64) NOT NULL COMMENT                                                         '服务逻辑编码',
   `name` VARCHAR(128) NOT NULL COMMENT                                                                '服务显示名称',
   `endpoint` VARCHAR(256) NOT NULL COMMENT                                                            '服务目标地址',
   `route_prefix` VARCHAR(128) NOT NULL COMMENT                                                        '网关路由前缀',
   `description` VARCHAR(512) DEFAULT NULL COMMENT                                                     '后端服务说明',
   `create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT                                      '创建时间',
   `update_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT          '更新时间',
   `version` INT NOT NULL DEFAULT 0 COMMENT                                                            '记录版本',
   `delete_flag` TINYINT NOT NULL DEFAULT 0 COMMENT                                                    '删除标识[0:未删除, 1:已删除]',
   PRIMARY KEY (`id`),
   UNIQUE KEY `uk_service_code` (`service_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=                             '服务注册表';

-- =============================================
-- 12. 资源接口表 (resource_api)
-- =============================================
DROP TABLE IF EXISTS `resource_api`;

CREATE TABLE `resource_api` (
    `id` BIGINT NOT NULL COMMENT                                                                        '资源接口标识',
    `service_code` VARCHAR(64) NOT NULL COMMENT                                                         '服务逻辑编码',
    `api_tags` VARCHAR(128) NOT NULL COMMENT                                                            '资源接口标签',
    `name` VARCHAR(128) NOT NULL COMMENT                                                                '资源接口名称',
    `method` VARCHAR(32) NOT NULL COMMENT                                                               '接口请求方法',
    `path` VARCHAR(512) NOT NULL COMMENT                                                                '接口请求路径',
    `description` VARCHAR(512) DEFAULT NULL COMMENT                                                     '资源接口说明',
    `create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT                                      '创建时间',
    `update_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT          '更新时间',
    `version` INT NOT NULL DEFAULT 0 COMMENT                                                            '记录版本',
    `delete_flag` TINYINT NOT NULL DEFAULT 0 COMMENT                                                    '删除标识[0:未删除, 1:已删除]',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_service_method_path` (`service_code`,`method`,`path`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=                             '资源接口表';

-- =============================================
-- 13. 控制单元表 (control_unit)
-- =============================================
DROP TABLE IF EXISTS `control_unit`;

CREATE TABLE `control_unit` (
    `id` BIGINT NOT NULL COMMENT 													                    '控制单元标识',
    `application_id` BIGINT NOT NULL COMMENT 													        '应用标识',
    `control_unit_name` VARCHAR(128) NOT NULL COMMENT                                                   '控制单元名称',
    `control_unit_scope` VARCHAR(32) NOT NULL COMMENT                                                   '控制单元类型[OPERATION("运营功能"), CUSTOMER("客户功能"), PERPETUAL("永久有效功能")]',
    `landing` TINYINT NOT NULL DEFAULT 0 COMMENT                                                        '默认数据[0:否, 1:是]',
    `status` VARCHAR(32) NOT NULL DEFAULT 'UNPUBLISHED' COMMENT 										'控制单元状态[PUBLISHED:已发布, UNPUBLISHED:未发布]',
    `description` VARCHAR(512) DEFAULT NULL COMMENT   												    '业务说明',
    `create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT                                      '创建时间',
    `update_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT          '更新时间',
    `version` INT NOT NULL DEFAULT 0 COMMENT                                                            '记录版本',
    `delete_flag` TINYINT NOT NULL DEFAULT 0 COMMENT                                                    '删除标识[0:未删除, 1:已删除]',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=                             '控制单元表';

-- =============================================
-- 14. 控制单元资源关联表 (control_unit_resource_relation)
-- =============================================
DROP TABLE IF EXISTS `control_unit_resource_relation`;

CREATE TABLE `control_unit_resource_relation` (
    `id` BIGINT NOT NULL COMMENT 													                    '主键标识',
    `control_unit_id` BIGINT NOT NULL COMMENT							    					        '控制单元标识',
    `resource_id` BIGINT NOT NULL COMMENT							    					            '资源标识',
    `resource_type` VARCHAR(32) NOT NULL COMMENT												        '资源类型[MENU:菜单, PAGE:页面, PAGE_ELEMENT:页面元素, API_ENDPOINT:接口地址]',
    `status`       VARCHAR(32) DEFAULT NULL COMMENT												        '激活状态[VISIBLE:显示, ENABLED:可用]',
    `create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT                                      '创建时间',
    `update_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT          '更新时间',
    `version` INT NOT NULL DEFAULT 0 COMMENT                                                            '记录版本',
    `delete_flag` TINYINT NOT NULL DEFAULT 0 COMMENT                                                    '删除标识[0:未删除, 1:已删除]',
    PRIMARY KEY (`id`),
    INDEX `idx_cu_type_del_res` (`control_unit_id`, `resource_type`, `delete_flag`, `resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=                             '控制单元资源关联表';

-- =============================================
-- 15. 角色表 (role)
-- =============================================
DROP TABLE IF EXISTS `role`;

CREATE TABLE `role` (
    `id` BIGINT NOT NULL COMMENT 													                    '角色标识',
    `organ_id` BIGINT DEFAULT NULL COMMENT 														        '机构标识',
    `role_name` VARCHAR(128) NOT NULL COMMENT 														    '角色名称',
    `role_type` VARCHAR(32) NOT NULL COMMENT 													        '角色类型[ADMIN:超管角色-只读, USER:用户角色]',
    `create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT                                      '创建时间',
    `update_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT          '更新时间',
    `version` INT NOT NULL DEFAULT 0 COMMENT                                                            '记录版本',
    `delete_flag` TINYINT NOT NULL DEFAULT 0 COMMENT                                                    '删除标识[0:未删除, 1:已删除]',
    PRIMARY KEY (`id`),
    INDEX `idx_organ_id_id` (`organ_id`, `id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=                             '角色表';

-- =============================================
-- 16. 用户角色关联表 (user_role_relation)
-- =============================================
DROP TABLE IF EXISTS `user_role_relation`;

CREATE TABLE `user_role_relation` (
    `id` BIGINT NOT NULL COMMENT 													                    '主键标识',
    `user_id` BIGINT NOT NULL COMMENT 															        '用户标识',
    `role_id` BIGINT NOT NULL COMMENT 															        '角色标识',
    `create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT                                      '创建时间',
    `update_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT          '更新时间',
    `version` INT NOT NULL DEFAULT 0 COMMENT                                                            '记录版本',
    `delete_flag` TINYINT NOT NULL DEFAULT 0 COMMENT                                                    '删除标识[0:未删除, 1:已删除]',
    PRIMARY KEY (`id`),
    INDEX `idx_organ_id_id` (`user_id`, `role_id`, `delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=                             '用户角色关联表';

-- =============================================
-- 17. 角色控制单元关联表 (role_control_unit_relation)
-- =============================================
DROP TABLE IF EXISTS `role_control_unit_relation`;

CREATE TABLE `role_control_unit_relation` (
    `id` BIGINT NOT NULL COMMENT 													                    '主键标识',
    `role_id` BIGINT NOT NULL COMMENT 															        '角色标识',
    `control_unit_id` BIGINT NOT NULL COMMENT 													        '控制单元标识',
    `application_authorization_id` BIGINT DEFAULT NULL COMMENT                                          '应用授权标识',
    `status` VARCHAR(32) NOT NULL DEFAULT 'ACTIVATED' COMMENT 											'控制单元状态[ACTIVATED:激活, DEACTIVATED:关停]',
    `create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT                                      '创建时间',
    `update_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT          '更新时间',
    `version` INT NOT NULL DEFAULT 0 COMMENT                                                            '记录版本',
    `delete_flag` TINYINT NOT NULL DEFAULT 0 COMMENT                                                    '删除标识[0:未删除, 1:已删除]',
    PRIMARY KEY (`id`),
    -- 默认索引：按角色 + 控制单元 + 状态
    INDEX `idx_role_sts_del_cu` (`role_id`, `status`, `delete_flag`, `control_unit_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=                             '角色控制单元关联表';

-- =============================================
-- 18. 控制域表 (control_domain)
-- =============================================
DROP TABLE IF EXISTS `control_domain`;

CREATE TABLE `control_domain` (
    `id` BIGINT NOT NULL COMMENT 													                    '控制域标识',
    `application_id` BIGINT NOT NULL COMMENT 													        '应用标识',
    `control_domain_name` VARCHAR(128) NOT NULL COMMENT                                                 '控制域名称',
    `control_domain_type` VARCHAR(32) NOT NULL COMMENT                                                  '控制域类型[TRADE("交易开通"), APPLICATION("应用授权开通")]',
    `control_domain_scope` VARCHAR(32) NOT NULL COMMENT                                                 '交付范围[CUSTOMER("客户交付"), OPERATION("平台运营")]',
    `description` TEXT DEFAULT NULL COMMENT   												            '业务说明',
    `create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT                                      '创建时间',
    `update_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT          '更新时间',
    `version` INT NOT NULL DEFAULT 0 COMMENT                                                            '记录版本',
    `delete_flag` TINYINT NOT NULL DEFAULT 0 COMMENT                                                    '删除标识[0:未删除, 1:已删除]',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=                             '控制域表';

-- =============================================
-- 19. 控制域控制单元关联表 (control_domain_control_unit_relation)
-- =============================================
DROP TABLE IF EXISTS `control_domain_control_unit_relation`;

CREATE TABLE `control_domain_control_unit_relation` (
    `id` BIGINT NOT NULL COMMENT 													                    '主键标识',
    `control_domain_id` BIGINT NOT NULL COMMENT                                                         '控制域标识',
    `control_unit_id` BIGINT NOT NULL COMMENT							    					        '控制单元标识',
    `create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT                                      '创建时间',
    `update_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT          '更新时间',
    `version` INT NOT NULL DEFAULT 0 COMMENT                                                            '记录版本',
    `delete_flag` TINYINT NOT NULL DEFAULT 0 COMMENT                                                    '删除标识[0:未删除, 1:已删除]',
    PRIMARY KEY (`id`),
    INDEX `idx_control_domain_unit` (`control_domain_id`, `control_unit_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=                             '控制域控制单元关联表';

-- =============================================
-- 20. 应用表 (application)
-- =============================================
DROP TABLE IF EXISTS `application`;

CREATE TABLE `application` (
    `id` BIGINT NOT NULL COMMENT 													                    '应用标识',
    `organ_id` BIGINT NOT NULL COMMENT 												                    '机构标识',
    `application_name` VARCHAR(128) NOT NULL COMMENT                                                    '应用名称',
    `application_code` VARCHAR(64) DEFAULT NULL COMMENT                                                 '应用编码',
    `can_integrate` TINYINT NOT NULL DEFAULT 0 COMMENT                                                  '是否具备集成功能[0:否, 1:是]',
    `landing` TINYINT NOT NULL DEFAULT 0 COMMENT                                                        '默认数据[0:否, 1:是]',
    `api_key_supported` TINYINT NOT NULL DEFAULT 0 COMMENT                                              '支持API密钥[0:否, 1:是]',
    `application_type` VARCHAR(32) NOT NULL COMMENT                                                     '应用类型[SUPPORT:支撑, SYSTEM:系统提供, PUBLIC:第三方提供, PRIVATE:私有]',
    `public_key_algorithm` VARCHAR(32) DEFAULT NULL COMMENT                                             '应用公钥算法',
    `public_key_format` VARCHAR(32)  DEFAULT NULL COMMENT                                               '应用公钥格式',
    `public_key` TEXT DEFAULT NULL COMMENT                                                              '应用公钥内容',
    `access_token_expires_in` INT NOT NULL COMMENT                                                      '访问令牌生存时间(秒)',
    `refresh_token_expires_in` INT NOT NULL COMMENT                                                     '刷新访问令牌生存时间(秒)',
    `endpoint_url` VARCHAR(512) NOT NULL COMMENT   												        '访问地址',
    `context_path` VARCHAR(512) DEFAULT NULL COMMENT   												    '应用路径',
    `status` VARCHAR(32) NOT NULL DEFAULT 'UNPUBLISHED' COMMENT 										'应用状态[PUBLISHED:已发布, UNPUBLISHED:未发布]',
    `description` VARCHAR(512) DEFAULT NULL COMMENT   												    '业务说明',
    `create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT                                      '创建时间',
    `update_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT          '更新时间',
    `version` INT NOT NULL DEFAULT 0 COMMENT                                                            '记录版本',
    `delete_flag` TINYINT NOT NULL DEFAULT 0 COMMENT                                                    '删除标识[0:未删除, 1:已删除]',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=                             '应用表';

-- =============================================
-- 21. 应用归类关系表 (application_suite)
-- =============================================
DROP TABLE IF EXISTS `application_suite`;

CREATE TABLE `application_suite` (
    `id` BIGINT NOT NULL COMMENT 													                    '主键标识',
    `application_id` BIGINT NOT NULL COMMENT 											                '应用标识',
    `master_application_id` BIGINT NOT NULL COMMENT                                                     '主应用标识',
    `create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT                                      '创建时间',
    `update_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT          '更新时间',
    `version` INT NOT NULL DEFAULT 0 COMMENT                                                            '记录版本',
    `delete_flag` TINYINT NOT NULL DEFAULT 0 COMMENT                                                    '删除标识[0:未删除, 1:已删除]',
    PRIMARY KEY (`id`),
    INDEX `uk_application_id` (`application_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=                             '应用归类关系表';

-- =============================================
-- 22. 应用授权记录表 (application_authorization)
-- =============================================
DROP TABLE IF EXISTS `application_authorization`;

CREATE TABLE `application_authorization` (
    `id` BIGINT NOT NULL COMMENT 													                    '应用授权标识',
    `organ_id` BIGINT NOT NULL COMMENT 												                    '机构标识',
    `application_id` BIGINT NOT NULL COMMENT 													        '应用标识',
    `control_domain_id` BIGINT NOT NULL COMMENT                                                         '控制域标识',
    `subscription_id` BIGINT DEFAULT NULL COMMENT                                                       '订阅标识',
    `status` VARCHAR(32) NOT NULL DEFAULT 'ACTIVATED' COMMENT 											'应用授权状态[ACTIVATED:激活, DEACTIVATED:关停]',
    `create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT                                      '创建时间',
    `update_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT          '更新时间',
    `version` INT NOT NULL DEFAULT 0 COMMENT                                                            '记录版本',
    `delete_flag` TINYINT NOT NULL DEFAULT 0 COMMENT                                                    '删除标识[0:未删除, 1:已删除]',
    PRIMARY KEY (`id`),
    INDEX `idx_application_id` (`application_id`),
    INDEX `idx_control_domain_id` (`control_domain_id`),
    INDEX `idx_organ_st_del_app` (`organ_id`, `status`, `delete_flag`, `application_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=                             '应用授权记录表';

-- =============================================
-- 23. 个人静态访问令牌表 (personal_static_access_token)
-- =============================================
DROP TABLE IF EXISTS `personal_static_access_token`;

CREATE TABLE `personal_static_access_token` (
     `id` BIGINT NOT NULL COMMENT 													                    '个人静态访问令牌标识',
     `application_authorization_id` BIGINT DEFAULT NULL COMMENT                                         '授权记录标识',
     `application_id` BIGINT NOT NULL COMMENT 													        '应用标识',
     `organ_id` BIGINT NOT NULL COMMENT 												                '机构标识',
     `user_id` BIGINT DEFAULT NULL COMMENT 														        '用户标识',
     `name` VARCHAR(128) NOT NULL COMMENT 															    '访问令牌名称',
     `token_hash` VARCHAR(64) NOT NULL COMMENT 												            '静态访问令牌的哈希摘要',
     `masked_token` VARCHAR(28) NOT NULL COMMENT 												        '脱敏令牌',
     `status` VARCHAR(32) NOT NULL DEFAULT 'ACTIVATED' COMMENT 											'状态[ACTIVATED:已启用, REVOKED:已吊销]',
     `create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT                                     '创建时间',
     `update_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT         '更新时间',
     `version` INT NOT NULL DEFAULT 0 COMMENT                                                           '记录版本',
     `delete_flag` TINYINT NOT NULL DEFAULT 0 COMMENT                                                   '删除标识[0:未删除, 1:已删除]',
     PRIMARY KEY (`id`),
     UNIQUE KEY `uk_token_hash` (`token_hash`),
     INDEX `idx_authorization_org_del` (`application_authorization_id`, `organ_id`, `delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=                             '个人静态访问令牌表';

-- =============================================
-- 24. 登录信息表 (login_token)
-- =============================================
DROP TABLE IF EXISTS `login_token`;

CREATE TABLE `login_token` (
    `id` BIGINT NOT NULL COMMENT 													                    '主键标识',
    `session_type` VARCHAR(32) DEFAULT NULL COMMENT 													'会话类型',
    `organ_id` BIGINT DEFAULT NULL COMMENT 														        '机构标识',
    `organ_type` VARCHAR(32) DEFAULT NULL COMMENT 													    '机构类型',
    `admin_company` TINYINT NOT NULL DEFAULT 0 COMMENT 											        '运营标记[0:否, 1:是]',
    `passport_id` BIGINT DEFAULT NULL COMMENT													        '账号标识',
    `user_id` BIGINT DEFAULT NULL COMMENT 														        '用户标识',
    `real_name` VARCHAR(128) DEFAULT NULL COMMENT 														'真实姓名',
    `admin_user` TINYINT NOT NULL DEFAULT 0 COMMENT 												    '管理员标记[0:否, 1:是]',
    `application_id` BIGINT DEFAULT NULL COMMENT 												        '应用标识',
    `application_organ_id` BIGINT DEFAULT NULL COMMENT 											        '应用组织标识',
    `client_id` VARCHAR(64) DEFAULT NULL COMMENT                                                        '客户端ID',
    `create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT                                      '创建时间',
    `update_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT          '更新时间',
    `version` INT NOT NULL DEFAULT 0 COMMENT                                                            '记录版本',
    `delete_flag` TINYINT NOT NULL DEFAULT 0 COMMENT                                                    '删除标识[0:未删除, 1:已删除]',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4 COLLATE=utf8mb4_unicode_ci COMMENT=								'登录信息表, 记录了当前登录状态的相关信息';

-- =============================================
-- 25. 审计事件表 (audit_event)
-- =============================================
DROP TABLE IF EXISTS `audit_event`;

CREATE TABLE `audit_event` (
    `id` BIGINT NOT NULL COMMENT 													                    '主键标识',
    `trace_id` VARCHAR(64) DEFAULT NULL COMMENT                                                         '网关跟踪标识',
    `client_id` VARCHAR(128) DEFAULT NULL COMMENT                                                       '客户端标识',
    `request_id` VARCHAR(64) DEFAULT NULL COMMENT                                                       '前端请求标识',
    `request_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT                                     '前端请求时间',
    `accept_language` VARCHAR(32) DEFAULT NULL COMMENT                                                  '语言偏好',
    `path` VARCHAR(512) DEFAULT NULL COMMENT                                                            '请求路径',
    `method` VARCHAR(32) DEFAULT NULL COMMENT                                                           '请求方法',
    `user_agent` VARCHAR(512) DEFAULT NULL COMMENT                                                      '客户端标识',
    `host` VARCHAR(255) DEFAULT NULL COMMENT                                                            '请求主机',
    `x_forwarded_for` VARCHAR(1024) DEFAULT NULL COMMENT                                                '代理链IP列表',
    `x_real_ip` VARCHAR(64) DEFAULT NULL COMMENT                                                        '真实客户端IP',
    `referer` VARCHAR(2048) DEFAULT NULL COMMENT                                                        '请求来源URL',
    `session_type` VARCHAR(32) DEFAULT NULL COMMENT                                                     '会话类型',
    `passport_id` BIGINT DEFAULT NULL COMMENT                                                           '账号标识',
    `user_id` BIGINT DEFAULT NULL COMMENT                                                               '用户标识',
    `name` VARCHAR(128) DEFAULT NULL COMMENT                                                            '真实姓名',
    `admin_user` TINYINT DEFAULT 0 COMMENT                                                              '超级管理员',
    `organ_id` BIGINT DEFAULT NULL COMMENT                                                              '组织标识',
    `organ_name` VARCHAR(255) DEFAULT NULL COMMENT                                                      '组织名称',
    `organ_type` VARCHAR(32) DEFAULT NULL COMMENT                                                       '组织类型',
    `admin_company` TINYINT DEFAULT 0 COMMENT                                                           '平台运营组织',
    `target_organ_id` BIGINT DEFAULT NULL COMMENT                                                       '数据操作的目标组织标识',
    `application_id` BIGINT DEFAULT NULL COMMENT                                                        '请求来源应用标识',
    `application_code` VARCHAR(64) DEFAULT NULL COMMENT                                                 '请求来源应用编码',
    `application_organ_id` BIGINT DEFAULT NULL COMMENT                                                  '请求来源应用所属机构标识',
    `payload` JSON DEFAULT NULL COMMENT                                                                 '请求/响应摘要',
    `create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT                                      '创建时间',
    `update_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT          '更新时间',
    `version` INT NOT NULL DEFAULT 0 COMMENT                                                            '记录版本',
    PRIMARY KEY (`id`),
    INDEX `idx_trace_id` (`trace_id`),
    INDEX `idx_request_id` (`request_id`),
    INDEX `idx_client_id` (`client_id`),
    INDEX `idx_passport_id` (`passport_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_organ_id` (`organ_id`),
    INDEX `idx_application_id` (`application_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=                         '审计事件表';

-- 账号
INSERT INTO `passport`
(`id`, `username`, `password`, `real_name`, `sex`, `birthday`, `id_no`, `mobile`, `email`, `status`, `create_time`, `update_time`)
VALUES
    (207, 'admin', 'PBKDF2WithHmacSHA256$65536$YSskABrEQZiuRcM3GMl6gQ==$Hl9gA9UnYmS1BoY3Ov3XY2qYQpUKF1Sl0QneYZ5zc7k=', '平台超管', 'MALE', NULL, NULL, NULL, NULL, 'NORMAL', '2026-02-01 09:12:28', '2026-02-01 09:12:28');

-- 机构
INSERT INTO `organ`
(`id`, `organ_name`, `organ_type`, `status`, `admin`, `create_time`, `update_time`)
VALUES
    (208, '平台机构', 'COMPANY', 'ACTIVE', 1, '2026-02-01 09:12:28', '2026-02-01 09:12:28');

-- 机构路径关系
INSERT INTO `organ_closure`
(`id`, `ancestor_id`, `descendant_id`, `descendant_type`, `relation_type`, `path_count`, `create_time`, `update_time`)
VALUES
    (209, 208, 208, 'COMPANY', 'SELF_ASSOCIATION', 1, '2026-02-01 09:12:28', '2026-02-01 09:12:28');

-- 角色
INSERT INTO `role`
(`id`, `organ_id`, `role_name`, `role_type`, `create_time`, `update_time`)
VALUES
    (210, 208, '超管角色', 'ADMIN', '2026-02-01 09:12:28', '2026-02-01 09:12:28');

-- 用户
INSERT INTO `user`
(`id`, `passport_id`, `organ_id`, `email`, `mobile`, `real_name`, `admin`, `create_time`, `update_time`)
VALUES
    (211, 207, 208, NULL, NULL, '平台管理员', 1, '2026-02-01 09:12:28', '2026-02-01 09:12:28');

-- 用户角色关联
INSERT INTO `user_role_relation`
(`id`, `user_id`, `role_id`, `create_time`, `update_time`)
VALUES
    (212, 211, 210, '2026-02-01 09:12:28', '2026-02-01 09:12:28');

-- 应用
INSERT INTO `application`
(`id`, `organ_id`, `application_name`, `application_code`, `can_integrate`, `landing`, `api_key_supported`, `application_type`, `public_key_algorithm`, `public_key_format`, `public_key`, `access_token_expires_in`, `refresh_token_expires_in`, `endpoint_url`, `context_path`, `status`, `description`, `create_time`, `update_time`)
VALUES
    (213, 208, '综合管理平台', 'g2rain-main-shell',  0, 1, 0, 'SUPPORT', 'EC', 'PEM', '-----BEGIN PUBLIC KEY-----\nMFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEXmlg1y2fUD9KJj4WB6DrRZU+iVwA yzz60AxRoFb2yDnBvYiiK9JR1p5QUw2jkR9RPvkZez1Kx2BqxwyOoWRV/A==\n-----END PUBLIC KEY-----\n', 3600, 86400, '//demo.g2rain.com', '/main/',    'PUBLISHED', '管理平台入口', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (215, 208, '业务支撑平台', 'g2rain-manager-app',  1, 1, 0, 'SUPPORT', 'EC', 'PEM', '-----BEGIN PUBLIC KEY-----\nMFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEXGDOn5B+GFE42lcMd5u47r6na9iE H1AzxAU49KiWBz17su0M1vPZ+s57bvMlYvbcPG2nfWcJvJzRuKUakrUhsA==\n-----END PUBLIC KEY-----\n', 3600, 86400, '//demo.g2rain.com', '/manager/',    'PUBLISHED', '业务支撑平台', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (216, 208, '基础支撑平台', 'g2rain-infra-app', 1, 1, 0, 'SUPPORT', 'EC', 'PEM', '-----BEGIN PUBLIC KEY-----\nMFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEAcmLmXDroj3aJiTFxP6oy5Q+3Tawz1LFg0BY1a5CRNynqpVvG+/wVGUhXf7KOJ7/nA2OO/H+IQaHryS+SXtnOA==\n-----END PUBLIC KEY-----\n', 3600, 86400, '//demo.g2rain.com', '/infra/', 'PUBLISHED', '用于管理字典, 国际化, 发号器功能', '2026-02-01 09:12:28', '2026-02-01 09:12:28');

-- 应用归类关系
INSERT INTO `application_suite`
(`id`, `application_id`, `master_application_id`, `create_time`, `update_time`)
VALUES
    (217, 215, 213, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (218, 216, 213, '2026-02-01 09:12:28', '2026-02-01 09:12:28');

-- 控制单元
INSERT INTO `control_unit`
(`id`, `application_id`, `control_unit_name`, `control_unit_scope`, `landing`, `status`, `description`, `create_time`, `update_time`)
VALUES
    (219, 213, '盘古',   'PERPETUAL', 1, 'PUBLISHED', '平台准入基础能力', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (220, 215, '燧人氏', 'OPERATION', 1, 'PUBLISHED', '核心运营支撑组件', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (221, 216, '女娲',   'OPERATION',  1, 'PUBLISHED', '平台保障技术能力', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (222, 215, '后羿',   'CUSTOMER',  1, 'PUBLISHED', '租户空间构建逻辑', '2026-02-01 09:12:28', '2026-02-01 09:12:28');

-- 角色控制单元关联
INSERT INTO `role_control_unit_relation`
(`id`, `role_id`, `control_unit_id`, `application_authorization_id`, `status`, `create_time`, `update_time`)
VALUES
    (223, 210, 220, NULL, 'ACTIVATED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (225, 210, 221, NULL, 'ACTIVATED', '2026-02-01 09:12:28', '2026-02-01 09:12:28');

-- 服务注册表
INSERT INTO `service_registry`
(`id`, `service_code`, `name`, `endpoint`, `route_prefix`, `description`, `create_time`, `update_time`)
VALUES
    (226, 'G2RAIN_BASIS', '业务支撑服务', 'lb://g2rain-basis', 'basis', '业务支撑服务', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (227, 'G2RAIN_INFRA', '基础支撑服务', 'lb://g2rain-infra', 'infra', '基础支撑服务', '2026-02-01 09:12:28', '2026-02-01 09:12:28');

-- 资源后端接口
INSERT INTO `resource_api`
(`id`, `service_code`, `api_tags`, `name`, `method`, `path`, `description`, `create_time`, `update_time`)
VALUES
    (223,'G2RAIN_INFRA','地域语言','新增或更新地域语言设置','POST','/locale_setting/save','新增或更新地域与语言偏好配置','2026-05-08 10:02:08','2026-05-08 10:02:08'),
    (225,'G2RAIN_INFRA','国际化信息','新增或更新国际化信息','POST','/i18n_message/save','新增或更新国际化文案信息','2026-05-08 10:02:08','2026-05-08 10:02:08'),
    (226,'G2RAIN_INFRA','全局唯一序列','新增或更新全局唯一 ID 记录','POST','/g2rain_raindrop/save','新增或更新全局唯一 ID 管理表数据','2026-05-08 10:02:08','2026-05-08 10:02:08'),
    (227,'G2RAIN_INFRA','字典用途','新增或更新字典用途','POST','/dictionary_usage/save','新增或更新字典用途信息','2026-05-08 10:02:08','2026-05-08 10:02:08'),
    (228,'G2RAIN_INFRA','字典明细','新增或更新字典明细','POST','/dictionary_item/save','新增或更新字典明细信息','2026-05-08 10:02:08','2026-05-08 10:02:08'),
    (231,'G2RAIN_INFRA','地域语言','分页查询地域语言设置列表','GET','/locale_setting/page','分页查询地域-语言设置列表','2026-05-08 10:02:08','2026-05-08 10:02:08'),
    (232,'G2RAIN_INFRA','地域语言','获取地域语言字典','GET','/locale_setting/locale_dict','获取地域语言字典','2026-05-08 10:02:08','2026-05-08 10:02:08'),
    (233,'G2RAIN_INFRA','地域语言','查询地域语言设置列表','GET','/locale_setting/list','根据查询条件返回地域-语言设置列表','2026-05-08 10:02:08','2026-05-08 10:02:08'),
    (235,'G2RAIN_INFRA','地域语言','获取语言地域映射','GET','/locale_setting/get_language_countries','获取语言地域映射','2026-05-08 10:02:08','2026-05-08 10:02:08'),
    (236,'G2RAIN_INFRA','地域语言','获取地域语言编码和名称映射集合','GET','/locale_setting/code_name_map','获取地域语言编码和名称映射集合','2026-05-08 10:02:08','2026-05-08 10:02:08'),
    (237,'G2RAIN_INFRA','国际化信息','分页查询国际化信息列表','GET','/i18n_message/page','分页查询国际化信息列表','2026-05-08 10:02:08','2026-05-08 10:02:08'),
    (238,'G2RAIN_INFRA','国际化信息','查询国际化信息列表','GET','/i18n_message/list','根据查询条件返回国际化信息列表','2026-05-08 10:02:08','2026-05-08 10:02:08'),
    (239,'G2RAIN_INFRA','国际化信息','获取国际化用途集合','GET','/i18n_message/i18n_message_usages','获取国际化用途集合','2026-05-08 10:02:08','2026-05-08 10:02:08'),
    (260,'G2RAIN_INFRA','全局唯一序列','分页查询全局唯一 ID 记录列表','GET','/g2rain_raindrop/page','分页查询全局唯一 ID 管理记录列表','2026-05-08 10:02:08','2026-05-08 10:02:08'),
    (261,'G2RAIN_INFRA','全局唯一序列','查询全局唯一 ID 记录列表','GET','/g2rain_raindrop/list','根据查询条件返回全局唯一 ID 管理记录列表','2026-05-08 10:02:08','2026-05-08 10:02:08'),
    (262,'G2RAIN_INFRA','全局唯一序列','查询业务标签字典集合','GET','/g2rain_raindrop/biz_tag_dict','查询业务标签字典集合','2026-05-08 10:02:08','2026-05-08 10:02:08'),
    (263,'G2RAIN_INFRA','字典用途','分页查询字典用途列表','GET','/dictionary_usage/page','分页查询字典用途列表','2026-05-08 10:02:08','2026-05-08 10:02:08'),
    (265,'G2RAIN_INFRA','字典用途','查询字典用途列表','GET','/dictionary_usage/list','根据查询条件返回字典用途列表','2026-05-08 10:02:08','2026-05-08 10:02:08'),
    (266,'G2RAIN_INFRA','字典明细','分页查询字典明细列表','GET','/dictionary_item/tree','分页查询字典明细列表','2026-05-08 10:02:08','2026-05-08 10:02:08'),
    (267,'G2RAIN_INFRA','字典明细','分页查询字典明细列表','GET','/dictionary_item/page','分页查询字典明细列表','2026-05-08 10:02:08','2026-05-08 10:02:08'),
    (268,'G2RAIN_INFRA','字典明细','查询字典明细列表','GET','/dictionary_item/list','根据查询条件返回字典明细列表','2026-05-08 10:02:08','2026-05-08 10:02:08'),
    (270,'G2RAIN_INFRA','地域语言','删除地域语言设置记录','DELETE','/locale_setting/{id}','根据主键删除地域-语言设置记录','2026-05-08 10:02:08','2026-05-08 10:02:08'),
    (271,'G2RAIN_INFRA','国际化信息','删除国际化信息记录','DELETE','/i18n_message/{id}','根据主键删除国际化信息记录','2026-05-08 10:02:08','2026-05-08 10:02:08'),
    (272,'G2RAIN_INFRA','全局唯一序列','删除全局唯一 ID 记录','DELETE','/g2rain_raindrop/{id}','根据主键删除全局唯一 ID 管理记录','2026-05-08 10:02:08','2026-05-08 10:02:08'),
    (273,'G2RAIN_INFRA','字典用途','删除字典用途记录','DELETE','/dictionary_usage/{id}','根据主键删除字典用途记录','2026-05-08 10:02:08','2026-05-08 10:02:08'),
    (275,'G2RAIN_INFRA','字典明细','删除字典明细记录','DELETE','/dictionary_item/{id}','根据主键删除字典明细记录','2026-05-08 10:02:08','2026-05-08 10:02:08'),
    (276,'G2RAIN_BASIS','用户-角色关联','新增或更新用户角色关联','POST','/user_role_relation/save','新增或更新用户角色关联记录','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (277,'G2RAIN_BASIS','用户-角色关联','为角色分配用户','POST','/user_role_relation/assign_users','批量为指定角色分配用户关联关系','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (278,'G2RAIN_BASIS','用户','新增或更新用户信息','POST','/user/save','新增或更新用户基础信息','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (279,'G2RAIN_BASIS','租户初始化','开通租户账号','POST','/tenant_provision/provision_account','为指定租户开通账号并初始化最小可用功能','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (280,'G2RAIN_BASIS','服务注册','新增或更新服务注册','POST','/service_registry/save','新增或更新服务注册信息','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (281,'G2RAIN_BASIS','角色-控制单元关联','新增角色控制单元关联','POST','/role_control_unit_relation/save','新增角色与控制单元的关联记录','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (282,'G2RAIN_BASIS','角色','新增或更新角色信息','POST','/role/save','新增或更新角色基础信息','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (283,'G2RAIN_BASIS','资源页面元素','新增或更新页面元素','POST','/resource_page_element/save','新增或更新应用资源页面元素信息','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (285,'G2RAIN_BASIS','资源页面','新增或更新资源页面','POST','/resource_page/save','新增或更新应用资源页面信息','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (286,'G2RAIN_BASIS','资源菜单','新增或更新资源菜单','POST','/resource_menu/save','新增或更新应用资源菜单信息','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (287,'G2RAIN_BASIS','资源上传','上传应用资源文件','POST','/resource/{applicationId}/upload','上传并解析指定应用的资源文件内容','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (288,'G2RAIN_BASIS','账号','更新账号状态','POST','/passport/{id}/status','根据主键更新账号启用状态','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (289,'G2RAIN_BASIS','账号','更新账号密码','POST','/passport/{id}/password','根据主键更新账号登录密码','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (290,'G2RAIN_BASIS','账号','新增或更新账号','POST','/passport/save','新增或更新账号信息','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (291,'G2RAIN_BASIS','机构','更新机构状态','POST','/organ/{id}/status','根据主键更新机构启用状态','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (292,'G2RAIN_BASIS','机构','调整机构层级关系','POST','/organ/{descendantId}/hierarchy','对指定机构执行挂载、迁移或卸载等层级调整操作','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (293,'G2RAIN_BASIS','机构','新增或更新机构信息','POST','/organ/save','新增或更新机构基础信息','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (295,'G2RAIN_BASIS','权限点资源关联','新增控制单元资源关联','POST','/control_unit_resource_relation/save','批量新增控制单元与资源关联记录','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (296,'G2RAIN_BASIS','控制单元','更新控制单元状态','POST','/control_unit/{id}/status','根据主键更新控制单元启用状态','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (297,'G2RAIN_BASIS','控制单元','新增或更新控制单元','POST','/control_unit/save','新增或更新控制单元基础信息','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (298,'G2RAIN_BASIS','控制域-控制单元关联','新增控制域控制单元关联','POST','/control_domain_control_unit_relation/save','批量新增控制域与控制单元关联记录','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (299,'G2RAIN_BASIS','控制域','新增或更新控制域信息','POST','/control_domain/save','新增或更新控制域基础信息','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (300,'G2RAIN_BASIS','应用归类关系','新增或更新应用归类关系','POST','/application_suite/save','新增或更新应用与归类的关联关系','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (301,'G2RAIN_BASIS','应用授权','修改应用授权记录状态','POST','/application_authorization/{id}/status','修改应用授权记录状态','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (302,'G2RAIN_BASIS','应用授权','新增或更新应用授权记录','POST','/application_authorization/save','新增或更新应用授权记录','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (303,'G2RAIN_BASIS','应用','更新应用状态','POST','/application/{id}/status','根据主键更新应用启用状态','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (305,'G2RAIN_BASIS','应用','下载应用公钥','GET','/application/{id}/public_key','下载指定应用的 PEM 或 DER 格式公钥文件','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (306,'G2RAIN_BASIS','应用','上传或更新应用公钥','POST','/application/{id}/public_key','上传 PEM/DER 公钥文件并更新应用公钥配置','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (307,'G2RAIN_BASIS','应用','新增或更新应用信息','POST','/application/save','新增或更新应用基础信息','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (308,'G2RAIN_BASIS','用户-角色关联','分页查询用户-角色关联列表','GET','/user_role_relation/page','分页查询用户角色关联列表','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (309,'G2RAIN_BASIS','用户-角色关联','查询用户-角色关联列表','GET','/user_role_relation/list','根据查询条件返回用户角色关联列表','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (310,'G2RAIN_BASIS','用户','获取用户下拉选项','GET','/user/user_options','返回用于下拉选择的用户简要信息集合','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (311,'G2RAIN_BASIS','用户','按角色查询用户列表','GET','/user/role/{roleId}','根据角色主键查询已关联用户列表','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (312,'G2RAIN_BASIS','用户','分页查询用户列表','GET','/user/page','分页查询用户列表','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (313,'G2RAIN_BASIS','用户','查询用户列表','GET','/user/list','根据查询条件返回用户列表','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (315,'G2RAIN_BASIS','服务注册','分页查询服务注册列表','GET','/service_registry/page','分页查询服务注册列表','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (316,'G2RAIN_BASIS','服务注册','查询服务注册列表','GET','/service_registry/list','根据查询条件返回服务注册列表','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (317,'G2RAIN_BASIS','角色-控制单元关联','按角色查询控制单元关联','GET','/role_control_unit_relation/role/{roleId}','根据角色主键查询角色控制单元关联列表','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (318,'G2RAIN_BASIS','角色-控制单元关联','分页查询角色-控制单元关联列表','GET','/role_control_unit_relation/page','分页查询角色控制单元关联列表','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (319,'G2RAIN_BASIS','角色-控制单元关联','查询角色-控制单元关联列表','GET','/role_control_unit_relation/list','根据查询条件返回角色控制单元关联列表','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (320,'G2RAIN_BASIS','角色','分页查询角色列表','GET','/role/page','分页查询角色列表','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (321,'G2RAIN_BASIS','角色','查询角色列表','GET','/role/list','根据查询条件返回角色列表','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (322,'G2RAIN_BASIS','资源页面元素','分页查询资源页面元素列表','GET','/resource_page_element/page','分页查询资源页面元素列表','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (323,'G2RAIN_BASIS','资源页面元素','查询资源页面元素列表','GET','/resource_page_element/list','根据查询条件返回资源页面元素列表','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (325,'G2RAIN_BASIS','资源页面','分页查询资源页面列表','GET','/resource_page/page','分页查询资源页面列表','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (326,'G2RAIN_BASIS','资源页面','查询资源页面列表','GET','/resource_page/list','根据查询条件返回资源页面列表','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (327,'G2RAIN_BASIS','资源菜单','分页查询资源菜单列表','GET','/resource_menu/page','分页查询资源菜单列表','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (328,'G2RAIN_BASIS','资源菜单','查询资源菜单列表','GET','/resource_menu/list','根据查询条件返回资源菜单列表','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (329,'G2RAIN_BASIS','资源接口','分页查询资源接口列表','GET','/resource_api/page','分页查询资源接口列表','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (330,'G2RAIN_BASIS','资源接口','查询资源接口列表','GET','/resource_api/list','根据查询条件返回资源接口列表','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (331,'G2RAIN_BASIS','账号','分页查询账号列表','GET','/passport/page','分页查询账号列表','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (332,'G2RAIN_BASIS','账号','查询账号列表','GET','/passport/list','根据查询条件返回账号列表','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (333,'G2RAIN_BASIS','机构','搜索机构','GET','/organ/search','根据机构名称关键字模糊查询机构列表','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (335,'G2RAIN_BASIS','机构','分页查询机构列表','GET','/organ/page','分页查询机构列表','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (336,'G2RAIN_BASIS','机构','查询机构列表','GET','/organ/list','根据查询条件返回机构列表','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (337,'G2RAIN_BASIS','机构','获取机构层级关系','GET','/organ/hierarchy','查询机构及其子机构的树形层级结构','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (338,'G2RAIN_BASIS','登录令牌','分页查询登录令牌列表','GET','/login_token/page','分页查询登录令牌列表','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (339,'G2RAIN_BASIS','登录令牌','查询登录令牌列表','GET','/login_token/list','根据查询条件返回登录令牌列表','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (350,'G2RAIN_BASIS','权限点资源关联','分页查询权限点资源关联列表','GET','/control_unit_resource_relation/page','分页查询权限点资源关联列表','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (351,'G2RAIN_BASIS','权限点资源关联','查询权限点资源关联列表','GET','/control_unit_resource_relation/list','根据查询条件返回权限点资源关联列表','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (352,'G2RAIN_BASIS','控制单元','分页查询控制单元列表','GET','/control_unit/page','分页查询控制单元列表','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (353,'G2RAIN_BASIS','控制单元','查询控制单元列表','GET','/control_unit/list','根据查询条件返回控制单元列表','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (355,'G2RAIN_BASIS','控制域-控制单元关联','分页查询控制域-控制单元关联列表','GET','/control_domain_control_unit_relation/page','分页查询控制域控制单元关联列表','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (356,'G2RAIN_BASIS','控制域-控制单元关联','查询控制域-控制单元关联列表','GET','/control_domain_control_unit_relation/list','根据查询条件返回控制域控制单元关联列表','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (357,'G2RAIN_BASIS','控制域','分页查询控制域列表','GET','/control_domain/page','分页查询控制域列表','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (358,'G2RAIN_BASIS','控制域','查询控制域列表','GET','/control_domain/list','根据查询条件返回控制域列表','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (359,'G2RAIN_BASIS','资源授权','查询当前用户信息','GET','/authority/user','查询当前登录用户的权限相关用户信息','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (360,'G2RAIN_BASIS','资源授权','查询资源权限信息','GET','/authority/resources','查询当前用户的资源访问权限信息','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (361,'G2RAIN_BASIS','资源授权','查询菜单权限列表','GET','/authority/menus','查询当前用户可访问的菜单权限列表','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (362,'G2RAIN_BASIS','审计事件','分页查询审计事件','GET','/audit_event/page','按条件筛选审计事件并分页，含总数与当前页数据','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (363,'G2RAIN_BASIS','审计事件','查询审计事件列表','GET','/audit_event/list','按条件筛选审计事件，不分页返回列表','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (365,'G2RAIN_BASIS','应用归类关系','分页查询应用归类关系列表','GET','/application_suite/page','分页查询应用归类关系列表','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (366,'G2RAIN_BASIS','应用归类关系','查询应用归类关系列表','GET','/application_suite/list','根据查询条件返回应用归类关系列表','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (367,'G2RAIN_BASIS','应用授权','分页查询应用授权记录列表','GET','/application_authorization/page','分页查询应用授权记录列表','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (368,'G2RAIN_BASIS','应用授权','查询应用授权记录列表','GET','/application_authorization/list','根据查询条件返回应用授权记录列表','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (369,'G2RAIN_BASIS','应用','检查应用公钥是否存在','GET','/application/{id}/has_public_key','检查指定应用是否已配置公钥','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (370,'G2RAIN_BASIS','应用','分页查询应用列表','GET','/application/page','分页查询应用列表','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (371,'G2RAIN_BASIS','应用','查询应用列表','GET','/application/list','根据查询条件返回应用列表','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (372,'G2RAIN_BASIS','应用','查询应用名称映射','GET','/application/id_name_map','根据查询条件获取应用 ID 与名称映射列表','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (373,'G2RAIN_BASIS','用户','删除用户记录','DELETE','/user/{id}','根据主键删除用户记录','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (375,'G2RAIN_BASIS','服务注册','删除服务注册','DELETE','/service_registry/{id}','根据主键删除服务注册记录','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (376,'G2RAIN_BASIS','角色','删除角色记录','DELETE','/role/{id}','根据主键删除角色记录','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (377,'G2RAIN_BASIS','资源页面元素','删除页面元素记录','DELETE','/resource_page_element/{id}','根据主键删除应用资源页面元素记录','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (378,'G2RAIN_BASIS','资源页面','删除资源页面记录','DELETE','/resource_page/{id}','根据主键删除应用资源页面记录','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (379,'G2RAIN_BASIS','资源菜单','删除资源菜单记录','DELETE','/resource_menu/{id}','根据主键删除应用资源菜单记录','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (380,'G2RAIN_BASIS','资源接口','根据主键删除资源接口记录','DELETE','/resource_api/{id}','根据主键删除资源接口记录','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (381,'G2RAIN_BASIS','账号','删除账号记录','DELETE','/passport/{id}','根据主键删除账号记录','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (382,'G2RAIN_BASIS','机构','删除机构记录','DELETE','/organ/{id}','根据主键删除机构记录','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (383,'G2RAIN_BASIS','控制单元','删除控制单元记录','DELETE','/control_unit/{id}','根据主键删除控制单元记录','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (385,'G2RAIN_BASIS','控制域','删除控制域记录','DELETE','/control_domain/{id}','根据主键删除控制域记录','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (386,'G2RAIN_BASIS','应用授权','根据主键删除应用授权记录','DELETE','/application_authorization/{id}','根据主键删除应用授权记录','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (387,'G2RAIN_BASIS','应用','删除应用记录','DELETE','/application/{id}','根据主键删除应用记录','2026-05-08 10:03:52','2026-05-08 10:03:52'),
    (388,'G2RAIN_BASIS','资源接口','批量导入资源接口','POST','/resource_api/{serviceCode}/import','批量导入资源接口信息','2026-04-28 02:14:24','2026-04-28 02:14:24'),
    (389,'G2RAIN_BASIS','资源接口','新增或更新资源接口','POST','/resource_api/save','新增或更新资源接口信息','2026-04-28 02:14:24','2026-04-28 02:14:24');

-- 应用资源菜单
INSERT INTO `resource_menu`
(`id`, `parent_id`, `application_id`, `menu_name`, `menu_code`, `link_path`, `icon`, `menu_sort_order`, `create_time`, `update_time`)
VALUES
    (390,NULL,208,'系统管理','system_management_menu','','',10000,'2026-02-01 01:12:28','2026-04-19 08:02:38'),
    (391,390,208,'机构管理','organ_management_menu','/organ','',1,'2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (392,390,208,'用户管理','user_management_menu','/user','',2,'2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (393,390,208,'角色管理','role_management_menu','/role','',3,'2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (395,NULL,208,'应用管理','application_management_menu','','',10001,'2026-02-01 01:12:28','2026-04-19 08:02:44'),
    (396,395,208,'应用配置','application_config_menu','/application','',1,'2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (397,395,208,'资源配置','resource_settings_menu','/resource_settings','',2,'2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (398,395,208,'资源菜单','resource_menu_menu','/resource_menu','',3,'2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (399,395,208,'资源页面','resource_page_menu','/resource_page','',5,'2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (500,395,208,'功能权限','control_unit_menu','/control_unit','',6,'2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (501,395,208,'业务能力','control_domain_menu','/control_domain','',7,'2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (502,395,208,'授权记录','application_authorization_menu','/application_authorization','',8,'2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (503,NULL,208,'平台配置','platform_settings_menu','','',10002,'2026-02-01 01:12:28','2026-04-19 08:02:50'),
    (505,503,208,'服务注册','service_registry_menu','/service_registry','',1,'2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (506,503,208,'服务接口','resource_api_menu','/resource_api','',2,'2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (507,NULL,208,'平台运营','platform_operations_menu','','',10003,'2026-02-01 01:12:28','2026-04-19 08:02:56'),
    (508,507,208,'账号管理','passport_management_menu','/passport','',1,'2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (509,507,208,'登陆日志','login_token_menu','/login_token','',1,'2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (510,507,208,'审计事件','audit_event_menu','/audit_event','',1,'2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (511,NULL,209,'平台技术','infra_menu','','',10004,'2026-04-11 04:06:57','2026-04-19 08:03:01'),
    (512,511,209,'序列号段调控','g2rain_raindrop_menu','/g2rain_raindrop','',1,'2026-04-11 04:15:29','2026-04-11 04:15:29'),
    (513,511,209,'字典用途设置','dictionary_usage_menu','/dictionary_usage','',2,'2026-04-11 04:10:42','2026-04-11 04:12:57'),
    (515,511,209,'地区语言设置','locale_setting_menu','/locale_setting','',3,'2026-04-11 04:15:06','2026-04-11 04:15:06'),
    (516,511,209,'多语言文案库','i18n_message_menu','/i18n_message','',5,'2026-04-11 04:13:38','2026-04-11 04:14:39');

-- 应用资源页面
INSERT INTO `resource_page`
(`id`, `application_id`, `page_name`, `page_code`, `link_path`, `create_time`, `update_time`)
VALUES
    (517,208,'机构管理界面','organ','/organ','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (518,208,'用户管理界面','user','/user','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (519,208,'角色管理界面','role','/role','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (520,208,'应用配置界面','application','/application','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (521,208,'资源配置界面','resource_settings','/resource_settings','2026-02-01 01:12:28','2026-05-05 13:08:53'),
    (522,208,'资源菜单界面','resource_menu','/resource_menu','2026-02-01 01:12:28','2026-05-05 13:08:53'),
    (523,208,'资源页面界面','resource_page','/resource_page','2026-02-01 01:12:28','2026-05-05 13:08:53'),
    (525,208,'功能权限界面','control_unit','/control_unit','2026-02-01 01:12:28','2026-05-05 13:08:53'),
    (526,208,'业务能力界面','control_domain','/control_domain','2026-02-01 01:12:28','2026-05-05 13:08:53'),
    (527,208,'授权记录界面','application_authorization','/application_authorization','2026-02-01 01:12:28','2026-05-05 13:08:53'),
    (528,208,'服务注册界面','service_registry','/service_registry','2026-02-01 01:12:28','2026-05-05 13:08:53'),
    (529,208,'服务接口界面','resource_api','/resource_api','2026-02-01 01:12:28','2026-05-05 13:08:53'),
    (530,208,'账号管理界面','passport','/passport','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (531,208,'登陆日志界面','login_token','/login_token','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (532,208,'审计事件界面','audit_event','/audit_event','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (533,209,'序列号段调控','g2rain_raindrop','/g2rain_raindrop','2026-04-11 07:23:17','2026-04-11 07:23:17'),
    (535,209,'字典用途设置','dictionary_usage','/dictionary_usage','2026-04-11 07:22:01','2026-04-11 07:22:01'),
    (536,209,'地区语言设置','locale_setting','/locale_setting','2026-04-11 07:23:02','2026-04-11 07:23:02'),
    (537,209,'多语言文案库','i18n_message','/i18n_message','2026-04-11 07:22:16','2026-04-11 07:22:31');

-- 应用页面元素
INSERT INTO `resource_page_element`
(`id`, `application_id`, `page_code`, `page_element_name`, `page_element_code`, `create_time`, `update_time`)
VALUES
    (538,208,'organ','新增按钮','organ:add','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (539,208,'organ','修改按钮','organ:edit','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (550,208,'organ','调整归属','organ:reassign','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (551,208,'organ','修改状态','organ:status_update','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (552,208,'organ','删除按钮','organ:delete','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (553,208,'user','新增按钮','user:add','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (555,208,'user','修改按钮','user:edit','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (556,208,'user','删除按钮','user:delete','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (557,208,'role','新增按钮','role:add','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (558,208,'role','修改按钮','role:edit','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (559,208,'role','分配用户','role:users_assign','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (560,208,'role','分配权限','role:control_utils_assign','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (561,208,'role','删除按钮','role:delete','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (562,208,'application','新增按钮','application:add','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (563,208,'application','修改按钮','application:edit','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (565,208,'application','关联应用','application:integrate','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (566,208,'application','公钥配置','application:public_key_config','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (567,208,'application','修改状态','application:status_update','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (568,208,'application','删除按钮','application:delete','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (569,208,'resource_settings','导入按钮','resource_settings:upload','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (570,208,'resource_menu','新增按钮','resource_menu:add','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (571,208,'resource_menu','修改按钮','resource_menu:edit','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (572,208,'resource_menu','删除按钮','resource_menu:delete','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (573,208,'resource_page','新增按钮','resource_page:add','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (575,208,'resource_page','修改按钮','resource_page:edit','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (576,208,'resource_page','页面元素','resource_page:page_element_mgmt','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (577,208,'resource_page','删除按钮','resource_page:delete','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (578,208,'control_unit','新增按钮','control_unit:add','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (579,208,'control_unit','修改按钮','control_unit:edit','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (580,208,'control_unit','配置资源','control_unit:resources_config','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (581,208,'control_unit','修改状态','control_unit:status_update','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (582,208,'control_unit','删除按钮','control_unit:delete','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (583,208,'control_domain','新增按钮','control_domain:add','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (585,208,'control_domain','修改按钮','control_domain:edit','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (586,208,'control_domain','关联权限','control_domain:control_utils_associate','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (587,208,'control_domain','开通功能','control_domain:features_activate','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (588,208,'control_domain','删除按钮','control_domain:delete','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (589,208,'application_authorization','修改状态','application_authorization:status_update','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (590,208,'application_authorization','同步能力','application_authorization:control_utils_sync','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (591,208,'service_registry','新增按钮','service_registry:add','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (592,208,'service_registry','修改按钮','service_registry:edit','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (593,208,'service_registry','删除按钮','service_registry:delete','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (595,208,'resource_api','新增按钮','resource_api:add','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (596,208,'resource_api','修改按钮','resource_api:edit','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (597,208,'resource_api','导入按钮','resource_api:import','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (598,208,'resource_api','删除按钮','resource_api:delete','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (599,208,'passport','新增按钮','passport:add','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (600,208,'passport','修改按钮','passport:edit','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (601,208,'passport','修改状态','passport:status_update','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (602,208,'passport','删除按钮','passport:delete','2026-02-01 01:12:28','2026-02-01 01:12:28'),
    (603,209,'g2rain_raindrop','新增按钮','g2rain_raindrop:add','2026-04-11 14:20:20','2026-04-11 14:20:20'),
    (605,209,'g2rain_raindrop','编辑按钮','g2rain_raindrop:edit','2026-04-11 14:20:20','2026-04-11 14:20:20'),
    (606,209,'g2rain_raindrop','删除按钮','g2rain_raindrop:delete','2026-04-11 14:20:20','2026-04-11 14:20:20'),
    (607,209,'dictionary_usage','新增按钮','dictionary_usage:add','2026-04-11 14:20:20','2026-04-11 14:20:20'),
    (608,209,'dictionary_usage','修改按钮','dictionary_usage:edit','2026-04-11 14:20:20','2026-04-11 14:20:20'),
    (609,209,'dictionary_usage','删除按钮','dictionary_usage:delete','2026-04-11 14:20:20','2026-04-11 14:20:20'),
    (610,209,'dictionary_usage','打开明细','dictionary_usage:items','2026-04-12 13:58:31','2026-04-12 14:12:10'),
    (611,209,'dictionary_usage','新增字典','dictionary_item:add','2026-04-12 14:07:40','2026-04-12 14:11:45'),
    (612,209,'dictionary_usage','删除字典','dictionary_item:delete','2026-04-12 14:07:40','2026-04-12 14:11:55'),
    (613,209,'dictionary_usage','修改字典','dictionary_item:edit','2026-04-12 14:07:40','2026-04-12 14:12:01'),
    (615,209,'locale_setting','新增按钮','locale_setting:add','2026-04-11 14:20:20','2026-04-11 14:20:20'),
    (616,209,'locale_setting','修改按钮','locale_setting:edit','2026-04-11 14:20:20','2026-04-11 14:20:20'),
    (617,209,'locale_setting','删除按钮','locale_setting:delete','2026-04-11 14:20:20','2026-04-11 14:20:20'),
    (618,209,'i18n_message','新增按钮','i18n_message:add','2026-04-11 14:20:20','2026-04-11 14:20:20'),
    (619,209,'i18n_message','修改按钮','i18n_message:edit','2026-04-11 14:20:20','2026-04-11 14:20:20'),
    (620,209,'i18n_message','删除按钮','i18n_message:delete','2026-04-11 14:20:20','2026-04-11 14:20:20');

-- 控制单元资源关联
INSERT INTO `control_unit_resource_relation`
(`id`, `control_unit_id`, `resource_id`, `resource_type`, `status`, `create_time`, `update_time`)
VALUES
    (621, 213, 390, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (622, 213, 391, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (623, 213, 392, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (625, 213, 393, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (626, 213, 395, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (627, 213, 396, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (628, 213, 397, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (629, 213, 398, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (630, 213, 399, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (631, 213, 500, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (632, 213, 501, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (633, 213, 502, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (635, 213, 503, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (636, 213, 505, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (637, 213, 506, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (638, 213, 507, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (639, 213, 508, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (650, 213, 509, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (651, 213, 510, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (652, 215, 390, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (653, 215, 391, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (655, 215, 392, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (656, 215, 393, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (657, 215, 395, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (658, 215, 396, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (659, 215, 502, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (660, 216, 511, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (661, 216, 512, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (662, 216, 513, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (663, 216, 515, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (665, 216, 516, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (666, 213, 517, 'PAGE', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (667, 213, 518, 'PAGE', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (668, 213, 519, 'PAGE', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (669, 213, 520, 'PAGE', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (670, 213, 521, 'PAGE', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (671, 213, 522, 'PAGE', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (672, 213, 523, 'PAGE', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (673, 213, 525, 'PAGE', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (675, 213, 526, 'PAGE', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (676, 213, 527, 'PAGE', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (677, 213, 528, 'PAGE', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (678, 213, 529, 'PAGE', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (679, 213, 530, 'PAGE', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (680, 213, 531, 'PAGE', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (681, 213, 532, 'PAGE', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (682, 215, 517, 'PAGE', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (683, 215, 518, 'PAGE', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (685, 215, 519, 'PAGE', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (686, 215, 520, 'PAGE', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (687, 215, 527, 'PAGE', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (688, 216, 533, 'PAGE', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (689, 216, 535, 'PAGE', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (690, 216, 536, 'PAGE', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (691, 216, 537, 'PAGE', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (692, 213, 538, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (693, 213, 539, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (695, 213, 550, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (696, 213, 551, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (697, 213, 552, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (698, 213, 553, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (699, 213, 555, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (700, 213, 556, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (701, 213, 557, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (702, 213, 558, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (703, 213, 559, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (705, 213, 560, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (706, 213, 561, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (707, 213, 562, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (708, 213, 563, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (709, 213, 565, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (710, 213, 566, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (711, 213, 567, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (712, 213, 568, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (713, 213, 569, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (715, 213, 570, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (716, 213, 571, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (717, 213, 572, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (718, 213, 573, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (719, 213, 575, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (720, 213, 576, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (721, 213, 577, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (722, 213, 578, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (723, 213, 579, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (725, 213, 580, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (726, 213, 581, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (727, 213, 582, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (728, 213, 583, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (729, 213, 585, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (730, 213, 586, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (731, 213, 587, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (732, 213, 588, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (733, 213, 589, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (735, 213, 590, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (736, 213, 591, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (737, 213, 592, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (738, 213, 593, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (739, 213, 595, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (750, 213, 596, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (751, 213, 597, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (752, 213, 598, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (753, 213, 599, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (755, 213, 600, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (756, 213, 601, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (757, 213, 602, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (758, 215, 538, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (759, 215, 539, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (760, 215, 550, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (761, 215, 551, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (762, 215, 552, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (763, 215, 553, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (765, 215, 555, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (766, 215, 556, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (767, 215, 557, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (768, 215, 558, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (769, 215, 559, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (770, 215, 560, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (771, 215, 561, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (772, 215, 590, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (773, 216, 603, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (775, 216, 605, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (776, 216, 606, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (777, 216, 607, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (778, 216, 608, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (779, 216, 609, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (780, 216, 610, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (781, 216, 611, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (782, 216, 612, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (783, 216, 613, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (785, 216, 615, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (786, 216, 616, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (787, 216, 617, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (788, 216, 618, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (789, 216, 619, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (790, 216, 620, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (791, 212, 236, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (792, 212, 238, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (793, 212, 268, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (795, 212, 279, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (796, 212, 289, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (797, 212, 290, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (798, 212, 359, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (799, 212, 360, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (800, 212, 361, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (801, 213, 238, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (802, 213, 268, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (803, 213, 276, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (805, 213, 277, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (806, 213, 278, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (807, 213, 279, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (808, 213, 280, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (809, 213, 281, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (810, 213, 282, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (811, 213, 283, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (812, 213, 285, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (813, 213, 286, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (815, 213, 287, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (816, 213, 288, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (817, 213, 289, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (818, 213, 290, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (819, 213, 291, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (820, 213, 292, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (821, 213, 293, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (822, 213, 295, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (823, 213, 296, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (825, 213, 297, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (826, 213, 298, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (827, 213, 299, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (828, 213, 300, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (829, 213, 301, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (830, 213, 302, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (831, 213, 303, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (832, 213, 305, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (833, 213, 306, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (835, 213, 307, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (836, 213, 308, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (837, 213, 309, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (838, 213, 310, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (839, 213, 311, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (850, 213, 312, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (851, 213, 313, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (852, 213, 315, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (853, 213, 316, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (855, 213, 317, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (856, 213, 318, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (857, 213, 319, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (858, 213, 320, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (859, 213, 321, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (860, 213, 322, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (861, 213, 323, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (862, 213, 325, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (863, 213, 326, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (865, 213, 327, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (866, 213, 328, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (867, 213, 329, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (868, 213, 330, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (869, 213, 331, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (870, 213, 332, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (871, 213, 333, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (872, 213, 335, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (873, 213, 336, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (875, 213, 337, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (876, 213, 338, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (877, 213, 339, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (878, 213, 350, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (879, 213, 351, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (880, 213, 352, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (881, 213, 353, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (882, 213, 355, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (883, 213, 356, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (885, 213, 357, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (886, 213, 358, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (887, 213, 359, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (888, 213, 360, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (889, 213, 361, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (890, 213, 362, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (891, 213, 363, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (892, 213, 365, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (893, 213, 366, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (895, 213, 367, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (896, 213, 368, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (897, 213, 369, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (898, 213, 370, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (899, 213, 371, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (900, 213, 372, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (901, 213, 373, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (902, 213, 375, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (903, 213, 376, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (905, 213, 377, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (906, 213, 378, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (907, 213, 379, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (908, 213, 380, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (909, 213, 381, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (910, 213, 382, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (911, 213, 383, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (912, 213, 385, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (913, 213, 386, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (915, 213, 387, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (916, 213, 388, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (917, 213, 389, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (918, 215, 238, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (919, 215, 268, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (920, 215, 276, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (921, 215, 277, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (922, 215, 278, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (923, 215, 281, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (925, 215, 282, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (926, 215, 291, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (927, 215, 292, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (928, 215, 293, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (929, 215, 308, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (930, 215, 309, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (931, 215, 310, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (932, 215, 311, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (933, 215, 312, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (935, 215, 313, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (936, 215, 317, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (937, 215, 318, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (938, 215, 319, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (939, 215, 320, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (950, 215, 321, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (951, 215, 333, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (952, 215, 335, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (953, 215, 336, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (955, 215, 337, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (956, 215, 360, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (957, 215, 361, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (958, 215, 367, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (959, 215, 368, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (960, 215, 370, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (961, 215, 371, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (962, 215, 372, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (963, 215, 373, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (965, 215, 376, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (966, 215, 382, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (967, 216, 360, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (968, 216, 361, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (969, 216, 223, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (970, 216, 225, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (971, 216, 226, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (972, 216, 227, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (973, 216, 228, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (975, 216, 231, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (976, 216, 232, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (977, 216, 233, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (978, 216, 235, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (979, 216, 236, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (980, 216, 237, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (981, 216, 238, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (982, 216, 239, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (983, 216, 260, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (985, 216, 261, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (986, 216, 262, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (987, 216, 263, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (988, 216, 265, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (989, 216, 266, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (990, 216, 267, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (991, 216, 268, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (992, 216, 270, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (993, 216, 271, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (995, 216, 272, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (996, 216, 273, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28'),
    (997, 216, 275, 'API_ENDPOINT', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28');
