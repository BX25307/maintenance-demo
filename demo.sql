DROP TABLE IF EXISTS "public"."device_info";
CREATE TABLE "public"."device_info" (
                                        "device_id" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
                                        "allocation_time" timestamp(6),
                                        "expire_time" timestamp(6),
                                        "binding_time" timestamp(6),
                                        "city_name" varchar(255) COLLATE "pg_catalog"."default",
                                        "city_code" varchar(255) COLLATE "pg_catalog"."default",
                                        "county_name" varchar(255) COLLATE "pg_catalog"."default",
                                        "county_code" varchar(255) COLLATE "pg_catalog"."default",
                                        "isp" varchar(20) COLLATE "pg_catalog"."default",
                                        "line_count" int4,
                                        "device_type" varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
                                        "device_name" varchar(255) COLLATE "pg_catalog"."default",
                                        "device_remark" varchar(255) COLLATE "pg_catalog"."default",
                                        "device_status" varchar(30) COLLATE "pg_catalog"."default" NOT NULL,
                                        "province_name" varchar(255) COLLATE "pg_catalog"."default",
                                        "province_code" varchar(255) COLLATE "pg_catalog"."default",
                                        "user_single_uplink_mbps" int4,
                                        "up_single_uplink_mbps" int4,
                                        "sn" varchar(255) COLLATE "pg_catalog"."default",
                                        "user_total_bandwidth_mbps" int4,
                                        "up_total_bandwidth_mbps" int4,
                                        "user_id" varchar(50) COLLATE "pg_catalog"."default",
                                        "device_rule_id" varchar(255) COLLATE "pg_catalog"."default",
                                        "platform_type" varchar(30) COLLATE "pg_catalog"."default" NOT NULL,
                                        "device_info_id" varchar(255) COLLATE "pg_catalog"."default",
                                        "income_date" date,
                                        "create_time" timestamp(6) DEFAULT now(),
                                        "update_time" timestamp(6) DEFAULT now(),
                                        "is_deleted" int2 NOT NULL DEFAULT 0,
                                        "tenant_id" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
                                        "business_info_id" varchar(50) COLLATE "pg_catalog"."default",
                                        "project_id" varchar(50) COLLATE "pg_catalog"."default",
                                        "order_id" varchar(50) COLLATE "pg_catalog"."default",
                                        "device_region_id" varchar(50) COLLATE "pg_catalog"."default",
                                        CONSTRAINT "device_info_copy2_pkey" PRIMARY KEY ("device_id")
)
;

ALTER TABLE "public"."device_info"
    OWNER TO "postgres";

CREATE INDEX "di_allocation_date_copy1_copy2" ON "public"."device_info" USING btree (
                                                                                     "allocation_time" "pg_catalog"."timestamp_ops" ASC NULLS LAST,
                                                                                     "is_deleted" "pg_catalog"."int2_ops" ASC NULLS LAST
    );

CREATE INDEX "di_business_isp_copy1_copy2" ON "public"."device_info" USING btree (
                                                                                  "business_info_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
                                                                                  "isp" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
                                                                                  "is_deleted" "pg_catalog"."int2_ops" ASC NULLS LAST
    );

CREATE INDEX "di_device_sn_copy1_copy2" ON "public"."device_info" USING btree (
                                                                               "device_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
                                                                               "sn" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
                                                                               "is_deleted" "pg_catalog"."int2_ops" ASC NULLS LAST
    );

CREATE INDEX "di_user_allocation_desc_copy1_copy2" ON "public"."device_info" USING btree (
                                                                                          "user_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
                                                                                          "is_deleted" "pg_catalog"."int2_ops" ASC NULLS LAST,
                                                                                          "allocation_time" "pg_catalog"."timestamp_ops" DESC NULLS FIRST
    );

CREATE INDEX "di_user_status_copy1_copy2" ON "public"."device_info" USING btree (
                                                                                 "user_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
                                                                                 "device_status" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
                                                                                 "is_deleted" "pg_catalog"."int2_ops" ASC NULLS LAST
    );

