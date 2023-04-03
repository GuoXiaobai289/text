package cn.iurac.testsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 
 * @TableName t_sys_operation
 */
@TableName(value ="t_sys_operation")
@Data
public class SysOperation implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 操作人id
     */
    private Long userId;

    /**
     * 操作人姓名
     */
    private String userName;

    /**
     * 操作名
     */
    private String operation;

    /**
     * 方法
     */
    private String method;

    /**
     * 请求地址
     */
    private String url;

    /**
     * 参数
     */
    private String argument;

    /**
     * ip地址
     */
    private String ip;

    /**
     * 创建时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}