package cn.iurac.testsystem.param.request.paper;

import lombok.Data;

import java.io.Serializable;

@Data
public class PaperPageRequestParam implements Serializable {

    private String title;

    private String startTime;

    private String endTime;

    private Long createUserId;


}
