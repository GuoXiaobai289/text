package cn.iurac.testsystem.param.request.clazz;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class ClazzRequestParam implements Serializable {

    @NotBlank(message = "班级名不能为空")
    private String name;

}
