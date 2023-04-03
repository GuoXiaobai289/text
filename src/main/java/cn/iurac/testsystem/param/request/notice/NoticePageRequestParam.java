package cn.iurac.testsystem.param.request.notice;

import lombok.Data;

import java.io.Serializable;

@Data
public class NoticePageRequestParam implements Serializable {

    private String title;

    private String content;

    private String startTime;

    private String endTime;


}
