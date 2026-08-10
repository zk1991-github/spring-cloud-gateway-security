/*
 Navicat Premium Dump SQL

 Source Server         : 192.168.110.100
 Source Server Type    : PostgreSQL
 Source Server Version : 160003 (160003)
 Source Host           : 192.168.110.100:5432
 Source Catalog        : vector-map-data
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 160003 (160003)
 File Encoding         : 65001

 Date: 10/08/2026 14:59:23
*/


-- ----------------------------
-- Table structure for gateway_config
-- ----------------------------
DROP TABLE IF EXISTS "public"."gateway_config";
CREATE TABLE "public"."gateway_config" (
  "id" int8 NOT NULL,
  "config_key" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "config_value" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "create_time" timestamp(6),
  "update_time" timestamp(6)
)
;

-- ----------------------------
-- Records of gateway_config
-- ----------------------------
INSERT INTO "public"."gateway_config" VALUES (1, 'ip_whitelist_enabled', 'true', NULL, '2026-08-07 11:12:35.803863');
INSERT INTO "public"."gateway_config" VALUES (2, 'mac_whitelist_enabled', 'true', NULL, NULL);

-- ----------------------------
-- Table structure for gateway_dict
-- ----------------------------
DROP TABLE IF EXISTS "public"."gateway_dict";
CREATE TABLE "public"."gateway_dict" (
  "id" int8 NOT NULL,
  "dict_type_id" int8 NOT NULL,
  "dict_val" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "dict_name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL
)
;
COMMENT ON COLUMN "public"."gateway_dict"."id" IS '唯一id';
COMMENT ON COLUMN "public"."gateway_dict"."dict_type_id" IS '字典类型id';
COMMENT ON COLUMN "public"."gateway_dict"."dict_val" IS '字典值';
COMMENT ON COLUMN "public"."gateway_dict"."dict_name" IS '字典名称';
COMMENT ON TABLE "public"."gateway_dict" IS '字典表';

-- ----------------------------
-- Records of gateway_dict
-- ----------------------------
INSERT INTO "public"."gateway_dict" VALUES (1, 1, '0', '私有');
INSERT INTO "public"."gateway_dict" VALUES (2, 1, '1', '公开');
INSERT INTO "public"."gateway_dict" VALUES (3, 1, '2', '匿名');

-- ----------------------------
-- Table structure for gateway_dict_type
-- ----------------------------
DROP TABLE IF EXISTS "public"."gateway_dict_type";
CREATE TABLE "public"."gateway_dict_type" (
  "id" int8 NOT NULL,
  "name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL
)
;
COMMENT ON COLUMN "public"."gateway_dict_type"."id" IS '唯一id';
COMMENT ON COLUMN "public"."gateway_dict_type"."name" IS '类型名称';
COMMENT ON TABLE "public"."gateway_dict_type" IS '字典类型表';

-- ----------------------------
-- Records of gateway_dict_type
-- ----------------------------
INSERT INTO "public"."gateway_dict_type" VALUES (1, '接口类型');

