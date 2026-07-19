/*
SQLyog 企业版 - MySQL GUI v8.14 
MySQL - 5.6.47 : Database - security_db
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`security_db` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `security_db`;

/*Table structure for table `gateway_dict` */

DROP TABLE IF EXISTS `gateway_dict`;

CREATE TABLE `gateway_dict` (
  `id` bigint(64) NOT NULL COMMENT '唯一id',
  `dict_type_id` bigint(64) NOT NULL COMMENT '字典类型id',
  `dict_val` varchar(255) NOT NULL COMMENT '字典索引',
  `dict_name` varchar(255) NOT NULL COMMENT '字典名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `gateway_dict` */

insert  into `gateway_dict`(`id`,`dict_type_id`,`dict_val`,`dict_name`) values (1,1,'0','私有'),(2,1,'1','公开'),(3,1,'2','匿名');

/*Table structure for table `gateway_dict_type` */

DROP TABLE IF EXISTS `gateway_dict_type`;

CREATE TABLE `gateway_dict_type` (
  `id` bigint(64) NOT NULL COMMENT '唯一id',
  `name` varchar(255) NOT NULL COMMENT '类型名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `gateway_dict_type` */

insert  into `gateway_dict_type`(`id`,`name`) values (1,'接口类型');

/*Table structure for table `gateway_permission` */

DROP TABLE IF EXISTS `gateway_permission`;

CREATE TABLE `gateway_permission` (
  `id` bigint(64) NOT NULL,
  `group_id` bigint(64) NOT NULL COMMENT '组id',
  `url_name` varchar(255) DEFAULT NULL COMMENT '接口名称',
  `url` varchar(255) NOT NULL COMMENT '接口地址',
  `open` tinyint(1) NOT NULL COMMENT '是否公开；0：私有；1：公开；2：匿名',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `fixed` tinyint(1) NOT NULL COMMENT '是否固定；0：非固定；1：固定',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `gateway_permission` */

insert  into `gateway_permission`(`id`,`group_id`,`url_name`,`url`,`open`,`description`,`fixed`,`create_time`) values (2078391084839272450,0,'网关内置接口','/gateway/**',0,'',1,'2026-07-18 16:07:03');

/*Table structure for table `gateway_permission_group` */

DROP TABLE IF EXISTS `gateway_permission_group`;

CREATE TABLE `gateway_permission_group` (
  `id` bigint(64) NOT NULL,
  `group_name` varchar(15) NOT NULL COMMENT '组名',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `gateway_permission_group` */

/*Table structure for table `gateway_request_monitor` */

DROP TABLE IF EXISTS `gateway_request_monitor`;

CREATE TABLE `gateway_request_monitor` (
  `id` bigint(64) NOT NULL,
  `url_path` varchar(255) NOT NULL COMMENT 'url路径',
  `status` int(4) NOT NULL COMMENT '请求状态',
  `response_duration` bigint(64) NOT NULL COMMENT '响应时长',
  `exception_desc` varchar(255) NOT NULL COMMENT '异常描述',
  `request_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '请求时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `gateway_request_monitor` */

insert  into `gateway_request_monitor`(`id`,`url_path`,`status`,`response_duration`,`exception_desc`,`request_time`) values (1710602547980406785,'/app',500,2096,'目标服务异常，请稍后重试.','2023-10-07 18:26:41'),(1720274676174827522,'/websocket/websocket.html',500,21072,'目标服务异常，请稍后重试.','2023-11-03 10:59:57'),(1720274962402525185,'/websocket/websocket.html',500,21066,'目标服务异常，请稍后重试.','2023-11-03 11:01:05'),(1720275454629273602,'/websocket/websocket.html',500,21090,'目标服务异常，请稍后重试.','2023-11-03 11:03:03'),(1720275995992285186,'/websocket/websocket.html',500,21011,'目标服务异常，请稍后重试.','2023-11-03 11:05:12'),(1727979136133107714,'/test',500,2078,'目标服务异常，请稍后重试.','2023-11-24 17:15:02'),(1727980193127182337,'/user/queryAllUsersByPage',200,129,'服务正常','2023-11-24 17:19:16');

/*Table structure for table `gateway_role_permission` */

DROP TABLE IF EXISTS `gateway_role_permission`;

CREATE TABLE `gateway_role_permission` (
  `id` bigint(64) NOT NULL,
  `role_id` bigint(64) NOT NULL COMMENT '角色id',
  `permission_id` bigint(64) NOT NULL COMMENT '权限id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `gateway_role_permission` */

insert  into `gateway_role_permission`(`id`,`role_id`,`permission_id`) values (2078486728362913793,1,2078391084839272450);

/*Table structure for table `gateway_whitelist` */

DROP TABLE IF EXISTS `gateway_whitelist`;

CREATE TABLE `gateway_whitelist` (
  `id` bigint(64) NOT NULL,
  `ip_addr` varchar(255) NOT NULL COMMENT 'ip地址',
  `mac_addr` varchar(50) DEFAULT NULL COMMENT 'mac地址',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `gateway_whitelist` */

insert  into `gateway_whitelist`(`id`,`ip_addr`,`mac_addr`,`create_time`,`update_time`) values (1,'127.0.0.1','6C-1F-F7-05-93-84','2026-06-25 11:20:56','0000-00-00 00:00:00');

/*Table structure for table `t_log` */

DROP TABLE IF EXISTS `t_log`;

CREATE TABLE `t_log` (
  `id` bigint(64) NOT NULL COMMENT '唯一id',
  `user_id` varchar(255) NOT NULL COMMENT '用户id',
  `username` varchar(255) NOT NULL COMMENT '用户名',
  `ip` varchar(255) NOT NULL COMMENT 'ip地址',
  `status` tinyint(1) NOT NULL COMMENT '登录状态',
  `msg` varchar(255) NOT NULL COMMENT '登录信息',
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '登录时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `t_log` */

insert  into `t_log`(`id`,`user_id`,`username`,`ip`,`status`,`msg`,`time`) values (1479021086000984065,'0','superadmin','127.0.0.1',1,'登录成功','2022-01-06 17:24:40'),(1710558306419937282,'0','superadmin','0:0:0:0:0:0:0:1',1,'登录成功','2023-10-07 15:30:55'),(1710592907234250754,'0','superadmin','127.0.0.1',1,'登录成功','2023-10-07 17:48:25'),(1710602428241416193,'0','superadmin','127.0.0.1',1,'登录成功','2023-10-07 18:26:15'),(2072228898540195841,'1','admin','0:0:0:0:0:0:0:1',1,'登录成功','2026-07-01 16:00:44'),(2072229202392354817,'1','admin','0:0:0:0:0:0:0:1',1,'登录成功','2026-07-01 16:01:47');

/*Table structure for table `t_role` */

DROP TABLE IF EXISTS `t_role`;

CREATE TABLE `t_role` (
  `id` bigint(64) NOT NULL,
  `role_name` varchar(50) NOT NULL COMMENT '角色名称',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `t_role` */

insert  into `t_role`(`id`,`role_name`,`description`) values (1,'管理员',NULL),(2,'普通用户',NULL);

/*Table structure for table `t_user` */

DROP TABLE IF EXISTS `t_user`;

CREATE TABLE `t_user` (
  `id` bigint(64) NOT NULL,
  `username` varchar(20) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `phone` varchar(255) DEFAULT NULL COMMENT '手机号',
  `account_non_locked` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否未被锁定',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `t_user` */

insert  into `t_user`(`id`,`username`,`password`,`phone`,`account_non_locked`,`create_time`,`update_time`) values (1,'admin','{bcrypt}$2a$10$zLX9NstaOMno60xyqDWaOupK5KXCPQp1n75GOIoa4JVFO.BrZJWm2','13333333333',1,'2024-03-27 15:31:30',NULL),(2,'zk','{bcrypt}$2a$10$0EQexC0XYw58x.ys.Ym8QO3H2Llr0G4wEAFddm8PkOUGy6hQraaui','14444444444',1,'2023-10-07 15:04:32',NULL);

/*Table structure for table `user_role` */

DROP TABLE IF EXISTS `user_role`;

CREATE TABLE `user_role` (
  `id` bigint(64) NOT NULL,
  `user_id` bigint(64) NOT NULL COMMENT '用户id',
  `role_id` bigint(64) NOT NULL COMMENT '角色id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `user_role` */

insert  into `user_role`(`id`,`user_id`,`role_id`) values (1,1,1),(2,2,2);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
