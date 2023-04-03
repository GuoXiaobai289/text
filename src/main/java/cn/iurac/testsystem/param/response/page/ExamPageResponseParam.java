package cn.iurac.testsystem.param.response.page;

import cn.iurac.testsystem.entity.Answer;
import cn.iurac.testsystem.entity.Clazz;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ExamPageResponseParam {

    private Long id;

    private String title;

    private String content;

    private Long paperId;

    private String paperTitle;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date startTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date endTime;

    private Integer time;

    private Integer state;

    private String createUserId;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    private String createUserName;

    private List<Clazz> clazzList;

    private Double max;

    private Double min;

    private Double avg;

    private Integer total;

    private Integer pass;

    private Integer attend;

    private Integer absent;

}
