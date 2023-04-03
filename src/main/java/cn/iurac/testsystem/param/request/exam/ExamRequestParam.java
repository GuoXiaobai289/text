package cn.iurac.testsystem.param.request.exam;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class ExamRequestParam implements Serializable {

    @NotBlank(message = "考试标题不能为空")
    private String title;

    @NotBlank(message = "考试描述信息不能为空")
    private String content;

    @NotNull(message = "找不到试卷")
    private Long paperId;

    @NotNull(message = "找不到班级")
    private List<Long> clazzIds;

    @Positive(message = "考试用时输入异常，请输入正整数")
    private Integer time;

    @Future(message = "开始考试时间必须大于此时")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date examStartTime;

    @Future(message = "结束考试时间必须大于此时")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date examEndTime;


}