COMMENT ON COLUMN "public"."device_info"."device_id" IS '设备ID';

COMMENT ON COLUMN "public"."device_info"."allocation_time" IS '分配时间';

COMMENT ON COLUMN "public"."device_info"."expire_time" IS '到期时间';

COMMENT ON COLUMN "public"."device_info"."binding_time" IS '设备绑定时间';

COMMENT ON COLUMN "public"."device_info"."city_name" IS '市名称';

COMMENT ON COLUMN "public"."device_info"."city_code" IS '市编码';

COMMENT ON COLUMN "public"."device_info"."county_name" IS '县名称';

COMMENT ON COLUMN "public"."device_info"."county_code" IS '县编码';

COMMENT ON COLUMN "public"."device_info"."isp" IS '运营商（TELECOM-电信, UNICOM-联通, MOBILE-移动, GUANGDIAN-光电）';

COMMENT ON COLUMN "public"."device_info"."line_count" IS '线路数量';

COMMENT ON COLUMN "public"."device_info"."device_type" IS '设备类型（CDN_BIG_BOX-大盒子, CDN_SMALL_BOX-小盒子）';

COMMENT ON COLUMN "public"."device_info"."device_name" IS '设备名称';

COMMENT ON COLUMN "public"."device_info"."device_remark" IS '设备备注';

COMMENT ON COLUMN "public"."device_info"."device_status" IS '设备状态（ DELIVERING-交付中,HEALTHY-健康, REMOVED-已清退, ERROR-异常）';

COMMENT ON COLUMN "public"."device_info"."province_name" IS '省份名称';

COMMENT ON COLUMN "public"."device_info"."province_code" IS '省份编码';

COMMENT ON COLUMN "public"."device_info"."user_single_uplink_mbps" IS '用户单条上行(Mbps)';

COMMENT ON COLUMN "public"."device_info"."up_single_uplink_mbps" IS '单条上行(Mbps)';

COMMENT ON COLUMN "public"."device_info"."sn" IS 'SN';

COMMENT ON COLUMN "public"."device_info"."user_total_bandwidth_mbps" IS '用户建设总带宽(Mbps)';

COMMENT ON COLUMN "public"."device_info"."up_total_bandwidth_mbps" IS '建设总带宽(Mbps)';

COMMENT ON COLUMN "public"."device_info"."user_id" IS '用户ID';

COMMENT ON COLUMN "public"."device_info"."device_rule_id" IS '设备规则ID';

COMMENT ON COLUMN "public"."device_info"."platform_type" IS '平台类型（NIU_LINK, NIULINK_INVERSE, OLINK, SCTC, ANTCLOUD, JUSHA, ONE_THING_CLOUD）';

COMMENT ON COLUMN "public"."device_info"."device_info_id" IS '设备基本信息ID';

COMMENT ON COLUMN "public"."device_info"."income_date" IS '开始收入时间';

COMMENT ON COLUMN "public"."device_info"."create_time" IS '创建时间';

COMMENT ON COLUMN "public"."device_info"."update_time" IS '更新时间';

COMMENT ON COLUMN "public"."device_info"."is_deleted" IS '是否删除(0:未删除，1-已删除)';

COMMENT ON COLUMN "public"."device_info"."tenant_id" IS '租户ID';

COMMENT ON COLUMN "public"."device_info"."business_info_id" IS '业务信息ID';

COMMENT ON COLUMN "public"."device_info"."project_id" IS '项目ID';

COMMENT ON COLUMN "public"."device_info"."order_id" IS '订单ID';

COMMENT ON COLUMN "public"."device_info"."device_region_id" IS '设备地区ID';

COMMENT ON TABLE "public"."device_info" IS '设备信息表';


