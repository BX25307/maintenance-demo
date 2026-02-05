-- ============================================================
-- 1. 初始化用户表 (sys_user)
-- 包括：1个老板，1个管理员，2个维修工，1个普通用户
-- ============================================================
INSERT INTO "public"."sys_user"
("user_id", "username", "password", "real_name", "phone", "avatar", "role_key", "balance", "tenant_id")
VALUES
    ('boss_01',   'boss',   '123456', '王老板', '13800138000', '', 'BOSS',      10000.00, 'tenant_001'),
    ('admin_01',  'admin',  '123456', '李管理', '13900139000', '', 'ADMIN',     0.00,     'tenant_001'),
    ('repair_01', 'jack',   '123456', '张师傅', '13700137001', '', 'REPAIRMAN', 500.00,   'tenant_001'),
    ('repair_02', 'tom',    '123456', '刘师傅', '13700137002', '', 'REPAIRMAN', 200.00,   'tenant_001'),
    ('user_01',   'alice',  '123456', '陈员工', '13600136000', '', 'USER',      0.00,     'tenant_001');

-- ============================================================
-- 2. 初始化维修工扩展信息 (repairman_info)
-- 关联上面的 repair_01 (空闲) 和 repair_02 (忙碌)
-- ============================================================
INSERT INTO "public"."repairman_info"
("repairman_id", "user_id", "work_status", "province_name", "city_name", "tenant_id")
VALUES
    ('rep_profile_01', 'repair_01', 0, '广东省', '深圳市', 'tenant_001'), -- 张师傅空闲
    ('rep_profile_02', 'repair_02', 1, '广东省', '深圳市', 'tenant_001'); -- 刘师傅忙碌

-- ============================================================
-- 3. 初始化设备信息 (device_info)
-- 设备归属 boss_01
-- ============================================================
INSERT INTO "public"."device_info"
("device_id", "device_name", "device_type", "device_status", "sn", "province_name", "city_name", "user_id", "tenant_id", "platform_type")
VALUES
    ('dev_01', '1号楼中央空调', 'AIR_COND', 'HEALTHY', 'SN2023001', '广东省', '深圳市', 'boss_01', 'tenant_001', 'OWN'),
    ('dev_02', '2号会议室投影', 'PROJECTOR','ERROR',   'SN2023002', '广东省', '深圳市', 'boss_01', 'tenant_001', 'OWN');

-- ============================================================
-- 4. 初始化工单数据 (work_order) - 覆盖多种状态
-- ============================================================

-- 场景 A: 待接单 (PENDING)
-- user_01 报修了 dev_01，还没有人接
INSERT INTO "public"."work_order"
("order_id", "order_sn", "device_id", "device_name", "reporter_id", "owner_id", "repairman_id",
 "address_detail", "fault_desc", "order_status",
 "material_fee", "labor_fee", "total_amount", "tenant_id", "create_time")
VALUES
    ('wo_pending_01', 'WO20231027001', 'dev_01', '1号楼中央空调', 'user_01', 'boss_01', NULL,
     '科技园1号楼301室', '空调不制冷，有异响', 0,
     0.00, 0.00, 0.00, 'tenant_001', NOW());

-- 场景 B: 维修中 (DISPATCHED/REPAIRING)
-- user_01 报修 dev_02，已被 repair_02 接单 (对应上面维修工状态为忙碌)
INSERT INTO "public"."work_order"
("order_id", "order_sn", "device_id", "device_name", "reporter_id", "owner_id", "repairman_id",
 "address_detail", "fault_desc", "order_status", "dispatch_time",
 "tenant_id", "create_time")
VALUES
    ('wo_repairing_01', 'WO20231027002', 'dev_02', '2号会议室投影', 'user_01', 'boss_01', 'repair_02',
     '科技园2号楼205会议室', '投影仪无法开机，指示灯闪烁', 1, NOW(),
     'tenant_001', NOW() - INTERVAL '2 hour');

-- 场景 C: 待支付 (FINISHED)
-- repair_01 修好了，填了费用，等待老板支付
INSERT INTO "public"."work_order"
("order_id", "order_sn", "device_id", "device_name", "reporter_id", "owner_id", "repairman_id",
 "address_detail", "fault_desc", "repair_result", "order_status",
 "material_fee", "labor_fee", "total_amount", "platform_rate", "platform_income", "repairman_income",
 "finish_time", "tenant_id", "create_time")
VALUES
    ('wo_finished_01', 'WO20231026999', 'dev_01', '1号楼中央空调', 'user_01', 'boss_01', 'repair_01',
     '科技园1号楼301室', '滤网堵塞', '已清洗滤网，加雪种', 3,
     50.00, 150.00, 200.00, 0.10, 15.00, 185.00,
     NOW(), 'tenant_001', NOW() - INTERVAL '1 day');

-- 场景 D: 已完成 (COMPLETED) - 历史订单
-- 包含完整流程，用于测试报表和流水
INSERT INTO "public"."work_order"
("order_id", "order_sn", "device_id", "device_name", "reporter_id", "owner_id", "repairman_id",
 "address_detail", "fault_desc", "repair_result", "order_status",
 "material_fee", "labor_fee", "total_amount", "platform_rate", "platform_income", "repairman_income",
 "pay_time", "finish_time", "tenant_id", "create_time")
VALUES
    ('wo_completed_01', 'WO20231025888', 'dev_02', '2号会议室投影', 'user_01', 'boss_01', 'repair_01',
     '科技园2号楼205会议室', '灯泡坏了', '更换原厂灯泡', 5,
     200.00, 100.00, 300.00, 0.10, 10.00, 290.00,
     NOW() - INTERVAL '2 day', NOW() - INTERVAL '2 day', 'tenant_001', NOW() - INTERVAL '3 day');

-- ============================================================
-- 5. 初始化资金流水 (capital_flow)
-- 对应上面的 wo_completed_01 订单
-- ============================================================
INSERT INTO "public"."capital_flow"
("trade_no", "order_id", "account_id", "flow_type", "amount", "balance_snapshot", "remark", "tenant_id", "create_time")
VALUES
-- 老板支出 300
('TRX_001_PAY', 'wo_completed_01', 'boss_01', 'PAY', -300.00, 9700.00, '支付工单WO20231025888', 'tenant_001', NOW() - INTERVAL '2 day'),
-- 维修工收入 290 (300 - 10块抽成)
('TRX_001_INC', 'wo_completed_01', 'repair_01', 'INCOME', 290.00, 790.00, '工单WO20231025888收入', 'tenant_001', NOW() - INTERVAL '2 day');

-- ============================================================
-- 6. 初始化日志 (work_order_log)
-- 简单给 pending 单加个日志
-- ============================================================
INSERT INTO "public"."work_order_log"
("order_id", "operator_id", "operator_role", "action_type", "action_desc", "create_time")
VALUES
    ('wo_pending_01', 'user_01', 'USER', 'CREATE', '用户提交报修', NOW());