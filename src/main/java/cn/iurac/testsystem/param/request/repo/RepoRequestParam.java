package cn.iurac.testsystem.param.request.repo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.List;

@Data
public class RepoRequestParam implements Serializable {

    @NotBlank(message = "题库名不能为空")
    private String title;

}
