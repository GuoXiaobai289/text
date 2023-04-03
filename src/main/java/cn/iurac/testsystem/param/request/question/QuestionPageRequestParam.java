package cn.iurac.testsystem.param.request.question;

import lombok.Data;

import java.io.Serializable;

@Data
public class QuestionPageRequestParam implements Serializable {

    private String content;

    private Integer type;

    private String startTime;

    private String endTime;

    private Long userId;


}
