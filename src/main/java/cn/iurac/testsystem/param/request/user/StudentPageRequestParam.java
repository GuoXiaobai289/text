package cn.iurac.testsystem.param.request.user;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class StudentPageRequestParam implements Serializable {

    private String username;

    private String name;

    private List<Long> studentIds;

}