-- ----------------------------
-- Table structure for gateway_permission
-- ----------------------------
DROP TABLE IF EXISTS "public"."gateway_permission";
CREATE TABLE "public"."gateway_permission" (
  "id" int8 NOT NULL,
  "group_id" int8 NOT NULL,
  "url_name" varchar(255) COLLATE "pg_catalog"."default",
  "url" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "open" int2 NOT NULL,
  "description" varchar(255) COLLATE "pg_catalog"."default",
  "fixed" int2 NOT NULL,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN "public"."gateway_permission"."id" IS '权限id';
COMMENT ON COLUMN "public"."gateway_permission"."group_id" IS '组id';
COMMENT ON COLUMN "public"."gateway_permission"."url_name" IS '接口名称';
COMMENT ON COLUMN "public"."gateway_permission"."url" IS '接口地址';
COMMENT ON COLUMN "public"."gateway_permission"."open" IS '是否公开 0:私有 1:公开 2:匿名';
COMMENT ON COLUMN "public"."gateway_permission"."description" IS '描述';
COMMENT ON COLUMN "public"."gateway_permission"."fixed" IS '是否固定 0:非固定 1:固定';
COMMENT ON COLUMN "public"."gateway_permission"."create_time" IS '创建时间';
COMMENT ON TABLE "public"."gateway_permission" IS '权限表';

-- ----------------------------
-- Records of gateway_permission
-- ----------------------------
INSERT INTO "public"."gateway_permission" VALUES (2077937030405197826, 0, '网关内置接口', '/gateway/**', 0, '', 1, '2026-07-17 10:02:48.909457');
INSERT INTO "public"."gateway_permission" VALUES (2079039559297921026, 0, '业务', '/vector-map/**', 0, '', 0, '2026-07-20 11:03:52.284565');

-- ----------------------------
-- Table structure for gateway_permission_group
-- ----------------------------
DROP TABLE IF EXISTS "public"."gateway_permission_group";
CREATE TABLE "public"."gateway_permission_group" (
  "id" int8 NOT NULL,
  "group_name" varchar(15) COLLATE "pg_catalog"."default" NOT NULL,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN "public"."gateway_permission_group"."id" IS '分组id';
COMMENT ON COLUMN "public"."gateway_permission_group"."group_name" IS '分组名';
COMMENT ON COLUMN "public"."gateway_permission_group"."create_time" IS '创建时间';
COMMENT ON TABLE "public"."gateway_permission_group" IS '权限分组表';

-- ----------------------------
-- Records of gateway_permission_group
-- ----------------------------

-- ----------------------------
-- Table structure for gateway_request_monitor
-- ----------------------------
DROP TABLE IF EXISTS "public"."gateway_request_monitor";
CREATE TABLE "public"."gateway_request_monitor" (
  "id" int8 NOT NULL,
  "url_path" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "status" int4 NOT NULL,
  "response_duration" int8 NOT NULL,
  "exception_desc" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "request_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN "public"."gateway_request_monitor"."id" IS '唯一id';
COMMENT ON COLUMN "public"."gateway_request_monitor"."url_path" IS 'url路径';
COMMENT ON COLUMN "public"."gateway_request_monitor"."status" IS '响应状态';
COMMENT ON COLUMN "public"."gateway_request_monitor"."response_duration" IS '响应时长';
COMMENT ON COLUMN "public"."gateway_request_monitor"."exception_desc" IS '异常描述';
COMMENT ON COLUMN "public"."gateway_request_monitor"."request_time" IS '请求时间';
COMMENT ON TABLE "public"."gateway_request_monitor" IS '请求监控表';

-- ----------------------------
-- Records of gateway_request_monitor
-- ----------------------------

-- ----------------------------
-- Table structure for gateway_role_permission
-- ----------------------------
DROP TABLE IF EXISTS "public"."gateway_role_permission";
CREATE TABLE "public"."gateway_role_permission" (
  "id" int8 NOT NULL,
  "role_id" int8 NOT NULL,
  "permission_id" int8 NOT NULL
)
;
COMMENT ON COLUMN "public"."gateway_role_permission"."id" IS '唯一id';
COMMENT ON COLUMN "public"."gateway_role_permission"."role_id" IS '角色id';
COMMENT ON COLUMN "public"."gateway_role_permission"."permission_id" IS '权限id';
COMMENT ON TABLE "public"."gateway_role_permission" IS '角色权限关联表';

-- ----------------------------
-- Records of gateway_role_permission
-- ----------------------------
INSERT INTO "public"."gateway_role_permission" VALUES (2079041339473678338, 1, 2077937030405197826);
INSERT INTO "public"."gateway_role_permission" VALUES (2079041339486261249, 1, 2079039559297921026);

-- ----------------------------
-- Table structure for gateway_whitelist
-- ----------------------------
DROP TABLE IF EXISTS "public"."gateway_whitelist";
CREATE TABLE "public"."gateway_whitelist" (
  "id" int8 NOT NULL,
  "ip_addr" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "mac_addr" varchar(50) COLLATE "pg_catalog"."default",
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "update_time" timestamp(6) NOT NULL DEFAULT '1970-01-01 00:00:00'::timestamp without time zone
)
;
COMMENT ON COLUMN "public"."gateway_whitelist"."id" IS '唯一id';
COMMENT ON COLUMN "public"."gateway_whitelist"."ip_addr" IS 'ip地址';
COMMENT ON COLUMN "public"."gateway_whitelist"."mac_addr" IS 'mac地址';
COMMENT ON COLUMN "public"."gateway_whitelist"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."gateway_whitelist"."update_time" IS '修改时间';
COMMENT ON TABLE "public"."gateway_whitelist" IS '白名单表';

-- ----------------------------
-- Records of gateway_whitelist
-- ----------------------------
INSERT INTO "public"."gateway_whitelist" VALUES (1, '127.0.0.1', '00-50-56-C0-00-08', '2026-08-07 05:04:02.66725', '1970-01-01 00:00:00');

-- ----------------------------
-- Table structure for t_log
-- ----------------------------
DROP TABLE IF EXISTS "public"."t_log";
CREATE TABLE "public"."t_log" (
  "id" int8 NOT NULL,
  "user_id" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "username" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "ip" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "status" int2 NOT NULL,
  "msg" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN "public"."t_log"."id" IS '唯一id';
COMMENT ON COLUMN "public"."t_log"."user_id" IS '用户id';
COMMENT ON COLUMN "public"."t_log"."username" IS '用户名';
COMMENT ON COLUMN "public"."t_log"."ip" IS 'ip地址';
COMMENT ON COLUMN "public"."t_log"."status" IS '登录状态';
COMMENT ON COLUMN "public"."t_log"."msg" IS '登录信息';
COMMENT ON COLUMN "public"."t_log"."time" IS '登录时间';
COMMENT ON TABLE "public"."t_log" IS '登录日志表';

-- ----------------------------
-- Records of t_log
-- ----------------------------
INSERT INTO "public"."t_log" VALUES (1840730109544116225, '1', 'admin', '172.30.1.44', 1, '登录成功', '2024-09-30 21:19:29');
INSERT INTO "public"."t_log" VALUES (1840730178705858562, '1', 'admin', '172.30.1.44', 1, '登录成功', '2024-09-30 21:19:45');
INSERT INTO "public"."t_log" VALUES (1840731196375900161, '1', 'admin', '172.30.1.44', 1, '登录成功', '2024-09-30 21:23:46');
INSERT INTO "public"."t_log" VALUES (1840731225751793665, '1', 'admin', '172.30.1.44', 1, '登录成功', '2024-09-30 21:23:53');
INSERT INTO "public"."t_log" VALUES (2072254623007752194, '1', 'admin', '0:0:0:0:0:0:0:1', 1, '登录成功', '2026-07-01 17:42:57.409023');
INSERT INTO "public"."t_log" VALUES (2072257869281607681, '1', 'admin', '0:0:0:0:0:0:0:1', 1, '登录成功', '2026-07-01 17:55:51.38469');
INSERT INTO "public"."t_log" VALUES (2072258036256849922, '0', 'superadmin', '0:0:0:0:0:0:0:1', 1, '登录成功', '2026-07-01 17:56:31.192414');
INSERT INTO "public"."t_log" VALUES (2072258474041524226, '1', 'admin', '0:0:0:0:0:0:0:1', 1, '登录成功', '2026-07-01 17:58:15.570862');
INSERT INTO "public"."t_log" VALUES (2072258864241819649, '0', 'superadmin', '0:0:0:0:0:0:0:1', 1, '登录成功', '2026-07-01 17:59:48.601431');
INSERT INTO "public"."t_log" VALUES (2072583650457161730, '1', 'admin', '0:0:0:0:0:0:0:1', 1, '登录成功', '2026-07-02 15:30:23');

-- ----------------------------
-- Table structure for t_role
-- ----------------------------
DROP TABLE IF EXISTS "public"."t_role";
CREATE TABLE "public"."t_role" (
  "id" int8 NOT NULL,
  "role_name" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "description" varchar(255) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."t_role"."id" IS '角色id';
COMMENT ON COLUMN "public"."t_role"."role_name" IS '角色名称';
COMMENT ON COLUMN "public"."t_role"."description" IS '描述';
COMMENT ON TABLE "public"."t_role" IS '角色表';

-- ----------------------------
-- Records of t_role
-- ----------------------------
INSERT INTO "public"."t_role" VALUES (1, '管理员', NULL);
INSERT INTO "public"."t_role" VALUES (2, '普通用户', NULL);

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS "public"."t_user";
CREATE TABLE "public"."t_user" (
  "id" int8 NOT NULL,
  "username" varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
  "password" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "phone" varchar(255) COLLATE "pg_catalog"."default",
  "account_non_locked" int2 NOT NULL DEFAULT 1,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN "public"."t_user"."id" IS '用户id';
COMMENT ON COLUMN "public"."t_user"."username" IS '用户名';
COMMENT ON COLUMN "public"."t_user"."password" IS '密码';
COMMENT ON COLUMN "public"."t_user"."phone" IS '手机号';
COMMENT ON COLUMN "public"."t_user"."account_non_locked" IS '是否未锁定';
COMMENT ON COLUMN "public"."t_user"."create_time" IS '创建时间';
COMMENT ON TABLE "public"."t_user" IS '用户表';

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO "public"."t_user" VALUES (1, 'admin', '{bcrypt}$2a$10$zLX9NstaOMno60xyqDWaOupK5KXCPQp1n75GOIoa4JVFO.BrZJWm2', '13333333333', 1, '2024-03-27 15:31:30');
INSERT INTO "public"."t_user" VALUES (2, 'zk', '{bcrypt}$2a$10$0EQexC0XYw58x.ys.Ym8QO3H2Llr0G4wEAFddm8PkOUGy6hQraaui', '14444444444', 1, '2023-10-07 15:04:32');

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS "public"."user_role";
CREATE TABLE "public"."user_role" (
  "id" int8 NOT NULL,
  "user_id" int8 NOT NULL,
  "role_id" int8 NOT NULL
)
;
COMMENT ON COLUMN "public"."user_role"."id" IS '唯一id';
COMMENT ON COLUMN "public"."user_role"."user_id" IS '用户id';
COMMENT ON COLUMN "public"."user_role"."role_id" IS '角色id';
COMMENT ON TABLE "public"."user_role" IS '用户角色关联表';

-- ----------------------------
-- Records of user_role
-- ----------------------------
INSERT INTO "public"."user_role" VALUES (1, 1, 1);
INSERT INTO "public"."user_role" VALUES (2, 2, 2);

-- ----------------------------
-- Uniques structure for table gateway_config
-- ----------------------------
ALTER TABLE "public"."gateway_config" ADD CONSTRAINT "gateway_config_config_key_key" UNIQUE ("config_key");

-- ----------------------------
-- Primary Key structure for table gateway_config
-- ----------------------------
ALTER TABLE "public"."gateway_config" ADD CONSTRAINT "gateway_config_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table gateway_dict
-- ----------------------------
ALTER TABLE "public"."gateway_dict" ADD CONSTRAINT "gateway_dict_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table gateway_dict_type
-- ----------------------------
ALTER TABLE "public"."gateway_dict_type" ADD CONSTRAINT "gateway_dict_type_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table gateway_permission
-- ----------------------------
ALTER TABLE "public"."gateway_permission" ADD CONSTRAINT "gateway_permission_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table gateway_permission_group
-- ----------------------------
ALTER TABLE "public"."gateway_permission_group" ADD CONSTRAINT "gateway_permission_group_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table gateway_request_monitor
-- ----------------------------
ALTER TABLE "public"."gateway_request_monitor" ADD CONSTRAINT "gateway_request_monitor_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table gateway_role_permission
-- ----------------------------
ALTER TABLE "public"."gateway_role_permission" ADD CONSTRAINT "gateway_role_permission_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table gateway_whitelist
-- ----------------------------
ALTER TABLE "public"."gateway_whitelist" ADD CONSTRAINT "gateway_whitelist_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table t_log
-- ----------------------------
ALTER TABLE "public"."t_log" ADD CONSTRAINT "t_log_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table t_role
-- ----------------------------
ALTER TABLE "public"."t_role" ADD CONSTRAINT "t_role_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table t_user
-- ----------------------------
ALTER TABLE "public"."t_user" ADD CONSTRAINT "t_user_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table user_role
-- ----------------------------
ALTER TABLE "public"."user_role" ADD CONSTRAINT "user_role_pkey" PRIMARY KEY ("id");
