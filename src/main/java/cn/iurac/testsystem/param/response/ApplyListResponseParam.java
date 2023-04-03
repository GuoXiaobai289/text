package cn.iurac.testsystem.param.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ApplyListResponseParam implements Serializable {

    private Long teacherId;

    private String teacherName;

    private Long clazzId;

    private String clazzName;
}
