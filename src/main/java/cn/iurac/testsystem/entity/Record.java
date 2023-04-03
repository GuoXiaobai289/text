package cn.iurac.testsystem.entity;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.StringJoiner;

import lombok.*;

/**
 * 
 * @TableName t_record
 */
@TableName(value ="t_record")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Record implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 考试id
     */
    private Long examId;

    /**
     * 题目id
     */
    private Long questionId;

    /**
     * 选择记录
     */
    private String answeredId;

    /**
     * 完成标志（0.未参加；1.已参加）
     */
    private Integer finishFlag;

    /**
     * 正确标志（0.错误；1.正确）
     */
    private Integer correctFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public int hashCode() {
        return this.identifySymbol().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(ObjectUtil.isNotNull(obj) && obj.getClass().equals(Record.class)){
            Record record = (Record) obj;
            return StrUtil.equals(this.identifySymbol(),record.identifySymbol());
        }
        return false;
    }

    public String identifySymbol(){
        return new StringJoiner(",")
                .add(String.valueOf(userId))
                .add(String.valueOf(examId))
                .add(String.valueOf(questionId))
                .add(answeredId)
                .toString();
    }
}