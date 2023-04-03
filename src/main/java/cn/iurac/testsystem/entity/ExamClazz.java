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
 * @TableName r_exam_clazz
 */
@TableName(value ="r_exam_clazz")
@Data
@Builder
public class ExamClazz implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 考试id
     */
    private Long examId;

    /**
     * 班级id
     */
    private Long clazzId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}