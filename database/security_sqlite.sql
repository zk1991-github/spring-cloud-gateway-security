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
  `id` bigint(64) NOT NULL,
  `dict_type_id` bigint(64) NOT NULL,
  `dict_val` varchar(255) NOT NULL,
  `dict_name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
);

/*Data for the table `gateway_dict` */

insert  into `gateway_dict`(`id`,`dict_type_id`,`dict_val`,`dict_name`) values (1,1,'0','私有'),(2,1,'1','公开'),(3,1,'2','匿名');

/*Table structure for table `gateway_dict_type` */

DROP TABLE IF EXISTS `gateway_dict_type`;

CREATE TABLE `gateway_dict_type` (
  `id` bigint(64) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
);

/*Data for the table `gateway_dict_type` */

insert  into `gateway_dict_type`(`id`,`name`) values (1,'接口类型');

/*Table structure for table `gateway_permission` */

DROP TABLE IF EXISTS `gateway_permission`;

CREATE TABLE `gateway_permission` (
  `id` bigint(64) NOT NULL,
  `url_name` varchar(255) DEFAULT NULL,
  `url` varchar(255) NOT NULL,
  `open` tinyint(1) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `fixed` tinyint(1) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);

/*Data for the table `gateway_permission` */

insert  into `gateway_permission`(`id`,`url_name`,`url`,`open`,`description`,`fixed`,`create_time`) values (1495637399244447746,'分页查询权限','/queryPermission',0,'分页查询权限',1,'2022-02-21 13:51:59'),(1495637567519924225,'新增权限','/addPermission',0,'新增权限',1,'2022-02-21 13:52:39'),(1495637664693559298,'删除权限','/delPermission',0,'删除权限',1,'2022-02-21 13:53:02'),(1495637762299207682,'修改权限','/updatePermission',0,'修改权限',1,'2022-02-21 13:53:25'),(1495637865344868354,'查询所有角色','/queryAllRoles',0,'查询所有角色',1,'2022-02-21 13:53:50'),(1495637948090097665,'根据角色绑定权限','/bindPermissionByRole',0,'根据角色绑定权限',1,'2022-02-21 13:54:09'),(1495638064385564674,'根据角色id，查询权限','/queryPermissionsByRoleId',0,'根据角色id，查询权限',1,'2022-02-21 13:54:37'),(1495638486122831873,'根据字典类型id，查询字典信息','/queryDictByDictTypeId',1,'根据字典类型id，查询字典信息',1,'2022-02-21 13:56:18'),(1499320522679283714,'批量删除权限','/delPermissions',0,'根据权限id，批量删除权限',1,'2022-03-03 17:47:23'),(1778354083523031042,'生成密码','/passwordGenerator',0,'',1,'2024-04-11 17:27:08'),(1778621402404941825,'分页查询私有权限','/queryPrivatePermission',0,'',1,'2024-04-12 11:09:22'),(1778708514361913345,'清空所有会话','/clearAllSessions',0,'',1,'2024-04-12 16:55:31');

/*Table structure for table `gateway_request_monitor` */

DROP TABLE IF EXISTS `gateway_request_monitor`;

CREATE TABLE `gateway_request_monitor` (
  `id` bigint(64) NOT NULL,
  `url_path` varchar(255) NOT NULL,
  `status` int(4) NOT NULL,
  `response_duration` bigint(64) NOT NULL,
  `exception_desc` varchar(255) NOT NULL,
  `request_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);

/*Data for the table `gateway_request_monitor` */

insert  into `gateway_request_monitor`(`id`,`url_path`,`status`,`response_duration`,`exception_desc`,`request_time`) values (1710602547980406785,'/app',500,2096,'目标服务异常，请稍后重试.','2023-10-07 18:26:41'),(1720274676174827522,'/websocket/websocket.html',500,21072,'目标服务异常，请稍后重试.','2023-11-03 10:59:57'),(1720274962402525185,'/websocket/websocket.html',500,21066,'目标服务异常，请稍后重试.','2023-11-03 11:01:05'),(1720275454629273602,'/websocket/websocket.html',500,21090,'目标服务异常，请稍后重试.','2023-11-03 11:03:03'),(1720275995992285186,'/websocket/websocket.html',500,21011,'目标服务异常，请稍后重试.','2023-11-03 11:05:12'),(1727979136133107714,'/test',500,2078,'目标服务异常，请稍后重试.','2023-11-24 17:15:02'),(1727980193127182337,'/user/queryAllUsersByPage',200,129,'服务正常','2023-11-24 17:19:16');

