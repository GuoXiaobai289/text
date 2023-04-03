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
 * @TableName r_question_repo
 */
@TableName(value ="r_question_repo")
@Data
@Builder
public class QuestionRepo implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 题目id
     */
    private Long questionId;

    /**
     * 题库id
     */
    private Long repoId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}