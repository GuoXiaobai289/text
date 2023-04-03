package cn.iurac.testsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName r_user_clazz
 */
@TableName(value ="r_user_clazz")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserClazz implements Serializable {
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
     * 班级id
     */
    private Long clazzId;

    /**
     * 删除标志
     */
    private Integer QuitFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}