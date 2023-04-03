/*
 Navicat Premium Data Transfer

 Source Server         : mysql(aliyun)
 Source Server Type    : MySQL
 Source Server Version : 80024
 Source Host           : localhost:3306
 Source Schema         : test-system

 Target Server Type    : MySQL
 Target Server Version : 80024
 File Encoding         : 65001

 Date: 04/06/2022 17:20:29
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for r_exam_clazz
-- ----------------------------
DROP TABLE IF EXISTS `r_exam_clazz`;
CREATE TABLE `r_exam_clazz`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `exam_id` bigint NULL DEFAULT NULL COMMENT '考试id',
  `clazz_id` bigint NULL DEFAULT NULL COMMENT '班级id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 34 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for r_paper_repo
-- ----------------------------
DROP TABLE IF EXISTS `r_paper_repo`;
CREATE TABLE `r_paper_repo`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `paper_id` bigint NULL DEFAULT NULL COMMENT '试卷id',
  `repo_id` bigint NULL DEFAULT NULL COMMENT '题库id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for r_question_repo
-- ----------------------------
DROP TABLE IF EXISTS `r_question_repo`;
CREATE TABLE `r_question_repo`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `question_id` bigint NULL DEFAULT NULL COMMENT '题目id',
  `repo_id` bigint NULL DEFAULT NULL COMMENT '题库id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for r_user_clazz
-- ----------------------------
DROP TABLE IF EXISTS `r_user_clazz`;
CREATE TABLE `r_user_clazz`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户id',
  `clazz_id` bigint NULL DEFAULT NULL COMMENT '班级id',
  `quit_flag` tinyint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_answer
-- ----------------------------
DROP TABLE IF EXISTS `t_answer`;
CREATE TABLE `t_answer`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '答案描述',
  `correct_flag` tinyint NULL DEFAULT NULL COMMENT '是否为正确答案（0.错误答案；1.正确答案）',
  `question_id` bigint NULL DEFAULT NULL COMMENT '题目id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 60 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_apply
-- ----------------------------
DROP TABLE IF EXISTS `t_apply`;
CREATE TABLE `t_apply`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NULL DEFAULT NULL COMMENT '学生id',
  `teacher_id` bigint NULL DEFAULT NULL COMMENT '教师id',
  `clazz_id` bigint NULL DEFAULT NULL COMMENT '班级id',
  `type` tinyint NULL DEFAULT NULL COMMENT '删除标志（-1.已拒绝；0.审批中；1.已通过）',
  `apply_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `finish_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `delete_flag` tinyint NULL DEFAULT NULL COMMENT '删除标志（0.正常；1.删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_clazz
-- ----------------------------
DROP TABLE IF EXISTS `t_clazz`;
CREATE TABLE `t_clazz`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '班级名',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_user_id` tinyint NULL DEFAULT NULL COMMENT '创建用户',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `delete_flag` tinyint NULL DEFAULT NULL COMMENT '删除标志（0.正常；1.删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_exam
-- ----------------------------
DROP TABLE IF EXISTS `t_exam`;
CREATE TABLE `t_exam`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '考试名称',
  `content` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
  `paper_id` bigint NULL DEFAULT NULL COMMENT '试卷Id',
  `start_time` datetime(0) NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime(0) NULL DEFAULT NULL COMMENT '结束时间',
  `time` int NULL DEFAULT NULL COMMENT '总时间（分钟）',
  `state` tinyint NULL DEFAULT NULL COMMENT '状态（0.未开始；1.进行中；2.改卷中；3.已结束；）',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建用户id',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `delete_flag` tinyint NULL DEFAULT NULL COMMENT '删除标志（0.正常；1.删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_grade
-- ----------------------------
DROP TABLE IF EXISTS `t_grade`;
CREATE TABLE `t_grade`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户id',
  `exam_id` bigint NULL DEFAULT NULL COMMENT '考试id',
  `attend_time` datetime(0) NULL DEFAULT NULL COMMENT '参加考试时间',
  `finish_time` datetime(0) NULL DEFAULT NULL COMMENT '完成时间',
  `user_score` int NULL DEFAULT NULL COMMENT '考试分数',
  `finish_flag` tinyint NULL DEFAULT NULL COMMENT '完成标志（0.未参加；1.已参加）',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `delete_flag` tinyint NULL DEFAULT NULL COMMENT '删除标志（0.正常；1.删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 176 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_paper
-- ----------------------------
DROP TABLE IF EXISTS `t_paper`;
CREATE TABLE `t_paper`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '试卷名',
  `radio_count` tinyint NULL DEFAULT NULL COMMENT '单选题数量',
  `radio_score` tinyint NULL DEFAULT NULL COMMENT '单选题分数',
  `select_count` tinyint NULL DEFAULT NULL COMMENT '多选题数量',
  `select_score` tinyint NULL DEFAULT NULL COMMENT '多选题分数',
  `judge_count` tinyint NULL DEFAULT NULL COMMENT '判断题数量',
  `judge_score` tinyint NULL DEFAULT NULL COMMENT '判断题分数',
  `score` int NULL DEFAULT NULL COMMENT '总分',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建用户id',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `delete_flag` tinyint NULL DEFAULT NULL COMMENT '删除标志（0.正常；1.删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_question
-- ----------------------------
DROP TABLE IF EXISTS `t_question`;
CREATE TABLE `t_question`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '题目描述',
  `type` tinyint NULL DEFAULT NULL COMMENT '题目标志（1.单选；2.多选；3.判断）',
  `correct_answer_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '正确答案id（1.单选直接显示对应答案id；2.多选答案用逗号分隔；3.判断题则用0表示错误，1表示正确）',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建用户id',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `delete_flag` tinyint NULL DEFAULT NULL COMMENT '删除标志（0.正常；1.删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_record
-- ----------------------------
DROP TABLE IF EXISTS `t_record`;
CREATE TABLE `t_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户id',
  `exam_id` bigint NULL DEFAULT NULL COMMENT '考试id',
  `question_id` bigint NULL DEFAULT NULL COMMENT '题目id',
  `answered_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '选择记录',
  `finish_flag` tinyint NULL DEFAULT NULL COMMENT '完成标志（0.未参加；1.已参加）',
  `correct_flag` tinyint NULL DEFAULT NULL COMMENT '正确标志（0.错误；1.正确）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 295 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_repo
-- ----------------------------
DROP TABLE IF EXISTS `t_repo`;
CREATE TABLE `t_repo`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '题库名',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建用户id',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `delete_flag` tinyint NULL DEFAULT NULL COMMENT '删除标志（0.正常；1.删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_sys_notice
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_notice`;
CREATE TABLE `t_sys_notice`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标题',
  `content` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '公告',
  `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建用户id',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `delete_flag` tinyint NULL DEFAULT NULL COMMENT '删除标志（0.正常；1.删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_sys_operation
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_operation`;
CREATE TABLE `t_sys_operation`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NULL DEFAULT NULL COMMENT '操作人id',
  `user_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作人姓名',
  `operation` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作名',
  `method` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '方法',
  `url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '请求地址',
  `argument` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '参数',
  `ip` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'ip地址',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1674 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '姓名',
  `salt` char(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '盐',
  `phone` char(11) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '手机',
  `email` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `img` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '头像图片地址',
  `role` tinyint NULL DEFAULT NULL COMMENT '角色（1.学生；2.教师；3.管理员）',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `lock_flag` tinyint NULL DEFAULT NULL COMMENT '锁定标志（0.正常；1.锁定）',
  `delete_flag` tinyint NULL DEFAULT NULL COMMENT '删除标志（0.正常；1.删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
