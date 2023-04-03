package cn.iurac.testsystem.param.request.apply;

import lombok.Data;

import java.io.Serializable;

@Data
public class ApplyPageRequestParam implements Serializable {

    private String studentUsername;

    private String teacherUsername;

    private String clazzName;

    private Integer type;

    private String applyStartTime;

    private String applyEndTime;

    private String createStartTime;

    private String createEndTime;

    private Long teacherId;

    private Long studentId;


}
