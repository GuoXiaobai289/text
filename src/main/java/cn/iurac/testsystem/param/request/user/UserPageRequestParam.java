package cn.iurac.testsystem.param.request.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserPageRequestParam implements Serializable {

    private String username;

    private Integer role;

    private String name;

    private String startTime;

    private String endTime;

    private Integer lockFlag;

}
