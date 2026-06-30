-- PostgreSQL 数据库建库脚本
-- 使用前请手动创建数据库: CREATE DATABASE security_db;

-- 字典表
CREATE TABLE IF NOT EXISTS gateway_dict (
    id          bigint NOT NULL,
    dict_type_id bigint NOT NULL,
    dict_val    varchar(255) NOT NULL,
    dict_name   varchar(255) NOT NULL,
    PRIMARY KEY (id)
);

COMMENT ON TABLE gateway_dict IS '字典表';
COMMENT ON COLUMN gateway_dict.id IS '唯一id';
COMMENT ON COLUMN gateway_dict.dict_type_id IS '字典类型id';
COMMENT ON COLUMN gateway_dict.dict_val IS '字典值';
COMMENT ON COLUMN gateway_dict.dict_name IS '字典名称';

INSERT INTO gateway_dict (id, dict_type_id, dict_val, dict_name)
VALUES (1, 1, '0', '私有'), (2, 1, '1', '公开'), (3, 1, '2', '匿名')
ON CONFLICT (id) DO NOTHING;

-- 字典类型表
CREATE TABLE IF NOT EXISTS gateway_dict_type (
    id   bigint NOT NULL,
    name varchar(255) NOT NULL,
    PRIMARY KEY (id)
);

COMMENT ON TABLE gateway_dict_type IS '字典类型表';
COMMENT ON COLUMN gateway_dict_type.id IS '唯一id';
COMMENT ON COLUMN gateway_dict_type.name IS '类型名称';

INSERT INTO gateway_dict_type (id, name)
VALUES (1, '接口权限')
ON CONFLICT (id) DO NOTHING;