-- ==========================================
-- 1. 系统用户表 (sys_user)
-- 重点改动：增加了 balance 钱包余额字段
-- ==========================================
DROP TABLE IF EXISTS "public"."sys_user";
CREATE TABLE "public"."sys_user" (
                                     "user_id" varchar(50) NOT NULL,
                                     "username" varchar(50) NOT NULL,
                                     "password" varchar(100) NOT NULL,
                                     "real_name" varchar(50),
                                     "phone" varchar(20),
                                     "avatar" varchar(255),
                                     "role_key" varchar(20) NOT NULL,
                                     "balance" decimal(10,2) DEFAULT 0.00,
                                     "tenant_id" varchar(50) NOT NULL,
                                     "create_time" timestamp(6) DEFAULT now(),
                                     "update_time" timestamp(6) DEFAULT now(),
                                     "is_deleted" int2 DEFAULT 0,
                                     CONSTRAINT "sys_user_pkey" PRIMARY KEY ("user_id")
);

-- 字段注释
COMMENT ON TABLE "public"."sys_user" IS '系统用户表';
COMMENT ON COLUMN "public"."sys_user"."user_id" IS '用户ID (主键)';
COMMENT ON COLUMN "public"."sys_user"."username" IS '登录账号';
COMMENT ON COLUMN "public"."sys_user"."password" IS '加密密码';
COMMENT ON COLUMN "public"."sys_user"."real_name" IS '真实姓名';
COMMENT ON COLUMN "public"."sys_user"."phone" IS '手机号';
COMMENT ON COLUMN "public"."sys_user"."avatar" IS '头像URL';
COMMENT ON COLUMN "public"."sys_user"."role_key" IS 'BOSS(老板), REPAIRMAN(维修工), ADMIN(管理员), USER(普通用户)';
COMMENT ON COLUMN "public"."sys_user"."balance" IS '钱包余额(默认0.00)';
COMMENT ON COLUMN "public"."sys_user"."tenant_id" IS '租户ID';
COMMENT ON COLUMN "public"."sys_user"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."sys_user"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."sys_user"."is_deleted" IS '0:正常, 1:删除';


-- ==========================================
-- 2. 维修工信息表 (repairman_info)
-- 重点改动：去掉了区域码，使用 province_name/city_name 进行直观匹配
-- ==========================================
DROP TABLE IF EXISTS "public"."repairman_info";
CREATE TABLE "public"."repairman_info" (
                                           "repairman_id" varchar(50) NOT NULL,
                                           "user_id" varchar(50) NOT NULL,
                                           "work_status" int2 DEFAULT 0,
                                           "province_name" varchar(50),
                                           "city_name" varchar(50),
                                           "tenant_id" varchar(50) NOT NULL,
                                           "create_time" timestamp(6) DEFAULT now(),
                                           "update_time" timestamp(6) DEFAULT now(),
                                           CONSTRAINT "repairman_info_pkey" PRIMARY KEY ("repairman_id")
);

-- 字段注释
COMMENT ON TABLE "public"."repairman_info" IS '维修工扩展信息表';
COMMENT ON COLUMN "public"."repairman_info"."repairman_id" IS '维修工唯一标识 (主键)';
COMMENT ON COLUMN "public"."repairman_info"."user_id" IS '关联系统用户ID';
COMMENT ON COLUMN "public"."repairman_info"."work_status" IS '0:空闲-可接单, 1:忙碌-维修中';
COMMENT ON COLUMN "public"."repairman_info"."province_name" IS '服务省份';
COMMENT ON COLUMN "public"."repairman_info"."city_name" IS '服务城市';
COMMENT ON COLUMN "public"."repairman_info"."tenant_id" IS '租户ID';
COMMENT ON COLUMN "public"."repairman_info"."create_time" IS '入职/创建时间';