/*Table structure for table `gateway_role_permission` */

DROP TABLE IF EXISTS `gateway_role_permission`;

CREATE TABLE `gateway_role_permission` (
  `id` bigint(64) NOT NULL,
  `role_id` bigint(64) NOT NULL,
  `permission_id` bigint(64) NOT NULL,
  PRIMARY KEY (`id`)
);

/*Data for the table `gateway_role_permission` */

insert  into `gateway_role_permission`(`id`,`role_id`,`permission_id`) values (1779687659203989506,1,1495637399244447746),(1779687659203989507,1,1495637567519924225),(1779687659203989508,1,1495637664693559298),(1779687659212378113,1,1495637762299207682),(1779687659212378114,1,1495637865344868354),(1779687659212378115,1,1495637948090097665),(1779687659212378116,1,1495638064385564674),(1779687659212378117,1,1499320522679283714),(1779687659212378118,1,1778621402404941825),(1779687659212378120,1,1778354083523031042),(1780782482933678082,2,1495637399244447746),(1780782482954649601,2,1495637567519924225),(1780782482967232514,2,1495637664693559298),(1780782482967232515,2,1495637762299207682),(1780782482967232516,2,1495637865344868354),(1780782482975621121,2,1495637948090097665),(1780782482975621122,2,1495638064385564674),(1780782482975621123,2,1499320522679283714),(1780782482975621124,2,1778621402404941825),(1781198773301657601,1,1778708514361913345),(1781198773301657602,2,1778708514361913345);

/*Table structure for table `t_log` */

DROP TABLE IF EXISTS `t_log`;

CREATE TABLE `t_log` (
  `id` bigint(64) NOT NULL,
  `user_id` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  `ip` varchar(255) NOT NULL,
  `status` tinyint(1) NOT NULL,
  `msg` varchar(255) NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);

/*Data for the table `t_log` */

insert  into `t_log`(`id`,`user_id`,`username`,`ip`,`status`,`msg`,`time`) values (1479021086000984065,'0','superadmin','127.0.0.1',1,'登录成功','2022-01-06 17:24:40'),(1710558306419937282,'0','superadmin','0:0:0:0:0:0:0:1',1,'登录成功','2023-10-07 15:30:55'),(1710592907234250754,'0','superadmin','127.0.0.1',1,'登录成功','2023-10-07 17:48:25'),(1710602428241416193,'0','superadmin','127.0.0.1',1,'登录成功','2023-10-07 18:26:15');

/*Table structure for table `t_role` */

DROP TABLE IF EXISTS `t_role`;

CREATE TABLE `t_role` (
  `id` bigint(64) NOT NULL,
  `role_name` varchar(50) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

/*Data for the table `t_role` */

insert  into `t_role`(`id`,`role_name`,`description`) values (1,'管理员',NULL),(2,'普通用户',NULL);

/*Table structure for table `t_user` */

DROP TABLE IF EXISTS `t_user`;

CREATE TABLE `t_user` (
  `id` bigint(64) NOT NULL,
  `username` varchar(20) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `account_non_locked` tinyint(1) NOT NULL DEFAULT '1',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);

/*Data for the table `t_user` */

insert  into `t_user`(`id`,`username`,`password`,`phone`,`account_non_locked`,`create_time`) values (1,'admin','{bcrypt}$2a$10$zLX9NstaOMno60xyqDWaOupK5KXCPQp1n75GOIoa4JVFO.BrZJWm2','13333333333',1,'2024-03-27 15:31:30'),(2,'zk','{bcrypt}$2a$10$0EQexC0XYw58x.ys.Ym8QO3H2Llr0G4wEAFddm8PkOUGy6hQraaui','14444444444',1,'2023-10-07 15:04:32');

/*Table structure for table `user_role` */

DROP TABLE IF EXISTS `user_role`;

CREATE TABLE `user_role` (
  `id` bigint(64) NOT NULL,
  `user_id` bigint(64) NOT NULL,
  `role_id` bigint(64) NOT NULL,
  PRIMARY KEY (`id`)
);

/*Data for the table `user_role` */

insert  into `user_role`(`id`,`user_id`,`role_id`) values (1,1,1),(2,2,2);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
