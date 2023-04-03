package cn.iurac.testsystem.param.request.grade;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class GradeRequestParam implements Serializable {

    @NotBlank(message = "题库名不能为空")
    private String title;

}