-- ==========================================
-- 3. 维修工单表 (work_order)
-- 重点改动：全能表，包含地址、财务结算、申诉、状态机
-- ==========================================
DROP TABLE IF EXISTS "public"."work_order";
CREATE TABLE "public"."work_order" (
                                       "order_id" varchar(50) NOT NULL,
                                       "order_sn" varchar(50) NOT NULL,
                                       "device_id" varchar(50) NOT NULL,
                                       "device_name" varchar(50) NOT NULL,
                                       "reporter_id" varchar(50) NOT NULL,
                                       "owner_id" varchar(50) NOT NULL,
                                       "repairman_id" varchar(50),
                                       "address_detail" varchar(255) NOT NULL ,
                                       "fault_desc" text NOT NULL,
                                       "fault_images" text,
                                       "repair_result" text,
                                       "repair_images" text,
                                       "order_status" int2 DEFAULT 0,

    -- 财务字段
                                       "material_fee" decimal(10,2) DEFAULT 0.00,
                                       "labor_fee" decimal(10,2) DEFAULT 0.00,
                                       "total_amount" decimal(10,2) DEFAULT 0.00,
                                       "platform_rate" decimal(4,2) DEFAULT 0.10,
                                       "platform_income" decimal(10,2) DEFAULT 0.00,
                                       "repairman_income" decimal(10,2) DEFAULT 0.00,

    -- 申诉字段
                                       "appeal_reason" varchar(500),
                                       "appeal_handle_log" text,

    -- 时间字段
                                       "create_time" timestamp(6) DEFAULT now(),
                                       "dispatch_time" timestamp(6),
                                       "arrive_time" timestamp(6),
                                       "finish_time" timestamp(6),
                                       "pay_time" timestamp(6),

                                       "tenant_id" varchar(50) NOT NULL,
                                       "update_time" timestamp(6) DEFAULT now(),
                                       "is_deleted" int2 DEFAULT 0,
                                       CONSTRAINT "work_order_pkey" PRIMARY KEY ("order_id")
);

-- 字段注释
COMMENT ON TABLE "public"."work_order" IS '维修工单主表 (含财务与申诉)';
COMMENT ON COLUMN "public"."work_order"."order_id" IS '工单系统ID (主键)';
COMMENT ON COLUMN "public"."work_order"."order_sn" IS '业务展示单号';
COMMENT ON COLUMN "public"."work_order"."device_id" IS '关联设备ID';
COMMENT ON COLUMN "public"."work_order"."device_name" IS '设备名称';
COMMENT ON COLUMN "public"."work_order"."reporter_id" IS '报修人ID';
COMMENT ON COLUMN "public"."work_order"."owner_id" IS '设备老板ID';
COMMENT ON COLUMN "public"."work_order"."repairman_id" IS '维修工ID';
COMMENT ON COLUMN "public"."work_order"."address_detail" IS '详细维修地址';
COMMENT ON COLUMN "public"."work_order"."fault_desc" IS '故障描述';
COMMENT ON COLUMN "public"."work_order"."fault_images" IS '故障图片 (JSON数组,可选)';
COMMENT ON COLUMN "public"."work_order"."repair_result" IS '完工备注';
COMMENT ON COLUMN "public"."work_order"."repair_images" IS '完工凭证图片 (JSON数组)';
COMMENT ON COLUMN "public"."work_order"."order_status" IS '0-待接单, 1-维修中, 3-待支付, 4-已取消, 5-已完成, 6-申诉中';
COMMENT ON COLUMN "public"."work_order"."material_fee" IS '材料费';
COMMENT ON COLUMN "public"."work_order"."labor_fee" IS '人工费';
COMMENT ON COLUMN "public"."work_order"."total_amount" IS '订单总额 (材料费 + 人工费)';
COMMENT ON COLUMN "public"."work_order"."platform_rate" IS '平台费率快照(抽成比例)';
COMMENT ON COLUMN "public"."work_order"."platform_income" IS '平台分润收入(人工费 * 费率)';
COMMENT ON COLUMN "public"."work_order"."repairman_income" IS '维修工实际收入(总额 - 平台分润)';
COMMENT ON COLUMN "public"."work_order"."appeal_reason" IS '老板申诉理由';
COMMENT ON COLUMN "public"."work_order"."appeal_handle_log" IS '管理员申诉处理记录';
COMMENT ON COLUMN "public"."work_order"."create_time" IS '报修时间';
COMMENT ON COLUMN "public"."work_order"."dispatch_time" IS '接单/派单时间';
COMMENT ON COLUMN "public"."work_order"."arrive_time" IS '维修工到达现场时间';
COMMENT ON COLUMN "public"."work_order"."finish_time" IS '完工录入时间';
COMMENT ON COLUMN "public"."work_order"."pay_time" IS '老板支付/结单时间';
COMMENT ON COLUMN "public"."work_order"."tenant_id" IS '租户ID';


