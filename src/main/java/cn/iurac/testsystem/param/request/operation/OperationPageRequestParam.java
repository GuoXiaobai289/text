package cn.iurac.testsystem.param.request.operation;

import lombok.Data;

import java.io.Serializable;

@Data
public class OperationPageRequestParam implements Serializable {

    private String userName;

    private String operation;

    private String url;

    private String ip;

    private String startTime;

    private String endTime;


}
