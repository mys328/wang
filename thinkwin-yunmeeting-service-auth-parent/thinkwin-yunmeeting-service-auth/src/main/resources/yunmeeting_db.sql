/*
Navicat MySQL Data Transfer

Source Server         : yunmeeting(52)
Source Server Version : 50718
Source Host           : 10.10.11.52:3306
Source Database       : yunmeeting_db

Target Server Type    : MYSQL
Target Server Version : 50718
File Encoding         : 65001

Date: 2017-08-28 10:40:32
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for sys_mail
-- ----------------------------
DROP TABLE IF EXISTS `sys_mail`;
CREATE TABLE `sys_mail` (
  `id` varchar(50) NOT NULL COMMENT '邮件ID',
  `emm_user_id` varchar(50) DEFAULT NULL COMMENT '用户ID',
  `title` varchar(150) DEFAULT NULL COMMENT '标题',
  `content` text COMMENT '配置内容',
  `src` varchar(128) DEFAULT NULL COMMENT '来源',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `send_time` datetime DEFAULT NULL COMMENT '邮件发送成功后回写的发送时间',
  `status` int(11) DEFAULT NULL COMMENT '发送状态(1:成功;0:失败)',
  `reserve_1` varchar(20) DEFAULT NULL COMMENT '预留字段1',
  `reserve_2` varchar(20) DEFAULT NULL COMMENT '预留字段2',
  `reserve_3` varchar(20) DEFAULT NULL COMMENT '预留字段3',
  PRIMARY KEY (`id`),
  KEY `AK_FK_Reference_4` (`emm_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='系统邮件表';

-- ----------------------------
-- Records of sys_mail
-- ----------------------------

-- ----------------------------
-- Table structure for sys_mail_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_mail_log`;
CREATE TABLE `sys_mail_log` (
  `id` varchar(50) NOT NULL,
  `mail_id` varchar(50) DEFAULT NULL COMMENT '邮件ID',
  `send_params` text COMMENT '邮件发送参数（保存邮件发送时的所有参数）',
  `status` int(11) DEFAULT NULL COMMENT '发送状态(1:成功;0:失败)',
  `send_time` datetime DEFAULT NULL COMMENT '邮件发送成功后回写的发送时间',
  `repeat_send` int(11) DEFAULT NULL COMMENT '重复发送的次数',
  `repeat_send_time` datetime DEFAULT NULL COMMENT '最后一次重复发送的时间',
  `error_msg` varchar(512) DEFAULT NULL COMMENT '发送错误消息',
  `reserve_1` varchar(20) DEFAULT NULL COMMENT '预留字段1',
  `reserve_2` varchar(20) DEFAULT NULL COMMENT '预留字段2',
  `reserve_3` varchar(20) DEFAULT NULL COMMENT '预留字段3',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='邮件发送记录';

-- ----------------------------
-- Records of sys_mail_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id` varchar(50) NOT NULL COMMENT '菜单主键',
  `menu_code` varchar(100) DEFAULT NULL COMMENT '菜单码',
  `menu_name` varchar(100) DEFAULT NULL COMMENT '菜单名称',
  `url` varchar(150) DEFAULT NULL,
  `parent_id` varchar(50) DEFAULT NULL,
  `icon` varchar(256) DEFAULT NULL COMMENT '图标url',
  `descript` varchar(128) DEFAULT NULL,
  `sort_num` int(11) DEFAULT NULL COMMENT '排序号',
  `creater` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `modifyer_id` varchar(50) DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `reserve_1` varchar(20) DEFAULT NULL COMMENT '预留字段1',
  `reserve_2` varchar(20) DEFAULT NULL COMMENT '预留字段2',
  `reserve_3` varchar(20) DEFAULT NULL COMMENT '预留字段3',
  `version_type` varchar(2) DEFAULT NULL COMMENT '版本需求类型（0：免费 1：收费）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES ('1', '', '企业设置', '/gotoEnterpriseSettingPage', '0', '', '', '1', '', '2017-08-01 16:52:04', '', '2017-08-01 16:52:24', '', '', '', '0');
INSERT INTO `sys_menu` VALUES ('2', '', '通讯录管理', '/gotoAddressListPage', '0', '', '', '2', '', '2017-08-01 16:52:06', '', '2017-08-01 16:52:27', '', '', '', '0');
INSERT INTO `sys_menu` VALUES ('3', '', '会议室管理', '/meetingRoom/skipMeetingRoom', '0', '', '', '3', '', '2017-08-01 16:52:09', '', '2017-08-01 16:52:30', '', '', '', '0');
INSERT INTO `sys_menu` VALUES ('4', '', '订单管理', '/commodity/orderManger', '0', '', '', '4', '', '2017-08-01 16:52:13', '', '2017-08-01 16:52:32', '', '', '', '0');
INSERT INTO `sys_menu` VALUES ('5', '', '统计分析', 'http://www.w3school.com.cn/', '0', '', '', '5', '', '2017-08-01 16:52:16', '', '2017-08-01 16:52:34', '', '', '', '0');
INSERT INTO `sys_menu` VALUES ('6', '', '操作日志', '/log/operationLog', '0', '', '', '6', '', '2017-08-01 16:52:18', '', '2017-08-01 16:52:36', '', '', '', '0');

-- ----------------------------
-- Table structure for sys_organization
-- ----------------------------
DROP TABLE IF EXISTS `sys_organization`;
CREATE TABLE `sys_organization` (
  `id` varchar(50) NOT NULL,
  `org_code` varchar(100) DEFAULT NULL COMMENT '组织编号',
  `org_name` varchar(100) DEFAULT NULL COMMENT '组织名称',
  `org_name_pinyin` varchar(100) DEFAULT NULL COMMENT '组织机构拼音',
  `org_nick_name` varchar(50) DEFAULT NULL COMMENT '组织昵称',
  `parent_id` varchar(50) DEFAULT NULL COMMENT '如果是没有父级该值默认为“0”',
  `org_type` int(11) DEFAULT NULL COMMENT '1:机构;2:部门;3:工作组',
  `org_latitude` int(11) DEFAULT NULL COMMENT '适用于机构[1:行政组织;2:账务组织;3:法人组织;4:职能组织]',
  `status` int(11) DEFAULT NULL COMMENT '有效：1；无效：0',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `compositor` int(11) DEFAULT '0' COMMENT '排序号',
  `reserve_1` varchar(20) DEFAULT NULL COMMENT '预留字段1',
  `reserve_2` varchar(20) DEFAULT NULL COMMENT '预留字段2',
  `reserve_3` varchar(20) DEFAULT NULL COMMENT '预留字段3',
  PRIMARY KEY (`id`),
  KEY `AK_idparentId` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='组织机构表';

-- ----------------------------
-- Records of sys_organization
-- ----------------------------

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
  `permission_id` varchar(50) NOT NULL COMMENT '权限主键id',
  `org_code` varchar(100) DEFAULT NULL,
  `org_name` varchar(100) DEFAULT NULL COMMENT '配置名称',
  `parent_id` varchar(50) DEFAULT NULL,
  `url` varchar(150) DEFAULT NULL,
  `display` int(11) DEFAULT NULL COMMENT '选择权限时,是否显示该权限 2:不显示, 1显示',
  `sort_number` int(11) DEFAULT NULL COMMENT '显示顺序',
  `descritp` varchar(128) DEFAULT NULL COMMENT '权限描述',
  `status` int(11) DEFAULT NULL COMMENT '有效：1；无效：0',
  `creater_id` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `modifyer_id` varchar(50) DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `reserve_1` varchar(20) DEFAULT NULL COMMENT '预留字段1',
  `reserve_2` varchar(20) DEFAULT NULL COMMENT '预留字段2',
  `reserve_3` varchar(20) DEFAULT NULL COMMENT '预留字段3',
  PRIMARY KEY (`permission_id`),
  KEY `AK_IDX_SP_DISPLAY` (`display`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_permission
-- ----------------------------
INSERT INTO `sys_permission` VALUES ('1', null, null, null, '/index.do', null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_permission` VALUES ('2', null, null, null, '/search', null, null, null, null, null, null, null, null, null, null, null);

-- ----------------------------
-- Table structure for sys_push
-- ----------------------------
DROP TABLE IF EXISTS `sys_push`;
CREATE TABLE `sys_push` (
  `id` varchar(50) NOT NULL COMMENT '邮件ID',
  `emm_user_id` varchar(50) DEFAULT NULL COMMENT '用户ID',
  `title` varchar(150) DEFAULT NULL COMMENT '标题',
  `content` text COMMENT '配置内容',
  `src` varchar(128) DEFAULT NULL COMMENT '来源',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `send_time` datetime DEFAULT NULL COMMENT '邮件发送成功后回写的发送时间',
  `status` int(11) DEFAULT NULL COMMENT '发送状态(1:成功;0:失败)',
  `reserve_1` varchar(20) DEFAULT NULL COMMENT '预留字段1',
  `reserve_2` varchar(20) DEFAULT NULL COMMENT '预留字段2',
  `reserve_3` varchar(20) DEFAULT NULL COMMENT '预留字段3',
  PRIMARY KEY (`id`),
  KEY `AK_FK_Reference_4` (`emm_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='系统推送表';

-- ----------------------------
-- Records of sys_push
-- ----------------------------

-- ----------------------------
-- Table structure for sys_push_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_push_log`;
CREATE TABLE `sys_push_log` (
  `id` varchar(50) NOT NULL,
  `mail_id` varchar(50) DEFAULT NULL COMMENT '邮件ID',
  `send_params` text COMMENT '邮件发送参数（保存邮件发送时的所有参数）',
  `status` int(11) DEFAULT NULL COMMENT '发送状态(1:成功;0:失败)',
  `send_time` datetime DEFAULT NULL COMMENT '邮件发送成功后回写的发送时间',
  `repeat_send` int(11) DEFAULT NULL COMMENT '重复发送的次数',
  `repeat_send_time` datetime DEFAULT NULL COMMENT '最后一次重复发送的时间',
  `error_msg` varchar(512) DEFAULT NULL COMMENT '发送错误消息',
  `reserve_1` varchar(20) DEFAULT NULL COMMENT '预留字段1',
  `reserve_2` varchar(20) DEFAULT NULL COMMENT '预留字段2',
  `reserve_3` varchar(20) DEFAULT NULL COMMENT '预留字段3',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='邮件发送记录';

-- ----------------------------
-- Records of sys_push_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `role_id` varchar(50) NOT NULL COMMENT '角色主键id',
  `role_name` varchar(150) DEFAULT NULL COMMENT '角色名称',
  `org_code` varchar(100) DEFAULT NULL,
  `org_name` varchar(100) DEFAULT NULL COMMENT '配置名称',
  `descript` varchar(128) DEFAULT NULL,
  `sort_num` int(11) DEFAULT NULL COMMENT '排序号',
  `is_system` int(11) DEFAULT '0' COMMENT '是否为系统默认角色, 1:是; 2:否',
  `creater_id` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `modifyer` varchar(50) DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `reserve_1` varchar(20) DEFAULT NULL COMMENT '预留字段1',
  `reserve_2` varchar(20) DEFAULT NULL COMMENT '预留字段2',
  `reserve_3` varchar(20) DEFAULT NULL COMMENT '预留字段3',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES ('1', '系统管理员', null, '', null, null, '1', '', '2017-06-20 09:15:23', null, null, null, null, null);
INSERT INTO `sys_role` VALUES ('2', '管理员', null, '', null, null, '1', '', '2017-07-04 16:40:39', null, null, null, null, null);
INSERT INTO `sys_role` VALUES ('3', '会议室管理员', null, null, null, null, '1', null, '2017-07-06 15:49:07', null, null, null, null, null);
INSERT INTO `sys_role` VALUES ('4', '会议室预订专员', null, null, null, null, '1', null, '2017-07-06 15:49:47', null, null, null, null, null);
INSERT INTO `sys_role` VALUES ('99', '普通员工', null, '', null, null, '1', null, '2017-06-29 11:03:36', null, null, null, null, null);

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `id` varchar(50) NOT NULL COMMENT '主键',
  `menu_id` varchar(50) DEFAULT NULL COMMENT '菜单主键ID',
  `role_id` varchar(50) DEFAULT NULL COMMENT '角色主键ID',
  `creater` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `modifyer` varchar(50) DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES ('1', '1', '1', null, '2017-08-11 10:01:19', null, '2017-08-11 10:01:22');
INSERT INTO `sys_role_menu` VALUES ('10', '4', '2', null, '2017-08-11 10:01:26', null, '2017-08-11 10:01:24');
INSERT INTO `sys_role_menu` VALUES ('11', '5', '2', null, '2017-08-11 10:01:27', null, '2017-08-11 10:01:29');
INSERT INTO `sys_role_menu` VALUES ('12', '6', '2', null, '2017-08-11 10:01:32', null, '2017-08-11 10:01:30');
INSERT INTO `sys_role_menu` VALUES ('13', '3', '3', null, '2017-08-11 10:01:35', null, '2017-08-11 10:01:37');
INSERT INTO `sys_role_menu` VALUES ('14', '6', '3', null, '2017-08-11 10:01:41', null, '2017-08-11 10:01:39');
INSERT INTO `sys_role_menu` VALUES ('2', '2', '1', null, '2017-08-11 10:01:43', null, '2017-08-11 10:01:44');
INSERT INTO `sys_role_menu` VALUES ('3', '3', '1', null, '2017-08-11 10:01:48', null, '2017-08-11 10:01:46');
INSERT INTO `sys_role_menu` VALUES ('4', '4', '1', null, '2017-08-11 10:01:50', null, '2017-08-11 10:01:52');
INSERT INTO `sys_role_menu` VALUES ('5', '5', '1', null, '2017-08-11 10:01:56', null, '2017-08-11 10:01:54');
INSERT INTO `sys_role_menu` VALUES ('6', '6', '1', null, '2017-08-11 10:01:58', null, '2017-08-11 10:02:00');
INSERT INTO `sys_role_menu` VALUES ('7', '1', '2', null, '2017-08-11 10:02:05', null, '2017-08-11 10:02:03');
INSERT INTO `sys_role_menu` VALUES ('8', '2', '2', null, '2017-08-11 10:02:07', null, '2017-08-11 10:02:08');
INSERT INTO `sys_role_menu` VALUES ('9', '3', '2', null, '2017-08-11 10:02:12', null, '2017-08-11 10:02:11');

-- ----------------------------
-- Table structure for sys_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
  `id` varchar(50) NOT NULL COMMENT '主键',
  `role_id` varchar(50) DEFAULT NULL COMMENT '角色主键ID',
  `permission_id` varchar(50) DEFAULT NULL COMMENT '权限主键ID',
  `creater_id` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `modifyer_id` varchar(50) DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_role_permission
-- ----------------------------
INSERT INTO `sys_role_permission` VALUES ('1', '1', '1', null, null, null, null);
INSERT INTO `sys_role_permission` VALUES ('2', '2', '1', null, null, null, null);
INSERT INTO `sys_role_permission` VALUES ('3', '3', '1', null, null, null, null);
INSERT INTO `sys_role_permission` VALUES ('4', '4', '1', null, null, null, null);
INSERT INTO `sys_role_permission` VALUES ('5', '99', '1', null, null, null, null);
INSERT INTO `sys_role_permission` VALUES ('6', '1', '2', null, null, null, null);

-- ----------------------------
-- Table structure for sys_setting
-- ----------------------------
DROP TABLE IF EXISTS `sys_setting`;
CREATE TABLE `sys_setting` (
  `id` varchar(50) NOT NULL COMMENT '系统配置ID',
  `tenant_code` varchar(30) DEFAULT NULL COMMENT '租户CODE',
  `platform_type` varchar(50) DEFAULT NULL COMMENT 'CM:1；乐会:2;企云会:3',
  `org_name` varchar(100) DEFAULT NULL COMMENT '配置名称',
  `setting_key` varchar(50) NOT NULL COMMENT '配置标识',
  `content` text COMMENT '配置内容',
  `creater_id` varchar(50) DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modifyer_id` varchar(50) DEFAULT NULL COMMENT '修改人ID',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `status` int(11) DEFAULT NULL COMMENT '有效:1 ；无效:0',
  `reserve_1` varchar(20) DEFAULT NULL COMMENT '预留字段1',
  `reserve_2` varchar(20) DEFAULT NULL COMMENT '预留字段2',
  `reserve_3` varchar(20) DEFAULT NULL COMMENT '预留字段3',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='系统统一配置表';

-- ----------------------------
-- Records of sys_setting
-- ----------------------------
INSERT INTO `sys_setting` VALUES ('a5d9889d47bf4f9eb623a181138068ca', null, null, null, 'base.ftl', 'PCNtYWNybyBwYWdlX2hlYWQ+CiAgPHRpdGxlPlBhZ2UgdGl0bGUhPC90aXRsZT4KICA8bGluayByZWw9InN0eWxlc2hlZXQiIGhyZWY9Ii9jc3MvZGVmYXVsdC5jc3MiPgo8LyNtYWNybz4KCjwjbWFjcm8gcGFnZV9ib2R5PgogIDxoMT5CYXNpYyBQYWdlPC9oMT4KICA8cD5UaGlzIGlzIHRoZSBib2R5IG9mIHRoZSBwYWdlITwvcD4KPC8jbWFjcm8+Cgo8I21hY3JvIGRpc3BsYXlfcGFnZT4KICA8IWRvY3R5cGUgaHRtbD4KICA8aHRtbD4KICAgIDxoZWFkPiAKICAgICAgPEBwYWdlX2hlYWQvPiAKICAgICAgPHNjcmlwdCB0eXBlPSJ0ZXh0L2phdmFzY3JpcHQiIHNyYz0iL2pzL3RyYWNraW5nLmpzIj48L3NjcmlwdD4KICAgIDwvaGVhZD4KICAgIDxib2R5PgogICAgICA8ZGl2IGlkPSJuYXYiPjxhIGhyZWY9Ii8iPkhvbWU8L2E+PC9kaXY+CiAgICAgIDxAcGFnZV9ib2R5Lz4KICAgIDwvYm9keT4KICA8L2h0bWw+CjwvI21hY3JvPg==', null, '2017-06-16 10:29:57', null, null, null, null, null, null);
INSERT INTO `sys_setting` VALUES ('a5d9889d47bf4f9eb623a181138068cd', null, null, null, 'meeting.ftl', 'PCNpbmNsdWRlICJiYXNlLmZ0bCI+CjwjbWFjcm8gcGFnZV9oZWFkPgogIDx0aXRsZT5Vc2VyIHBhZ2U8L3RpdGxlPgogIDxzY3JpcHQgc3JjPSIvanMvdXNlci5taW4uanMiPjwvc2NyaXB0Pgo8LyNtYWNybz4KCjwjbWFjcm8gcGFnZV9ib2R5Pgo8cD48Yj5EZWFyIDxhIGhyZWY9Im1haWx0bzoke3BlcnNvbi5lbWFpbH0iPiR7cGVyc29uLm5hbWV9PC9hPiw8L2I+PC9wPgo8cD5UaGlzIGlzIGFuIGV4YW1wbGUgPGk+SFRNTDwvaT4gZW1haWwgc2VudCBieSAke3ZlcnNpb259IGFuZCBGcmVlTWFya2VyLjwvcD4KPHA+PGltZyBzcmM9IiR7bWFpbENvbnRleHQuaW5zZXJ0KCJzZWFtTG9nby5wbmciKX0iIC8+PC9wPgo8cD5JdCBoYXMgYW4gYWx0ZXJuYXRpdmUgdGV4dCBib2R5IGZvciBtYWlsIHJlYWRlcnMgdGhhdCBkb24ndCBzdXBwb3J0IGh0bWwuPC9wPgo8LyNtYWNybz4KCjxAZGlzcGxheV9wYWdlLz4=', null, '2017-06-16 10:30:44', null, null, null, null, null, null);
INSERT INTO `sys_setting` VALUES ('a5d9889d47bf4f9eb623a181138068ce', '', '', '', 'smtp.config', '{\"host\":\"smtp.qq.com\",\"port\":\"465\",\"ssl\":true,\"username\":\"1406693536@qq.com\",\"pwd\":\"jazymwinuykqijjg\",\"nickname\":\"zhanglei\"}', '', '2017-06-16 10:30:44', '', '2017-06-20 16:05:52', null, '', '', '');

-- ----------------------------
-- Table structure for sys_sms
-- ----------------------------
DROP TABLE IF EXISTS `sys_sms`;
CREATE TABLE `sys_sms` (
  `id` varchar(50) NOT NULL COMMENT '邮件ID',
  `emm_user_id` varchar(50) DEFAULT NULL COMMENT '用户ID',
  `title` varchar(150) DEFAULT NULL COMMENT '标题',
  `content` text COMMENT '配置内容',
  `src` varchar(128) DEFAULT NULL COMMENT '来源',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `send_time` datetime DEFAULT NULL COMMENT '邮件发送成功后回写的发送时间',
  `status` int(11) DEFAULT NULL COMMENT '发送状态(1:成功;0:失败)',
  `reserve_1` varchar(20) DEFAULT NULL COMMENT '预留字段1',
  `reserve_2` varchar(20) DEFAULT NULL COMMENT '预留字段2',
  `reserve_3` varchar(20) DEFAULT NULL COMMENT '预留字段3',
  PRIMARY KEY (`id`),
  KEY `AK_FK_Reference_4` (`emm_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='系统短信表';

-- ----------------------------
-- Records of sys_sms
-- ----------------------------

-- ----------------------------
-- Table structure for sys_sms_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_sms_log`;
CREATE TABLE `sys_sms_log` (
  `id` varchar(50) NOT NULL,
  `mail_id` varchar(50) DEFAULT NULL COMMENT '邮件ID',
  `send_params` text COMMENT '邮件发送参数（保存邮件发送时的所有参数）',
  `status` int(11) DEFAULT NULL COMMENT '发送状态(1:成功;0:失败)',
  `send_time` datetime DEFAULT NULL COMMENT '邮件发送成功后回写的发送时间',
  `repeat_send` int(11) DEFAULT NULL COMMENT '重复发送的次数',
  `repeat_send_time` datetime DEFAULT NULL COMMENT '最后一次重复发送的时间',
  `error_msg` varchar(512) DEFAULT NULL COMMENT '发送错误消息',
  `reserve_1` varchar(20) DEFAULT NULL COMMENT '预留字段1',
  `reserve_2` varchar(20) DEFAULT NULL COMMENT '预留字段2',
  `reserve_3` varchar(20) DEFAULT NULL COMMENT '预留字段3',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='系统推送logger';

-- ----------------------------
-- Records of sys_sms_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` varchar(50) NOT NULL COMMENT '用户Id',
  `phone_number` varchar(11) DEFAULT NULL COMMENT '手机号',
  `user_name` varchar(50) DEFAULT NULL COMMENT '用户名称',
  `user_name_pinyin` varchar(100) DEFAULT NULL COMMENT '用户名拼音',
  `email` varchar(50) DEFAULT NULL COMMENT '已绑定邮箱',
  `wechat` varchar(100) DEFAULT NULL COMMENT '已绑定微信号',
  `org_id` varchar(50) DEFAULT NULL COMMENT '组织机构Id',
  `org_name` varchar(100) DEFAULT NULL COMMENT '组织机构名称',
  `position` varchar(100) DEFAULT NULL COMMENT '职位',
  `tenant_id` varchar(50) DEFAULT NULL COMMENT '租户Id',
  `sex` int(11) DEFAULT NULL COMMENT '性别(0:代表男 1:代表女)',
  `photo` varchar(255) DEFAULT NULL COMMENT '用户头像源路径',
  `centralGraph` varchar(255) DEFAULT NULL COMMENT '用户头像中图',
  `thumbnails` varchar(255) DEFAULT NULL COMMENT '用户头像小图',
  `birthday` datetime DEFAULT NULL COMMENT '生日',
  `address` varchar(255) DEFAULT NULL COMMENT '居住地址',
  `status` int(11) DEFAULT NULL COMMENT '人员状态：1.已激活、2.未激活、3.已禁用、4.未分配部门、89.离职、',
  `creater` varchar(50) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modifyer` varchar(50) DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `device_token` varchar(50) DEFAULT NULL COMMENT '设备token  手机唯一标识 web端可为空',
  `device_type` int(11) DEFAULT NULL COMMENT '设备类型 0其他 1:IOS 2:Android 3:pc',
  `reserve_1` varchar(20) DEFAULT NULL COMMENT '预留字段1',
  `reserve_2` varchar(20) DEFAULT NULL COMMENT '预留字段2',
  `reserve_3` varchar(20) DEFAULT NULL COMMENT '预留字段3',
  `user_number` varchar(20) DEFAULT NULL COMMENT '员工编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='系统用户表';

-- ----------------------------
-- Records of sys_user
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_permission`;
CREATE TABLE `sys_user_permission` (
  `id` varchar(50) NOT NULL COMMENT '主键',
  `user_id` varchar(50) DEFAULT NULL COMMENT '用户的主键ID',
  `permission_id` varchar(50) DEFAULT NULL COMMENT '权限主键ID',
  `creater_id` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `modifyer_id` varchar(50) DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_user_permission
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id` varchar(50) NOT NULL COMMENT '主键',
  `creater_id` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `modifyer_id` varchar(50) DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `user_id` varchar(50) DEFAULT NULL COMMENT '用户的主键ID',
  `role_id` varchar(50) DEFAULT NULL COMMENT '角色主键ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------

-- ----------------------------
-- Table structure for yummeeting_conference_room_middle
-- ----------------------------
DROP TABLE IF EXISTS `yummeeting_conference_room_middle`;
CREATE TABLE `yummeeting_conference_room_middle` (
  `id` varchar(50) NOT NULL,
  `confreren_id` varchar(50) DEFAULT NULL,
  `room_id` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `AK_Identifier_2` (`confreren_id`,`room_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='会议_会议室中间表';

-- ----------------------------
-- Records of yummeeting_conference_room_middle
-- ----------------------------

-- ----------------------------
-- Table structure for yuncm_device_service
-- ----------------------------
DROP TABLE IF EXISTS `yuncm_device_service`;
CREATE TABLE `yuncm_device_service` (
  `id` varchar(50) NOT NULL,
  `name` varchar(100) DEFAULT NULL COMMENT '设备服务名称',
  `creater_id` varchar(50) DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modifyer_id` varchar(50) DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `state` varchar(2) DEFAULT NULL COMMENT '业务状态:1:启用；0:停用',
  `delete_state` varchar(2) DEFAULT NULL COMMENT '记录状态:1:已删除；0:未删除',
  `reserve_1` varchar(20) DEFAULT NULL COMMENT '预留字段1',
  `reserve_3` varchar(20) DEFAULT NULL COMMENT '预留字段3',
  `reserve_2` varchar(20) DEFAULT NULL COMMENT '预留字段2',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='设备服务';

-- ----------------------------
-- Records of yuncm_device_service
-- ----------------------------
INSERT INTO `yuncm_device_service` VALUES ('001', '白板', null, null, null, null, '1', '0', null, null, null);
INSERT INTO `yuncm_device_service` VALUES ('002', '视频会议', null, null, null, null, '1', '0', null, null, null);
INSERT INTO `yuncm_device_service` VALUES ('003', '显示', null, null, null, null, '1', '0', null, null, null);
INSERT INTO `yuncm_device_service` VALUES ('004', '扩音', null, null, null, null, '1', '0', null, null, null);

-- ----------------------------
-- Table structure for yuncm_meeting_room
-- ----------------------------
DROP TABLE IF EXISTS `yuncm_meeting_room`;
CREATE TABLE `yuncm_meeting_room` (
  `id` varchar(50) NOT NULL,
  `name` varchar(200) DEFAULT NULL COMMENT '会议室名称',
  `persion_number` int(11) DEFAULT NULL COMMENT '可容纳人数',
  `location` varchar(200) DEFAULT NULL COMMENT '会议室位置',
  `image_url` varchar(200) DEFAULT NULL COMMENT '会议室图片',
  `qr_code_url` varchar(200) DEFAULT NULL COMMENT '会议室二维码URL',
  `area_id` varchar(50) DEFAULT NULL COMMENT '区域ID',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  `admin_id` varchar(50) DEFAULT NULL COMMENT '管理员ID',
  `is_audit` varchar(2) DEFAULT NULL COMMENT '0:无须审核 ；1:需要审核',
  `start_time` datetime DEFAULT NULL COMMENT '会议开始日期',
  `end_time` datetime DEFAULT NULL COMMENT '会议结束日期',
  `creater_id` varchar(50) DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modifyer_id` varchar(50) DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `oper_reason` varchar(200) DEFAULT NULL COMMENT '操作原因',
  `state` varchar(2) DEFAULT NULL COMMENT '会议室状态:不能为空1－永久关闭;2－正常开启;;3－临时关闭',
  `delete_state` varchar(2) DEFAULT NULL COMMENT '记录状态:1:已删除；0:未删除',
  `global_sort` int(11) DEFAULT NULL COMMENT '全局排序用于全部区域排序',
  `reserve_1` varchar(20) DEFAULT NULL COMMENT '预留字段1',
  `reserve_3` varchar(20) DEFAULT NULL COMMENT '预留字段3',
  `reserve_2` varchar(20) DEFAULT NULL COMMENT '预留字段2',
  PRIMARY KEY (`id`),
  KEY `AK_Identifier_2` (`area_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='会议室';

-- ----------------------------
-- Records of yuncm_meeting_room
-- ----------------------------

-- ----------------------------
-- Table structure for yuncm_reserve_config
-- ----------------------------
DROP TABLE IF EXISTS `yuncm_reserve_config`;
CREATE TABLE `yuncm_reserve_config` (
  `id` varchar(50) NOT NULL COMMENT '主键',
  `role_id` varchar(50) DEFAULT NULL COMMENT '角色ID',
  `role_name` varchar(100) DEFAULT NULL COMMENT '角色名称，会议室管理员，会议室预定专员',
  `delete_state` varchar(2) DEFAULT NULL COMMENT '记录状态:1:已删除；0:未删除',
  `creater_id` varchar(50) DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modifyer_id` varchar(50) DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `reserve_1` varchar(20) DEFAULT NULL COMMENT '预留字段1',
  `reserve_3` varchar(20) DEFAULT NULL COMMENT '预留字段3',
  `reserve_2` varchar(20) DEFAULT NULL COMMENT '预留字段2',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='会议室预订角色配置表，配置有哪些角色可供选择;用于预计角色选择列表';

-- ----------------------------
-- Records of yuncm_reserve_config
-- ----------------------------
INSERT INTO `yuncm_reserve_config` VALUES ('1000', '3', '会议室管理员', '0', null, null, null, null, null, null, null);
INSERT INTO `yuncm_reserve_config` VALUES ('1001', '4', '会议室预定专员', '0', null, null, null, null, null, null, null);

-- ----------------------------
-- Table structure for yuncm_room_area
-- ----------------------------
DROP TABLE IF EXISTS `yuncm_room_area`;
CREATE TABLE `yuncm_room_area` (
  `id` varchar(50) NOT NULL COMMENT '主键',
  `pid` varchar(50) DEFAULT NULL COMMENT '区域父ID',
  `name` varchar(50) DEFAULT NULL COMMENT '区域名称',
  `is_default` varchar(2) DEFAULT NULL COMMENT '是否默认区域。1：是；0：否',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  `creater_id` varchar(50) DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modifyer_id` varchar(50) DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `delete_state` char(10) DEFAULT NULL COMMENT '记录状态:1:已删除；0:未删除',
  `reserve_1` varchar(20) DEFAULT NULL COMMENT '预留字段1',
  `reserve_3` varchar(20) DEFAULT NULL COMMENT '预留字段3',
  `reserve_2` varchar(20) DEFAULT NULL COMMENT '预留字段2',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='会议室区域';

-- ----------------------------
-- Records of yuncm_room_area
-- ----------------------------

-- ----------------------------
-- Table structure for yuncm_room_reserve_auth
-- ----------------------------
DROP TABLE IF EXISTS `yuncm_room_reserve_auth`;
CREATE TABLE `yuncm_room_reserve_auth` (
  `id` varchar(50) NOT NULL COMMENT '主键',
  `meeting_root_id` varchar(50) DEFAULT NULL COMMENT '会议室ID',
  `role_id` varchar(50) DEFAULT NULL COMMENT '角色ID',
  `creater_id` varchar(50) DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modifyer_id` varchar(50) DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `AK_Identifier_2` (`meeting_root_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='会议室预订权限';

-- ----------------------------
-- Records of yuncm_room_reserve_auth
-- ----------------------------

-- ----------------------------
-- Table structure for yuncm_room_reserve_conf
-- ----------------------------
DROP TABLE IF EXISTS `yuncm_room_reserve_conf`;
CREATE TABLE `yuncm_room_reserve_conf` (
  `id` varchar(50) NOT NULL COMMENT '主键',
  `reserve_time_start` datetime DEFAULT NULL COMMENT '可预订时间开始',
  `reserve_time_end` datetime DEFAULT NULL COMMENT '可预订时间结束',
  `meeting_maximum` int(11) DEFAULT NULL COMMENT '单次会议最大时长 0：为不限制',
  `meeting_minimum` int(11) DEFAULT NULL COMMENT '单次会议最小时长 0：为不限制',
  `reserve_cycle` int(11) DEFAULT NULL COMMENT '会议室可预订周期',
  `may_start_early` int(11) DEFAULT NULL COMMENT '会议可提前开始时间',
  `release_settings` varchar(50) DEFAULT NULL COMMENT '会议释放设置',
  `creater_id` varchar(50) DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modifyer_id` varchar(50) DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `delete_state` char(10) DEFAULT NULL COMMENT '记录状态:1:已删除；0:未删除',
  `reserve_1` varchar(20) DEFAULT NULL COMMENT '预留字段1',
  `reserve_3` varchar(20) DEFAULT NULL COMMENT '预留字段3',
  `reserve_2` varchar(20) DEFAULT NULL COMMENT '预留字段2',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='会议室预订配置';

-- ----------------------------
-- Records of yuncm_room_reserve_conf
-- ----------------------------
INSERT INTO `yuncm_room_reserve_conf` VALUES ('0001', '2000-12-30 06:00:00', '2000-12-30 23:59:00', '0', '10', '0', null, null, null, null, null, null, null, null, null, null);

-- ----------------------------
-- Table structure for yunmc_room_device_service
-- ----------------------------
DROP TABLE IF EXISTS `yunmc_room_device_service`;
CREATE TABLE `yunmc_room_device_service` (
  `id` varchar(50) NOT NULL COMMENT '主键',
  `meeting_root_id` varchar(50) DEFAULT NULL COMMENT '会议室ID',
  `device_service_id` varchar(50) DEFAULT NULL COMMENT '会议设备服务ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='会议室_设备服务中间表';

-- ----------------------------
-- Records of yunmc_room_device_service
-- ----------------------------

-- ----------------------------
-- Table structure for yunmeeting_conference
-- ----------------------------
DROP TABLE IF EXISTS `yunmeeting_conference`;
CREATE TABLE `yunmeeting_conference` (
  `id` varchar(50) NOT NULL COMMENT '主键',
  `conference_name` varchar(100) DEFAULT NULL COMMENT '会议名称',
  `conterence_content` text COMMENT '会议内容',
  `client_type` varchar(2) DEFAULT NULL COMMENT '会议来源  0－WEB端；1－微信端；2－APP端；3－信息发布屏',
  `reservation_person_id` varchar(50) DEFAULT NULL COMMENT '预订人Id',
  `organizer_id` varchar(50) DEFAULT NULL COMMENT '组织者id',
  `take_start_date` datetime DEFAULT NULL COMMENT '预计开始时间',
  `take_end_date` datetime DEFAULT NULL COMMENT '预计结束时间',
  `confrerence_create_time` datetime DEFAULT NULL COMMENT '会议创建时间',
  `confrerence_cancel_time` datetime DEFAULT NULL COMMENT '会议取消时间',
  `act_start_time` datetime DEFAULT NULL COMMENT '实际开始时间',
  `act_end_time` datetime DEFAULT NULL COMMENT '实际结束时间',
  `reservation_success_time` datetime DEFAULT NULL COMMENT '预约成功时间',
  `ext_data` text COMMENT '扩展数据',
  `is_public` varchar(2) DEFAULT NULL COMMENT '是否公开  0:不公开；1:公开',
  `is_audit` varchar(2) DEFAULT NULL COMMENT '是否需要审核  0:不需要审核；1:需要审核',
  `host_unit` varchar(200) DEFAULT NULL COMMENT '主办单位',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  `creater_id` varchar(50) DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modifyer_id` varchar(50) DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `modify_reason` varchar(500) DEFAULT NULL COMMENT '变更原因',
  `state` varchar(2) DEFAULT NULL COMMENT '会议状态:0:审核未通过;1:待审核； 2:未开始；3:进行中；4:已结束；5：已取消',
  `delete_state` varchar(2) DEFAULT NULL COMMENT '记录状态:1:已删除；0:未删除',
  `cancel_state` varchar(2) DEFAULT NULL COMMENT '取消状态：1:已取消；0:未取消',
  `cancel_reason` varchar(500) DEFAULT NULL COMMENT '取消原因',
  `reserve_1` varchar(20) DEFAULT NULL COMMENT '预留字段1',
  `reserve_3` varchar(20) DEFAULT NULL COMMENT '预留字段3',
  `reserve_2` varchar(20) DEFAULT NULL COMMENT '预留字段2',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='会议(预订)表';

-- ----------------------------
-- Records of yunmeeting_conference
-- ----------------------------

-- ----------------------------
-- Table structure for yunmeeting_conference_audit
-- ----------------------------
DROP TABLE IF EXISTS `yunmeeting_conference_audit`;
CREATE TABLE `yunmeeting_conference_audit` (
  `id` varchar(50) NOT NULL DEFAULT '' COMMENT '主键',
  `audit_annotations` varchar(50) DEFAULT NULL COMMENT '审核批注',
  `act_auditor` varchar(50) DEFAULT NULL COMMENT '实际审核人',
  `act_audit_time` datetime DEFAULT NULL COMMENT '实际审核时间',
  `base_confreren_id` varchar(50) DEFAULT NULL COMMENT '会议公共表Id',
  `inform_time` datetime DEFAULT NULL COMMENT '提前发送通知时间',
  `create_id` varchar(50) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modifyer_id` varchar(50) DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `state` varchar(2) DEFAULT NULL COMMENT '参会回复状态:1审核通过；0:审核未通过',
  `delete_state` varchar(2) DEFAULT NULL COMMENT '记录状态:1:已删除；0:未删除',
  `reserve_1` varchar(20) DEFAULT NULL COMMENT '预留字段1',
  `reserve_3` varchar(20) DEFAULT NULL COMMENT '预留字段3',
  `reserve_2` varchar(20) DEFAULT NULL COMMENT '预留字段2',
  PRIMARY KEY (`id`),
  KEY `AK_Identifier_2` (`base_confreren_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='会议审核';

-- ----------------------------
-- Records of yunmeeting_conference_audit
-- ----------------------------

-- ----------------------------
-- Table structure for yunmeeting_conference_user_info
-- ----------------------------
DROP TABLE IF EXISTS `yunmeeting_conference_user_info`;
CREATE TABLE `yunmeeting_conference_user_info` (
  `id` varchar(50) NOT NULL DEFAULT '' COMMENT '主键',
  `participants_info_id` varchar(50) DEFAULT NULL COMMENT '参会信息表id',
  `participants_id` varchar(50) DEFAULT NULL COMMENT '参会人Id',
  `participants_name` varchar(20) DEFAULT NULL COMMENT '参会人姓名',
  `creater_id` varchar(50) DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `reserve_1` varchar(20) DEFAULT NULL COMMENT '预留字段1',
  `reserve_3` varchar(20) DEFAULT NULL COMMENT '预留字段3',
  `reserve_2` varchar(20) DEFAULT NULL COMMENT '预留字段2',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='机构参会人员信息表';

-- ----------------------------
-- Records of yunmeeting_conference_user_info
-- ----------------------------

-- ----------------------------
-- Table structure for yunmeeting_dynamics
-- ----------------------------
DROP TABLE IF EXISTS `yunmeeting_dynamics`;
CREATE TABLE `yunmeeting_dynamics` (
  `id` varchar(50) NOT NULL,
  `content` varchar(2000) DEFAULT NULL COMMENT '动态内容',
  `conference_id` varchar(50) DEFAULT NULL COMMENT '会议Id',
  `participants_id` varchar(50) DEFAULT NULL COMMENT '参会人Id',
  `inform_time` datetime DEFAULT NULL COMMENT '提前发送通知时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `dynamics_type` varchar(2) DEFAULT NULL COMMENT '0:用户留言；1:系统推送',
  `delete_state` varchar(2) DEFAULT NULL COMMENT '记录状态:1:已删除；0:未删除',
  `reserve_1` varchar(20) DEFAULT NULL COMMENT '预留字段1',
  `reserve_3` varchar(20) DEFAULT NULL COMMENT '预留字段3',
  `reserve_2` varchar(20) DEFAULT NULL COMMENT '预留字段2',
  PRIMARY KEY (`id`),
  KEY `AK_Identifier_2` (`conference_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='会议动态表';

-- ----------------------------
-- Records of yunmeeting_dynamics
-- ----------------------------

-- ----------------------------
-- Table structure for yunmeeting_dynamics_click_record
-- ----------------------------
DROP TABLE IF EXISTS `yunmeeting_dynamics_click_record`;
CREATE TABLE `yunmeeting_dynamics_click_record` (
  `id` varchar(50) NOT NULL,
  `dynamics_id` varchar(50) DEFAULT NULL COMMENT '动态id',
  `participants_id` varchar(50) DEFAULT NULL COMMENT '动态点击人id',
  `create_time` datetime DEFAULT NULL,
  `reserve_1` varchar(20) DEFAULT NULL,
  `reserve_2` varchar(20) DEFAULT NULL,
  `reserve_3` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='���鶯̬�����¼,';

-- ----------------------------
-- Records of yunmeeting_dynamics_click_record
-- ----------------------------

-- ----------------------------
-- Table structure for yunmeeting_meeting_sign
-- ----------------------------
DROP TABLE IF EXISTS `yunmeeting_meeting_sign`;
CREATE TABLE `yunmeeting_meeting_sign` (
  `ID` varchar(50) NOT NULL,
  `confreren_id` varchar(50) DEFAULT NULL COMMENT '会议Id',
  `participants_id` varchar(50) DEFAULT NULL COMMENT '参会人Id',
  `sign_time` datetime DEFAULT NULL COMMENT '签到时间',
  `reserve_1` varchar(20) DEFAULT NULL COMMENT '预留字段1',
  `reserve_2` varchar(20) DEFAULT NULL COMMENT '预留字段2',
  `reserve_3` varchar(20) DEFAULT NULL COMMENT '预留字段3',
  PRIMARY KEY (`ID`),
  KEY `AK_Identifier_2` (`confreren_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='会议签到表';

-- ----------------------------
-- Records of yunmeeting_meeting_sign
-- ----------------------------

-- ----------------------------
-- Table structure for yunmeeting_message_inform
-- ----------------------------
DROP TABLE IF EXISTS `yunmeeting_message_inform`;
CREATE TABLE `yunmeeting_message_inform` (
  `id` varchar(50) NOT NULL DEFAULT '' COMMENT '主键',
  `conference_id` varchar(50) DEFAULT NULL COMMENT '会议Id',
  `inform_type` varchar(50) DEFAULT NULL COMMENT '会议通知类型 1：微信  2：邮件（多个类型用逗号分隔）',
  `inform_time` datetime DEFAULT NULL COMMENT '提前发送通知时间',
  `timed_task_id` varchar(50) DEFAULT NULL COMMENT '定时任务Id',
  `reserve_1` varchar(20) DEFAULT NULL COMMENT '预留字段1',
  `reserve_3` varchar(20) DEFAULT NULL COMMENT '预留字段3',
  `reserve_2` varchar(20) DEFAULT NULL COMMENT '预留字段2',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='会议消息通知';

-- ----------------------------
-- Records of yunmeeting_message_inform
-- ----------------------------

-- ----------------------------
-- Table structure for yunmeeting_participants_info
-- ----------------------------
DROP TABLE IF EXISTS `yunmeeting_participants_info`;
CREATE TABLE `yunmeeting_participants_info` (
  `id` varchar(50) NOT NULL DEFAULT '' COMMENT '主键',
  `conference_id` varchar(50) DEFAULT NULL COMMENT '会议表Id',
  `org_id` varchar(50) DEFAULT NULL COMMENT '当参会记录为人时，记录人的组织机构ID;当参会为机构时，同参会ID;当参会为会议分组时，空',
  `participants_id` varchar(50) DEFAULT NULL COMMENT '参会Id（如果是个人，则为user ID;如果是组织机构，则是组织机构ID;如果是会议分组，则是会议分组ID）',
  `participants_name` varchar(20) DEFAULT NULL COMMENT '如果是人，保存姓名信息；如果是组织机构，保存组织机构名称；如果会议分组，则保存会议分组名称',
  `is_inner` varchar(2) DEFAULT NULL COMMENT '是否内部参会人 1:内部参会人；0:非内部参会人',
  `creater_id` varchar(50) DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `type` varchar(2) DEFAULT NULL COMMENT '参会类型:0:普通参会人；1：组织机构;2:会议分组参会[本状态待设计]',
  `reserve_1` varchar(20) DEFAULT NULL COMMENT '预留字段1',
  `reserve_3` varchar(20) DEFAULT NULL COMMENT '预留字段3',
  `reserve_2` varchar(20) DEFAULT NULL COMMENT '预留字段2',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='参会信息表（包括人员，组织机构参会）';

-- ----------------------------
-- Records of yunmeeting_participants_info
-- ----------------------------

-- ----------------------------
-- Table structure for yunmeeting_participants_reply
-- ----------------------------
DROP TABLE IF EXISTS `yunmeeting_participants_reply`;
CREATE TABLE `yunmeeting_participants_reply` (
  `id` varchar(50) NOT NULL,
  `reply_state` varchar(2) DEFAULT NULL COMMENT '1:接受；2:暂定；0拒绝',
  `conference_id` varchar(50) DEFAULT NULL COMMENT '会议表ID',
  `participants_id` varchar(50) DEFAULT NULL COMMENT '参会人ID',
  `reply_time` datetime DEFAULT NULL COMMENT '回复时间',
  `reserve_1` varchar(20) DEFAULT NULL COMMENT '预留字段1',
  `reserve_2` varchar(20) DEFAULT NULL COMMENT '预留字段2',
  `reserve_3` varchar(20) DEFAULT NULL COMMENT '预留字段3',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='参会人回复';

-- ----------------------------
-- Records of yunmeeting_participants_reply
-- ----------------------------
