-- control_unit.session_type 生产增量迁移
-- 顺序：可空列 → 函数索引 → 存量分类回填 → 校验 → 非空约束
-- 禁止无条件 DEFAULT 'USER'，避免 Passport/匿名/MEMBER 被误纳入员工权限。
-- 要求：MySQL 8.0.13+

-- 1. 增加可空字段
ALTER TABLE control_unit
    ADD COLUMN session_type VARCHAR(32) NULL
        COMMENT '会话主体类型[SessionType: USER|MEMBER|PASSPORT|ANONYMOUS]'
        AFTER application_id;

-- 2. 函数索引（仅有效记录参与前缀匹配）
CREATE INDEX idx_control_unit_session_status
    ON control_unit (session_type, status, (IF(delete_flag = 0, 0, NULL)));

-- 3. 存量分类检查（人工回填前执行，用于生成清单）
-- 3.1 存在有效角色关联的控制单元（候选 USER）
SELECT cu.id, cu.control_unit_name, cu.control_unit_scope, cu.landing, cu.status,
       COUNT(rcur.id) AS activated_role_links
FROM control_unit cu
LEFT JOIN role_control_unit_relation rcur
  ON rcur.control_unit_id = cu.id
 AND rcur.delete_flag = 0
 AND rcur.status = 'ACTIVATED'
WHERE cu.delete_flag = 0
GROUP BY cu.id, cu.control_unit_name, cu.control_unit_scope, cu.landing, cu.status;

-- 3.2 当前 Passport 默认权限来源（候选 PASSPORT）
SELECT cu.id, cu.control_unit_name, cu.application_id
FROM control_unit cu
WHERE cu.delete_flag = 0
  AND cu.landing = 1
  AND cu.control_unit_scope = 'PERPETUAL'
  AND cu.status = 'PUBLISHED';

-- 3.3 回填示例（按环境清单手工调整后执行，勿直接套用到生产）
-- UPDATE control_unit SET session_type = 'PASSPORT' WHERE id = 14 AND delete_flag = 0;
-- UPDATE control_unit SET session_type = 'USER' WHERE id IN (15, 16, 17, 18, 19) AND delete_flag = 0;

-- 4. 回填完成后校验：有效记录不得为空或非法值
SELECT id, control_unit_name, session_type
FROM control_unit
WHERE delete_flag = 0
  AND (session_type IS NULL
       OR session_type NOT IN ('USER', 'MEMBER', 'PASSPORT', 'ANONYMOUS'));
-- 结果必须为空

-- 5. 收紧非空约束（仅在步骤 4 结果为空后执行）
-- ALTER TABLE control_unit
--     MODIFY COLUMN session_type VARCHAR(32) NOT NULL
--         COMMENT '会话主体类型[SessionType: USER|MEMBER|PASSPORT|ANONYMOUS]';
