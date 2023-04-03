package cn.iurac.testsystem.param.request.grade;

import lombok.Data;

import java.io.Serializable;

@Data
public class GradePageRequestParam implements Serializable {

    private Long id;

    private String username;

    private String title;

    private String startTime;

    private String endTime;

    private Long createUserId;


}
