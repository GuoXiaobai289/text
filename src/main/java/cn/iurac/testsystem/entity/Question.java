package cn.iurac.testsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

/**
 * 
 * @TableName t_question
 */
@TableName(value ="t_question")
@Data
@Builder
public class Question implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 题目描述
     */
    private String content;

    /**
     * 题目标志（1.单选；2.多选；3.判断）
     */
    private Integer type;

    /**
     * 正确答案id（1.单选直接显示对应答案id；2.多选答案用逗号分隔；3.判断题则用0表示错误，1表示正确）
     */
    private String correctAnswerId;

    /**
     * 创建用户id
     */
    private Long createUserId;

    /**
     * 创建时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;

    /**
     * 删除标志（0.正常；1.删除）
     */
    private Integer deleteFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}