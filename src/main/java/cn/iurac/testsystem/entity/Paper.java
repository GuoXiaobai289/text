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
 * @TableName t_paper
 */
@TableName(value ="t_paper")
@Data
@Builder
public class Paper implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 试卷名
     */
    private String title;

    /**
     * 单选题数量
     */
    private Integer radioCount;

    /**
     * 单选题分数
     */
    private Integer radioScore;

    /**
     * 多选题数量
     */
    private Integer selectCount;

    /**
     * 多选题分数
     */
    private Integer selectScore;

    /**
     * 判断题数量
     */
    private Integer judgeCount;

    /**
     * 判断题分数
     */
    private Integer judgeScore;

    /**
     * 总分
     */
    private Integer score;

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