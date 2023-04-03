package cn.iurac.testsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName r_grade_record
 */
@TableName(value ="r_grade_record")
@Data
public class GradeRecord implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private Long gradeId;

    /**
     * 
     */
    private Long recordId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}