package cn.iurac.testsystem.param.request.exam;

import lombok.Data;

import java.io.Serializable;

@Data
public class ExamPageRequestParam implements Serializable {

    private String title;

    private String content;

    private String state;

    private String examStartTime;

    private String examEndTime;

    private Long createUserId;

    private String createStartTime;

    private String createEndTime;

}
