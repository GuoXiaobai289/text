package cn.iurac.testsystem.param.request.clazz;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ClazzPageRequestParam implements Serializable {

    private String name;

    private String startTime;

    private String endTime;

    private List<Long> clazzIds;


}
