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
 * @TableName r_paper_repo
 */
@TableName(value ="r_paper_repo")
@Data
@Builder
public class PaperRepo implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 试卷id
     */
    private Long paperId;

    /**
     * 题库id
     */
    private Long repoId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}