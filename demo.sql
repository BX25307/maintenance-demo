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

CREATE TABLE "public"."repairman_info" (
    -- 1. 主键
                                           "repairman_id" varchar(50) NOT NULL,

    -- 2. 关联用户 (核心外键)
                                           "user_id" varchar(50) NOT NULL,

    -- 3. 业务状态 (这是这张表存在的意义)
                                           "work_status" int2 NOT NULL DEFAULT 0,

    -- 4. 系统审计
                                           "tenant_id" varchar(50) NOT NULL,
                                           "create_time" timestamp(6) DEFAULT now(),
                                           "update_time" timestamp(6) DEFAULT now(),
                                           "is_deleted" int2 NOT NULL DEFAULT 0,

                                           CONSTRAINT "repairman_info_pkey" PRIMARY KEY ("repairman_id")
);

-- ==========================================
-- 字段详细说明 (注释)
-- ==========================================
COMMENT ON TABLE "public"."repairman_info" IS '维修人员业务扩展表';

COMMENT ON COLUMN "public"."repairman_info"."repairman_id" IS '维修工信息唯一标识 (主键)';
COMMENT ON COLUMN "public"."repairman_info"."user_id" IS 'user_id';
COMMENT ON COLUMN "public"."repairman_info"."work_status" IS '0:空闲-可接单, 1:忙碌-维修中';
COMMENT ON COLUMN "public"."repairman_info"."tenant_id" IS '租户ID';
COMMENT ON COLUMN "public"."repairman_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."repairman_info"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."repairman_info"."is_deleted" IS '0:正常, 1:已删除';

CREATE TABLE "public"."work_order" (
    -- 1. 主键与单号
                                       "order_id" varchar(50) NOT NULL,

    -- 2. 关联各方ID (外键区域)
                                       "device_id" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
                                       "owner_id" varchar(50) COLLATE "pg_catalog"."default",
                                       "reporter_id" varchar(50) COLLATE "pg_catalog"."default",
                                       "admin_id" varchar(50) COLLATE "pg_catalog"."default",
                                       "repairman_id" varchar(50) COLLATE "pg_catalog"."default",

    -- 3. 报修内容
                                       "fault_desc" text COLLATE "pg_catalog"."default",
                                       "fault_images" text COLLATE "pg_catalog"."default",

    -- 4. 维修结果
                                       "repair_result" text COLLATE "pg_catalog"."default",

    -- 5. 流程控制
                                       "order_status" int2 NOT NULL DEFAULT 0,

    -- 6. 时间节点
                                       "create_time" timestamp(6) DEFAULT now(),
                                       "assign_time" timestamp(6),
                                       "finish_time" timestamp(6),

    -- 7. 系统通用字段
                                       "update_time" timestamp(6) DEFAULT now(),
                                       "is_deleted" int2 NOT NULL DEFAULT 0,
                                       "tenant_id" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,

                                       CONSTRAINT "work_order_pkey" PRIMARY KEY ("order_id")
);

-- 字段含义详细规定
COMMENT ON TABLE "public"."work_order" IS '设备维修工单主表';
COMMENT ON COLUMN "public"."work_order"."order_id" IS '工单唯一标识(主键)';
COMMENT ON COLUMN "public"."work_order"."device_id" IS '关联的设备ID';
COMMENT ON COLUMN "public"."work_order"."owner_id" IS '设备所属老板ID';
COMMENT ON COLUMN "public"."work_order"."reporter_id" IS '报修人ID';
COMMENT ON COLUMN "public"."work_order"."admin_id" IS '派单的管理员ID';
COMMENT ON COLUMN "public"."work_order"."repairman_id" IS '被指派的维修员ID';
COMMENT ON COLUMN "public"."work_order"."fault_desc" IS '故障详细描述';
COMMENT ON COLUMN "public"."work_order"."fault_images" IS '故障现场照片(JSON数组格式)';
COMMENT ON COLUMN "public"."work_order"."repair_result" IS '维修完成的备注';
COMMENT ON COLUMN "public"."work_order"."order_status" IS '0-待派单, 1-待维修, 2-维修中, 3-已完成, 4-已取消';
COMMENT ON COLUMN "public"."work_order"."create_time" IS '报修提交时间';
COMMENT ON COLUMN "public"."work_order"."assign_time" IS '管理员执行派单动作的时间';
COMMENT ON COLUMN "public"."work_order"."finish_time" IS '维修结束/工单关闭的时间';
COMMENT ON COLUMN "public"."work_order"."update_time" IS '最后更新时间';
COMMENT ON COLUMN "public"."work_order"."is_deleted" IS '逻辑删除标志：0-未删除, 1-已删除';
COMMENT ON COLUMN "public"."work_order"."tenant_id" IS '租户ID';

