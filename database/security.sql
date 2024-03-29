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
  `url_name` varchar(255) DEFAULT NULL COMMENT '接口名称',
  `url` varchar(255) NOT NULL COMMENT '接口地址',
  `open` tinyint(1) NOT NULL COMMENT '是否公开；0：私有；1：公开；2：匿名',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `fixed` tinyint(1) NOT NULL COMMENT '是否固定；0：非固定；1：固定',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `gateway_permission` */

insert  into `gateway_permission`(`id`,`url_name`,`url`,`open`,`description`,`fixed`,`create_time`) values (1495637399244447746,'分页查询权限','/queryPermission',0,'分页查询权限',1,'2022-02-21 13:51:59'),(1495637567519924225,'新增权限','/addPermission',0,'新增权限',1,'2022-02-21 13:52:39'),(1495637664693559298,'删除权限','/delPermission',0,'删除权限',1,'2022-02-21 13:53:02'),(1495637762299207682,'修改权限','/updatePermission',0,'修改权限',1,'2022-02-21 13:53:25'),(1495637865344868354,'查询所有角色','/queryAllRoles',0,'查询所有角色',1,'2022-02-21 13:53:50'),(1495637948090097665,'根据角色绑定权限','/bindPermissionByRole',0,'根据角色绑定权限',1,'2022-02-21 13:54:09'),(1495638064385564674,'根据角色id，查询权限','/queryPermissionsByRoleId',0,'根据角色id，查询权限',1,'2022-02-21 13:54:37'),(1495638486122831873,'根据字典类型id，查询字典信息','/queryDictByDictTypeId',1,'根据字典类型id，查询字典信息',1,'2022-02-21 13:56:18'),(1499320522679283714,'批量删除权限','/delPermission',0,'根据权限id，批量删除权限',1,'2022-03-03 17:47:23'),(1706248088114479106,'测试请求修改','/user-update/**',0,'',0,'2023-09-25 18:03:39');

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

insert  into `gateway_request_monitor`(`id`,`url_path`,`status`,`response_duration`,`exception_desc`,`request_time`) values (1710602547980406785,'/app',500,2096,'目标服务异常，请稍后重试.','2023-10-07 18:26:41');

/*Table structure for table `gateway_role_permission` */

DROP TABLE IF EXISTS `gateway_role_permission`;

CREATE TABLE `gateway_role_permission` (
  `id` bigint(64) NOT NULL,
  `role_id` bigint(64) NOT NULL COMMENT '角色id',
  `permission_id` bigint(64) NOT NULL COMMENT '权限id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `gateway_role_permission` */

insert  into `gateway_role_permission`(`id`,`role_id`,`permission_id`) values (1699722707756740609,1,1495637399244447746),(1699722707773517825,1,1495637567519924225),(1699722707773517826,1,1495637664693559298),(1699722707773517827,1,1495637762299207682),(1699722707786100737,1,1495637865344868354),(1699722707786100738,1,1495637948090097665),(1699722707786100739,1,1495638064385564674),(1699722707790295041,1,1499320522679283714),(1708789244576677890,1,1706248088114479106),(1709146513428500481,2,1499320522679283714),(1709146513428500482,2,1495638064385564674);

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

insert  into `t_log`(`id`,`user_id`,`username`,`ip`,`status`,`msg`,`time`) values (1479021086000984065,'0','superadmin','127.0.0.1',1,'登录成功','2022-01-06 17:24:40'),(1710558306419937282,'0','superadmin','0:0:0:0:0:0:0:1',1,'登录成功','2023-10-07 15:30:55'),(1710592907234250754,'0','superadmin','127.0.0.1',1,'登录成功','2023-10-07 17:48:25'),(1710602428241416193,'0','superadmin','127.0.0.1',1,'登录成功','2023-10-07 18:26:15');

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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `t_user` */

insert  into `t_user`(`id`,`username`,`password`,`phone`,`account_non_locked`,`create_time`) values (1,'admin','{bcrypt}$2a$10$zLX9NstaOMno60xyqDWaOupK5KXCPQp1n75GOIoa4JVFO.BrZJWm2','13333333333',1,'2023-10-07 15:04:32'),(2,'zk','{bcrypt}$2a$10$0EQexC0XYw58x.ys.Ym8QO3H2Llr0G4wEAFddm8PkOUGy6hQraaui','14444444444',1,'2023-10-07 15:04:32');

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
