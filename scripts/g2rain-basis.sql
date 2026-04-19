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
    `create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT                                      '创建时间',
    `update_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT          '更新时间',
    `version` INT NOT NULL DEFAULT 0 COMMENT                                                            '记录版本',
    `delete_flag` TINYINT NOT NULL DEFAULT 0 COMMENT                                                    '删除标识[0:未删除, 1:已删除]',
    PRIMARY KEY (`id`),
    INDEX `idx_username` (`username`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=                             '账号表';

-- =============================================
-- 2. 用户表 (user)
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
-- 3. 机构表 (organ)
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
-- 4. 机构路径关系表 (organ_closure)
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
-- 5. 应用资源菜单表 (resource_menu)
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
-- 6. 应用资源页面表 (resource_page)
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
-- 7. 应用资源页面元素表 (resource_page_element)
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
-- 8. 接口地址表 (resource_api_endpoint)
-- =============================================
DROP TABLE IF EXISTS `api_endpoint`;

CREATE TABLE `api_endpoint` (
    `id` BIGINT NOT NULL COMMENT 													                    '接口标识',
    `api_name` VARCHAR(128) NOT NULL COMMENT 														    '接口名称',
    `api_url` VARCHAR(128) NOT NULL COMMENT 													        '接口路径',
    `request_method` VARCHAR(32) NOT NULL COMMENT   												    '请求方法',
    `api_tag` VARCHAR(128) NOT NULL COMMENT   												            '接口标签, 接口分类',
    `description` VARCHAR(512) DEFAULT NULL COMMENT   												    '业务说明',
    `create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT                                      '创建时间',
    `update_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT          '更新时间',
    `version` INT NOT NULL DEFAULT 0 COMMENT                                                            '记录版本',
    `delete_flag` TINYINT NOT NULL DEFAULT 0 COMMENT                                                    '删除标识[0:未删除, 1:已删除]',
    PRIMARY KEY (`id`),
    INDEX `uk_api_url_method` (`api_url`, `request_method`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=                             '接口地址表';

-- =============================================
-- 9. 应用资源接口地址表 (resource_api_endpoint)
-- =============================================
DROP TABLE IF EXISTS `resource_api_endpoint`;

CREATE TABLE `resource_api_endpoint` (
    `id` BIGINT NOT NULL COMMENT 													                    '接口标识',
    `application_id` BIGINT NOT NULL COMMENT 													        '应用标识',
    `api_endpoint_id` BIGINT NOT NULL COMMENT 													        '接口地址标识',
    `create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT                                      '创建时间',
    `update_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT          '更新时间',
    `version` INT NOT NULL DEFAULT 0 COMMENT                                                            '记录版本',
    `delete_flag` TINYINT NOT NULL DEFAULT 0 COMMENT                                                    '删除标识[0:未删除, 1:已删除]',
    PRIMARY KEY (`id`),
    INDEX `idx_app_api_del` (`application_id`, `api_endpoint_id`, `delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=                             '应用资源接口地址表';

-- =============================================
-- 10. 控制单元表 (control_unit)
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
-- 11. 控制单元资源关联表 (control_unit_resource_relation)
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
-- 12. 角色表 (role)
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
-- 13. 用户角色关联表 (user_role_relation)
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
-- 14. 角色控制单元关联表 (role_control_unit_relation)
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
-- 15. 控制域表 (control_domain)
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
-- 16. 控制域控制单元关联表 (control_domain_control_unit_relation)
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
-- 17. 应用表 (application)
-- =============================================
DROP TABLE IF EXISTS `application`;

CREATE TABLE `application` (
    `id` BIGINT NOT NULL COMMENT 													                    '应用标识',
    `organ_id` BIGINT NOT NULL COMMENT 												                    '机构标识',
    `application_name` VARCHAR(128) NOT NULL COMMENT                                                    '应用名称',
    `application_code` VARCHAR(64) DEFAULT NULL COMMENT                                                 '应用编码',
    `can_integrate` TINYINT NOT NULL DEFAULT 0 COMMENT                                                  '是否具备集成功能[0:否, 1:是]',
    `landing` TINYINT NOT NULL DEFAULT 0 COMMENT                                                        '默认数据[0:否, 1:是]',
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
-- 18. 应用归类关系表 (application_suite)
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
-- 19. 应用授权记录表 (application_authorization)
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
-- 20. 登录信息表 (login_token)
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
    `lastest_refresh_time` TIMESTAMP NULL DEFAULT NULL COMMENT                                          '最近一次刷新时间',
    `create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT                                      '创建时间',
    `update_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT          '更新时间',
    `version` INT NOT NULL DEFAULT 0 COMMENT                                                            '记录版本',
    `delete_flag` TINYINT NOT NULL DEFAULT 0 COMMENT                                                    '删除标识[0:未删除, 1:已删除]',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4 COLLATE=utf8mb4_unicode_ci COMMENT=								'登录信息表, 记录了当前登录状态的相关信息';

-- 账号
INSERT INTO `passport`
(`id`, `username`, `password`, `real_name`, `sex`, `birthday`, `id_no`, `mobile`, `email`, `status`, `create_time`, `update_time`, `version`, `delete_flag`)
VALUES
    (1, 'admin', 'PBKDF2WithHmacSHA256$65536$YSskABrEQZiuRcM3GMl6gQ==$Hl9gA9UnYmS1BoY3Ov3XY2qYQpUKF1Sl0QneYZ5zc7k=', '平台超管', 'MALE', NULL, NULL, NULL, NULL, 'NORMAL', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0);

-- 机构
INSERT INTO `organ`
(`id`, `organ_name`, `organ_type`, `status`, `admin`, `create_time`, `update_time`, `version`, `delete_flag`)
VALUES
    (2, '平台机构', 'COMPANY', 'ACTIVE', 1, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0);

-- 机构路径关系
INSERT INTO `organ_closure`
(`id`, `ancestor_id`, `descendant_id`, `descendant_type`, `relation_type`, `path_count`, `create_time`, `update_time`, `version`, `delete_flag`)
VALUES
    (3, 2, 2, 'COMPANY', 'SELF_ASSOCIATION', 1, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0);

-- 角色
INSERT INTO `role`
(`id`, `organ_id`, `role_name`, `role_type`, `create_time`, `update_time`, `version`, `delete_flag`)
VALUES
    (5, 2, '超管角色', 'ADMIN', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0);

-- 用户
INSERT INTO `user`
(`id`, `passport_id`, `organ_id`, `email`, `mobile`, `real_name`, `admin`, `create_time`, `update_time`, `version`, `delete_flag`)
VALUES
    (6, 1, 2, NULL, NULL, '平台管理员', 1, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0);

-- 用户角色关联
INSERT INTO `user_role_relation`
(`id`, `user_id`, `role_id`, `create_time`, `update_time`, `version`, `delete_flag`)
VALUES
    (7, 6, 5, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0);

-- 应用
INSERT INTO `application`
(`id`, `organ_id`, `application_name`, `application_code`, `can_integrate`, `landing`, `application_type`, `public_key_algorithm`, `public_key_format`, `public_key`, `access_token_expires_in`, `refresh_token_expires_in`, `endpoint_url`, `context_path`, `status`, `description`, `create_time`, `update_time`, `version`, `delete_flag`)
VALUES
    (8, 2, '综合管理平台', 'g2rain-main-shell',  1, 1, 'SUPPORT', 'EC', 'PEM', '-----BEGIN PUBLIC KEY-----\nMFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEXmlg1y2fUD9KJj4WB6DrRZU+iVwA\nyzz60AxRoFb2yDnBvYiiK9JR1p5QUw2jkR9RPvkZez1Kx2BqxwyOoWRV/A==\n-----END PUBLIC KEY-----\n', 3600, 86400, 'http://43.138.13.145:8080', '/main/',    'PUBLISHED', '管理平台入口', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (9, 2, '支撑管理平台', 'g2rain-manager-app', 0, 1, 'SUPPORT', 'EC', 'PEM', '-----BEGIN PUBLIC KEY-----\nMFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEXGDOn5B+GFE42lcMd5u47r6na9iE\nH1AzxAU49KiWBz17su0M1vPZ+s57bvMlYvbcPG2nfWcJvJzRuKUakrUhsA==\n-----END PUBLIC KEY-----\n', 3600, 86400, 'http://43.138.13.145:8080', '/manager/', 'PUBLISHED', '系统支撑功能', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0);

-- 应用归类关系
INSERT INTO `application_suite`
(`id`, `application_id`, `master_application_id`, `create_time`, `update_time`, `version`, `delete_flag`)
VALUES
    (10, 9, 8, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0);

-- 控制单元
INSERT INTO `control_unit`
(`id`, `application_id`, `control_unit_name`, `control_unit_scope`, `landing`, `status`, `description`, `create_time`, `update_time`, `version`, `delete_flag`)
VALUES
    (11, 8, '盘古',   'PERPETUAL', 1, 'PUBLISHED', '平台准入基础能力', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (12, 9, '燧人氏', 'OPERATION', 1, 'PUBLISHED', '核心运营支撑组件', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (15, 9, '女娲',   'CUSTOMER',  1, 'PUBLISHED', '租户空间构建逻辑', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0);

-- 角色控制单元关联
INSERT INTO `role_control_unit_relation`
(`id`, `role_id`, `control_unit_id`, `application_authorization_id`, `status`, `create_time`, `update_time`, `version`, `delete_flag`)
VALUES
    (16, 5, 12, NULL, 'ACTIVATED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0);

-- 应用资源菜单
INSERT INTO `resource_menu`
(`id`, `parent_id`, `application_id`, `menu_name`, `menu_code`, `link_path`, `icon`, `menu_sort_order`, `create_time`, `update_time`, `version`, `delete_flag`)
VALUES
    (17, NULL, 8, '账号开通', 'tenant-provision-menu',          '', 							'', 1, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (18, 17,   8, '开通设置', 'provision-settings-menu',	     '/tenant-provision', 			'', 1, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (19, NULL, 9, '系统管理', 'system-management-menu',         '', 							'', 2, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (20, 19,   9, '机构管理', 'organ-management-menu', 		 '/organ',       				'', 1, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (21, 19,   9, '用户管理', 'user-management-menu',           '/user',        				'', 2, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (22, 19,   9, '角色管理', 'role-management-menu', 		     '/role',        				'', 3, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (23, NULL, 9, '应用管理', 'application-management-menu',    '', 							'', 3, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (25, 23,   9, '应用配置', 'application-config-menu', 		 '/application', 				'', 1, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (26, 23,   9, '资源配置', 'resource-settings-menu', 		 '/resource-settings', 		'', 2, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (27, 23,   9, '资源菜单', 'resource-menu-menu', 			 '/resource-menu', 			'', 3, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (28, 23,   9, '资源页面', 'resource-page-menu', 			 '/resource-page', 		   	'', 5, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (29, 23,   9, '资源接口', 'resource-api-endpoint-menu', 	 '/resource-api-endpoint',		'', 6, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (30, 23,   9, '功能权限', 'control-unit-menu', 			 '/control-unit', 				'', 7, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (31, 23,   9, '业务能力', 'control-domain-menu', 			 '/control-domain', 		   	'', 8, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (32, 23,   9, '授权记录', 'application-authorization-menu', '/application-authorization', '', 9, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (33, NULL, 9, '平台配置', 'platform-settings-menu',   		 '', 						    '', 5, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (35, 33,   9, '后端接口', 'backend-api-endpoint-menu', 	 '/api-endpoint', 			    '', 1, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (36, NULL, 9, '平台运营', 'platform-operations-menu', 		 '', 							'', 6, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (37, 36,   9, '账号管理', 'passport-management-menu', 		 '/passport', 					'', 1, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0);

-- 应用资源页面
INSERT INTO `resource_page`
(`id`, `application_id`, `page_name`, `page_code`, `link_path`, `create_time`, `update_time`, `version`, `delete_flag`)
VALUES
    (38, 8, '账号开通界面', 'tenant-provision',	        '/tenant-provision', 			'2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (39, 9, '机构管理界面', 'organ',			            '/organ', 						'2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (50, 9, '用户管理界面', 'user',			            '/user', 						'2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (51, 9, '角色管理界面', 'role',			            '/role', 						'2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (52, 9, '应用配置界面', 'application',		        '/application', 				'2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (53, 9, '资源配置界面', 'resource-settings',			'/resource-settings', 			'2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (55, 9, '资源菜单界面', 'resource-menu',				'/resource-menu', 				'2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (56, 9, '资源页面界面', 'resource-page',				'/resource-page',				'2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (57, 9, '资源接口界面', 'resource-api-endpoint',		'/resource-api-endpoint',		'2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (58, 9, '功能权限界面', 'control-unit',				'/control-unit',				'2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (59, 9, '业务能力界面', 'control-domain',			    '/control-domain',				'2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (60, 9, '授权记录界面', 'application-authorization', '/application-authorization',	'2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (61, 9, '后端接口界面', 'api-endpoint',		        '/api-endpoint', 				'2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (62, 9, '账号管理界面', 'passport',		            '/passport', 					'2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0);

-- 应用资源页面元素
INSERT INTO `resource_page_element`
(`id`, `application_id`, `page_code`, `page_element_name`, `page_element_code`, `create_time`, `update_time`, `version`, `delete_flag`)
VALUES
    (63, 8, 'tenant-provision',	        '保存按钮', 'tenant-provision:save',            '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (65, 9, 'organ',		                '新增按钮', 'organ:add', 						  '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (66, 9, 'organ',		                '修改按钮', 'organ:edit', 					  '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (67, 9, 'organ',		                '调整归属', 'organ:reassign',					  '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (68, 9, 'organ',		                '修改状态', 'organ:status-update',			  '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (69, 9, 'organ',		                '删除按钮', 'organ:delete', 					  '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (70, 9, 'user',			            '新增按钮', 'user:add', 						  '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (71, 9, 'user',			            '修改按钮', 'user:edit', 						  '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (72, 9, 'user',			            '删除按钮', 'user:delete', 					  '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (73, 9, 'role',			            '新增按钮', 'role:add', 						  '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (75, 9, 'role',			            '修改按钮', 'role:edit', 						  '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (76, 9, 'role',			            '分配用户', 'role:users-assign', 				  '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (77, 9, 'role',			            '分配权限', 'role:control-utils-assign',		  '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (78, 9, 'role',			            '删除按钮', 'role:delete', 					  '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (79, 9, 'application',		        '新增按钮', 'application:add', 				  '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (80, 9, 'application',		        '修改按钮', 'application:edit', 				  '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (81, 9, 'application',		        '关联应用', 'application:integrate', 			  '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (82, 9, 'application',		        '公钥配置', 'application:public-key-config', 	  '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (83, 9, 'application',		        '修改状态', 'application:status:update', 		  '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (85, 9, 'application',		        '删除按钮', 'application:delete', 			  '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (86, 9, 'resource-settings',			'导入按钮', 'resource-settings:upload', 		  '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (87, 9, 'resource-menu',				'新增按钮', 'resource-menu:add',				  '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (88, 9, 'resource-menu',				'修改按钮', 'resource-menu:edit',				  '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (89, 9, 'resource-menu',				'删除按钮', 'resource-menu:delete',			  '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (90, 9, 'resource-page',		        '新增按钮', 'resource-page:add',		          '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (91, 9, 'resource-page',		        '修改按钮', 'resource-page:edit',		          '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (92, 9, 'resource-page',				'页面元素', 'resource-page:page-element-mgmt',  '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (93, 9, 'resource-page',		        '删除按钮', 'resource-page:delete',	          '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (95, 9, 'resource-api-endpoint',		'新增按钮', 'resource-api-endpoint:add',		  '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (96, 9, 'resource-api-endpoint',	    '修改按钮', 'resource-api-endpoint:edit',		  '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (97, 9, 'resource-api-endpoint',	    '删除按钮', 'resource-api-endpoint:delete',	  '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (98, 9, 'control-unit',				'新增按钮', 'control-unit:add',				  '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (99, 9, 'control-unit',				'修改按钮', 'control-unit:edit',				  '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (100,9, 'control-unit',				'配置资源', 'control-unit:resources-config',    '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (101,9, 'control-unit',		        '修改状态', 'control-unit:status-update', 	  '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (102,9, 'control-unit',				'删除按钮', 'control-unit:delete',			  '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (103,9, 'control-domain',			    '新增按钮', 'control-domain:add',		          '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (105,9, 'control-domain',			    '修改按钮', 'control-domain:edit',		      '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (106,9, 'control-domain',			    '关联权限', 'control-domain:control-utils-associate',	      '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (107,9, 'control-domain',			    '开通功能', 'control-domain:features-activate', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (108,9, 'control-domain',			    '删除按钮', 'control-domain:delete',	          '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (109,9, 'application-authorization',  '修改状态', 'application-authorization:status-update',      '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (110,9, 'application-authorization',  '同步能力', 'application-authorization:control-utils-sync', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (111,9, 'api-endpoint',		        '新增按钮', 'api-endpoint:add', 				  '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (112,9, 'api-endpoint',		        '修改按钮', 'api-endpoint:edit', 				  '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (113,9, 'api-endpoint',		        '导入按钮', 'api-endpoint:upload', 			  '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (115,9, 'api-endpoint',		        '删除按钮', 'api-endpoint:delete', 			  '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (116,9, 'passport',		            '新增按钮', 'passport:add', 					  '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (117,9, 'passport',		            '修改按钮', 'passport:edit', 					  '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (118,9, 'passport',		            '修改状态', 'passport:status-update', 		  '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (119,9, 'passport',		            '删除按钮', 'passport:delete', 				  '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0);

-- 后端接口
INSERT INTO `api_endpoint`
(`id`, `api_name`, `api_url`, `request_method`, `api_tag`, `description`, `create_time`, `update_time`, `version`, `delete_flag`)
VALUES
    (123, '字典新增', '/dict/save', 'POST', '字典', 'e312', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0);

-- 应用资源接口
INSERT INTO `resource_api_endpoint`
(`id`, `application_id`, `api_endpoint_id`, `create_time`, `update_time`, `version`, `delete_flag`)
VALUES
    (567, 8, 10028, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0);

-- 控制单元资源关联
INSERT INTO `control_unit_resource_relation`
(`id`, `control_unit_id`, `resource_id`, `resource_type`, `status`, `create_time`, `update_time`, `version`, `delete_flag`)
VALUES
    (120, 11, 17, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (121, 11, 18, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (122, 12, 19, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (123, 12, 20, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (125, 12, 21, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (126, 12, 22, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (127, 12, 23, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (128, 12, 25, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (129, 12, 26, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (130, 12, 27, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (131, 12, 28, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (132, 12, 29, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (133, 12, 30, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (135, 12, 31, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (136, 12, 32, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (137, 12, 33, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (138, 12, 35, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (139, 12, 36, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (150, 12, 37, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (151, 15, 19, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (152, 15, 20, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (153, 15, 21, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (155, 15, 22, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (156, 15, 23, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (157, 15, 25, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (158, 15, 32, 'MENU', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (159, 11, 38, 'PAGE', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (160, 12, 39, 'PAGE', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (161, 12, 50, 'PAGE', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (162, 12, 51, 'PAGE', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (163, 12, 52, 'PAGE', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (165, 12, 53, 'PAGE', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (166, 12, 55, 'PAGE', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (167, 12, 56, 'PAGE', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (168, 12, 57, 'PAGE', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (169, 12, 58, 'PAGE', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (170, 12, 59, 'PAGE', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (171, 12, 60, 'PAGE', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (172, 12, 61, 'PAGE', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (173, 12, 62, 'PAGE', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (175, 15, 39, 'PAGE', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (176, 15, 50, 'PAGE', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (177, 15, 51, 'PAGE', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (178, 15, 52, 'PAGE', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (179, 15, 60, 'PAGE', NULL, '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (180, 11, 63, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (181, 12, 65, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (182, 12, 66, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (183, 12, 67, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (185, 12, 68, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (186, 12, 69, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (187, 12, 70, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (188, 12, 71, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (189, 12, 72, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (190, 12, 73, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (191, 12, 75, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (192, 12, 76, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (193, 12, 77, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (195, 12, 78, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (196, 12, 79, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (197, 12, 80, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (198, 12, 81, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (199, 12, 82, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (200, 12, 83, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (201, 12, 85, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (202, 12, 86, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (203, 12, 87, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (205, 12, 88, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (206, 12, 89, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (207, 12, 90, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (208, 12, 91, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (209, 12, 92, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (210, 12, 93, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (211, 12, 95, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (212, 12, 96, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (213, 12, 97, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (215, 12, 98, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (216, 12, 99, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (217, 12, 100, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (218, 12, 101, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (219, 12, 102, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (220, 12, 103, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (221, 12, 105, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (222, 12, 106, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (223, 12, 107, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (225, 12, 108, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (226, 12, 109, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (227, 12, 110, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (228, 12, 111, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (229, 12, 112, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (230, 12, 113, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (231, 12, 115, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (232, 12, 116, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (233, 12, 117, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (235, 12, 118, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (236, 12, 119, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (237, 15, 65, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (238, 15, 66, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (239, 15, 67, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (260, 15, 68, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (261, 15, 69, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (262, 15, 70, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (263, 15, 71, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (265, 15, 72, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (266, 15, 73, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (267, 15, 75, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (268, 15, 76, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (269, 15, 77, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (270, 15, 78, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0),
    (271, 15, 110, 'PAGE_ELEMENT', 'ENABLED', '2026-02-01 09:12:28', '2026-02-01 09:12:28', 0, 0);