-- 权限表
CREATE TABLE IF NOT EXISTS gateway_permission (
    id          bigint NOT NULL,
    group_id    bigint NOT NULL,
    url_name    varchar(255),
    url         varchar(255) NOT NULL,
    open        smallint NOT NULL,
    description varchar(255),
    fixed       smallint NOT NULL,
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

COMMENT ON TABLE gateway_permission IS '权限表';
COMMENT ON COLUMN gateway_permission.id IS '权限id';
COMMENT ON COLUMN gateway_permission.group_id IS '组id';
COMMENT ON COLUMN gateway_permission.url_name IS '接口名称';
COMMENT ON COLUMN gateway_permission.url IS '接口地址';
COMMENT ON COLUMN gateway_permission.open IS '是否公开 0:私有 1:公开 2:匿名';
COMMENT ON COLUMN gateway_permission.description IS '描述';
COMMENT ON COLUMN gateway_permission.fixed IS '是否固定 0:非固定 1:固定';
COMMENT ON COLUMN gateway_permission.create_time IS '创建时间';

INSERT INTO gateway_permission (id, group_id, url_name, url, open, description, fixed, create_time) VALUES
(1652509001346134017, 1652508934101630977, '放权', '/gateway/test/**', 1, '', 0, '2023-04-30 01:29:30'),
(1652590143767207938, 1652508934101630977, '测试放权', '/gateway/hello/**', 1, '', 0, '2023-04-30 07:08:24'),
(1655736656925999106, 1655736526144806914, '日志分页', '/log/logPage', 0, '', 0, '2023-05-09 02:09:13'),
(1655736673993146369, 1655736526144806914, '日志删除', '/log/delLog', 0, '', 0, '2023-05-09 02:09:17'),
(1686199840235286530, 1655736526144806914, '查看所有请求监控', '/requestMonitor/selectAll', 0, '', 0, '2023-08-01 14:34:41'),
(1686386764905422849, 1652508934101630977, '全部放行', '/**', 2, '', 1, '2023-08-02 03:24:40'),
(1687416688680931329, 1687416601613008898, '用户分页', '/user/userPage', 0, '', 0, '2023-08-04 23:47:43'),
(1687416715191365633, 1687416601613008898, '新增用户', '/user/addUser', 0, '', 0, '2023-08-04 23:47:49'),
(1687416736441782273, 1687416601613008898, '重置密码', '/user/resetPassword', 0, '', 0, '2023-08-04 23:47:54'),
(1687416766954745857, 1687416601613008898, '删除用户', '/user/delUser', 0, '', 0, '2023-08-04 23:48:01'),
(1687416811210264577, 1687416601613008898, '修改用户', '/user/updateUser', 0, '', 0, '2023-08-04 23:48:12'),
(1687416973747474434, 1687416918134075393, '角色分页', '/role/rolePage', 0, '', 0, '2023-08-04 23:48:52'),
(1687416997182042113, 1687416918134075393, '新增角色', '/role/addRole', 0, '', 0, '2023-08-04 23:48:58'),
(1687417019693838337, 1687416918134075393, '修改角色', '/role/updateRole', 0, '', 0, '2023-08-04 23:49:03'),
(1687417039714037761, 1687416918134075393, '删除角色', '/role/delRole', 0, '', 0, '2023-08-04 23:49:08'),
(1687417100028473345, 1687416918134075393, '授权', '/role/assignPermission', 0, '', 0, '2023-08-04 23:49:22'),
(1687425795737088002, 1687425761876586498, '权限分页', '/permission/permissionPage', 0, '', 0, '2023-08-05 00:23:28'),
(1687425838362628097, 1687425761876586498, '新增权限', '/permission/addPermission', 0, '', 0, '2023-08-05 00:23:38'),
(1687425877668024322, 1687425761876586498, '修改权限', '/permission/updatePermission', 0, '', 0, '2023-08-05 00:23:47'),
(1687425925790142465, 1687425761876586498, '删除权限', '/permission/delPermission', 0, '', 0, '2023-08-05 00:23:58'),
(1687425966917586946, 1687425761876586498, '添加分组', '/permission/addGroup', 0, '', 0, '2023-08-05 00:24:08'),
(1687426000124940290, 1687425761876586498, '分组分页', '/permission/groupPage', 0, '', 0, '2023-08-05 00:24:16')
ON CONFLICT (id) DO NOTHING;

-- 权限分组表
CREATE TABLE IF NOT EXISTS gateway_permission_group (
    id          bigint NOT NULL,
    group_name  varchar(15) NOT NULL,
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

COMMENT ON TABLE gateway_permission_group IS '权限分组表';
COMMENT ON COLUMN gateway_permission_group.id IS '分组id';
COMMENT ON COLUMN gateway_permission_group.group_name IS '分组名';
COMMENT ON COLUMN gateway_permission_group.create_time IS '创建时间';

INSERT INTO gateway_permission_group (id, group_name, create_time) VALUES
(1652508934101630977, '放权', '2023-04-30 01:29:13'),
(1655736526144806914, '监控', '2023-05-09 02:08:53'),
(1687416601613008898, '用户管理', '2023-08-04 23:47:21'),
(1687416918134075393, '角色管理', '2023-08-04 23:48:36'),
(1687425761876586498, '权限管理', '2023-08-05 00:23:19')
ON CONFLICT (id) DO NOTHING;

-- 请求监控表
CREATE TABLE IF NOT EXISTS gateway_request_monitor (
    id                bigint NOT NULL,
    url_path          varchar(255) NOT NULL,
    status            int NOT NULL,
    response_duration bigint NOT NULL,
    exception_desc    varchar(255) NOT NULL,
    request_time      timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

COMMENT ON TABLE gateway_request_monitor IS '请求监控表';
COMMENT ON COLUMN gateway_request_monitor.id IS '唯一id';
COMMENT ON COLUMN gateway_request_monitor.url_path IS 'url路径';
COMMENT ON COLUMN gateway_request_monitor.status IS '响应状态';
COMMENT ON COLUMN gateway_request_monitor.response_duration IS '响应时长';
COMMENT ON COLUMN gateway_request_monitor.exception_desc IS '异常描述';
COMMENT ON COLUMN gateway_request_monitor.request_time IS '请求时间';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS gateway_role_permission (
    id            bigint NOT NULL,
    role_id       bigint NOT NULL,
    permission_id bigint NOT NULL,
    PRIMARY KEY (id)
);

COMMENT ON TABLE gateway_role_permission IS '角色权限关联表';
COMMENT ON COLUMN gateway_role_permission.id IS '唯一id';
COMMENT ON COLUMN gateway_role_permission.role_id IS '角色id';
COMMENT ON COLUMN gateway_role_permission.permission_id IS '权限id';

INSERT INTO gateway_role_permission (id, role_id, permission_id) VALUES
(1652510271258333186, 1, 1652509001346134017),
(1652590143767207938, 1, 1652590143767207938),
(1655736656925999106, 1, 1655736656925999106),
(1655736673993146369, 1, 1655736673993146369),
(1686199840235286530, 1, 1686199840235286530),
(1686386764905422849, 1, 1686386764905422849),
(1687416688680931329, 1, 1687416688680931329),
(1687416715191365633, 1, 1687416715191365633),
(1687416736441782273, 1, 1687416736441782273),
(1687416766954745857, 1, 1687416766954745857),
(1687416811210264577, 1, 1687416811210264577),
(1687416973747474434, 1, 1687416973747474434),
(1687416997182042113, 1, 1687416997182042113),
(1687417019693838337, 1, 1687417019693838337),
(1687417039714037761, 1, 1687417039714037761),
(1687417100028473345, 1, 1687417100028473345),
(1687425795737088002, 1, 1687425795737088002),
(1687425838362628097, 1, 1687425838362628097),
(1687425877668024322, 1, 1687425877668024322),
(1687425925790142465, 1, 1687425925790142465),
(1687425966917586946, 1, 1687425966917586946),
(1687426000124940290, 1, 1687426000124940290),
(1756102532051693570, 2, 1686386764905422849),
(1756102532051693571, 2, 1686386764905422849),
(1756102532051693572, 2, 1686386764905422849),
(1756102532051693573, 2, 1686386764905422849),
(1756102532051693574, 2, 1686386764905422849),
(1756102532051693575, 2, 1686386764905422849)
ON CONFLICT (id) DO NOTHING;

-- 白名单表
CREATE TABLE IF NOT EXISTS gateway_whitelist (
    id          bigint NOT NULL,
    ip_addr     varchar(255) NOT NULL,
    mac_addr    varchar(50),
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time timestamp NOT NULL DEFAULT '1970-01-01 00:00:00',
    PRIMARY KEY (id)
);

COMMENT ON TABLE gateway_whitelist IS '白名单表';
COMMENT ON COLUMN gateway_whitelist.id IS '唯一id';
COMMENT ON COLUMN gateway_whitelist.ip_addr IS 'ip地址';
COMMENT ON COLUMN gateway_whitelist.mac_addr IS 'mac地址';
COMMENT ON COLUMN gateway_whitelist.create_time IS '创建时间';
COMMENT ON COLUMN gateway_whitelist.update_time IS '修改时间';

INSERT INTO gateway_whitelist (id, ip_addr, mac_addr, create_time, update_time)
VALUES (1, '127.0.0.1', '6C-1F-F7-05-93-84', '2026-06-25 11:20:56', '1970-01-01 00:00:00')
ON CONFLICT (id) DO NOTHING;

-- 登录日志表
CREATE TABLE IF NOT EXISTS t_log (
    id       bigint NOT NULL,
    user_id  varchar(255) NOT NULL,
    username varchar(255) NOT NULL,
    ip       varchar(255) NOT NULL,
    status   smallint NOT NULL,
    msg      varchar(255) NOT NULL,
    time     timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

COMMENT ON TABLE t_log IS '登录日志表';
COMMENT ON COLUMN t_log.id IS '唯一id';
COMMENT ON COLUMN t_log.user_id IS '用户id';
COMMENT ON COLUMN t_log.username IS '用户名';
COMMENT ON COLUMN t_log.ip IS 'ip地址';
COMMENT ON COLUMN t_log.status IS '登录状态';
COMMENT ON COLUMN t_log.msg IS '登录信息';
COMMENT ON COLUMN t_log.time IS '登录时间';

INSERT INTO t_log (id, user_id, username, ip, status, msg, time) VALUES
(1840730109544116225, '1', 'admin', '172.30.1.44', 1, '登录成功', '2024-09-30 21:19:29'),
(1840730178705858562, '1', 'admin', '172.30.1.44', 1, '登录成功', '2024-09-30 21:19:45'),
(1840731196375900161, '1', 'admin', '172.30.1.44', 1, '登录成功', '2024-09-30 21:23:46'),
(1840731225751793665, '1', 'admin', '172.30.1.44', 1, '登录成功', '2024-09-30 21:23:53')
ON CONFLICT (id) DO NOTHING;

-- 角色表
CREATE TABLE IF NOT EXISTS t_role (
    id          bigint NOT NULL,
    role_name   varchar(50) NOT NULL,
    description varchar(255),
    PRIMARY KEY (id)
);

COMMENT ON TABLE t_role IS '角色表';
COMMENT ON COLUMN t_role.id IS '角色id';
COMMENT ON COLUMN t_role.role_name IS '角色名称';
COMMENT ON COLUMN t_role.description IS '描述';

INSERT INTO t_role (id, role_name, description)
VALUES (1, '管理员', NULL), (2, '普通用户', NULL)
ON CONFLICT (id) DO NOTHING;

-- 用户表
CREATE TABLE IF NOT EXISTS t_user (
    id                bigint NOT NULL,
    username          varchar(20) NOT NULL,
    password          varchar(255) NOT NULL,
    phone             varchar(255),
    account_non_locked smallint NOT NULL DEFAULT 1,
    create_time       timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

COMMENT ON TABLE t_user IS '用户表';
COMMENT ON COLUMN t_user.id IS '用户id';
COMMENT ON COLUMN t_user.username IS '用户名';
COMMENT ON COLUMN t_user.password IS '密码';
COMMENT ON COLUMN t_user.phone IS '手机号';
COMMENT ON COLUMN t_user.account_non_locked IS '是否未锁定';
COMMENT ON COLUMN t_user.create_time IS '创建时间';

INSERT INTO t_user (id, username, password, phone, account_non_locked, create_time) VALUES
(1, 'admin', '{bcrypt}$2a$10$zLX9NstaOMno60xyqDWaOupK5KXCPQp1n75GOIoa4JVFO.BrZJWm2', '13333333333', 1, '2024-03-27 15:31:30'),
(2, 'zk', '{bcrypt}$2a$10$0EQexC0XYw58x.ys.Ym8QO3H2Llr0G4wEAFddm8PkOUGy6hQraaui', '14444444444', 1, '2023-10-07 15:04:32')
ON CONFLICT (id) DO NOTHING;

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS user_role (
    id      bigint NOT NULL,
    user_id bigint NOT NULL,
    role_id bigint NOT NULL,
    PRIMARY KEY (id)
);

COMMENT ON TABLE user_role IS '用户角色关联表';
COMMENT ON COLUMN user_role.id IS '唯一id';
COMMENT ON COLUMN user_role.role_id IS '角色id';
COMMENT ON COLUMN user_role.user_id IS '用户id';

INSERT INTO user_role (id, user_id, role_id)
VALUES (1, 1, 1), (2, 2, 2)
ON CONFLICT (id) DO NOTHING;