-- ==========================================
-- 4. 资金流水表 (capital_flow)
-- 重点改动：包含余额快照，用于老板查账和平台对账
-- ==========================================
DROP TABLE IF EXISTS "public"."capital_flow";
CREATE TABLE "public"."capital_flow" (
                                         "flow_id" int8 NOT NULL GENERATED BY DEFAULT AS IDENTITY,
                                         "trade_no" varchar(64) NOT NULL,
                                         "order_id" varchar(50) NOT NULL,
                                         "account_id" varchar(50) NOT NULL,
                                         "flow_type" varchar(20) NOT NULL,
                                         "amount" decimal(10,2) NOT NULL,
                                         "balance_snapshot" decimal(10,2) NOT NULL,
                                         "remark" varchar(255),
                                         "tenant_id" varchar(50) NOT NULL,
                                         "create_time" timestamp(6) DEFAULT now(),
                                         CONSTRAINT "capital_flow_pkey" PRIMARY KEY ("flow_id")
);

-- 字段注释
COMMENT ON TABLE "public"."capital_flow" IS '资金流水表';
COMMENT ON COLUMN "public"."capital_flow"."flow_id" IS '流水ID (自增主键)';
COMMENT ON COLUMN "public"."capital_flow"."trade_no" IS '交易流水号 (唯一)';
COMMENT ON COLUMN "public"."capital_flow"."order_id" IS '关联工单ID';
COMMENT ON COLUMN "public"."capital_flow"."account_id" IS '资金归属账户ID(用户ID/PLATFORM_SYS)';
COMMENT ON COLUMN "public"."capital_flow"."flow_type" IS 'PAY(支出), INCOME(收入), REFUND(退款)';
COMMENT ON COLUMN "public"."capital_flow"."amount" IS '变动金额 (负数为支出，正数为收入)';
COMMENT ON COLUMN "public"."capital_flow"."balance_snapshot" IS '变动后的余额快照 (用于前端展示和对账)';
COMMENT ON COLUMN "public"."capital_flow"."remark" IS '备注说明';
COMMENT ON COLUMN "public"."capital_flow"."tenant_id" IS '租户ID';
COMMENT ON COLUMN "public"."capital_flow"."create_time" IS '记账时间';


-- ==========================================
-- 5. 工单操作日志表 (work_order_log)
-- ==========================================
DROP TABLE IF EXISTS "public"."work_order_log";
CREATE TABLE "public"."work_order_log" (
                                           "log_id" int8 NOT NULL GENERATED BY DEFAULT AS IDENTITY,
                                           "order_id" varchar(50) NOT NULL,
                                           "operator_id" varchar(50) NOT NULL,
                                           "operator_role" varchar(20) NOT NULL,
                                           "action_type" varchar(50) NOT NULL,
                                           "action_desc" varchar(255),
                                           "create_time" timestamp(6) DEFAULT now(),
                                           CONSTRAINT "work_order_log_pkey" PRIMARY KEY ("log_id")
);

-- 字段注释
COMMENT ON TABLE "public"."work_order_log" IS '工单全链路操作日志表';
COMMENT ON COLUMN "public"."work_order_log"."log_id" IS '日志ID (自增主键)';
COMMENT ON COLUMN "public"."work_order_log"."order_id" IS '关联工单ID';
COMMENT ON COLUMN "public"."work_order_log"."operator_id" IS '操作人ID';
COMMENT ON COLUMN "public"."work_order_log"."operator_role" IS 'USER, BOSS, REPAIRMAN, ADMIN';
COMMENT ON COLUMN "public"."work_order_log"."action_type" IS 'CREATE, ASSIGN, TRANSFER, FINISH, APPEAL, PAY, CANCEL';
COMMENT ON COLUMN "public"."work_order_log"."action_desc" IS '备注/详情';
COMMENT ON COLUMN "public"."work_order_log"."create_time" IS '操作时间';