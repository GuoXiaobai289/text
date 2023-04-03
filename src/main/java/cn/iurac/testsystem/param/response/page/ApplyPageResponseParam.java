package cn.iurac.testsystem.param.response.page;

import cn.iurac.testsystem.entity.Answer;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class ApplyPageResponseParam implements Serializable {

    private Long id;

    private Long studentId;

    private String studentUsername;

    private String studentName;

    private Long teacherId;

    private String teacherUsername;

    private String teacherName;

    private Long clazzId;

    private String clazzName;

    private Integer type;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date applyTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date finishTime;

    private Integer deleteFlag;
}
