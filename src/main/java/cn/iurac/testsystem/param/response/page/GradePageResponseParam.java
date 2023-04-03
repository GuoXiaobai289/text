package cn.iurac.testsystem.param.response.page;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class GradePageResponseParam implements Serializable {

    private Long id;

    private Long userId;

    private String username;

    private String name;

    private Long examId;

    private String title;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date attendTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date finishTime;

    private String userScore;

    private Integer finishFlag;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;
}
