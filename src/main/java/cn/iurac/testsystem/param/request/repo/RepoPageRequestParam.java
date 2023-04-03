package cn.iurac.testsystem.param.request.repo;

import lombok.Data;

import java.io.Serializable;

@Data
public class RepoPageRequestParam implements Serializable {

    private String title;

    private String startTime;

    private String endTime;


}