CREATE TABLE "public"."work_order_log" (
    -- 1. 主键
                                           "log_id" bigserial NOT NULL,

    -- 2. 关联工单
                                           "order_id" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,

    -- 3. 操作者信息
                                           "operator_id" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
                                           "operator_role" varchar(20) COLLATE "pg_catalog"."default",

    -- 4. 动作详情
                                           "action_type" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
                                           "action_desc" varchar(255) COLLATE "pg_catalog"."default",

    -- 5. 时间
                                           "create_time" timestamp(6) DEFAULT now(),

                                           CONSTRAINT "work_order_log_pkey" PRIMARY KEY ("log_id")
);

-- 字段含义详细规定
COMMENT ON TABLE "public"."work_order_log" IS '工单操作日志表';
COMMENT ON COLUMN "public"."work_order_log"."log_id" IS '日志流水号(自增主键)';
COMMENT ON COLUMN "public"."work_order_log"."order_id" IS '关联的工单ID';
COMMENT ON COLUMN "public"."work_order_log"."operator_id" IS '执行操作的人员ID';
COMMENT ON COLUMN "public"."work_order_log"."operator_role" IS 'USER(普通用户), ADMIN(管理员), REPAIRMAN(维修员)';
COMMENT ON COLUMN "public"."work_order_log"."action_type" IS 'CREATE(创建), ASSIGN(派单), START(开始), FINISH(完成), CANCEL(取消)';
COMMENT ON COLUMN "public"."work_order_log"."action_desc" IS '操作备注';
COMMENT ON COLUMN "public"."work_order_log"."create_time" IS '操作发生的时间';

CREATE TABLE "public"."sys_user" (
    -- 1. 核心身份
                                     "user_id" varchar(50) NOT NULL,

    -- 2. 登录认证
                                     "username" varchar(50) NOT NULL,
                                     "password" varchar(100) NOT NULL,

    -- 3. 基础信息
                                     "real_name" varchar(50),
                                     "phone" varchar(20),
                                     "avatar" varchar(255),

    -- 4. 权限控制 (核心)
                                     "role_key" varchar(20) NOT NULL,

    -- 5. 系统审计
                                     "tenant_id" varchar(50) NOT NULL,
                                     "create_time" timestamp(6) DEFAULT now(),
                                     "update_time" timestamp(6) DEFAULT now(),
                                     "is_deleted" int2 NOT NULL DEFAULT 0,

                                     CONSTRAINT "sys_user_pkey" PRIMARY KEY ("user_id")
);

-- ==========================================
-- 字段详细说明 (注释)
-- ==========================================
COMMENT ON TABLE "public"."sys_user" IS '系统统一用户基础信息表';

COMMENT ON COLUMN "public"."sys_user"."user_id" IS '用户唯一标识 (主键)';
COMMENT ON COLUMN "public"."sys_user"."username" IS '登录账号 (唯一索引)';
COMMENT ON COLUMN "public"."sys_user"."password" IS '登录密码 ';
COMMENT ON COLUMN "public"."sys_user"."real_name" IS '真实姓名';
COMMENT ON COLUMN "public"."sys_user"."phone" IS '联系手机号';
COMMENT ON COLUMN "public"."sys_user"."avatar" IS '用户头像地址';
COMMENT ON COLUMN "public"."sys_user"."role_key" IS 'ADMIN-管理员, USER-普通用户, REPAIRMAN-维修员,BOSS-老板';
COMMENT ON COLUMN "public"."sys_user"."tenant_id" IS '租户ID';
COMMENT ON COLUMN "public"."sys_user"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."sys_user"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."sys_user"."is_deleted" IS '0:正常, 1:已删除';