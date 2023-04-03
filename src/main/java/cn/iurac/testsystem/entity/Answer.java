package cn.iurac.testsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

/**
 * 
 * @TableName t_answer
 */
@TableName(value ="t_answer")
@Data
@Builder
public class Answer implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 答案描述
     */
    private String content;

    /**
     * 是否为正确答案（0.错误答案；1.正确答案）
     */
    private Integer correctFlag;

    /**
     * 题目id
     */
    private Long questionId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}