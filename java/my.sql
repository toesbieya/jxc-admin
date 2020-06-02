/*
 Navicat Premium Data Transfer

 Source Server         : mysql
 Source Server Type    : MySQL
 Source Server Version : 80018
 Source Host           : localhost:3306
 Source Schema         : my

 Target Server Type    : MySQL
 Target Server Version : 80018
 File Encoding         : 65001

 Date: 03/06/2020 00:28:56
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for biz_document_history
-- ----------------------------
DROP TABLE IF EXISTS `biz_document_history`;
CREATE TABLE `biz_document_history`  (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '单据状态变更记录表',
  `pid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '单据ID',
  `type` int(11) NOT NULL COMMENT '变更类型，0撤回，1提交，2通过，3驳回',
  `uid` int(11) NULL DEFAULT NULL COMMENT '操作人id',
  `uname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作人名称',
  `status_before` int(10) NULL DEFAULT NULL COMMENT '原状态',
  `status_after` int(10) NULL DEFAULT NULL COMMENT '变更后的状态',
  `time` bigint(20) NOT NULL COMMENT '变更时间',
  `info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '相关信息',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `pid_time_idx`(`pid`, `time`) USING BTREE,
  INDEX `uid_idx`(`uid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for biz_purchase_inbound
-- ----------------------------
DROP TABLE IF EXISTS `biz_purchase_inbound`;
CREATE TABLE `biz_purchase_inbound`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '采购入库',
  `pid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '采购订单id',
  `cid` int(11) NOT NULL COMMENT '创建人id',
  `cname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建人名称',
  `ctime` bigint(20) NOT NULL COMMENT '创建时间',
  `vid` int(11) NULL DEFAULT NULL COMMENT '审核人id',
  `vname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核人名称',
  `vtime` bigint(20) NULL DEFAULT NULL COMMENT '审核时间',
  `status` int(11) NOT NULL COMMENT '0拟定、1待审核、2已审核',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `pid_idx`(`pid`) USING BTREE,
  INDEX `cid_idx`(`cid`) USING BTREE,
  INDEX `ctime_idx`(`ctime`) USING BTREE,
  INDEX `status_idx`(`status`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for biz_purchase_inbound_sub
-- ----------------------------
DROP TABLE IF EXISTS `biz_purchase_inbound_sub`;
CREATE TABLE `biz_purchase_inbound_sub`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '采购入库子表',
  `pid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '采购入库id',
  `cid` int(11) NOT NULL COMMENT '商品分类id',
  `cname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品分类名称',
  `num` decimal(10, 2) NOT NULL COMMENT '入库数量',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `pid_cid_unique_idx`(`pid`, `cid`) USING BTREE COMMENT '同一采购入库单不能有重复分类的商品'
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for biz_purchase_order
-- ----------------------------
DROP TABLE IF EXISTS `biz_purchase_order`;
CREATE TABLE `biz_purchase_order`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '采购订单表',
  `sid` int(11) NOT NULL COMMENT '供应商id',
  `sname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '供应商名称',
  `cid` int(11) NOT NULL COMMENT '创建人id',
  `cname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建人名称',
  `ctime` bigint(20) NOT NULL COMMENT '创建时间',
  `vid` int(11) NULL DEFAULT NULL COMMENT '审核人id',
  `vname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核人名称',
  `vtime` bigint(20) NULL DEFAULT NULL COMMENT '审核时间',
  `status` int(11) NOT NULL DEFAULT 0 COMMENT '0拟定，1待审核，2已审核',
  `finish` int(11) NOT NULL DEFAULT 0 COMMENT '0未开始，1进行中，2已完成',
  `ftime` bigint(20) NULL DEFAULT NULL COMMENT '完成时间',
  `total` decimal(10, 2) NULL DEFAULT NULL COMMENT '总额',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `sid_idx`(`sid`) USING BTREE,
  INDEX `cid_idx`(`cid`) USING BTREE,
  INDEX `ctime_idx`(`ctime`) USING BTREE,
  INDEX `status_idx`(`status`) USING BTREE,
  INDEX `finish_idx`(`finish`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for biz_purchase_order_sub
-- ----------------------------
DROP TABLE IF EXISTS `biz_purchase_order_sub`;
CREATE TABLE `biz_purchase_order_sub`  (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '采购订单子表',
  `pid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '采购订单ID',
  `cid` int(11) NOT NULL COMMENT '分类ID',
  `cname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `num` decimal(10, 2) NOT NULL COMMENT '采购数量',
  `price` decimal(10, 2) NOT NULL COMMENT '采购单价',
  `remain_num` decimal(10, 2) NOT NULL COMMENT '剩余未入库的数量',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `pid_cid_unique_idx`(`pid`, `cid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for biz_sell_order
-- ----------------------------
DROP TABLE IF EXISTS `biz_sell_order`;
CREATE TABLE `biz_sell_order`  (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '销售订单',
  `customer_id` int(11) NOT NULL COMMENT '客户id',
  `customer_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '客户名称',
  `cid` int(11) NOT NULL COMMENT '创建人id',
  `cname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建人名称',
  `ctime` bigint(20) NOT NULL COMMENT '创建时间',
  `vid` int(11) NULL DEFAULT NULL COMMENT '审核人id',
  `vname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核人名称',
  `vtime` bigint(20) NULL DEFAULT NULL COMMENT '审核时间',
  `status` int(11) NOT NULL DEFAULT 0 COMMENT '0拟定，1待审核，2已审核',
  `finish` int(11) NOT NULL DEFAULT 0 COMMENT '0未开始，1进行中，2已完成',
  `ftime` bigint(20) NULL DEFAULT NULL COMMENT '完成时间',
  `total` decimal(10, 2) NULL DEFAULT NULL COMMENT '总额',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `customer_id_idx`(`customer_id`) USING BTREE,
  INDEX `cid_idx`(`cid`) USING BTREE,
  INDEX `ctime_idx`(`ctime`) USING BTREE,
  INDEX `status_idx`(`status`) USING BTREE,
  INDEX `finish_idx`(`finish`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for biz_sell_order_sub
-- ----------------------------
DROP TABLE IF EXISTS `biz_sell_order_sub`;
CREATE TABLE `biz_sell_order_sub`  (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '销售订单子表',
  `pid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '销售订单ID',
  `cid` int(11) NOT NULL COMMENT '分类ID',
  `cname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `num` decimal(10, 2) NOT NULL COMMENT '销售数量',
  `price` decimal(10, 2) NOT NULL COMMENT '销售单价',
  `remain_num` decimal(10, 2) NOT NULL COMMENT '剩余未出库的数量',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `pid_cid_unique_idx`(`pid`, `cid`) USING BTREE COMMENT '同一张销售订单内不能有重复分类的商品'
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for biz_sell_outbound
-- ----------------------------
DROP TABLE IF EXISTS `biz_sell_outbound`;
CREATE TABLE `biz_sell_outbound`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '销售出库',
  `pid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '销售订单id',
  `cid` int(11) NOT NULL COMMENT '创建人id',
  `cname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建人名称',
  `ctime` bigint(20) NOT NULL COMMENT '创建时间',
  `vid` int(11) NULL DEFAULT NULL COMMENT '审核人id',
  `vname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核人名称',
  `vtime` bigint(20) NULL DEFAULT NULL COMMENT '审核时间',
  `status` int(11) NOT NULL COMMENT '0拟定、1待审核、2已审核',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `pid_idx`(`pid`) USING BTREE,
  INDEX `cid_idx`(`cid`) USING BTREE,
  INDEX `ctime_idx`(`ctime`) USING BTREE,
  INDEX `status_idx`(`status`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for biz_sell_outbound_sub
-- ----------------------------
DROP TABLE IF EXISTS `biz_sell_outbound_sub`;
CREATE TABLE `biz_sell_outbound_sub`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '销售出库子表',
  `pid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '销售入库id',
  `sid` int(11) NOT NULL COMMENT '库存id',
  `cid` int(11) NOT NULL COMMENT '商品分类id',
  `cname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品分类名称',
  `num` decimal(10, 2) NOT NULL COMMENT '出库数量',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for biz_stock
-- ----------------------------
DROP TABLE IF EXISTS `biz_stock`;
CREATE TABLE `biz_stock`  (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '库存表',
  `cid` int(11) NOT NULL COMMENT '分类id',
  `cname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `num` decimal(10, 2) NOT NULL COMMENT '库存数量',
  `price` decimal(10, 2) NOT NULL COMMENT '采购单价',
  `ctime` bigint(20) NOT NULL COMMENT '入库日期',
  `cgrkid` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '采购入库单id',
  `cgddid` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '采购订单id',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `cid_cgrkid_unique_idx`(`cid`, `cgrkid`) USING BTREE,
  INDEX `cdate_idx`(`ctime`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for msg
-- ----------------------------
DROP TABLE IF EXISTS `msg`;
CREATE TABLE `msg`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '消息表',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '消息内容',
  `type` tinyint(1) NOT NULL COMMENT '消息类型，0通知提醒、1系统公告',
  `cid` int(11) NOT NULL COMMENT '创建人id',
  `cname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建人名称',
  `ctime` bigint(20) NOT NULL COMMENT '创建时间',
  `pid` int(11) NULL DEFAULT NULL COMMENT '发布人id',
  `pname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发布人名称',
  `ptime` bigint(20) NULL DEFAULT NULL COMMENT '发布时间',
  `wid` int(11) NULL DEFAULT NULL COMMENT '撤回人id',
  `wname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '撤回人名称',
  `wtime` bigint(20) NULL DEFAULT NULL COMMENT '撤回时间',
  `status` tinyint(1) NOT NULL COMMENT '消息状态，0拟定、1已发布、2已撤回',
  `all` tinyint(1) NOT NULL COMMENT '是否向全体用户发送，0否1是',
  `recipient` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '接收人，以逗号隔开的用户id字符串',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `type_idx`(`type`) USING BTREE,
  INDEX `cid_idx`(`cid`) USING BTREE,
  INDEX `ctime_idx`(`ctime`) USING BTREE,
  INDEX `status_idx`(`status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for msg_state
-- ----------------------------
DROP TABLE IF EXISTS `msg_state`;
CREATE TABLE `msg_state`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '消息已读状态表',
  `mid` int(11) NOT NULL COMMENT '消息id',
  `uid` int(11) NOT NULL COMMENT '用户id',
  `time` bigint(20) NOT NULL COMMENT '已读时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `mid_idx`(`mid`) USING BTREE,
  INDEX `uid_idx`(`uid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for rec_attachment
-- ----------------------------
DROP TABLE IF EXISTS `rec_attachment`;
CREATE TABLE `rec_attachment`  (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '附件表',
  `pid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '原始名称',
  `time` bigint(20) NOT NULL COMMENT '上传时间',
  `order` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `size` bigint(20) NULL DEFAULT NULL COMMENT '大小，单位byte',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `pid_idx`(`pid`) USING BTREE,
  INDEX `url_idx`(`url`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for rec_login_history
-- ----------------------------
DROP TABLE IF EXISTS `rec_login_history`;
CREATE TABLE `rec_login_history`  (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '用户登录登出历史记录',
  `uid` int(11) NOT NULL COMMENT '用户id',
  `uname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户名称',
  `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'ip',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '地址',
  `type` int(11) NOT NULL COMMENT '0登出，1登陆',
  `time` bigint(20) NOT NULL COMMENT '操作时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `uid_idx`(`uid`) USING BTREE,
  INDEX `type_idx`(`type`) USING BTREE,
  INDEX `time_idx`(`time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 243 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for rec_user_action
-- ----------------------------
DROP TABLE IF EXISTS `rec_user_action`;
CREATE TABLE `rec_user_action`  (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '用户行为记录表',
  `uid` int(11) NULL DEFAULT NULL COMMENT '用户id',
  `uname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户名称',
  `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '访问ip',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '访问的url',
  `time` bigint(20) NOT NULL COMMENT '访问时间',
  `action` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '动作描述',
  `error` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '错误信息',
  `type` int(11) NOT NULL COMMENT '0失败、1成功',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 58 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for stat_finish_order
-- ----------------------------
DROP TABLE IF EXISTS `stat_finish_order`;
CREATE TABLE `stat_finish_order`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '每天统计已完成的订单数',
  `purchase` int(11) NOT NULL COMMENT '采购订单完成数',
  `sell` int(11) NOT NULL COMMENT '销售订单完成数',
  `time` bigint(20) NOT NULL COMMENT '完成时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 97 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for stat_profit_goods
-- ----------------------------
DROP TABLE IF EXISTS `stat_profit_goods`;
CREATE TABLE `stat_profit_goods`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '统计每天每种商品的采购额、销售额、毛利润',
  `cid` int(11) NOT NULL COMMENT '商品分类id',
  `purchase` decimal(10, 2) NOT NULL COMMENT '采购额',
  `sell` decimal(10, 2) NOT NULL COMMENT '销售额',
  `profit` decimal(10, 2) NOT NULL COMMENT '毛利润',
  `time` bigint(20) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for stat_profit_total
-- ----------------------------
DROP TABLE IF EXISTS `stat_profit_total`;
CREATE TABLE `stat_profit_total`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '统计每天的毛利润',
  `purchase` decimal(10, 2) NOT NULL COMMENT '采购额',
  `sell` decimal(10, 2) NOT NULL COMMENT '销售额',
  `profit` decimal(10, 2) NOT NULL COMMENT '毛利润',
  `time` bigint(20) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 97 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_category
-- ----------------------------
DROP TABLE IF EXISTS `sys_category`;
CREATE TABLE `sys_category`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '商品分类表',
  `pid` int(11) NOT NULL COMMENT '上级分类id，根节点时为0',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '节点名称',
  `type` int(11) NOT NULL COMMENT '0为分类，1为实体，只有分类才能有下级节点',
  `ctime` bigint(20) NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name_unique_idx`(`name`) USING BTREE,
  INDEX `pid_idx`(`pid`) USING BTREE,
  INDEX `type_idx`(`type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_category
-- ----------------------------
INSERT INTO `sys_category` VALUES (1, 0, '马良铅笔', 1, 1582204211500);
INSERT INTO `sys_category` VALUES (2, 0, '水桶', 1, 1582204211500);
INSERT INTO `sys_category` VALUES (3, 0, '旺旺饼干', 1, 1582878418713);
INSERT INTO `sys_category` VALUES (4, 0, '肥宅快乐水', 1, 1582878429869);
INSERT INTO `sys_category` VALUES (5, 0, '康帅傅红烧牛肉面', 1, 1582878461760);
INSERT INTO `sys_category` VALUES (6, 0, '神光棒', 1, 1582878512292);
INSERT INTO `sys_category` VALUES (7, 0, '黑框眼镜', 1, 1582878543136);

-- ----------------------------
-- Table structure for sys_customer
-- ----------------------------
DROP TABLE IF EXISTS `sys_customer`;
CREATE TABLE `sys_customer`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '客户表',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '名称',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '地址',
  `linkman` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系人',
  `linkphone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系电话',
  `region` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '所在地区id',
  `status` int(11) NOT NULL COMMENT '0禁用，1启用',
  `ctime` bigint(20) NOT NULL COMMENT '创建时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name_unique_idx`(`name`) USING BTREE,
  INDEX `status_idx`(`status`) USING BTREE,
  INDEX `ctime_idx`(`ctime`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_customer
-- ----------------------------
INSERT INTO `sys_customer` VALUES (1, '测试客户1', '火星', '小张', '132154215663', '2101', 1, 1582277886315, NULL);

-- ----------------------------
-- Table structure for sys_department
-- ----------------------------
DROP TABLE IF EXISTS `sys_department`;
CREATE TABLE `sys_department`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '部门表',
  `pid` int(11) NOT NULL COMMENT '父部门id',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '部门名称',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '0禁用，1正常',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `pid_name_unique_idx`(`pid`, `name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_department
-- ----------------------------
INSERT INTO `sys_department` VALUES (1, 0, '组织机构', 1);
INSERT INTO `sys_department` VALUES (2, 1, 'AA', 1);
INSERT INTO `sys_department` VALUES (3, 1, 'BB', 1);

-- ----------------------------
-- Table structure for sys_region
-- ----------------------------
DROP TABLE IF EXISTS `sys_region`;
CREATE TABLE `sys_region`  (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '行政区划表',
  `pid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `fullname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '全名，不同级别间以逗号隔开',
  `level` int(10) NOT NULL COMMENT '节点级别，省、市、区',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `pid_idx`(`pid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_region
-- ----------------------------
INSERT INTO `sys_region` VALUES ('11', '0', '北京市', '北京市', 1);
INSERT INTO `sys_region` VALUES ('110101', '11', '东城区', '北京市,东城区', 2);
INSERT INTO `sys_region` VALUES ('110102', '11', '西城区', '北京市,西城区', 2);
INSERT INTO `sys_region` VALUES ('110105', '11', '朝阳区', '北京市,朝阳区', 2);
INSERT INTO `sys_region` VALUES ('110106', '11', '丰台区', '北京市,丰台区', 2);
INSERT INTO `sys_region` VALUES ('110107', '11', '石景山区', '北京市,石景山区', 2);
INSERT INTO `sys_region` VALUES ('110108', '11', '海淀区', '北京市,海淀区', 2);
INSERT INTO `sys_region` VALUES ('110109', '11', '门头沟区', '北京市,门头沟区', 2);
INSERT INTO `sys_region` VALUES ('110111', '11', '房山区', '北京市,房山区', 2);
INSERT INTO `sys_region` VALUES ('110112', '11', '通州区', '北京市,通州区', 2);
INSERT INTO `sys_region` VALUES ('110113', '11', '顺义区', '北京市,顺义区', 2);
INSERT INTO `sys_region` VALUES ('110114', '11', '昌平区', '北京市,昌平区', 2);
INSERT INTO `sys_region` VALUES ('110115', '11', '大兴区', '北京市,大兴区', 2);
INSERT INTO `sys_region` VALUES ('110116', '11', '怀柔区', '北京市,怀柔区', 2);
INSERT INTO `sys_region` VALUES ('110117', '11', '平谷区', '北京市,平谷区', 2);
INSERT INTO `sys_region` VALUES ('110118', '11', '密云区', '北京市,密云区', 2);
INSERT INTO `sys_region` VALUES ('110119', '11', '延庆区', '北京市,延庆区', 2);
INSERT INTO `sys_region` VALUES ('12', '0', '天津市', '天津市', 1);
INSERT INTO `sys_region` VALUES ('120101', '12', '和平区', '天津市,和平区', 2);
INSERT INTO `sys_region` VALUES ('120102', '12', '河东区', '天津市,河东区', 2);
INSERT INTO `sys_region` VALUES ('120103', '12', '河西区', '天津市,河西区', 2);
INSERT INTO `sys_region` VALUES ('120104', '12', '南开区', '天津市,南开区', 2);
INSERT INTO `sys_region` VALUES ('120105', '12', '河北区', '天津市,河北区', 2);
INSERT INTO `sys_region` VALUES ('120106', '12', '红桥区', '天津市,红桥区', 2);
INSERT INTO `sys_region` VALUES ('120110', '12', '东丽区', '天津市,东丽区', 2);
INSERT INTO `sys_region` VALUES ('120111', '12', '西青区', '天津市,西青区', 2);
INSERT INTO `sys_region` VALUES ('120112', '12', '津南区', '天津市,津南区', 2);
INSERT INTO `sys_region` VALUES ('120113', '12', '北辰区', '天津市,北辰区', 2);
INSERT INTO `sys_region` VALUES ('120114', '12', '武清区', '天津市,武清区', 2);
INSERT INTO `sys_region` VALUES ('120115', '12', '宝坻区', '天津市,宝坻区', 2);
INSERT INTO `sys_region` VALUES ('120116', '12', '滨海新区', '天津市,滨海新区', 2);
INSERT INTO `sys_region` VALUES ('120117', '12', '宁河区', '天津市,宁河区', 2);
INSERT INTO `sys_region` VALUES ('120118', '12', '静海区', '天津市,静海区', 2);
INSERT INTO `sys_region` VALUES ('120119', '12', '蓟州区', '天津市,蓟州区', 2);
INSERT INTO `sys_region` VALUES ('13', '0', '河北省', '河北省', 1);
INSERT INTO `sys_region` VALUES ('1301', '13', '石家庄市', '河北省,石家庄市', 2);
INSERT INTO `sys_region` VALUES ('130102', '1301', '长安区', '河北省,石家庄市,长安区', 3);
INSERT INTO `sys_region` VALUES ('130104', '1301', '桥西区', '河北省,石家庄市,桥西区', 3);
INSERT INTO `sys_region` VALUES ('130105', '1301', '新华区', '河北省,石家庄市,新华区', 3);
INSERT INTO `sys_region` VALUES ('130107', '1301', '井陉矿区', '河北省,石家庄市,井陉矿区', 3);
INSERT INTO `sys_region` VALUES ('130108', '1301', '裕华区', '河北省,石家庄市,裕华区', 3);
INSERT INTO `sys_region` VALUES ('130109', '1301', '藁城区', '河北省,石家庄市,藁城区', 3);
INSERT INTO `sys_region` VALUES ('130110', '1301', '鹿泉区', '河北省,石家庄市,鹿泉区', 3);
INSERT INTO `sys_region` VALUES ('130111', '1301', '栾城区', '河北省,石家庄市,栾城区', 3);
INSERT INTO `sys_region` VALUES ('130121', '1301', '井陉县', '河北省,石家庄市,井陉县', 3);
INSERT INTO `sys_region` VALUES ('130123', '1301', '正定县', '河北省,石家庄市,正定县', 3);
INSERT INTO `sys_region` VALUES ('130125', '1301', '行唐县', '河北省,石家庄市,行唐县', 3);
INSERT INTO `sys_region` VALUES ('130126', '1301', '灵寿县', '河北省,石家庄市,灵寿县', 3);
INSERT INTO `sys_region` VALUES ('130127', '1301', '高邑县', '河北省,石家庄市,高邑县', 3);
INSERT INTO `sys_region` VALUES ('130128', '1301', '深泽县', '河北省,石家庄市,深泽县', 3);
INSERT INTO `sys_region` VALUES ('130129', '1301', '赞皇县', '河北省,石家庄市,赞皇县', 3);
INSERT INTO `sys_region` VALUES ('130130', '1301', '无极县', '河北省,石家庄市,无极县', 3);
INSERT INTO `sys_region` VALUES ('130131', '1301', '平山县', '河北省,石家庄市,平山县', 3);
INSERT INTO `sys_region` VALUES ('130132', '1301', '元氏县', '河北省,石家庄市,元氏县', 3);
INSERT INTO `sys_region` VALUES ('130133', '1301', '赵县', '河北省,石家庄市,赵县', 3);
INSERT INTO `sys_region` VALUES ('130171', '1301', '石家庄高新技术产业开发区', '河北省,石家庄市,石家庄高新技术产业开发区', 3);
INSERT INTO `sys_region` VALUES ('130172', '1301', '石家庄循环化工园区', '河北省,石家庄市,石家庄循环化工园区', 3);
INSERT INTO `sys_region` VALUES ('130181', '1301', '辛集市', '河北省,石家庄市,辛集市', 3);
INSERT INTO `sys_region` VALUES ('130183', '1301', '晋州市', '河北省,石家庄市,晋州市', 3);
INSERT INTO `sys_region` VALUES ('130184', '1301', '新乐市', '河北省,石家庄市,新乐市', 3);
INSERT INTO `sys_region` VALUES ('1302', '13', '唐山市', '河北省,唐山市', 2);
INSERT INTO `sys_region` VALUES ('130202', '1302', '路南区', '河北省,唐山市,路南区', 3);
INSERT INTO `sys_region` VALUES ('130203', '1302', '路北区', '河北省,唐山市,路北区', 3);
INSERT INTO `sys_region` VALUES ('130204', '1302', '古冶区', '河北省,唐山市,古冶区', 3);
INSERT INTO `sys_region` VALUES ('130205', '1302', '开平区', '河北省,唐山市,开平区', 3);
INSERT INTO `sys_region` VALUES ('130207', '1302', '丰南区', '河北省,唐山市,丰南区', 3);
INSERT INTO `sys_region` VALUES ('130208', '1302', '丰润区', '河北省,唐山市,丰润区', 3);
INSERT INTO `sys_region` VALUES ('130209', '1302', '曹妃甸区', '河北省,唐山市,曹妃甸区', 3);
INSERT INTO `sys_region` VALUES ('130224', '1302', '滦南县', '河北省,唐山市,滦南县', 3);
INSERT INTO `sys_region` VALUES ('130225', '1302', '乐亭县', '河北省,唐山市,乐亭县', 3);
INSERT INTO `sys_region` VALUES ('130227', '1302', '迁西县', '河北省,唐山市,迁西县', 3);
INSERT INTO `sys_region` VALUES ('130229', '1302', '玉田县', '河北省,唐山市,玉田县', 3);
INSERT INTO `sys_region` VALUES ('130271', '1302', '唐山市芦台经济技术开发区', '河北省,唐山市,唐山市芦台经济技术开发区', 3);
INSERT INTO `sys_region` VALUES ('130272', '1302', '唐山市汉沽管理区', '河北省,唐山市,唐山市汉沽管理区', 3);
INSERT INTO `sys_region` VALUES ('130273', '1302', '唐山高新技术产业开发区', '河北省,唐山市,唐山高新技术产业开发区', 3);
INSERT INTO `sys_region` VALUES ('130274', '1302', '河北唐山海港经济开发区', '河北省,唐山市,河北唐山海港经济开发区', 3);
INSERT INTO `sys_region` VALUES ('130281', '1302', '遵化市', '河北省,唐山市,遵化市', 3);
INSERT INTO `sys_region` VALUES ('130283', '1302', '迁安市', '河北省,唐山市,迁安市', 3);
INSERT INTO `sys_region` VALUES ('130284', '1302', '滦州市', '河北省,唐山市,滦州市', 3);
INSERT INTO `sys_region` VALUES ('1303', '13', '秦皇岛市', '河北省,秦皇岛市', 2);
INSERT INTO `sys_region` VALUES ('130302', '1303', '海港区', '河北省,秦皇岛市,海港区', 3);
INSERT INTO `sys_region` VALUES ('130303', '1303', '山海关区', '河北省,秦皇岛市,山海关区', 3);
INSERT INTO `sys_region` VALUES ('130304', '1303', '北戴河区', '河北省,秦皇岛市,北戴河区', 3);
INSERT INTO `sys_region` VALUES ('130306', '1303', '抚宁区', '河北省,秦皇岛市,抚宁区', 3);
INSERT INTO `sys_region` VALUES ('130321', '1303', '青龙满族自治县', '河北省,秦皇岛市,青龙满族自治县', 3);
INSERT INTO `sys_region` VALUES ('130322', '1303', '昌黎县', '河北省,秦皇岛市,昌黎县', 3);
INSERT INTO `sys_region` VALUES ('130324', '1303', '卢龙县', '河北省,秦皇岛市,卢龙县', 3);
INSERT INTO `sys_region` VALUES ('130371', '1303', '秦皇岛市经济技术开发区', '河北省,秦皇岛市,秦皇岛市经济技术开发区', 3);
INSERT INTO `sys_region` VALUES ('130372', '1303', '北戴河新区', '河北省,秦皇岛市,北戴河新区', 3);
INSERT INTO `sys_region` VALUES ('1304', '13', '邯郸市', '河北省,邯郸市', 2);
INSERT INTO `sys_region` VALUES ('130402', '1304', '邯山区', '河北省,邯郸市,邯山区', 3);
INSERT INTO `sys_region` VALUES ('130403', '1304', '丛台区', '河北省,邯郸市,丛台区', 3);
INSERT INTO `sys_region` VALUES ('130404', '1304', '复兴区', '河北省,邯郸市,复兴区', 3);
INSERT INTO `sys_region` VALUES ('130406', '1304', '峰峰矿区', '河北省,邯郸市,峰峰矿区', 3);
INSERT INTO `sys_region` VALUES ('130407', '1304', '肥乡区', '河北省,邯郸市,肥乡区', 3);
INSERT INTO `sys_region` VALUES ('130408', '1304', '永年区', '河北省,邯郸市,永年区', 3);
INSERT INTO `sys_region` VALUES ('130423', '1304', '临漳县', '河北省,邯郸市,临漳县', 3);
INSERT INTO `sys_region` VALUES ('130424', '1304', '成安县', '河北省,邯郸市,成安县', 3);
INSERT INTO `sys_region` VALUES ('130425', '1304', '大名县', '河北省,邯郸市,大名县', 3);
INSERT INTO `sys_region` VALUES ('130426', '1304', '涉县', '河北省,邯郸市,涉县', 3);
INSERT INTO `sys_region` VALUES ('130427', '1304', '磁县', '河北省,邯郸市,磁县', 3);
INSERT INTO `sys_region` VALUES ('130430', '1304', '邱县', '河北省,邯郸市,邱县', 3);
INSERT INTO `sys_region` VALUES ('130431', '1304', '鸡泽县', '河北省,邯郸市,鸡泽县', 3);
INSERT INTO `sys_region` VALUES ('130432', '1304', '广平县', '河北省,邯郸市,广平县', 3);
INSERT INTO `sys_region` VALUES ('130433', '1304', '馆陶县', '河北省,邯郸市,馆陶县', 3);
INSERT INTO `sys_region` VALUES ('130434', '1304', '魏县', '河北省,邯郸市,魏县', 3);
INSERT INTO `sys_region` VALUES ('130435', '1304', '曲周县', '河北省,邯郸市,曲周县', 3);
INSERT INTO `sys_region` VALUES ('130471', '1304', '邯郸经济技术开发区', '河北省,邯郸市,邯郸经济技术开发区', 3);
INSERT INTO `sys_region` VALUES ('130473', '1304', '邯郸冀南新区', '河北省,邯郸市,邯郸冀南新区', 3);
INSERT INTO `sys_region` VALUES ('130481', '1304', '武安市', '河北省,邯郸市,武安市', 3);
INSERT INTO `sys_region` VALUES ('1305', '13', '邢台市', '河北省,邢台市', 2);
INSERT INTO `sys_region` VALUES ('130502', '1305', '桥东区', '河北省,邢台市,桥东区', 3);
INSERT INTO `sys_region` VALUES ('130503', '1305', '桥西区', '河北省,邢台市,桥西区', 3);
INSERT INTO `sys_region` VALUES ('130521', '1305', '邢台县', '河北省,邢台市,邢台县', 3);
INSERT INTO `sys_region` VALUES ('130522', '1305', '临城县', '河北省,邢台市,临城县', 3);
INSERT INTO `sys_region` VALUES ('130523', '1305', '内丘县', '河北省,邢台市,内丘县', 3);
INSERT INTO `sys_region` VALUES ('130524', '1305', '柏乡县', '河北省,邢台市,柏乡县', 3);
INSERT INTO `sys_region` VALUES ('130525', '1305', '隆尧县', '河北省,邢台市,隆尧县', 3);
INSERT INTO `sys_region` VALUES ('130526', '1305', '任县', '河北省,邢台市,任县', 3);
INSERT INTO `sys_region` VALUES ('130527', '1305', '南和县', '河北省,邢台市,南和县', 3);
INSERT INTO `sys_region` VALUES ('130528', '1305', '宁晋县', '河北省,邢台市,宁晋县', 3);
INSERT INTO `sys_region` VALUES ('130529', '1305', '巨鹿县', '河北省,邢台市,巨鹿县', 3);
INSERT INTO `sys_region` VALUES ('130530', '1305', '新河县', '河北省,邢台市,新河县', 3);
INSERT INTO `sys_region` VALUES ('130531', '1305', '广宗县', '河北省,邢台市,广宗县', 3);
INSERT INTO `sys_region` VALUES ('130532', '1305', '平乡县', '河北省,邢台市,平乡县', 3);
INSERT INTO `sys_region` VALUES ('130533', '1305', '威县', '河北省,邢台市,威县', 3);
INSERT INTO `sys_region` VALUES ('130534', '1305', '清河县', '河北省,邢台市,清河县', 3);
INSERT INTO `sys_region` VALUES ('130535', '1305', '临西县', '河北省,邢台市,临西县', 3);
INSERT INTO `sys_region` VALUES ('130571', '1305', '河北邢台经济开发区', '河北省,邢台市,河北邢台经济开发区', 3);
INSERT INTO `sys_region` VALUES ('130581', '1305', '南宫市', '河北省,邢台市,南宫市', 3);
INSERT INTO `sys_region` VALUES ('130582', '1305', '沙河市', '河北省,邢台市,沙河市', 3);
INSERT INTO `sys_region` VALUES ('1306', '13', '保定市', '河北省,保定市', 2);
INSERT INTO `sys_region` VALUES ('130602', '1306', '竞秀区', '河北省,保定市,竞秀区', 3);
INSERT INTO `sys_region` VALUES ('130606', '1306', '莲池区', '河北省,保定市,莲池区', 3);
INSERT INTO `sys_region` VALUES ('130607', '1306', '满城区', '河北省,保定市,满城区', 3);
INSERT INTO `sys_region` VALUES ('130608', '1306', '清苑区', '河北省,保定市,清苑区', 3);
INSERT INTO `sys_region` VALUES ('130609', '1306', '徐水区', '河北省,保定市,徐水区', 3);
INSERT INTO `sys_region` VALUES ('130623', '1306', '涞水县', '河北省,保定市,涞水县', 3);
INSERT INTO `sys_region` VALUES ('130624', '1306', '阜平县', '河北省,保定市,阜平县', 3);
INSERT INTO `sys_region` VALUES ('130626', '1306', '定兴县', '河北省,保定市,定兴县', 3);
INSERT INTO `sys_region` VALUES ('130627', '1306', '唐县', '河北省,保定市,唐县', 3);
INSERT INTO `sys_region` VALUES ('130628', '1306', '高阳县', '河北省,保定市,高阳县', 3);
INSERT INTO `sys_region` VALUES ('130629', '1306', '容城县', '河北省,保定市,容城县', 3);
INSERT INTO `sys_region` VALUES ('130630', '1306', '涞源县', '河北省,保定市,涞源县', 3);
INSERT INTO `sys_region` VALUES ('130631', '1306', '望都县', '河北省,保定市,望都县', 3);
INSERT INTO `sys_region` VALUES ('130632', '1306', '安新县', '河北省,保定市,安新县', 3);
INSERT INTO `sys_region` VALUES ('130633', '1306', '易县', '河北省,保定市,易县', 3);
INSERT INTO `sys_region` VALUES ('130634', '1306', '曲阳县', '河北省,保定市,曲阳县', 3);
INSERT INTO `sys_region` VALUES ('130635', '1306', '蠡县', '河北省,保定市,蠡县', 3);
INSERT INTO `sys_region` VALUES ('130636', '1306', '顺平县', '河北省,保定市,顺平县', 3);
INSERT INTO `sys_region` VALUES ('130637', '1306', '博野县', '河北省,保定市,博野县', 3);
INSERT INTO `sys_region` VALUES ('130638', '1306', '雄县', '河北省,保定市,雄县', 3);
INSERT INTO `sys_region` VALUES ('130671', '1306', '保定高新技术产业开发区', '河北省,保定市,保定高新技术产业开发区', 3);
INSERT INTO `sys_region` VALUES ('130672', '1306', '保定白沟新城', '河北省,保定市,保定白沟新城', 3);
INSERT INTO `sys_region` VALUES ('130681', '1306', '涿州市', '河北省,保定市,涿州市', 3);
INSERT INTO `sys_region` VALUES ('130682', '1306', '定州市', '河北省,保定市,定州市', 3);
INSERT INTO `sys_region` VALUES ('130683', '1306', '安国市', '河北省,保定市,安国市', 3);
INSERT INTO `sys_region` VALUES ('130684', '1306', '高碑店市', '河北省,保定市,高碑店市', 3);
INSERT INTO `sys_region` VALUES ('1307', '13', '张家口市', '河北省,张家口市', 2);
INSERT INTO `sys_region` VALUES ('130702', '1307', '桥东区', '河北省,张家口市,桥东区', 3);
INSERT INTO `sys_region` VALUES ('130703', '1307', '桥西区', '河北省,张家口市,桥西区', 3);
INSERT INTO `sys_region` VALUES ('130705', '1307', '宣化区', '河北省,张家口市,宣化区', 3);
INSERT INTO `sys_region` VALUES ('130706', '1307', '下花园区', '河北省,张家口市,下花园区', 3);
INSERT INTO `sys_region` VALUES ('130708', '1307', '万全区', '河北省,张家口市,万全区', 3);
INSERT INTO `sys_region` VALUES ('130709', '1307', '崇礼区', '河北省,张家口市,崇礼区', 3);
INSERT INTO `sys_region` VALUES ('130722', '1307', '张北县', '河北省,张家口市,张北县', 3);
INSERT INTO `sys_region` VALUES ('130723', '1307', '康保县', '河北省,张家口市,康保县', 3);
INSERT INTO `sys_region` VALUES ('130724', '1307', '沽源县', '河北省,张家口市,沽源县', 3);
INSERT INTO `sys_region` VALUES ('130725', '1307', '尚义县', '河北省,张家口市,尚义县', 3);
INSERT INTO `sys_region` VALUES ('130726', '1307', '蔚县', '河北省,张家口市,蔚县', 3);
INSERT INTO `sys_region` VALUES ('130727', '1307', '阳原县', '河北省,张家口市,阳原县', 3);
INSERT INTO `sys_region` VALUES ('130728', '1307', '怀安县', '河北省,张家口市,怀安县', 3);
INSERT INTO `sys_region` VALUES ('130730', '1307', '怀来县', '河北省,张家口市,怀来县', 3);
INSERT INTO `sys_region` VALUES ('130731', '1307', '涿鹿县', '河北省,张家口市,涿鹿县', 3);
INSERT INTO `sys_region` VALUES ('130732', '1307', '赤城县', '河北省,张家口市,赤城县', 3);
INSERT INTO `sys_region` VALUES ('130771', '1307', '张家口市高新技术产业开发区', '河北省,张家口市,张家口市高新技术产业开发区', 3);
INSERT INTO `sys_region` VALUES ('130772', '1307', '张家口市察北管理区', '河北省,张家口市,张家口市察北管理区', 3);
INSERT INTO `sys_region` VALUES ('130773', '1307', '张家口市塞北管理区', '河北省,张家口市,张家口市塞北管理区', 3);
INSERT INTO `sys_region` VALUES ('1308', '13', '承德市', '河北省,承德市', 2);
INSERT INTO `sys_region` VALUES ('130802', '1308', '双桥区', '河北省,承德市,双桥区', 3);
INSERT INTO `sys_region` VALUES ('130803', '1308', '双滦区', '河北省,承德市,双滦区', 3);
INSERT INTO `sys_region` VALUES ('130804', '1308', '鹰手营子矿区', '河北省,承德市,鹰手营子矿区', 3);
INSERT INTO `sys_region` VALUES ('130821', '1308', '承德县', '河北省,承德市,承德县', 3);
INSERT INTO `sys_region` VALUES ('130822', '1308', '兴隆县', '河北省,承德市,兴隆县', 3);
INSERT INTO `sys_region` VALUES ('130824', '1308', '滦平县', '河北省,承德市,滦平县', 3);
INSERT INTO `sys_region` VALUES ('130825', '1308', '隆化县', '河北省,承德市,隆化县', 3);
INSERT INTO `sys_region` VALUES ('130826', '1308', '丰宁满族自治县', '河北省,承德市,丰宁满族自治县', 3);
INSERT INTO `sys_region` VALUES ('130827', '1308', '宽城满族自治县', '河北省,承德市,宽城满族自治县', 3);
INSERT INTO `sys_region` VALUES ('130828', '1308', '围场满族蒙古族自治县', '河北省,承德市,围场满族蒙古族自治县', 3);
INSERT INTO `sys_region` VALUES ('130871', '1308', '承德高新技术产业开发区', '河北省,承德市,承德高新技术产业开发区', 3);
INSERT INTO `sys_region` VALUES ('130881', '1308', '平泉市', '河北省,承德市,平泉市', 3);
INSERT INTO `sys_region` VALUES ('1309', '13', '沧州市', '河北省,沧州市', 2);
INSERT INTO `sys_region` VALUES ('130902', '1309', '新华区', '河北省,沧州市,新华区', 3);
INSERT INTO `sys_region` VALUES ('130903', '1309', '运河区', '河北省,沧州市,运河区', 3);
INSERT INTO `sys_region` VALUES ('130921', '1309', '沧县', '河北省,沧州市,沧县', 3);
INSERT INTO `sys_region` VALUES ('130922', '1309', '青县', '河北省,沧州市,青县', 3);
INSERT INTO `sys_region` VALUES ('130923', '1309', '东光县', '河北省,沧州市,东光县', 3);
INSERT INTO `sys_region` VALUES ('130924', '1309', '海兴县', '河北省,沧州市,海兴县', 3);
INSERT INTO `sys_region` VALUES ('130925', '1309', '盐山县', '河北省,沧州市,盐山县', 3);
INSERT INTO `sys_region` VALUES ('130926', '1309', '肃宁县', '河北省,沧州市,肃宁县', 3);
INSERT INTO `sys_region` VALUES ('130927', '1309', '南皮县', '河北省,沧州市,南皮县', 3);
INSERT INTO `sys_region` VALUES ('130928', '1309', '吴桥县', '河北省,沧州市,吴桥县', 3);
INSERT INTO `sys_region` VALUES ('130929', '1309', '献县', '河北省,沧州市,献县', 3);
INSERT INTO `sys_region` VALUES ('130930', '1309', '孟村回族自治县', '河北省,沧州市,孟村回族自治县', 3);
INSERT INTO `sys_region` VALUES ('130971', '1309', '河北沧州经济开发区', '河北省,沧州市,河北沧州经济开发区', 3);
INSERT INTO `sys_region` VALUES ('130972', '1309', '沧州高新技术产业开发区', '河北省,沧州市,沧州高新技术产业开发区', 3);
INSERT INTO `sys_region` VALUES ('130973', '1309', '沧州渤海新区', '河北省,沧州市,沧州渤海新区', 3);
INSERT INTO `sys_region` VALUES ('130981', '1309', '泊头市', '河北省,沧州市,泊头市', 3);
INSERT INTO `sys_region` VALUES ('130982', '1309', '任丘市', '河北省,沧州市,任丘市', 3);
INSERT INTO `sys_region` VALUES ('130983', '1309', '黄骅市', '河北省,沧州市,黄骅市', 3);
INSERT INTO `sys_region` VALUES ('130984', '1309', '河间市', '河北省,沧州市,河间市', 3);
INSERT INTO `sys_region` VALUES ('1310', '13', '廊坊市', '河北省,廊坊市', 2);
INSERT INTO `sys_region` VALUES ('131002', '1310', '安次区', '河北省,廊坊市,安次区', 3);
INSERT INTO `sys_region` VALUES ('131003', '1310', '广阳区', '河北省,廊坊市,广阳区', 3);
INSERT INTO `sys_region` VALUES ('131022', '1310', '固安县', '河北省,廊坊市,固安县', 3);
INSERT INTO `sys_region` VALUES ('131023', '1310', '永清县', '河北省,廊坊市,永清县', 3);
INSERT INTO `sys_region` VALUES ('131024', '1310', '香河县', '河北省,廊坊市,香河县', 3);
INSERT INTO `sys_region` VALUES ('131025', '1310', '大城县', '河北省,廊坊市,大城县', 3);
INSERT INTO `sys_region` VALUES ('131026', '1310', '文安县', '河北省,廊坊市,文安县', 3);
INSERT INTO `sys_region` VALUES ('131028', '1310', '大厂回族自治县', '河北省,廊坊市,大厂回族自治县', 3);
INSERT INTO `sys_region` VALUES ('131071', '1310', '廊坊经济技术开发区', '河北省,廊坊市,廊坊经济技术开发区', 3);
INSERT INTO `sys_region` VALUES ('131081', '1310', '霸州市', '河北省,廊坊市,霸州市', 3);
INSERT INTO `sys_region` VALUES ('131082', '1310', '三河市', '河北省,廊坊市,三河市', 3);
INSERT INTO `sys_region` VALUES ('1311', '13', '衡水市', '河北省,衡水市', 2);
INSERT INTO `sys_region` VALUES ('131102', '1311', '桃城区', '河北省,衡水市,桃城区', 3);
INSERT INTO `sys_region` VALUES ('131103', '1311', '冀州区', '河北省,衡水市,冀州区', 3);
INSERT INTO `sys_region` VALUES ('131121', '1311', '枣强县', '河北省,衡水市,枣强县', 3);
INSERT INTO `sys_region` VALUES ('131122', '1311', '武邑县', '河北省,衡水市,武邑县', 3);
INSERT INTO `sys_region` VALUES ('131123', '1311', '武强县', '河北省,衡水市,武强县', 3);
INSERT INTO `sys_region` VALUES ('131124', '1311', '饶阳县', '河北省,衡水市,饶阳县', 3);
INSERT INTO `sys_region` VALUES ('131125', '1311', '安平县', '河北省,衡水市,安平县', 3);
INSERT INTO `sys_region` VALUES ('131126', '1311', '故城县', '河北省,衡水市,故城县', 3);
INSERT INTO `sys_region` VALUES ('131127', '1311', '景县', '河北省,衡水市,景县', 3);
INSERT INTO `sys_region` VALUES ('131128', '1311', '阜城县', '河北省,衡水市,阜城县', 3);
INSERT INTO `sys_region` VALUES ('131171', '1311', '河北衡水高新技术产业开发区', '河北省,衡水市,河北衡水高新技术产业开发区', 3);
INSERT INTO `sys_region` VALUES ('131172', '1311', '衡水滨湖新区', '河北省,衡水市,衡水滨湖新区', 3);
INSERT INTO `sys_region` VALUES ('131182', '1311', '深州市', '河北省,衡水市,深州市', 3);
INSERT INTO `sys_region` VALUES ('14', '0', '山西省', '山西省', 1);
INSERT INTO `sys_region` VALUES ('1401', '14', '太原市', '山西省,太原市', 2);
INSERT INTO `sys_region` VALUES ('140105', '1401', '小店区', '山西省,太原市,小店区', 3);
INSERT INTO `sys_region` VALUES ('140106', '1401', '迎泽区', '山西省,太原市,迎泽区', 3);
INSERT INTO `sys_region` VALUES ('140107', '1401', '杏花岭区', '山西省,太原市,杏花岭区', 3);
INSERT INTO `sys_region` VALUES ('140108', '1401', '尖草坪区', '山西省,太原市,尖草坪区', 3);
INSERT INTO `sys_region` VALUES ('140109', '1401', '万柏林区', '山西省,太原市,万柏林区', 3);
INSERT INTO `sys_region` VALUES ('140110', '1401', '晋源区', '山西省,太原市,晋源区', 3);
INSERT INTO `sys_region` VALUES ('140121', '1401', '清徐县', '山西省,太原市,清徐县', 3);
INSERT INTO `sys_region` VALUES ('140122', '1401', '阳曲县', '山西省,太原市,阳曲县', 3);
INSERT INTO `sys_region` VALUES ('140123', '1401', '娄烦县', '山西省,太原市,娄烦县', 3);
INSERT INTO `sys_region` VALUES ('140171', '1401', '山西转型综合改革示范区', '山西省,太原市,山西转型综合改革示范区', 3);
INSERT INTO `sys_region` VALUES ('140181', '1401', '古交市', '山西省,太原市,古交市', 3);
INSERT INTO `sys_region` VALUES ('1402', '14', '大同市', '山西省,大同市', 2);
INSERT INTO `sys_region` VALUES ('140212', '1402', '新荣区', '山西省,大同市,新荣区', 3);
INSERT INTO `sys_region` VALUES ('140213', '1402', '平城区', '山西省,大同市,平城区', 3);
INSERT INTO `sys_region` VALUES ('140214', '1402', '云冈区', '山西省,大同市,云冈区', 3);
INSERT INTO `sys_region` VALUES ('140215', '1402', '云州区', '山西省,大同市,云州区', 3);
INSERT INTO `sys_region` VALUES ('140221', '1402', '阳高县', '山西省,大同市,阳高县', 3);
INSERT INTO `sys_region` VALUES ('140222', '1402', '天镇县', '山西省,大同市,天镇县', 3);
INSERT INTO `sys_region` VALUES ('140223', '1402', '广灵县', '山西省,大同市,广灵县', 3);
INSERT INTO `sys_region` VALUES ('140224', '1402', '灵丘县', '山西省,大同市,灵丘县', 3);
INSERT INTO `sys_region` VALUES ('140225', '1402', '浑源县', '山西省,大同市,浑源县', 3);
INSERT INTO `sys_region` VALUES ('140226', '1402', '左云县', '山西省,大同市,左云县', 3);
INSERT INTO `sys_region` VALUES ('140271', '1402', '山西大同经济开发区', '山西省,大同市,山西大同经济开发区', 3);
INSERT INTO `sys_region` VALUES ('1403', '14', '阳泉市', '山西省,阳泉市', 2);
INSERT INTO `sys_region` VALUES ('140302', '1403', '城区', '山西省,阳泉市,城区', 3);
INSERT INTO `sys_region` VALUES ('140303', '1403', '矿区', '山西省,阳泉市,矿区', 3);
INSERT INTO `sys_region` VALUES ('140311', '1403', '郊区', '山西省,阳泉市,郊区', 3);
INSERT INTO `sys_region` VALUES ('140321', '1403', '平定县', '山西省,阳泉市,平定县', 3);
INSERT INTO `sys_region` VALUES ('140322', '1403', '盂县', '山西省,阳泉市,盂县', 3);
INSERT INTO `sys_region` VALUES ('1404', '14', '长治市', '山西省,长治市', 2);
INSERT INTO `sys_region` VALUES ('140403', '1404', '潞州区', '山西省,长治市,潞州区', 3);
INSERT INTO `sys_region` VALUES ('140404', '1404', '上党区', '山西省,长治市,上党区', 3);
INSERT INTO `sys_region` VALUES ('140405', '1404', '屯留区', '山西省,长治市,屯留区', 3);
INSERT INTO `sys_region` VALUES ('140406', '1404', '潞城区', '山西省,长治市,潞城区', 3);
INSERT INTO `sys_region` VALUES ('140423', '1404', '襄垣县', '山西省,长治市,襄垣县', 3);
INSERT INTO `sys_region` VALUES ('140425', '1404', '平顺县', '山西省,长治市,平顺县', 3);
INSERT INTO `sys_region` VALUES ('140426', '1404', '黎城县', '山西省,长治市,黎城县', 3);
INSERT INTO `sys_region` VALUES ('140427', '1404', '壶关县', '山西省,长治市,壶关县', 3);
INSERT INTO `sys_region` VALUES ('140428', '1404', '长子县', '山西省,长治市,长子县', 3);
INSERT INTO `sys_region` VALUES ('140429', '1404', '武乡县', '山西省,长治市,武乡县', 3);
INSERT INTO `sys_region` VALUES ('140430', '1404', '沁县', '山西省,长治市,沁县', 3);
INSERT INTO `sys_region` VALUES ('140431', '1404', '沁源县', '山西省,长治市,沁源县', 3);
INSERT INTO `sys_region` VALUES ('140471', '1404', '山西长治高新技术产业园区', '山西省,长治市,山西长治高新技术产业园区', 3);
INSERT INTO `sys_region` VALUES ('1405', '14', '晋城市', '山西省,晋城市', 2);
INSERT INTO `sys_region` VALUES ('140502', '1405', '城区', '山西省,晋城市,城区', 3);
INSERT INTO `sys_region` VALUES ('140521', '1405', '沁水县', '山西省,晋城市,沁水县', 3);
INSERT INTO `sys_region` VALUES ('140522', '1405', '阳城县', '山西省,晋城市,阳城县', 3);
INSERT INTO `sys_region` VALUES ('140524', '1405', '陵川县', '山西省,晋城市,陵川县', 3);
INSERT INTO `sys_region` VALUES ('140525', '1405', '泽州县', '山西省,晋城市,泽州县', 3);
INSERT INTO `sys_region` VALUES ('140581', '1405', '高平市', '山西省,晋城市,高平市', 3);
INSERT INTO `sys_region` VALUES ('1406', '14', '朔州市', '山西省,朔州市', 2);
INSERT INTO `sys_region` VALUES ('140602', '1406', '朔城区', '山西省,朔州市,朔城区', 3);
INSERT INTO `sys_region` VALUES ('140603', '1406', '平鲁区', '山西省,朔州市,平鲁区', 3);
INSERT INTO `sys_region` VALUES ('140621', '1406', '山阴县', '山西省,朔州市,山阴县', 3);
INSERT INTO `sys_region` VALUES ('140622', '1406', '应县', '山西省,朔州市,应县', 3);
INSERT INTO `sys_region` VALUES ('140623', '1406', '右玉县', '山西省,朔州市,右玉县', 3);
INSERT INTO `sys_region` VALUES ('140671', '1406', '山西朔州经济开发区', '山西省,朔州市,山西朔州经济开发区', 3);
INSERT INTO `sys_region` VALUES ('140681', '1406', '怀仁市', '山西省,朔州市,怀仁市', 3);
INSERT INTO `sys_region` VALUES ('1407', '14', '晋中市', '山西省,晋中市', 2);
INSERT INTO `sys_region` VALUES ('140702', '1407', '榆次区', '山西省,晋中市,榆次区', 3);
INSERT INTO `sys_region` VALUES ('140721', '1407', '榆社县', '山西省,晋中市,榆社县', 3);
INSERT INTO `sys_region` VALUES ('140722', '1407', '左权县', '山西省,晋中市,左权县', 3);
INSERT INTO `sys_region` VALUES ('140723', '1407', '和顺县', '山西省,晋中市,和顺县', 3);
INSERT INTO `sys_region` VALUES ('140724', '1407', '昔阳县', '山西省,晋中市,昔阳县', 3);
INSERT INTO `sys_region` VALUES ('140725', '1407', '寿阳县', '山西省,晋中市,寿阳县', 3);
INSERT INTO `sys_region` VALUES ('140726', '1407', '太谷县', '山西省,晋中市,太谷县', 3);
INSERT INTO `sys_region` VALUES ('140727', '1407', '祁县', '山西省,晋中市,祁县', 3);
INSERT INTO `sys_region` VALUES ('140728', '1407', '平遥县', '山西省,晋中市,平遥县', 3);
INSERT INTO `sys_region` VALUES ('140729', '1407', '灵石县', '山西省,晋中市,灵石县', 3);
INSERT INTO `sys_region` VALUES ('140781', '1407', '介休市', '山西省,晋中市,介休市', 3);
INSERT INTO `sys_region` VALUES ('1408', '14', '运城市', '山西省,运城市', 2);
INSERT INTO `sys_region` VALUES ('140802', '1408', '盐湖区', '山西省,运城市,盐湖区', 3);
INSERT INTO `sys_region` VALUES ('140821', '1408', '临猗县', '山西省,运城市,临猗县', 3);
INSERT INTO `sys_region` VALUES ('140822', '1408', '万荣县', '山西省,运城市,万荣县', 3);
INSERT INTO `sys_region` VALUES ('140823', '1408', '闻喜县', '山西省,运城市,闻喜县', 3);
INSERT INTO `sys_region` VALUES ('140824', '1408', '稷山县', '山西省,运城市,稷山县', 3);
INSERT INTO `sys_region` VALUES ('140825', '1408', '新绛县', '山西省,运城市,新绛县', 3);
INSERT INTO `sys_region` VALUES ('140826', '1408', '绛县', '山西省,运城市,绛县', 3);
INSERT INTO `sys_region` VALUES ('140827', '1408', '垣曲县', '山西省,运城市,垣曲县', 3);
INSERT INTO `sys_region` VALUES ('140828', '1408', '夏县', '山西省,运城市,夏县', 3);
INSERT INTO `sys_region` VALUES ('140829', '1408', '平陆县', '山西省,运城市,平陆县', 3);
INSERT INTO `sys_region` VALUES ('140830', '1408', '芮城县', '山西省,运城市,芮城县', 3);
INSERT INTO `sys_region` VALUES ('140881', '1408', '永济市', '山西省,运城市,永济市', 3);
INSERT INTO `sys_region` VALUES ('140882', '1408', '河津市', '山西省,运城市,河津市', 3);
INSERT INTO `sys_region` VALUES ('1409', '14', '忻州市', '山西省,忻州市', 2);
INSERT INTO `sys_region` VALUES ('140902', '1409', '忻府区', '山西省,忻州市,忻府区', 3);
INSERT INTO `sys_region` VALUES ('140921', '1409', '定襄县', '山西省,忻州市,定襄县', 3);
INSERT INTO `sys_region` VALUES ('140922', '1409', '五台县', '山西省,忻州市,五台县', 3);
INSERT INTO `sys_region` VALUES ('140923', '1409', '代县', '山西省,忻州市,代县', 3);
INSERT INTO `sys_region` VALUES ('140924', '1409', '繁峙县', '山西省,忻州市,繁峙县', 3);
INSERT INTO `sys_region` VALUES ('140925', '1409', '宁武县', '山西省,忻州市,宁武县', 3);
INSERT INTO `sys_region` VALUES ('140926', '1409', '静乐县', '山西省,忻州市,静乐县', 3);
INSERT INTO `sys_region` VALUES ('140927', '1409', '神池县', '山西省,忻州市,神池县', 3);
INSERT INTO `sys_region` VALUES ('140928', '1409', '五寨县', '山西省,忻州市,五寨县', 3);
INSERT INTO `sys_region` VALUES ('140929', '1409', '岢岚县', '山西省,忻州市,岢岚县', 3);
INSERT INTO `sys_region` VALUES ('140930', '1409', '河曲县', '山西省,忻州市,河曲县', 3);
INSERT INTO `sys_region` VALUES ('140931', '1409', '保德县', '山西省,忻州市,保德县', 3);
INSERT INTO `sys_region` VALUES ('140932', '1409', '偏关县', '山西省,忻州市,偏关县', 3);
INSERT INTO `sys_region` VALUES ('140971', '1409', '五台山风景名胜区', '山西省,忻州市,五台山风景名胜区', 3);
INSERT INTO `sys_region` VALUES ('140981', '1409', '原平市', '山西省,忻州市,原平市', 3);
INSERT INTO `sys_region` VALUES ('1410', '14', '临汾市', '山西省,临汾市', 2);
INSERT INTO `sys_region` VALUES ('141002', '1410', '尧都区', '山西省,临汾市,尧都区', 3);
INSERT INTO `sys_region` VALUES ('141021', '1410', '曲沃县', '山西省,临汾市,曲沃县', 3);
INSERT INTO `sys_region` VALUES ('141022', '1410', '翼城县', '山西省,临汾市,翼城县', 3);
INSERT INTO `sys_region` VALUES ('141023', '1410', '襄汾县', '山西省,临汾市,襄汾县', 3);
INSERT INTO `sys_region` VALUES ('141024', '1410', '洪洞县', '山西省,临汾市,洪洞县', 3);
INSERT INTO `sys_region` VALUES ('141025', '1410', '古县', '山西省,临汾市,古县', 3);
INSERT INTO `sys_region` VALUES ('141026', '1410', '安泽县', '山西省,临汾市,安泽县', 3);
INSERT INTO `sys_region` VALUES ('141027', '1410', '浮山县', '山西省,临汾市,浮山县', 3);
INSERT INTO `sys_region` VALUES ('141028', '1410', '吉县', '山西省,临汾市,吉县', 3);
INSERT INTO `sys_region` VALUES ('141029', '1410', '乡宁县', '山西省,临汾市,乡宁县', 3);
INSERT INTO `sys_region` VALUES ('141030', '1410', '大宁县', '山西省,临汾市,大宁县', 3);
INSERT INTO `sys_region` VALUES ('141031', '1410', '隰县', '山西省,临汾市,隰县', 3);
INSERT INTO `sys_region` VALUES ('141032', '1410', '永和县', '山西省,临汾市,永和县', 3);
INSERT INTO `sys_region` VALUES ('141033', '1410', '蒲县', '山西省,临汾市,蒲县', 3);
INSERT INTO `sys_region` VALUES ('141034', '1410', '汾西县', '山西省,临汾市,汾西县', 3);
INSERT INTO `sys_region` VALUES ('141081', '1410', '侯马市', '山西省,临汾市,侯马市', 3);
INSERT INTO `sys_region` VALUES ('141082', '1410', '霍州市', '山西省,临汾市,霍州市', 3);
INSERT INTO `sys_region` VALUES ('1411', '14', '吕梁市', '山西省,吕梁市', 2);
INSERT INTO `sys_region` VALUES ('141102', '1411', '离石区', '山西省,吕梁市,离石区', 3);
INSERT INTO `sys_region` VALUES ('141121', '1411', '文水县', '山西省,吕梁市,文水县', 3);
INSERT INTO `sys_region` VALUES ('141122', '1411', '交城县', '山西省,吕梁市,交城县', 3);
INSERT INTO `sys_region` VALUES ('141123', '1411', '兴县', '山西省,吕梁市,兴县', 3);
INSERT INTO `sys_region` VALUES ('141124', '1411', '临县', '山西省,吕梁市,临县', 3);
INSERT INTO `sys_region` VALUES ('141125', '1411', '柳林县', '山西省,吕梁市,柳林县', 3);
INSERT INTO `sys_region` VALUES ('141126', '1411', '石楼县', '山西省,吕梁市,石楼县', 3);
INSERT INTO `sys_region` VALUES ('141127', '1411', '岚县', '山西省,吕梁市,岚县', 3);
INSERT INTO `sys_region` VALUES ('141128', '1411', '方山县', '山西省,吕梁市,方山县', 3);
INSERT INTO `sys_region` VALUES ('141129', '1411', '中阳县', '山西省,吕梁市,中阳县', 3);
INSERT INTO `sys_region` VALUES ('141130', '1411', '交口县', '山西省,吕梁市,交口县', 3);
INSERT INTO `sys_region` VALUES ('141181', '1411', '孝义市', '山西省,吕梁市,孝义市', 3);
INSERT INTO `sys_region` VALUES ('141182', '1411', '汾阳市', '山西省,吕梁市,汾阳市', 3);
INSERT INTO `sys_region` VALUES ('15', '0', '内蒙古自治区', '内蒙古自治区', 1);
INSERT INTO `sys_region` VALUES ('1501', '15', '呼和浩特市', '内蒙古自治区,呼和浩特市', 2);
INSERT INTO `sys_region` VALUES ('150102', '1501', '新城区', '内蒙古自治区,呼和浩特市,新城区', 3);
INSERT INTO `sys_region` VALUES ('150103', '1501', '回民区', '内蒙古自治区,呼和浩特市,回民区', 3);
INSERT INTO `sys_region` VALUES ('150104', '1501', '玉泉区', '内蒙古自治区,呼和浩特市,玉泉区', 3);
INSERT INTO `sys_region` VALUES ('150105', '1501', '赛罕区', '内蒙古自治区,呼和浩特市,赛罕区', 3);
INSERT INTO `sys_region` VALUES ('150121', '1501', '土默特左旗', '内蒙古自治区,呼和浩特市,土默特左旗', 3);
INSERT INTO `sys_region` VALUES ('150122', '1501', '托克托县', '内蒙古自治区,呼和浩特市,托克托县', 3);
INSERT INTO `sys_region` VALUES ('150123', '1501', '和林格尔县', '内蒙古自治区,呼和浩特市,和林格尔县', 3);
INSERT INTO `sys_region` VALUES ('150124', '1501', '清水河县', '内蒙古自治区,呼和浩特市,清水河县', 3);
INSERT INTO `sys_region` VALUES ('150125', '1501', '武川县', '内蒙古自治区,呼和浩特市,武川县', 3);
INSERT INTO `sys_region` VALUES ('150171', '1501', '呼和浩特金海工业园区', '内蒙古自治区,呼和浩特市,呼和浩特金海工业园区', 3);
INSERT INTO `sys_region` VALUES ('150172', '1501', '呼和浩特经济技术开发区', '内蒙古自治区,呼和浩特市,呼和浩特经济技术开发区', 3);
INSERT INTO `sys_region` VALUES ('1502', '15', '包头市', '内蒙古自治区,包头市', 2);
INSERT INTO `sys_region` VALUES ('150202', '1502', '东河区', '内蒙古自治区,包头市,东河区', 3);
INSERT INTO `sys_region` VALUES ('150203', '1502', '昆都仑区', '内蒙古自治区,包头市,昆都仑区', 3);
INSERT INTO `sys_region` VALUES ('150204', '1502', '青山区', '内蒙古自治区,包头市,青山区', 3);
INSERT INTO `sys_region` VALUES ('150205', '1502', '石拐区', '内蒙古自治区,包头市,石拐区', 3);
INSERT INTO `sys_region` VALUES ('150206', '1502', '白云鄂博矿区', '内蒙古自治区,包头市,白云鄂博矿区', 3);
INSERT INTO `sys_region` VALUES ('150207', '1502', '九原区', '内蒙古自治区,包头市,九原区', 3);
INSERT INTO `sys_region` VALUES ('150221', '1502', '土默特右旗', '内蒙古自治区,包头市,土默特右旗', 3);
INSERT INTO `sys_region` VALUES ('150222', '1502', '固阳县', '内蒙古自治区,包头市,固阳县', 3);
INSERT INTO `sys_region` VALUES ('150223', '1502', '达尔罕茂明安联合旗', '内蒙古自治区,包头市,达尔罕茂明安联合旗', 3);
INSERT INTO `sys_region` VALUES ('150271', '1502', '包头稀土高新技术产业开发区', '内蒙古自治区,包头市,包头稀土高新技术产业开发区', 3);
INSERT INTO `sys_region` VALUES ('1503', '15', '乌海市', '内蒙古自治区,乌海市', 2);
INSERT INTO `sys_region` VALUES ('150302', '1503', '海勃湾区', '内蒙古自治区,乌海市,海勃湾区', 3);
INSERT INTO `sys_region` VALUES ('150303', '1503', '海南区', '内蒙古自治区,乌海市,海南区', 3);
INSERT INTO `sys_region` VALUES ('150304', '1503', '乌达区', '内蒙古自治区,乌海市,乌达区', 3);
INSERT INTO `sys_region` VALUES ('1504', '15', '赤峰市', '内蒙古自治区,赤峰市', 2);
INSERT INTO `sys_region` VALUES ('150402', '1504', '红山区', '内蒙古自治区,赤峰市,红山区', 3);
INSERT INTO `sys_region` VALUES ('150403', '1504', '元宝山区', '内蒙古自治区,赤峰市,元宝山区', 3);
INSERT INTO `sys_region` VALUES ('150404', '1504', '松山区', '内蒙古自治区,赤峰市,松山区', 3);
INSERT INTO `sys_region` VALUES ('150421', '1504', '阿鲁科尔沁旗', '内蒙古自治区,赤峰市,阿鲁科尔沁旗', 3);
INSERT INTO `sys_region` VALUES ('150422', '1504', '巴林左旗', '内蒙古自治区,赤峰市,巴林左旗', 3);
INSERT INTO `sys_region` VALUES ('150423', '1504', '巴林右旗', '内蒙古自治区,赤峰市,巴林右旗', 3);
INSERT INTO `sys_region` VALUES ('150424', '1504', '林西县', '内蒙古自治区,赤峰市,林西县', 3);
INSERT INTO `sys_region` VALUES ('150425', '1504', '克什克腾旗', '内蒙古自治区,赤峰市,克什克腾旗', 3);
INSERT INTO `sys_region` VALUES ('150426', '1504', '翁牛特旗', '内蒙古自治区,赤峰市,翁牛特旗', 3);
INSERT INTO `sys_region` VALUES ('150428', '1504', '喀喇沁旗', '内蒙古自治区,赤峰市,喀喇沁旗', 3);
INSERT INTO `sys_region` VALUES ('150429', '1504', '宁城县', '内蒙古自治区,赤峰市,宁城县', 3);
INSERT INTO `sys_region` VALUES ('150430', '1504', '敖汉旗', '内蒙古自治区,赤峰市,敖汉旗', 3);
INSERT INTO `sys_region` VALUES ('1505', '15', '通辽市', '内蒙古自治区,通辽市', 2);
INSERT INTO `sys_region` VALUES ('150502', '1505', '科尔沁区', '内蒙古自治区,通辽市,科尔沁区', 3);
INSERT INTO `sys_region` VALUES ('150521', '1505', '科尔沁左翼中旗', '内蒙古自治区,通辽市,科尔沁左翼中旗', 3);
INSERT INTO `sys_region` VALUES ('150522', '1505', '科尔沁左翼后旗', '内蒙古自治区,通辽市,科尔沁左翼后旗', 3);
INSERT INTO `sys_region` VALUES ('150523', '1505', '开鲁县', '内蒙古自治区,通辽市,开鲁县', 3);
INSERT INTO `sys_region` VALUES ('150524', '1505', '库伦旗', '内蒙古自治区,通辽市,库伦旗', 3);
INSERT INTO `sys_region` VALUES ('150525', '1505', '奈曼旗', '内蒙古自治区,通辽市,奈曼旗', 3);
INSERT INTO `sys_region` VALUES ('150526', '1505', '扎鲁特旗', '内蒙古自治区,通辽市,扎鲁特旗', 3);
INSERT INTO `sys_region` VALUES ('150571', '1505', '通辽经济技术开发区', '内蒙古自治区,通辽市,通辽经济技术开发区', 3);
INSERT INTO `sys_region` VALUES ('150581', '1505', '霍林郭勒市', '内蒙古自治区,通辽市,霍林郭勒市', 3);
INSERT INTO `sys_region` VALUES ('1506', '15', '鄂尔多斯市', '内蒙古自治区,鄂尔多斯市', 2);
INSERT INTO `sys_region` VALUES ('150602', '1506', '东胜区', '内蒙古自治区,鄂尔多斯市,东胜区', 3);
INSERT INTO `sys_region` VALUES ('150603', '1506', '康巴什区', '内蒙古自治区,鄂尔多斯市,康巴什区', 3);
INSERT INTO `sys_region` VALUES ('150621', '1506', '达拉特旗', '内蒙古自治区,鄂尔多斯市,达拉特旗', 3);
INSERT INTO `sys_region` VALUES ('150622', '1506', '准格尔旗', '内蒙古自治区,鄂尔多斯市,准格尔旗', 3);
INSERT INTO `sys_region` VALUES ('150623', '1506', '鄂托克前旗', '内蒙古自治区,鄂尔多斯市,鄂托克前旗', 3);
INSERT INTO `sys_region` VALUES ('150624', '1506', '鄂托克旗', '内蒙古自治区,鄂尔多斯市,鄂托克旗', 3);
INSERT INTO `sys_region` VALUES ('150625', '1506', '杭锦旗', '内蒙古自治区,鄂尔多斯市,杭锦旗', 3);
INSERT INTO `sys_region` VALUES ('150626', '1506', '乌审旗', '内蒙古自治区,鄂尔多斯市,乌审旗', 3);
INSERT INTO `sys_region` VALUES ('150627', '1506', '伊金霍洛旗', '内蒙古自治区,鄂尔多斯市,伊金霍洛旗', 3);
INSERT INTO `sys_region` VALUES ('1507', '15', '呼伦贝尔市', '内蒙古自治区,呼伦贝尔市', 2);
INSERT INTO `sys_region` VALUES ('150702', '1507', '海拉尔区', '内蒙古自治区,呼伦贝尔市,海拉尔区', 3);
INSERT INTO `sys_region` VALUES ('150703', '1507', '扎赉诺尔区', '内蒙古自治区,呼伦贝尔市,扎赉诺尔区', 3);
INSERT INTO `sys_region` VALUES ('150721', '1507', '阿荣旗', '内蒙古自治区,呼伦贝尔市,阿荣旗', 3);
INSERT INTO `sys_region` VALUES ('150722', '1507', '莫力达瓦达斡尔族自治旗', '内蒙古自治区,呼伦贝尔市,莫力达瓦达斡尔族自治旗', 3);
INSERT INTO `sys_region` VALUES ('150723', '1507', '鄂伦春自治旗', '内蒙古自治区,呼伦贝尔市,鄂伦春自治旗', 3);
INSERT INTO `sys_region` VALUES ('150724', '1507', '鄂温克族自治旗', '内蒙古自治区,呼伦贝尔市,鄂温克族自治旗', 3);
INSERT INTO `sys_region` VALUES ('150725', '1507', '陈巴尔虎旗', '内蒙古自治区,呼伦贝尔市,陈巴尔虎旗', 3);
INSERT INTO `sys_region` VALUES ('150726', '1507', '新巴尔虎左旗', '内蒙古自治区,呼伦贝尔市,新巴尔虎左旗', 3);
INSERT INTO `sys_region` VALUES ('150727', '1507', '新巴尔虎右旗', '内蒙古自治区,呼伦贝尔市,新巴尔虎右旗', 3);
INSERT INTO `sys_region` VALUES ('150781', '1507', '满洲里市', '内蒙古自治区,呼伦贝尔市,满洲里市', 3);
INSERT INTO `sys_region` VALUES ('150782', '1507', '牙克石市', '内蒙古自治区,呼伦贝尔市,牙克石市', 3);
INSERT INTO `sys_region` VALUES ('150783', '1507', '扎兰屯市', '内蒙古自治区,呼伦贝尔市,扎兰屯市', 3);
INSERT INTO `sys_region` VALUES ('150784', '1507', '额尔古纳市', '内蒙古自治区,呼伦贝尔市,额尔古纳市', 3);
INSERT INTO `sys_region` VALUES ('150785', '1507', '根河市', '内蒙古自治区,呼伦贝尔市,根河市', 3);
INSERT INTO `sys_region` VALUES ('1508', '15', '巴彦淖尔市', '内蒙古自治区,巴彦淖尔市', 2);
INSERT INTO `sys_region` VALUES ('150802', '1508', '临河区', '内蒙古自治区,巴彦淖尔市,临河区', 3);
INSERT INTO `sys_region` VALUES ('150821', '1508', '五原县', '内蒙古自治区,巴彦淖尔市,五原县', 3);
INSERT INTO `sys_region` VALUES ('150822', '1508', '磴口县', '内蒙古自治区,巴彦淖尔市,磴口县', 3);
INSERT INTO `sys_region` VALUES ('150823', '1508', '乌拉特前旗', '内蒙古自治区,巴彦淖尔市,乌拉特前旗', 3);
INSERT INTO `sys_region` VALUES ('150824', '1508', '乌拉特中旗', '内蒙古自治区,巴彦淖尔市,乌拉特中旗', 3);
INSERT INTO `sys_region` VALUES ('150825', '1508', '乌拉特后旗', '内蒙古自治区,巴彦淖尔市,乌拉特后旗', 3);
INSERT INTO `sys_region` VALUES ('150826', '1508', '杭锦后旗', '内蒙古自治区,巴彦淖尔市,杭锦后旗', 3);
INSERT INTO `sys_region` VALUES ('1509', '15', '乌兰察布市', '内蒙古自治区,乌兰察布市', 2);
INSERT INTO `sys_region` VALUES ('150902', '1509', '集宁区', '内蒙古自治区,乌兰察布市,集宁区', 3);
INSERT INTO `sys_region` VALUES ('150921', '1509', '卓资县', '内蒙古自治区,乌兰察布市,卓资县', 3);
INSERT INTO `sys_region` VALUES ('150922', '1509', '化德县', '内蒙古自治区,乌兰察布市,化德县', 3);
INSERT INTO `sys_region` VALUES ('150923', '1509', '商都县', '内蒙古自治区,乌兰察布市,商都县', 3);
INSERT INTO `sys_region` VALUES ('150924', '1509', '兴和县', '内蒙古自治区,乌兰察布市,兴和县', 3);
INSERT INTO `sys_region` VALUES ('150925', '1509', '凉城县', '内蒙古自治区,乌兰察布市,凉城县', 3);
INSERT INTO `sys_region` VALUES ('150926', '1509', '察哈尔右翼前旗', '内蒙古自治区,乌兰察布市,察哈尔右翼前旗', 3);
INSERT INTO `sys_region` VALUES ('150927', '1509', '察哈尔右翼中旗', '内蒙古自治区,乌兰察布市,察哈尔右翼中旗', 3);
INSERT INTO `sys_region` VALUES ('150928', '1509', '察哈尔右翼后旗', '内蒙古自治区,乌兰察布市,察哈尔右翼后旗', 3);
INSERT INTO `sys_region` VALUES ('150929', '1509', '四子王旗', '内蒙古自治区,乌兰察布市,四子王旗', 3);
INSERT INTO `sys_region` VALUES ('150981', '1509', '丰镇市', '内蒙古自治区,乌兰察布市,丰镇市', 3);
INSERT INTO `sys_region` VALUES ('1522', '15', '兴安盟', '内蒙古自治区,兴安盟', 2);
INSERT INTO `sys_region` VALUES ('152201', '1522', '乌兰浩特市', '内蒙古自治区,兴安盟,乌兰浩特市', 3);
INSERT INTO `sys_region` VALUES ('152202', '1522', '阿尔山市', '内蒙古自治区,兴安盟,阿尔山市', 3);
INSERT INTO `sys_region` VALUES ('152221', '1522', '科尔沁右翼前旗', '内蒙古自治区,兴安盟,科尔沁右翼前旗', 3);
INSERT INTO `sys_region` VALUES ('152222', '1522', '科尔沁右翼中旗', '内蒙古自治区,兴安盟,科尔沁右翼中旗', 3);
INSERT INTO `sys_region` VALUES ('152223', '1522', '扎赉特旗', '内蒙古自治区,兴安盟,扎赉特旗', 3);
INSERT INTO `sys_region` VALUES ('152224', '1522', '突泉县', '内蒙古自治区,兴安盟,突泉县', 3);
INSERT INTO `sys_region` VALUES ('1525', '15', '锡林郭勒盟', '内蒙古自治区,锡林郭勒盟', 2);
INSERT INTO `sys_region` VALUES ('152501', '1525', '二连浩特市', '内蒙古自治区,锡林郭勒盟,二连浩特市', 3);
INSERT INTO `sys_region` VALUES ('152502', '1525', '锡林浩特市', '内蒙古自治区,锡林郭勒盟,锡林浩特市', 3);
INSERT INTO `sys_region` VALUES ('152522', '1525', '阿巴嘎旗', '内蒙古自治区,锡林郭勒盟,阿巴嘎旗', 3);
INSERT INTO `sys_region` VALUES ('152523', '1525', '苏尼特左旗', '内蒙古自治区,锡林郭勒盟,苏尼特左旗', 3);
INSERT INTO `sys_region` VALUES ('152524', '1525', '苏尼特右旗', '内蒙古自治区,锡林郭勒盟,苏尼特右旗', 3);
INSERT INTO `sys_region` VALUES ('152525', '1525', '东乌珠穆沁旗', '内蒙古自治区,锡林郭勒盟,东乌珠穆沁旗', 3);
INSERT INTO `sys_region` VALUES ('152526', '1525', '西乌珠穆沁旗', '内蒙古自治区,锡林郭勒盟,西乌珠穆沁旗', 3);
INSERT INTO `sys_region` VALUES ('152527', '1525', '太仆寺旗', '内蒙古自治区,锡林郭勒盟,太仆寺旗', 3);
INSERT INTO `sys_region` VALUES ('152528', '1525', '镶黄旗', '内蒙古自治区,锡林郭勒盟,镶黄旗', 3);
INSERT INTO `sys_region` VALUES ('152529', '1525', '正镶白旗', '内蒙古自治区,锡林郭勒盟,正镶白旗', 3);
INSERT INTO `sys_region` VALUES ('152530', '1525', '正蓝旗', '内蒙古自治区,锡林郭勒盟,正蓝旗', 3);
INSERT INTO `sys_region` VALUES ('152531', '1525', '多伦县', '内蒙古自治区,锡林郭勒盟,多伦县', 3);
INSERT INTO `sys_region` VALUES ('152571', '1525', '乌拉盖管委会', '内蒙古自治区,锡林郭勒盟,乌拉盖管委会', 3);
INSERT INTO `sys_region` VALUES ('1529', '15', '阿拉善盟', '内蒙古自治区,阿拉善盟', 2);
INSERT INTO `sys_region` VALUES ('152921', '1529', '阿拉善左旗', '内蒙古自治区,阿拉善盟,阿拉善左旗', 3);
INSERT INTO `sys_region` VALUES ('152922', '1529', '阿拉善右旗', '内蒙古自治区,阿拉善盟,阿拉善右旗', 3);
INSERT INTO `sys_region` VALUES ('152923', '1529', '额济纳旗', '内蒙古自治区,阿拉善盟,额济纳旗', 3);
INSERT INTO `sys_region` VALUES ('152971', '1529', '内蒙古阿拉善经济开发区', '内蒙古自治区,阿拉善盟,内蒙古阿拉善经济开发区', 3);
INSERT INTO `sys_region` VALUES ('21', '0', '辽宁省', '辽宁省', 1);
INSERT INTO `sys_region` VALUES ('2101', '21', '沈阳市', '辽宁省,沈阳市', 2);
INSERT INTO `sys_region` VALUES ('210102', '2101', '和平区', '辽宁省,沈阳市,和平区', 3);
INSERT INTO `sys_region` VALUES ('210103', '2101', '沈河区', '辽宁省,沈阳市,沈河区', 3);
INSERT INTO `sys_region` VALUES ('210104', '2101', '大东区', '辽宁省,沈阳市,大东区', 3);
INSERT INTO `sys_region` VALUES ('210105', '2101', '皇姑区', '辽宁省,沈阳市,皇姑区', 3);
INSERT INTO `sys_region` VALUES ('210106', '2101', '铁西区', '辽宁省,沈阳市,铁西区', 3);
INSERT INTO `sys_region` VALUES ('210111', '2101', '苏家屯区', '辽宁省,沈阳市,苏家屯区', 3);
INSERT INTO `sys_region` VALUES ('210112', '2101', '浑南区', '辽宁省,沈阳市,浑南区', 3);
INSERT INTO `sys_region` VALUES ('210113', '2101', '沈北新区', '辽宁省,沈阳市,沈北新区', 3);
INSERT INTO `sys_region` VALUES ('210114', '2101', '于洪区', '辽宁省,沈阳市,于洪区', 3);
INSERT INTO `sys_region` VALUES ('210115', '2101', '辽中区', '辽宁省,沈阳市,辽中区', 3);
INSERT INTO `sys_region` VALUES ('210123', '2101', '康平县', '辽宁省,沈阳市,康平县', 3);
INSERT INTO `sys_region` VALUES ('210124', '2101', '法库县', '辽宁省,沈阳市,法库县', 3);
INSERT INTO `sys_region` VALUES ('210181', '2101', '新民市', '辽宁省,沈阳市,新民市', 3);
INSERT INTO `sys_region` VALUES ('2102', '21', '大连市', '辽宁省,大连市', 2);
INSERT INTO `sys_region` VALUES ('210202', '2102', '中山区', '辽宁省,大连市,中山区', 3);
INSERT INTO `sys_region` VALUES ('210203', '2102', '西岗区', '辽宁省,大连市,西岗区', 3);
INSERT INTO `sys_region` VALUES ('210204', '2102', '沙河口区', '辽宁省,大连市,沙河口区', 3);
INSERT INTO `sys_region` VALUES ('210211', '2102', '甘井子区', '辽宁省,大连市,甘井子区', 3);
INSERT INTO `sys_region` VALUES ('210212', '2102', '旅顺口区', '辽宁省,大连市,旅顺口区', 3);
INSERT INTO `sys_region` VALUES ('210213', '2102', '金州区', '辽宁省,大连市,金州区', 3);
INSERT INTO `sys_region` VALUES ('210214', '2102', '普兰店区', '辽宁省,大连市,普兰店区', 3);
INSERT INTO `sys_region` VALUES ('210224', '2102', '长海县', '辽宁省,大连市,长海县', 3);
INSERT INTO `sys_region` VALUES ('210281', '2102', '瓦房店市', '辽宁省,大连市,瓦房店市', 3);
INSERT INTO `sys_region` VALUES ('210283', '2102', '庄河市', '辽宁省,大连市,庄河市', 3);
INSERT INTO `sys_region` VALUES ('2103', '21', '鞍山市', '辽宁省,鞍山市', 2);
INSERT INTO `sys_region` VALUES ('210302', '2103', '铁东区', '辽宁省,鞍山市,铁东区', 3);
INSERT INTO `sys_region` VALUES ('210303', '2103', '铁西区', '辽宁省,鞍山市,铁西区', 3);
INSERT INTO `sys_region` VALUES ('210304', '2103', '立山区', '辽宁省,鞍山市,立山区', 3);
INSERT INTO `sys_region` VALUES ('210311', '2103', '千山区', '辽宁省,鞍山市,千山区', 3);
INSERT INTO `sys_region` VALUES ('210321', '2103', '台安县', '辽宁省,鞍山市,台安县', 3);
INSERT INTO `sys_region` VALUES ('210323', '2103', '岫岩满族自治县', '辽宁省,鞍山市,岫岩满族自治县', 3);
INSERT INTO `sys_region` VALUES ('210381', '2103', '海城市', '辽宁省,鞍山市,海城市', 3);
INSERT INTO `sys_region` VALUES ('2104', '21', '抚顺市', '辽宁省,抚顺市', 2);
INSERT INTO `sys_region` VALUES ('210402', '2104', '新抚区', '辽宁省,抚顺市,新抚区', 3);
INSERT INTO `sys_region` VALUES ('210403', '2104', '东洲区', '辽宁省,抚顺市,东洲区', 3);
INSERT INTO `sys_region` VALUES ('210404', '2104', '望花区', '辽宁省,抚顺市,望花区', 3);
INSERT INTO `sys_region` VALUES ('210411', '2104', '顺城区', '辽宁省,抚顺市,顺城区', 3);
INSERT INTO `sys_region` VALUES ('210421', '2104', '抚顺县', '辽宁省,抚顺市,抚顺县', 3);
INSERT INTO `sys_region` VALUES ('210422', '2104', '新宾满族自治县', '辽宁省,抚顺市,新宾满族自治县', 3);
INSERT INTO `sys_region` VALUES ('210423', '2104', '清原满族自治县', '辽宁省,抚顺市,清原满族自治县', 3);
INSERT INTO `sys_region` VALUES ('2105', '21', '本溪市', '辽宁省,本溪市', 2);
INSERT INTO `sys_region` VALUES ('210502', '2105', '平山区', '辽宁省,本溪市,平山区', 3);
INSERT INTO `sys_region` VALUES ('210503', '2105', '溪湖区', '辽宁省,本溪市,溪湖区', 3);
INSERT INTO `sys_region` VALUES ('210504', '2105', '明山区', '辽宁省,本溪市,明山区', 3);
INSERT INTO `sys_region` VALUES ('210505', '2105', '南芬区', '辽宁省,本溪市,南芬区', 3);
INSERT INTO `sys_region` VALUES ('210521', '2105', '本溪满族自治县', '辽宁省,本溪市,本溪满族自治县', 3);
INSERT INTO `sys_region` VALUES ('210522', '2105', '桓仁满族自治县', '辽宁省,本溪市,桓仁满族自治县', 3);
INSERT INTO `sys_region` VALUES ('2106', '21', '丹东市', '辽宁省,丹东市', 2);
INSERT INTO `sys_region` VALUES ('210602', '2106', '元宝区', '辽宁省,丹东市,元宝区', 3);
INSERT INTO `sys_region` VALUES ('210603', '2106', '振兴区', '辽宁省,丹东市,振兴区', 3);
INSERT INTO `sys_region` VALUES ('210604', '2106', '振安区', '辽宁省,丹东市,振安区', 3);
INSERT INTO `sys_region` VALUES ('210624', '2106', '宽甸满族自治县', '辽宁省,丹东市,宽甸满族自治县', 3);
INSERT INTO `sys_region` VALUES ('210681', '2106', '东港市', '辽宁省,丹东市,东港市', 3);
INSERT INTO `sys_region` VALUES ('210682', '2106', '凤城市', '辽宁省,丹东市,凤城市', 3);
INSERT INTO `sys_region` VALUES ('2107', '21', '锦州市', '辽宁省,锦州市', 2);
INSERT INTO `sys_region` VALUES ('210702', '2107', '古塔区', '辽宁省,锦州市,古塔区', 3);
INSERT INTO `sys_region` VALUES ('210703', '2107', '凌河区', '辽宁省,锦州市,凌河区', 3);
INSERT INTO `sys_region` VALUES ('210711', '2107', '太和区', '辽宁省,锦州市,太和区', 3);
INSERT INTO `sys_region` VALUES ('210726', '2107', '黑山县', '辽宁省,锦州市,黑山县', 3);
INSERT INTO `sys_region` VALUES ('210727', '2107', '义县', '辽宁省,锦州市,义县', 3);
INSERT INTO `sys_region` VALUES ('210781', '2107', '凌海市', '辽宁省,锦州市,凌海市', 3);
INSERT INTO `sys_region` VALUES ('210782', '2107', '北镇市', '辽宁省,锦州市,北镇市', 3);
INSERT INTO `sys_region` VALUES ('2108', '21', '营口市', '辽宁省,营口市', 2);
INSERT INTO `sys_region` VALUES ('210802', '2108', '站前区', '辽宁省,营口市,站前区', 3);
INSERT INTO `sys_region` VALUES ('210803', '2108', '西市区', '辽宁省,营口市,西市区', 3);
INSERT INTO `sys_region` VALUES ('210804', '2108', '鲅鱼圈区', '辽宁省,营口市,鲅鱼圈区', 3);
INSERT INTO `sys_region` VALUES ('210811', '2108', '老边区', '辽宁省,营口市,老边区', 3);
INSERT INTO `sys_region` VALUES ('210881', '2108', '盖州市', '辽宁省,营口市,盖州市', 3);
INSERT INTO `sys_region` VALUES ('210882', '2108', '大石桥市', '辽宁省,营口市,大石桥市', 3);
INSERT INTO `sys_region` VALUES ('2109', '21', '阜新市', '辽宁省,阜新市', 2);
INSERT INTO `sys_region` VALUES ('210902', '2109', '海州区', '辽宁省,阜新市,海州区', 3);
INSERT INTO `sys_region` VALUES ('210903', '2109', '新邱区', '辽宁省,阜新市,新邱区', 3);
INSERT INTO `sys_region` VALUES ('210904', '2109', '太平区', '辽宁省,阜新市,太平区', 3);
INSERT INTO `sys_region` VALUES ('210905', '2109', '清河门区', '辽宁省,阜新市,清河门区', 3);
INSERT INTO `sys_region` VALUES ('210911', '2109', '细河区', '辽宁省,阜新市,细河区', 3);
INSERT INTO `sys_region` VALUES ('210921', '2109', '阜新蒙古族自治县', '辽宁省,阜新市,阜新蒙古族自治县', 3);
INSERT INTO `sys_region` VALUES ('210922', '2109', '彰武县', '辽宁省,阜新市,彰武县', 3);
INSERT INTO `sys_region` VALUES ('2110', '21', '辽阳市', '辽宁省,辽阳市', 2);
INSERT INTO `sys_region` VALUES ('211002', '2110', '白塔区', '辽宁省,辽阳市,白塔区', 3);
INSERT INTO `sys_region` VALUES ('211003', '2110', '文圣区', '辽宁省,辽阳市,文圣区', 3);
INSERT INTO `sys_region` VALUES ('211004', '2110', '宏伟区', '辽宁省,辽阳市,宏伟区', 3);
INSERT INTO `sys_region` VALUES ('211005', '2110', '弓长岭区', '辽宁省,辽阳市,弓长岭区', 3);
INSERT INTO `sys_region` VALUES ('211011', '2110', '太子河区', '辽宁省,辽阳市,太子河区', 3);
INSERT INTO `sys_region` VALUES ('211021', '2110', '辽阳县', '辽宁省,辽阳市,辽阳县', 3);
INSERT INTO `sys_region` VALUES ('211081', '2110', '灯塔市', '辽宁省,辽阳市,灯塔市', 3);
INSERT INTO `sys_region` VALUES ('2111', '21', '盘锦市', '辽宁省,盘锦市', 2);
INSERT INTO `sys_region` VALUES ('211102', '2111', '双台子区', '辽宁省,盘锦市,双台子区', 3);
INSERT INTO `sys_region` VALUES ('211103', '2111', '兴隆台区', '辽宁省,盘锦市,兴隆台区', 3);
INSERT INTO `sys_region` VALUES ('211104', '2111', '大洼区', '辽宁省,盘锦市,大洼区', 3);
INSERT INTO `sys_region` VALUES ('211122', '2111', '盘山县', '辽宁省,盘锦市,盘山县', 3);
INSERT INTO `sys_region` VALUES ('2112', '21', '铁岭市', '辽宁省,铁岭市', 2);
INSERT INTO `sys_region` VALUES ('211202', '2112', '银州区', '辽宁省,铁岭市,银州区', 3);
INSERT INTO `sys_region` VALUES ('211204', '2112', '清河区', '辽宁省,铁岭市,清河区', 3);
INSERT INTO `sys_region` VALUES ('211221', '2112', '铁岭县', '辽宁省,铁岭市,铁岭县', 3);
INSERT INTO `sys_region` VALUES ('211223', '2112', '西丰县', '辽宁省,铁岭市,西丰县', 3);
INSERT INTO `sys_region` VALUES ('211224', '2112', '昌图县', '辽宁省,铁岭市,昌图县', 3);
INSERT INTO `sys_region` VALUES ('211281', '2112', '调兵山市', '辽宁省,铁岭市,调兵山市', 3);
INSERT INTO `sys_region` VALUES ('211282', '2112', '开原市', '辽宁省,铁岭市,开原市', 3);
INSERT INTO `sys_region` VALUES ('2113', '21', '朝阳市', '辽宁省,朝阳市', 2);
INSERT INTO `sys_region` VALUES ('211302', '2113', '双塔区', '辽宁省,朝阳市,双塔区', 3);
INSERT INTO `sys_region` VALUES ('211303', '2113', '龙城区', '辽宁省,朝阳市,龙城区', 3);
INSERT INTO `sys_region` VALUES ('211321', '2113', '朝阳县', '辽宁省,朝阳市,朝阳县', 3);
INSERT INTO `sys_region` VALUES ('211322', '2113', '建平县', '辽宁省,朝阳市,建平县', 3);
INSERT INTO `sys_region` VALUES ('211324', '2113', '喀喇沁左翼蒙古族自治县', '辽宁省,朝阳市,喀喇沁左翼蒙古族自治县', 3);
INSERT INTO `sys_region` VALUES ('211381', '2113', '北票市', '辽宁省,朝阳市,北票市', 3);
INSERT INTO `sys_region` VALUES ('211382', '2113', '凌源市', '辽宁省,朝阳市,凌源市', 3);
INSERT INTO `sys_region` VALUES ('2114', '21', '葫芦岛市', '辽宁省,葫芦岛市', 2);
INSERT INTO `sys_region` VALUES ('211402', '2114', '连山区', '辽宁省,葫芦岛市,连山区', 3);
INSERT INTO `sys_region` VALUES ('211403', '2114', '龙港区', '辽宁省,葫芦岛市,龙港区', 3);
INSERT INTO `sys_region` VALUES ('211404', '2114', '南票区', '辽宁省,葫芦岛市,南票区', 3);
INSERT INTO `sys_region` VALUES ('211421', '2114', '绥中县', '辽宁省,葫芦岛市,绥中县', 3);
INSERT INTO `sys_region` VALUES ('211422', '2114', '建昌县', '辽宁省,葫芦岛市,建昌县', 3);
INSERT INTO `sys_region` VALUES ('211481', '2114', '兴城市', '辽宁省,葫芦岛市,兴城市', 3);
INSERT INTO `sys_region` VALUES ('22', '0', '吉林省', '吉林省', 1);
INSERT INTO `sys_region` VALUES ('2201', '22', '长春市', '吉林省,长春市', 2);
INSERT INTO `sys_region` VALUES ('220102', '2201', '南关区', '吉林省,长春市,南关区', 3);
INSERT INTO `sys_region` VALUES ('220103', '2201', '宽城区', '吉林省,长春市,宽城区', 3);
INSERT INTO `sys_region` VALUES ('220104', '2201', '朝阳区', '吉林省,长春市,朝阳区', 3);
INSERT INTO `sys_region` VALUES ('220105', '2201', '二道区', '吉林省,长春市,二道区', 3);
INSERT INTO `sys_region` VALUES ('220106', '2201', '绿园区', '吉林省,长春市,绿园区', 3);
INSERT INTO `sys_region` VALUES ('220112', '2201', '双阳区', '吉林省,长春市,双阳区', 3);
INSERT INTO `sys_region` VALUES ('220113', '2201', '九台区', '吉林省,长春市,九台区', 3);
INSERT INTO `sys_region` VALUES ('220122', '2201', '农安县', '吉林省,长春市,农安县', 3);
INSERT INTO `sys_region` VALUES ('220171', '2201', '长春经济技术开发区', '吉林省,长春市,长春经济技术开发区', 3);
INSERT INTO `sys_region` VALUES ('220172', '2201', '长春净月高新技术产业开发区', '吉林省,长春市,长春净月高新技术产业开发区', 3);
INSERT INTO `sys_region` VALUES ('220173', '2201', '长春高新技术产业开发区', '吉林省,长春市,长春高新技术产业开发区', 3);
INSERT INTO `sys_region` VALUES ('220174', '2201', '长春汽车经济技术开发区', '吉林省,长春市,长春汽车经济技术开发区', 3);
INSERT INTO `sys_region` VALUES ('220182', '2201', '榆树市', '吉林省,长春市,榆树市', 3);
INSERT INTO `sys_region` VALUES ('220183', '2201', '德惠市', '吉林省,长春市,德惠市', 3);
INSERT INTO `sys_region` VALUES ('2202', '22', '吉林市', '吉林省,吉林市', 2);
INSERT INTO `sys_region` VALUES ('220202', '2202', '昌邑区', '吉林省,吉林市,昌邑区', 3);
INSERT INTO `sys_region` VALUES ('220203', '2202', '龙潭区', '吉林省,吉林市,龙潭区', 3);
INSERT INTO `sys_region` VALUES ('220204', '2202', '船营区', '吉林省,吉林市,船营区', 3);
INSERT INTO `sys_region` VALUES ('220211', '2202', '丰满区', '吉林省,吉林市,丰满区', 3);
INSERT INTO `sys_region` VALUES ('220221', '2202', '永吉县', '吉林省,吉林市,永吉县', 3);
INSERT INTO `sys_region` VALUES ('220271', '2202', '吉林经济开发区', '吉林省,吉林市,吉林经济开发区', 3);
INSERT INTO `sys_region` VALUES ('220272', '2202', '吉林高新技术产业开发区', '吉林省,吉林市,吉林高新技术产业开发区', 3);
INSERT INTO `sys_region` VALUES ('220273', '2202', '吉林中国新加坡食品区', '吉林省,吉林市,吉林中国新加坡食品区', 3);
INSERT INTO `sys_region` VALUES ('220281', '2202', '蛟河市', '吉林省,吉林市,蛟河市', 3);
INSERT INTO `sys_region` VALUES ('220282', '2202', '桦甸市', '吉林省,吉林市,桦甸市', 3);
INSERT INTO `sys_region` VALUES ('220283', '2202', '舒兰市', '吉林省,吉林市,舒兰市', 3);
INSERT INTO `sys_region` VALUES ('220284', '2202', '磐石市', '吉林省,吉林市,磐石市', 3);
INSERT INTO `sys_region` VALUES ('2203', '22', '四平市', '吉林省,四平市', 2);
INSERT INTO `sys_region` VALUES ('220302', '2203', '铁西区', '吉林省,四平市,铁西区', 3);
INSERT INTO `sys_region` VALUES ('220303', '2203', '铁东区', '吉林省,四平市,铁东区', 3);
INSERT INTO `sys_region` VALUES ('220322', '2203', '梨树县', '吉林省,四平市,梨树县', 3);
INSERT INTO `sys_region` VALUES ('220323', '2203', '伊通满族自治县', '吉林省,四平市,伊通满族自治县', 3);
INSERT INTO `sys_region` VALUES ('220381', '2203', '公主岭市', '吉林省,四平市,公主岭市', 3);
INSERT INTO `sys_region` VALUES ('220382', '2203', '双辽市', '吉林省,四平市,双辽市', 3);
INSERT INTO `sys_region` VALUES ('2204', '22', '辽源市', '吉林省,辽源市', 2);
INSERT INTO `sys_region` VALUES ('220402', '2204', '龙山区', '吉林省,辽源市,龙山区', 3);
INSERT INTO `sys_region` VALUES ('220403', '2204', '西安区', '吉林省,辽源市,西安区', 3);
INSERT INTO `sys_region` VALUES ('220421', '2204', '东丰县', '吉林省,辽源市,东丰县', 3);
INSERT INTO `sys_region` VALUES ('220422', '2204', '东辽县', '吉林省,辽源市,东辽县', 3);
INSERT INTO `sys_region` VALUES ('2205', '22', '通化市', '吉林省,通化市', 2);
INSERT INTO `sys_region` VALUES ('220502', '2205', '东昌区', '吉林省,通化市,东昌区', 3);
INSERT INTO `sys_region` VALUES ('220503', '2205', '二道江区', '吉林省,通化市,二道江区', 3);
INSERT INTO `sys_region` VALUES ('220521', '2205', '通化县', '吉林省,通化市,通化县', 3);
INSERT INTO `sys_region` VALUES ('220523', '2205', '辉南县', '吉林省,通化市,辉南县', 3);
INSERT INTO `sys_region` VALUES ('220524', '2205', '柳河县', '吉林省,通化市,柳河县', 3);
INSERT INTO `sys_region` VALUES ('220581', '2205', '梅河口市', '吉林省,通化市,梅河口市', 3);
INSERT INTO `sys_region` VALUES ('220582', '2205', '集安市', '吉林省,通化市,集安市', 3);
INSERT INTO `sys_region` VALUES ('2206', '22', '白山市', '吉林省,白山市', 2);
INSERT INTO `sys_region` VALUES ('220602', '2206', '浑江区', '吉林省,白山市,浑江区', 3);
INSERT INTO `sys_region` VALUES ('220605', '2206', '江源区', '吉林省,白山市,江源区', 3);
INSERT INTO `sys_region` VALUES ('220621', '2206', '抚松县', '吉林省,白山市,抚松县', 3);
INSERT INTO `sys_region` VALUES ('220622', '2206', '靖宇县', '吉林省,白山市,靖宇县', 3);
INSERT INTO `sys_region` VALUES ('220623', '2206', '长白朝鲜族自治县', '吉林省,白山市,长白朝鲜族自治县', 3);
INSERT INTO `sys_region` VALUES ('220681', '2206', '临江市', '吉林省,白山市,临江市', 3);
INSERT INTO `sys_region` VALUES ('2207', '22', '松原市', '吉林省,松原市', 2);
INSERT INTO `sys_region` VALUES ('220702', '2207', '宁江区', '吉林省,松原市,宁江区', 3);
INSERT INTO `sys_region` VALUES ('220721', '2207', '前郭尔罗斯蒙古族自治县', '吉林省,松原市,前郭尔罗斯蒙古族自治县', 3);
INSERT INTO `sys_region` VALUES ('220722', '2207', '长岭县', '吉林省,松原市,长岭县', 3);
INSERT INTO `sys_region` VALUES ('220723', '2207', '乾安县', '吉林省,松原市,乾安县', 3);
INSERT INTO `sys_region` VALUES ('220771', '2207', '吉林松原经济开发区', '吉林省,松原市,吉林松原经济开发区', 3);
INSERT INTO `sys_region` VALUES ('220781', '2207', '扶余市', '吉林省,松原市,扶余市', 3);
INSERT INTO `sys_region` VALUES ('2208', '22', '白城市', '吉林省,白城市', 2);
INSERT INTO `sys_region` VALUES ('220802', '2208', '洮北区', '吉林省,白城市,洮北区', 3);
INSERT INTO `sys_region` VALUES ('220821', '2208', '镇赉县', '吉林省,白城市,镇赉县', 3);
INSERT INTO `sys_region` VALUES ('220822', '2208', '通榆县', '吉林省,白城市,通榆县', 3);
INSERT INTO `sys_region` VALUES ('220871', '2208', '吉林白城经济开发区', '吉林省,白城市,吉林白城经济开发区', 3);
INSERT INTO `sys_region` VALUES ('220881', '2208', '洮南市', '吉林省,白城市,洮南市', 3);
INSERT INTO `sys_region` VALUES ('220882', '2208', '大安市', '吉林省,白城市,大安市', 3);
INSERT INTO `sys_region` VALUES ('2224', '22', '延边朝鲜族自治州', '吉林省,延边朝鲜族自治州', 2);
INSERT INTO `sys_region` VALUES ('222401', '2224', '延吉市', '吉林省,延边朝鲜族自治州,延吉市', 3);
INSERT INTO `sys_region` VALUES ('222402', '2224', '图们市', '吉林省,延边朝鲜族自治州,图们市', 3);
INSERT INTO `sys_region` VALUES ('222403', '2224', '敦化市', '吉林省,延边朝鲜族自治州,敦化市', 3);
INSERT INTO `sys_region` VALUES ('222404', '2224', '珲春市', '吉林省,延边朝鲜族自治州,珲春市', 3);
INSERT INTO `sys_region` VALUES ('222405', '2224', '龙井市', '吉林省,延边朝鲜族自治州,龙井市', 3);
INSERT INTO `sys_region` VALUES ('222406', '2224', '和龙市', '吉林省,延边朝鲜族自治州,和龙市', 3);
INSERT INTO `sys_region` VALUES ('222424', '2224', '汪清县', '吉林省,延边朝鲜族自治州,汪清县', 3);
INSERT INTO `sys_region` VALUES ('222426', '2224', '安图县', '吉林省,延边朝鲜族自治州,安图县', 3);
INSERT INTO `sys_region` VALUES ('23', '0', '黑龙江省', '黑龙江省', 1);
INSERT INTO `sys_region` VALUES ('2301', '23', '哈尔滨市', '黑龙江省,哈尔滨市', 2);
INSERT INTO `sys_region` VALUES ('230102', '2301', '道里区', '黑龙江省,哈尔滨市,道里区', 3);
INSERT INTO `sys_region` VALUES ('230103', '2301', '南岗区', '黑龙江省,哈尔滨市,南岗区', 3);
INSERT INTO `sys_region` VALUES ('230104', '2301', '道外区', '黑龙江省,哈尔滨市,道外区', 3);
INSERT INTO `sys_region` VALUES ('230108', '2301', '平房区', '黑龙江省,哈尔滨市,平房区', 3);
INSERT INTO `sys_region` VALUES ('230109', '2301', '松北区', '黑龙江省,哈尔滨市,松北区', 3);
INSERT INTO `sys_region` VALUES ('230110', '2301', '香坊区', '黑龙江省,哈尔滨市,香坊区', 3);
INSERT INTO `sys_region` VALUES ('230111', '2301', '呼兰区', '黑龙江省,哈尔滨市,呼兰区', 3);
INSERT INTO `sys_region` VALUES ('230112', '2301', '阿城区', '黑龙江省,哈尔滨市,阿城区', 3);
INSERT INTO `sys_region` VALUES ('230113', '2301', '双城区', '黑龙江省,哈尔滨市,双城区', 3);
INSERT INTO `sys_region` VALUES ('230123', '2301', '依兰县', '黑龙江省,哈尔滨市,依兰县', 3);
INSERT INTO `sys_region` VALUES ('230124', '2301', '方正县', '黑龙江省,哈尔滨市,方正县', 3);
INSERT INTO `sys_region` VALUES ('230125', '2301', '宾县', '黑龙江省,哈尔滨市,宾县', 3);
INSERT INTO `sys_region` VALUES ('230126', '2301', '巴彦县', '黑龙江省,哈尔滨市,巴彦县', 3);
INSERT INTO `sys_region` VALUES ('230127', '2301', '木兰县', '黑龙江省,哈尔滨市,木兰县', 3);
INSERT INTO `sys_region` VALUES ('230128', '2301', '通河县', '黑龙江省,哈尔滨市,通河县', 3);
INSERT INTO `sys_region` VALUES ('230129', '2301', '延寿县', '黑龙江省,哈尔滨市,延寿县', 3);
INSERT INTO `sys_region` VALUES ('230183', '2301', '尚志市', '黑龙江省,哈尔滨市,尚志市', 3);
INSERT INTO `sys_region` VALUES ('230184', '2301', '五常市', '黑龙江省,哈尔滨市,五常市', 3);
INSERT INTO `sys_region` VALUES ('2302', '23', '齐齐哈尔市', '黑龙江省,齐齐哈尔市', 2);
INSERT INTO `sys_region` VALUES ('230202', '2302', '龙沙区', '黑龙江省,齐齐哈尔市,龙沙区', 3);
INSERT INTO `sys_region` VALUES ('230203', '2302', '建华区', '黑龙江省,齐齐哈尔市,建华区', 3);
INSERT INTO `sys_region` VALUES ('230204', '2302', '铁锋区', '黑龙江省,齐齐哈尔市,铁锋区', 3);
INSERT INTO `sys_region` VALUES ('230205', '2302', '昂昂溪区', '黑龙江省,齐齐哈尔市,昂昂溪区', 3);
INSERT INTO `sys_region` VALUES ('230206', '2302', '富拉尔基区', '黑龙江省,齐齐哈尔市,富拉尔基区', 3);
INSERT INTO `sys_region` VALUES ('230207', '2302', '碾子山区', '黑龙江省,齐齐哈尔市,碾子山区', 3);
INSERT INTO `sys_region` VALUES ('230208', '2302', '梅里斯达斡尔族区', '黑龙江省,齐齐哈尔市,梅里斯达斡尔族区', 3);
INSERT INTO `sys_region` VALUES ('230221', '2302', '龙江县', '黑龙江省,齐齐哈尔市,龙江县', 3);
INSERT INTO `sys_region` VALUES ('230223', '2302', '依安县', '黑龙江省,齐齐哈尔市,依安县', 3);
INSERT INTO `sys_region` VALUES ('230224', '2302', '泰来县', '黑龙江省,齐齐哈尔市,泰来县', 3);
INSERT INTO `sys_region` VALUES ('230225', '2302', '甘南县', '黑龙江省,齐齐哈尔市,甘南县', 3);
INSERT INTO `sys_region` VALUES ('230227', '2302', '富裕县', '黑龙江省,齐齐哈尔市,富裕县', 3);
INSERT INTO `sys_region` VALUES ('230229', '2302', '克山县', '黑龙江省,齐齐哈尔市,克山县', 3);
INSERT INTO `sys_region` VALUES ('230230', '2302', '克东县', '黑龙江省,齐齐哈尔市,克东县', 3);
INSERT INTO `sys_region` VALUES ('230231', '2302', '拜泉县', '黑龙江省,齐齐哈尔市,拜泉县', 3);
INSERT INTO `sys_region` VALUES ('230281', '2302', '讷河市', '黑龙江省,齐齐哈尔市,讷河市', 3);
INSERT INTO `sys_region` VALUES ('2303', '23', '鸡西市', '黑龙江省,鸡西市', 2);
INSERT INTO `sys_region` VALUES ('230302', '2303', '鸡冠区', '黑龙江省,鸡西市,鸡冠区', 3);
INSERT INTO `sys_region` VALUES ('230303', '2303', '恒山区', '黑龙江省,鸡西市,恒山区', 3);
INSERT INTO `sys_region` VALUES ('230304', '2303', '滴道区', '黑龙江省,鸡西市,滴道区', 3);
INSERT INTO `sys_region` VALUES ('230305', '2303', '梨树区', '黑龙江省,鸡西市,梨树区', 3);
INSERT INTO `sys_region` VALUES ('230306', '2303', '城子河区', '黑龙江省,鸡西市,城子河区', 3);
INSERT INTO `sys_region` VALUES ('230307', '2303', '麻山区', '黑龙江省,鸡西市,麻山区', 3);
INSERT INTO `sys_region` VALUES ('230321', '2303', '鸡东县', '黑龙江省,鸡西市,鸡东县', 3);
INSERT INTO `sys_region` VALUES ('230381', '2303', '虎林市', '黑龙江省,鸡西市,虎林市', 3);
INSERT INTO `sys_region` VALUES ('230382', '2303', '密山市', '黑龙江省,鸡西市,密山市', 3);
INSERT INTO `sys_region` VALUES ('2304', '23', '鹤岗市', '黑龙江省,鹤岗市', 2);
INSERT INTO `sys_region` VALUES ('230402', '2304', '向阳区', '黑龙江省,鹤岗市,向阳区', 3);
INSERT INTO `sys_region` VALUES ('230403', '2304', '工农区', '黑龙江省,鹤岗市,工农区', 3);
INSERT INTO `sys_region` VALUES ('230404', '2304', '南山区', '黑龙江省,鹤岗市,南山区', 3);
INSERT INTO `sys_region` VALUES ('230405', '2304', '兴安区', '黑龙江省,鹤岗市,兴安区', 3);
INSERT INTO `sys_region` VALUES ('230406', '2304', '东山区', '黑龙江省,鹤岗市,东山区', 3);
INSERT INTO `sys_region` VALUES ('230407', '2304', '兴山区', '黑龙江省,鹤岗市,兴山区', 3);
INSERT INTO `sys_region` VALUES ('230421', '2304', '萝北县', '黑龙江省,鹤岗市,萝北县', 3);
INSERT INTO `sys_region` VALUES ('230422', '2304', '绥滨县', '黑龙江省,鹤岗市,绥滨县', 3);
INSERT INTO `sys_region` VALUES ('2305', '23', '双鸭山市', '黑龙江省,双鸭山市', 2);
INSERT INTO `sys_region` VALUES ('230502', '2305', '尖山区', '黑龙江省,双鸭山市,尖山区', 3);
INSERT INTO `sys_region` VALUES ('230503', '2305', '岭东区', '黑龙江省,双鸭山市,岭东区', 3);
INSERT INTO `sys_region` VALUES ('230505', '2305', '四方台区', '黑龙江省,双鸭山市,四方台区', 3);
INSERT INTO `sys_region` VALUES ('230506', '2305', '宝山区', '黑龙江省,双鸭山市,宝山区', 3);
INSERT INTO `sys_region` VALUES ('230521', '2305', '集贤县', '黑龙江省,双鸭山市,集贤县', 3);
INSERT INTO `sys_region` VALUES ('230522', '2305', '友谊县', '黑龙江省,双鸭山市,友谊县', 3);
INSERT INTO `sys_region` VALUES ('230523', '2305', '宝清县', '黑龙江省,双鸭山市,宝清县', 3);
INSERT INTO `sys_region` VALUES ('230524', '2305', '饶河县', '黑龙江省,双鸭山市,饶河县', 3);
INSERT INTO `sys_region` VALUES ('2306', '23', '大庆市', '黑龙江省,大庆市', 2);
INSERT INTO `sys_region` VALUES ('230602', '2306', '萨尔图区', '黑龙江省,大庆市,萨尔图区', 3);
INSERT INTO `sys_region` VALUES ('230603', '2306', '龙凤区', '黑龙江省,大庆市,龙凤区', 3);
INSERT INTO `sys_region` VALUES ('230604', '2306', '让胡路区', '黑龙江省,大庆市,让胡路区', 3);
INSERT INTO `sys_region` VALUES ('230605', '2306', '红岗区', '黑龙江省,大庆市,红岗区', 3);
INSERT INTO `sys_region` VALUES ('230606', '2306', '大同区', '黑龙江省,大庆市,大同区', 3);
INSERT INTO `sys_region` VALUES ('230621', '2306', '肇州县', '黑龙江省,大庆市,肇州县', 3);
INSERT INTO `sys_region` VALUES ('230622', '2306', '肇源县', '黑龙江省,大庆市,肇源县', 3);
INSERT INTO `sys_region` VALUES ('230623', '2306', '林甸县', '黑龙江省,大庆市,林甸县', 3);
INSERT INTO `sys_region` VALUES ('230624', '2306', '杜尔伯特蒙古族自治县', '黑龙江省,大庆市,杜尔伯特蒙古族自治县', 3);
INSERT INTO `sys_region` VALUES ('230671', '2306', '大庆高新技术产业开发区', '黑龙江省,大庆市,大庆高新技术产业开发区', 3);
INSERT INTO `sys_region` VALUES ('2307', '23', '伊春市', '黑龙江省,伊春市', 2);
INSERT INTO `sys_region` VALUES ('230702', '2307', '伊春区', '黑龙江省,伊春市,伊春区', 3);
INSERT INTO `sys_region` VALUES ('230703', '2307', '南岔区', '黑龙江省,伊春市,南岔区', 3);
INSERT INTO `sys_region` VALUES ('230704', '2307', '友好区', '黑龙江省,伊春市,友好区', 3);
INSERT INTO `sys_region` VALUES ('230705', '2307', '西林区', '黑龙江省,伊春市,西林区', 3);
INSERT INTO `sys_region` VALUES ('230706', '2307', '翠峦区', '黑龙江省,伊春市,翠峦区', 3);
INSERT INTO `sys_region` VALUES ('230707', '2307', '新青区', '黑龙江省,伊春市,新青区', 3);
INSERT INTO `sys_region` VALUES ('230708', '2307', '美溪区', '黑龙江省,伊春市,美溪区', 3);
INSERT INTO `sys_region` VALUES ('230709', '2307', '金山屯区', '黑龙江省,伊春市,金山屯区', 3);
INSERT INTO `sys_region` VALUES ('230710', '2307', '五营区', '黑龙江省,伊春市,五营区', 3);
INSERT INTO `sys_region` VALUES ('230711', '2307', '乌马河区', '黑龙江省,伊春市,乌马河区', 3);
INSERT INTO `sys_region` VALUES ('230712', '2307', '汤旺河区', '黑龙江省,伊春市,汤旺河区', 3);
INSERT INTO `sys_region` VALUES ('230713', '2307', '带岭区', '黑龙江省,伊春市,带岭区', 3);
INSERT INTO `sys_region` VALUES ('230714', '2307', '乌伊岭区', '黑龙江省,伊春市,乌伊岭区', 3);
INSERT INTO `sys_region` VALUES ('230715', '2307', '红星区', '黑龙江省,伊春市,红星区', 3);
INSERT INTO `sys_region` VALUES ('230716', '2307', '上甘岭区', '黑龙江省,伊春市,上甘岭区', 3);
INSERT INTO `sys_region` VALUES ('230722', '2307', '嘉荫县', '黑龙江省,伊春市,嘉荫县', 3);
INSERT INTO `sys_region` VALUES ('230781', '2307', '铁力市', '黑龙江省,伊春市,铁力市', 3);
INSERT INTO `sys_region` VALUES ('2308', '23', '佳木斯市', '黑龙江省,佳木斯市', 2);
INSERT INTO `sys_region` VALUES ('230803', '2308', '向阳区', '黑龙江省,佳木斯市,向阳区', 3);
INSERT INTO `sys_region` VALUES ('230804', '2308', '前进区', '黑龙江省,佳木斯市,前进区', 3);
INSERT INTO `sys_region` VALUES ('230805', '2308', '东风区', '黑龙江省,佳木斯市,东风区', 3);
INSERT INTO `sys_region` VALUES ('230811', '2308', '郊区', '黑龙江省,佳木斯市,郊区', 3);
INSERT INTO `sys_region` VALUES ('230822', '2308', '桦南县', '黑龙江省,佳木斯市,桦南县', 3);
INSERT INTO `sys_region` VALUES ('230826', '2308', '桦川县', '黑龙江省,佳木斯市,桦川县', 3);
INSERT INTO `sys_region` VALUES ('230828', '2308', '汤原县', '黑龙江省,佳木斯市,汤原县', 3);
INSERT INTO `sys_region` VALUES ('230881', '2308', '同江市', '黑龙江省,佳木斯市,同江市', 3);
INSERT INTO `sys_region` VALUES ('230882', '2308', '富锦市', '黑龙江省,佳木斯市,富锦市', 3);
INSERT INTO `sys_region` VALUES ('230883', '2308', '抚远市', '黑龙江省,佳木斯市,抚远市', 3);
INSERT INTO `sys_region` VALUES ('2309', '23', '七台河市', '黑龙江省,七台河市', 2);
INSERT INTO `sys_region` VALUES ('230902', '2309', '新兴区', '黑龙江省,七台河市,新兴区', 3);
INSERT INTO `sys_region` VALUES ('230903', '2309', '桃山区', '黑龙江省,七台河市,桃山区', 3);
INSERT INTO `sys_region` VALUES ('230904', '2309', '茄子河区', '黑龙江省,七台河市,茄子河区', 3);
INSERT INTO `sys_region` VALUES ('230921', '2309', '勃利县', '黑龙江省,七台河市,勃利县', 3);
INSERT INTO `sys_region` VALUES ('2310', '23', '牡丹江市', '黑龙江省,牡丹江市', 2);
INSERT INTO `sys_region` VALUES ('231002', '2310', '东安区', '黑龙江省,牡丹江市,东安区', 3);
INSERT INTO `sys_region` VALUES ('231003', '2310', '阳明区', '黑龙江省,牡丹江市,阳明区', 3);
INSERT INTO `sys_region` VALUES ('231004', '2310', '爱民区', '黑龙江省,牡丹江市,爱民区', 3);
INSERT INTO `sys_region` VALUES ('231005', '2310', '西安区', '黑龙江省,牡丹江市,西安区', 3);
INSERT INTO `sys_region` VALUES ('231025', '2310', '林口县', '黑龙江省,牡丹江市,林口县', 3);
INSERT INTO `sys_region` VALUES ('231071', '2310', '牡丹江经济技术开发区', '黑龙江省,牡丹江市,牡丹江经济技术开发区', 3);
INSERT INTO `sys_region` VALUES ('231081', '2310', '绥芬河市', '黑龙江省,牡丹江市,绥芬河市', 3);
INSERT INTO `sys_region` VALUES ('231083', '2310', '海林市', '黑龙江省,牡丹江市,海林市', 3);
INSERT INTO `sys_region` VALUES ('231084', '2310', '宁安市', '黑龙江省,牡丹江市,宁安市', 3);
INSERT INTO `sys_region` VALUES ('231085', '2310', '穆棱市', '黑龙江省,牡丹江市,穆棱市', 3);
INSERT INTO `sys_region` VALUES ('231086', '2310', '东宁市', '黑龙江省,牡丹江市,东宁市', 3);
INSERT INTO `sys_region` VALUES ('2311', '23', '黑河市', '黑龙江省,黑河市', 2);
INSERT INTO `sys_region` VALUES ('231102', '2311', '爱辉区', '黑龙江省,黑河市,爱辉区', 3);
INSERT INTO `sys_region` VALUES ('231121', '2311', '嫩江县', '黑龙江省,黑河市,嫩江县', 3);
INSERT INTO `sys_region` VALUES ('231123', '2311', '逊克县', '黑龙江省,黑河市,逊克县', 3);
INSERT INTO `sys_region` VALUES ('231124', '2311', '孙吴县', '黑龙江省,黑河市,孙吴县', 3);
INSERT INTO `sys_region` VALUES ('231181', '2311', '北安市', '黑龙江省,黑河市,北安市', 3);
INSERT INTO `sys_region` VALUES ('231182', '2311', '五大连池市', '黑龙江省,黑河市,五大连池市', 3);
INSERT INTO `sys_region` VALUES ('2312', '23', '绥化市', '黑龙江省,绥化市', 2);
INSERT INTO `sys_region` VALUES ('231202', '2312', '北林区', '黑龙江省,绥化市,北林区', 3);
INSERT INTO `sys_region` VALUES ('231221', '2312', '望奎县', '黑龙江省,绥化市,望奎县', 3);
INSERT INTO `sys_region` VALUES ('231222', '2312', '兰西县', '黑龙江省,绥化市,兰西县', 3);
INSERT INTO `sys_region` VALUES ('231223', '2312', '青冈县', '黑龙江省,绥化市,青冈县', 3);
INSERT INTO `sys_region` VALUES ('231224', '2312', '庆安县', '黑龙江省,绥化市,庆安县', 3);
INSERT INTO `sys_region` VALUES ('231225', '2312', '明水县', '黑龙江省,绥化市,明水县', 3);
INSERT INTO `sys_region` VALUES ('231226', '2312', '绥棱县', '黑龙江省,绥化市,绥棱县', 3);
INSERT INTO `sys_region` VALUES ('231281', '2312', '安达市', '黑龙江省,绥化市,安达市', 3);
INSERT INTO `sys_region` VALUES ('231282', '2312', '肇东市', '黑龙江省,绥化市,肇东市', 3);
INSERT INTO `sys_region` VALUES ('231283', '2312', '海伦市', '黑龙江省,绥化市,海伦市', 3);
INSERT INTO `sys_region` VALUES ('2327', '23', '大兴安岭地区', '黑龙江省,大兴安岭地区', 2);
INSERT INTO `sys_region` VALUES ('232701', '2327', '漠河市', '黑龙江省,大兴安岭地区,漠河市', 3);
INSERT INTO `sys_region` VALUES ('232721', '2327', '呼玛县', '黑龙江省,大兴安岭地区,呼玛县', 3);
INSERT INTO `sys_region` VALUES ('232722', '2327', '塔河县', '黑龙江省,大兴安岭地区,塔河县', 3);
INSERT INTO `sys_region` VALUES ('232761', '2327', '加格达奇区', '黑龙江省,大兴安岭地区,加格达奇区', 3);
INSERT INTO `sys_region` VALUES ('232762', '2327', '松岭区', '黑龙江省,大兴安岭地区,松岭区', 3);
INSERT INTO `sys_region` VALUES ('232763', '2327', '新林区', '黑龙江省,大兴安岭地区,新林区', 3);
INSERT INTO `sys_region` VALUES ('232764', '2327', '呼中区', '黑龙江省,大兴安岭地区,呼中区', 3);
INSERT INTO `sys_region` VALUES ('31', '0', '上海市', '上海市', 1);
INSERT INTO `sys_region` VALUES ('310101', '31', '黄浦区', '上海市,黄浦区', 2);
INSERT INTO `sys_region` VALUES ('310104', '31', '徐汇区', '上海市,徐汇区', 2);
INSERT INTO `sys_region` VALUES ('310105', '31', '长宁区', '上海市,长宁区', 2);
INSERT INTO `sys_region` VALUES ('310106', '31', '静安区', '上海市,静安区', 2);
INSERT INTO `sys_region` VALUES ('310107', '31', '普陀区', '上海市,普陀区', 2);
INSERT INTO `sys_region` VALUES ('310109', '31', '虹口区', '上海市,虹口区', 2);
INSERT INTO `sys_region` VALUES ('310110', '31', '杨浦区', '上海市,杨浦区', 2);
INSERT INTO `sys_region` VALUES ('310112', '31', '闵行区', '上海市,闵行区', 2);
INSERT INTO `sys_region` VALUES ('310113', '31', '宝山区', '上海市,宝山区', 2);
INSERT INTO `sys_region` VALUES ('310114', '31', '嘉定区', '上海市,嘉定区', 2);
INSERT INTO `sys_region` VALUES ('310115', '31', '浦东新区', '上海市,浦东新区', 2);
INSERT INTO `sys_region` VALUES ('310116', '31', '金山区', '上海市,金山区', 2);
INSERT INTO `sys_region` VALUES ('310117', '31', '松江区', '上海市,松江区', 2);
INSERT INTO `sys_region` VALUES ('310118', '31', '青浦区', '上海市,青浦区', 2);
INSERT INTO `sys_region` VALUES ('310120', '31', '奉贤区', '上海市,奉贤区', 2);
INSERT INTO `sys_region` VALUES ('310151', '31', '崇明区', '上海市,崇明区', 2);
INSERT INTO `sys_region` VALUES ('32', '0', '江苏省', '江苏省', 1);
INSERT INTO `sys_region` VALUES ('3201', '32', '南京市', '江苏省,南京市', 2);
INSERT INTO `sys_region` VALUES ('320102', '3201', '玄武区', '江苏省,南京市,玄武区', 3);
INSERT INTO `sys_region` VALUES ('320104', '3201', '秦淮区', '江苏省,南京市,秦淮区', 3);
INSERT INTO `sys_region` VALUES ('320105', '3201', '建邺区', '江苏省,南京市,建邺区', 3);
INSERT INTO `sys_region` VALUES ('320106', '3201', '鼓楼区', '江苏省,南京市,鼓楼区', 3);
INSERT INTO `sys_region` VALUES ('320111', '3201', '浦口区', '江苏省,南京市,浦口区', 3);
INSERT INTO `sys_region` VALUES ('320113', '3201', '栖霞区', '江苏省,南京市,栖霞区', 3);
INSERT INTO `sys_region` VALUES ('320114', '3201', '雨花台区', '江苏省,南京市,雨花台区', 3);
INSERT INTO `sys_region` VALUES ('320115', '3201', '江宁区', '江苏省,南京市,江宁区', 3);
INSERT INTO `sys_region` VALUES ('320116', '3201', '六合区', '江苏省,南京市,六合区', 3);
INSERT INTO `sys_region` VALUES ('320117', '3201', '溧水区', '江苏省,南京市,溧水区', 3);
INSERT INTO `sys_region` VALUES ('320118', '3201', '高淳区', '江苏省,南京市,高淳区', 3);
INSERT INTO `sys_region` VALUES ('3202', '32', '无锡市', '江苏省,无锡市', 2);
INSERT INTO `sys_region` VALUES ('320205', '3202', '锡山区', '江苏省,无锡市,锡山区', 3);
INSERT INTO `sys_region` VALUES ('320206', '3202', '惠山区', '江苏省,无锡市,惠山区', 3);
INSERT INTO `sys_region` VALUES ('320211', '3202', '滨湖区', '江苏省,无锡市,滨湖区', 3);
INSERT INTO `sys_region` VALUES ('320213', '3202', '梁溪区', '江苏省,无锡市,梁溪区', 3);
INSERT INTO `sys_region` VALUES ('320214', '3202', '新吴区', '江苏省,无锡市,新吴区', 3);
INSERT INTO `sys_region` VALUES ('320281', '3202', '江阴市', '江苏省,无锡市,江阴市', 3);
INSERT INTO `sys_region` VALUES ('320282', '3202', '宜兴市', '江苏省,无锡市,宜兴市', 3);
INSERT INTO `sys_region` VALUES ('3203', '32', '徐州市', '江苏省,徐州市', 2);
INSERT INTO `sys_region` VALUES ('320302', '3203', '鼓楼区', '江苏省,徐州市,鼓楼区', 3);
INSERT INTO `sys_region` VALUES ('320303', '3203', '云龙区', '江苏省,徐州市,云龙区', 3);
INSERT INTO `sys_region` VALUES ('320305', '3203', '贾汪区', '江苏省,徐州市,贾汪区', 3);
INSERT INTO `sys_region` VALUES ('320311', '3203', '泉山区', '江苏省,徐州市,泉山区', 3);
INSERT INTO `sys_region` VALUES ('320312', '3203', '铜山区', '江苏省,徐州市,铜山区', 3);
INSERT INTO `sys_region` VALUES ('320321', '3203', '丰县', '江苏省,徐州市,丰县', 3);
INSERT INTO `sys_region` VALUES ('320322', '3203', '沛县', '江苏省,徐州市,沛县', 3);
INSERT INTO `sys_region` VALUES ('320324', '3203', '睢宁县', '江苏省,徐州市,睢宁县', 3);
INSERT INTO `sys_region` VALUES ('320371', '3203', '徐州经济技术开发区', '江苏省,徐州市,徐州经济技术开发区', 3);
INSERT INTO `sys_region` VALUES ('320381', '3203', '新沂市', '江苏省,徐州市,新沂市', 3);
INSERT INTO `sys_region` VALUES ('320382', '3203', '邳州市', '江苏省,徐州市,邳州市', 3);
INSERT INTO `sys_region` VALUES ('3204', '32', '常州市', '江苏省,常州市', 2);
INSERT INTO `sys_region` VALUES ('320402', '3204', '天宁区', '江苏省,常州市,天宁区', 3);
INSERT INTO `sys_region` VALUES ('320404', '3204', '钟楼区', '江苏省,常州市,钟楼区', 3);
INSERT INTO `sys_region` VALUES ('320411', '3204', '新北区', '江苏省,常州市,新北区', 3);
INSERT INTO `sys_region` VALUES ('320412', '3204', '武进区', '江苏省,常州市,武进区', 3);
INSERT INTO `sys_region` VALUES ('320413', '3204', '金坛区', '江苏省,常州市,金坛区', 3);
INSERT INTO `sys_region` VALUES ('320481', '3204', '溧阳市', '江苏省,常州市,溧阳市', 3);
INSERT INTO `sys_region` VALUES ('3205', '32', '苏州市', '江苏省,苏州市', 2);
INSERT INTO `sys_region` VALUES ('320505', '3205', '虎丘区', '江苏省,苏州市,虎丘区', 3);
INSERT INTO `sys_region` VALUES ('320506', '3205', '吴中区', '江苏省,苏州市,吴中区', 3);
INSERT INTO `sys_region` VALUES ('320507', '3205', '相城区', '江苏省,苏州市,相城区', 3);
INSERT INTO `sys_region` VALUES ('320508', '3205', '姑苏区', '江苏省,苏州市,姑苏区', 3);
INSERT INTO `sys_region` VALUES ('320509', '3205', '吴江区', '江苏省,苏州市,吴江区', 3);
INSERT INTO `sys_region` VALUES ('320571', '3205', '苏州工业园区', '江苏省,苏州市,苏州工业园区', 3);
INSERT INTO `sys_region` VALUES ('320581', '3205', '常熟市', '江苏省,苏州市,常熟市', 3);
INSERT INTO `sys_region` VALUES ('320582', '3205', '张家港市', '江苏省,苏州市,张家港市', 3);
INSERT INTO `sys_region` VALUES ('320583', '3205', '昆山市', '江苏省,苏州市,昆山市', 3);
INSERT INTO `sys_region` VALUES ('320585', '3205', '太仓市', '江苏省,苏州市,太仓市', 3);
INSERT INTO `sys_region` VALUES ('3206', '32', '南通市', '江苏省,南通市', 2);
INSERT INTO `sys_region` VALUES ('320602', '3206', '崇川区', '江苏省,南通市,崇川区', 3);
INSERT INTO `sys_region` VALUES ('320611', '3206', '港闸区', '江苏省,南通市,港闸区', 3);
INSERT INTO `sys_region` VALUES ('320612', '3206', '通州区', '江苏省,南通市,通州区', 3);
INSERT INTO `sys_region` VALUES ('320623', '3206', '如东县', '江苏省,南通市,如东县', 3);
INSERT INTO `sys_region` VALUES ('320671', '3206', '南通经济技术开发区', '江苏省,南通市,南通经济技术开发区', 3);
INSERT INTO `sys_region` VALUES ('320681', '3206', '启东市', '江苏省,南通市,启东市', 3);
INSERT INTO `sys_region` VALUES ('320682', '3206', '如皋市', '江苏省,南通市,如皋市', 3);
INSERT INTO `sys_region` VALUES ('320684', '3206', '海门市', '江苏省,南通市,海门市', 3);
INSERT INTO `sys_region` VALUES ('320685', '3206', '海安市', '江苏省,南通市,海安市', 3);
INSERT INTO `sys_region` VALUES ('3207', '32', '连云港市', '江苏省,连云港市', 2);
INSERT INTO `sys_region` VALUES ('320703', '3207', '连云区', '江苏省,连云港市,连云区', 3);
INSERT INTO `sys_region` VALUES ('320706', '3207', '海州区', '江苏省,连云港市,海州区', 3);
INSERT INTO `sys_region` VALUES ('320707', '3207', '赣榆区', '江苏省,连云港市,赣榆区', 3);
INSERT INTO `sys_region` VALUES ('320722', '3207', '东海县', '江苏省,连云港市,东海县', 3);
INSERT INTO `sys_region` VALUES ('320723', '3207', '灌云县', '江苏省,连云港市,灌云县', 3);
INSERT INTO `sys_region` VALUES ('320724', '3207', '灌南县', '江苏省,连云港市,灌南县', 3);
INSERT INTO `sys_region` VALUES ('320771', '3207', '连云港经济技术开发区', '江苏省,连云港市,连云港经济技术开发区', 3);
INSERT INTO `sys_region` VALUES ('320772', '3207', '连云港高新技术产业开发区', '江苏省,连云港市,连云港高新技术产业开发区', 3);
INSERT INTO `sys_region` VALUES ('3208', '32', '淮安市', '江苏省,淮安市', 2);
INSERT INTO `sys_region` VALUES ('320803', '3208', '淮安区', '江苏省,淮安市,淮安区', 3);
INSERT INTO `sys_region` VALUES ('320804', '3208', '淮阴区', '江苏省,淮安市,淮阴区', 3);
INSERT INTO `sys_region` VALUES ('320812', '3208', '清江浦区', '江苏省,淮安市,清江浦区', 3);
INSERT INTO `sys_region` VALUES ('320813', '3208', '洪泽区', '江苏省,淮安市,洪泽区', 3);
INSERT INTO `sys_region` VALUES ('320826', '3208', '涟水县', '江苏省,淮安市,涟水县', 3);
INSERT INTO `sys_region` VALUES ('320830', '3208', '盱眙县', '江苏省,淮安市,盱眙县', 3);
INSERT INTO `sys_region` VALUES ('320831', '3208', '金湖县', '江苏省,淮安市,金湖县', 3);
INSERT INTO `sys_region` VALUES ('320871', '3208', '淮安经济技术开发区', '江苏省,淮安市,淮安经济技术开发区', 3);
INSERT INTO `sys_region` VALUES ('3209', '32', '盐城市', '江苏省,盐城市', 2);
INSERT INTO `sys_region` VALUES ('320902', '3209', '亭湖区', '江苏省,盐城市,亭湖区', 3);
INSERT INTO `sys_region` VALUES ('320903', '3209', '盐都区', '江苏省,盐城市,盐都区', 3);
INSERT INTO `sys_region` VALUES ('320904', '3209', '大丰区', '江苏省,盐城市,大丰区', 3);
INSERT INTO `sys_region` VALUES ('320921', '3209', '响水县', '江苏省,盐城市,响水县', 3);
INSERT INTO `sys_region` VALUES ('320922', '3209', '滨海县', '江苏省,盐城市,滨海县', 3);
INSERT INTO `sys_region` VALUES ('320923', '3209', '阜宁县', '江苏省,盐城市,阜宁县', 3);
INSERT INTO `sys_region` VALUES ('320924', '3209', '射阳县', '江苏省,盐城市,射阳县', 3);
INSERT INTO `sys_region` VALUES ('320925', '3209', '建湖县', '江苏省,盐城市,建湖县', 3);
INSERT INTO `sys_region` VALUES ('320971', '3209', '盐城经济技术开发区', '江苏省,盐城市,盐城经济技术开发区', 3);
INSERT INTO `sys_region` VALUES ('320981', '3209', '东台市', '江苏省,盐城市,东台市', 3);
INSERT INTO `sys_region` VALUES ('3210', '32', '扬州市', '江苏省,扬州市', 2);
INSERT INTO `sys_region` VALUES ('321002', '3210', '广陵区', '江苏省,扬州市,广陵区', 3);
INSERT INTO `sys_region` VALUES ('321003', '3210', '邗江区', '江苏省,扬州市,邗江区', 3);
INSERT INTO `sys_region` VALUES ('321012', '3210', '江都区', '江苏省,扬州市,江都区', 3);
INSERT INTO `sys_region` VALUES ('321023', '3210', '宝应县', '江苏省,扬州市,宝应县', 3);
INSERT INTO `sys_region` VALUES ('321071', '3210', '扬州经济技术开发区', '江苏省,扬州市,扬州经济技术开发区', 3);
INSERT INTO `sys_region` VALUES ('321081', '3210', '仪征市', '江苏省,扬州市,仪征市', 3);
INSERT INTO `sys_region` VALUES ('321084', '3210', '高邮市', '江苏省,扬州市,高邮市', 3);
INSERT INTO `sys_region` VALUES ('3211', '32', '镇江市', '江苏省,镇江市', 2);
INSERT INTO `sys_region` VALUES ('321102', '3211', '京口区', '江苏省,镇江市,京口区', 3);
INSERT INTO `sys_region` VALUES ('321111', '3211', '润州区', '江苏省,镇江市,润州区', 3);
INSERT INTO `sys_region` VALUES ('321112', '3211', '丹徒区', '江苏省,镇江市,丹徒区', 3);
INSERT INTO `sys_region` VALUES ('321171', '3211', '镇江新区', '江苏省,镇江市,镇江新区', 3);
INSERT INTO `sys_region` VALUES ('321181', '3211', '丹阳市', '江苏省,镇江市,丹阳市', 3);
INSERT INTO `sys_region` VALUES ('321182', '3211', '扬中市', '江苏省,镇江市,扬中市', 3);
INSERT INTO `sys_region` VALUES ('321183', '3211', '句容市', '江苏省,镇江市,句容市', 3);
INSERT INTO `sys_region` VALUES ('3212', '32', '泰州市', '江苏省,泰州市', 2);
INSERT INTO `sys_region` VALUES ('321202', '3212', '海陵区', '江苏省,泰州市,海陵区', 3);
INSERT INTO `sys_region` VALUES ('321203', '3212', '高港区', '江苏省,泰州市,高港区', 3);
INSERT INTO `sys_region` VALUES ('321204', '3212', '姜堰区', '江苏省,泰州市,姜堰区', 3);
INSERT INTO `sys_region` VALUES ('321271', '3212', '泰州医药高新技术产业开发区', '江苏省,泰州市,泰州医药高新技术产业开发区', 3);
INSERT INTO `sys_region` VALUES ('321281', '3212', '兴化市', '江苏省,泰州市,兴化市', 3);
INSERT INTO `sys_region` VALUES ('321282', '3212', '靖江市', '江苏省,泰州市,靖江市', 3);
INSERT INTO `sys_region` VALUES ('321283', '3212', '泰兴市', '江苏省,泰州市,泰兴市', 3);
INSERT INTO `sys_region` VALUES ('3213', '32', '宿迁市', '江苏省,宿迁市', 2);
INSERT INTO `sys_region` VALUES ('321302', '3213', '宿城区', '江苏省,宿迁市,宿城区', 3);
INSERT INTO `sys_region` VALUES ('321311', '3213', '宿豫区', '江苏省,宿迁市,宿豫区', 3);
INSERT INTO `sys_region` VALUES ('321322', '3213', '沭阳县', '江苏省,宿迁市,沭阳县', 3);
INSERT INTO `sys_region` VALUES ('321323', '3213', '泗阳县', '江苏省,宿迁市,泗阳县', 3);
INSERT INTO `sys_region` VALUES ('321324', '3213', '泗洪县', '江苏省,宿迁市,泗洪县', 3);
INSERT INTO `sys_region` VALUES ('321371', '3213', '宿迁经济技术开发区', '江苏省,宿迁市,宿迁经济技术开发区', 3);
INSERT INTO `sys_region` VALUES ('33', '0', '浙江省', '浙江省', 1);
INSERT INTO `sys_region` VALUES ('3301', '33', '杭州市', '浙江省,杭州市', 2);
INSERT INTO `sys_region` VALUES ('330102', '3301', '上城区', '浙江省,杭州市,上城区', 3);
INSERT INTO `sys_region` VALUES ('330103', '3301', '下城区', '浙江省,杭州市,下城区', 3);
INSERT INTO `sys_region` VALUES ('330104', '3301', '江干区', '浙江省,杭州市,江干区', 3);
INSERT INTO `sys_region` VALUES ('330105', '3301', '拱墅区', '浙江省,杭州市,拱墅区', 3);
INSERT INTO `sys_region` VALUES ('330106', '3301', '西湖区', '浙江省,杭州市,西湖区', 3);
INSERT INTO `sys_region` VALUES ('330108', '3301', '滨江区', '浙江省,杭州市,滨江区', 3);
INSERT INTO `sys_region` VALUES ('330109', '3301', '萧山区', '浙江省,杭州市,萧山区', 3);
INSERT INTO `sys_region` VALUES ('330110', '3301', '余杭区', '浙江省,杭州市,余杭区', 3);
INSERT INTO `sys_region` VALUES ('330111', '3301', '富阳区', '浙江省,杭州市,富阳区', 3);
INSERT INTO `sys_region` VALUES ('330112', '3301', '临安区', '浙江省,杭州市,临安区', 3);
INSERT INTO `sys_region` VALUES ('330122', '3301', '桐庐县', '浙江省,杭州市,桐庐县', 3);
INSERT INTO `sys_region` VALUES ('330127', '3301', '淳安县', '浙江省,杭州市,淳安县', 3);
INSERT INTO `sys_region` VALUES ('330182', '3301', '建德市', '浙江省,杭州市,建德市', 3);
INSERT INTO `sys_region` VALUES ('3302', '33', '宁波市', '浙江省,宁波市', 2);
INSERT INTO `sys_region` VALUES ('330203', '3302', '海曙区', '浙江省,宁波市,海曙区', 3);
INSERT INTO `sys_region` VALUES ('330205', '3302', '江北区', '浙江省,宁波市,江北区', 3);
INSERT INTO `sys_region` VALUES ('330206', '3302', '北仑区', '浙江省,宁波市,北仑区', 3);
INSERT INTO `sys_region` VALUES ('330211', '3302', '镇海区', '浙江省,宁波市,镇海区', 3);
INSERT INTO `sys_region` VALUES ('330212', '3302', '鄞州区', '浙江省,宁波市,鄞州区', 3);
INSERT INTO `sys_region` VALUES ('330213', '3302', '奉化区', '浙江省,宁波市,奉化区', 3);
INSERT INTO `sys_region` VALUES ('330225', '3302', '象山县', '浙江省,宁波市,象山县', 3);
INSERT INTO `sys_region` VALUES ('330226', '3302', '宁海县', '浙江省,宁波市,宁海县', 3);
INSERT INTO `sys_region` VALUES ('330281', '3302', '余姚市', '浙江省,宁波市,余姚市', 3);
INSERT INTO `sys_region` VALUES ('330282', '3302', '慈溪市', '浙江省,宁波市,慈溪市', 3);
INSERT INTO `sys_region` VALUES ('3303', '33', '温州市', '浙江省,温州市', 2);
INSERT INTO `sys_region` VALUES ('330302', '3303', '鹿城区', '浙江省,温州市,鹿城区', 3);
INSERT INTO `sys_region` VALUES ('330303', '3303', '龙湾区', '浙江省,温州市,龙湾区', 3);
INSERT INTO `sys_region` VALUES ('330304', '3303', '瓯海区', '浙江省,温州市,瓯海区', 3);
INSERT INTO `sys_region` VALUES ('330305', '3303', '洞头区', '浙江省,温州市,洞头区', 3);
INSERT INTO `sys_region` VALUES ('330324', '3303', '永嘉县', '浙江省,温州市,永嘉县', 3);
INSERT INTO `sys_region` VALUES ('330326', '3303', '平阳县', '浙江省,温州市,平阳县', 3);
INSERT INTO `sys_region` VALUES ('330327', '3303', '苍南县', '浙江省,温州市,苍南县', 3);
INSERT INTO `sys_region` VALUES ('330328', '3303', '文成县', '浙江省,温州市,文成县', 3);
INSERT INTO `sys_region` VALUES ('330329', '3303', '泰顺县', '浙江省,温州市,泰顺县', 3);
INSERT INTO `sys_region` VALUES ('330371', '3303', '温州经济技术开发区', '浙江省,温州市,温州经济技术开发区', 3);
INSERT INTO `sys_region` VALUES ('330381', '3303', '瑞安市', '浙江省,温州市,瑞安市', 3);
INSERT INTO `sys_region` VALUES ('330382', '3303', '乐清市', '浙江省,温州市,乐清市', 3);
INSERT INTO `sys_region` VALUES ('3304', '33', '嘉兴市', '浙江省,嘉兴市', 2);
INSERT INTO `sys_region` VALUES ('330402', '3304', '南湖区', '浙江省,嘉兴市,南湖区', 3);
INSERT INTO `sys_region` VALUES ('330411', '3304', '秀洲区', '浙江省,嘉兴市,秀洲区', 3);
INSERT INTO `sys_region` VALUES ('330421', '3304', '嘉善县', '浙江省,嘉兴市,嘉善县', 3);
INSERT INTO `sys_region` VALUES ('330424', '3304', '海盐县', '浙江省,嘉兴市,海盐县', 3);
INSERT INTO `sys_region` VALUES ('330481', '3304', '海宁市', '浙江省,嘉兴市,海宁市', 3);
INSERT INTO `sys_region` VALUES ('330482', '3304', '平湖市', '浙江省,嘉兴市,平湖市', 3);
INSERT INTO `sys_region` VALUES ('330483', '3304', '桐乡市', '浙江省,嘉兴市,桐乡市', 3);
INSERT INTO `sys_region` VALUES ('3305', '33', '湖州市', '浙江省,湖州市', 2);
INSERT INTO `sys_region` VALUES ('330502', '3305', '吴兴区', '浙江省,湖州市,吴兴区', 3);
INSERT INTO `sys_region` VALUES ('330503', '3305', '南浔区', '浙江省,湖州市,南浔区', 3);
INSERT INTO `sys_region` VALUES ('330521', '3305', '德清县', '浙江省,湖州市,德清县', 3);
INSERT INTO `sys_region` VALUES ('330522', '3305', '长兴县', '浙江省,湖州市,长兴县', 3);
INSERT INTO `sys_region` VALUES ('330523', '3305', '安吉县', '浙江省,湖州市,安吉县', 3);
INSERT INTO `sys_region` VALUES ('3306', '33', '绍兴市', '浙江省,绍兴市', 2);
INSERT INTO `sys_region` VALUES ('330602', '3306', '越城区', '浙江省,绍兴市,越城区', 3);
INSERT INTO `sys_region` VALUES ('330603', '3306', '柯桥区', '浙江省,绍兴市,柯桥区', 3);
INSERT INTO `sys_region` VALUES ('330604', '3306', '上虞区', '浙江省,绍兴市,上虞区', 3);
INSERT INTO `sys_region` VALUES ('330624', '3306', '新昌县', '浙江省,绍兴市,新昌县', 3);
INSERT INTO `sys_region` VALUES ('330681', '3306', '诸暨市', '浙江省,绍兴市,诸暨市', 3);
INSERT INTO `sys_region` VALUES ('330683', '3306', '嵊州市', '浙江省,绍兴市,嵊州市', 3);
INSERT INTO `sys_region` VALUES ('3307', '33', '金华市', '浙江省,金华市', 2);
INSERT INTO `sys_region` VALUES ('330702', '3307', '婺城区', '浙江省,金华市,婺城区', 3);
INSERT INTO `sys_region` VALUES ('330703', '3307', '金东区', '浙江省,金华市,金东区', 3);
INSERT INTO `sys_region` VALUES ('330723', '3307', '武义县', '浙江省,金华市,武义县', 3);
INSERT INTO `sys_region` VALUES ('330726', '3307', '浦江县', '浙江省,金华市,浦江县', 3);
INSERT INTO `sys_region` VALUES ('330727', '3307', '磐安县', '浙江省,金华市,磐安县', 3);
INSERT INTO `sys_region` VALUES ('330781', '3307', '兰溪市', '浙江省,金华市,兰溪市', 3);
INSERT INTO `sys_region` VALUES ('330782', '3307', '义乌市', '浙江省,金华市,义乌市', 3);
INSERT INTO `sys_region` VALUES ('330783', '3307', '东阳市', '浙江省,金华市,东阳市', 3);
INSERT INTO `sys_region` VALUES ('330784', '3307', '永康市', '浙江省,金华市,永康市', 3);
INSERT INTO `sys_region` VALUES ('3308', '33', '衢州市', '浙江省,衢州市', 2);
INSERT INTO `sys_region` VALUES ('330802', '3308', '柯城区', '浙江省,衢州市,柯城区', 3);
INSERT INTO `sys_region` VALUES ('330803', '3308', '衢江区', '浙江省,衢州市,衢江区', 3);
INSERT INTO `sys_region` VALUES ('330822', '3308', '常山县', '浙江省,衢州市,常山县', 3);
INSERT INTO `sys_region` VALUES ('330824', '3308', '开化县', '浙江省,衢州市,开化县', 3);
INSERT INTO `sys_region` VALUES ('330825', '3308', '龙游县', '浙江省,衢州市,龙游县', 3);
INSERT INTO `sys_region` VALUES ('330881', '3308', '江山市', '浙江省,衢州市,江山市', 3);
INSERT INTO `sys_region` VALUES ('3309', '33', '舟山市', '浙江省,舟山市', 2);
INSERT INTO `sys_region` VALUES ('330902', '3309', '定海区', '浙江省,舟山市,定海区', 3);
INSERT INTO `sys_region` VALUES ('330903', '3309', '普陀区', '浙江省,舟山市,普陀区', 3);
INSERT INTO `sys_region` VALUES ('330921', '3309', '岱山县', '浙江省,舟山市,岱山县', 3);
INSERT INTO `sys_region` VALUES ('330922', '3309', '嵊泗县', '浙江省,舟山市,嵊泗县', 3);
INSERT INTO `sys_region` VALUES ('3310', '33', '台州市', '浙江省,台州市', 2);
INSERT INTO `sys_region` VALUES ('331002', '3310', '椒江区', '浙江省,台州市,椒江区', 3);
INSERT INTO `sys_region` VALUES ('331003', '3310', '黄岩区', '浙江省,台州市,黄岩区', 3);
INSERT INTO `sys_region` VALUES ('331004', '3310', '路桥区', '浙江省,台州市,路桥区', 3);
INSERT INTO `sys_region` VALUES ('331022', '3310', '三门县', '浙江省,台州市,三门县', 3);
INSERT INTO `sys_region` VALUES ('331023', '3310', '天台县', '浙江省,台州市,天台县', 3);
INSERT INTO `sys_region` VALUES ('331024', '3310', '仙居县', '浙江省,台州市,仙居县', 3);
INSERT INTO `sys_region` VALUES ('331081', '3310', '温岭市', '浙江省,台州市,温岭市', 3);
INSERT INTO `sys_region` VALUES ('331082', '3310', '临海市', '浙江省,台州市,临海市', 3);
INSERT INTO `sys_region` VALUES ('331083', '3310', '玉环市', '浙江省,台州市,玉环市', 3);
INSERT INTO `sys_region` VALUES ('3311', '33', '丽水市', '浙江省,丽水市', 2);
INSERT INTO `sys_region` VALUES ('331102', '3311', '莲都区', '浙江省,丽水市,莲都区', 3);
INSERT INTO `sys_region` VALUES ('331121', '3311', '青田县', '浙江省,丽水市,青田县', 3);
INSERT INTO `sys_region` VALUES ('331122', '3311', '缙云县', '浙江省,丽水市,缙云县', 3);
INSERT INTO `sys_region` VALUES ('331123', '3311', '遂昌县', '浙江省,丽水市,遂昌县', 3);
INSERT INTO `sys_region` VALUES ('331124', '3311', '松阳县', '浙江省,丽水市,松阳县', 3);
INSERT INTO `sys_region` VALUES ('331125', '3311', '云和县', '浙江省,丽水市,云和县', 3);
INSERT INTO `sys_region` VALUES ('331126', '3311', '庆元县', '浙江省,丽水市,庆元县', 3);
INSERT INTO `sys_region` VALUES ('331127', '3311', '景宁畲族自治县', '浙江省,丽水市,景宁畲族自治县', 3);
INSERT INTO `sys_region` VALUES ('331181', '3311', '龙泉市', '浙江省,丽水市,龙泉市', 3);
INSERT INTO `sys_region` VALUES ('34', '0', '安徽省', '安徽省', 1);
INSERT INTO `sys_region` VALUES ('3401', '34', '合肥市', '安徽省,合肥市', 2);
INSERT INTO `sys_region` VALUES ('340102', '3401', '瑶海区', '安徽省,合肥市,瑶海区', 3);
INSERT INTO `sys_region` VALUES ('340103', '3401', '庐阳区', '安徽省,合肥市,庐阳区', 3);
INSERT INTO `sys_region` VALUES ('340104', '3401', '蜀山区', '安徽省,合肥市,蜀山区', 3);
INSERT INTO `sys_region` VALUES ('340111', '3401', '包河区', '安徽省,合肥市,包河区', 3);
INSERT INTO `sys_region` VALUES ('340121', '3401', '长丰县', '安徽省,合肥市,长丰县', 3);
INSERT INTO `sys_region` VALUES ('340122', '3401', '肥东县', '安徽省,合肥市,肥东县', 3);
INSERT INTO `sys_region` VALUES ('340123', '3401', '肥西县', '安徽省,合肥市,肥西县', 3);
INSERT INTO `sys_region` VALUES ('340124', '3401', '庐江县', '安徽省,合肥市,庐江县', 3);
INSERT INTO `sys_region` VALUES ('340171', '3401', '合肥高新技术产业开发区', '安徽省,合肥市,合肥高新技术产业开发区', 3);
INSERT INTO `sys_region` VALUES ('340172', '3401', '合肥经济技术开发区', '安徽省,合肥市,合肥经济技术开发区', 3);
INSERT INTO `sys_region` VALUES ('340173', '3401', '合肥新站高新技术产业开发区', '安徽省,合肥市,合肥新站高新技术产业开发区', 3);
INSERT INTO `sys_region` VALUES ('340181', '3401', '巢湖市', '安徽省,合肥市,巢湖市', 3);
INSERT INTO `sys_region` VALUES ('3402', '34', '芜湖市', '安徽省,芜湖市', 2);
INSERT INTO `sys_region` VALUES ('340202', '3402', '镜湖区', '安徽省,芜湖市,镜湖区', 3);
INSERT INTO `sys_region` VALUES ('340203', '3402', '弋江区', '安徽省,芜湖市,弋江区', 3);
INSERT INTO `sys_region` VALUES ('340207', '3402', '鸠江区', '安徽省,芜湖市,鸠江区', 3);
INSERT INTO `sys_region` VALUES ('340208', '3402', '三山区', '安徽省,芜湖市,三山区', 3);
INSERT INTO `sys_region` VALUES ('340221', '3402', '芜湖县', '安徽省,芜湖市,芜湖县', 3);
INSERT INTO `sys_region` VALUES ('340222', '3402', '繁昌县', '安徽省,芜湖市,繁昌县', 3);
INSERT INTO `sys_region` VALUES ('340223', '3402', '南陵县', '安徽省,芜湖市,南陵县', 3);
INSERT INTO `sys_region` VALUES ('340225', '3402', '无为县', '安徽省,芜湖市,无为县', 3);
INSERT INTO `sys_region` VALUES ('340271', '3402', '芜湖经济技术开发区', '安徽省,芜湖市,芜湖经济技术开发区', 3);
INSERT INTO `sys_region` VALUES ('340272', '3402', '安徽芜湖长江大桥经济开发区', '安徽省,芜湖市,安徽芜湖长江大桥经济开发区', 3);
INSERT INTO `sys_region` VALUES ('3403', '34', '蚌埠市', '安徽省,蚌埠市', 2);
INSERT INTO `sys_region` VALUES ('340302', '3403', '龙子湖区', '安徽省,蚌埠市,龙子湖区', 3);
INSERT INTO `sys_region` VALUES ('340303', '3403', '蚌山区', '安徽省,蚌埠市,蚌山区', 3);
INSERT INTO `sys_region` VALUES ('340304', '3403', '禹会区', '安徽省,蚌埠市,禹会区', 3);
INSERT INTO `sys_region` VALUES ('340311', '3403', '淮上区', '安徽省,蚌埠市,淮上区', 3);
INSERT INTO `sys_region` VALUES ('340321', '3403', '怀远县', '安徽省,蚌埠市,怀远县', 3);
INSERT INTO `sys_region` VALUES ('340322', '3403', '五河县', '安徽省,蚌埠市,五河县', 3);
INSERT INTO `sys_region` VALUES ('340323', '3403', '固镇县', '安徽省,蚌埠市,固镇县', 3);
INSERT INTO `sys_region` VALUES ('340371', '3403', '蚌埠市高新技术开发区', '安徽省,蚌埠市,蚌埠市高新技术开发区', 3);
INSERT INTO `sys_region` VALUES ('340372', '3403', '蚌埠市经济开发区', '安徽省,蚌埠市,蚌埠市经济开发区', 3);
INSERT INTO `sys_region` VALUES ('3404', '34', '淮南市', '安徽省,淮南市', 2);
INSERT INTO `sys_region` VALUES ('340402', '3404', '大通区', '安徽省,淮南市,大通区', 3);
INSERT INTO `sys_region` VALUES ('340403', '3404', '田家庵区', '安徽省,淮南市,田家庵区', 3);
INSERT INTO `sys_region` VALUES ('340404', '3404', '谢家集区', '安徽省,淮南市,谢家集区', 3);
INSERT INTO `sys_region` VALUES ('340405', '3404', '八公山区', '安徽省,淮南市,八公山区', 3);
INSERT INTO `sys_region` VALUES ('340406', '3404', '潘集区', '安徽省,淮南市,潘集区', 3);
INSERT INTO `sys_region` VALUES ('340421', '3404', '凤台县', '安徽省,淮南市,凤台县', 3);
INSERT INTO `sys_region` VALUES ('340422', '3404', '寿县', '安徽省,淮南市,寿县', 3);
INSERT INTO `sys_region` VALUES ('3405', '34', '马鞍山市', '安徽省,马鞍山市', 2);
INSERT INTO `sys_region` VALUES ('340503', '3405', '花山区', '安徽省,马鞍山市,花山区', 3);
INSERT INTO `sys_region` VALUES ('340504', '3405', '雨山区', '安徽省,马鞍山市,雨山区', 3);
INSERT INTO `sys_region` VALUES ('340506', '3405', '博望区', '安徽省,马鞍山市,博望区', 3);
INSERT INTO `sys_region` VALUES ('340521', '3405', '当涂县', '安徽省,马鞍山市,当涂县', 3);
INSERT INTO `sys_region` VALUES ('340522', '3405', '含山县', '安徽省,马鞍山市,含山县', 3);
INSERT INTO `sys_region` VALUES ('340523', '3405', '和县', '安徽省,马鞍山市,和县', 3);
INSERT INTO `sys_region` VALUES ('3406', '34', '淮北市', '安徽省,淮北市', 2);
INSERT INTO `sys_region` VALUES ('340602', '3406', '杜集区', '安徽省,淮北市,杜集区', 3);
INSERT INTO `sys_region` VALUES ('340603', '3406', '相山区', '安徽省,淮北市,相山区', 3);
INSERT INTO `sys_region` VALUES ('340604', '3406', '烈山区', '安徽省,淮北市,烈山区', 3);
INSERT INTO `sys_region` VALUES ('340621', '3406', '濉溪县', '安徽省,淮北市,濉溪县', 3);
INSERT INTO `sys_region` VALUES ('3407', '34', '铜陵市', '安徽省,铜陵市', 2);
INSERT INTO `sys_region` VALUES ('340705', '3407', '铜官区', '安徽省,铜陵市,铜官区', 3);
INSERT INTO `sys_region` VALUES ('340706', '3407', '义安区', '安徽省,铜陵市,义安区', 3);
INSERT INTO `sys_region` VALUES ('340711', '3407', '郊区', '安徽省,铜陵市,郊区', 3);
INSERT INTO `sys_region` VALUES ('340722', '3407', '枞阳县', '安徽省,铜陵市,枞阳县', 3);
INSERT INTO `sys_region` VALUES ('3408', '34', '安庆市', '安徽省,安庆市', 2);
INSERT INTO `sys_region` VALUES ('340802', '3408', '迎江区', '安徽省,安庆市,迎江区', 3);
INSERT INTO `sys_region` VALUES ('340803', '3408', '大观区', '安徽省,安庆市,大观区', 3);
INSERT INTO `sys_region` VALUES ('340811', '3408', '宜秀区', '安徽省,安庆市,宜秀区', 3);
INSERT INTO `sys_region` VALUES ('340822', '3408', '怀宁县', '安徽省,安庆市,怀宁县', 3);
INSERT INTO `sys_region` VALUES ('340825', '3408', '太湖县', '安徽省,安庆市,太湖县', 3);
INSERT INTO `sys_region` VALUES ('340826', '3408', '宿松县', '安徽省,安庆市,宿松县', 3);
INSERT INTO `sys_region` VALUES ('340827', '3408', '望江县', '安徽省,安庆市,望江县', 3);
INSERT INTO `sys_region` VALUES ('340828', '3408', '岳西县', '安徽省,安庆市,岳西县', 3);
INSERT INTO `sys_region` VALUES ('340871', '3408', '安徽安庆经济开发区', '安徽省,安庆市,安徽安庆经济开发区', 3);
INSERT INTO `sys_region` VALUES ('340881', '3408', '桐城市', '安徽省,安庆市,桐城市', 3);
INSERT INTO `sys_region` VALUES ('340882', '3408', '潜山市', '安徽省,安庆市,潜山市', 3);
INSERT INTO `sys_region` VALUES ('3410', '34', '黄山市', '安徽省,黄山市', 2);
INSERT INTO `sys_region` VALUES ('341002', '3410', '屯溪区', '安徽省,黄山市,屯溪区', 3);
INSERT INTO `sys_region` VALUES ('341003', '3410', '黄山区', '安徽省,黄山市,黄山区', 3);
INSERT INTO `sys_region` VALUES ('341004', '3410', '徽州区', '安徽省,黄山市,徽州区', 3);
INSERT INTO `sys_region` VALUES ('341021', '3410', '歙县', '安徽省,黄山市,歙县', 3);
INSERT INTO `sys_region` VALUES ('341022', '3410', '休宁县', '安徽省,黄山市,休宁县', 3);
INSERT INTO `sys_region` VALUES ('341023', '3410', '黟县', '安徽省,黄山市,黟县', 3);
INSERT INTO `sys_region` VALUES ('341024', '3410', '祁门县', '安徽省,黄山市,祁门县', 3);
INSERT INTO `sys_region` VALUES ('3411', '34', '滁州市', '安徽省,滁州市', 2);
INSERT INTO `sys_region` VALUES ('341102', '3411', '琅琊区', '安徽省,滁州市,琅琊区', 3);
INSERT INTO `sys_region` VALUES ('341103', '3411', '南谯区', '安徽省,滁州市,南谯区', 3);
INSERT INTO `sys_region` VALUES ('341122', '3411', '来安县', '安徽省,滁州市,来安县', 3);
INSERT INTO `sys_region` VALUES ('341124', '3411', '全椒县', '安徽省,滁州市,全椒县', 3);
INSERT INTO `sys_region` VALUES ('341125', '3411', '定远县', '安徽省,滁州市,定远县', 3);
INSERT INTO `sys_region` VALUES ('341126', '3411', '凤阳县', '安徽省,滁州市,凤阳县', 3);
INSERT INTO `sys_region` VALUES ('341171', '3411', '苏滁现代产业园', '安徽省,滁州市,苏滁现代产业园', 3);
INSERT INTO `sys_region` VALUES ('341172', '3411', '滁州经济技术开发区', '安徽省,滁州市,滁州经济技术开发区', 3);
INSERT INTO `sys_region` VALUES ('341181', '3411', '天长市', '安徽省,滁州市,天长市', 3);
INSERT INTO `sys_region` VALUES ('341182', '3411', '明光市', '安徽省,滁州市,明光市', 3);
INSERT INTO `sys_region` VALUES ('3412', '34', '阜阳市', '安徽省,阜阳市', 2);
INSERT INTO `sys_region` VALUES ('341202', '3412', '颍州区', '安徽省,阜阳市,颍州区', 3);
INSERT INTO `sys_region` VALUES ('341203', '3412', '颍东区', '安徽省,阜阳市,颍东区', 3);
INSERT INTO `sys_region` VALUES ('341204', '3412', '颍泉区', '安徽省,阜阳市,颍泉区', 3);
INSERT INTO `sys_region` VALUES ('341221', '3412', '临泉县', '安徽省,阜阳市,临泉县', 3);
INSERT INTO `sys_region` VALUES ('341222', '3412', '太和县', '安徽省,阜阳市,太和县', 3);
INSERT INTO `sys_region` VALUES ('341225', '3412', '阜南县', '安徽省,阜阳市,阜南县', 3);
INSERT INTO `sys_region` VALUES ('341226', '3412', '颍上县', '安徽省,阜阳市,颍上县', 3);
INSERT INTO `sys_region` VALUES ('341271', '3412', '阜阳合肥现代产业园区', '安徽省,阜阳市,阜阳合肥现代产业园区', 3);
INSERT INTO `sys_region` VALUES ('341272', '3412', '阜阳经济技术开发区', '安徽省,阜阳市,阜阳经济技术开发区', 3);
INSERT INTO `sys_region` VALUES ('341282', '3412', '界首市', '安徽省,阜阳市,界首市', 3);
INSERT INTO `sys_region` VALUES ('3413', '34', '宿州市', '安徽省,宿州市', 2);
INSERT INTO `sys_region` VALUES ('341302', '3413', '埇桥区', '安徽省,宿州市,埇桥区', 3);
INSERT INTO `sys_region` VALUES ('341321', '3413', '砀山县', '安徽省,宿州市,砀山县', 3);
INSERT INTO `sys_region` VALUES ('341322', '3413', '萧县', '安徽省,宿州市,萧县', 3);
INSERT INTO `sys_region` VALUES ('341323', '3413', '灵璧县', '安徽省,宿州市,灵璧县', 3);
INSERT INTO `sys_region` VALUES ('341324', '3413', '泗县', '安徽省,宿州市,泗县', 3);
INSERT INTO `sys_region` VALUES ('341371', '3413', '宿州马鞍山现代产业园区', '安徽省,宿州市,宿州马鞍山现代产业园区', 3);
INSERT INTO `sys_region` VALUES ('341372', '3413', '宿州经济技术开发区', '安徽省,宿州市,宿州经济技术开发区', 3);
INSERT INTO `sys_region` VALUES ('3415', '34', '六安市', '安徽省,六安市', 2);
INSERT INTO `sys_region` VALUES ('341502', '3415', '金安区', '安徽省,六安市,金安区', 3);
INSERT INTO `sys_region` VALUES ('341503', '3415', '裕安区', '安徽省,六安市,裕安区', 3);
INSERT INTO `sys_region` VALUES ('341504', '3415', '叶集区', '安徽省,六安市,叶集区', 3);
INSERT INTO `sys_region` VALUES ('341522', '3415', '霍邱县', '安徽省,六安市,霍邱县', 3);
INSERT INTO `sys_region` VALUES ('341523', '3415', '舒城县', '安徽省,六安市,舒城县', 3);
INSERT INTO `sys_region` VALUES ('341524', '3415', '金寨县', '安徽省,六安市,金寨县', 3);
INSERT INTO `sys_region` VALUES ('341525', '3415', '霍山县', '安徽省,六安市,霍山县', 3);
INSERT INTO `sys_region` VALUES ('3416', '34', '亳州市', '安徽省,亳州市', 2);
INSERT INTO `sys_region` VALUES ('341602', '3416', '谯城区', '安徽省,亳州市,谯城区', 3);
INSERT INTO `sys_region` VALUES ('341621', '3416', '涡阳县', '安徽省,亳州市,涡阳县', 3);
INSERT INTO `sys_region` VALUES ('341622', '3416', '蒙城县', '安徽省,亳州市,蒙城县', 3);
INSERT INTO `sys_region` VALUES ('341623', '3416', '利辛县', '安徽省,亳州市,利辛县', 3);
INSERT INTO `sys_region` VALUES ('3417', '34', '池州市', '安徽省,池州市', 2);
INSERT INTO `sys_region` VALUES ('341702', '3417', '贵池区', '安徽省,池州市,贵池区', 3);
INSERT INTO `sys_region` VALUES ('341721', '3417', '东至县', '安徽省,池州市,东至县', 3);
INSERT INTO `sys_region` VALUES ('341722', '3417', '石台县', '安徽省,池州市,石台县', 3);
INSERT INTO `sys_region` VALUES ('341723', '3417', '青阳县', '安徽省,池州市,青阳县', 3);
INSERT INTO `sys_region` VALUES ('3418', '34', '宣城市', '安徽省,宣城市', 2);
INSERT INTO `sys_region` VALUES ('341802', '3418', '宣州区', '安徽省,宣城市,宣州区', 3);
INSERT INTO `sys_region` VALUES ('341821', '3418', '郎溪县', '安徽省,宣城市,郎溪县', 3);
INSERT INTO `sys_region` VALUES ('341822', '3418', '广德县', '安徽省,宣城市,广德县', 3);
INSERT INTO `sys_region` VALUES ('341823', '3418', '泾县', '安徽省,宣城市,泾县', 3);
INSERT INTO `sys_region` VALUES ('341824', '3418', '绩溪县', '安徽省,宣城市,绩溪县', 3);
INSERT INTO `sys_region` VALUES ('341825', '3418', '旌德县', '安徽省,宣城市,旌德县', 3);
INSERT INTO `sys_region` VALUES ('341871', '3418', '宣城市经济开发区', '安徽省,宣城市,宣城市经济开发区', 3);
INSERT INTO `sys_region` VALUES ('341881', '3418', '宁国市', '安徽省,宣城市,宁国市', 3);
INSERT INTO `sys_region` VALUES ('35', '0', '福建省', '福建省', 1);
INSERT INTO `sys_region` VALUES ('3501', '35', '福州市', '福建省,福州市', 2);
INSERT INTO `sys_region` VALUES ('350102', '3501', '鼓楼区', '福建省,福州市,鼓楼区', 3);
INSERT INTO `sys_region` VALUES ('350103', '3501', '台江区', '福建省,福州市,台江区', 3);
INSERT INTO `sys_region` VALUES ('350104', '3501', '仓山区', '福建省,福州市,仓山区', 3);
INSERT INTO `sys_region` VALUES ('350105', '3501', '马尾区', '福建省,福州市,马尾区', 3);
INSERT INTO `sys_region` VALUES ('350111', '3501', '晋安区', '福建省,福州市,晋安区', 3);
INSERT INTO `sys_region` VALUES ('350112', '3501', '长乐区', '福建省,福州市,长乐区', 3);
INSERT INTO `sys_region` VALUES ('350121', '3501', '闽侯县', '福建省,福州市,闽侯县', 3);
INSERT INTO `sys_region` VALUES ('350122', '3501', '连江县', '福建省,福州市,连江县', 3);
INSERT INTO `sys_region` VALUES ('350123', '3501', '罗源县', '福建省,福州市,罗源县', 3);
INSERT INTO `sys_region` VALUES ('350124', '3501', '闽清县', '福建省,福州市,闽清县', 3);
INSERT INTO `sys_region` VALUES ('350125', '3501', '永泰县', '福建省,福州市,永泰县', 3);
INSERT INTO `sys_region` VALUES ('350128', '3501', '平潭县', '福建省,福州市,平潭县', 3);
INSERT INTO `sys_region` VALUES ('350181', '3501', '福清市', '福建省,福州市,福清市', 3);
INSERT INTO `sys_region` VALUES ('3502', '35', '厦门市', '福建省,厦门市', 2);
INSERT INTO `sys_region` VALUES ('350203', '3502', '思明区', '福建省,厦门市,思明区', 3);
INSERT INTO `sys_region` VALUES ('350205', '3502', '海沧区', '福建省,厦门市,海沧区', 3);
INSERT INTO `sys_region` VALUES ('350206', '3502', '湖里区', '福建省,厦门市,湖里区', 3);
INSERT INTO `sys_region` VALUES ('350211', '3502', '集美区', '福建省,厦门市,集美区', 3);
INSERT INTO `sys_region` VALUES ('350212', '3502', '同安区', '福建省,厦门市,同安区', 3);
INSERT INTO `sys_region` VALUES ('350213', '3502', '翔安区', '福建省,厦门市,翔安区', 3);
INSERT INTO `sys_region` VALUES ('3503', '35', '莆田市', '福建省,莆田市', 2);
INSERT INTO `sys_region` VALUES ('350302', '3503', '城厢区', '福建省,莆田市,城厢区', 3);
INSERT INTO `sys_region` VALUES ('350303', '3503', '涵江区', '福建省,莆田市,涵江区', 3);
INSERT INTO `sys_region` VALUES ('350304', '3503', '荔城区', '福建省,莆田市,荔城区', 3);
INSERT INTO `sys_region` VALUES ('350305', '3503', '秀屿区', '福建省,莆田市,秀屿区', 3);
INSERT INTO `sys_region` VALUES ('350322', '3503', '仙游县', '福建省,莆田市,仙游县', 3);
INSERT INTO `sys_region` VALUES ('3504', '35', '三明市', '福建省,三明市', 2);
INSERT INTO `sys_region` VALUES ('350402', '3504', '梅列区', '福建省,三明市,梅列区', 3);
INSERT INTO `sys_region` VALUES ('350403', '3504', '三元区', '福建省,三明市,三元区', 3);
INSERT INTO `sys_region` VALUES ('350421', '3504', '明溪县', '福建省,三明市,明溪县', 3);
INSERT INTO `sys_region` VALUES ('350423', '3504', '清流县', '福建省,三明市,清流县', 3);
INSERT INTO `sys_region` VALUES ('350424', '3504', '宁化县', '福建省,三明市,宁化县', 3);
INSERT INTO `sys_region` VALUES ('350425', '3504', '大田县', '福建省,三明市,大田县', 3);
INSERT INTO `sys_region` VALUES ('350426', '3504', '尤溪县', '福建省,三明市,尤溪县', 3);
INSERT INTO `sys_region` VALUES ('350427', '3504', '沙县', '福建省,三明市,沙县', 3);
INSERT INTO `sys_region` VALUES ('350428', '3504', '将乐县', '福建省,三明市,将乐县', 3);
INSERT INTO `sys_region` VALUES ('350429', '3504', '泰宁县', '福建省,三明市,泰宁县', 3);
INSERT INTO `sys_region` VALUES ('350430', '3504', '建宁县', '福建省,三明市,建宁县', 3);
INSERT INTO `sys_region` VALUES ('350481', '3504', '永安市', '福建省,三明市,永安市', 3);
INSERT INTO `sys_region` VALUES ('3505', '35', '泉州市', '福建省,泉州市', 2);
INSERT INTO `sys_region` VALUES ('350502', '3505', '鲤城区', '福建省,泉州市,鲤城区', 3);
INSERT INTO `sys_region` VALUES ('350503', '3505', '丰泽区', '福建省,泉州市,丰泽区', 3);
INSERT INTO `sys_region` VALUES ('350504', '3505', '洛江区', '福建省,泉州市,洛江区', 3);
INSERT INTO `sys_region` VALUES ('350505', '3505', '泉港区', '福建省,泉州市,泉港区', 3);
INSERT INTO `sys_region` VALUES ('350521', '3505', '惠安县', '福建省,泉州市,惠安县', 3);
INSERT INTO `sys_region` VALUES ('350524', '3505', '安溪县', '福建省,泉州市,安溪县', 3);
INSERT INTO `sys_region` VALUES ('350525', '3505', '永春县', '福建省,泉州市,永春县', 3);
INSERT INTO `sys_region` VALUES ('350526', '3505', '德化县', '福建省,泉州市,德化县', 3);
INSERT INTO `sys_region` VALUES ('350527', '3505', '金门县', '福建省,泉州市,金门县', 3);
INSERT INTO `sys_region` VALUES ('350581', '3505', '石狮市', '福建省,泉州市,石狮市', 3);
INSERT INTO `sys_region` VALUES ('350582', '3505', '晋江市', '福建省,泉州市,晋江市', 3);
INSERT INTO `sys_region` VALUES ('350583', '3505', '南安市', '福建省,泉州市,南安市', 3);
INSERT INTO `sys_region` VALUES ('3506', '35', '漳州市', '福建省,漳州市', 2);
INSERT INTO `sys_region` VALUES ('350602', '3506', '芗城区', '福建省,漳州市,芗城区', 3);
INSERT INTO `sys_region` VALUES ('350603', '3506', '龙文区', '福建省,漳州市,龙文区', 3);
INSERT INTO `sys_region` VALUES ('350622', '3506', '云霄县', '福建省,漳州市,云霄县', 3);
INSERT INTO `sys_region` VALUES ('350623', '3506', '漳浦县', '福建省,漳州市,漳浦县', 3);
INSERT INTO `sys_region` VALUES ('350624', '3506', '诏安县', '福建省,漳州市,诏安县', 3);
INSERT INTO `sys_region` VALUES ('350625', '3506', '长泰县', '福建省,漳州市,长泰县', 3);
INSERT INTO `sys_region` VALUES ('350626', '3506', '东山县', '福建省,漳州市,东山县', 3);
INSERT INTO `sys_region` VALUES ('350627', '3506', '南靖县', '福建省,漳州市,南靖县', 3);
INSERT INTO `sys_region` VALUES ('350628', '3506', '平和县', '福建省,漳州市,平和县', 3);
INSERT INTO `sys_region` VALUES ('350629', '3506', '华安县', '福建省,漳州市,华安县', 3);
INSERT INTO `sys_region` VALUES ('350681', '3506', '龙海市', '福建省,漳州市,龙海市', 3);
INSERT INTO `sys_region` VALUES ('3507', '35', '南平市', '福建省,南平市', 2);
INSERT INTO `sys_region` VALUES ('350702', '3507', '延平区', '福建省,南平市,延平区', 3);
INSERT INTO `sys_region` VALUES ('350703', '3507', '建阳区', '福建省,南平市,建阳区', 3);
INSERT INTO `sys_region` VALUES ('350721', '3507', '顺昌县', '福建省,南平市,顺昌县', 3);
INSERT INTO `sys_region` VALUES ('350722', '3507', '浦城县', '福建省,南平市,浦城县', 3);
INSERT INTO `sys_region` VALUES ('350723', '3507', '光泽县', '福建省,南平市,光泽县', 3);
INSERT INTO `sys_region` VALUES ('350724', '3507', '松溪县', '福建省,南平市,松溪县', 3);
INSERT INTO `sys_region` VALUES ('350725', '3507', '政和县', '福建省,南平市,政和县', 3);
INSERT INTO `sys_region` VALUES ('350781', '3507', '邵武市', '福建省,南平市,邵武市', 3);
INSERT INTO `sys_region` VALUES ('350782', '3507', '武夷山市', '福建省,南平市,武夷山市', 3);
INSERT INTO `sys_region` VALUES ('350783', '3507', '建瓯市', '福建省,南平市,建瓯市', 3);
INSERT INTO `sys_region` VALUES ('3508', '35', '龙岩市', '福建省,龙岩市', 2);
INSERT INTO `sys_region` VALUES ('350802', '3508', '新罗区', '福建省,龙岩市,新罗区', 3);
INSERT INTO `sys_region` VALUES ('350803', '3508', '永定区', '福建省,龙岩市,永定区', 3);
INSERT INTO `sys_region` VALUES ('350821', '3508', '长汀县', '福建省,龙岩市,长汀县', 3);
INSERT INTO `sys_region` VALUES ('350823', '3508', '上杭县', '福建省,龙岩市,上杭县', 3);
INSERT INTO `sys_region` VALUES ('350824', '3508', '武平县', '福建省,龙岩市,武平县', 3);
INSERT INTO `sys_region` VALUES ('350825', '3508', '连城县', '福建省,龙岩市,连城县', 3);
INSERT INTO `sys_region` VALUES ('350881', '3508', '漳平市', '福建省,龙岩市,漳平市', 3);
INSERT INTO `sys_region` VALUES ('3509', '35', '宁德市', '福建省,宁德市', 2);
INSERT INTO `sys_region` VALUES ('350902', '3509', '蕉城区', '福建省,宁德市,蕉城区', 3);
INSERT INTO `sys_region` VALUES ('350921', '3509', '霞浦县', '福建省,宁德市,霞浦县', 3);
INSERT INTO `sys_region` VALUES ('350922', '3509', '古田县', '福建省,宁德市,古田县', 3);
INSERT INTO `sys_region` VALUES ('350923', '3509', '屏南县', '福建省,宁德市,屏南县', 3);
INSERT INTO `sys_region` VALUES ('350924', '3509', '寿宁县', '福建省,宁德市,寿宁县', 3);
INSERT INTO `sys_region` VALUES ('350925', '3509', '周宁县', '福建省,宁德市,周宁县', 3);
INSERT INTO `sys_region` VALUES ('350926', '3509', '柘荣县', '福建省,宁德市,柘荣县', 3);
INSERT INTO `sys_region` VALUES ('350981', '3509', '福安市', '福建省,宁德市,福安市', 3);
INSERT INTO `sys_region` VALUES ('350982', '3509', '福鼎市', '福建省,宁德市,福鼎市', 3);
INSERT INTO `sys_region` VALUES ('36', '0', '江西省', '江西省', 1);
INSERT INTO `sys_region` VALUES ('3601', '36', '南昌市', '江西省,南昌市', 2);
INSERT INTO `sys_region` VALUES ('360102', '3601', '东湖区', '江西省,南昌市,东湖区', 3);
INSERT INTO `sys_region` VALUES ('360103', '3601', '西湖区', '江西省,南昌市,西湖区', 3);
INSERT INTO `sys_region` VALUES ('360104', '3601', '青云谱区', '江西省,南昌市,青云谱区', 3);
INSERT INTO `sys_region` VALUES ('360105', '3601', '湾里区', '江西省,南昌市,湾里区', 3);
INSERT INTO `sys_region` VALUES ('360111', '3601', '青山湖区', '江西省,南昌市,青山湖区', 3);
INSERT INTO `sys_region` VALUES ('360112', '3601', '新建区', '江西省,南昌市,新建区', 3);
INSERT INTO `sys_region` VALUES ('360121', '3601', '南昌县', '江西省,南昌市,南昌县', 3);
INSERT INTO `sys_region` VALUES ('360123', '3601', '安义县', '江西省,南昌市,安义县', 3);
INSERT INTO `sys_region` VALUES ('360124', '3601', '进贤县', '江西省,南昌市,进贤县', 3);
INSERT INTO `sys_region` VALUES ('3602', '36', '景德镇市', '江西省,景德镇市', 2);
INSERT INTO `sys_region` VALUES ('360202', '3602', '昌江区', '江西省,景德镇市,昌江区', 3);
INSERT INTO `sys_region` VALUES ('360203', '3602', '珠山区', '江西省,景德镇市,珠山区', 3);
INSERT INTO `sys_region` VALUES ('360222', '3602', '浮梁县', '江西省,景德镇市,浮梁县', 3);
INSERT INTO `sys_region` VALUES ('360281', '3602', '乐平市', '江西省,景德镇市,乐平市', 3);
INSERT INTO `sys_region` VALUES ('3603', '36', '萍乡市', '江西省,萍乡市', 2);
INSERT INTO `sys_region` VALUES ('360302', '3603', '安源区', '江西省,萍乡市,安源区', 3);
INSERT INTO `sys_region` VALUES ('360313', '3603', '湘东区', '江西省,萍乡市,湘东区', 3);
INSERT INTO `sys_region` VALUES ('360321', '3603', '莲花县', '江西省,萍乡市,莲花县', 3);
INSERT INTO `sys_region` VALUES ('360322', '3603', '上栗县', '江西省,萍乡市,上栗县', 3);
INSERT INTO `sys_region` VALUES ('360323', '3603', '芦溪县', '江西省,萍乡市,芦溪县', 3);
INSERT INTO `sys_region` VALUES ('3604', '36', '九江市', '江西省,九江市', 2);
INSERT INTO `sys_region` VALUES ('360402', '3604', '濂溪区', '江西省,九江市,濂溪区', 3);
INSERT INTO `sys_region` VALUES ('360403', '3604', '浔阳区', '江西省,九江市,浔阳区', 3);
INSERT INTO `sys_region` VALUES ('360404', '3604', '柴桑区', '江西省,九江市,柴桑区', 3);
INSERT INTO `sys_region` VALUES ('360423', '3604', '武宁县', '江西省,九江市,武宁县', 3);
INSERT INTO `sys_region` VALUES ('360424', '3604', '修水县', '江西省,九江市,修水县', 3);
INSERT INTO `sys_region` VALUES ('360425', '3604', '永修县', '江西省,九江市,永修县', 3);
INSERT INTO `sys_region` VALUES ('360426', '3604', '德安县', '江西省,九江市,德安县', 3);
INSERT INTO `sys_region` VALUES ('360428', '3604', '都昌县', '江西省,九江市,都昌县', 3);
INSERT INTO `sys_region` VALUES ('360429', '3604', '湖口县', '江西省,九江市,湖口县', 3);
INSERT INTO `sys_region` VALUES ('360430', '3604', '彭泽县', '江西省,九江市,彭泽县', 3);
INSERT INTO `sys_region` VALUES ('360481', '3604', '瑞昌市', '江西省,九江市,瑞昌市', 3);
INSERT INTO `sys_region` VALUES ('360482', '3604', '共青城市', '江西省,九江市,共青城市', 3);
INSERT INTO `sys_region` VALUES ('360483', '3604', '庐山市', '江西省,九江市,庐山市', 3);
INSERT INTO `sys_region` VALUES ('3605', '36', '新余市', '江西省,新余市', 2);
INSERT INTO `sys_region` VALUES ('360502', '3605', '渝水区', '江西省,新余市,渝水区', 3);
INSERT INTO `sys_region` VALUES ('360521', '3605', '分宜县', '江西省,新余市,分宜县', 3);
INSERT INTO `sys_region` VALUES ('3606', '36', '鹰潭市', '江西省,鹰潭市', 2);
INSERT INTO `sys_region` VALUES ('360602', '3606', '月湖区', '江西省,鹰潭市,月湖区', 3);
INSERT INTO `sys_region` VALUES ('360603', '3606', '余江区', '江西省,鹰潭市,余江区', 3);
INSERT INTO `sys_region` VALUES ('360681', '3606', '贵溪市', '江西省,鹰潭市,贵溪市', 3);
INSERT INTO `sys_region` VALUES ('3607', '36', '赣州市', '江西省,赣州市', 2);
INSERT INTO `sys_region` VALUES ('360702', '3607', '章贡区', '江西省,赣州市,章贡区', 3);
INSERT INTO `sys_region` VALUES ('360703', '3607', '南康区', '江西省,赣州市,南康区', 3);
INSERT INTO `sys_region` VALUES ('360704', '3607', '赣县区', '江西省,赣州市,赣县区', 3);
INSERT INTO `sys_region` VALUES ('360722', '3607', '信丰县', '江西省,赣州市,信丰县', 3);
INSERT INTO `sys_region` VALUES ('360723', '3607', '大余县', '江西省,赣州市,大余县', 3);
INSERT INTO `sys_region` VALUES ('360724', '3607', '上犹县', '江西省,赣州市,上犹县', 3);
INSERT INTO `sys_region` VALUES ('360725', '3607', '崇义县', '江西省,赣州市,崇义县', 3);
INSERT INTO `sys_region` VALUES ('360726', '3607', '安远县', '江西省,赣州市,安远县', 3);
INSERT INTO `sys_region` VALUES ('360727', '3607', '龙南县', '江西省,赣州市,龙南县', 3);
INSERT INTO `sys_region` VALUES ('360728', '3607', '定南县', '江西省,赣州市,定南县', 3);
INSERT INTO `sys_region` VALUES ('360729', '3607', '全南县', '江西省,赣州市,全南县', 3);
INSERT INTO `sys_region` VALUES ('360730', '3607', '宁都县', '江西省,赣州市,宁都县', 3);
INSERT INTO `sys_region` VALUES ('360731', '3607', '于都县', '江西省,赣州市,于都县', 3);
INSERT INTO `sys_region` VALUES ('360732', '3607', '兴国县', '江西省,赣州市,兴国县', 3);
INSERT INTO `sys_region` VALUES ('360733', '3607', '会昌县', '江西省,赣州市,会昌县', 3);
INSERT INTO `sys_region` VALUES ('360734', '3607', '寻乌县', '江西省,赣州市,寻乌县', 3);
INSERT INTO `sys_region` VALUES ('360735', '3607', '石城县', '江西省,赣州市,石城县', 3);
INSERT INTO `sys_region` VALUES ('360781', '3607', '瑞金市', '江西省,赣州市,瑞金市', 3);
INSERT INTO `sys_region` VALUES ('3608', '36', '吉安市', '江西省,吉安市', 2);
INSERT INTO `sys_region` VALUES ('360802', '3608', '吉州区', '江西省,吉安市,吉州区', 3);
INSERT INTO `sys_region` VALUES ('360803', '3608', '青原区', '江西省,吉安市,青原区', 3);
INSERT INTO `sys_region` VALUES ('360821', '3608', '吉安县', '江西省,吉安市,吉安县', 3);
INSERT INTO `sys_region` VALUES ('360822', '3608', '吉水县', '江西省,吉安市,吉水县', 3);
INSERT INTO `sys_region` VALUES ('360823', '3608', '峡江县', '江西省,吉安市,峡江县', 3);
INSERT INTO `sys_region` VALUES ('360824', '3608', '新干县', '江西省,吉安市,新干县', 3);
INSERT INTO `sys_region` VALUES ('360825', '3608', '永丰县', '江西省,吉安市,永丰县', 3);
INSERT INTO `sys_region` VALUES ('360826', '3608', '泰和县', '江西省,吉安市,泰和县', 3);
INSERT INTO `sys_region` VALUES ('360827', '3608', '遂川县', '江西省,吉安市,遂川县', 3);
INSERT INTO `sys_region` VALUES ('360828', '3608', '万安县', '江西省,吉安市,万安县', 3);
INSERT INTO `sys_region` VALUES ('360829', '3608', '安福县', '江西省,吉安市,安福县', 3);
INSERT INTO `sys_region` VALUES ('360830', '3608', '永新县', '江西省,吉安市,永新县', 3);
INSERT INTO `sys_region` VALUES ('360881', '3608', '井冈山市', '江西省,吉安市,井冈山市', 3);
INSERT INTO `sys_region` VALUES ('3609', '36', '宜春市', '江西省,宜春市', 2);
INSERT INTO `sys_region` VALUES ('360902', '3609', '袁州区', '江西省,宜春市,袁州区', 3);
INSERT INTO `sys_region` VALUES ('360921', '3609', '奉新县', '江西省,宜春市,奉新县', 3);
INSERT INTO `sys_region` VALUES ('360922', '3609', '万载县', '江西省,宜春市,万载县', 3);
INSERT INTO `sys_region` VALUES ('360923', '3609', '上高县', '江西省,宜春市,上高县', 3);
INSERT INTO `sys_region` VALUES ('360924', '3609', '宜丰县', '江西省,宜春市,宜丰县', 3);
INSERT INTO `sys_region` VALUES ('360925', '3609', '靖安县', '江西省,宜春市,靖安县', 3);
INSERT INTO `sys_region` VALUES ('360926', '3609', '铜鼓县', '江西省,宜春市,铜鼓县', 3);
INSERT INTO `sys_region` VALUES ('360981', '3609', '丰城市', '江西省,宜春市,丰城市', 3);
INSERT INTO `sys_region` VALUES ('360982', '3609', '樟树市', '江西省,宜春市,樟树市', 3);
INSERT INTO `sys_region` VALUES ('360983', '3609', '高安市', '江西省,宜春市,高安市', 3);
INSERT INTO `sys_region` VALUES ('3610', '36', '抚州市', '江西省,抚州市', 2);
INSERT INTO `sys_region` VALUES ('361002', '3610', '临川区', '江西省,抚州市,临川区', 3);
INSERT INTO `sys_region` VALUES ('361003', '3610', '东乡区', '江西省,抚州市,东乡区', 3);
INSERT INTO `sys_region` VALUES ('361021', '3610', '南城县', '江西省,抚州市,南城县', 3);
INSERT INTO `sys_region` VALUES ('361022', '3610', '黎川县', '江西省,抚州市,黎川县', 3);
INSERT INTO `sys_region` VALUES ('361023', '3610', '南丰县', '江西省,抚州市,南丰县', 3);
INSERT INTO `sys_region` VALUES ('361024', '3610', '崇仁县', '江西省,抚州市,崇仁县', 3);
INSERT INTO `sys_region` VALUES ('361025', '3610', '乐安县', '江西省,抚州市,乐安县', 3);
INSERT INTO `sys_region` VALUES ('361026', '3610', '宜黄县', '江西省,抚州市,宜黄县', 3);
INSERT INTO `sys_region` VALUES ('361027', '3610', '金溪县', '江西省,抚州市,金溪县', 3);
INSERT INTO `sys_region` VALUES ('361028', '3610', '资溪县', '江西省,抚州市,资溪县', 3);
INSERT INTO `sys_region` VALUES ('361030', '3610', '广昌县', '江西省,抚州市,广昌县', 3);
INSERT INTO `sys_region` VALUES ('3611', '36', '上饶市', '江西省,上饶市', 2);
INSERT INTO `sys_region` VALUES ('361102', '3611', '信州区', '江西省,上饶市,信州区', 3);
INSERT INTO `sys_region` VALUES ('361103', '3611', '广丰区', '江西省,上饶市,广丰区', 3);
INSERT INTO `sys_region` VALUES ('361121', '3611', '上饶县', '江西省,上饶市,上饶县', 3);
INSERT INTO `sys_region` VALUES ('361123', '3611', '玉山县', '江西省,上饶市,玉山县', 3);
INSERT INTO `sys_region` VALUES ('361124', '3611', '铅山县', '江西省,上饶市,铅山县', 3);
INSERT INTO `sys_region` VALUES ('361125', '3611', '横峰县', '江西省,上饶市,横峰县', 3);
INSERT INTO `sys_region` VALUES ('361126', '3611', '弋阳县', '江西省,上饶市,弋阳县', 3);
INSERT INTO `sys_region` VALUES ('361127', '3611', '余干县', '江西省,上饶市,余干县', 3);
INSERT INTO `sys_region` VALUES ('361128', '3611', '鄱阳县', '江西省,上饶市,鄱阳县', 3);
INSERT INTO `sys_region` VALUES ('361129', '3611', '万年县', '江西省,上饶市,万年县', 3);
INSERT INTO `sys_region` VALUES ('361130', '3611', '婺源县', '江西省,上饶市,婺源县', 3);
INSERT INTO `sys_region` VALUES ('361181', '3611', '德兴市', '江西省,上饶市,德兴市', 3);
INSERT INTO `sys_region` VALUES ('37', '0', '山东省', '山东省', 1);
INSERT INTO `sys_region` VALUES ('3701', '37', '济南市', '山东省,济南市', 2);
INSERT INTO `sys_region` VALUES ('370102', '3701', '历下区', '山东省,济南市,历下区', 3);
INSERT INTO `sys_region` VALUES ('370103', '3701', '市中区', '山东省,济南市,市中区', 3);
INSERT INTO `sys_region` VALUES ('370104', '3701', '槐荫区', '山东省,济南市,槐荫区', 3);
INSERT INTO `sys_region` VALUES ('370105', '3701', '天桥区', '山东省,济南市,天桥区', 3);
INSERT INTO `sys_region` VALUES ('370112', '3701', '历城区', '山东省,济南市,历城区', 3);
INSERT INTO `sys_region` VALUES ('370113', '3701', '长清区', '山东省,济南市,长清区', 3);
INSERT INTO `sys_region` VALUES ('370114', '3701', '章丘区', '山东省,济南市,章丘区', 3);
INSERT INTO `sys_region` VALUES ('370115', '3701', '济阳区', '山东省,济南市,济阳区', 3);
INSERT INTO `sys_region` VALUES ('370124', '3701', '平阴县', '山东省,济南市,平阴县', 3);
INSERT INTO `sys_region` VALUES ('370126', '3701', '商河县', '山东省,济南市,商河县', 3);
INSERT INTO `sys_region` VALUES ('370171', '3701', '济南高新技术产业开发区', '山东省,济南市,济南高新技术产业开发区', 3);
INSERT INTO `sys_region` VALUES ('3702', '37', '青岛市', '山东省,青岛市', 2);
INSERT INTO `sys_region` VALUES ('370202', '3702', '市南区', '山东省,青岛市,市南区', 3);
INSERT INTO `sys_region` VALUES ('370203', '3702', '市北区', '山东省,青岛市,市北区', 3);
INSERT INTO `sys_region` VALUES ('370211', '3702', '黄岛区', '山东省,青岛市,黄岛区', 3);
INSERT INTO `sys_region` VALUES ('370212', '3702', '崂山区', '山东省,青岛市,崂山区', 3);
INSERT INTO `sys_region` VALUES ('370213', '3702', '李沧区', '山东省,青岛市,李沧区', 3);
INSERT INTO `sys_region` VALUES ('370214', '3702', '城阳区', '山东省,青岛市,城阳区', 3);
INSERT INTO `sys_region` VALUES ('370215', '3702', '即墨区', '山东省,青岛市,即墨区', 3);
INSERT INTO `sys_region` VALUES ('370271', '3702', '青岛高新技术产业开发区', '山东省,青岛市,青岛高新技术产业开发区', 3);
INSERT INTO `sys_region` VALUES ('370281', '3702', '胶州市', '山东省,青岛市,胶州市', 3);
INSERT INTO `sys_region` VALUES ('370283', '3702', '平度市', '山东省,青岛市,平度市', 3);
INSERT INTO `sys_region` VALUES ('370285', '3702', '莱西市', '山东省,青岛市,莱西市', 3);
INSERT INTO `sys_region` VALUES ('3703', '37', '淄博市', '山东省,淄博市', 2);
INSERT INTO `sys_region` VALUES ('370302', '3703', '淄川区', '山东省,淄博市,淄川区', 3);
INSERT INTO `sys_region` VALUES ('370303', '3703', '张店区', '山东省,淄博市,张店区', 3);
INSERT INTO `sys_region` VALUES ('370304', '3703', '博山区', '山东省,淄博市,博山区', 3);
INSERT INTO `sys_region` VALUES ('370305', '3703', '临淄区', '山东省,淄博市,临淄区', 3);
INSERT INTO `sys_region` VALUES ('370306', '3703', '周村区', '山东省,淄博市,周村区', 3);
INSERT INTO `sys_region` VALUES ('370321', '3703', '桓台县', '山东省,淄博市,桓台县', 3);
INSERT INTO `sys_region` VALUES ('370322', '3703', '高青县', '山东省,淄博市,高青县', 3);
INSERT INTO `sys_region` VALUES ('370323', '3703', '沂源县', '山东省,淄博市,沂源县', 3);
INSERT INTO `sys_region` VALUES ('3704', '37', '枣庄市', '山东省,枣庄市', 2);
INSERT INTO `sys_region` VALUES ('370402', '3704', '市中区', '山东省,枣庄市,市中区', 3);
INSERT INTO `sys_region` VALUES ('370403', '3704', '薛城区', '山东省,枣庄市,薛城区', 3);
INSERT INTO `sys_region` VALUES ('370404', '3704', '峄城区', '山东省,枣庄市,峄城区', 3);
INSERT INTO `sys_region` VALUES ('370405', '3704', '台儿庄区', '山东省,枣庄市,台儿庄区', 3);
INSERT INTO `sys_region` VALUES ('370406', '3704', '山亭区', '山东省,枣庄市,山亭区', 3);
INSERT INTO `sys_region` VALUES ('370481', '3704', '滕州市', '山东省,枣庄市,滕州市', 3);
INSERT INTO `sys_region` VALUES ('3705', '37', '东营市', '山东省,东营市', 2);
INSERT INTO `sys_region` VALUES ('370502', '3705', '东营区', '山东省,东营市,东营区', 3);
INSERT INTO `sys_region` VALUES ('370503', '3705', '河口区', '山东省,东营市,河口区', 3);
INSERT INTO `sys_region` VALUES ('370505', '3705', '垦利区', '山东省,东营市,垦利区', 3);
INSERT INTO `sys_region` VALUES ('370522', '3705', '利津县', '山东省,东营市,利津县', 3);
INSERT INTO `sys_region` VALUES ('370523', '3705', '广饶县', '山东省,东营市,广饶县', 3);
INSERT INTO `sys_region` VALUES ('370571', '3705', '东营经济技术开发区', '山东省,东营市,东营经济技术开发区', 3);
INSERT INTO `sys_region` VALUES ('370572', '3705', '东营港经济开发区', '山东省,东营市,东营港经济开发区', 3);
INSERT INTO `sys_region` VALUES ('3706', '37', '烟台市', '山东省,烟台市', 2);
INSERT INTO `sys_region` VALUES ('370602', '3706', '芝罘区', '山东省,烟台市,芝罘区', 3);
INSERT INTO `sys_region` VALUES ('370611', '3706', '福山区', '山东省,烟台市,福山区', 3);
INSERT INTO `sys_region` VALUES ('370612', '3706', '牟平区', '山东省,烟台市,牟平区', 3);
INSERT INTO `sys_region` VALUES ('370613', '3706', '莱山区', '山东省,烟台市,莱山区', 3);
INSERT INTO `sys_region` VALUES ('370634', '3706', '长岛县', '山东省,烟台市,长岛县', 3);
INSERT INTO `sys_region` VALUES ('370671', '3706', '烟台高新技术产业开发区', '山东省,烟台市,烟台高新技术产业开发区', 3);
INSERT INTO `sys_region` VALUES ('370672', '3706', '烟台经济技术开发区', '山东省,烟台市,烟台经济技术开发区', 3);
INSERT INTO `sys_region` VALUES ('370681', '3706', '龙口市', '山东省,烟台市,龙口市', 3);
INSERT INTO `sys_region` VALUES ('370682', '3706', '莱阳市', '山东省,烟台市,莱阳市', 3);
INSERT INTO `sys_region` VALUES ('370683', '3706', '莱州市', '山东省,烟台市,莱州市', 3);
INSERT INTO `sys_region` VALUES ('370684', '3706', '蓬莱市', '山东省,烟台市,蓬莱市', 3);
INSERT INTO `sys_region` VALUES ('370685', '3706', '招远市', '山东省,烟台市,招远市', 3);
INSERT INTO `sys_region` VALUES ('370686', '3706', '栖霞市', '山东省,烟台市,栖霞市', 3);
INSERT INTO `sys_region` VALUES ('370687', '3706', '海阳市', '山东省,烟台市,海阳市', 3);
INSERT INTO `sys_region` VALUES ('3707', '37', '潍坊市', '山东省,潍坊市', 2);
INSERT INTO `sys_region` VALUES ('370702', '3707', '潍城区', '山东省,潍坊市,潍城区', 3);
INSERT INTO `sys_region` VALUES ('370703', '3707', '寒亭区', '山东省,潍坊市,寒亭区', 3);
INSERT INTO `sys_region` VALUES ('370704', '3707', '坊子区', '山东省,潍坊市,坊子区', 3);
INSERT INTO `sys_region` VALUES ('370705', '3707', '奎文区', '山东省,潍坊市,奎文区', 3);
INSERT INTO `sys_region` VALUES ('370724', '3707', '临朐县', '山东省,潍坊市,临朐县', 3);
INSERT INTO `sys_region` VALUES ('370725', '3707', '昌乐县', '山东省,潍坊市,昌乐县', 3);
INSERT INTO `sys_region` VALUES ('370772', '3707', '潍坊滨海经济技术开发区', '山东省,潍坊市,潍坊滨海经济技术开发区', 3);
INSERT INTO `sys_region` VALUES ('370781', '3707', '青州市', '山东省,潍坊市,青州市', 3);
INSERT INTO `sys_region` VALUES ('370782', '3707', '诸城市', '山东省,潍坊市,诸城市', 3);
INSERT INTO `sys_region` VALUES ('370783', '3707', '寿光市', '山东省,潍坊市,寿光市', 3);
INSERT INTO `sys_region` VALUES ('370784', '3707', '安丘市', '山东省,潍坊市,安丘市', 3);
INSERT INTO `sys_region` VALUES ('370785', '3707', '高密市', '山东省,潍坊市,高密市', 3);
INSERT INTO `sys_region` VALUES ('370786', '3707', '昌邑市', '山东省,潍坊市,昌邑市', 3);
INSERT INTO `sys_region` VALUES ('3708', '37', '济宁市', '山东省,济宁市', 2);
INSERT INTO `sys_region` VALUES ('370811', '3708', '任城区', '山东省,济宁市,任城区', 3);
INSERT INTO `sys_region` VALUES ('370812', '3708', '兖州区', '山东省,济宁市,兖州区', 3);
INSERT INTO `sys_region` VALUES ('370826', '3708', '微山县', '山东省,济宁市,微山县', 3);
INSERT INTO `sys_region` VALUES ('370827', '3708', '鱼台县', '山东省,济宁市,鱼台县', 3);
INSERT INTO `sys_region` VALUES ('370828', '3708', '金乡县', '山东省,济宁市,金乡县', 3);
INSERT INTO `sys_region` VALUES ('370829', '3708', '嘉祥县', '山东省,济宁市,嘉祥县', 3);
INSERT INTO `sys_region` VALUES ('370830', '3708', '汶上县', '山东省,济宁市,汶上县', 3);
INSERT INTO `sys_region` VALUES ('370831', '3708', '泗水县', '山东省,济宁市,泗水县', 3);
INSERT INTO `sys_region` VALUES ('370832', '3708', '梁山县', '山东省,济宁市,梁山县', 3);
INSERT INTO `sys_region` VALUES ('370871', '3708', '济宁高新技术产业开发区', '山东省,济宁市,济宁高新技术产业开发区', 3);
INSERT INTO `sys_region` VALUES ('370881', '3708', '曲阜市', '山东省,济宁市,曲阜市', 3);
INSERT INTO `sys_region` VALUES ('370883', '3708', '邹城市', '山东省,济宁市,邹城市', 3);
INSERT INTO `sys_region` VALUES ('3709', '37', '泰安市', '山东省,泰安市', 2);
INSERT INTO `sys_region` VALUES ('370902', '3709', '泰山区', '山东省,泰安市,泰山区', 3);
INSERT INTO `sys_region` VALUES ('370911', '3709', '岱岳区', '山东省,泰安市,岱岳区', 3);
INSERT INTO `sys_region` VALUES ('370921', '3709', '宁阳县', '山东省,泰安市,宁阳县', 3);
INSERT INTO `sys_region` VALUES ('370923', '3709', '东平县', '山东省,泰安市,东平县', 3);
INSERT INTO `sys_region` VALUES ('370982', '3709', '新泰市', '山东省,泰安市,新泰市', 3);
INSERT INTO `sys_region` VALUES ('370983', '3709', '肥城市', '山东省,泰安市,肥城市', 3);
INSERT INTO `sys_region` VALUES ('3710', '37', '威海市', '山东省,威海市', 2);
INSERT INTO `sys_region` VALUES ('371002', '3710', '环翠区', '山东省,威海市,环翠区', 3);
INSERT INTO `sys_region` VALUES ('371003', '3710', '文登区', '山东省,威海市,文登区', 3);
INSERT INTO `sys_region` VALUES ('371071', '3710', '威海火炬高技术产业开发区', '山东省,威海市,威海火炬高技术产业开发区', 3);
INSERT INTO `sys_region` VALUES ('371072', '3710', '威海经济技术开发区', '山东省,威海市,威海经济技术开发区', 3);
INSERT INTO `sys_region` VALUES ('371073', '3710', '威海临港经济技术开发区', '山东省,威海市,威海临港经济技术开发区', 3);
INSERT INTO `sys_region` VALUES ('371082', '3710', '荣成市', '山东省,威海市,荣成市', 3);
INSERT INTO `sys_region` VALUES ('371083', '3710', '乳山市', '山东省,威海市,乳山市', 3);
INSERT INTO `sys_region` VALUES ('3711', '37', '日照市', '山东省,日照市', 2);
INSERT INTO `sys_region` VALUES ('371102', '3711', '东港区', '山东省,日照市,东港区', 3);
INSERT INTO `sys_region` VALUES ('371103', '3711', '岚山区', '山东省,日照市,岚山区', 3);
INSERT INTO `sys_region` VALUES ('371121', '3711', '五莲县', '山东省,日照市,五莲县', 3);
INSERT INTO `sys_region` VALUES ('371122', '3711', '莒县', '山东省,日照市,莒县', 3);
INSERT INTO `sys_region` VALUES ('371171', '3711', '日照经济技术开发区', '山东省,日照市,日照经济技术开发区', 3);
INSERT INTO `sys_region` VALUES ('3712', '37', '莱芜市', '山东省,莱芜市', 2);
INSERT INTO `sys_region` VALUES ('371202', '3712', '莱城区', '山东省,莱芜市,莱城区', 3);
INSERT INTO `sys_region` VALUES ('371203', '3712', '钢城区', '山东省,莱芜市,钢城区', 3);
INSERT INTO `sys_region` VALUES ('3713', '37', '临沂市', '山东省,临沂市', 2);
INSERT INTO `sys_region` VALUES ('371302', '3713', '兰山区', '山东省,临沂市,兰山区', 3);
INSERT INTO `sys_region` VALUES ('371311', '3713', '罗庄区', '山东省,临沂市,罗庄区', 3);
INSERT INTO `sys_region` VALUES ('371312', '3713', '河东区', '山东省,临沂市,河东区', 3);
INSERT INTO `sys_region` VALUES ('371321', '3713', '沂南县', '山东省,临沂市,沂南县', 3);
INSERT INTO `sys_region` VALUES ('371322', '3713', '郯城县', '山东省,临沂市,郯城县', 3);
INSERT INTO `sys_region` VALUES ('371323', '3713', '沂水县', '山东省,临沂市,沂水县', 3);
INSERT INTO `sys_region` VALUES ('371324', '3713', '兰陵县', '山东省,临沂市,兰陵县', 3);
INSERT INTO `sys_region` VALUES ('371325', '3713', '费县', '山东省,临沂市,费县', 3);
INSERT INTO `sys_region` VALUES ('371326', '3713', '平邑县', '山东省,临沂市,平邑县', 3);
INSERT INTO `sys_region` VALUES ('371327', '3713', '莒南县', '山东省,临沂市,莒南县', 3);
INSERT INTO `sys_region` VALUES ('371328', '3713', '蒙阴县', '山东省,临沂市,蒙阴县', 3);
INSERT INTO `sys_region` VALUES ('371329', '3713', '临沭县', '山东省,临沂市,临沭县', 3);
INSERT INTO `sys_region` VALUES ('371371', '3713', '临沂高新技术产业开发区', '山东省,临沂市,临沂高新技术产业开发区', 3);
INSERT INTO `sys_region` VALUES ('371372', '3713', '临沂经济技术开发区', '山东省,临沂市,临沂经济技术开发区', 3);
INSERT INTO `sys_region` VALUES ('371373', '3713', '临沂临港经济开发区', '山东省,临沂市,临沂临港经济开发区', 3);
INSERT INTO `sys_region` VALUES ('3714', '37', '德州市', '山东省,德州市', 2);
INSERT INTO `sys_region` VALUES ('371402', '3714', '德城区', '山东省,德州市,德城区', 3);
INSERT INTO `sys_region` VALUES ('371403', '3714', '陵城区', '山东省,德州市,陵城区', 3);
INSERT INTO `sys_region` VALUES ('371422', '3714', '宁津县', '山东省,德州市,宁津县', 3);
INSERT INTO `sys_region` VALUES ('371423', '3714', '庆云县', '山东省,德州市,庆云县', 3);
INSERT INTO `sys_region` VALUES ('371424', '3714', '临邑县', '山东省,德州市,临邑县', 3);
INSERT INTO `sys_region` VALUES ('371425', '3714', '齐河县', '山东省,德州市,齐河县', 3);
INSERT INTO `sys_region` VALUES ('371426', '3714', '平原县', '山东省,德州市,平原县', 3);
INSERT INTO `sys_region` VALUES ('371427', '3714', '夏津县', '山东省,德州市,夏津县', 3);
INSERT INTO `sys_region` VALUES ('371428', '3714', '武城县', '山东省,德州市,武城县', 3);
INSERT INTO `sys_region` VALUES ('371471', '3714', '德州经济技术开发区', '山东省,德州市,德州经济技术开发区', 3);
INSERT INTO `sys_region` VALUES ('371472', '3714', '德州运河经济开发区', '山东省,德州市,德州运河经济开发区', 3);
INSERT INTO `sys_region` VALUES ('371481', '3714', '乐陵市', '山东省,德州市,乐陵市', 3);
INSERT INTO `sys_region` VALUES ('371482', '3714', '禹城市', '山东省,德州市,禹城市', 3);
INSERT INTO `sys_region` VALUES ('3715', '37', '聊城市', '山东省,聊城市', 2);
INSERT INTO `sys_region` VALUES ('371502', '3715', '东昌府区', '山东省,聊城市,东昌府区', 3);
INSERT INTO `sys_region` VALUES ('371521', '3715', '阳谷县', '山东省,聊城市,阳谷县', 3);
INSERT INTO `sys_region` VALUES ('371522', '3715', '莘县', '山东省,聊城市,莘县', 3);
INSERT INTO `sys_region` VALUES ('371523', '3715', '茌平县', '山东省,聊城市,茌平县', 3);
INSERT INTO `sys_region` VALUES ('371524', '3715', '东阿县', '山东省,聊城市,东阿县', 3);
INSERT INTO `sys_region` VALUES ('371525', '3715', '冠县', '山东省,聊城市,冠县', 3);
INSERT INTO `sys_region` VALUES ('371526', '3715', '高唐县', '山东省,聊城市,高唐县', 3);
INSERT INTO `sys_region` VALUES ('371581', '3715', '临清市', '山东省,聊城市,临清市', 3);
INSERT INTO `sys_region` VALUES ('3716', '37', '滨州市', '山东省,滨州市', 2);
INSERT INTO `sys_region` VALUES ('371602', '3716', '滨城区', '山东省,滨州市,滨城区', 3);
INSERT INTO `sys_region` VALUES ('371603', '3716', '沾化区', '山东省,滨州市,沾化区', 3);
INSERT INTO `sys_region` VALUES ('371621', '3716', '惠民县', '山东省,滨州市,惠民县', 3);
INSERT INTO `sys_region` VALUES ('371622', '3716', '阳信县', '山东省,滨州市,阳信县', 3);
INSERT INTO `sys_region` VALUES ('371623', '3716', '无棣县', '山东省,滨州市,无棣县', 3);
INSERT INTO `sys_region` VALUES ('371625', '3716', '博兴县', '山东省,滨州市,博兴县', 3);
INSERT INTO `sys_region` VALUES ('371681', '3716', '邹平市', '山东省,滨州市,邹平市', 3);
INSERT INTO `sys_region` VALUES ('3717', '37', '菏泽市', '山东省,菏泽市', 2);
INSERT INTO `sys_region` VALUES ('371702', '3717', '牡丹区', '山东省,菏泽市,牡丹区', 3);
INSERT INTO `sys_region` VALUES ('371703', '3717', '定陶区', '山东省,菏泽市,定陶区', 3);
INSERT INTO `sys_region` VALUES ('371721', '3717', '曹县', '山东省,菏泽市,曹县', 3);
INSERT INTO `sys_region` VALUES ('371722', '3717', '单县', '山东省,菏泽市,单县', 3);
INSERT INTO `sys_region` VALUES ('371723', '3717', '成武县', '山东省,菏泽市,成武县', 3);
INSERT INTO `sys_region` VALUES ('371724', '3717', '巨野县', '山东省,菏泽市,巨野县', 3);
INSERT INTO `sys_region` VALUES ('371725', '3717', '郓城县', '山东省,菏泽市,郓城县', 3);
INSERT INTO `sys_region` VALUES ('371726', '3717', '鄄城县', '山东省,菏泽市,鄄城县', 3);
INSERT INTO `sys_region` VALUES ('371728', '3717', '东明县', '山东省,菏泽市,东明县', 3);
INSERT INTO `sys_region` VALUES ('371771', '3717', '菏泽经济技术开发区', '山东省,菏泽市,菏泽经济技术开发区', 3);
INSERT INTO `sys_region` VALUES ('371772', '3717', '菏泽高新技术开发区', '山东省,菏泽市,菏泽高新技术开发区', 3);
INSERT INTO `sys_region` VALUES ('41', '0', '河南省', '河南省', 1);
INSERT INTO `sys_region` VALUES ('4101', '41', '郑州市', '河南省,郑州市', 2);
INSERT INTO `sys_region` VALUES ('410102', '4101', '中原区', '河南省,郑州市,中原区', 3);
INSERT INTO `sys_region` VALUES ('410103', '4101', '二七区', '河南省,郑州市,二七区', 3);
INSERT INTO `sys_region` VALUES ('410104', '4101', '管城回族区', '河南省,郑州市,管城回族区', 3);
INSERT INTO `sys_region` VALUES ('410105', '4101', '金水区', '河南省,郑州市,金水区', 3);
INSERT INTO `sys_region` VALUES ('410106', '4101', '上街区', '河南省,郑州市,上街区', 3);
INSERT INTO `sys_region` VALUES ('410108', '4101', '惠济区', '河南省,郑州市,惠济区', 3);
INSERT INTO `sys_region` VALUES ('410122', '4101', '中牟县', '河南省,郑州市,中牟县', 3);
INSERT INTO `sys_region` VALUES ('410171', '4101', '郑州经济技术开发区', '河南省,郑州市,郑州经济技术开发区', 3);
INSERT INTO `sys_region` VALUES ('410172', '4101', '郑州高新技术产业开发区', '河南省,郑州市,郑州高新技术产业开发区', 3);
INSERT INTO `sys_region` VALUES ('410173', '4101', '郑州航空港经济综合实验区', '河南省,郑州市,郑州航空港经济综合实验区', 3);
INSERT INTO `sys_region` VALUES ('410181', '4101', '巩义市', '河南省,郑州市,巩义市', 3);
INSERT INTO `sys_region` VALUES ('410182', '4101', '荥阳市', '河南省,郑州市,荥阳市', 3);
INSERT INTO `sys_region` VALUES ('410183', '4101', '新密市', '河南省,郑州市,新密市', 3);
INSERT INTO `sys_region` VALUES ('410184', '4101', '新郑市', '河南省,郑州市,新郑市', 3);
INSERT INTO `sys_region` VALUES ('410185', '4101', '登封市', '河南省,郑州市,登封市', 3);
INSERT INTO `sys_region` VALUES ('4102', '41', '开封市', '河南省,开封市', 2);
INSERT INTO `sys_region` VALUES ('410202', '4102', '龙亭区', '河南省,开封市,龙亭区', 3);
INSERT INTO `sys_region` VALUES ('410203', '4102', '顺河回族区', '河南省,开封市,顺河回族区', 3);
INSERT INTO `sys_region` VALUES ('410204', '4102', '鼓楼区', '河南省,开封市,鼓楼区', 3);
INSERT INTO `sys_region` VALUES ('410205', '4102', '禹王台区', '河南省,开封市,禹王台区', 3);
INSERT INTO `sys_region` VALUES ('410212', '4102', '祥符区', '河南省,开封市,祥符区', 3);
INSERT INTO `sys_region` VALUES ('410221', '4102', '杞县', '河南省,开封市,杞县', 3);
INSERT INTO `sys_region` VALUES ('410222', '4102', '通许县', '河南省,开封市,通许县', 3);
INSERT INTO `sys_region` VALUES ('410223', '4102', '尉氏县', '河南省,开封市,尉氏县', 3);
INSERT INTO `sys_region` VALUES ('410225', '4102', '兰考县', '河南省,开封市,兰考县', 3);
INSERT INTO `sys_region` VALUES ('4103', '41', '洛阳市', '河南省,洛阳市', 2);
INSERT INTO `sys_region` VALUES ('410302', '4103', '老城区', '河南省,洛阳市,老城区', 3);
INSERT INTO `sys_region` VALUES ('410303', '4103', '西工区', '河南省,洛阳市,西工区', 3);
INSERT INTO `sys_region` VALUES ('410304', '4103', '瀍河回族区', '河南省,洛阳市,瀍河回族区', 3);
INSERT INTO `sys_region` VALUES ('410305', '4103', '涧西区', '河南省,洛阳市,涧西区', 3);
INSERT INTO `sys_region` VALUES ('410306', '4103', '吉利区', '河南省,洛阳市,吉利区', 3);
INSERT INTO `sys_region` VALUES ('410311', '4103', '洛龙区', '河南省,洛阳市,洛龙区', 3);
INSERT INTO `sys_region` VALUES ('410322', '4103', '孟津县', '河南省,洛阳市,孟津县', 3);
INSERT INTO `sys_region` VALUES ('410323', '4103', '新安县', '河南省,洛阳市,新安县', 3);
INSERT INTO `sys_region` VALUES ('410324', '4103', '栾川县', '河南省,洛阳市,栾川县', 3);
INSERT INTO `sys_region` VALUES ('410325', '4103', '嵩县', '河南省,洛阳市,嵩县', 3);
INSERT INTO `sys_region` VALUES ('410326', '4103', '汝阳县', '河南省,洛阳市,汝阳县', 3);
INSERT INTO `sys_region` VALUES ('410327', '4103', '宜阳县', '河南省,洛阳市,宜阳县', 3);
INSERT INTO `sys_region` VALUES ('410328', '4103', '洛宁县', '河南省,洛阳市,洛宁县', 3);
INSERT INTO `sys_region` VALUES ('410329', '4103', '伊川县', '河南省,洛阳市,伊川县', 3);
INSERT INTO `sys_region` VALUES ('410371', '4103', '洛阳高新技术产业开发区', '河南省,洛阳市,洛阳高新技术产业开发区', 3);
INSERT INTO `sys_region` VALUES ('410381', '4103', '偃师市', '河南省,洛阳市,偃师市', 3);
INSERT INTO `sys_region` VALUES ('4104', '41', '平顶山市', '河南省,平顶山市', 2);
INSERT INTO `sys_region` VALUES ('410402', '4104', '新华区', '河南省,平顶山市,新华区', 3);
INSERT INTO `sys_region` VALUES ('410403', '4104', '卫东区', '河南省,平顶山市,卫东区', 3);
INSERT INTO `sys_region` VALUES ('410404', '4104', '石龙区', '河南省,平顶山市,石龙区', 3);
INSERT INTO `sys_region` VALUES ('410411', '4104', '湛河区', '河南省,平顶山市,湛河区', 3);
INSERT INTO `sys_region` VALUES ('410421', '4104', '宝丰县', '河南省,平顶山市,宝丰县', 3);
INSERT INTO `sys_region` VALUES ('410422', '4104', '叶县', '河南省,平顶山市,叶县', 3);
INSERT INTO `sys_region` VALUES ('410423', '4104', '鲁山县', '河南省,平顶山市,鲁山县', 3);
INSERT INTO `sys_region` VALUES ('410425', '4104', '郏县', '河南省,平顶山市,郏县', 3);
INSERT INTO `sys_region` VALUES ('410471', '4104', '平顶山高新技术产业开发区', '河南省,平顶山市,平顶山高新技术产业开发区', 3);
INSERT INTO `sys_region` VALUES ('410472', '4104', '平顶山市新城区', '河南省,平顶山市,平顶山市新城区', 3);
INSERT INTO `sys_region` VALUES ('410481', '4104', '舞钢市', '河南省,平顶山市,舞钢市', 3);
INSERT INTO `sys_region` VALUES ('410482', '4104', '汝州市', '河南省,平顶山市,汝州市', 3);
INSERT INTO `sys_region` VALUES ('4105', '41', '安阳市', '河南省,安阳市', 2);
INSERT INTO `sys_region` VALUES ('410502', '4105', '文峰区', '河南省,安阳市,文峰区', 3);
INSERT INTO `sys_region` VALUES ('410503', '4105', '北关区', '河南省,安阳市,北关区', 3);
INSERT INTO `sys_region` VALUES ('410505', '4105', '殷都区', '河南省,安阳市,殷都区', 3);
INSERT INTO `sys_region` VALUES ('410506', '4105', '龙安区', '河南省,安阳市,龙安区', 3);
INSERT INTO `sys_region` VALUES ('410522', '4105', '安阳县', '河南省,安阳市,安阳县', 3);
INSERT INTO `sys_region` VALUES ('410523', '4105', '汤阴县', '河南省,安阳市,汤阴县', 3);
INSERT INTO `sys_region` VALUES ('410526', '4105', '滑县', '河南省,安阳市,滑县', 3);
INSERT INTO `sys_region` VALUES ('410527', '4105', '内黄县', '河南省,安阳市,内黄县', 3);
INSERT INTO `sys_region` VALUES ('410571', '4105', '安阳高新技术产业开发区', '河南省,安阳市,安阳高新技术产业开发区', 3);
INSERT INTO `sys_region` VALUES ('410581', '4105', '林州市', '河南省,安阳市,林州市', 3);
INSERT INTO `sys_region` VALUES ('4106', '41', '鹤壁市', '河南省,鹤壁市', 2);
INSERT INTO `sys_region` VALUES ('410602', '4106', '鹤山区', '河南省,鹤壁市,鹤山区', 3);
INSERT INTO `sys_region` VALUES ('410603', '4106', '山城区', '河南省,鹤壁市,山城区', 3);
INSERT INTO `sys_region` VALUES ('410611', '4106', '淇滨区', '河南省,鹤壁市,淇滨区', 3);
INSERT INTO `sys_region` VALUES ('410621', '4106', '浚县', '河南省,鹤壁市,浚县', 3);
INSERT INTO `sys_region` VALUES ('410622', '4106', '淇县', '河南省,鹤壁市,淇县', 3);
INSERT INTO `sys_region` VALUES ('410671', '4106', '鹤壁经济技术开发区', '河南省,鹤壁市,鹤壁经济技术开发区', 3);
INSERT INTO `sys_region` VALUES ('4107', '41', '新乡市', '河南省,新乡市', 2);
INSERT INTO `sys_region` VALUES ('410702', '4107', '红旗区', '河南省,新乡市,红旗区', 3);
INSERT INTO `sys_region` VALUES ('410703', '4107', '卫滨区', '河南省,新乡市,卫滨区', 3);
INSERT INTO `sys_region` VALUES ('410704', '4107', '凤泉区', '河南省,新乡市,凤泉区', 3);
INSERT INTO `sys_region` VALUES ('410711', '4107', '牧野区', '河南省,新乡市,牧野区', 3);
INSERT INTO `sys_region` VALUES ('410721', '4107', '新乡县', '河南省,新乡市,新乡县', 3);
INSERT INTO `sys_region` VALUES ('410724', '4107', '获嘉县', '河南省,新乡市,获嘉县', 3);
INSERT INTO `sys_region` VALUES ('410725', '4107', '原阳县', '河南省,新乡市,原阳县', 3);
INSERT INTO `sys_region` VALUES ('410726', '4107', '延津县', '河南省,新乡市,延津县', 3);
INSERT INTO `sys_region` VALUES ('410727', '4107', '封丘县', '河南省,新乡市,封丘县', 3);
INSERT INTO `sys_region` VALUES ('410728', '4107', '长垣县', '河南省,新乡市,长垣县', 3);
INSERT INTO `sys_region` VALUES ('410771', '4107', '新乡高新技术产业开发区', '河南省,新乡市,新乡高新技术产业开发区', 3);
INSERT INTO `sys_region` VALUES ('410772', '4107', '新乡经济技术开发区', '河南省,新乡市,新乡经济技术开发区', 3);
INSERT INTO `sys_region` VALUES ('410773', '4107', '新乡市平原城乡一体化示范区', '河南省,新乡市,新乡市平原城乡一体化示范区', 3);
INSERT INTO `sys_region` VALUES ('410781', '4107', '卫辉市', '河南省,新乡市,卫辉市', 3);
INSERT INTO `sys_region` VALUES ('410782', '4107', '辉县市', '河南省,新乡市,辉县市', 3);
INSERT INTO `sys_region` VALUES ('4108', '41', '焦作市', '河南省,焦作市', 2);
INSERT INTO `sys_region` VALUES ('410802', '4108', '解放区', '河南省,焦作市,解放区', 3);
INSERT INTO `sys_region` VALUES ('410803', '4108', '中站区', '河南省,焦作市,中站区', 3);
INSERT INTO `sys_region` VALUES ('410804', '4108', '马村区', '河南省,焦作市,马村区', 3);
INSERT INTO `sys_region` VALUES ('410811', '4108', '山阳区', '河南省,焦作市,山阳区', 3);
INSERT INTO `sys_region` VALUES ('410821', '4108', '修武县', '河南省,焦作市,修武县', 3);
INSERT INTO `sys_region` VALUES ('410822', '4108', '博爱县', '河南省,焦作市,博爱县', 3);
INSERT INTO `sys_region` VALUES ('410823', '4108', '武陟县', '河南省,焦作市,武陟县', 3);
INSERT INTO `sys_region` VALUES ('410825', '4108', '温县', '河南省,焦作市,温县', 3);
INSERT INTO `sys_region` VALUES ('410871', '4108', '焦作城乡一体化示范区', '河南省,焦作市,焦作城乡一体化示范区', 3);
INSERT INTO `sys_region` VALUES ('410882', '4108', '沁阳市', '河南省,焦作市,沁阳市', 3);
INSERT INTO `sys_region` VALUES ('410883', '4108', '孟州市', '河南省,焦作市,孟州市', 3);
INSERT INTO `sys_region` VALUES ('4109', '41', '濮阳市', '河南省,濮阳市', 2);
INSERT INTO `sys_region` VALUES ('410902', '4109', '华龙区', '河南省,濮阳市,华龙区', 3);
INSERT INTO `sys_region` VALUES ('410922', '4109', '清丰县', '河南省,濮阳市,清丰县', 3);
INSERT INTO `sys_region` VALUES ('410923', '4109', '南乐县', '河南省,濮阳市,南乐县', 3);
INSERT INTO `sys_region` VALUES ('410926', '4109', '范县', '河南省,濮阳市,范县', 3);
INSERT INTO `sys_region` VALUES ('410927', '4109', '台前县', '河南省,濮阳市,台前县', 3);
INSERT INTO `sys_region` VALUES ('410928', '4109', '濮阳县', '河南省,濮阳市,濮阳县', 3);
INSERT INTO `sys_region` VALUES ('410971', '4109', '河南濮阳工业园区', '河南省,濮阳市,河南濮阳工业园区', 3);
INSERT INTO `sys_region` VALUES ('410972', '4109', '濮阳经济技术开发区', '河南省,濮阳市,濮阳经济技术开发区', 3);
INSERT INTO `sys_region` VALUES ('4110', '41', '许昌市', '河南省,许昌市', 2);
INSERT INTO `sys_region` VALUES ('411002', '4110', '魏都区', '河南省,许昌市,魏都区', 3);
INSERT INTO `sys_region` VALUES ('411003', '4110', '建安区', '河南省,许昌市,建安区', 3);
INSERT INTO `sys_region` VALUES ('411024', '4110', '鄢陵县', '河南省,许昌市,鄢陵县', 3);
INSERT INTO `sys_region` VALUES ('411025', '4110', '襄城县', '河南省,许昌市,襄城县', 3);
INSERT INTO `sys_region` VALUES ('411071', '4110', '许昌经济技术开发区', '河南省,许昌市,许昌经济技术开发区', 3);
INSERT INTO `sys_region` VALUES ('411081', '4110', '禹州市', '河南省,许昌市,禹州市', 3);
INSERT INTO `sys_region` VALUES ('411082', '4110', '长葛市', '河南省,许昌市,长葛市', 3);
INSERT INTO `sys_region` VALUES ('4111', '41', '漯河市', '河南省,漯河市', 2);
INSERT INTO `sys_region` VALUES ('411102', '4111', '源汇区', '河南省,漯河市,源汇区', 3);
INSERT INTO `sys_region` VALUES ('411103', '4111', '郾城区', '河南省,漯河市,郾城区', 3);
INSERT INTO `sys_region` VALUES ('411104', '4111', '召陵区', '河南省,漯河市,召陵区', 3);
INSERT INTO `sys_region` VALUES ('411121', '4111', '舞阳县', '河南省,漯河市,舞阳县', 3);
INSERT INTO `sys_region` VALUES ('411122', '4111', '临颍县', '河南省,漯河市,临颍县', 3);
INSERT INTO `sys_region` VALUES ('411171', '4111', '漯河经济技术开发区', '河南省,漯河市,漯河经济技术开发区', 3);
INSERT INTO `sys_region` VALUES ('4112', '41', '三门峡市', '河南省,三门峡市', 2);
INSERT INTO `sys_region` VALUES ('411202', '4112', '湖滨区', '河南省,三门峡市,湖滨区', 3);
INSERT INTO `sys_region` VALUES ('411203', '4112', '陕州区', '河南省,三门峡市,陕州区', 3);
INSERT INTO `sys_region` VALUES ('411221', '4112', '渑池县', '河南省,三门峡市,渑池县', 3);
INSERT INTO `sys_region` VALUES ('411224', '4112', '卢氏县', '河南省,三门峡市,卢氏县', 3);
INSERT INTO `sys_region` VALUES ('411271', '4112', '河南三门峡经济开发区', '河南省,三门峡市,河南三门峡经济开发区', 3);
INSERT INTO `sys_region` VALUES ('411281', '4112', '义马市', '河南省,三门峡市,义马市', 3);
INSERT INTO `sys_region` VALUES ('411282', '4112', '灵宝市', '河南省,三门峡市,灵宝市', 3);
INSERT INTO `sys_region` VALUES ('4113', '41', '南阳市', '河南省,南阳市', 2);
INSERT INTO `sys_region` VALUES ('411302', '4113', '宛城区', '河南省,南阳市,宛城区', 3);
INSERT INTO `sys_region` VALUES ('411303', '4113', '卧龙区', '河南省,南阳市,卧龙区', 3);
INSERT INTO `sys_region` VALUES ('411321', '4113', '南召县', '河南省,南阳市,南召县', 3);
INSERT INTO `sys_region` VALUES ('411322', '4113', '方城县', '河南省,南阳市,方城县', 3);
INSERT INTO `sys_region` VALUES ('411323', '4113', '西峡县', '河南省,南阳市,西峡县', 3);
INSERT INTO `sys_region` VALUES ('411324', '4113', '镇平县', '河南省,南阳市,镇平县', 3);
INSERT INTO `sys_region` VALUES ('411325', '4113', '内乡县', '河南省,南阳市,内乡县', 3);
INSERT INTO `sys_region` VALUES ('411326', '4113', '淅川县', '河南省,南阳市,淅川县', 3);
INSERT INTO `sys_region` VALUES ('411327', '4113', '社旗县', '河南省,南阳市,社旗县', 3);
INSERT INTO `sys_region` VALUES ('411328', '4113', '唐河县', '河南省,南阳市,唐河县', 3);
INSERT INTO `sys_region` VALUES ('411329', '4113', '新野县', '河南省,南阳市,新野县', 3);
INSERT INTO `sys_region` VALUES ('411330', '4113', '桐柏县', '河南省,南阳市,桐柏县', 3);
INSERT INTO `sys_region` VALUES ('411371', '4113', '南阳高新技术产业开发区', '河南省,南阳市,南阳高新技术产业开发区', 3);
INSERT INTO `sys_region` VALUES ('411372', '4113', '南阳市城乡一体化示范区', '河南省,南阳市,南阳市城乡一体化示范区', 3);
INSERT INTO `sys_region` VALUES ('411381', '4113', '邓州市', '河南省,南阳市,邓州市', 3);
INSERT INTO `sys_region` VALUES ('4114', '41', '商丘市', '河南省,商丘市', 2);
INSERT INTO `sys_region` VALUES ('411402', '4114', '梁园区', '河南省,商丘市,梁园区', 3);
INSERT INTO `sys_region` VALUES ('411403', '4114', '睢阳区', '河南省,商丘市,睢阳区', 3);
INSERT INTO `sys_region` VALUES ('411421', '4114', '民权县', '河南省,商丘市,民权县', 3);
INSERT INTO `sys_region` VALUES ('411422', '4114', '睢县', '河南省,商丘市,睢县', 3);
INSERT INTO `sys_region` VALUES ('411423', '4114', '宁陵县', '河南省,商丘市,宁陵县', 3);
INSERT INTO `sys_region` VALUES ('411424', '4114', '柘城县', '河南省,商丘市,柘城县', 3);
INSERT INTO `sys_region` VALUES ('411425', '4114', '虞城县', '河南省,商丘市,虞城县', 3);
INSERT INTO `sys_region` VALUES ('411426', '4114', '夏邑县', '河南省,商丘市,夏邑县', 3);
INSERT INTO `sys_region` VALUES ('411471', '4114', '豫东综合物流产业聚集区', '河南省,商丘市,豫东综合物流产业聚集区', 3);
INSERT INTO `sys_region` VALUES ('411472', '4114', '河南商丘经济开发区', '河南省,商丘市,河南商丘经济开发区', 3);
INSERT INTO `sys_region` VALUES ('411481', '4114', '永城市', '河南省,商丘市,永城市', 3);
INSERT INTO `sys_region` VALUES ('4115', '41', '信阳市', '河南省,信阳市', 2);
INSERT INTO `sys_region` VALUES ('411502', '4115', '浉河区', '河南省,信阳市,浉河区', 3);
INSERT INTO `sys_region` VALUES ('411503', '4115', '平桥区', '河南省,信阳市,平桥区', 3);
INSERT INTO `sys_region` VALUES ('411521', '4115', '罗山县', '河南省,信阳市,罗山县', 3);
INSERT INTO `sys_region` VALUES ('411522', '4115', '光山县', '河南省,信阳市,光山县', 3);
INSERT INTO `sys_region` VALUES ('411523', '4115', '新县', '河南省,信阳市,新县', 3);
INSERT INTO `sys_region` VALUES ('411524', '4115', '商城县', '河南省,信阳市,商城县', 3);
INSERT INTO `sys_region` VALUES ('411525', '4115', '固始县', '河南省,信阳市,固始县', 3);
INSERT INTO `sys_region` VALUES ('411526', '4115', '潢川县', '河南省,信阳市,潢川县', 3);
INSERT INTO `sys_region` VALUES ('411527', '4115', '淮滨县', '河南省,信阳市,淮滨县', 3);
INSERT INTO `sys_region` VALUES ('411528', '4115', '息县', '河南省,信阳市,息县', 3);
INSERT INTO `sys_region` VALUES ('411571', '4115', '信阳高新技术产业开发区', '河南省,信阳市,信阳高新技术产业开发区', 3);
INSERT INTO `sys_region` VALUES ('4116', '41', '周口市', '河南省,周口市', 2);
INSERT INTO `sys_region` VALUES ('411602', '4116', '川汇区', '河南省,周口市,川汇区', 3);
INSERT INTO `sys_region` VALUES ('411621', '4116', '扶沟县', '河南省,周口市,扶沟县', 3);
INSERT INTO `sys_region` VALUES ('411622', '4116', '西华县', '河南省,周口市,西华县', 3);
INSERT INTO `sys_region` VALUES ('411623', '4116', '商水县', '河南省,周口市,商水县', 3);
INSERT INTO `sys_region` VALUES ('411624', '4116', '沈丘县', '河南省,周口市,沈丘县', 3);
INSERT INTO `sys_region` VALUES ('411625', '4116', '郸城县', '河南省,周口市,郸城县', 3);
INSERT INTO `sys_region` VALUES ('411626', '4116', '淮阳县', '河南省,周口市,淮阳县', 3);
INSERT INTO `sys_region` VALUES ('411627', '4116', '太康县', '河南省,周口市,太康县', 3);
INSERT INTO `sys_region` VALUES ('411628', '4116', '鹿邑县', '河南省,周口市,鹿邑县', 3);
INSERT INTO `sys_region` VALUES ('411671', '4116', '河南周口经济开发区', '河南省,周口市,河南周口经济开发区', 3);
INSERT INTO `sys_region` VALUES ('411681', '4116', '项城市', '河南省,周口市,项城市', 3);
INSERT INTO `sys_region` VALUES ('4117', '41', '驻马店市', '河南省,驻马店市', 2);
INSERT INTO `sys_region` VALUES ('411702', '4117', '驿城区', '河南省,驻马店市,驿城区', 3);
INSERT INTO `sys_region` VALUES ('411721', '4117', '西平县', '河南省,驻马店市,西平县', 3);
INSERT INTO `sys_region` VALUES ('411722', '4117', '上蔡县', '河南省,驻马店市,上蔡县', 3);
INSERT INTO `sys_region` VALUES ('411723', '4117', '平舆县', '河南省,驻马店市,平舆县', 3);
INSERT INTO `sys_region` VALUES ('411724', '4117', '正阳县', '河南省,驻马店市,正阳县', 3);
INSERT INTO `sys_region` VALUES ('411725', '4117', '确山县', '河南省,驻马店市,确山县', 3);
INSERT INTO `sys_region` VALUES ('411726', '4117', '泌阳县', '河南省,驻马店市,泌阳县', 3);
INSERT INTO `sys_region` VALUES ('411727', '4117', '汝南县', '河南省,驻马店市,汝南县', 3);
INSERT INTO `sys_region` VALUES ('411728', '4117', '遂平县', '河南省,驻马店市,遂平县', 3);
INSERT INTO `sys_region` VALUES ('411729', '4117', '新蔡县', '河南省,驻马店市,新蔡县', 3);
INSERT INTO `sys_region` VALUES ('411771', '4117', '河南驻马店经济开发区', '河南省,驻马店市,河南驻马店经济开发区', 3);
INSERT INTO `sys_region` VALUES ('4190', '41', '省直辖县级行政区划', '河南省,省直辖县级行政区划', 2);
INSERT INTO `sys_region` VALUES ('419001', '4190', '济源市', '河南省,省直辖县级行政区划,济源市', 3);
INSERT INTO `sys_region` VALUES ('42', '0', '湖北省', '湖北省', 1);
INSERT INTO `sys_region` VALUES ('4201', '42', '武汉市', '湖北省,武汉市', 2);
INSERT INTO `sys_region` VALUES ('420102', '4201', '江岸区', '湖北省,武汉市,江岸区', 3);
INSERT INTO `sys_region` VALUES ('420103', '4201', '江汉区', '湖北省,武汉市,江汉区', 3);
INSERT INTO `sys_region` VALUES ('420104', '4201', '硚口区', '湖北省,武汉市,硚口区', 3);
INSERT INTO `sys_region` VALUES ('420105', '4201', '汉阳区', '湖北省,武汉市,汉阳区', 3);
INSERT INTO `sys_region` VALUES ('420106', '4201', '武昌区', '湖北省,武汉市,武昌区', 3);
INSERT INTO `sys_region` VALUES ('420107', '4201', '青山区', '湖北省,武汉市,青山区', 3);
INSERT INTO `sys_region` VALUES ('420111', '4201', '洪山区', '湖北省,武汉市,洪山区', 3);
INSERT INTO `sys_region` VALUES ('420112', '4201', '东西湖区', '湖北省,武汉市,东西湖区', 3);
INSERT INTO `sys_region` VALUES ('420113', '4201', '汉南区', '湖北省,武汉市,汉南区', 3);
INSERT INTO `sys_region` VALUES ('420114', '4201', '蔡甸区', '湖北省,武汉市,蔡甸区', 3);
INSERT INTO `sys_region` VALUES ('420115', '4201', '江夏区', '湖北省,武汉市,江夏区', 3);
INSERT INTO `sys_region` VALUES ('420116', '4201', '黄陂区', '湖北省,武汉市,黄陂区', 3);
INSERT INTO `sys_region` VALUES ('420117', '4201', '新洲区', '湖北省,武汉市,新洲区', 3);
INSERT INTO `sys_region` VALUES ('4202', '42', '黄石市', '湖北省,黄石市', 2);
INSERT INTO `sys_region` VALUES ('420202', '4202', '黄石港区', '湖北省,黄石市,黄石港区', 3);
INSERT INTO `sys_region` VALUES ('420203', '4202', '西塞山区', '湖北省,黄石市,西塞山区', 3);
INSERT INTO `sys_region` VALUES ('420204', '4202', '下陆区', '湖北省,黄石市,下陆区', 3);
INSERT INTO `sys_region` VALUES ('420205', '4202', '铁山区', '湖北省,黄石市,铁山区', 3);
INSERT INTO `sys_region` VALUES ('420222', '4202', '阳新县', '湖北省,黄石市,阳新县', 3);
INSERT INTO `sys_region` VALUES ('420281', '4202', '大冶市', '湖北省,黄石市,大冶市', 3);
INSERT INTO `sys_region` VALUES ('4203', '42', '十堰市', '湖北省,十堰市', 2);
INSERT INTO `sys_region` VALUES ('420302', '4203', '茅箭区', '湖北省,十堰市,茅箭区', 3);
INSERT INTO `sys_region` VALUES ('420303', '4203', '张湾区', '湖北省,十堰市,张湾区', 3);
INSERT INTO `sys_region` VALUES ('420304', '4203', '郧阳区', '湖北省,十堰市,郧阳区', 3);
INSERT INTO `sys_region` VALUES ('420322', '4203', '郧西县', '湖北省,十堰市,郧西县', 3);
INSERT INTO `sys_region` VALUES ('420323', '4203', '竹山县', '湖北省,十堰市,竹山县', 3);
INSERT INTO `sys_region` VALUES ('420324', '4203', '竹溪县', '湖北省,十堰市,竹溪县', 3);
INSERT INTO `sys_region` VALUES ('420325', '4203', '房县', '湖北省,十堰市,房县', 3);
INSERT INTO `sys_region` VALUES ('420381', '4203', '丹江口市', '湖北省,十堰市,丹江口市', 3);
INSERT INTO `sys_region` VALUES ('4205', '42', '宜昌市', '湖北省,宜昌市', 2);
INSERT INTO `sys_region` VALUES ('420502', '4205', '西陵区', '湖北省,宜昌市,西陵区', 3);
INSERT INTO `sys_region` VALUES ('420503', '4205', '伍家岗区', '湖北省,宜昌市,伍家岗区', 3);
INSERT INTO `sys_region` VALUES ('420504', '4205', '点军区', '湖北省,宜昌市,点军区', 3);
INSERT INTO `sys_region` VALUES ('420505', '4205', '猇亭区', '湖北省,宜昌市,猇亭区', 3);
INSERT INTO `sys_region` VALUES ('420506', '4205', '夷陵区', '湖北省,宜昌市,夷陵区', 3);
INSERT INTO `sys_region` VALUES ('420525', '4205', '远安县', '湖北省,宜昌市,远安县', 3);
INSERT INTO `sys_region` VALUES ('420526', '4205', '兴山县', '湖北省,宜昌市,兴山县', 3);
INSERT INTO `sys_region` VALUES ('420527', '4205', '秭归县', '湖北省,宜昌市,秭归县', 3);
INSERT INTO `sys_region` VALUES ('420528', '4205', '长阳土家族自治县', '湖北省,宜昌市,长阳土家族自治县', 3);
INSERT INTO `sys_region` VALUES ('420529', '4205', '五峰土家族自治县', '湖北省,宜昌市,五峰土家族自治县', 3);
INSERT INTO `sys_region` VALUES ('420581', '4205', '宜都市', '湖北省,宜昌市,宜都市', 3);
INSERT INTO `sys_region` VALUES ('420582', '4205', '当阳市', '湖北省,宜昌市,当阳市', 3);
INSERT INTO `sys_region` VALUES ('420583', '4205', '枝江市', '湖北省,宜昌市,枝江市', 3);
INSERT INTO `sys_region` VALUES ('4206', '42', '襄阳市', '湖北省,襄阳市', 2);
INSERT INTO `sys_region` VALUES ('420602', '4206', '襄城区', '湖北省,襄阳市,襄城区', 3);
INSERT INTO `sys_region` VALUES ('420606', '4206', '樊城区', '湖北省,襄阳市,樊城区', 3);
INSERT INTO `sys_region` VALUES ('420607', '4206', '襄州区', '湖北省,襄阳市,襄州区', 3);
INSERT INTO `sys_region` VALUES ('420624', '4206', '南漳县', '湖北省,襄阳市,南漳县', 3);
INSERT INTO `sys_region` VALUES ('420625', '4206', '谷城县', '湖北省,襄阳市,谷城县', 3);
INSERT INTO `sys_region` VALUES ('420626', '4206', '保康县', '湖北省,襄阳市,保康县', 3);
INSERT INTO `sys_region` VALUES ('420682', '4206', '老河口市', '湖北省,襄阳市,老河口市', 3);
INSERT INTO `sys_region` VALUES ('420683', '4206', '枣阳市', '湖北省,襄阳市,枣阳市', 3);
INSERT INTO `sys_region` VALUES ('420684', '4206', '宜城市', '湖北省,襄阳市,宜城市', 3);
INSERT INTO `sys_region` VALUES ('4207', '42', '鄂州市', '湖北省,鄂州市', 2);
INSERT INTO `sys_region` VALUES ('420702', '4207', '梁子湖区', '湖北省,鄂州市,梁子湖区', 3);
INSERT INTO `sys_region` VALUES ('420703', '4207', '华容区', '湖北省,鄂州市,华容区', 3);
INSERT INTO `sys_region` VALUES ('420704', '4207', '鄂城区', '湖北省,鄂州市,鄂城区', 3);
INSERT INTO `sys_region` VALUES ('4208', '42', '荆门市', '湖北省,荆门市', 2);
INSERT INTO `sys_region` VALUES ('420802', '4208', '东宝区', '湖北省,荆门市,东宝区', 3);
INSERT INTO `sys_region` VALUES ('420804', '4208', '掇刀区', '湖北省,荆门市,掇刀区', 3);
INSERT INTO `sys_region` VALUES ('420822', '4208', '沙洋县', '湖北省,荆门市,沙洋县', 3);
INSERT INTO `sys_region` VALUES ('420881', '4208', '钟祥市', '湖北省,荆门市,钟祥市', 3);
INSERT INTO `sys_region` VALUES ('420882', '4208', '京山市', '湖北省,荆门市,京山市', 3);
INSERT INTO `sys_region` VALUES ('4209', '42', '孝感市', '湖北省,孝感市', 2);
INSERT INTO `sys_region` VALUES ('420902', '4209', '孝南区', '湖北省,孝感市,孝南区', 3);
INSERT INTO `sys_region` VALUES ('420921', '4209', '孝昌县', '湖北省,孝感市,孝昌县', 3);
INSERT INTO `sys_region` VALUES ('420922', '4209', '大悟县', '湖北省,孝感市,大悟县', 3);
INSERT INTO `sys_region` VALUES ('420923', '4209', '云梦县', '湖北省,孝感市,云梦县', 3);
INSERT INTO `sys_region` VALUES ('420981', '4209', '应城市', '湖北省,孝感市,应城市', 3);
INSERT INTO `sys_region` VALUES ('420982', '4209', '安陆市', '湖北省,孝感市,安陆市', 3);
INSERT INTO `sys_region` VALUES ('420984', '4209', '汉川市', '湖北省,孝感市,汉川市', 3);
INSERT INTO `sys_region` VALUES ('4210', '42', '荆州市', '湖北省,荆州市', 2);
INSERT INTO `sys_region` VALUES ('421002', '4210', '沙市区', '湖北省,荆州市,沙市区', 3);
INSERT INTO `sys_region` VALUES ('421003', '4210', '荆州区', '湖北省,荆州市,荆州区', 3);
INSERT INTO `sys_region` VALUES ('421022', '4210', '公安县', '湖北省,荆州市,公安县', 3);
INSERT INTO `sys_region` VALUES ('421023', '4210', '监利县', '湖北省,荆州市,监利县', 3);
INSERT INTO `sys_region` VALUES ('421024', '4210', '江陵县', '湖北省,荆州市,江陵县', 3);
INSERT INTO `sys_region` VALUES ('421071', '4210', '荆州经济技术开发区', '湖北省,荆州市,荆州经济技术开发区', 3);
INSERT INTO `sys_region` VALUES ('421081', '4210', '石首市', '湖北省,荆州市,石首市', 3);
INSERT INTO `sys_region` VALUES ('421083', '4210', '洪湖市', '湖北省,荆州市,洪湖市', 3);
INSERT INTO `sys_region` VALUES ('421087', '4210', '松滋市', '湖北省,荆州市,松滋市', 3);
INSERT INTO `sys_region` VALUES ('4211', '42', '黄冈市', '湖北省,黄冈市', 2);
INSERT INTO `sys_region` VALUES ('421102', '4211', '黄州区', '湖北省,黄冈市,黄州区', 3);
INSERT INTO `sys_region` VALUES ('421121', '4211', '团风县', '湖北省,黄冈市,团风县', 3);
INSERT INTO `sys_region` VALUES ('421122', '4211', '红安县', '湖北省,黄冈市,红安县', 3);
INSERT INTO `sys_region` VALUES ('421123', '4211', '罗田县', '湖北省,黄冈市,罗田县', 3);
INSERT INTO `sys_region` VALUES ('421124', '4211', '英山县', '湖北省,黄冈市,英山县', 3);
INSERT INTO `sys_region` VALUES ('421125', '4211', '浠水县', '湖北省,黄冈市,浠水县', 3);
INSERT INTO `sys_region` VALUES ('421126', '4211', '蕲春县', '湖北省,黄冈市,蕲春县', 3);
INSERT INTO `sys_region` VALUES ('421127', '4211', '黄梅县', '湖北省,黄冈市,黄梅县', 3);
INSERT INTO `sys_region` VALUES ('421171', '4211', '龙感湖管理区', '湖北省,黄冈市,龙感湖管理区', 3);
INSERT INTO `sys_region` VALUES ('421181', '4211', '麻城市', '湖北省,黄冈市,麻城市', 3);
INSERT INTO `sys_region` VALUES ('421182', '4211', '武穴市', '湖北省,黄冈市,武穴市', 3);
INSERT INTO `sys_region` VALUES ('4212', '42', '咸宁市', '湖北省,咸宁市', 2);
INSERT INTO `sys_region` VALUES ('421202', '4212', '咸安区', '湖北省,咸宁市,咸安区', 3);
INSERT INTO `sys_region` VALUES ('421221', '4212', '嘉鱼县', '湖北省,咸宁市,嘉鱼县', 3);
INSERT INTO `sys_region` VALUES ('421222', '4212', '通城县', '湖北省,咸宁市,通城县', 3);
INSERT INTO `sys_region` VALUES ('421223', '4212', '崇阳县', '湖北省,咸宁市,崇阳县', 3);
INSERT INTO `sys_region` VALUES ('421224', '4212', '通山县', '湖北省,咸宁市,通山县', 3);
INSERT INTO `sys_region` VALUES ('421281', '4212', '赤壁市', '湖北省,咸宁市,赤壁市', 3);
INSERT INTO `sys_region` VALUES ('4213', '42', '随州市', '湖北省,随州市', 2);
INSERT INTO `sys_region` VALUES ('421303', '4213', '曾都区', '湖北省,随州市,曾都区', 3);
INSERT INTO `sys_region` VALUES ('421321', '4213', '随县', '湖北省,随州市,随县', 3);
INSERT INTO `sys_region` VALUES ('421381', '4213', '广水市', '湖北省,随州市,广水市', 3);
INSERT INTO `sys_region` VALUES ('4228', '42', '恩施土家族苗族自治州', '湖北省,恩施土家族苗族自治州', 2);
INSERT INTO `sys_region` VALUES ('422801', '4228', '恩施市', '湖北省,恩施土家族苗族自治州,恩施市', 3);
INSERT INTO `sys_region` VALUES ('422802', '4228', '利川市', '湖北省,恩施土家族苗族自治州,利川市', 3);
INSERT INTO `sys_region` VALUES ('422822', '4228', '建始县', '湖北省,恩施土家族苗族自治州,建始县', 3);
INSERT INTO `sys_region` VALUES ('422823', '4228', '巴东县', '湖北省,恩施土家族苗族自治州,巴东县', 3);
INSERT INTO `sys_region` VALUES ('422825', '4228', '宣恩县', '湖北省,恩施土家族苗族自治州,宣恩县', 3);
INSERT INTO `sys_region` VALUES ('422826', '4228', '咸丰县', '湖北省,恩施土家族苗族自治州,咸丰县', 3);
INSERT INTO `sys_region` VALUES ('422827', '4228', '来凤县', '湖北省,恩施土家族苗族自治州,来凤县', 3);
INSERT INTO `sys_region` VALUES ('422828', '4228', '鹤峰县', '湖北省,恩施土家族苗族自治州,鹤峰县', 3);
INSERT INTO `sys_region` VALUES ('4290', '42', '省直辖县级行政区划', '湖北省,省直辖县级行政区划', 2);
INSERT INTO `sys_region` VALUES ('429004', '4290', '仙桃市', '湖北省,省直辖县级行政区划,仙桃市', 3);
INSERT INTO `sys_region` VALUES ('429005', '4290', '潜江市', '湖北省,省直辖县级行政区划,潜江市', 3);
INSERT INTO `sys_region` VALUES ('429006', '4290', '天门市', '湖北省,省直辖县级行政区划,天门市', 3);
INSERT INTO `sys_region` VALUES ('429021', '4290', '神农架林区', '湖北省,省直辖县级行政区划,神农架林区', 3);
INSERT INTO `sys_region` VALUES ('43', '0', '湖南省', '湖南省', 1);
INSERT INTO `sys_region` VALUES ('4301', '43', '长沙市', '湖南省,长沙市', 2);
INSERT INTO `sys_region` VALUES ('430102', '4301', '芙蓉区', '湖南省,长沙市,芙蓉区', 3);
INSERT INTO `sys_region` VALUES ('430103', '4301', '天心区', '湖南省,长沙市,天心区', 3);
INSERT INTO `sys_region` VALUES ('430104', '4301', '岳麓区', '湖南省,长沙市,岳麓区', 3);
INSERT INTO `sys_region` VALUES ('430105', '4301', '开福区', '湖南省,长沙市,开福区', 3);
INSERT INTO `sys_region` VALUES ('430111', '4301', '雨花区', '湖南省,长沙市,雨花区', 3);
INSERT INTO `sys_region` VALUES ('430112', '4301', '望城区', '湖南省,长沙市,望城区', 3);
INSERT INTO `sys_region` VALUES ('430121', '4301', '长沙县', '湖南省,长沙市,长沙县', 3);
INSERT INTO `sys_region` VALUES ('430181', '4301', '浏阳市', '湖南省,长沙市,浏阳市', 3);
INSERT INTO `sys_region` VALUES ('430182', '4301', '宁乡市', '湖南省,长沙市,宁乡市', 3);
INSERT INTO `sys_region` VALUES ('4302', '43', '株洲市', '湖南省,株洲市', 2);
INSERT INTO `sys_region` VALUES ('430202', '4302', '荷塘区', '湖南省,株洲市,荷塘区', 3);
INSERT INTO `sys_region` VALUES ('430203', '4302', '芦淞区', '湖南省,株洲市,芦淞区', 3);
INSERT INTO `sys_region` VALUES ('430204', '4302', '石峰区', '湖南省,株洲市,石峰区', 3);
INSERT INTO `sys_region` VALUES ('430211', '4302', '天元区', '湖南省,株洲市,天元区', 3);
INSERT INTO `sys_region` VALUES ('430212', '4302', '渌口区', '湖南省,株洲市,渌口区', 3);
INSERT INTO `sys_region` VALUES ('430223', '4302', '攸县', '湖南省,株洲市,攸县', 3);
INSERT INTO `sys_region` VALUES ('430224', '4302', '茶陵县', '湖南省,株洲市,茶陵县', 3);
INSERT INTO `sys_region` VALUES ('430225', '4302', '炎陵县', '湖南省,株洲市,炎陵县', 3);
INSERT INTO `sys_region` VALUES ('430271', '4302', '云龙示范区', '湖南省,株洲市,云龙示范区', 3);
INSERT INTO `sys_region` VALUES ('430281', '4302', '醴陵市', '湖南省,株洲市,醴陵市', 3);
INSERT INTO `sys_region` VALUES ('4303', '43', '湘潭市', '湖南省,湘潭市', 2);
INSERT INTO `sys_region` VALUES ('430302', '4303', '雨湖区', '湖南省,湘潭市,雨湖区', 3);
INSERT INTO `sys_region` VALUES ('430304', '4303', '岳塘区', '湖南省,湘潭市,岳塘区', 3);
INSERT INTO `sys_region` VALUES ('430321', '4303', '湘潭县', '湖南省,湘潭市,湘潭县', 3);
INSERT INTO `sys_region` VALUES ('430371', '4303', '湖南湘潭高新技术产业园区', '湖南省,湘潭市,湖南湘潭高新技术产业园区', 3);
INSERT INTO `sys_region` VALUES ('430372', '4303', '湘潭昭山示范区', '湖南省,湘潭市,湘潭昭山示范区', 3);
INSERT INTO `sys_region` VALUES ('430373', '4303', '湘潭九华示范区', '湖南省,湘潭市,湘潭九华示范区', 3);
INSERT INTO `sys_region` VALUES ('430381', '4303', '湘乡市', '湖南省,湘潭市,湘乡市', 3);
INSERT INTO `sys_region` VALUES ('430382', '4303', '韶山市', '湖南省,湘潭市,韶山市', 3);
INSERT INTO `sys_region` VALUES ('4304', '43', '衡阳市', '湖南省,衡阳市', 2);
INSERT INTO `sys_region` VALUES ('430405', '4304', '珠晖区', '湖南省,衡阳市,珠晖区', 3);
INSERT INTO `sys_region` VALUES ('430406', '4304', '雁峰区', '湖南省,衡阳市,雁峰区', 3);
INSERT INTO `sys_region` VALUES ('430407', '4304', '石鼓区', '湖南省,衡阳市,石鼓区', 3);
INSERT INTO `sys_region` VALUES ('430408', '4304', '蒸湘区', '湖南省,衡阳市,蒸湘区', 3);
INSERT INTO `sys_region` VALUES ('430412', '4304', '南岳区', '湖南省,衡阳市,南岳区', 3);
INSERT INTO `sys_region` VALUES ('430421', '4304', '衡阳县', '湖南省,衡阳市,衡阳县', 3);
INSERT INTO `sys_region` VALUES ('430422', '4304', '衡南县', '湖南省,衡阳市,衡南县', 3);
INSERT INTO `sys_region` VALUES ('430423', '4304', '衡山县', '湖南省,衡阳市,衡山县', 3);
INSERT INTO `sys_region` VALUES ('430424', '4304', '衡东县', '湖南省,衡阳市,衡东县', 3);
INSERT INTO `sys_region` VALUES ('430426', '4304', '祁东县', '湖南省,衡阳市,祁东县', 3);
INSERT INTO `sys_region` VALUES ('430471', '4304', '衡阳综合保税区', '湖南省,衡阳市,衡阳综合保税区', 3);
INSERT INTO `sys_region` VALUES ('430472', '4304', '湖南衡阳高新技术产业园区', '湖南省,衡阳市,湖南衡阳高新技术产业园区', 3);
INSERT INTO `sys_region` VALUES ('430473', '4304', '湖南衡阳松木经济开发区', '湖南省,衡阳市,湖南衡阳松木经济开发区', 3);
INSERT INTO `sys_region` VALUES ('430481', '4304', '耒阳市', '湖南省,衡阳市,耒阳市', 3);
INSERT INTO `sys_region` VALUES ('430482', '4304', '常宁市', '湖南省,衡阳市,常宁市', 3);
INSERT INTO `sys_region` VALUES ('4305', '43', '邵阳市', '湖南省,邵阳市', 2);
INSERT INTO `sys_region` VALUES ('430502', '4305', '双清区', '湖南省,邵阳市,双清区', 3);
INSERT INTO `sys_region` VALUES ('430503', '4305', '大祥区', '湖南省,邵阳市,大祥区', 3);
INSERT INTO `sys_region` VALUES ('430511', '4305', '北塔区', '湖南省,邵阳市,北塔区', 3);
INSERT INTO `sys_region` VALUES ('430521', '4305', '邵东县', '湖南省,邵阳市,邵东县', 3);
INSERT INTO `sys_region` VALUES ('430522', '4305', '新邵县', '湖南省,邵阳市,新邵县', 3);
INSERT INTO `sys_region` VALUES ('430523', '4305', '邵阳县', '湖南省,邵阳市,邵阳县', 3);
INSERT INTO `sys_region` VALUES ('430524', '4305', '隆回县', '湖南省,邵阳市,隆回县', 3);
INSERT INTO `sys_region` VALUES ('430525', '4305', '洞口县', '湖南省,邵阳市,洞口县', 3);
INSERT INTO `sys_region` VALUES ('430527', '4305', '绥宁县', '湖南省,邵阳市,绥宁县', 3);
INSERT INTO `sys_region` VALUES ('430528', '4305', '新宁县', '湖南省,邵阳市,新宁县', 3);
INSERT INTO `sys_region` VALUES ('430529', '4305', '城步苗族自治县', '湖南省,邵阳市,城步苗族自治县', 3);
INSERT INTO `sys_region` VALUES ('430581', '4305', '武冈市', '湖南省,邵阳市,武冈市', 3);
INSERT INTO `sys_region` VALUES ('4306', '43', '岳阳市', '湖南省,岳阳市', 2);
INSERT INTO `sys_region` VALUES ('430602', '4306', '岳阳楼区', '湖南省,岳阳市,岳阳楼区', 3);
INSERT INTO `sys_region` VALUES ('430603', '4306', '云溪区', '湖南省,岳阳市,云溪区', 3);
INSERT INTO `sys_region` VALUES ('430611', '4306', '君山区', '湖南省,岳阳市,君山区', 3);
INSERT INTO `sys_region` VALUES ('430621', '4306', '岳阳县', '湖南省,岳阳市,岳阳县', 3);
INSERT INTO `sys_region` VALUES ('430623', '4306', '华容县', '湖南省,岳阳市,华容县', 3);
INSERT INTO `sys_region` VALUES ('430624', '4306', '湘阴县', '湖南省,岳阳市,湘阴县', 3);
INSERT INTO `sys_region` VALUES ('430626', '4306', '平江县', '湖南省,岳阳市,平江县', 3);
INSERT INTO `sys_region` VALUES ('430671', '4306', '岳阳市屈原管理区', '湖南省,岳阳市,岳阳市屈原管理区', 3);
INSERT INTO `sys_region` VALUES ('430681', '4306', '汨罗市', '湖南省,岳阳市,汨罗市', 3);
INSERT INTO `sys_region` VALUES ('430682', '4306', '临湘市', '湖南省,岳阳市,临湘市', 3);
INSERT INTO `sys_region` VALUES ('4307', '43', '常德市', '湖南省,常德市', 2);
INSERT INTO `sys_region` VALUES ('430702', '4307', '武陵区', '湖南省,常德市,武陵区', 3);
INSERT INTO `sys_region` VALUES ('430703', '4307', '鼎城区', '湖南省,常德市,鼎城区', 3);
INSERT INTO `sys_region` VALUES ('430721', '4307', '安乡县', '湖南省,常德市,安乡县', 3);
INSERT INTO `sys_region` VALUES ('430722', '4307', '汉寿县', '湖南省,常德市,汉寿县', 3);
INSERT INTO `sys_region` VALUES ('430723', '4307', '澧县', '湖南省,常德市,澧县', 3);
INSERT INTO `sys_region` VALUES ('430724', '4307', '临澧县', '湖南省,常德市,临澧县', 3);
INSERT INTO `sys_region` VALUES ('430725', '4307', '桃源县', '湖南省,常德市,桃源县', 3);
INSERT INTO `sys_region` VALUES ('430726', '4307', '石门县', '湖南省,常德市,石门县', 3);
INSERT INTO `sys_region` VALUES ('430771', '4307', '常德市西洞庭管理区', '湖南省,常德市,常德市西洞庭管理区', 3);
INSERT INTO `sys_region` VALUES ('430781', '4307', '津市市', '湖南省,常德市,津市市', 3);
INSERT INTO `sys_region` VALUES ('4308', '43', '张家界市', '湖南省,张家界市', 2);
INSERT INTO `sys_region` VALUES ('430802', '4308', '永定区', '湖南省,张家界市,永定区', 3);
INSERT INTO `sys_region` VALUES ('430811', '4308', '武陵源区', '湖南省,张家界市,武陵源区', 3);
INSERT INTO `sys_region` VALUES ('430821', '4308', '慈利县', '湖南省,张家界市,慈利县', 3);
INSERT INTO `sys_region` VALUES ('430822', '4308', '桑植县', '湖南省,张家界市,桑植县', 3);
INSERT INTO `sys_region` VALUES ('4309', '43', '益阳市', '湖南省,益阳市', 2);
INSERT INTO `sys_region` VALUES ('430902', '4309', '资阳区', '湖南省,益阳市,资阳区', 3);
INSERT INTO `sys_region` VALUES ('430903', '4309', '赫山区', '湖南省,益阳市,赫山区', 3);
INSERT INTO `sys_region` VALUES ('430921', '4309', '南县', '湖南省,益阳市,南县', 3);
INSERT INTO `sys_region` VALUES ('430922', '4309', '桃江县', '湖南省,益阳市,桃江县', 3);
INSERT INTO `sys_region` VALUES ('430923', '4309', '安化县', '湖南省,益阳市,安化县', 3);
INSERT INTO `sys_region` VALUES ('430971', '4309', '益阳市大通湖管理区', '湖南省,益阳市,益阳市大通湖管理区', 3);
INSERT INTO `sys_region` VALUES ('430972', '4309', '湖南益阳高新技术产业园区', '湖南省,益阳市,湖南益阳高新技术产业园区', 3);
INSERT INTO `sys_region` VALUES ('430981', '4309', '沅江市', '湖南省,益阳市,沅江市', 3);
INSERT INTO `sys_region` VALUES ('4310', '43', '郴州市', '湖南省,郴州市', 2);
INSERT INTO `sys_region` VALUES ('431002', '4310', '北湖区', '湖南省,郴州市,北湖区', 3);
INSERT INTO `sys_region` VALUES ('431003', '4310', '苏仙区', '湖南省,郴州市,苏仙区', 3);
INSERT INTO `sys_region` VALUES ('431021', '4310', '桂阳县', '湖南省,郴州市,桂阳县', 3);
INSERT INTO `sys_region` VALUES ('431022', '4310', '宜章县', '湖南省,郴州市,宜章县', 3);
INSERT INTO `sys_region` VALUES ('431023', '4310', '永兴县', '湖南省,郴州市,永兴县', 3);
INSERT INTO `sys_region` VALUES ('431024', '4310', '嘉禾县', '湖南省,郴州市,嘉禾县', 3);
INSERT INTO `sys_region` VALUES ('431025', '4310', '临武县', '湖南省,郴州市,临武县', 3);
INSERT INTO `sys_region` VALUES ('431026', '4310', '汝城县', '湖南省,郴州市,汝城县', 3);
INSERT INTO `sys_region` VALUES ('431027', '4310', '桂东县', '湖南省,郴州市,桂东县', 3);
INSERT INTO `sys_region` VALUES ('431028', '4310', '安仁县', '湖南省,郴州市,安仁县', 3);
INSERT INTO `sys_region` VALUES ('431081', '4310', '资兴市', '湖南省,郴州市,资兴市', 3);
INSERT INTO `sys_region` VALUES ('4311', '43', '永州市', '湖南省,永州市', 2);
INSERT INTO `sys_region` VALUES ('431102', '4311', '零陵区', '湖南省,永州市,零陵区', 3);
INSERT INTO `sys_region` VALUES ('431103', '4311', '冷水滩区', '湖南省,永州市,冷水滩区', 3);
INSERT INTO `sys_region` VALUES ('431121', '4311', '祁阳县', '湖南省,永州市,祁阳县', 3);
INSERT INTO `sys_region` VALUES ('431122', '4311', '东安县', '湖南省,永州市,东安县', 3);
INSERT INTO `sys_region` VALUES ('431123', '4311', '双牌县', '湖南省,永州市,双牌县', 3);
INSERT INTO `sys_region` VALUES ('431124', '4311', '道县', '湖南省,永州市,道县', 3);
INSERT INTO `sys_region` VALUES ('431125', '4311', '江永县', '湖南省,永州市,江永县', 3);
INSERT INTO `sys_region` VALUES ('431126', '4311', '宁远县', '湖南省,永州市,宁远县', 3);
INSERT INTO `sys_region` VALUES ('431127', '4311', '蓝山县', '湖南省,永州市,蓝山县', 3);
INSERT INTO `sys_region` VALUES ('431128', '4311', '新田县', '湖南省,永州市,新田县', 3);
INSERT INTO `sys_region` VALUES ('431129', '4311', '江华瑶族自治县', '湖南省,永州市,江华瑶族自治县', 3);
INSERT INTO `sys_region` VALUES ('431171', '4311', '永州经济技术开发区', '湖南省,永州市,永州经济技术开发区', 3);
INSERT INTO `sys_region` VALUES ('431172', '4311', '永州市金洞管理区', '湖南省,永州市,永州市金洞管理区', 3);
INSERT INTO `sys_region` VALUES ('431173', '4311', '永州市回龙圩管理区', '湖南省,永州市,永州市回龙圩管理区', 3);
INSERT INTO `sys_region` VALUES ('4312', '43', '怀化市', '湖南省,怀化市', 2);
INSERT INTO `sys_region` VALUES ('431202', '4312', '鹤城区', '湖南省,怀化市,鹤城区', 3);
INSERT INTO `sys_region` VALUES ('431221', '4312', '中方县', '湖南省,怀化市,中方县', 3);
INSERT INTO `sys_region` VALUES ('431222', '4312', '沅陵县', '湖南省,怀化市,沅陵县', 3);
INSERT INTO `sys_region` VALUES ('431223', '4312', '辰溪县', '湖南省,怀化市,辰溪县', 3);
INSERT INTO `sys_region` VALUES ('431224', '4312', '溆浦县', '湖南省,怀化市,溆浦县', 3);
INSERT INTO `sys_region` VALUES ('431225', '4312', '会同县', '湖南省,怀化市,会同县', 3);
INSERT INTO `sys_region` VALUES ('431226', '4312', '麻阳苗族自治县', '湖南省,怀化市,麻阳苗族自治县', 3);
INSERT INTO `sys_region` VALUES ('431227', '4312', '新晃侗族自治县', '湖南省,怀化市,新晃侗族自治县', 3);
INSERT INTO `sys_region` VALUES ('431228', '4312', '芷江侗族自治县', '湖南省,怀化市,芷江侗族自治县', 3);
INSERT INTO `sys_region` VALUES ('431229', '4312', '靖州苗族侗族自治县', '湖南省,怀化市,靖州苗族侗族自治县', 3);
INSERT INTO `sys_region` VALUES ('431230', '4312', '通道侗族自治县', '湖南省,怀化市,通道侗族自治县', 3);
INSERT INTO `sys_region` VALUES ('431271', '4312', '怀化市洪江管理区', '湖南省,怀化市,怀化市洪江管理区', 3);
INSERT INTO `sys_region` VALUES ('431281', '4312', '洪江市', '湖南省,怀化市,洪江市', 3);
INSERT INTO `sys_region` VALUES ('4313', '43', '娄底市', '湖南省,娄底市', 2);
INSERT INTO `sys_region` VALUES ('431302', '4313', '娄星区', '湖南省,娄底市,娄星区', 3);
INSERT INTO `sys_region` VALUES ('431321', '4313', '双峰县', '湖南省,娄底市,双峰县', 3);
INSERT INTO `sys_region` VALUES ('431322', '4313', '新化县', '湖南省,娄底市,新化县', 3);
INSERT INTO `sys_region` VALUES ('431381', '4313', '冷水江市', '湖南省,娄底市,冷水江市', 3);
INSERT INTO `sys_region` VALUES ('431382', '4313', '涟源市', '湖南省,娄底市,涟源市', 3);
INSERT INTO `sys_region` VALUES ('4331', '43', '湘西土家族苗族自治州', '湖南省,湘西土家族苗族自治州', 2);
INSERT INTO `sys_region` VALUES ('433101', '4331', '吉首市', '湖南省,湘西土家族苗族自治州,吉首市', 3);
INSERT INTO `sys_region` VALUES ('433122', '4331', '泸溪县', '湖南省,湘西土家族苗族自治州,泸溪县', 3);
INSERT INTO `sys_region` VALUES ('433123', '4331', '凤凰县', '湖南省,湘西土家族苗族自治州,凤凰县', 3);
INSERT INTO `sys_region` VALUES ('433124', '4331', '花垣县', '湖南省,湘西土家族苗族自治州,花垣县', 3);
INSERT INTO `sys_region` VALUES ('433125', '4331', '保靖县', '湖南省,湘西土家族苗族自治州,保靖县', 3);
INSERT INTO `sys_region` VALUES ('433126', '4331', '古丈县', '湖南省,湘西土家族苗族自治州,古丈县', 3);
INSERT INTO `sys_region` VALUES ('433127', '4331', '永顺县', '湖南省,湘西土家族苗族自治州,永顺县', 3);
INSERT INTO `sys_region` VALUES ('433130', '4331', '龙山县', '湖南省,湘西土家族苗族自治州,龙山县', 3);
INSERT INTO `sys_region` VALUES ('433172', '4331', '湖南吉首经济开发区', '湖南省,湘西土家族苗族自治州,湖南吉首经济开发区', 3);
INSERT INTO `sys_region` VALUES ('433173', '4331', '湖南永顺经济开发区', '湖南省,湘西土家族苗族自治州,湖南永顺经济开发区', 3);
INSERT INTO `sys_region` VALUES ('44', '0', '广东省', '广东省', 1);
INSERT INTO `sys_region` VALUES ('4401', '44', '广州市', '广东省,广州市', 2);
INSERT INTO `sys_region` VALUES ('440103', '4401', '荔湾区', '广东省,广州市,荔湾区', 3);
INSERT INTO `sys_region` VALUES ('440104', '4401', '越秀区', '广东省,广州市,越秀区', 3);
INSERT INTO `sys_region` VALUES ('440105', '4401', '海珠区', '广东省,广州市,海珠区', 3);
INSERT INTO `sys_region` VALUES ('440106', '4401', '天河区', '广东省,广州市,天河区', 3);
INSERT INTO `sys_region` VALUES ('440111', '4401', '白云区', '广东省,广州市,白云区', 3);
INSERT INTO `sys_region` VALUES ('440112', '4401', '黄埔区', '广东省,广州市,黄埔区', 3);
INSERT INTO `sys_region` VALUES ('440113', '4401', '番禺区', '广东省,广州市,番禺区', 3);
INSERT INTO `sys_region` VALUES ('440114', '4401', '花都区', '广东省,广州市,花都区', 3);
INSERT INTO `sys_region` VALUES ('440115', '4401', '南沙区', '广东省,广州市,南沙区', 3);
INSERT INTO `sys_region` VALUES ('440117', '4401', '从化区', '广东省,广州市,从化区', 3);
INSERT INTO `sys_region` VALUES ('440118', '4401', '增城区', '广东省,广州市,增城区', 3);
INSERT INTO `sys_region` VALUES ('4402', '44', '韶关市', '广东省,韶关市', 2);
INSERT INTO `sys_region` VALUES ('440203', '4402', '武江区', '广东省,韶关市,武江区', 3);
INSERT INTO `sys_region` VALUES ('440204', '4402', '浈江区', '广东省,韶关市,浈江区', 3);
INSERT INTO `sys_region` VALUES ('440205', '4402', '曲江区', '广东省,韶关市,曲江区', 3);
INSERT INTO `sys_region` VALUES ('440222', '4402', '始兴县', '广东省,韶关市,始兴县', 3);
INSERT INTO `sys_region` VALUES ('440224', '4402', '仁化县', '广东省,韶关市,仁化县', 3);
INSERT INTO `sys_region` VALUES ('440229', '4402', '翁源县', '广东省,韶关市,翁源县', 3);
INSERT INTO `sys_region` VALUES ('440232', '4402', '乳源瑶族自治县', '广东省,韶关市,乳源瑶族自治县', 3);
INSERT INTO `sys_region` VALUES ('440233', '4402', '新丰县', '广东省,韶关市,新丰县', 3);
INSERT INTO `sys_region` VALUES ('440281', '4402', '乐昌市', '广东省,韶关市,乐昌市', 3);
INSERT INTO `sys_region` VALUES ('440282', '4402', '南雄市', '广东省,韶关市,南雄市', 3);
INSERT INTO `sys_region` VALUES ('4403', '44', '深圳市', '广东省,深圳市', 2);
INSERT INTO `sys_region` VALUES ('440303', '4403', '罗湖区', '广东省,深圳市,罗湖区', 3);
INSERT INTO `sys_region` VALUES ('440304', '4403', '福田区', '广东省,深圳市,福田区', 3);
INSERT INTO `sys_region` VALUES ('440305', '4403', '南山区', '广东省,深圳市,南山区', 3);
INSERT INTO `sys_region` VALUES ('440306', '4403', '宝安区', '广东省,深圳市,宝安区', 3);
INSERT INTO `sys_region` VALUES ('440307', '4403', '龙岗区', '广东省,深圳市,龙岗区', 3);
INSERT INTO `sys_region` VALUES ('440308', '4403', '盐田区', '广东省,深圳市,盐田区', 3);
INSERT INTO `sys_region` VALUES ('440309', '4403', '龙华区', '广东省,深圳市,龙华区', 3);
INSERT INTO `sys_region` VALUES ('440310', '4403', '坪山区', '广东省,深圳市,坪山区', 3);
INSERT INTO `sys_region` VALUES ('440311', '4403', '光明区', '广东省,深圳市,光明区', 3);
INSERT INTO `sys_region` VALUES ('4404', '44', '珠海市', '广东省,珠海市', 2);
INSERT INTO `sys_region` VALUES ('440402', '4404', '香洲区', '广东省,珠海市,香洲区', 3);
INSERT INTO `sys_region` VALUES ('440403', '4404', '斗门区', '广东省,珠海市,斗门区', 3);
INSERT INTO `sys_region` VALUES ('440404', '4404', '金湾区', '广东省,珠海市,金湾区', 3);
INSERT INTO `sys_region` VALUES ('4405', '44', '汕头市', '广东省,汕头市', 2);
INSERT INTO `sys_region` VALUES ('440507', '4405', '龙湖区', '广东省,汕头市,龙湖区', 3);
INSERT INTO `sys_region` VALUES ('440511', '4405', '金平区', '广东省,汕头市,金平区', 3);
INSERT INTO `sys_region` VALUES ('440512', '4405', '濠江区', '广东省,汕头市,濠江区', 3);
INSERT INTO `sys_region` VALUES ('440513', '4405', '潮阳区', '广东省,汕头市,潮阳区', 3);
INSERT INTO `sys_region` VALUES ('440514', '4405', '潮南区', '广东省,汕头市,潮南区', 3);
INSERT INTO `sys_region` VALUES ('440515', '4405', '澄海区', '广东省,汕头市,澄海区', 3);
INSERT INTO `sys_region` VALUES ('440523', '4405', '南澳县', '广东省,汕头市,南澳县', 3);
INSERT INTO `sys_region` VALUES ('4406', '44', '佛山市', '广东省,佛山市', 2);
INSERT INTO `sys_region` VALUES ('440604', '4406', '禅城区', '广东省,佛山市,禅城区', 3);
INSERT INTO `sys_region` VALUES ('440605', '4406', '南海区', '广东省,佛山市,南海区', 3);
INSERT INTO `sys_region` VALUES ('440606', '4406', '顺德区', '广东省,佛山市,顺德区', 3);
INSERT INTO `sys_region` VALUES ('440607', '4406', '三水区', '广东省,佛山市,三水区', 3);
INSERT INTO `sys_region` VALUES ('440608', '4406', '高明区', '广东省,佛山市,高明区', 3);
INSERT INTO `sys_region` VALUES ('4407', '44', '江门市', '广东省,江门市', 2);
INSERT INTO `sys_region` VALUES ('440703', '4407', '蓬江区', '广东省,江门市,蓬江区', 3);
INSERT INTO `sys_region` VALUES ('440704', '4407', '江海区', '广东省,江门市,江海区', 3);
INSERT INTO `sys_region` VALUES ('440705', '4407', '新会区', '广东省,江门市,新会区', 3);
INSERT INTO `sys_region` VALUES ('440781', '4407', '台山市', '广东省,江门市,台山市', 3);
INSERT INTO `sys_region` VALUES ('440783', '4407', '开平市', '广东省,江门市,开平市', 3);
INSERT INTO `sys_region` VALUES ('440784', '4407', '鹤山市', '广东省,江门市,鹤山市', 3);
INSERT INTO `sys_region` VALUES ('440785', '4407', '恩平市', '广东省,江门市,恩平市', 3);
INSERT INTO `sys_region` VALUES ('4408', '44', '湛江市', '广东省,湛江市', 2);
INSERT INTO `sys_region` VALUES ('440802', '4408', '赤坎区', '广东省,湛江市,赤坎区', 3);
INSERT INTO `sys_region` VALUES ('440803', '4408', '霞山区', '广东省,湛江市,霞山区', 3);
INSERT INTO `sys_region` VALUES ('440804', '4408', '坡头区', '广东省,湛江市,坡头区', 3);
INSERT INTO `sys_region` VALUES ('440811', '4408', '麻章区', '广东省,湛江市,麻章区', 3);
INSERT INTO `sys_region` VALUES ('440823', '4408', '遂溪县', '广东省,湛江市,遂溪县', 3);
INSERT INTO `sys_region` VALUES ('440825', '4408', '徐闻县', '广东省,湛江市,徐闻县', 3);
INSERT INTO `sys_region` VALUES ('440881', '4408', '廉江市', '广东省,湛江市,廉江市', 3);
INSERT INTO `sys_region` VALUES ('440882', '4408', '雷州市', '广东省,湛江市,雷州市', 3);
INSERT INTO `sys_region` VALUES ('440883', '4408', '吴川市', '广东省,湛江市,吴川市', 3);
INSERT INTO `sys_region` VALUES ('4409', '44', '茂名市', '广东省,茂名市', 2);
INSERT INTO `sys_region` VALUES ('440902', '4409', '茂南区', '广东省,茂名市,茂南区', 3);
INSERT INTO `sys_region` VALUES ('440904', '4409', '电白区', '广东省,茂名市,电白区', 3);
INSERT INTO `sys_region` VALUES ('440981', '4409', '高州市', '广东省,茂名市,高州市', 3);
INSERT INTO `sys_region` VALUES ('440982', '4409', '化州市', '广东省,茂名市,化州市', 3);
INSERT INTO `sys_region` VALUES ('440983', '4409', '信宜市', '广东省,茂名市,信宜市', 3);
INSERT INTO `sys_region` VALUES ('4412', '44', '肇庆市', '广东省,肇庆市', 2);
INSERT INTO `sys_region` VALUES ('441202', '4412', '端州区', '广东省,肇庆市,端州区', 3);
INSERT INTO `sys_region` VALUES ('441203', '4412', '鼎湖区', '广东省,肇庆市,鼎湖区', 3);
INSERT INTO `sys_region` VALUES ('441204', '4412', '高要区', '广东省,肇庆市,高要区', 3);
INSERT INTO `sys_region` VALUES ('441223', '4412', '广宁县', '广东省,肇庆市,广宁县', 3);
INSERT INTO `sys_region` VALUES ('441224', '4412', '怀集县', '广东省,肇庆市,怀集县', 3);
INSERT INTO `sys_region` VALUES ('441225', '4412', '封开县', '广东省,肇庆市,封开县', 3);
INSERT INTO `sys_region` VALUES ('441226', '4412', '德庆县', '广东省,肇庆市,德庆县', 3);
INSERT INTO `sys_region` VALUES ('441284', '4412', '四会市', '广东省,肇庆市,四会市', 3);
INSERT INTO `sys_region` VALUES ('4413', '44', '惠州市', '广东省,惠州市', 2);
INSERT INTO `sys_region` VALUES ('441302', '4413', '惠城区', '广东省,惠州市,惠城区', 3);
INSERT INTO `sys_region` VALUES ('441303', '4413', '惠阳区', '广东省,惠州市,惠阳区', 3);
INSERT INTO `sys_region` VALUES ('441322', '4413', '博罗县', '广东省,惠州市,博罗县', 3);
INSERT INTO `sys_region` VALUES ('441323', '4413', '惠东县', '广东省,惠州市,惠东县', 3);
INSERT INTO `sys_region` VALUES ('441324', '4413', '龙门县', '广东省,惠州市,龙门县', 3);
INSERT INTO `sys_region` VALUES ('4414', '44', '梅州市', '广东省,梅州市', 2);
INSERT INTO `sys_region` VALUES ('441402', '4414', '梅江区', '广东省,梅州市,梅江区', 3);
INSERT INTO `sys_region` VALUES ('441403', '4414', '梅县区', '广东省,梅州市,梅县区', 3);
INSERT INTO `sys_region` VALUES ('441422', '4414', '大埔县', '广东省,梅州市,大埔县', 3);
INSERT INTO `sys_region` VALUES ('441423', '4414', '丰顺县', '广东省,梅州市,丰顺县', 3);
INSERT INTO `sys_region` VALUES ('441424', '4414', '五华县', '广东省,梅州市,五华县', 3);
INSERT INTO `sys_region` VALUES ('441426', '4414', '平远县', '广东省,梅州市,平远县', 3);
INSERT INTO `sys_region` VALUES ('441427', '4414', '蕉岭县', '广东省,梅州市,蕉岭县', 3);
INSERT INTO `sys_region` VALUES ('441481', '4414', '兴宁市', '广东省,梅州市,兴宁市', 3);
INSERT INTO `sys_region` VALUES ('4415', '44', '汕尾市', '广东省,汕尾市', 2);
INSERT INTO `sys_region` VALUES ('441502', '4415', '城区', '广东省,汕尾市,城区', 3);
INSERT INTO `sys_region` VALUES ('441521', '4415', '海丰县', '广东省,汕尾市,海丰县', 3);
INSERT INTO `sys_region` VALUES ('441523', '4415', '陆河县', '广东省,汕尾市,陆河县', 3);
INSERT INTO `sys_region` VALUES ('441581', '4415', '陆丰市', '广东省,汕尾市,陆丰市', 3);
INSERT INTO `sys_region` VALUES ('4416', '44', '河源市', '广东省,河源市', 2);
INSERT INTO `sys_region` VALUES ('441602', '4416', '源城区', '广东省,河源市,源城区', 3);
INSERT INTO `sys_region` VALUES ('441621', '4416', '紫金县', '广东省,河源市,紫金县', 3);
INSERT INTO `sys_region` VALUES ('441622', '4416', '龙川县', '广东省,河源市,龙川县', 3);
INSERT INTO `sys_region` VALUES ('441623', '4416', '连平县', '广东省,河源市,连平县', 3);
INSERT INTO `sys_region` VALUES ('441624', '4416', '和平县', '广东省,河源市,和平县', 3);
INSERT INTO `sys_region` VALUES ('441625', '4416', '东源县', '广东省,河源市,东源县', 3);
INSERT INTO `sys_region` VALUES ('4417', '44', '阳江市', '广东省,阳江市', 2);
INSERT INTO `sys_region` VALUES ('441702', '4417', '江城区', '广东省,阳江市,江城区', 3);
INSERT INTO `sys_region` VALUES ('441704', '4417', '阳东区', '广东省,阳江市,阳东区', 3);
INSERT INTO `sys_region` VALUES ('441721', '4417', '阳西县', '广东省,阳江市,阳西县', 3);
INSERT INTO `sys_region` VALUES ('441781', '4417', '阳春市', '广东省,阳江市,阳春市', 3);
INSERT INTO `sys_region` VALUES ('4418', '44', '清远市', '广东省,清远市', 2);
INSERT INTO `sys_region` VALUES ('441802', '4418', '清城区', '广东省,清远市,清城区', 3);
INSERT INTO `sys_region` VALUES ('441803', '4418', '清新区', '广东省,清远市,清新区', 3);
INSERT INTO `sys_region` VALUES ('441821', '4418', '佛冈县', '广东省,清远市,佛冈县', 3);
INSERT INTO `sys_region` VALUES ('441823', '4418', '阳山县', '广东省,清远市,阳山县', 3);
INSERT INTO `sys_region` VALUES ('441825', '4418', '连山壮族瑶族自治县', '广东省,清远市,连山壮族瑶族自治县', 3);
INSERT INTO `sys_region` VALUES ('441826', '4418', '连南瑶族自治县', '广东省,清远市,连南瑶族自治县', 3);
INSERT INTO `sys_region` VALUES ('441881', '4418', '英德市', '广东省,清远市,英德市', 3);
INSERT INTO `sys_region` VALUES ('441882', '4418', '连州市', '广东省,清远市,连州市', 3);
INSERT INTO `sys_region` VALUES ('4419', '44', '东莞市', '广东省,东莞市', 2);
INSERT INTO `sys_region` VALUES ('441900003', '4419', '东城街道', '广东省,东莞市,东城街道', 3);
INSERT INTO `sys_region` VALUES ('441900004', '4419', '南城街道', '广东省,东莞市,南城街道', 3);
INSERT INTO `sys_region` VALUES ('441900005', '4419', '万江街道', '广东省,东莞市,万江街道', 3);
INSERT INTO `sys_region` VALUES ('441900006', '4419', '莞城街道', '广东省,东莞市,莞城街道', 3);
INSERT INTO `sys_region` VALUES ('441900101', '4419', '石碣镇', '广东省,东莞市,石碣镇', 3);
INSERT INTO `sys_region` VALUES ('441900102', '4419', '石龙镇', '广东省,东莞市,石龙镇', 3);
INSERT INTO `sys_region` VALUES ('441900103', '4419', '茶山镇', '广东省,东莞市,茶山镇', 3);
INSERT INTO `sys_region` VALUES ('441900104', '4419', '石排镇', '广东省,东莞市,石排镇', 3);
INSERT INTO `sys_region` VALUES ('441900105', '4419', '企石镇', '广东省,东莞市,企石镇', 3);
INSERT INTO `sys_region` VALUES ('441900106', '4419', '横沥镇', '广东省,东莞市,横沥镇', 3);
INSERT INTO `sys_region` VALUES ('441900107', '4419', '桥头镇', '广东省,东莞市,桥头镇', 3);
INSERT INTO `sys_region` VALUES ('441900108', '4419', '谢岗镇', '广东省,东莞市,谢岗镇', 3);
INSERT INTO `sys_region` VALUES ('441900109', '4419', '东坑镇', '广东省,东莞市,东坑镇', 3);
INSERT INTO `sys_region` VALUES ('441900110', '4419', '常平镇', '广东省,东莞市,常平镇', 3);
INSERT INTO `sys_region` VALUES ('441900111', '4419', '寮步镇', '广东省,东莞市,寮步镇', 3);
INSERT INTO `sys_region` VALUES ('441900112', '4419', '樟木头镇', '广东省,东莞市,樟木头镇', 3);
INSERT INTO `sys_region` VALUES ('441900113', '4419', '大朗镇', '广东省,东莞市,大朗镇', 3);
INSERT INTO `sys_region` VALUES ('441900114', '4419', '黄江镇', '广东省,东莞市,黄江镇', 3);
INSERT INTO `sys_region` VALUES ('441900115', '4419', '清溪镇', '广东省,东莞市,清溪镇', 3);
INSERT INTO `sys_region` VALUES ('441900116', '4419', '塘厦镇', '广东省,东莞市,塘厦镇', 3);
INSERT INTO `sys_region` VALUES ('441900117', '4419', '凤岗镇', '广东省,东莞市,凤岗镇', 3);
INSERT INTO `sys_region` VALUES ('441900118', '4419', '大岭山镇', '广东省,东莞市,大岭山镇', 3);
INSERT INTO `sys_region` VALUES ('441900119', '4419', '长安镇', '广东省,东莞市,长安镇', 3);
INSERT INTO `sys_region` VALUES ('441900121', '4419', '虎门镇', '广东省,东莞市,虎门镇', 3);
INSERT INTO `sys_region` VALUES ('441900122', '4419', '厚街镇', '广东省,东莞市,厚街镇', 3);
INSERT INTO `sys_region` VALUES ('441900123', '4419', '沙田镇', '广东省,东莞市,沙田镇', 3);
INSERT INTO `sys_region` VALUES ('441900124', '4419', '道滘镇', '广东省,东莞市,道滘镇', 3);
INSERT INTO `sys_region` VALUES ('441900125', '4419', '洪梅镇', '广东省,东莞市,洪梅镇', 3);
INSERT INTO `sys_region` VALUES ('441900126', '4419', '麻涌镇', '广东省,东莞市,麻涌镇', 3);
INSERT INTO `sys_region` VALUES ('441900127', '4419', '望牛墩镇', '广东省,东莞市,望牛墩镇', 3);
INSERT INTO `sys_region` VALUES ('441900128', '4419', '中堂镇', '广东省,东莞市,中堂镇', 3);
INSERT INTO `sys_region` VALUES ('441900129', '4419', '高埗镇', '广东省,东莞市,高埗镇', 3);
INSERT INTO `sys_region` VALUES ('441900401', '4419', '松山湖管委会', '广东省,东莞市,松山湖管委会', 3);
INSERT INTO `sys_region` VALUES ('441900402', '4419', '东莞港', '广东省,东莞市,东莞港', 3);
INSERT INTO `sys_region` VALUES ('441900403', '4419', '东莞生态园', '广东省,东莞市,东莞生态园', 3);
INSERT INTO `sys_region` VALUES ('4420', '44', '中山市', '广东省,中山市', 2);
INSERT INTO `sys_region` VALUES ('442000001', '4420', '石岐区街道', '广东省,中山市,石岐区街道', 3);
INSERT INTO `sys_region` VALUES ('442000002', '4420', '东区街道', '广东省,中山市,东区街道', 3);
INSERT INTO `sys_region` VALUES ('442000003', '4420', '火炬开发区街道', '广东省,中山市,火炬开发区街道', 3);
INSERT INTO `sys_region` VALUES ('442000004', '4420', '西区街道', '广东省,中山市,西区街道', 3);
INSERT INTO `sys_region` VALUES ('442000005', '4420', '南区街道', '广东省,中山市,南区街道', 3);
INSERT INTO `sys_region` VALUES ('442000006', '4420', '五桂山街道', '广东省,中山市,五桂山街道', 3);
INSERT INTO `sys_region` VALUES ('442000100', '4420', '小榄镇', '广东省,中山市,小榄镇', 3);
INSERT INTO `sys_region` VALUES ('442000101', '4420', '黄圃镇', '广东省,中山市,黄圃镇', 3);
INSERT INTO `sys_region` VALUES ('442000102', '4420', '民众镇', '广东省,中山市,民众镇', 3);
INSERT INTO `sys_region` VALUES ('442000103', '4420', '东凤镇', '广东省,中山市,东凤镇', 3);
INSERT INTO `sys_region` VALUES ('442000104', '4420', '东升镇', '广东省,中山市,东升镇', 3);
INSERT INTO `sys_region` VALUES ('442000105', '4420', '古镇镇', '广东省,中山市,古镇镇', 3);
INSERT INTO `sys_region` VALUES ('442000106', '4420', '沙溪镇', '广东省,中山市,沙溪镇', 3);
INSERT INTO `sys_region` VALUES ('442000107', '4420', '坦洲镇', '广东省,中山市,坦洲镇', 3);
INSERT INTO `sys_region` VALUES ('442000108', '4420', '港口镇', '广东省,中山市,港口镇', 3);
INSERT INTO `sys_region` VALUES ('442000109', '4420', '三角镇', '广东省,中山市,三角镇', 3);
INSERT INTO `sys_region` VALUES ('442000110', '4420', '横栏镇', '广东省,中山市,横栏镇', 3);
INSERT INTO `sys_region` VALUES ('442000111', '4420', '南头镇', '广东省,中山市,南头镇', 3);
INSERT INTO `sys_region` VALUES ('442000112', '4420', '阜沙镇', '广东省,中山市,阜沙镇', 3);
INSERT INTO `sys_region` VALUES ('442000113', '4420', '南朗镇', '广东省,中山市,南朗镇', 3);
INSERT INTO `sys_region` VALUES ('442000114', '4420', '三乡镇', '广东省,中山市,三乡镇', 3);
INSERT INTO `sys_region` VALUES ('442000115', '4420', '板芙镇', '广东省,中山市,板芙镇', 3);
INSERT INTO `sys_region` VALUES ('442000116', '4420', '大涌镇', '广东省,中山市,大涌镇', 3);
INSERT INTO `sys_region` VALUES ('442000117', '4420', '神湾镇', '广东省,中山市,神湾镇', 3);
INSERT INTO `sys_region` VALUES ('4451', '44', '潮州市', '广东省,潮州市', 2);
INSERT INTO `sys_region` VALUES ('445102', '4451', '湘桥区', '广东省,潮州市,湘桥区', 3);
INSERT INTO `sys_region` VALUES ('445103', '4451', '潮安区', '广东省,潮州市,潮安区', 3);
INSERT INTO `sys_region` VALUES ('445122', '4451', '饶平县', '广东省,潮州市,饶平县', 3);
INSERT INTO `sys_region` VALUES ('4452', '44', '揭阳市', '广东省,揭阳市', 2);
INSERT INTO `sys_region` VALUES ('445202', '4452', '榕城区', '广东省,揭阳市,榕城区', 3);
INSERT INTO `sys_region` VALUES ('445203', '4452', '揭东区', '广东省,揭阳市,揭东区', 3);
INSERT INTO `sys_region` VALUES ('445222', '4452', '揭西县', '广东省,揭阳市,揭西县', 3);
INSERT INTO `sys_region` VALUES ('445224', '4452', '惠来县', '广东省,揭阳市,惠来县', 3);
INSERT INTO `sys_region` VALUES ('445281', '4452', '普宁市', '广东省,揭阳市,普宁市', 3);
INSERT INTO `sys_region` VALUES ('4453', '44', '云浮市', '广东省,云浮市', 2);
INSERT INTO `sys_region` VALUES ('445302', '4453', '云城区', '广东省,云浮市,云城区', 3);
INSERT INTO `sys_region` VALUES ('445303', '4453', '云安区', '广东省,云浮市,云安区', 3);
INSERT INTO `sys_region` VALUES ('445321', '4453', '新兴县', '广东省,云浮市,新兴县', 3);
INSERT INTO `sys_region` VALUES ('445322', '4453', '郁南县', '广东省,云浮市,郁南县', 3);
INSERT INTO `sys_region` VALUES ('445381', '4453', '罗定市', '广东省,云浮市,罗定市', 3);
INSERT INTO `sys_region` VALUES ('45', '0', '广西壮族自治区', '广西壮族自治区', 1);
INSERT INTO `sys_region` VALUES ('4501', '45', '南宁市', '广西壮族自治区,南宁市', 2);
INSERT INTO `sys_region` VALUES ('450102', '4501', '兴宁区', '广西壮族自治区,南宁市,兴宁区', 3);
INSERT INTO `sys_region` VALUES ('450103', '4501', '青秀区', '广西壮族自治区,南宁市,青秀区', 3);
INSERT INTO `sys_region` VALUES ('450105', '4501', '江南区', '广西壮族自治区,南宁市,江南区', 3);
INSERT INTO `sys_region` VALUES ('450107', '4501', '西乡塘区', '广西壮族自治区,南宁市,西乡塘区', 3);
INSERT INTO `sys_region` VALUES ('450108', '4501', '良庆区', '广西壮族自治区,南宁市,良庆区', 3);
INSERT INTO `sys_region` VALUES ('450109', '4501', '邕宁区', '广西壮族自治区,南宁市,邕宁区', 3);
INSERT INTO `sys_region` VALUES ('450110', '4501', '武鸣区', '广西壮族自治区,南宁市,武鸣区', 3);
INSERT INTO `sys_region` VALUES ('450123', '4501', '隆安县', '广西壮族自治区,南宁市,隆安县', 3);
INSERT INTO `sys_region` VALUES ('450124', '4501', '马山县', '广西壮族自治区,南宁市,马山县', 3);
INSERT INTO `sys_region` VALUES ('450125', '4501', '上林县', '广西壮族自治区,南宁市,上林县', 3);
INSERT INTO `sys_region` VALUES ('450126', '4501', '宾阳县', '广西壮族自治区,南宁市,宾阳县', 3);
INSERT INTO `sys_region` VALUES ('450127', '4501', '横县', '广西壮族自治区,南宁市,横县', 3);
INSERT INTO `sys_region` VALUES ('4502', '45', '柳州市', '广西壮族自治区,柳州市', 2);
INSERT INTO `sys_region` VALUES ('450202', '4502', '城中区', '广西壮族自治区,柳州市,城中区', 3);
INSERT INTO `sys_region` VALUES ('450203', '4502', '鱼峰区', '广西壮族自治区,柳州市,鱼峰区', 3);
INSERT INTO `sys_region` VALUES ('450204', '4502', '柳南区', '广西壮族自治区,柳州市,柳南区', 3);
INSERT INTO `sys_region` VALUES ('450205', '4502', '柳北区', '广西壮族自治区,柳州市,柳北区', 3);
INSERT INTO `sys_region` VALUES ('450206', '4502', '柳江区', '广西壮族自治区,柳州市,柳江区', 3);
INSERT INTO `sys_region` VALUES ('450222', '4502', '柳城县', '广西壮族自治区,柳州市,柳城县', 3);
INSERT INTO `sys_region` VALUES ('450223', '4502', '鹿寨县', '广西壮族自治区,柳州市,鹿寨县', 3);
INSERT INTO `sys_region` VALUES ('450224', '4502', '融安县', '广西壮族自治区,柳州市,融安县', 3);
INSERT INTO `sys_region` VALUES ('450225', '4502', '融水苗族自治县', '广西壮族自治区,柳州市,融水苗族自治县', 3);
INSERT INTO `sys_region` VALUES ('450226', '4502', '三江侗族自治县', '广西壮族自治区,柳州市,三江侗族自治县', 3);
INSERT INTO `sys_region` VALUES ('4503', '45', '桂林市', '广西壮族自治区,桂林市', 2);
INSERT INTO `sys_region` VALUES ('450302', '4503', '秀峰区', '广西壮族自治区,桂林市,秀峰区', 3);
INSERT INTO `sys_region` VALUES ('450303', '4503', '叠彩区', '广西壮族自治区,桂林市,叠彩区', 3);
INSERT INTO `sys_region` VALUES ('450304', '4503', '象山区', '广西壮族自治区,桂林市,象山区', 3);
INSERT INTO `sys_region` VALUES ('450305', '4503', '七星区', '广西壮族自治区,桂林市,七星区', 3);
INSERT INTO `sys_region` VALUES ('450311', '4503', '雁山区', '广西壮族自治区,桂林市,雁山区', 3);
INSERT INTO `sys_region` VALUES ('450312', '4503', '临桂区', '广西壮族自治区,桂林市,临桂区', 3);
INSERT INTO `sys_region` VALUES ('450321', '4503', '阳朔县', '广西壮族自治区,桂林市,阳朔县', 3);
INSERT INTO `sys_region` VALUES ('450323', '4503', '灵川县', '广西壮族自治区,桂林市,灵川县', 3);
INSERT INTO `sys_region` VALUES ('450324', '4503', '全州县', '广西壮族自治区,桂林市,全州县', 3);
INSERT INTO `sys_region` VALUES ('450325', '4503', '兴安县', '广西壮族自治区,桂林市,兴安县', 3);
INSERT INTO `sys_region` VALUES ('450326', '4503', '永福县', '广西壮族自治区,桂林市,永福县', 3);
INSERT INTO `sys_region` VALUES ('450327', '4503', '灌阳县', '广西壮族自治区,桂林市,灌阳县', 3);
INSERT INTO `sys_region` VALUES ('450328', '4503', '龙胜各族自治县', '广西壮族自治区,桂林市,龙胜各族自治县', 3);
INSERT INTO `sys_region` VALUES ('450329', '4503', '资源县', '广西壮族自治区,桂林市,资源县', 3);
INSERT INTO `sys_region` VALUES ('450330', '4503', '平乐县', '广西壮族自治区,桂林市,平乐县', 3);
INSERT INTO `sys_region` VALUES ('450332', '4503', '恭城瑶族自治县', '广西壮族自治区,桂林市,恭城瑶族自治县', 3);
INSERT INTO `sys_region` VALUES ('450381', '4503', '荔浦市', '广西壮族自治区,桂林市,荔浦市', 3);
INSERT INTO `sys_region` VALUES ('4504', '45', '梧州市', '广西壮族自治区,梧州市', 2);
INSERT INTO `sys_region` VALUES ('450403', '4504', '万秀区', '广西壮族自治区,梧州市,万秀区', 3);
INSERT INTO `sys_region` VALUES ('450405', '4504', '长洲区', '广西壮族自治区,梧州市,长洲区', 3);
INSERT INTO `sys_region` VALUES ('450406', '4504', '龙圩区', '广西壮族自治区,梧州市,龙圩区', 3);
INSERT INTO `sys_region` VALUES ('450421', '4504', '苍梧县', '广西壮族自治区,梧州市,苍梧县', 3);
INSERT INTO `sys_region` VALUES ('450422', '4504', '藤县', '广西壮族自治区,梧州市,藤县', 3);
INSERT INTO `sys_region` VALUES ('450423', '4504', '蒙山县', '广西壮族自治区,梧州市,蒙山县', 3);
INSERT INTO `sys_region` VALUES ('450481', '4504', '岑溪市', '广西壮族自治区,梧州市,岑溪市', 3);
INSERT INTO `sys_region` VALUES ('4505', '45', '北海市', '广西壮族自治区,北海市', 2);
INSERT INTO `sys_region` VALUES ('450502', '4505', '海城区', '广西壮族自治区,北海市,海城区', 3);
INSERT INTO `sys_region` VALUES ('450503', '4505', '银海区', '广西壮族自治区,北海市,银海区', 3);
INSERT INTO `sys_region` VALUES ('450512', '4505', '铁山港区', '广西壮族自治区,北海市,铁山港区', 3);
INSERT INTO `sys_region` VALUES ('450521', '4505', '合浦县', '广西壮族自治区,北海市,合浦县', 3);
INSERT INTO `sys_region` VALUES ('4506', '45', '防城港市', '广西壮族自治区,防城港市', 2);
INSERT INTO `sys_region` VALUES ('450602', '4506', '港口区', '广西壮族自治区,防城港市,港口区', 3);
INSERT INTO `sys_region` VALUES ('450603', '4506', '防城区', '广西壮族自治区,防城港市,防城区', 3);
INSERT INTO `sys_region` VALUES ('450621', '4506', '上思县', '广西壮族自治区,防城港市,上思县', 3);
INSERT INTO `sys_region` VALUES ('450681', '4506', '东兴市', '广西壮族自治区,防城港市,东兴市', 3);
INSERT INTO `sys_region` VALUES ('4507', '45', '钦州市', '广西壮族自治区,钦州市', 2);
INSERT INTO `sys_region` VALUES ('450702', '4507', '钦南区', '广西壮族自治区,钦州市,钦南区', 3);
INSERT INTO `sys_region` VALUES ('450703', '4507', '钦北区', '广西壮族自治区,钦州市,钦北区', 3);
INSERT INTO `sys_region` VALUES ('450721', '4507', '灵山县', '广西壮族自治区,钦州市,灵山县', 3);
INSERT INTO `sys_region` VALUES ('450722', '4507', '浦北县', '广西壮族自治区,钦州市,浦北县', 3);
INSERT INTO `sys_region` VALUES ('4508', '45', '贵港市', '广西壮族自治区,贵港市', 2);
INSERT INTO `sys_region` VALUES ('450802', '4508', '港北区', '广西壮族自治区,贵港市,港北区', 3);
INSERT INTO `sys_region` VALUES ('450803', '4508', '港南区', '广西壮族自治区,贵港市,港南区', 3);
INSERT INTO `sys_region` VALUES ('450804', '4508', '覃塘区', '广西壮族自治区,贵港市,覃塘区', 3);
INSERT INTO `sys_region` VALUES ('450821', '4508', '平南县', '广西壮族自治区,贵港市,平南县', 3);
INSERT INTO `sys_region` VALUES ('450881', '4508', '桂平市', '广西壮族自治区,贵港市,桂平市', 3);
INSERT INTO `sys_region` VALUES ('4509', '45', '玉林市', '广西壮族自治区,玉林市', 2);
INSERT INTO `sys_region` VALUES ('450902', '4509', '玉州区', '广西壮族自治区,玉林市,玉州区', 3);
INSERT INTO `sys_region` VALUES ('450903', '4509', '福绵区', '广西壮族自治区,玉林市,福绵区', 3);
INSERT INTO `sys_region` VALUES ('450921', '4509', '容县', '广西壮族自治区,玉林市,容县', 3);
INSERT INTO `sys_region` VALUES ('450922', '4509', '陆川县', '广西壮族自治区,玉林市,陆川县', 3);
INSERT INTO `sys_region` VALUES ('450923', '4509', '博白县', '广西壮族自治区,玉林市,博白县', 3);
INSERT INTO `sys_region` VALUES ('450924', '4509', '兴业县', '广西壮族自治区,玉林市,兴业县', 3);
INSERT INTO `sys_region` VALUES ('450981', '4509', '北流市', '广西壮族自治区,玉林市,北流市', 3);
INSERT INTO `sys_region` VALUES ('4510', '45', '百色市', '广西壮族自治区,百色市', 2);
INSERT INTO `sys_region` VALUES ('451002', '4510', '右江区', '广西壮族自治区,百色市,右江区', 3);
INSERT INTO `sys_region` VALUES ('451021', '4510', '田阳县', '广西壮族自治区,百色市,田阳县', 3);
INSERT INTO `sys_region` VALUES ('451022', '4510', '田东县', '广西壮族自治区,百色市,田东县', 3);
INSERT INTO `sys_region` VALUES ('451023', '4510', '平果县', '广西壮族自治区,百色市,平果县', 3);
INSERT INTO `sys_region` VALUES ('451024', '4510', '德保县', '广西壮族自治区,百色市,德保县', 3);
INSERT INTO `sys_region` VALUES ('451026', '4510', '那坡县', '广西壮族自治区,百色市,那坡县', 3);
INSERT INTO `sys_region` VALUES ('451027', '4510', '凌云县', '广西壮族自治区,百色市,凌云县', 3);
INSERT INTO `sys_region` VALUES ('451028', '4510', '乐业县', '广西壮族自治区,百色市,乐业县', 3);
INSERT INTO `sys_region` VALUES ('451029', '4510', '田林县', '广西壮族自治区,百色市,田林县', 3);
INSERT INTO `sys_region` VALUES ('451030', '4510', '西林县', '广西壮族自治区,百色市,西林县', 3);
INSERT INTO `sys_region` VALUES ('451031', '4510', '隆林各族自治县', '广西壮族自治区,百色市,隆林各族自治县', 3);
INSERT INTO `sys_region` VALUES ('451081', '4510', '靖西市', '广西壮族自治区,百色市,靖西市', 3);
INSERT INTO `sys_region` VALUES ('4511', '45', '贺州市', '广西壮族自治区,贺州市', 2);
INSERT INTO `sys_region` VALUES ('451102', '4511', '八步区', '广西壮族自治区,贺州市,八步区', 3);
INSERT INTO `sys_region` VALUES ('451103', '4511', '平桂区', '广西壮族自治区,贺州市,平桂区', 3);
INSERT INTO `sys_region` VALUES ('451121', '4511', '昭平县', '广西壮族自治区,贺州市,昭平县', 3);
INSERT INTO `sys_region` VALUES ('451122', '4511', '钟山县', '广西壮族自治区,贺州市,钟山县', 3);
INSERT INTO `sys_region` VALUES ('451123', '4511', '富川瑶族自治县', '广西壮族自治区,贺州市,富川瑶族自治县', 3);
INSERT INTO `sys_region` VALUES ('4512', '45', '河池市', '广西壮族自治区,河池市', 2);
INSERT INTO `sys_region` VALUES ('451202', '4512', '金城江区', '广西壮族自治区,河池市,金城江区', 3);
INSERT INTO `sys_region` VALUES ('451203', '4512', '宜州区', '广西壮族自治区,河池市,宜州区', 3);
INSERT INTO `sys_region` VALUES ('451221', '4512', '南丹县', '广西壮族自治区,河池市,南丹县', 3);
INSERT INTO `sys_region` VALUES ('451222', '4512', '天峨县', '广西壮族自治区,河池市,天峨县', 3);
INSERT INTO `sys_region` VALUES ('451223', '4512', '凤山县', '广西壮族自治区,河池市,凤山县', 3);
INSERT INTO `sys_region` VALUES ('451224', '4512', '东兰县', '广西壮族自治区,河池市,东兰县', 3);
INSERT INTO `sys_region` VALUES ('451225', '4512', '罗城仫佬族自治县', '广西壮族自治区,河池市,罗城仫佬族自治县', 3);
INSERT INTO `sys_region` VALUES ('451226', '4512', '环江毛南族自治县', '广西壮族自治区,河池市,环江毛南族自治县', 3);
INSERT INTO `sys_region` VALUES ('451227', '4512', '巴马瑶族自治县', '广西壮族自治区,河池市,巴马瑶族自治县', 3);
INSERT INTO `sys_region` VALUES ('451228', '4512', '都安瑶族自治县', '广西壮族自治区,河池市,都安瑶族自治县', 3);
INSERT INTO `sys_region` VALUES ('451229', '4512', '大化瑶族自治县', '广西壮族自治区,河池市,大化瑶族自治县', 3);
INSERT INTO `sys_region` VALUES ('4513', '45', '来宾市', '广西壮族自治区,来宾市', 2);
INSERT INTO `sys_region` VALUES ('451302', '4513', '兴宾区', '广西壮族自治区,来宾市,兴宾区', 3);
INSERT INTO `sys_region` VALUES ('451321', '4513', '忻城县', '广西壮族自治区,来宾市,忻城县', 3);
INSERT INTO `sys_region` VALUES ('451322', '4513', '象州县', '广西壮族自治区,来宾市,象州县', 3);
INSERT INTO `sys_region` VALUES ('451323', '4513', '武宣县', '广西壮族自治区,来宾市,武宣县', 3);
INSERT INTO `sys_region` VALUES ('451324', '4513', '金秀瑶族自治县', '广西壮族自治区,来宾市,金秀瑶族自治县', 3);
INSERT INTO `sys_region` VALUES ('451381', '4513', '合山市', '广西壮族自治区,来宾市,合山市', 3);
INSERT INTO `sys_region` VALUES ('4514', '45', '崇左市', '广西壮族自治区,崇左市', 2);
INSERT INTO `sys_region` VALUES ('451402', '4514', '江州区', '广西壮族自治区,崇左市,江州区', 3);
INSERT INTO `sys_region` VALUES ('451421', '4514', '扶绥县', '广西壮族自治区,崇左市,扶绥县', 3);
INSERT INTO `sys_region` VALUES ('451422', '4514', '宁明县', '广西壮族自治区,崇左市,宁明县', 3);
INSERT INTO `sys_region` VALUES ('451423', '4514', '龙州县', '广西壮族自治区,崇左市,龙州县', 3);
INSERT INTO `sys_region` VALUES ('451424', '4514', '大新县', '广西壮族自治区,崇左市,大新县', 3);
INSERT INTO `sys_region` VALUES ('451425', '4514', '天等县', '广西壮族自治区,崇左市,天等县', 3);
INSERT INTO `sys_region` VALUES ('451481', '4514', '凭祥市', '广西壮族自治区,崇左市,凭祥市', 3);
INSERT INTO `sys_region` VALUES ('46', '0', '海南省', '海南省', 1);
INSERT INTO `sys_region` VALUES ('4601', '46', '海口市', '海南省,海口市', 2);
INSERT INTO `sys_region` VALUES ('460105', '4601', '秀英区', '海南省,海口市,秀英区', 3);
INSERT INTO `sys_region` VALUES ('460106', '4601', '龙华区', '海南省,海口市,龙华区', 3);
INSERT INTO `sys_region` VALUES ('460107', '4601', '琼山区', '海南省,海口市,琼山区', 3);
INSERT INTO `sys_region` VALUES ('460108', '4601', '美兰区', '海南省,海口市,美兰区', 3);
INSERT INTO `sys_region` VALUES ('4602', '46', '三亚市', '海南省,三亚市', 2);
INSERT INTO `sys_region` VALUES ('460202', '4602', '海棠区', '海南省,三亚市,海棠区', 3);
INSERT INTO `sys_region` VALUES ('460203', '4602', '吉阳区', '海南省,三亚市,吉阳区', 3);
INSERT INTO `sys_region` VALUES ('460204', '4602', '天涯区', '海南省,三亚市,天涯区', 3);
INSERT INTO `sys_region` VALUES ('460205', '4602', '崖州区', '海南省,三亚市,崖州区', 3);
INSERT INTO `sys_region` VALUES ('4603', '46', '三沙市', '海南省,三沙市', 2);
INSERT INTO `sys_region` VALUES ('460321', '4603', '西沙群岛', '海南省,三沙市,西沙群岛', 3);
INSERT INTO `sys_region` VALUES ('460322', '4603', '南沙群岛', '海南省,三沙市,南沙群岛', 3);
INSERT INTO `sys_region` VALUES ('460323', '4603', '中沙群岛的岛礁及其海域', '海南省,三沙市,中沙群岛的岛礁及其海域', 3);
INSERT INTO `sys_region` VALUES ('4604', '46', '儋州市', '海南省,儋州市', 2);
INSERT INTO `sys_region` VALUES ('460400100', '4604', '那大镇', '海南省,儋州市,那大镇', 3);
INSERT INTO `sys_region` VALUES ('460400101', '4604', '和庆镇', '海南省,儋州市,和庆镇', 3);
INSERT INTO `sys_region` VALUES ('460400102', '4604', '南丰镇', '海南省,儋州市,南丰镇', 3);
INSERT INTO `sys_region` VALUES ('460400103', '4604', '大成镇', '海南省,儋州市,大成镇', 3);
INSERT INTO `sys_region` VALUES ('460400104', '4604', '雅星镇', '海南省,儋州市,雅星镇', 3);
INSERT INTO `sys_region` VALUES ('460400105', '4604', '兰洋镇', '海南省,儋州市,兰洋镇', 3);
INSERT INTO `sys_region` VALUES ('460400106', '4604', '光村镇', '海南省,儋州市,光村镇', 3);
INSERT INTO `sys_region` VALUES ('460400107', '4604', '木棠镇', '海南省,儋州市,木棠镇', 3);
INSERT INTO `sys_region` VALUES ('460400108', '4604', '海头镇', '海南省,儋州市,海头镇', 3);
INSERT INTO `sys_region` VALUES ('460400109', '4604', '峨蔓镇', '海南省,儋州市,峨蔓镇', 3);
INSERT INTO `sys_region` VALUES ('460400111', '4604', '王五镇', '海南省,儋州市,王五镇', 3);
INSERT INTO `sys_region` VALUES ('460400112', '4604', '白马井镇', '海南省,儋州市,白马井镇', 3);
INSERT INTO `sys_region` VALUES ('460400113', '4604', '中和镇', '海南省,儋州市,中和镇', 3);
INSERT INTO `sys_region` VALUES ('460400114', '4604', '排浦镇', '海南省,儋州市,排浦镇', 3);
INSERT INTO `sys_region` VALUES ('460400115', '4604', '东成镇', '海南省,儋州市,东成镇', 3);
INSERT INTO `sys_region` VALUES ('460400116', '4604', '新州镇', '海南省,儋州市,新州镇', 3);
INSERT INTO `sys_region` VALUES ('460400499', '4604', '洋浦经济开发区', '海南省,儋州市,洋浦经济开发区', 3);
INSERT INTO `sys_region` VALUES ('460400500', '4604', '华南热作学院', '海南省,儋州市,华南热作学院', 3);
INSERT INTO `sys_region` VALUES ('4690', '46', '省直辖县级行政区划', '海南省,省直辖县级行政区划', 2);
INSERT INTO `sys_region` VALUES ('469001', '4690', '五指山市', '海南省,省直辖县级行政区划,五指山市', 3);
INSERT INTO `sys_region` VALUES ('469002', '4690', '琼海市', '海南省,省直辖县级行政区划,琼海市', 3);
INSERT INTO `sys_region` VALUES ('469005', '4690', '文昌市', '海南省,省直辖县级行政区划,文昌市', 3);
INSERT INTO `sys_region` VALUES ('469006', '4690', '万宁市', '海南省,省直辖县级行政区划,万宁市', 3);
INSERT INTO `sys_region` VALUES ('469007', '4690', '东方市', '海南省,省直辖县级行政区划,东方市', 3);
INSERT INTO `sys_region` VALUES ('469021', '4690', '定安县', '海南省,省直辖县级行政区划,定安县', 3);
INSERT INTO `sys_region` VALUES ('469022', '4690', '屯昌县', '海南省,省直辖县级行政区划,屯昌县', 3);
INSERT INTO `sys_region` VALUES ('469023', '4690', '澄迈县', '海南省,省直辖县级行政区划,澄迈县', 3);
INSERT INTO `sys_region` VALUES ('469024', '4690', '临高县', '海南省,省直辖县级行政区划,临高县', 3);
INSERT INTO `sys_region` VALUES ('469025', '4690', '白沙黎族自治县', '海南省,省直辖县级行政区划,白沙黎族自治县', 3);
INSERT INTO `sys_region` VALUES ('469026', '4690', '昌江黎族自治县', '海南省,省直辖县级行政区划,昌江黎族自治县', 3);
INSERT INTO `sys_region` VALUES ('469027', '4690', '乐东黎族自治县', '海南省,省直辖县级行政区划,乐东黎族自治县', 3);
INSERT INTO `sys_region` VALUES ('469028', '4690', '陵水黎族自治县', '海南省,省直辖县级行政区划,陵水黎族自治县', 3);
INSERT INTO `sys_region` VALUES ('469029', '4690', '保亭黎族苗族自治县', '海南省,省直辖县级行政区划,保亭黎族苗族自治县', 3);
INSERT INTO `sys_region` VALUES ('469030', '4690', '琼中黎族苗族自治县', '海南省,省直辖县级行政区划,琼中黎族苗族自治县', 3);
INSERT INTO `sys_region` VALUES ('50', '0', '重庆市', '重庆市', 1);
INSERT INTO `sys_region` VALUES ('500101', '50', '万州区', '重庆市,万州区', 2);
INSERT INTO `sys_region` VALUES ('500102', '50', '涪陵区', '重庆市,涪陵区', 2);
INSERT INTO `sys_region` VALUES ('500103', '50', '渝中区', '重庆市,渝中区', 2);
INSERT INTO `sys_region` VALUES ('500104', '50', '大渡口区', '重庆市,大渡口区', 2);
INSERT INTO `sys_region` VALUES ('500105', '50', '江北区', '重庆市,江北区', 2);
INSERT INTO `sys_region` VALUES ('500106', '50', '沙坪坝区', '重庆市,沙坪坝区', 2);
INSERT INTO `sys_region` VALUES ('500107', '50', '九龙坡区', '重庆市,九龙坡区', 2);
INSERT INTO `sys_region` VALUES ('500108', '50', '南岸区', '重庆市,南岸区', 2);
INSERT INTO `sys_region` VALUES ('500109', '50', '北碚区', '重庆市,北碚区', 2);
INSERT INTO `sys_region` VALUES ('500110', '50', '綦江区', '重庆市,綦江区', 2);
INSERT INTO `sys_region` VALUES ('500111', '50', '大足区', '重庆市,大足区', 2);
INSERT INTO `sys_region` VALUES ('500112', '50', '渝北区', '重庆市,渝北区', 2);
INSERT INTO `sys_region` VALUES ('500113', '50', '巴南区', '重庆市,巴南区', 2);
INSERT INTO `sys_region` VALUES ('500114', '50', '黔江区', '重庆市,黔江区', 2);
INSERT INTO `sys_region` VALUES ('500115', '50', '长寿区', '重庆市,长寿区', 2);
INSERT INTO `sys_region` VALUES ('500116', '50', '江津区', '重庆市,江津区', 2);
INSERT INTO `sys_region` VALUES ('500117', '50', '合川区', '重庆市,合川区', 2);
INSERT INTO `sys_region` VALUES ('500118', '50', '永川区', '重庆市,永川区', 2);
INSERT INTO `sys_region` VALUES ('500119', '50', '南川区', '重庆市,南川区', 2);
INSERT INTO `sys_region` VALUES ('500120', '50', '璧山区', '重庆市,璧山区', 2);
INSERT INTO `sys_region` VALUES ('500151', '50', '铜梁区', '重庆市,铜梁区', 2);
INSERT INTO `sys_region` VALUES ('500152', '50', '潼南区', '重庆市,潼南区', 2);
INSERT INTO `sys_region` VALUES ('500153', '50', '荣昌区', '重庆市,荣昌区', 2);
INSERT INTO `sys_region` VALUES ('500154', '50', '开州区', '重庆市,开州区', 2);
INSERT INTO `sys_region` VALUES ('500155', '50', '梁平区', '重庆市,梁平区', 2);
INSERT INTO `sys_region` VALUES ('500156', '50', '武隆区', '重庆市,武隆区', 2);
INSERT INTO `sys_region` VALUES ('51', '0', '四川省', '四川省', 1);
INSERT INTO `sys_region` VALUES ('5101', '51', '成都市', '四川省,成都市', 2);
INSERT INTO `sys_region` VALUES ('510104', '5101', '锦江区', '四川省,成都市,锦江区', 3);
INSERT INTO `sys_region` VALUES ('510105', '5101', '青羊区', '四川省,成都市,青羊区', 3);
INSERT INTO `sys_region` VALUES ('510106', '5101', '金牛区', '四川省,成都市,金牛区', 3);
INSERT INTO `sys_region` VALUES ('510107', '5101', '武侯区', '四川省,成都市,武侯区', 3);
INSERT INTO `sys_region` VALUES ('510108', '5101', '成华区', '四川省,成都市,成华区', 3);
INSERT INTO `sys_region` VALUES ('510112', '5101', '龙泉驿区', '四川省,成都市,龙泉驿区', 3);
INSERT INTO `sys_region` VALUES ('510113', '5101', '青白江区', '四川省,成都市,青白江区', 3);
INSERT INTO `sys_region` VALUES ('510114', '5101', '新都区', '四川省,成都市,新都区', 3);
INSERT INTO `sys_region` VALUES ('510115', '5101', '温江区', '四川省,成都市,温江区', 3);
INSERT INTO `sys_region` VALUES ('510116', '5101', '双流区', '四川省,成都市,双流区', 3);
INSERT INTO `sys_region` VALUES ('510117', '5101', '郫都区', '四川省,成都市,郫都区', 3);
INSERT INTO `sys_region` VALUES ('510121', '5101', '金堂县', '四川省,成都市,金堂县', 3);
INSERT INTO `sys_region` VALUES ('510129', '5101', '大邑县', '四川省,成都市,大邑县', 3);
INSERT INTO `sys_region` VALUES ('510131', '5101', '蒲江县', '四川省,成都市,蒲江县', 3);
INSERT INTO `sys_region` VALUES ('510132', '5101', '新津县', '四川省,成都市,新津县', 3);
INSERT INTO `sys_region` VALUES ('510181', '5101', '都江堰市', '四川省,成都市,都江堰市', 3);
INSERT INTO `sys_region` VALUES ('510182', '5101', '彭州市', '四川省,成都市,彭州市', 3);
INSERT INTO `sys_region` VALUES ('510183', '5101', '邛崃市', '四川省,成都市,邛崃市', 3);
INSERT INTO `sys_region` VALUES ('510184', '5101', '崇州市', '四川省,成都市,崇州市', 3);
INSERT INTO `sys_region` VALUES ('510185', '5101', '简阳市', '四川省,成都市,简阳市', 3);
INSERT INTO `sys_region` VALUES ('5103', '51', '自贡市', '四川省,自贡市', 2);
INSERT INTO `sys_region` VALUES ('510302', '5103', '自流井区', '四川省,自贡市,自流井区', 3);
INSERT INTO `sys_region` VALUES ('510303', '5103', '贡井区', '四川省,自贡市,贡井区', 3);
INSERT INTO `sys_region` VALUES ('510304', '5103', '大安区', '四川省,自贡市,大安区', 3);
INSERT INTO `sys_region` VALUES ('510311', '5103', '沿滩区', '四川省,自贡市,沿滩区', 3);
INSERT INTO `sys_region` VALUES ('510321', '5103', '荣县', '四川省,自贡市,荣县', 3);
INSERT INTO `sys_region` VALUES ('510322', '5103', '富顺县', '四川省,自贡市,富顺县', 3);
INSERT INTO `sys_region` VALUES ('5104', '51', '攀枝花市', '四川省,攀枝花市', 2);
INSERT INTO `sys_region` VALUES ('510402', '5104', '东区', '四川省,攀枝花市,东区', 3);
INSERT INTO `sys_region` VALUES ('510403', '5104', '西区', '四川省,攀枝花市,西区', 3);
INSERT INTO `sys_region` VALUES ('510411', '5104', '仁和区', '四川省,攀枝花市,仁和区', 3);
INSERT INTO `sys_region` VALUES ('510421', '5104', '米易县', '四川省,攀枝花市,米易县', 3);
INSERT INTO `sys_region` VALUES ('510422', '5104', '盐边县', '四川省,攀枝花市,盐边县', 3);
INSERT INTO `sys_region` VALUES ('5105', '51', '泸州市', '四川省,泸州市', 2);
INSERT INTO `sys_region` VALUES ('510502', '5105', '江阳区', '四川省,泸州市,江阳区', 3);
INSERT INTO `sys_region` VALUES ('510503', '5105', '纳溪区', '四川省,泸州市,纳溪区', 3);
INSERT INTO `sys_region` VALUES ('510504', '5105', '龙马潭区', '四川省,泸州市,龙马潭区', 3);
INSERT INTO `sys_region` VALUES ('510521', '5105', '泸县', '四川省,泸州市,泸县', 3);
INSERT INTO `sys_region` VALUES ('510522', '5105', '合江县', '四川省,泸州市,合江县', 3);
INSERT INTO `sys_region` VALUES ('510524', '5105', '叙永县', '四川省,泸州市,叙永县', 3);
INSERT INTO `sys_region` VALUES ('510525', '5105', '古蔺县', '四川省,泸州市,古蔺县', 3);
INSERT INTO `sys_region` VALUES ('5106', '51', '德阳市', '四川省,德阳市', 2);
INSERT INTO `sys_region` VALUES ('510603', '5106', '旌阳区', '四川省,德阳市,旌阳区', 3);
INSERT INTO `sys_region` VALUES ('510604', '5106', '罗江区', '四川省,德阳市,罗江区', 3);
INSERT INTO `sys_region` VALUES ('510623', '5106', '中江县', '四川省,德阳市,中江县', 3);
INSERT INTO `sys_region` VALUES ('510681', '5106', '广汉市', '四川省,德阳市,广汉市', 3);
INSERT INTO `sys_region` VALUES ('510682', '5106', '什邡市', '四川省,德阳市,什邡市', 3);
INSERT INTO `sys_region` VALUES ('510683', '5106', '绵竹市', '四川省,德阳市,绵竹市', 3);
INSERT INTO `sys_region` VALUES ('5107', '51', '绵阳市', '四川省,绵阳市', 2);
INSERT INTO `sys_region` VALUES ('510703', '5107', '涪城区', '四川省,绵阳市,涪城区', 3);
INSERT INTO `sys_region` VALUES ('510704', '5107', '游仙区', '四川省,绵阳市,游仙区', 3);
INSERT INTO `sys_region` VALUES ('510705', '5107', '安州区', '四川省,绵阳市,安州区', 3);
INSERT INTO `sys_region` VALUES ('510722', '5107', '三台县', '四川省,绵阳市,三台县', 3);
INSERT INTO `sys_region` VALUES ('510723', '5107', '盐亭县', '四川省,绵阳市,盐亭县', 3);
INSERT INTO `sys_region` VALUES ('510725', '5107', '梓潼县', '四川省,绵阳市,梓潼县', 3);
INSERT INTO `sys_region` VALUES ('510726', '5107', '北川羌族自治县', '四川省,绵阳市,北川羌族自治县', 3);
INSERT INTO `sys_region` VALUES ('510727', '5107', '平武县', '四川省,绵阳市,平武县', 3);
INSERT INTO `sys_region` VALUES ('510781', '5107', '江油市', '四川省,绵阳市,江油市', 3);
INSERT INTO `sys_region` VALUES ('5108', '51', '广元市', '四川省,广元市', 2);
INSERT INTO `sys_region` VALUES ('510802', '5108', '利州区', '四川省,广元市,利州区', 3);
INSERT INTO `sys_region` VALUES ('510811', '5108', '昭化区', '四川省,广元市,昭化区', 3);
INSERT INTO `sys_region` VALUES ('510812', '5108', '朝天区', '四川省,广元市,朝天区', 3);
INSERT INTO `sys_region` VALUES ('510821', '5108', '旺苍县', '四川省,广元市,旺苍县', 3);
INSERT INTO `sys_region` VALUES ('510822', '5108', '青川县', '四川省,广元市,青川县', 3);
INSERT INTO `sys_region` VALUES ('510823', '5108', '剑阁县', '四川省,广元市,剑阁县', 3);
INSERT INTO `sys_region` VALUES ('510824', '5108', '苍溪县', '四川省,广元市,苍溪县', 3);
INSERT INTO `sys_region` VALUES ('5109', '51', '遂宁市', '四川省,遂宁市', 2);
INSERT INTO `sys_region` VALUES ('510903', '5109', '船山区', '四川省,遂宁市,船山区', 3);
INSERT INTO `sys_region` VALUES ('510904', '5109', '安居区', '四川省,遂宁市,安居区', 3);
INSERT INTO `sys_region` VALUES ('510921', '5109', '蓬溪县', '四川省,遂宁市,蓬溪县', 3);
INSERT INTO `sys_region` VALUES ('510922', '5109', '射洪县', '四川省,遂宁市,射洪县', 3);
INSERT INTO `sys_region` VALUES ('510923', '5109', '大英县', '四川省,遂宁市,大英县', 3);
INSERT INTO `sys_region` VALUES ('5110', '51', '内江市', '四川省,内江市', 2);
INSERT INTO `sys_region` VALUES ('511002', '5110', '市中区', '四川省,内江市,市中区', 3);
INSERT INTO `sys_region` VALUES ('511011', '5110', '东兴区', '四川省,内江市,东兴区', 3);
INSERT INTO `sys_region` VALUES ('511024', '5110', '威远县', '四川省,内江市,威远县', 3);
INSERT INTO `sys_region` VALUES ('511025', '5110', '资中县', '四川省,内江市,资中县', 3);
INSERT INTO `sys_region` VALUES ('511071', '5110', '内江经济开发区', '四川省,内江市,内江经济开发区', 3);
INSERT INTO `sys_region` VALUES ('511083', '5110', '隆昌市', '四川省,内江市,隆昌市', 3);
INSERT INTO `sys_region` VALUES ('5111', '51', '乐山市', '四川省,乐山市', 2);
INSERT INTO `sys_region` VALUES ('511102', '5111', '市中区', '四川省,乐山市,市中区', 3);
INSERT INTO `sys_region` VALUES ('511111', '5111', '沙湾区', '四川省,乐山市,沙湾区', 3);
INSERT INTO `sys_region` VALUES ('511112', '5111', '五通桥区', '四川省,乐山市,五通桥区', 3);
INSERT INTO `sys_region` VALUES ('511113', '5111', '金口河区', '四川省,乐山市,金口河区', 3);
INSERT INTO `sys_region` VALUES ('511123', '5111', '犍为县', '四川省,乐山市,犍为县', 3);
INSERT INTO `sys_region` VALUES ('511124', '5111', '井研县', '四川省,乐山市,井研县', 3);
INSERT INTO `sys_region` VALUES ('511126', '5111', '夹江县', '四川省,乐山市,夹江县', 3);
INSERT INTO `sys_region` VALUES ('511129', '5111', '沐川县', '四川省,乐山市,沐川县', 3);
INSERT INTO `sys_region` VALUES ('511132', '5111', '峨边彝族自治县', '四川省,乐山市,峨边彝族自治县', 3);
INSERT INTO `sys_region` VALUES ('511133', '5111', '马边彝族自治县', '四川省,乐山市,马边彝族自治县', 3);
INSERT INTO `sys_region` VALUES ('511181', '5111', '峨眉山市', '四川省,乐山市,峨眉山市', 3);
INSERT INTO `sys_region` VALUES ('5113', '51', '南充市', '四川省,南充市', 2);
INSERT INTO `sys_region` VALUES ('511302', '5113', '顺庆区', '四川省,南充市,顺庆区', 3);
INSERT INTO `sys_region` VALUES ('511303', '5113', '高坪区', '四川省,南充市,高坪区', 3);
INSERT INTO `sys_region` VALUES ('511304', '5113', '嘉陵区', '四川省,南充市,嘉陵区', 3);
INSERT INTO `sys_region` VALUES ('511321', '5113', '南部县', '四川省,南充市,南部县', 3);
INSERT INTO `sys_region` VALUES ('511322', '5113', '营山县', '四川省,南充市,营山县', 3);
INSERT INTO `sys_region` VALUES ('511323', '5113', '蓬安县', '四川省,南充市,蓬安县', 3);
INSERT INTO `sys_region` VALUES ('511324', '5113', '仪陇县', '四川省,南充市,仪陇县', 3);
INSERT INTO `sys_region` VALUES ('511325', '5113', '西充县', '四川省,南充市,西充县', 3);
INSERT INTO `sys_region` VALUES ('511381', '5113', '阆中市', '四川省,南充市,阆中市', 3);
INSERT INTO `sys_region` VALUES ('5114', '51', '眉山市', '四川省,眉山市', 2);
INSERT INTO `sys_region` VALUES ('511402', '5114', '东坡区', '四川省,眉山市,东坡区', 3);
INSERT INTO `sys_region` VALUES ('511403', '5114', '彭山区', '四川省,眉山市,彭山区', 3);
INSERT INTO `sys_region` VALUES ('511421', '5114', '仁寿县', '四川省,眉山市,仁寿县', 3);
INSERT INTO `sys_region` VALUES ('511423', '5114', '洪雅县', '四川省,眉山市,洪雅县', 3);
INSERT INTO `sys_region` VALUES ('511424', '5114', '丹棱县', '四川省,眉山市,丹棱县', 3);
INSERT INTO `sys_region` VALUES ('511425', '5114', '青神县', '四川省,眉山市,青神县', 3);
INSERT INTO `sys_region` VALUES ('5115', '51', '宜宾市', '四川省,宜宾市', 2);
INSERT INTO `sys_region` VALUES ('511502', '5115', '翠屏区', '四川省,宜宾市,翠屏区', 3);
INSERT INTO `sys_region` VALUES ('511503', '5115', '南溪区', '四川省,宜宾市,南溪区', 3);
INSERT INTO `sys_region` VALUES ('511504', '5115', '叙州区', '四川省,宜宾市,叙州区', 3);
INSERT INTO `sys_region` VALUES ('511523', '5115', '江安县', '四川省,宜宾市,江安县', 3);
INSERT INTO `sys_region` VALUES ('511524', '5115', '长宁县', '四川省,宜宾市,长宁县', 3);
INSERT INTO `sys_region` VALUES ('511525', '5115', '高县', '四川省,宜宾市,高县', 3);
INSERT INTO `sys_region` VALUES ('511526', '5115', '珙县', '四川省,宜宾市,珙县', 3);
INSERT INTO `sys_region` VALUES ('511527', '5115', '筠连县', '四川省,宜宾市,筠连县', 3);
INSERT INTO `sys_region` VALUES ('511528', '5115', '兴文县', '四川省,宜宾市,兴文县', 3);
INSERT INTO `sys_region` VALUES ('511529', '5115', '屏山县', '四川省,宜宾市,屏山县', 3);
INSERT INTO `sys_region` VALUES ('5116', '51', '广安市', '四川省,广安市', 2);
INSERT INTO `sys_region` VALUES ('511602', '5116', '广安区', '四川省,广安市,广安区', 3);
INSERT INTO `sys_region` VALUES ('511603', '5116', '前锋区', '四川省,广安市,前锋区', 3);
INSERT INTO `sys_region` VALUES ('511621', '5116', '岳池县', '四川省,广安市,岳池县', 3);
INSERT INTO `sys_region` VALUES ('511622', '5116', '武胜县', '四川省,广安市,武胜县', 3);
INSERT INTO `sys_region` VALUES ('511623', '5116', '邻水县', '四川省,广安市,邻水县', 3);
INSERT INTO `sys_region` VALUES ('511681', '5116', '华蓥市', '四川省,广安市,华蓥市', 3);
INSERT INTO `sys_region` VALUES ('5117', '51', '达州市', '四川省,达州市', 2);
INSERT INTO `sys_region` VALUES ('511702', '5117', '通川区', '四川省,达州市,通川区', 3);
INSERT INTO `sys_region` VALUES ('511703', '5117', '达川区', '四川省,达州市,达川区', 3);
INSERT INTO `sys_region` VALUES ('511722', '5117', '宣汉县', '四川省,达州市,宣汉县', 3);
INSERT INTO `sys_region` VALUES ('511723', '5117', '开江县', '四川省,达州市,开江县', 3);
INSERT INTO `sys_region` VALUES ('511724', '5117', '大竹县', '四川省,达州市,大竹县', 3);
INSERT INTO `sys_region` VALUES ('511725', '5117', '渠县', '四川省,达州市,渠县', 3);
INSERT INTO `sys_region` VALUES ('511771', '5117', '达州经济开发区', '四川省,达州市,达州经济开发区', 3);
INSERT INTO `sys_region` VALUES ('511781', '5117', '万源市', '四川省,达州市,万源市', 3);
INSERT INTO `sys_region` VALUES ('5118', '51', '雅安市', '四川省,雅安市', 2);
INSERT INTO `sys_region` VALUES ('511802', '5118', '雨城区', '四川省,雅安市,雨城区', 3);
INSERT INTO `sys_region` VALUES ('511803', '5118', '名山区', '四川省,雅安市,名山区', 3);
INSERT INTO `sys_region` VALUES ('511822', '5118', '荥经县', '四川省,雅安市,荥经县', 3);
INSERT INTO `sys_region` VALUES ('511823', '5118', '汉源县', '四川省,雅安市,汉源县', 3);
INSERT INTO `sys_region` VALUES ('511824', '5118', '石棉县', '四川省,雅安市,石棉县', 3);
INSERT INTO `sys_region` VALUES ('511825', '5118', '天全县', '四川省,雅安市,天全县', 3);
INSERT INTO `sys_region` VALUES ('511826', '5118', '芦山县', '四川省,雅安市,芦山县', 3);
INSERT INTO `sys_region` VALUES ('511827', '5118', '宝兴县', '四川省,雅安市,宝兴县', 3);
INSERT INTO `sys_region` VALUES ('5119', '51', '巴中市', '四川省,巴中市', 2);
INSERT INTO `sys_region` VALUES ('511902', '5119', '巴州区', '四川省,巴中市,巴州区', 3);
INSERT INTO `sys_region` VALUES ('511903', '5119', '恩阳区', '四川省,巴中市,恩阳区', 3);
INSERT INTO `sys_region` VALUES ('511921', '5119', '通江县', '四川省,巴中市,通江县', 3);
INSERT INTO `sys_region` VALUES ('511922', '5119', '南江县', '四川省,巴中市,南江县', 3);
INSERT INTO `sys_region` VALUES ('511923', '5119', '平昌县', '四川省,巴中市,平昌县', 3);
INSERT INTO `sys_region` VALUES ('511971', '5119', '巴中经济开发区', '四川省,巴中市,巴中经济开发区', 3);
INSERT INTO `sys_region` VALUES ('5120', '51', '资阳市', '四川省,资阳市', 2);
INSERT INTO `sys_region` VALUES ('512002', '5120', '雁江区', '四川省,资阳市,雁江区', 3);
INSERT INTO `sys_region` VALUES ('512021', '5120', '安岳县', '四川省,资阳市,安岳县', 3);
INSERT INTO `sys_region` VALUES ('512022', '5120', '乐至县', '四川省,资阳市,乐至县', 3);
INSERT INTO `sys_region` VALUES ('5132', '51', '阿坝藏族羌族自治州', '四川省,阿坝藏族羌族自治州', 2);
INSERT INTO `sys_region` VALUES ('513201', '5132', '马尔康市', '四川省,阿坝藏族羌族自治州,马尔康市', 3);
INSERT INTO `sys_region` VALUES ('513221', '5132', '汶川县', '四川省,阿坝藏族羌族自治州,汶川县', 3);
INSERT INTO `sys_region` VALUES ('513222', '5132', '理县', '四川省,阿坝藏族羌族自治州,理县', 3);
INSERT INTO `sys_region` VALUES ('513223', '5132', '茂县', '四川省,阿坝藏族羌族自治州,茂县', 3);
INSERT INTO `sys_region` VALUES ('513224', '5132', '松潘县', '四川省,阿坝藏族羌族自治州,松潘县', 3);
INSERT INTO `sys_region` VALUES ('513225', '5132', '九寨沟县', '四川省,阿坝藏族羌族自治州,九寨沟县', 3);
INSERT INTO `sys_region` VALUES ('513226', '5132', '金川县', '四川省,阿坝藏族羌族自治州,金川县', 3);
INSERT INTO `sys_region` VALUES ('513227', '5132', '小金县', '四川省,阿坝藏族羌族自治州,小金县', 3);
INSERT INTO `sys_region` VALUES ('513228', '5132', '黑水县', '四川省,阿坝藏族羌族自治州,黑水县', 3);
INSERT INTO `sys_region` VALUES ('513230', '5132', '壤塘县', '四川省,阿坝藏族羌族自治州,壤塘县', 3);
INSERT INTO `sys_region` VALUES ('513231', '5132', '阿坝县', '四川省,阿坝藏族羌族自治州,阿坝县', 3);
INSERT INTO `sys_region` VALUES ('513232', '5132', '若尔盖县', '四川省,阿坝藏族羌族自治州,若尔盖县', 3);
INSERT INTO `sys_region` VALUES ('513233', '5132', '红原县', '四川省,阿坝藏族羌族自治州,红原县', 3);
INSERT INTO `sys_region` VALUES ('5133', '51', '甘孜藏族自治州', '四川省,甘孜藏族自治州', 2);
INSERT INTO `sys_region` VALUES ('513301', '5133', '康定市', '四川省,甘孜藏族自治州,康定市', 3);
INSERT INTO `sys_region` VALUES ('513322', '5133', '泸定县', '四川省,甘孜藏族自治州,泸定县', 3);
INSERT INTO `sys_region` VALUES ('513323', '5133', '丹巴县', '四川省,甘孜藏族自治州,丹巴县', 3);
INSERT INTO `sys_region` VALUES ('513324', '5133', '九龙县', '四川省,甘孜藏族自治州,九龙县', 3);
INSERT INTO `sys_region` VALUES ('513325', '5133', '雅江县', '四川省,甘孜藏族自治州,雅江县', 3);
INSERT INTO `sys_region` VALUES ('513326', '5133', '道孚县', '四川省,甘孜藏族自治州,道孚县', 3);
INSERT INTO `sys_region` VALUES ('513327', '5133', '炉霍县', '四川省,甘孜藏族自治州,炉霍县', 3);
INSERT INTO `sys_region` VALUES ('513328', '5133', '甘孜县', '四川省,甘孜藏族自治州,甘孜县', 3);
INSERT INTO `sys_region` VALUES ('513329', '5133', '新龙县', '四川省,甘孜藏族自治州,新龙县', 3);
INSERT INTO `sys_region` VALUES ('513330', '5133', '德格县', '四川省,甘孜藏族自治州,德格县', 3);
INSERT INTO `sys_region` VALUES ('513331', '5133', '白玉县', '四川省,甘孜藏族自治州,白玉县', 3);
INSERT INTO `sys_region` VALUES ('513332', '5133', '石渠县', '四川省,甘孜藏族自治州,石渠县', 3);
INSERT INTO `sys_region` VALUES ('513333', '5133', '色达县', '四川省,甘孜藏族自治州,色达县', 3);
INSERT INTO `sys_region` VALUES ('513334', '5133', '理塘县', '四川省,甘孜藏族自治州,理塘县', 3);
INSERT INTO `sys_region` VALUES ('513335', '5133', '巴塘县', '四川省,甘孜藏族自治州,巴塘县', 3);
INSERT INTO `sys_region` VALUES ('513336', '5133', '乡城县', '四川省,甘孜藏族自治州,乡城县', 3);
INSERT INTO `sys_region` VALUES ('513337', '5133', '稻城县', '四川省,甘孜藏族自治州,稻城县', 3);
INSERT INTO `sys_region` VALUES ('513338', '5133', '得荣县', '四川省,甘孜藏族自治州,得荣县', 3);
INSERT INTO `sys_region` VALUES ('5134', '51', '凉山彝族自治州', '四川省,凉山彝族自治州', 2);
INSERT INTO `sys_region` VALUES ('513401', '5134', '西昌市', '四川省,凉山彝族自治州,西昌市', 3);
INSERT INTO `sys_region` VALUES ('513422', '5134', '木里藏族自治县', '四川省,凉山彝族自治州,木里藏族自治县', 3);
INSERT INTO `sys_region` VALUES ('513423', '5134', '盐源县', '四川省,凉山彝族自治州,盐源县', 3);
INSERT INTO `sys_region` VALUES ('513424', '5134', '德昌县', '四川省,凉山彝族自治州,德昌县', 3);
INSERT INTO `sys_region` VALUES ('513425', '5134', '会理县', '四川省,凉山彝族自治州,会理县', 3);
INSERT INTO `sys_region` VALUES ('513426', '5134', '会东县', '四川省,凉山彝族自治州,会东县', 3);
INSERT INTO `sys_region` VALUES ('513427', '5134', '宁南县', '四川省,凉山彝族自治州,宁南县', 3);
INSERT INTO `sys_region` VALUES ('513428', '5134', '普格县', '四川省,凉山彝族自治州,普格县', 3);
INSERT INTO `sys_region` VALUES ('513429', '5134', '布拖县', '四川省,凉山彝族自治州,布拖县', 3);
INSERT INTO `sys_region` VALUES ('513430', '5134', '金阳县', '四川省,凉山彝族自治州,金阳县', 3);
INSERT INTO `sys_region` VALUES ('513431', '5134', '昭觉县', '四川省,凉山彝族自治州,昭觉县', 3);
INSERT INTO `sys_region` VALUES ('513432', '5134', '喜德县', '四川省,凉山彝族自治州,喜德县', 3);
INSERT INTO `sys_region` VALUES ('513433', '5134', '冕宁县', '四川省,凉山彝族自治州,冕宁县', 3);
INSERT INTO `sys_region` VALUES ('513434', '5134', '越西县', '四川省,凉山彝族自治州,越西县', 3);
INSERT INTO `sys_region` VALUES ('513435', '5134', '甘洛县', '四川省,凉山彝族自治州,甘洛县', 3);
INSERT INTO `sys_region` VALUES ('513436', '5134', '美姑县', '四川省,凉山彝族自治州,美姑县', 3);
INSERT INTO `sys_region` VALUES ('513437', '5134', '雷波县', '四川省,凉山彝族自治州,雷波县', 3);
INSERT INTO `sys_region` VALUES ('52', '0', '贵州省', '贵州省', 1);
INSERT INTO `sys_region` VALUES ('5201', '52', '贵阳市', '贵州省,贵阳市', 2);
INSERT INTO `sys_region` VALUES ('520102', '5201', '南明区', '贵州省,贵阳市,南明区', 3);
INSERT INTO `sys_region` VALUES ('520103', '5201', '云岩区', '贵州省,贵阳市,云岩区', 3);
INSERT INTO `sys_region` VALUES ('520111', '5201', '花溪区', '贵州省,贵阳市,花溪区', 3);
INSERT INTO `sys_region` VALUES ('520112', '5201', '乌当区', '贵州省,贵阳市,乌当区', 3);
INSERT INTO `sys_region` VALUES ('520113', '5201', '白云区', '贵州省,贵阳市,白云区', 3);
INSERT INTO `sys_region` VALUES ('520115', '5201', '观山湖区', '贵州省,贵阳市,观山湖区', 3);
INSERT INTO `sys_region` VALUES ('520121', '5201', '开阳县', '贵州省,贵阳市,开阳县', 3);
INSERT INTO `sys_region` VALUES ('520122', '5201', '息烽县', '贵州省,贵阳市,息烽县', 3);
INSERT INTO `sys_region` VALUES ('520123', '5201', '修文县', '贵州省,贵阳市,修文县', 3);
INSERT INTO `sys_region` VALUES ('520181', '5201', '清镇市', '贵州省,贵阳市,清镇市', 3);
INSERT INTO `sys_region` VALUES ('5202', '52', '六盘水市', '贵州省,六盘水市', 2);
INSERT INTO `sys_region` VALUES ('520201', '5202', '钟山区', '贵州省,六盘水市,钟山区', 3);
INSERT INTO `sys_region` VALUES ('520203', '5202', '六枝特区', '贵州省,六盘水市,六枝特区', 3);
INSERT INTO `sys_region` VALUES ('520221', '5202', '水城县', '贵州省,六盘水市,水城县', 3);
INSERT INTO `sys_region` VALUES ('520281', '5202', '盘州市', '贵州省,六盘水市,盘州市', 3);
INSERT INTO `sys_region` VALUES ('5203', '52', '遵义市', '贵州省,遵义市', 2);
INSERT INTO `sys_region` VALUES ('520302', '5203', '红花岗区', '贵州省,遵义市,红花岗区', 3);
INSERT INTO `sys_region` VALUES ('520303', '5203', '汇川区', '贵州省,遵义市,汇川区', 3);
INSERT INTO `sys_region` VALUES ('520304', '5203', '播州区', '贵州省,遵义市,播州区', 3);
INSERT INTO `sys_region` VALUES ('520322', '5203', '桐梓县', '贵州省,遵义市,桐梓县', 3);
INSERT INTO `sys_region` VALUES ('520323', '5203', '绥阳县', '贵州省,遵义市,绥阳县', 3);
INSERT INTO `sys_region` VALUES ('520324', '5203', '正安县', '贵州省,遵义市,正安县', 3);
INSERT INTO `sys_region` VALUES ('520325', '5203', '道真仡佬族苗族自治县', '贵州省,遵义市,道真仡佬族苗族自治县', 3);
INSERT INTO `sys_region` VALUES ('520326', '5203', '务川仡佬族苗族自治县', '贵州省,遵义市,务川仡佬族苗族自治县', 3);
INSERT INTO `sys_region` VALUES ('520327', '5203', '凤冈县', '贵州省,遵义市,凤冈县', 3);
INSERT INTO `sys_region` VALUES ('520328', '5203', '湄潭县', '贵州省,遵义市,湄潭县', 3);
INSERT INTO `sys_region` VALUES ('520329', '5203', '余庆县', '贵州省,遵义市,余庆县', 3);
INSERT INTO `sys_region` VALUES ('520330', '5203', '习水县', '贵州省,遵义市,习水县', 3);
INSERT INTO `sys_region` VALUES ('520381', '5203', '赤水市', '贵州省,遵义市,赤水市', 3);
INSERT INTO `sys_region` VALUES ('520382', '5203', '仁怀市', '贵州省,遵义市,仁怀市', 3);
INSERT INTO `sys_region` VALUES ('5204', '52', '安顺市', '贵州省,安顺市', 2);
INSERT INTO `sys_region` VALUES ('520402', '5204', '西秀区', '贵州省,安顺市,西秀区', 3);
INSERT INTO `sys_region` VALUES ('520403', '5204', '平坝区', '贵州省,安顺市,平坝区', 3);
INSERT INTO `sys_region` VALUES ('520422', '5204', '普定县', '贵州省,安顺市,普定县', 3);
INSERT INTO `sys_region` VALUES ('520423', '5204', '镇宁布依族苗族自治县', '贵州省,安顺市,镇宁布依族苗族自治县', 3);
INSERT INTO `sys_region` VALUES ('520424', '5204', '关岭布依族苗族自治县', '贵州省,安顺市,关岭布依族苗族自治县', 3);
INSERT INTO `sys_region` VALUES ('520425', '5204', '紫云苗族布依族自治县', '贵州省,安顺市,紫云苗族布依族自治县', 3);
INSERT INTO `sys_region` VALUES ('5205', '52', '毕节市', '贵州省,毕节市', 2);
INSERT INTO `sys_region` VALUES ('520502', '5205', '七星关区', '贵州省,毕节市,七星关区', 3);
INSERT INTO `sys_region` VALUES ('520521', '5205', '大方县', '贵州省,毕节市,大方县', 3);
INSERT INTO `sys_region` VALUES ('520522', '5205', '黔西县', '贵州省,毕节市,黔西县', 3);
INSERT INTO `sys_region` VALUES ('520523', '5205', '金沙县', '贵州省,毕节市,金沙县', 3);
INSERT INTO `sys_region` VALUES ('520524', '5205', '织金县', '贵州省,毕节市,织金县', 3);
INSERT INTO `sys_region` VALUES ('520525', '5205', '纳雍县', '贵州省,毕节市,纳雍县', 3);
INSERT INTO `sys_region` VALUES ('520526', '5205', '威宁彝族回族苗族自治县', '贵州省,毕节市,威宁彝族回族苗族自治县', 3);
INSERT INTO `sys_region` VALUES ('520527', '5205', '赫章县', '贵州省,毕节市,赫章县', 3);
INSERT INTO `sys_region` VALUES ('5206', '52', '铜仁市', '贵州省,铜仁市', 2);
INSERT INTO `sys_region` VALUES ('520602', '5206', '碧江区', '贵州省,铜仁市,碧江区', 3);
INSERT INTO `sys_region` VALUES ('520603', '5206', '万山区', '贵州省,铜仁市,万山区', 3);
INSERT INTO `sys_region` VALUES ('520621', '5206', '江口县', '贵州省,铜仁市,江口县', 3);
INSERT INTO `sys_region` VALUES ('520622', '5206', '玉屏侗族自治县', '贵州省,铜仁市,玉屏侗族自治县', 3);
INSERT INTO `sys_region` VALUES ('520623', '5206', '石阡县', '贵州省,铜仁市,石阡县', 3);
INSERT INTO `sys_region` VALUES ('520624', '5206', '思南县', '贵州省,铜仁市,思南县', 3);
INSERT INTO `sys_region` VALUES ('520625', '5206', '印江土家族苗族自治县', '贵州省,铜仁市,印江土家族苗族自治县', 3);
INSERT INTO `sys_region` VALUES ('520626', '5206', '德江县', '贵州省,铜仁市,德江县', 3);
INSERT INTO `sys_region` VALUES ('520627', '5206', '沿河土家族自治县', '贵州省,铜仁市,沿河土家族自治县', 3);
INSERT INTO `sys_region` VALUES ('520628', '5206', '松桃苗族自治县', '贵州省,铜仁市,松桃苗族自治县', 3);
INSERT INTO `sys_region` VALUES ('5223', '52', '黔西南布依族苗族自治州', '贵州省,黔西南布依族苗族自治州', 2);
INSERT INTO `sys_region` VALUES ('522301', '5223', '兴义市', '贵州省,黔西南布依族苗族自治州,兴义市', 3);
INSERT INTO `sys_region` VALUES ('522302', '5223', '兴仁市', '贵州省,黔西南布依族苗族自治州,兴仁市', 3);
INSERT INTO `sys_region` VALUES ('522323', '5223', '普安县', '贵州省,黔西南布依族苗族自治州,普安县', 3);
INSERT INTO `sys_region` VALUES ('522324', '5223', '晴隆县', '贵州省,黔西南布依族苗族自治州,晴隆县', 3);
INSERT INTO `sys_region` VALUES ('522325', '5223', '贞丰县', '贵州省,黔西南布依族苗族自治州,贞丰县', 3);
INSERT INTO `sys_region` VALUES ('522326', '5223', '望谟县', '贵州省,黔西南布依族苗族自治州,望谟县', 3);
INSERT INTO `sys_region` VALUES ('522327', '5223', '册亨县', '贵州省,黔西南布依族苗族自治州,册亨县', 3);
INSERT INTO `sys_region` VALUES ('522328', '5223', '安龙县', '贵州省,黔西南布依族苗族自治州,安龙县', 3);
INSERT INTO `sys_region` VALUES ('5226', '52', '黔东南苗族侗族自治州', '贵州省,黔东南苗族侗族自治州', 2);
INSERT INTO `sys_region` VALUES ('522601', '5226', '凯里市', '贵州省,黔东南苗族侗族自治州,凯里市', 3);
INSERT INTO `sys_region` VALUES ('522622', '5226', '黄平县', '贵州省,黔东南苗族侗族自治州,黄平县', 3);
INSERT INTO `sys_region` VALUES ('522623', '5226', '施秉县', '贵州省,黔东南苗族侗族自治州,施秉县', 3);
INSERT INTO `sys_region` VALUES ('522624', '5226', '三穗县', '贵州省,黔东南苗族侗族自治州,三穗县', 3);
INSERT INTO `sys_region` VALUES ('522625', '5226', '镇远县', '贵州省,黔东南苗族侗族自治州,镇远县', 3);
INSERT INTO `sys_region` VALUES ('522626', '5226', '岑巩县', '贵州省,黔东南苗族侗族自治州,岑巩县', 3);
INSERT INTO `sys_region` VALUES ('522627', '5226', '天柱县', '贵州省,黔东南苗族侗族自治州,天柱县', 3);
INSERT INTO `sys_region` VALUES ('522628', '5226', '锦屏县', '贵州省,黔东南苗族侗族自治州,锦屏县', 3);
INSERT INTO `sys_region` VALUES ('522629', '5226', '剑河县', '贵州省,黔东南苗族侗族自治州,剑河县', 3);
INSERT INTO `sys_region` VALUES ('522630', '5226', '台江县', '贵州省,黔东南苗族侗族自治州,台江县', 3);
INSERT INTO `sys_region` VALUES ('522631', '5226', '黎平县', '贵州省,黔东南苗族侗族自治州,黎平县', 3);
INSERT INTO `sys_region` VALUES ('522632', '5226', '榕江县', '贵州省,黔东南苗族侗族自治州,榕江县', 3);
INSERT INTO `sys_region` VALUES ('522633', '5226', '从江县', '贵州省,黔东南苗族侗族自治州,从江县', 3);
INSERT INTO `sys_region` VALUES ('522634', '5226', '雷山县', '贵州省,黔东南苗族侗族自治州,雷山县', 3);
INSERT INTO `sys_region` VALUES ('522635', '5226', '麻江县', '贵州省,黔东南苗族侗族自治州,麻江县', 3);
INSERT INTO `sys_region` VALUES ('522636', '5226', '丹寨县', '贵州省,黔东南苗族侗族自治州,丹寨县', 3);
INSERT INTO `sys_region` VALUES ('5227', '52', '黔南布依族苗族自治州', '贵州省,黔南布依族苗族自治州', 2);
INSERT INTO `sys_region` VALUES ('522701', '5227', '都匀市', '贵州省,黔南布依族苗族自治州,都匀市', 3);
INSERT INTO `sys_region` VALUES ('522702', '5227', '福泉市', '贵州省,黔南布依族苗族自治州,福泉市', 3);
INSERT INTO `sys_region` VALUES ('522722', '5227', '荔波县', '贵州省,黔南布依族苗族自治州,荔波县', 3);
INSERT INTO `sys_region` VALUES ('522723', '5227', '贵定县', '贵州省,黔南布依族苗族自治州,贵定县', 3);
INSERT INTO `sys_region` VALUES ('522725', '5227', '瓮安县', '贵州省,黔南布依族苗族自治州,瓮安县', 3);
INSERT INTO `sys_region` VALUES ('522726', '5227', '独山县', '贵州省,黔南布依族苗族自治州,独山县', 3);
INSERT INTO `sys_region` VALUES ('522727', '5227', '平塘县', '贵州省,黔南布依族苗族自治州,平塘县', 3);
INSERT INTO `sys_region` VALUES ('522728', '5227', '罗甸县', '贵州省,黔南布依族苗族自治州,罗甸县', 3);
INSERT INTO `sys_region` VALUES ('522729', '5227', '长顺县', '贵州省,黔南布依族苗族自治州,长顺县', 3);
INSERT INTO `sys_region` VALUES ('522730', '5227', '龙里县', '贵州省,黔南布依族苗族自治州,龙里县', 3);
INSERT INTO `sys_region` VALUES ('522731', '5227', '惠水县', '贵州省,黔南布依族苗族自治州,惠水县', 3);
INSERT INTO `sys_region` VALUES ('522732', '5227', '三都水族自治县', '贵州省,黔南布依族苗族自治州,三都水族自治县', 3);
INSERT INTO `sys_region` VALUES ('53', '0', '云南省', '云南省', 1);
INSERT INTO `sys_region` VALUES ('5301', '53', '昆明市', '云南省,昆明市', 2);
INSERT INTO `sys_region` VALUES ('530102', '5301', '五华区', '云南省,昆明市,五华区', 3);
INSERT INTO `sys_region` VALUES ('530103', '5301', '盘龙区', '云南省,昆明市,盘龙区', 3);
INSERT INTO `sys_region` VALUES ('530111', '5301', '官渡区', '云南省,昆明市,官渡区', 3);
INSERT INTO `sys_region` VALUES ('530112', '5301', '西山区', '云南省,昆明市,西山区', 3);
INSERT INTO `sys_region` VALUES ('530113', '5301', '东川区', '云南省,昆明市,东川区', 3);
INSERT INTO `sys_region` VALUES ('530114', '5301', '呈贡区', '云南省,昆明市,呈贡区', 3);
INSERT INTO `sys_region` VALUES ('530115', '5301', '晋宁区', '云南省,昆明市,晋宁区', 3);
INSERT INTO `sys_region` VALUES ('530124', '5301', '富民县', '云南省,昆明市,富民县', 3);
INSERT INTO `sys_region` VALUES ('530125', '5301', '宜良县', '云南省,昆明市,宜良县', 3);
INSERT INTO `sys_region` VALUES ('530126', '5301', '石林彝族自治县', '云南省,昆明市,石林彝族自治县', 3);
INSERT INTO `sys_region` VALUES ('530127', '5301', '嵩明县', '云南省,昆明市,嵩明县', 3);
INSERT INTO `sys_region` VALUES ('530128', '5301', '禄劝彝族苗族自治县', '云南省,昆明市,禄劝彝族苗族自治县', 3);
INSERT INTO `sys_region` VALUES ('530129', '5301', '寻甸回族彝族自治县', '云南省,昆明市,寻甸回族彝族自治县', 3);
INSERT INTO `sys_region` VALUES ('530181', '5301', '安宁市', '云南省,昆明市,安宁市', 3);
INSERT INTO `sys_region` VALUES ('5303', '53', '曲靖市', '云南省,曲靖市', 2);
INSERT INTO `sys_region` VALUES ('530302', '5303', '麒麟区', '云南省,曲靖市,麒麟区', 3);
INSERT INTO `sys_region` VALUES ('530303', '5303', '沾益区', '云南省,曲靖市,沾益区', 3);
INSERT INTO `sys_region` VALUES ('530304', '5303', '马龙区', '云南省,曲靖市,马龙区', 3);
INSERT INTO `sys_region` VALUES ('530322', '5303', '陆良县', '云南省,曲靖市,陆良县', 3);
INSERT INTO `sys_region` VALUES ('530323', '5303', '师宗县', '云南省,曲靖市,师宗县', 3);
INSERT INTO `sys_region` VALUES ('530324', '5303', '罗平县', '云南省,曲靖市,罗平县', 3);
INSERT INTO `sys_region` VALUES ('530325', '5303', '富源县', '云南省,曲靖市,富源县', 3);
INSERT INTO `sys_region` VALUES ('530326', '5303', '会泽县', '云南省,曲靖市,会泽县', 3);
INSERT INTO `sys_region` VALUES ('530381', '5303', '宣威市', '云南省,曲靖市,宣威市', 3);
INSERT INTO `sys_region` VALUES ('5304', '53', '玉溪市', '云南省,玉溪市', 2);
INSERT INTO `sys_region` VALUES ('530402', '5304', '红塔区', '云南省,玉溪市,红塔区', 3);
INSERT INTO `sys_region` VALUES ('530403', '5304', '江川区', '云南省,玉溪市,江川区', 3);
INSERT INTO `sys_region` VALUES ('530422', '5304', '澄江县', '云南省,玉溪市,澄江县', 3);
INSERT INTO `sys_region` VALUES ('530423', '5304', '通海县', '云南省,玉溪市,通海县', 3);
INSERT INTO `sys_region` VALUES ('530424', '5304', '华宁县', '云南省,玉溪市,华宁县', 3);
INSERT INTO `sys_region` VALUES ('530425', '5304', '易门县', '云南省,玉溪市,易门县', 3);
INSERT INTO `sys_region` VALUES ('530426', '5304', '峨山彝族自治县', '云南省,玉溪市,峨山彝族自治县', 3);
INSERT INTO `sys_region` VALUES ('530427', '5304', '新平彝族傣族自治县', '云南省,玉溪市,新平彝族傣族自治县', 3);
INSERT INTO `sys_region` VALUES ('530428', '5304', '元江哈尼族彝族傣族自治县', '云南省,玉溪市,元江哈尼族彝族傣族自治县', 3);
INSERT INTO `sys_region` VALUES ('5305', '53', '保山市', '云南省,保山市', 2);
INSERT INTO `sys_region` VALUES ('530502', '5305', '隆阳区', '云南省,保山市,隆阳区', 3);
INSERT INTO `sys_region` VALUES ('530521', '5305', '施甸县', '云南省,保山市,施甸县', 3);
INSERT INTO `sys_region` VALUES ('530523', '5305', '龙陵县', '云南省,保山市,龙陵县', 3);
INSERT INTO `sys_region` VALUES ('530524', '5305', '昌宁县', '云南省,保山市,昌宁县', 3);
INSERT INTO `sys_region` VALUES ('530581', '5305', '腾冲市', '云南省,保山市,腾冲市', 3);
INSERT INTO `sys_region` VALUES ('5306', '53', '昭通市', '云南省,昭通市', 2);
INSERT INTO `sys_region` VALUES ('530602', '5306', '昭阳区', '云南省,昭通市,昭阳区', 3);
INSERT INTO `sys_region` VALUES ('530621', '5306', '鲁甸县', '云南省,昭通市,鲁甸县', 3);
INSERT INTO `sys_region` VALUES ('530622', '5306', '巧家县', '云南省,昭通市,巧家县', 3);
INSERT INTO `sys_region` VALUES ('530623', '5306', '盐津县', '云南省,昭通市,盐津县', 3);
INSERT INTO `sys_region` VALUES ('530624', '5306', '大关县', '云南省,昭通市,大关县', 3);
INSERT INTO `sys_region` VALUES ('530625', '5306', '永善县', '云南省,昭通市,永善县', 3);
INSERT INTO `sys_region` VALUES ('530626', '5306', '绥江县', '云南省,昭通市,绥江县', 3);
INSERT INTO `sys_region` VALUES ('530627', '5306', '镇雄县', '云南省,昭通市,镇雄县', 3);
INSERT INTO `sys_region` VALUES ('530628', '5306', '彝良县', '云南省,昭通市,彝良县', 3);
INSERT INTO `sys_region` VALUES ('530629', '5306', '威信县', '云南省,昭通市,威信县', 3);
INSERT INTO `sys_region` VALUES ('530681', '5306', '水富市', '云南省,昭通市,水富市', 3);
INSERT INTO `sys_region` VALUES ('5307', '53', '丽江市', '云南省,丽江市', 2);
INSERT INTO `sys_region` VALUES ('530702', '5307', '古城区', '云南省,丽江市,古城区', 3);
INSERT INTO `sys_region` VALUES ('530721', '5307', '玉龙纳西族自治县', '云南省,丽江市,玉龙纳西族自治县', 3);
INSERT INTO `sys_region` VALUES ('530722', '5307', '永胜县', '云南省,丽江市,永胜县', 3);
INSERT INTO `sys_region` VALUES ('530723', '5307', '华坪县', '云南省,丽江市,华坪县', 3);
INSERT INTO `sys_region` VALUES ('530724', '5307', '宁蒗彝族自治县', '云南省,丽江市,宁蒗彝族自治县', 3);
INSERT INTO `sys_region` VALUES ('5308', '53', '普洱市', '云南省,普洱市', 2);
INSERT INTO `sys_region` VALUES ('530802', '5308', '思茅区', '云南省,普洱市,思茅区', 3);
INSERT INTO `sys_region` VALUES ('530821', '5308', '宁洱哈尼族彝族自治县', '云南省,普洱市,宁洱哈尼族彝族自治县', 3);
INSERT INTO `sys_region` VALUES ('530822', '5308', '墨江哈尼族自治县', '云南省,普洱市,墨江哈尼族自治县', 3);
INSERT INTO `sys_region` VALUES ('530823', '5308', '景东彝族自治县', '云南省,普洱市,景东彝族自治县', 3);
INSERT INTO `sys_region` VALUES ('530824', '5308', '景谷傣族彝族自治县', '云南省,普洱市,景谷傣族彝族自治县', 3);
INSERT INTO `sys_region` VALUES ('530825', '5308', '镇沅彝族哈尼族拉祜族自治县', '云南省,普洱市,镇沅彝族哈尼族拉祜族自治县', 3);
INSERT INTO `sys_region` VALUES ('530826', '5308', '江城哈尼族彝族自治县', '云南省,普洱市,江城哈尼族彝族自治县', 3);
INSERT INTO `sys_region` VALUES ('530827', '5308', '孟连傣族拉祜族佤族自治县', '云南省,普洱市,孟连傣族拉祜族佤族自治县', 3);
INSERT INTO `sys_region` VALUES ('530828', '5308', '澜沧拉祜族自治县', '云南省,普洱市,澜沧拉祜族自治县', 3);
INSERT INTO `sys_region` VALUES ('530829', '5308', '西盟佤族自治县', '云南省,普洱市,西盟佤族自治县', 3);
INSERT INTO `sys_region` VALUES ('5309', '53', '临沧市', '云南省,临沧市', 2);
INSERT INTO `sys_region` VALUES ('530902', '5309', '临翔区', '云南省,临沧市,临翔区', 3);
INSERT INTO `sys_region` VALUES ('530921', '5309', '凤庆县', '云南省,临沧市,凤庆县', 3);
INSERT INTO `sys_region` VALUES ('530922', '5309', '云县', '云南省,临沧市,云县', 3);
INSERT INTO `sys_region` VALUES ('530923', '5309', '永德县', '云南省,临沧市,永德县', 3);
INSERT INTO `sys_region` VALUES ('530924', '5309', '镇康县', '云南省,临沧市,镇康县', 3);
INSERT INTO `sys_region` VALUES ('530925', '5309', '双江拉祜族佤族布朗族傣族自治县', '云南省,临沧市,双江拉祜族佤族布朗族傣族自治县', 3);
INSERT INTO `sys_region` VALUES ('530926', '5309', '耿马傣族佤族自治县', '云南省,临沧市,耿马傣族佤族自治县', 3);
INSERT INTO `sys_region` VALUES ('530927', '5309', '沧源佤族自治县', '云南省,临沧市,沧源佤族自治县', 3);
INSERT INTO `sys_region` VALUES ('5323', '53', '楚雄彝族自治州', '云南省,楚雄彝族自治州', 2);
INSERT INTO `sys_region` VALUES ('532301', '5323', '楚雄市', '云南省,楚雄彝族自治州,楚雄市', 3);
INSERT INTO `sys_region` VALUES ('532322', '5323', '双柏县', '云南省,楚雄彝族自治州,双柏县', 3);
INSERT INTO `sys_region` VALUES ('532323', '5323', '牟定县', '云南省,楚雄彝族自治州,牟定县', 3);
INSERT INTO `sys_region` VALUES ('532324', '5323', '南华县', '云南省,楚雄彝族自治州,南华县', 3);
INSERT INTO `sys_region` VALUES ('532325', '5323', '姚安县', '云南省,楚雄彝族自治州,姚安县', 3);
INSERT INTO `sys_region` VALUES ('532326', '5323', '大姚县', '云南省,楚雄彝族自治州,大姚县', 3);
INSERT INTO `sys_region` VALUES ('532327', '5323', '永仁县', '云南省,楚雄彝族自治州,永仁县', 3);
INSERT INTO `sys_region` VALUES ('532328', '5323', '元谋县', '云南省,楚雄彝族自治州,元谋县', 3);
INSERT INTO `sys_region` VALUES ('532329', '5323', '武定县', '云南省,楚雄彝族自治州,武定县', 3);
INSERT INTO `sys_region` VALUES ('532331', '5323', '禄丰县', '云南省,楚雄彝族自治州,禄丰县', 3);
INSERT INTO `sys_region` VALUES ('5325', '53', '红河哈尼族彝族自治州', '云南省,红河哈尼族彝族自治州', 2);
INSERT INTO `sys_region` VALUES ('532501', '5325', '个旧市', '云南省,红河哈尼族彝族自治州,个旧市', 3);
INSERT INTO `sys_region` VALUES ('532502', '5325', '开远市', '云南省,红河哈尼族彝族自治州,开远市', 3);
INSERT INTO `sys_region` VALUES ('532503', '5325', '蒙自市', '云南省,红河哈尼族彝族自治州,蒙自市', 3);
INSERT INTO `sys_region` VALUES ('532504', '5325', '弥勒市', '云南省,红河哈尼族彝族自治州,弥勒市', 3);
INSERT INTO `sys_region` VALUES ('532523', '5325', '屏边苗族自治县', '云南省,红河哈尼族彝族自治州,屏边苗族自治县', 3);
INSERT INTO `sys_region` VALUES ('532524', '5325', '建水县', '云南省,红河哈尼族彝族自治州,建水县', 3);
INSERT INTO `sys_region` VALUES ('532525', '5325', '石屏县', '云南省,红河哈尼族彝族自治州,石屏县', 3);
INSERT INTO `sys_region` VALUES ('532527', '5325', '泸西县', '云南省,红河哈尼族彝族自治州,泸西县', 3);
INSERT INTO `sys_region` VALUES ('532528', '5325', '元阳县', '云南省,红河哈尼族彝族自治州,元阳县', 3);
INSERT INTO `sys_region` VALUES ('532529', '5325', '红河县', '云南省,红河哈尼族彝族自治州,红河县', 3);
INSERT INTO `sys_region` VALUES ('532530', '5325', '金平苗族瑶族傣族自治县', '云南省,红河哈尼族彝族自治州,金平苗族瑶族傣族自治县', 3);
INSERT INTO `sys_region` VALUES ('532531', '5325', '绿春县', '云南省,红河哈尼族彝族自治州,绿春县', 3);
INSERT INTO `sys_region` VALUES ('532532', '5325', '河口瑶族自治县', '云南省,红河哈尼族彝族自治州,河口瑶族自治县', 3);
INSERT INTO `sys_region` VALUES ('5326', '53', '文山壮族苗族自治州', '云南省,文山壮族苗族自治州', 2);
INSERT INTO `sys_region` VALUES ('532601', '5326', '文山市', '云南省,文山壮族苗族自治州,文山市', 3);
INSERT INTO `sys_region` VALUES ('532622', '5326', '砚山县', '云南省,文山壮族苗族自治州,砚山县', 3);
INSERT INTO `sys_region` VALUES ('532623', '5326', '西畴县', '云南省,文山壮族苗族自治州,西畴县', 3);
INSERT INTO `sys_region` VALUES ('532624', '5326', '麻栗坡县', '云南省,文山壮族苗族自治州,麻栗坡县', 3);
INSERT INTO `sys_region` VALUES ('532625', '5326', '马关县', '云南省,文山壮族苗族自治州,马关县', 3);
INSERT INTO `sys_region` VALUES ('532626', '5326', '丘北县', '云南省,文山壮族苗族自治州,丘北县', 3);
INSERT INTO `sys_region` VALUES ('532627', '5326', '广南县', '云南省,文山壮族苗族自治州,广南县', 3);
INSERT INTO `sys_region` VALUES ('532628', '5326', '富宁县', '云南省,文山壮族苗族自治州,富宁县', 3);
INSERT INTO `sys_region` VALUES ('5328', '53', '西双版纳傣族自治州', '云南省,西双版纳傣族自治州', 2);
INSERT INTO `sys_region` VALUES ('532801', '5328', '景洪市', '云南省,西双版纳傣族自治州,景洪市', 3);
INSERT INTO `sys_region` VALUES ('532822', '5328', '勐海县', '云南省,西双版纳傣族自治州,勐海县', 3);
INSERT INTO `sys_region` VALUES ('532823', '5328', '勐腊县', '云南省,西双版纳傣族自治州,勐腊县', 3);
INSERT INTO `sys_region` VALUES ('5329', '53', '大理白族自治州', '云南省,大理白族自治州', 2);
INSERT INTO `sys_region` VALUES ('532901', '5329', '大理市', '云南省,大理白族自治州,大理市', 3);
INSERT INTO `sys_region` VALUES ('532922', '5329', '漾濞彝族自治县', '云南省,大理白族自治州,漾濞彝族自治县', 3);
INSERT INTO `sys_region` VALUES ('532923', '5329', '祥云县', '云南省,大理白族自治州,祥云县', 3);
INSERT INTO `sys_region` VALUES ('532924', '5329', '宾川县', '云南省,大理白族自治州,宾川县', 3);
INSERT INTO `sys_region` VALUES ('532925', '5329', '弥渡县', '云南省,大理白族自治州,弥渡县', 3);
INSERT INTO `sys_region` VALUES ('532926', '5329', '南涧彝族自治县', '云南省,大理白族自治州,南涧彝族自治县', 3);
INSERT INTO `sys_region` VALUES ('532927', '5329', '巍山彝族回族自治县', '云南省,大理白族自治州,巍山彝族回族自治县', 3);
INSERT INTO `sys_region` VALUES ('532928', '5329', '永平县', '云南省,大理白族自治州,永平县', 3);
INSERT INTO `sys_region` VALUES ('532929', '5329', '云龙县', '云南省,大理白族自治州,云龙县', 3);
INSERT INTO `sys_region` VALUES ('532930', '5329', '洱源县', '云南省,大理白族自治州,洱源县', 3);
INSERT INTO `sys_region` VALUES ('532931', '5329', '剑川县', '云南省,大理白族自治州,剑川县', 3);
INSERT INTO `sys_region` VALUES ('532932', '5329', '鹤庆县', '云南省,大理白族自治州,鹤庆县', 3);
INSERT INTO `sys_region` VALUES ('5331', '53', '德宏傣族景颇族自治州', '云南省,德宏傣族景颇族自治州', 2);
INSERT INTO `sys_region` VALUES ('533102', '5331', '瑞丽市', '云南省,德宏傣族景颇族自治州,瑞丽市', 3);
INSERT INTO `sys_region` VALUES ('533103', '5331', '芒市', '云南省,德宏傣族景颇族自治州,芒市', 3);
INSERT INTO `sys_region` VALUES ('533122', '5331', '梁河县', '云南省,德宏傣族景颇族自治州,梁河县', 3);
INSERT INTO `sys_region` VALUES ('533123', '5331', '盈江县', '云南省,德宏傣族景颇族自治州,盈江县', 3);
INSERT INTO `sys_region` VALUES ('533124', '5331', '陇川县', '云南省,德宏傣族景颇族自治州,陇川县', 3);
INSERT INTO `sys_region` VALUES ('5333', '53', '怒江傈僳族自治州', '云南省,怒江傈僳族自治州', 2);
INSERT INTO `sys_region` VALUES ('533301', '5333', '泸水市', '云南省,怒江傈僳族自治州,泸水市', 3);
INSERT INTO `sys_region` VALUES ('533323', '5333', '福贡县', '云南省,怒江傈僳族自治州,福贡县', 3);
INSERT INTO `sys_region` VALUES ('533324', '5333', '贡山独龙族怒族自治县', '云南省,怒江傈僳族自治州,贡山独龙族怒族自治县', 3);
INSERT INTO `sys_region` VALUES ('533325', '5333', '兰坪白族普米族自治县', '云南省,怒江傈僳族自治州,兰坪白族普米族自治县', 3);
INSERT INTO `sys_region` VALUES ('5334', '53', '迪庆藏族自治州', '云南省,迪庆藏族自治州', 2);
INSERT INTO `sys_region` VALUES ('533401', '5334', '香格里拉市', '云南省,迪庆藏族自治州,香格里拉市', 3);
INSERT INTO `sys_region` VALUES ('533422', '5334', '德钦县', '云南省,迪庆藏族自治州,德钦县', 3);
INSERT INTO `sys_region` VALUES ('533423', '5334', '维西傈僳族自治县', '云南省,迪庆藏族自治州,维西傈僳族自治县', 3);
INSERT INTO `sys_region` VALUES ('54', '0', '西藏自治区', '西藏自治区', 1);
INSERT INTO `sys_region` VALUES ('5401', '54', '拉萨市', '西藏自治区,拉萨市', 2);
INSERT INTO `sys_region` VALUES ('540102', '5401', '城关区', '西藏自治区,拉萨市,城关区', 3);
INSERT INTO `sys_region` VALUES ('540103', '5401', '堆龙德庆区', '西藏自治区,拉萨市,堆龙德庆区', 3);
INSERT INTO `sys_region` VALUES ('540104', '5401', '达孜区', '西藏自治区,拉萨市,达孜区', 3);
INSERT INTO `sys_region` VALUES ('540121', '5401', '林周县', '西藏自治区,拉萨市,林周县', 3);
INSERT INTO `sys_region` VALUES ('540122', '5401', '当雄县', '西藏自治区,拉萨市,当雄县', 3);
INSERT INTO `sys_region` VALUES ('540123', '5401', '尼木县', '西藏自治区,拉萨市,尼木县', 3);
INSERT INTO `sys_region` VALUES ('540124', '5401', '曲水县', '西藏自治区,拉萨市,曲水县', 3);
INSERT INTO `sys_region` VALUES ('540127', '5401', '墨竹工卡县', '西藏自治区,拉萨市,墨竹工卡县', 3);
INSERT INTO `sys_region` VALUES ('540171', '5401', '格尔木藏青工业园区', '西藏自治区,拉萨市,格尔木藏青工业园区', 3);
INSERT INTO `sys_region` VALUES ('540172', '5401', '拉萨经济技术开发区', '西藏自治区,拉萨市,拉萨经济技术开发区', 3);
INSERT INTO `sys_region` VALUES ('540173', '5401', '西藏文化旅游创意园区', '西藏自治区,拉萨市,西藏文化旅游创意园区', 3);
INSERT INTO `sys_region` VALUES ('540174', '5401', '达孜工业园区', '西藏自治区,拉萨市,达孜工业园区', 3);
INSERT INTO `sys_region` VALUES ('5402', '54', '日喀则市', '西藏自治区,日喀则市', 2);
INSERT INTO `sys_region` VALUES ('540202', '5402', '桑珠孜区', '西藏自治区,日喀则市,桑珠孜区', 3);
INSERT INTO `sys_region` VALUES ('540221', '5402', '南木林县', '西藏自治区,日喀则市,南木林县', 3);
INSERT INTO `sys_region` VALUES ('540222', '5402', '江孜县', '西藏自治区,日喀则市,江孜县', 3);
INSERT INTO `sys_region` VALUES ('540223', '5402', '定日县', '西藏自治区,日喀则市,定日县', 3);
INSERT INTO `sys_region` VALUES ('540224', '5402', '萨迦县', '西藏自治区,日喀则市,萨迦县', 3);
INSERT INTO `sys_region` VALUES ('540225', '5402', '拉孜县', '西藏自治区,日喀则市,拉孜县', 3);
INSERT INTO `sys_region` VALUES ('540226', '5402', '昂仁县', '西藏自治区,日喀则市,昂仁县', 3);
INSERT INTO `sys_region` VALUES ('540227', '5402', '谢通门县', '西藏自治区,日喀则市,谢通门县', 3);
INSERT INTO `sys_region` VALUES ('540228', '5402', '白朗县', '西藏自治区,日喀则市,白朗县', 3);
INSERT INTO `sys_region` VALUES ('540229', '5402', '仁布县', '西藏自治区,日喀则市,仁布县', 3);
INSERT INTO `sys_region` VALUES ('540230', '5402', '康马县', '西藏自治区,日喀则市,康马县', 3);
INSERT INTO `sys_region` VALUES ('540231', '5402', '定结县', '西藏自治区,日喀则市,定结县', 3);
INSERT INTO `sys_region` VALUES ('540232', '5402', '仲巴县', '西藏自治区,日喀则市,仲巴县', 3);
INSERT INTO `sys_region` VALUES ('540233', '5402', '亚东县', '西藏自治区,日喀则市,亚东县', 3);
INSERT INTO `sys_region` VALUES ('540234', '5402', '吉隆县', '西藏自治区,日喀则市,吉隆县', 3);
INSERT INTO `sys_region` VALUES ('540235', '5402', '聂拉木县', '西藏自治区,日喀则市,聂拉木县', 3);
INSERT INTO `sys_region` VALUES ('540236', '5402', '萨嘎县', '西藏自治区,日喀则市,萨嘎县', 3);
INSERT INTO `sys_region` VALUES ('540237', '5402', '岗巴县', '西藏自治区,日喀则市,岗巴县', 3);
INSERT INTO `sys_region` VALUES ('5403', '54', '昌都市', '西藏自治区,昌都市', 2);
INSERT INTO `sys_region` VALUES ('540302', '5403', '卡若区', '西藏自治区,昌都市,卡若区', 3);
INSERT INTO `sys_region` VALUES ('540321', '5403', '江达县', '西藏自治区,昌都市,江达县', 3);
INSERT INTO `sys_region` VALUES ('540322', '5403', '贡觉县', '西藏自治区,昌都市,贡觉县', 3);
INSERT INTO `sys_region` VALUES ('540323', '5403', '类乌齐县', '西藏自治区,昌都市,类乌齐县', 3);
INSERT INTO `sys_region` VALUES ('540324', '5403', '丁青县', '西藏自治区,昌都市,丁青县', 3);
INSERT INTO `sys_region` VALUES ('540325', '5403', '察雅县', '西藏自治区,昌都市,察雅县', 3);
INSERT INTO `sys_region` VALUES ('540326', '5403', '八宿县', '西藏自治区,昌都市,八宿县', 3);
INSERT INTO `sys_region` VALUES ('540327', '5403', '左贡县', '西藏自治区,昌都市,左贡县', 3);
INSERT INTO `sys_region` VALUES ('540328', '5403', '芒康县', '西藏自治区,昌都市,芒康县', 3);
INSERT INTO `sys_region` VALUES ('540329', '5403', '洛隆县', '西藏自治区,昌都市,洛隆县', 3);
INSERT INTO `sys_region` VALUES ('540330', '5403', '边坝县', '西藏自治区,昌都市,边坝县', 3);
INSERT INTO `sys_region` VALUES ('5404', '54', '林芝市', '西藏自治区,林芝市', 2);
INSERT INTO `sys_region` VALUES ('540402', '5404', '巴宜区', '西藏自治区,林芝市,巴宜区', 3);
INSERT INTO `sys_region` VALUES ('540421', '5404', '工布江达县', '西藏自治区,林芝市,工布江达县', 3);
INSERT INTO `sys_region` VALUES ('540422', '5404', '米林县', '西藏自治区,林芝市,米林县', 3);
INSERT INTO `sys_region` VALUES ('540423', '5404', '墨脱县', '西藏自治区,林芝市,墨脱县', 3);
INSERT INTO `sys_region` VALUES ('540424', '5404', '波密县', '西藏自治区,林芝市,波密县', 3);
INSERT INTO `sys_region` VALUES ('540425', '5404', '察隅县', '西藏自治区,林芝市,察隅县', 3);
INSERT INTO `sys_region` VALUES ('540426', '5404', '朗县', '西藏自治区,林芝市,朗县', 3);
INSERT INTO `sys_region` VALUES ('5405', '54', '山南市', '西藏自治区,山南市', 2);
INSERT INTO `sys_region` VALUES ('540502', '5405', '乃东区', '西藏自治区,山南市,乃东区', 3);
INSERT INTO `sys_region` VALUES ('540521', '5405', '扎囊县', '西藏自治区,山南市,扎囊县', 3);
INSERT INTO `sys_region` VALUES ('540522', '5405', '贡嘎县', '西藏自治区,山南市,贡嘎县', 3);
INSERT INTO `sys_region` VALUES ('540523', '5405', '桑日县', '西藏自治区,山南市,桑日县', 3);
INSERT INTO `sys_region` VALUES ('540524', '5405', '琼结县', '西藏自治区,山南市,琼结县', 3);
INSERT INTO `sys_region` VALUES ('540525', '5405', '曲松县', '西藏自治区,山南市,曲松县', 3);
INSERT INTO `sys_region` VALUES ('540526', '5405', '措美县', '西藏自治区,山南市,措美县', 3);
INSERT INTO `sys_region` VALUES ('540527', '5405', '洛扎县', '西藏自治区,山南市,洛扎县', 3);
INSERT INTO `sys_region` VALUES ('540528', '5405', '加查县', '西藏自治区,山南市,加查县', 3);
INSERT INTO `sys_region` VALUES ('540529', '5405', '隆子县', '西藏自治区,山南市,隆子县', 3);
INSERT INTO `sys_region` VALUES ('540530', '5405', '错那县', '西藏自治区,山南市,错那县', 3);
INSERT INTO `sys_region` VALUES ('540531', '5405', '浪卡子县', '西藏自治区,山南市,浪卡子县', 3);
INSERT INTO `sys_region` VALUES ('5406', '54', '那曲市', '西藏自治区,那曲市', 2);
INSERT INTO `sys_region` VALUES ('540602', '5406', '色尼区', '西藏自治区,那曲市,色尼区', 3);
INSERT INTO `sys_region` VALUES ('540621', '5406', '嘉黎县', '西藏自治区,那曲市,嘉黎县', 3);
INSERT INTO `sys_region` VALUES ('540622', '5406', '比如县', '西藏自治区,那曲市,比如县', 3);
INSERT INTO `sys_region` VALUES ('540623', '5406', '聂荣县', '西藏自治区,那曲市,聂荣县', 3);
INSERT INTO `sys_region` VALUES ('540624', '5406', '安多县', '西藏自治区,那曲市,安多县', 3);
INSERT INTO `sys_region` VALUES ('540625', '5406', '申扎县', '西藏自治区,那曲市,申扎县', 3);
INSERT INTO `sys_region` VALUES ('540626', '5406', '索县', '西藏自治区,那曲市,索县', 3);
INSERT INTO `sys_region` VALUES ('540627', '5406', '班戈县', '西藏自治区,那曲市,班戈县', 3);
INSERT INTO `sys_region` VALUES ('540628', '5406', '巴青县', '西藏自治区,那曲市,巴青县', 3);
INSERT INTO `sys_region` VALUES ('540629', '5406', '尼玛县', '西藏自治区,那曲市,尼玛县', 3);
INSERT INTO `sys_region` VALUES ('540630', '5406', '双湖县', '西藏自治区,那曲市,双湖县', 3);
INSERT INTO `sys_region` VALUES ('5425', '54', '阿里地区', '西藏自治区,阿里地区', 2);
INSERT INTO `sys_region` VALUES ('542521', '5425', '普兰县', '西藏自治区,阿里地区,普兰县', 3);
INSERT INTO `sys_region` VALUES ('542522', '5425', '札达县', '西藏自治区,阿里地区,札达县', 3);
INSERT INTO `sys_region` VALUES ('542523', '5425', '噶尔县', '西藏自治区,阿里地区,噶尔县', 3);
INSERT INTO `sys_region` VALUES ('542524', '5425', '日土县', '西藏自治区,阿里地区,日土县', 3);
INSERT INTO `sys_region` VALUES ('542525', '5425', '革吉县', '西藏自治区,阿里地区,革吉县', 3);
INSERT INTO `sys_region` VALUES ('542526', '5425', '改则县', '西藏自治区,阿里地区,改则县', 3);
INSERT INTO `sys_region` VALUES ('542527', '5425', '措勤县', '西藏自治区,阿里地区,措勤县', 3);
INSERT INTO `sys_region` VALUES ('61', '0', '陕西省', '陕西省', 1);
INSERT INTO `sys_region` VALUES ('6101', '61', '西安市', '陕西省,西安市', 2);
INSERT INTO `sys_region` VALUES ('610102', '6101', '新城区', '陕西省,西安市,新城区', 3);
INSERT INTO `sys_region` VALUES ('610103', '6101', '碑林区', '陕西省,西安市,碑林区', 3);
INSERT INTO `sys_region` VALUES ('610104', '6101', '莲湖区', '陕西省,西安市,莲湖区', 3);
INSERT INTO `sys_region` VALUES ('610111', '6101', '灞桥区', '陕西省,西安市,灞桥区', 3);
INSERT INTO `sys_region` VALUES ('610112', '6101', '未央区', '陕西省,西安市,未央区', 3);
INSERT INTO `sys_region` VALUES ('610113', '6101', '雁塔区', '陕西省,西安市,雁塔区', 3);
INSERT INTO `sys_region` VALUES ('610114', '6101', '阎良区', '陕西省,西安市,阎良区', 3);
INSERT INTO `sys_region` VALUES ('610115', '6101', '临潼区', '陕西省,西安市,临潼区', 3);
INSERT INTO `sys_region` VALUES ('610116', '6101', '长安区', '陕西省,西安市,长安区', 3);
INSERT INTO `sys_region` VALUES ('610117', '6101', '高陵区', '陕西省,西安市,高陵区', 3);
INSERT INTO `sys_region` VALUES ('610118', '6101', '鄠邑区', '陕西省,西安市,鄠邑区', 3);
INSERT INTO `sys_region` VALUES ('610122', '6101', '蓝田县', '陕西省,西安市,蓝田县', 3);
INSERT INTO `sys_region` VALUES ('610124', '6101', '周至县', '陕西省,西安市,周至县', 3);
INSERT INTO `sys_region` VALUES ('6102', '61', '铜川市', '陕西省,铜川市', 2);
INSERT INTO `sys_region` VALUES ('610202', '6102', '王益区', '陕西省,铜川市,王益区', 3);
INSERT INTO `sys_region` VALUES ('610203', '6102', '印台区', '陕西省,铜川市,印台区', 3);
INSERT INTO `sys_region` VALUES ('610204', '6102', '耀州区', '陕西省,铜川市,耀州区', 3);
INSERT INTO `sys_region` VALUES ('610222', '6102', '宜君县', '陕西省,铜川市,宜君县', 3);
INSERT INTO `sys_region` VALUES ('6103', '61', '宝鸡市', '陕西省,宝鸡市', 2);
INSERT INTO `sys_region` VALUES ('610302', '6103', '渭滨区', '陕西省,宝鸡市,渭滨区', 3);
INSERT INTO `sys_region` VALUES ('610303', '6103', '金台区', '陕西省,宝鸡市,金台区', 3);
INSERT INTO `sys_region` VALUES ('610304', '6103', '陈仓区', '陕西省,宝鸡市,陈仓区', 3);
INSERT INTO `sys_region` VALUES ('610322', '6103', '凤翔县', '陕西省,宝鸡市,凤翔县', 3);
INSERT INTO `sys_region` VALUES ('610323', '6103', '岐山县', '陕西省,宝鸡市,岐山县', 3);
INSERT INTO `sys_region` VALUES ('610324', '6103', '扶风县', '陕西省,宝鸡市,扶风县', 3);
INSERT INTO `sys_region` VALUES ('610326', '6103', '眉县', '陕西省,宝鸡市,眉县', 3);
INSERT INTO `sys_region` VALUES ('610327', '6103', '陇县', '陕西省,宝鸡市,陇县', 3);
INSERT INTO `sys_region` VALUES ('610328', '6103', '千阳县', '陕西省,宝鸡市,千阳县', 3);
INSERT INTO `sys_region` VALUES ('610329', '6103', '麟游县', '陕西省,宝鸡市,麟游县', 3);
INSERT INTO `sys_region` VALUES ('610330', '6103', '凤县', '陕西省,宝鸡市,凤县', 3);
INSERT INTO `sys_region` VALUES ('610331', '6103', '太白县', '陕西省,宝鸡市,太白县', 3);
INSERT INTO `sys_region` VALUES ('6104', '61', '咸阳市', '陕西省,咸阳市', 2);
INSERT INTO `sys_region` VALUES ('610402', '6104', '秦都区', '陕西省,咸阳市,秦都区', 3);
INSERT INTO `sys_region` VALUES ('610403', '6104', '杨陵区', '陕西省,咸阳市,杨陵区', 3);
INSERT INTO `sys_region` VALUES ('610404', '6104', '渭城区', '陕西省,咸阳市,渭城区', 3);
INSERT INTO `sys_region` VALUES ('610422', '6104', '三原县', '陕西省,咸阳市,三原县', 3);
INSERT INTO `sys_region` VALUES ('610423', '6104', '泾阳县', '陕西省,咸阳市,泾阳县', 3);
INSERT INTO `sys_region` VALUES ('610424', '6104', '乾县', '陕西省,咸阳市,乾县', 3);
INSERT INTO `sys_region` VALUES ('610425', '6104', '礼泉县', '陕西省,咸阳市,礼泉县', 3);
INSERT INTO `sys_region` VALUES ('610426', '6104', '永寿县', '陕西省,咸阳市,永寿县', 3);
INSERT INTO `sys_region` VALUES ('610428', '6104', '长武县', '陕西省,咸阳市,长武县', 3);
INSERT INTO `sys_region` VALUES ('610429', '6104', '旬邑县', '陕西省,咸阳市,旬邑县', 3);
INSERT INTO `sys_region` VALUES ('610430', '6104', '淳化县', '陕西省,咸阳市,淳化县', 3);
INSERT INTO `sys_region` VALUES ('610431', '6104', '武功县', '陕西省,咸阳市,武功县', 3);
INSERT INTO `sys_region` VALUES ('610481', '6104', '兴平市', '陕西省,咸阳市,兴平市', 3);
INSERT INTO `sys_region` VALUES ('610482', '6104', '彬州市', '陕西省,咸阳市,彬州市', 3);
INSERT INTO `sys_region` VALUES ('6105', '61', '渭南市', '陕西省,渭南市', 2);
INSERT INTO `sys_region` VALUES ('610502', '6105', '临渭区', '陕西省,渭南市,临渭区', 3);
INSERT INTO `sys_region` VALUES ('610503', '6105', '华州区', '陕西省,渭南市,华州区', 3);
INSERT INTO `sys_region` VALUES ('610522', '6105', '潼关县', '陕西省,渭南市,潼关县', 3);
INSERT INTO `sys_region` VALUES ('610523', '6105', '大荔县', '陕西省,渭南市,大荔县', 3);
INSERT INTO `sys_region` VALUES ('610524', '6105', '合阳县', '陕西省,渭南市,合阳县', 3);
INSERT INTO `sys_region` VALUES ('610525', '6105', '澄城县', '陕西省,渭南市,澄城县', 3);
INSERT INTO `sys_region` VALUES ('610526', '6105', '蒲城县', '陕西省,渭南市,蒲城县', 3);
INSERT INTO `sys_region` VALUES ('610527', '6105', '白水县', '陕西省,渭南市,白水县', 3);
INSERT INTO `sys_region` VALUES ('610528', '6105', '富平县', '陕西省,渭南市,富平县', 3);
INSERT INTO `sys_region` VALUES ('610581', '6105', '韩城市', '陕西省,渭南市,韩城市', 3);
INSERT INTO `sys_region` VALUES ('610582', '6105', '华阴市', '陕西省,渭南市,华阴市', 3);
INSERT INTO `sys_region` VALUES ('6106', '61', '延安市', '陕西省,延安市', 2);
INSERT INTO `sys_region` VALUES ('610602', '6106', '宝塔区', '陕西省,延安市,宝塔区', 3);
INSERT INTO `sys_region` VALUES ('610603', '6106', '安塞区', '陕西省,延安市,安塞区', 3);
INSERT INTO `sys_region` VALUES ('610621', '6106', '延长县', '陕西省,延安市,延长县', 3);
INSERT INTO `sys_region` VALUES ('610622', '6106', '延川县', '陕西省,延安市,延川县', 3);
INSERT INTO `sys_region` VALUES ('610623', '6106', '子长县', '陕西省,延安市,子长县', 3);
INSERT INTO `sys_region` VALUES ('610625', '6106', '志丹县', '陕西省,延安市,志丹县', 3);
INSERT INTO `sys_region` VALUES ('610626', '6106', '吴起县', '陕西省,延安市,吴起县', 3);
INSERT INTO `sys_region` VALUES ('610627', '6106', '甘泉县', '陕西省,延安市,甘泉县', 3);
INSERT INTO `sys_region` VALUES ('610628', '6106', '富县', '陕西省,延安市,富县', 3);
INSERT INTO `sys_region` VALUES ('610629', '6106', '洛川县', '陕西省,延安市,洛川县', 3);
INSERT INTO `sys_region` VALUES ('610630', '6106', '宜川县', '陕西省,延安市,宜川县', 3);
INSERT INTO `sys_region` VALUES ('610631', '6106', '黄龙县', '陕西省,延安市,黄龙县', 3);
INSERT INTO `sys_region` VALUES ('610632', '6106', '黄陵县', '陕西省,延安市,黄陵县', 3);
INSERT INTO `sys_region` VALUES ('6107', '61', '汉中市', '陕西省,汉中市', 2);
INSERT INTO `sys_region` VALUES ('610702', '6107', '汉台区', '陕西省,汉中市,汉台区', 3);
INSERT INTO `sys_region` VALUES ('610703', '6107', '南郑区', '陕西省,汉中市,南郑区', 3);
INSERT INTO `sys_region` VALUES ('610722', '6107', '城固县', '陕西省,汉中市,城固县', 3);
INSERT INTO `sys_region` VALUES ('610723', '6107', '洋县', '陕西省,汉中市,洋县', 3);
INSERT INTO `sys_region` VALUES ('610724', '6107', '西乡县', '陕西省,汉中市,西乡县', 3);
INSERT INTO `sys_region` VALUES ('610725', '6107', '勉县', '陕西省,汉中市,勉县', 3);
INSERT INTO `sys_region` VALUES ('610726', '6107', '宁强县', '陕西省,汉中市,宁强县', 3);
INSERT INTO `sys_region` VALUES ('610727', '6107', '略阳县', '陕西省,汉中市,略阳县', 3);
INSERT INTO `sys_region` VALUES ('610728', '6107', '镇巴县', '陕西省,汉中市,镇巴县', 3);
INSERT INTO `sys_region` VALUES ('610729', '6107', '留坝县', '陕西省,汉中市,留坝县', 3);
INSERT INTO `sys_region` VALUES ('610730', '6107', '佛坪县', '陕西省,汉中市,佛坪县', 3);
INSERT INTO `sys_region` VALUES ('6108', '61', '榆林市', '陕西省,榆林市', 2);
INSERT INTO `sys_region` VALUES ('610802', '6108', '榆阳区', '陕西省,榆林市,榆阳区', 3);
INSERT INTO `sys_region` VALUES ('610803', '6108', '横山区', '陕西省,榆林市,横山区', 3);
INSERT INTO `sys_region` VALUES ('610822', '6108', '府谷县', '陕西省,榆林市,府谷县', 3);
INSERT INTO `sys_region` VALUES ('610824', '6108', '靖边县', '陕西省,榆林市,靖边县', 3);
INSERT INTO `sys_region` VALUES ('610825', '6108', '定边县', '陕西省,榆林市,定边县', 3);
INSERT INTO `sys_region` VALUES ('610826', '6108', '绥德县', '陕西省,榆林市,绥德县', 3);
INSERT INTO `sys_region` VALUES ('610827', '6108', '米脂县', '陕西省,榆林市,米脂县', 3);
INSERT INTO `sys_region` VALUES ('610828', '6108', '佳县', '陕西省,榆林市,佳县', 3);
INSERT INTO `sys_region` VALUES ('610829', '6108', '吴堡县', '陕西省,榆林市,吴堡县', 3);
INSERT INTO `sys_region` VALUES ('610830', '6108', '清涧县', '陕西省,榆林市,清涧县', 3);
INSERT INTO `sys_region` VALUES ('610831', '6108', '子洲县', '陕西省,榆林市,子洲县', 3);
INSERT INTO `sys_region` VALUES ('610881', '6108', '神木市', '陕西省,榆林市,神木市', 3);
INSERT INTO `sys_region` VALUES ('6109', '61', '安康市', '陕西省,安康市', 2);
INSERT INTO `sys_region` VALUES ('610902', '6109', '汉滨区', '陕西省,安康市,汉滨区', 3);
INSERT INTO `sys_region` VALUES ('610921', '6109', '汉阴县', '陕西省,安康市,汉阴县', 3);
INSERT INTO `sys_region` VALUES ('610922', '6109', '石泉县', '陕西省,安康市,石泉县', 3);
INSERT INTO `sys_region` VALUES ('610923', '6109', '宁陕县', '陕西省,安康市,宁陕县', 3);
INSERT INTO `sys_region` VALUES ('610924', '6109', '紫阳县', '陕西省,安康市,紫阳县', 3);
INSERT INTO `sys_region` VALUES ('610925', '6109', '岚皋县', '陕西省,安康市,岚皋县', 3);
INSERT INTO `sys_region` VALUES ('610926', '6109', '平利县', '陕西省,安康市,平利县', 3);
INSERT INTO `sys_region` VALUES ('610927', '6109', '镇坪县', '陕西省,安康市,镇坪县', 3);
INSERT INTO `sys_region` VALUES ('610928', '6109', '旬阳县', '陕西省,安康市,旬阳县', 3);
INSERT INTO `sys_region` VALUES ('610929', '6109', '白河县', '陕西省,安康市,白河县', 3);
INSERT INTO `sys_region` VALUES ('6110', '61', '商洛市', '陕西省,商洛市', 2);
INSERT INTO `sys_region` VALUES ('611002', '6110', '商州区', '陕西省,商洛市,商州区', 3);
INSERT INTO `sys_region` VALUES ('611021', '6110', '洛南县', '陕西省,商洛市,洛南县', 3);
INSERT INTO `sys_region` VALUES ('611022', '6110', '丹凤县', '陕西省,商洛市,丹凤县', 3);
INSERT INTO `sys_region` VALUES ('611023', '6110', '商南县', '陕西省,商洛市,商南县', 3);
INSERT INTO `sys_region` VALUES ('611024', '6110', '山阳县', '陕西省,商洛市,山阳县', 3);
INSERT INTO `sys_region` VALUES ('611025', '6110', '镇安县', '陕西省,商洛市,镇安县', 3);
INSERT INTO `sys_region` VALUES ('611026', '6110', '柞水县', '陕西省,商洛市,柞水县', 3);
INSERT INTO `sys_region` VALUES ('62', '0', '甘肃省', '甘肃省', 1);
INSERT INTO `sys_region` VALUES ('6201', '62', '兰州市', '甘肃省,兰州市', 2);
INSERT INTO `sys_region` VALUES ('620102', '6201', '城关区', '甘肃省,兰州市,城关区', 3);
INSERT INTO `sys_region` VALUES ('620103', '6201', '七里河区', '甘肃省,兰州市,七里河区', 3);
INSERT INTO `sys_region` VALUES ('620104', '6201', '西固区', '甘肃省,兰州市,西固区', 3);
INSERT INTO `sys_region` VALUES ('620105', '6201', '安宁区', '甘肃省,兰州市,安宁区', 3);
INSERT INTO `sys_region` VALUES ('620111', '6201', '红古区', '甘肃省,兰州市,红古区', 3);
INSERT INTO `sys_region` VALUES ('620121', '6201', '永登县', '甘肃省,兰州市,永登县', 3);
INSERT INTO `sys_region` VALUES ('620122', '6201', '皋兰县', '甘肃省,兰州市,皋兰县', 3);
INSERT INTO `sys_region` VALUES ('620123', '6201', '榆中县', '甘肃省,兰州市,榆中县', 3);
INSERT INTO `sys_region` VALUES ('620171', '6201', '兰州新区', '甘肃省,兰州市,兰州新区', 3);
INSERT INTO `sys_region` VALUES ('6202', '62', '嘉峪关市', '甘肃省,嘉峪关市', 2);
INSERT INTO `sys_region` VALUES ('620201100', '6202', '新城镇', '甘肃省,嘉峪关市,新城镇', 3);
INSERT INTO `sys_region` VALUES ('620201101', '6202', '峪泉镇', '甘肃省,嘉峪关市,峪泉镇', 3);
INSERT INTO `sys_region` VALUES ('620201102', '6202', '文殊镇', '甘肃省,嘉峪关市,文殊镇', 3);
INSERT INTO `sys_region` VALUES ('620201401', '6202', '雄关区', '甘肃省,嘉峪关市,雄关区', 3);
INSERT INTO `sys_region` VALUES ('620201402', '6202', '镜铁区', '甘肃省,嘉峪关市,镜铁区', 3);
INSERT INTO `sys_region` VALUES ('620201403', '6202', '长城区', '甘肃省,嘉峪关市,长城区', 3);
INSERT INTO `sys_region` VALUES ('6203', '62', '金昌市', '甘肃省,金昌市', 2);
INSERT INTO `sys_region` VALUES ('620302', '6203', '金川区', '甘肃省,金昌市,金川区', 3);
INSERT INTO `sys_region` VALUES ('620321', '6203', '永昌县', '甘肃省,金昌市,永昌县', 3);
INSERT INTO `sys_region` VALUES ('6204', '62', '白银市', '甘肃省,白银市', 2);
INSERT INTO `sys_region` VALUES ('620402', '6204', '白银区', '甘肃省,白银市,白银区', 3);
INSERT INTO `sys_region` VALUES ('620403', '6204', '平川区', '甘肃省,白银市,平川区', 3);
INSERT INTO `sys_region` VALUES ('620421', '6204', '靖远县', '甘肃省,白银市,靖远县', 3);
INSERT INTO `sys_region` VALUES ('620422', '6204', '会宁县', '甘肃省,白银市,会宁县', 3);
INSERT INTO `sys_region` VALUES ('620423', '6204', '景泰县', '甘肃省,白银市,景泰县', 3);
INSERT INTO `sys_region` VALUES ('6205', '62', '天水市', '甘肃省,天水市', 2);
INSERT INTO `sys_region` VALUES ('620502', '6205', '秦州区', '甘肃省,天水市,秦州区', 3);
INSERT INTO `sys_region` VALUES ('620503', '6205', '麦积区', '甘肃省,天水市,麦积区', 3);
INSERT INTO `sys_region` VALUES ('620521', '6205', '清水县', '甘肃省,天水市,清水县', 3);
INSERT INTO `sys_region` VALUES ('620522', '6205', '秦安县', '甘肃省,天水市,秦安县', 3);
INSERT INTO `sys_region` VALUES ('620523', '6205', '甘谷县', '甘肃省,天水市,甘谷县', 3);
INSERT INTO `sys_region` VALUES ('620524', '6205', '武山县', '甘肃省,天水市,武山县', 3);
INSERT INTO `sys_region` VALUES ('620525', '6205', '张家川回族自治县', '甘肃省,天水市,张家川回族自治县', 3);
INSERT INTO `sys_region` VALUES ('6206', '62', '武威市', '甘肃省,武威市', 2);
INSERT INTO `sys_region` VALUES ('620602', '6206', '凉州区', '甘肃省,武威市,凉州区', 3);
INSERT INTO `sys_region` VALUES ('620621', '6206', '民勤县', '甘肃省,武威市,民勤县', 3);
INSERT INTO `sys_region` VALUES ('620622', '6206', '古浪县', '甘肃省,武威市,古浪县', 3);
INSERT INTO `sys_region` VALUES ('620623', '6206', '天祝藏族自治县', '甘肃省,武威市,天祝藏族自治县', 3);
INSERT INTO `sys_region` VALUES ('6207', '62', '张掖市', '甘肃省,张掖市', 2);
INSERT INTO `sys_region` VALUES ('620702', '6207', '甘州区', '甘肃省,张掖市,甘州区', 3);
INSERT INTO `sys_region` VALUES ('620721', '6207', '肃南裕固族自治县', '甘肃省,张掖市,肃南裕固族自治县', 3);
INSERT INTO `sys_region` VALUES ('620722', '6207', '民乐县', '甘肃省,张掖市,民乐县', 3);
INSERT INTO `sys_region` VALUES ('620723', '6207', '临泽县', '甘肃省,张掖市,临泽县', 3);
INSERT INTO `sys_region` VALUES ('620724', '6207', '高台县', '甘肃省,张掖市,高台县', 3);
INSERT INTO `sys_region` VALUES ('620725', '6207', '山丹县', '甘肃省,张掖市,山丹县', 3);
INSERT INTO `sys_region` VALUES ('6208', '62', '平凉市', '甘肃省,平凉市', 2);
INSERT INTO `sys_region` VALUES ('620802', '6208', '崆峒区', '甘肃省,平凉市,崆峒区', 3);
INSERT INTO `sys_region` VALUES ('620821', '6208', '泾川县', '甘肃省,平凉市,泾川县', 3);
INSERT INTO `sys_region` VALUES ('620822', '6208', '灵台县', '甘肃省,平凉市,灵台县', 3);
INSERT INTO `sys_region` VALUES ('620823', '6208', '崇信县', '甘肃省,平凉市,崇信县', 3);
INSERT INTO `sys_region` VALUES ('620825', '6208', '庄浪县', '甘肃省,平凉市,庄浪县', 3);
INSERT INTO `sys_region` VALUES ('620826', '6208', '静宁县', '甘肃省,平凉市,静宁县', 3);
INSERT INTO `sys_region` VALUES ('620881', '6208', '华亭市', '甘肃省,平凉市,华亭市', 3);
INSERT INTO `sys_region` VALUES ('6209', '62', '酒泉市', '甘肃省,酒泉市', 2);
INSERT INTO `sys_region` VALUES ('620902', '6209', '肃州区', '甘肃省,酒泉市,肃州区', 3);
INSERT INTO `sys_region` VALUES ('620921', '6209', '金塔县', '甘肃省,酒泉市,金塔县', 3);
INSERT INTO `sys_region` VALUES ('620922', '6209', '瓜州县', '甘肃省,酒泉市,瓜州县', 3);
INSERT INTO `sys_region` VALUES ('620923', '6209', '肃北蒙古族自治县', '甘肃省,酒泉市,肃北蒙古族自治县', 3);
INSERT INTO `sys_region` VALUES ('620924', '6209', '阿克塞哈萨克族自治县', '甘肃省,酒泉市,阿克塞哈萨克族自治县', 3);
INSERT INTO `sys_region` VALUES ('620981', '6209', '玉门市', '甘肃省,酒泉市,玉门市', 3);
INSERT INTO `sys_region` VALUES ('620982', '6209', '敦煌市', '甘肃省,酒泉市,敦煌市', 3);
INSERT INTO `sys_region` VALUES ('6210', '62', '庆阳市', '甘肃省,庆阳市', 2);
INSERT INTO `sys_region` VALUES ('621002', '6210', '西峰区', '甘肃省,庆阳市,西峰区', 3);
INSERT INTO `sys_region` VALUES ('621021', '6210', '庆城县', '甘肃省,庆阳市,庆城县', 3);
INSERT INTO `sys_region` VALUES ('621022', '6210', '环县', '甘肃省,庆阳市,环县', 3);
INSERT INTO `sys_region` VALUES ('621023', '6210', '华池县', '甘肃省,庆阳市,华池县', 3);
INSERT INTO `sys_region` VALUES ('621024', '6210', '合水县', '甘肃省,庆阳市,合水县', 3);
INSERT INTO `sys_region` VALUES ('621025', '6210', '正宁县', '甘肃省,庆阳市,正宁县', 3);
INSERT INTO `sys_region` VALUES ('621026', '6210', '宁县', '甘肃省,庆阳市,宁县', 3);
INSERT INTO `sys_region` VALUES ('621027', '6210', '镇原县', '甘肃省,庆阳市,镇原县', 3);
INSERT INTO `sys_region` VALUES ('6211', '62', '定西市', '甘肃省,定西市', 2);
INSERT INTO `sys_region` VALUES ('621102', '6211', '安定区', '甘肃省,定西市,安定区', 3);
INSERT INTO `sys_region` VALUES ('621121', '6211', '通渭县', '甘肃省,定西市,通渭县', 3);
INSERT INTO `sys_region` VALUES ('621122', '6211', '陇西县', '甘肃省,定西市,陇西县', 3);
INSERT INTO `sys_region` VALUES ('621123', '6211', '渭源县', '甘肃省,定西市,渭源县', 3);
INSERT INTO `sys_region` VALUES ('621124', '6211', '临洮县', '甘肃省,定西市,临洮县', 3);
INSERT INTO `sys_region` VALUES ('621125', '6211', '漳县', '甘肃省,定西市,漳县', 3);
INSERT INTO `sys_region` VALUES ('621126', '6211', '岷县', '甘肃省,定西市,岷县', 3);
INSERT INTO `sys_region` VALUES ('6212', '62', '陇南市', '甘肃省,陇南市', 2);
INSERT INTO `sys_region` VALUES ('621202', '6212', '武都区', '甘肃省,陇南市,武都区', 3);
INSERT INTO `sys_region` VALUES ('621221', '6212', '成县', '甘肃省,陇南市,成县', 3);
INSERT INTO `sys_region` VALUES ('621222', '6212', '文县', '甘肃省,陇南市,文县', 3);
INSERT INTO `sys_region` VALUES ('621223', '6212', '宕昌县', '甘肃省,陇南市,宕昌县', 3);
INSERT INTO `sys_region` VALUES ('621224', '6212', '康县', '甘肃省,陇南市,康县', 3);
INSERT INTO `sys_region` VALUES ('621225', '6212', '西和县', '甘肃省,陇南市,西和县', 3);
INSERT INTO `sys_region` VALUES ('621226', '6212', '礼县', '甘肃省,陇南市,礼县', 3);
INSERT INTO `sys_region` VALUES ('621227', '6212', '徽县', '甘肃省,陇南市,徽县', 3);
INSERT INTO `sys_region` VALUES ('621228', '6212', '两当县', '甘肃省,陇南市,两当县', 3);
INSERT INTO `sys_region` VALUES ('6229', '62', '临夏回族自治州', '甘肃省,临夏回族自治州', 2);
INSERT INTO `sys_region` VALUES ('622901', '6229', '临夏市', '甘肃省,临夏回族自治州,临夏市', 3);
INSERT INTO `sys_region` VALUES ('622921', '6229', '临夏县', '甘肃省,临夏回族自治州,临夏县', 3);
INSERT INTO `sys_region` VALUES ('622922', '6229', '康乐县', '甘肃省,临夏回族自治州,康乐县', 3);
INSERT INTO `sys_region` VALUES ('622923', '6229', '永靖县', '甘肃省,临夏回族自治州,永靖县', 3);
INSERT INTO `sys_region` VALUES ('622924', '6229', '广河县', '甘肃省,临夏回族自治州,广河县', 3);
INSERT INTO `sys_region` VALUES ('622925', '6229', '和政县', '甘肃省,临夏回族自治州,和政县', 3);
INSERT INTO `sys_region` VALUES ('622926', '6229', '东乡族自治县', '甘肃省,临夏回族自治州,东乡族自治县', 3);
INSERT INTO `sys_region` VALUES ('622927', '6229', '积石山保安族东乡族撒拉族自治县', '甘肃省,临夏回族自治州,积石山保安族东乡族撒拉族自治县', 3);
INSERT INTO `sys_region` VALUES ('6230', '62', '甘南藏族自治州', '甘肃省,甘南藏族自治州', 2);
INSERT INTO `sys_region` VALUES ('623001', '6230', '合作市', '甘肃省,甘南藏族自治州,合作市', 3);
INSERT INTO `sys_region` VALUES ('623021', '6230', '临潭县', '甘肃省,甘南藏族自治州,临潭县', 3);
INSERT INTO `sys_region` VALUES ('623022', '6230', '卓尼县', '甘肃省,甘南藏族自治州,卓尼县', 3);
INSERT INTO `sys_region` VALUES ('623023', '6230', '舟曲县', '甘肃省,甘南藏族自治州,舟曲县', 3);
INSERT INTO `sys_region` VALUES ('623024', '6230', '迭部县', '甘肃省,甘南藏族自治州,迭部县', 3);
INSERT INTO `sys_region` VALUES ('623025', '6230', '玛曲县', '甘肃省,甘南藏族自治州,玛曲县', 3);
INSERT INTO `sys_region` VALUES ('623026', '6230', '碌曲县', '甘肃省,甘南藏族自治州,碌曲县', 3);
INSERT INTO `sys_region` VALUES ('623027', '6230', '夏河县', '甘肃省,甘南藏族自治州,夏河县', 3);
INSERT INTO `sys_region` VALUES ('63', '0', '青海省', '青海省', 1);
INSERT INTO `sys_region` VALUES ('6301', '63', '西宁市', '青海省,西宁市', 2);
INSERT INTO `sys_region` VALUES ('630102', '6301', '城东区', '青海省,西宁市,城东区', 3);
INSERT INTO `sys_region` VALUES ('630103', '6301', '城中区', '青海省,西宁市,城中区', 3);
INSERT INTO `sys_region` VALUES ('630104', '6301', '城西区', '青海省,西宁市,城西区', 3);
INSERT INTO `sys_region` VALUES ('630105', '6301', '城北区', '青海省,西宁市,城北区', 3);
INSERT INTO `sys_region` VALUES ('630121', '6301', '大通回族土族自治县', '青海省,西宁市,大通回族土族自治县', 3);
INSERT INTO `sys_region` VALUES ('630122', '6301', '湟中县', '青海省,西宁市,湟中县', 3);
INSERT INTO `sys_region` VALUES ('630123', '6301', '湟源县', '青海省,西宁市,湟源县', 3);
INSERT INTO `sys_region` VALUES ('6302', '63', '海东市', '青海省,海东市', 2);
INSERT INTO `sys_region` VALUES ('630202', '6302', '乐都区', '青海省,海东市,乐都区', 3);
INSERT INTO `sys_region` VALUES ('630203', '6302', '平安区', '青海省,海东市,平安区', 3);
INSERT INTO `sys_region` VALUES ('630222', '6302', '民和回族土族自治县', '青海省,海东市,民和回族土族自治县', 3);
INSERT INTO `sys_region` VALUES ('630223', '6302', '互助土族自治县', '青海省,海东市,互助土族自治县', 3);
INSERT INTO `sys_region` VALUES ('630224', '6302', '化隆回族自治县', '青海省,海东市,化隆回族自治县', 3);
INSERT INTO `sys_region` VALUES ('630225', '6302', '循化撒拉族自治县', '青海省,海东市,循化撒拉族自治县', 3);
INSERT INTO `sys_region` VALUES ('6322', '63', '海北藏族自治州', '青海省,海北藏族自治州', 2);
INSERT INTO `sys_region` VALUES ('632221', '6322', '门源回族自治县', '青海省,海北藏族自治州,门源回族自治县', 3);
INSERT INTO `sys_region` VALUES ('632222', '6322', '祁连县', '青海省,海北藏族自治州,祁连县', 3);
INSERT INTO `sys_region` VALUES ('632223', '6322', '海晏县', '青海省,海北藏族自治州,海晏县', 3);
INSERT INTO `sys_region` VALUES ('632224', '6322', '刚察县', '青海省,海北藏族自治州,刚察县', 3);
INSERT INTO `sys_region` VALUES ('6323', '63', '黄南藏族自治州', '青海省,黄南藏族自治州', 2);
INSERT INTO `sys_region` VALUES ('632321', '6323', '同仁县', '青海省,黄南藏族自治州,同仁县', 3);
INSERT INTO `sys_region` VALUES ('632322', '6323', '尖扎县', '青海省,黄南藏族自治州,尖扎县', 3);
INSERT INTO `sys_region` VALUES ('632323', '6323', '泽库县', '青海省,黄南藏族自治州,泽库县', 3);
INSERT INTO `sys_region` VALUES ('632324', '6323', '河南蒙古族自治县', '青海省,黄南藏族自治州,河南蒙古族自治县', 3);
INSERT INTO `sys_region` VALUES ('6325', '63', '海南藏族自治州', '青海省,海南藏族自治州', 2);
INSERT INTO `sys_region` VALUES ('632521', '6325', '共和县', '青海省,海南藏族自治州,共和县', 3);
INSERT INTO `sys_region` VALUES ('632522', '6325', '同德县', '青海省,海南藏族自治州,同德县', 3);
INSERT INTO `sys_region` VALUES ('632523', '6325', '贵德县', '青海省,海南藏族自治州,贵德县', 3);
INSERT INTO `sys_region` VALUES ('632524', '6325', '兴海县', '青海省,海南藏族自治州,兴海县', 3);
INSERT INTO `sys_region` VALUES ('632525', '6325', '贵南县', '青海省,海南藏族自治州,贵南县', 3);
INSERT INTO `sys_region` VALUES ('6326', '63', '果洛藏族自治州', '青海省,果洛藏族自治州', 2);
INSERT INTO `sys_region` VALUES ('632621', '6326', '玛沁县', '青海省,果洛藏族自治州,玛沁县', 3);
INSERT INTO `sys_region` VALUES ('632622', '6326', '班玛县', '青海省,果洛藏族自治州,班玛县', 3);
INSERT INTO `sys_region` VALUES ('632623', '6326', '甘德县', '青海省,果洛藏族自治州,甘德县', 3);
INSERT INTO `sys_region` VALUES ('632624', '6326', '达日县', '青海省,果洛藏族自治州,达日县', 3);
INSERT INTO `sys_region` VALUES ('632625', '6326', '久治县', '青海省,果洛藏族自治州,久治县', 3);
INSERT INTO `sys_region` VALUES ('632626', '6326', '玛多县', '青海省,果洛藏族自治州,玛多县', 3);
INSERT INTO `sys_region` VALUES ('6327', '63', '玉树藏族自治州', '青海省,玉树藏族自治州', 2);
INSERT INTO `sys_region` VALUES ('632701', '6327', '玉树市', '青海省,玉树藏族自治州,玉树市', 3);
INSERT INTO `sys_region` VALUES ('632722', '6327', '杂多县', '青海省,玉树藏族自治州,杂多县', 3);
INSERT INTO `sys_region` VALUES ('632723', '6327', '称多县', '青海省,玉树藏族自治州,称多县', 3);
INSERT INTO `sys_region` VALUES ('632724', '6327', '治多县', '青海省,玉树藏族自治州,治多县', 3);
INSERT INTO `sys_region` VALUES ('632725', '6327', '囊谦县', '青海省,玉树藏族自治州,囊谦县', 3);
INSERT INTO `sys_region` VALUES ('632726', '6327', '曲麻莱县', '青海省,玉树藏族自治州,曲麻莱县', 3);
INSERT INTO `sys_region` VALUES ('6328', '63', '海西蒙古族藏族自治州', '青海省,海西蒙古族藏族自治州', 2);
INSERT INTO `sys_region` VALUES ('632801', '6328', '格尔木市', '青海省,海西蒙古族藏族自治州,格尔木市', 3);
INSERT INTO `sys_region` VALUES ('632802', '6328', '德令哈市', '青海省,海西蒙古族藏族自治州,德令哈市', 3);
INSERT INTO `sys_region` VALUES ('632803', '6328', '茫崖市', '青海省,海西蒙古族藏族自治州,茫崖市', 3);
INSERT INTO `sys_region` VALUES ('632821', '6328', '乌兰县', '青海省,海西蒙古族藏族自治州,乌兰县', 3);
INSERT INTO `sys_region` VALUES ('632822', '6328', '都兰县', '青海省,海西蒙古族藏族自治州,都兰县', 3);
INSERT INTO `sys_region` VALUES ('632823', '6328', '天峻县', '青海省,海西蒙古族藏族自治州,天峻县', 3);
INSERT INTO `sys_region` VALUES ('632857', '6328', '大柴旦行政委员会', '青海省,海西蒙古族藏族自治州,大柴旦行政委员会', 3);
INSERT INTO `sys_region` VALUES ('64', '0', '宁夏回族自治区', '宁夏回族自治区', 1);
INSERT INTO `sys_region` VALUES ('6401', '64', '银川市', '宁夏回族自治区,银川市', 2);
INSERT INTO `sys_region` VALUES ('640104', '6401', '兴庆区', '宁夏回族自治区,银川市,兴庆区', 3);
INSERT INTO `sys_region` VALUES ('640105', '6401', '西夏区', '宁夏回族自治区,银川市,西夏区', 3);
INSERT INTO `sys_region` VALUES ('640106', '6401', '金凤区', '宁夏回族自治区,银川市,金凤区', 3);
INSERT INTO `sys_region` VALUES ('640121', '6401', '永宁县', '宁夏回族自治区,银川市,永宁县', 3);
INSERT INTO `sys_region` VALUES ('640122', '6401', '贺兰县', '宁夏回族自治区,银川市,贺兰县', 3);
INSERT INTO `sys_region` VALUES ('640181', '6401', '灵武市', '宁夏回族自治区,银川市,灵武市', 3);
INSERT INTO `sys_region` VALUES ('6402', '64', '石嘴山市', '宁夏回族自治区,石嘴山市', 2);
INSERT INTO `sys_region` VALUES ('640202', '6402', '大武口区', '宁夏回族自治区,石嘴山市,大武口区', 3);
INSERT INTO `sys_region` VALUES ('640205', '6402', '惠农区', '宁夏回族自治区,石嘴山市,惠农区', 3);
INSERT INTO `sys_region` VALUES ('640221', '6402', '平罗县', '宁夏回族自治区,石嘴山市,平罗县', 3);
INSERT INTO `sys_region` VALUES ('6403', '64', '吴忠市', '宁夏回族自治区,吴忠市', 2);
INSERT INTO `sys_region` VALUES ('640302', '6403', '利通区', '宁夏回族自治区,吴忠市,利通区', 3);
INSERT INTO `sys_region` VALUES ('640303', '6403', '红寺堡区', '宁夏回族自治区,吴忠市,红寺堡区', 3);
INSERT INTO `sys_region` VALUES ('640323', '6403', '盐池县', '宁夏回族自治区,吴忠市,盐池县', 3);
INSERT INTO `sys_region` VALUES ('640324', '6403', '同心县', '宁夏回族自治区,吴忠市,同心县', 3);
INSERT INTO `sys_region` VALUES ('640381', '6403', '青铜峡市', '宁夏回族自治区,吴忠市,青铜峡市', 3);
INSERT INTO `sys_region` VALUES ('6404', '64', '固原市', '宁夏回族自治区,固原市', 2);
INSERT INTO `sys_region` VALUES ('640402', '6404', '原州区', '宁夏回族自治区,固原市,原州区', 3);
INSERT INTO `sys_region` VALUES ('640422', '6404', '西吉县', '宁夏回族自治区,固原市,西吉县', 3);
INSERT INTO `sys_region` VALUES ('640423', '6404', '隆德县', '宁夏回族自治区,固原市,隆德县', 3);
INSERT INTO `sys_region` VALUES ('640424', '6404', '泾源县', '宁夏回族自治区,固原市,泾源县', 3);
INSERT INTO `sys_region` VALUES ('640425', '6404', '彭阳县', '宁夏回族自治区,固原市,彭阳县', 3);
INSERT INTO `sys_region` VALUES ('6405', '64', '中卫市', '宁夏回族自治区,中卫市', 2);
INSERT INTO `sys_region` VALUES ('640502', '6405', '沙坡头区', '宁夏回族自治区,中卫市,沙坡头区', 3);
INSERT INTO `sys_region` VALUES ('640521', '6405', '中宁县', '宁夏回族自治区,中卫市,中宁县', 3);
INSERT INTO `sys_region` VALUES ('640522', '6405', '海原县', '宁夏回族自治区,中卫市,海原县', 3);
INSERT INTO `sys_region` VALUES ('65', '0', '新疆维吾尔自治区', '新疆维吾尔自治区', 1);
INSERT INTO `sys_region` VALUES ('6501', '65', '乌鲁木齐市', '新疆维吾尔自治区,乌鲁木齐市', 2);
INSERT INTO `sys_region` VALUES ('650102', '6501', '天山区', '新疆维吾尔自治区,乌鲁木齐市,天山区', 3);
INSERT INTO `sys_region` VALUES ('650103', '6501', '沙依巴克区', '新疆维吾尔自治区,乌鲁木齐市,沙依巴克区', 3);
INSERT INTO `sys_region` VALUES ('650104', '6501', '新市区', '新疆维吾尔自治区,乌鲁木齐市,新市区', 3);
INSERT INTO `sys_region` VALUES ('650105', '6501', '水磨沟区', '新疆维吾尔自治区,乌鲁木齐市,水磨沟区', 3);
INSERT INTO `sys_region` VALUES ('650106', '6501', '头屯河区', '新疆维吾尔自治区,乌鲁木齐市,头屯河区', 3);
INSERT INTO `sys_region` VALUES ('650107', '6501', '达坂城区', '新疆维吾尔自治区,乌鲁木齐市,达坂城区', 3);
INSERT INTO `sys_region` VALUES ('650109', '6501', '米东区', '新疆维吾尔自治区,乌鲁木齐市,米东区', 3);
INSERT INTO `sys_region` VALUES ('650121', '6501', '乌鲁木齐县', '新疆维吾尔自治区,乌鲁木齐市,乌鲁木齐县', 3);
INSERT INTO `sys_region` VALUES ('650171', '6501', '乌鲁木齐经济技术开发区', '新疆维吾尔自治区,乌鲁木齐市,乌鲁木齐经济技术开发区', 3);
INSERT INTO `sys_region` VALUES ('650172', '6501', '乌鲁木齐高新技术产业开发区', '新疆维吾尔自治区,乌鲁木齐市,乌鲁木齐高新技术产业开发区', 3);
INSERT INTO `sys_region` VALUES ('6502', '65', '克拉玛依市', '新疆维吾尔自治区,克拉玛依市', 2);
INSERT INTO `sys_region` VALUES ('650202', '6502', '独山子区', '新疆维吾尔自治区,克拉玛依市,独山子区', 3);
INSERT INTO `sys_region` VALUES ('650203', '6502', '克拉玛依区', '新疆维吾尔自治区,克拉玛依市,克拉玛依区', 3);
INSERT INTO `sys_region` VALUES ('650204', '6502', '白碱滩区', '新疆维吾尔自治区,克拉玛依市,白碱滩区', 3);
INSERT INTO `sys_region` VALUES ('650205', '6502', '乌尔禾区', '新疆维吾尔自治区,克拉玛依市,乌尔禾区', 3);
INSERT INTO `sys_region` VALUES ('6504', '65', '吐鲁番市', '新疆维吾尔自治区,吐鲁番市', 2);
INSERT INTO `sys_region` VALUES ('650402', '6504', '高昌区', '新疆维吾尔自治区,吐鲁番市,高昌区', 3);
INSERT INTO `sys_region` VALUES ('650421', '6504', '鄯善县', '新疆维吾尔自治区,吐鲁番市,鄯善县', 3);
INSERT INTO `sys_region` VALUES ('650422', '6504', '托克逊县', '新疆维吾尔自治区,吐鲁番市,托克逊县', 3);
INSERT INTO `sys_region` VALUES ('6505', '65', '哈密市', '新疆维吾尔自治区,哈密市', 2);
INSERT INTO `sys_region` VALUES ('650502', '6505', '伊州区', '新疆维吾尔自治区,哈密市,伊州区', 3);
INSERT INTO `sys_region` VALUES ('650521', '6505', '巴里坤哈萨克自治县', '新疆维吾尔自治区,哈密市,巴里坤哈萨克自治县', 3);
INSERT INTO `sys_region` VALUES ('650522', '6505', '伊吾县', '新疆维吾尔自治区,哈密市,伊吾县', 3);
INSERT INTO `sys_region` VALUES ('6523', '65', '昌吉回族自治州', '新疆维吾尔自治区,昌吉回族自治州', 2);
INSERT INTO `sys_region` VALUES ('652301', '6523', '昌吉市', '新疆维吾尔自治区,昌吉回族自治州,昌吉市', 3);
INSERT INTO `sys_region` VALUES ('652302', '6523', '阜康市', '新疆维吾尔自治区,昌吉回族自治州,阜康市', 3);
INSERT INTO `sys_region` VALUES ('652323', '6523', '呼图壁县', '新疆维吾尔自治区,昌吉回族自治州,呼图壁县', 3);
INSERT INTO `sys_region` VALUES ('652324', '6523', '玛纳斯县', '新疆维吾尔自治区,昌吉回族自治州,玛纳斯县', 3);
INSERT INTO `sys_region` VALUES ('652325', '6523', '奇台县', '新疆维吾尔自治区,昌吉回族自治州,奇台县', 3);
INSERT INTO `sys_region` VALUES ('652327', '6523', '吉木萨尔县', '新疆维吾尔自治区,昌吉回族自治州,吉木萨尔县', 3);
INSERT INTO `sys_region` VALUES ('652328', '6523', '木垒哈萨克自治县', '新疆维吾尔自治区,昌吉回族自治州,木垒哈萨克自治县', 3);
INSERT INTO `sys_region` VALUES ('6527', '65', '博尔塔拉蒙古自治州', '新疆维吾尔自治区,博尔塔拉蒙古自治州', 2);
INSERT INTO `sys_region` VALUES ('652701', '6527', '博乐市', '新疆维吾尔自治区,博尔塔拉蒙古自治州,博乐市', 3);
INSERT INTO `sys_region` VALUES ('652702', '6527', '阿拉山口市', '新疆维吾尔自治区,博尔塔拉蒙古自治州,阿拉山口市', 3);
INSERT INTO `sys_region` VALUES ('652722', '6527', '精河县', '新疆维吾尔自治区,博尔塔拉蒙古自治州,精河县', 3);
INSERT INTO `sys_region` VALUES ('652723', '6527', '温泉县', '新疆维吾尔自治区,博尔塔拉蒙古自治州,温泉县', 3);
INSERT INTO `sys_region` VALUES ('6528', '65', '巴音郭楞蒙古自治州', '新疆维吾尔自治区,巴音郭楞蒙古自治州', 2);
INSERT INTO `sys_region` VALUES ('652801', '6528', '库尔勒市', '新疆维吾尔自治区,巴音郭楞蒙古自治州,库尔勒市', 3);
INSERT INTO `sys_region` VALUES ('652822', '6528', '轮台县', '新疆维吾尔自治区,巴音郭楞蒙古自治州,轮台县', 3);
INSERT INTO `sys_region` VALUES ('652823', '6528', '尉犁县', '新疆维吾尔自治区,巴音郭楞蒙古自治州,尉犁县', 3);
INSERT INTO `sys_region` VALUES ('652824', '6528', '若羌县', '新疆维吾尔自治区,巴音郭楞蒙古自治州,若羌县', 3);
INSERT INTO `sys_region` VALUES ('652825', '6528', '且末县', '新疆维吾尔自治区,巴音郭楞蒙古自治州,且末县', 3);
INSERT INTO `sys_region` VALUES ('652826', '6528', '焉耆回族自治县', '新疆维吾尔自治区,巴音郭楞蒙古自治州,焉耆回族自治县', 3);
INSERT INTO `sys_region` VALUES ('652827', '6528', '和静县', '新疆维吾尔自治区,巴音郭楞蒙古自治州,和静县', 3);
INSERT INTO `sys_region` VALUES ('652828', '6528', '和硕县', '新疆维吾尔自治区,巴音郭楞蒙古自治州,和硕县', 3);
INSERT INTO `sys_region` VALUES ('652829', '6528', '博湖县', '新疆维吾尔自治区,巴音郭楞蒙古自治州,博湖县', 3);
INSERT INTO `sys_region` VALUES ('652871', '6528', '库尔勒经济技术开发区', '新疆维吾尔自治区,巴音郭楞蒙古自治州,库尔勒经济技术开发区', 3);
INSERT INTO `sys_region` VALUES ('6529', '65', '阿克苏地区', '新疆维吾尔自治区,阿克苏地区', 2);
INSERT INTO `sys_region` VALUES ('652901', '6529', '阿克苏市', '新疆维吾尔自治区,阿克苏地区,阿克苏市', 3);
INSERT INTO `sys_region` VALUES ('652922', '6529', '温宿县', '新疆维吾尔自治区,阿克苏地区,温宿县', 3);
INSERT INTO `sys_region` VALUES ('652923', '6529', '库车县', '新疆维吾尔自治区,阿克苏地区,库车县', 3);
INSERT INTO `sys_region` VALUES ('652924', '6529', '沙雅县', '新疆维吾尔自治区,阿克苏地区,沙雅县', 3);
INSERT INTO `sys_region` VALUES ('652925', '6529', '新和县', '新疆维吾尔自治区,阿克苏地区,新和县', 3);
INSERT INTO `sys_region` VALUES ('652926', '6529', '拜城县', '新疆维吾尔自治区,阿克苏地区,拜城县', 3);
INSERT INTO `sys_region` VALUES ('652927', '6529', '乌什县', '新疆维吾尔自治区,阿克苏地区,乌什县', 3);
INSERT INTO `sys_region` VALUES ('652928', '6529', '阿瓦提县', '新疆维吾尔自治区,阿克苏地区,阿瓦提县', 3);
INSERT INTO `sys_region` VALUES ('652929', '6529', '柯坪县', '新疆维吾尔自治区,阿克苏地区,柯坪县', 3);
INSERT INTO `sys_region` VALUES ('6530', '65', '克孜勒苏柯尔克孜自治州', '新疆维吾尔自治区,克孜勒苏柯尔克孜自治州', 2);
INSERT INTO `sys_region` VALUES ('653001', '6530', '阿图什市', '新疆维吾尔自治区,克孜勒苏柯尔克孜自治州,阿图什市', 3);
INSERT INTO `sys_region` VALUES ('653022', '6530', '阿克陶县', '新疆维吾尔自治区,克孜勒苏柯尔克孜自治州,阿克陶县', 3);
INSERT INTO `sys_region` VALUES ('653023', '6530', '阿合奇县', '新疆维吾尔自治区,克孜勒苏柯尔克孜自治州,阿合奇县', 3);
INSERT INTO `sys_region` VALUES ('653024', '6530', '乌恰县', '新疆维吾尔自治区,克孜勒苏柯尔克孜自治州,乌恰县', 3);
INSERT INTO `sys_region` VALUES ('6531', '65', '喀什地区', '新疆维吾尔自治区,喀什地区', 2);
INSERT INTO `sys_region` VALUES ('653101', '6531', '喀什市', '新疆维吾尔自治区,喀什地区,喀什市', 3);
INSERT INTO `sys_region` VALUES ('653121', '6531', '疏附县', '新疆维吾尔自治区,喀什地区,疏附县', 3);
INSERT INTO `sys_region` VALUES ('653122', '6531', '疏勒县', '新疆维吾尔自治区,喀什地区,疏勒县', 3);
INSERT INTO `sys_region` VALUES ('653123', '6531', '英吉沙县', '新疆维吾尔自治区,喀什地区,英吉沙县', 3);
INSERT INTO `sys_region` VALUES ('653124', '6531', '泽普县', '新疆维吾尔自治区,喀什地区,泽普县', 3);
INSERT INTO `sys_region` VALUES ('653125', '6531', '莎车县', '新疆维吾尔自治区,喀什地区,莎车县', 3);
INSERT INTO `sys_region` VALUES ('653126', '6531', '叶城县', '新疆维吾尔自治区,喀什地区,叶城县', 3);
INSERT INTO `sys_region` VALUES ('653127', '6531', '麦盖提县', '新疆维吾尔自治区,喀什地区,麦盖提县', 3);
INSERT INTO `sys_region` VALUES ('653128', '6531', '岳普湖县', '新疆维吾尔自治区,喀什地区,岳普湖县', 3);
INSERT INTO `sys_region` VALUES ('653129', '6531', '伽师县', '新疆维吾尔自治区,喀什地区,伽师县', 3);
INSERT INTO `sys_region` VALUES ('653130', '6531', '巴楚县', '新疆维吾尔自治区,喀什地区,巴楚县', 3);
INSERT INTO `sys_region` VALUES ('653131', '6531', '塔什库尔干塔吉克自治县', '新疆维吾尔自治区,喀什地区,塔什库尔干塔吉克自治县', 3);
INSERT INTO `sys_region` VALUES ('6532', '65', '和田地区', '新疆维吾尔自治区,和田地区', 2);
INSERT INTO `sys_region` VALUES ('653201', '6532', '和田市', '新疆维吾尔自治区,和田地区,和田市', 3);
INSERT INTO `sys_region` VALUES ('653221', '6532', '和田县', '新疆维吾尔自治区,和田地区,和田县', 3);
INSERT INTO `sys_region` VALUES ('653222', '6532', '墨玉县', '新疆维吾尔自治区,和田地区,墨玉县', 3);
INSERT INTO `sys_region` VALUES ('653223', '6532', '皮山县', '新疆维吾尔自治区,和田地区,皮山县', 3);
INSERT INTO `sys_region` VALUES ('653224', '6532', '洛浦县', '新疆维吾尔自治区,和田地区,洛浦县', 3);
INSERT INTO `sys_region` VALUES ('653225', '6532', '策勒县', '新疆维吾尔自治区,和田地区,策勒县', 3);
INSERT INTO `sys_region` VALUES ('653226', '6532', '于田县', '新疆维吾尔自治区,和田地区,于田县', 3);
INSERT INTO `sys_region` VALUES ('653227', '6532', '民丰县', '新疆维吾尔自治区,和田地区,民丰县', 3);
INSERT INTO `sys_region` VALUES ('6540', '65', '伊犁哈萨克自治州', '新疆维吾尔自治区,伊犁哈萨克自治州', 2);
INSERT INTO `sys_region` VALUES ('654002', '6540', '伊宁市', '新疆维吾尔自治区,伊犁哈萨克自治州,伊宁市', 3);
INSERT INTO `sys_region` VALUES ('654003', '6540', '奎屯市', '新疆维吾尔自治区,伊犁哈萨克自治州,奎屯市', 3);
INSERT INTO `sys_region` VALUES ('654004', '6540', '霍尔果斯市', '新疆维吾尔自治区,伊犁哈萨克自治州,霍尔果斯市', 3);
INSERT INTO `sys_region` VALUES ('654021', '6540', '伊宁县', '新疆维吾尔自治区,伊犁哈萨克自治州,伊宁县', 3);
INSERT INTO `sys_region` VALUES ('654022', '6540', '察布查尔锡伯自治县', '新疆维吾尔自治区,伊犁哈萨克自治州,察布查尔锡伯自治县', 3);
INSERT INTO `sys_region` VALUES ('654023', '6540', '霍城县', '新疆维吾尔自治区,伊犁哈萨克自治州,霍城县', 3);
INSERT INTO `sys_region` VALUES ('654024', '6540', '巩留县', '新疆维吾尔自治区,伊犁哈萨克自治州,巩留县', 3);
INSERT INTO `sys_region` VALUES ('654025', '6540', '新源县', '新疆维吾尔自治区,伊犁哈萨克自治州,新源县', 3);
INSERT INTO `sys_region` VALUES ('654026', '6540', '昭苏县', '新疆维吾尔自治区,伊犁哈萨克自治州,昭苏县', 3);
INSERT INTO `sys_region` VALUES ('654027', '6540', '特克斯县', '新疆维吾尔自治区,伊犁哈萨克自治州,特克斯县', 3);
INSERT INTO `sys_region` VALUES ('654028', '6540', '尼勒克县', '新疆维吾尔自治区,伊犁哈萨克自治州,尼勒克县', 3);
INSERT INTO `sys_region` VALUES ('6542', '65', '塔城地区', '新疆维吾尔自治区,塔城地区', 2);
INSERT INTO `sys_region` VALUES ('654201', '6542', '塔城市', '新疆维吾尔自治区,塔城地区,塔城市', 3);
INSERT INTO `sys_region` VALUES ('654202', '6542', '乌苏市', '新疆维吾尔自治区,塔城地区,乌苏市', 3);
INSERT INTO `sys_region` VALUES ('654221', '6542', '额敏县', '新疆维吾尔自治区,塔城地区,额敏县', 3);
INSERT INTO `sys_region` VALUES ('654223', '6542', '沙湾县', '新疆维吾尔自治区,塔城地区,沙湾县', 3);
INSERT INTO `sys_region` VALUES ('654224', '6542', '托里县', '新疆维吾尔自治区,塔城地区,托里县', 3);
INSERT INTO `sys_region` VALUES ('654225', '6542', '裕民县', '新疆维吾尔自治区,塔城地区,裕民县', 3);
INSERT INTO `sys_region` VALUES ('654226', '6542', '和布克赛尔蒙古自治县', '新疆维吾尔自治区,塔城地区,和布克赛尔蒙古自治县', 3);
INSERT INTO `sys_region` VALUES ('6543', '65', '阿勒泰地区', '新疆维吾尔自治区,阿勒泰地区', 2);
INSERT INTO `sys_region` VALUES ('654301', '6543', '阿勒泰市', '新疆维吾尔自治区,阿勒泰地区,阿勒泰市', 3);
INSERT INTO `sys_region` VALUES ('654321', '6543', '布尔津县', '新疆维吾尔自治区,阿勒泰地区,布尔津县', 3);
INSERT INTO `sys_region` VALUES ('654322', '6543', '富蕴县', '新疆维吾尔自治区,阿勒泰地区,富蕴县', 3);
INSERT INTO `sys_region` VALUES ('654323', '6543', '福海县', '新疆维吾尔自治区,阿勒泰地区,福海县', 3);
INSERT INTO `sys_region` VALUES ('654324', '6543', '哈巴河县', '新疆维吾尔自治区,阿勒泰地区,哈巴河县', 3);
INSERT INTO `sys_region` VALUES ('654325', '6543', '青河县', '新疆维吾尔自治区,阿勒泰地区,青河县', 3);
INSERT INTO `sys_region` VALUES ('654326', '6543', '吉木乃县', '新疆维吾尔自治区,阿勒泰地区,吉木乃县', 3);
INSERT INTO `sys_region` VALUES ('6590', '65', '自治区直辖县级行政区划', '新疆维吾尔自治区,自治区直辖县级行政区划', 2);
INSERT INTO `sys_region` VALUES ('659001', '6590', '石河子市', '新疆维吾尔自治区,自治区直辖县级行政区划,石河子市', 3);
INSERT INTO `sys_region` VALUES ('659002', '6590', '阿拉尔市', '新疆维吾尔自治区,自治区直辖县级行政区划,阿拉尔市', 3);
INSERT INTO `sys_region` VALUES ('659003', '6590', '图木舒克市', '新疆维吾尔自治区,自治区直辖县级行政区划,图木舒克市', 3);
INSERT INTO `sys_region` VALUES ('659004', '6590', '五家渠市', '新疆维吾尔自治区,自治区直辖县级行政区划,五家渠市', 3);
INSERT INTO `sys_region` VALUES ('659006', '6590', '铁门关市', '新疆维吾尔自治区,自治区直辖县级行政区划,铁门关市', 3);

-- ----------------------------
-- Table structure for sys_resource
-- ----------------------------
DROP TABLE IF EXISTS `sys_resource`;
CREATE TABLE `sys_resource`  (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT 'api资源表',
  `pid` bigint(20) NULL DEFAULT NULL COMMENT '上级id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '资源名称',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'url',
  `admin` int(1) NOT NULL DEFAULT 0 COMMENT '0默认，1只有超级管理员才能使用',
  `total_rate` int(1) NOT NULL DEFAULT 100 COMMENT '该url每秒能接受的请求数',
  `ip_rate` int(1) NOT NULL DEFAULT 1 COMMENT '该url对于每个ip每秒能接受的请求数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 361 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_resource
-- ----------------------------
INSERT INTO `sys_resource` VALUES (1, 0, '采购管理', '/purchase', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (2, 1, '采购订单', '/order', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (10, 2, '添加', '/add', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (11, 2, '修改', '/update', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (12, 2, '提交', '/commit', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (13, 2, '撤回', '/withdraw', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (14, 2, '通过', '/pass', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (15, 2, '驳回', '/reject', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (16, 2, '删除', '/del', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (17, 2, '导出', '/export', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (30, 1, '采购入库', '/inbound', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (40, 30, '添加', '/add', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (41, 30, '修改', '/update', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (42, 30, '提交', '/commit', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (43, 30, '撤回', '/withdraw', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (44, 30, '通过', '/pass', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (45, 30, '驳回', '/reject', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (46, 30, '删除', '/del', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (47, 30, '导出', '/export', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (60, 0, '销售管理', '/sell', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (61, 60, '销售订单', '/order', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (70, 61, '添加', '/add', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (71, 61, '修改', '/update', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (72, 61, '提交', '/commit', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (73, 61, '撤回', '/withdraw', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (74, 61, '通过', '/pass', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (75, 61, '驳回', '/reject', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (76, 61, '删除', '/del', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (77, 61, '导出', '/export', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (90, 60, '销售出库', '/outbound', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (100, 90, '添加', '/add', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (101, 90, '修改', '/update', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (102, 90, '提交', '/commit', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (103, 90, '撤回', '/withdraw', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (104, 90, '通过', '/pass', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (105, 90, '驳回', '/reject', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (106, 90, '删除', '/del', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (107, 90, '导出', '/export', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (120, 0, '库存管理', '/stock', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (121, 120, '当前库存', '/current', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (122, 121, '导出', '/export', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (140, 0, '消息中心', '/message', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (141, 140, '消息管理', '/manage', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (142, 141, '添加', '/add', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (143, 141, '修改', '/update', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (144, 141, '删除', '/del', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (145, 141, '发布', '/publish', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (146, 141, '撤回', '/withdraw', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (200, 0, '系统管理', '/system', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (201, 200, '部门管理', '/department', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (202, 201, '添加', '/add', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (203, 201, '修改', '/update', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (204, 201, '删除', '/del', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (220, 200, '角色管理', '/role', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (221, 220, '添加', '/add', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (222, 220, '修改', '/update', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (223, 220, '删除', '/del', 1, 100, 1);
INSERT INTO `sys_resource` VALUES (240, 200, '用户管理', '/user', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (241, 240, '添加', '/add', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (242, 240, '修改', '/update', 1, 100, 1);
INSERT INTO `sys_resource` VALUES (243, 240, '删除', '/del', 1, 100, 1);
INSERT INTO `sys_resource` VALUES (244, 240, '踢出', '/kick', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (245, 240, '重置密码', '/resetPwd', 1, 100, 1);
INSERT INTO `sys_resource` VALUES (260, 200, '商品分类', '/category', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (261, 260, '添加', '/add', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (262, 260, '修改', '/update', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (263, 260, '删除', '/del', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (280, 200, '客户管理', '/customer', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (281, 280, '添加', '/add', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (282, 280, '修改', '/update', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (283, 280, '删除', '/del', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (300, 200, '供应商管理', '/supplier', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (301, 300, '添加', '/add', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (302, 300, '修改', '/update', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (303, 300, '删除', '/del', 0, 100, 1);
INSERT INTO `sys_resource` VALUES (350, 200, '接口管理', '/resource', 1, 100, 1);
INSERT INTO `sys_resource` VALUES (351, 350, '修改', '/update', 1, 100, 1);
INSERT INTO `sys_resource` VALUES (360, 200, '系统监控', '/monitor', 1, 100, 1);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '角色表',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `resource_id` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '角色具有的权限ID串',
  `cid` int(11) NOT NULL COMMENT '创建人',
  `cname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建人名称',
  `ctime` bigint(20) NOT NULL COMMENT '创建时间',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '0禁用，1正常',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name_unique_idx`(`name`) USING BTREE,
  INDEX `status_idx`(`status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '默认管理员', '1,2,10,11,12,13,14,15,16,17,30,40,41,42,43,44,45,46,47,60,61,70,71,72,73,74,75,76,77,90,100,101,102,103,104,105,106,107,120,121,122,140,141,142,143,144,145,146,200,201,202,203,204,220,221,222,240,241,244,260,261,262,263,280,281,282,283,300,301,302,303', 1, 'admin', 1582184632922, 1);

-- ----------------------------
-- Table structure for sys_supplier
-- ----------------------------
DROP TABLE IF EXISTS `sys_supplier`;
CREATE TABLE `sys_supplier`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '供应商',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '名称',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '地址',
  `linkman` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系人',
  `linkphone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系电话',
  `region` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '所在地区id',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '0禁用，1启用',
  `ctime` bigint(20) NOT NULL COMMENT '创建时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name_unique_idx`(`name`) USING BTREE,
  INDEX `ctime_idx`(`ctime`) USING BTREE,
  INDEX `status_idx`(`status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_supplier
-- ----------------------------
INSERT INTO `sys_supplier` VALUES (1, '供应商1号', '地址1', '15215612521521', '1321421521521', '1401', 1, 1582204266451, NULL);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '用户表',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录名',
  `pwd` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `role` bigint(20) NULL DEFAULT NULL COMMENT '角色',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像url',
  `ctime` bigint(20) NOT NULL COMMENT '创建时间',
  `admin` int(1) NOT NULL DEFAULT 0 COMMENT '是否为超级管理员 0否1是',
  `status` int(1) NOT NULL DEFAULT 1 COMMENT '状态 1正常 0禁用',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name_pwd_unique`(`name`, `pwd`) USING BTREE COMMENT '登录名和密码唯一'
) ENGINE = InnoDB AUTO_INCREMENT = 46 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', NULL, NULL, 1582184632922, 1, 1);

-- ----------------------------
-- Function structure for check_category_use
-- ----------------------------
DROP FUNCTION IF EXISTS `check_category_use`;
delimiter ;;
CREATE FUNCTION `check_category_use`(cid INT)
 RETURNS tinyint(4)
  READS SQL DATA 
BEGIN
  declare r int;
	select count(0) from biz_purchase_order_sub where cid=cid into r;
	if r=0 then
		select count(0) from biz_sell_order_sub where cid=cid into r;
		return r;
	else 
		return 1;
	end if;
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
