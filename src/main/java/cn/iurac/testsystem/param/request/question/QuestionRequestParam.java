package cn.iurac.testsystem.param.request.question;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.List;

@Data
public class QuestionRequestParam implements Serializable {

    @NotBlank(message = "题目不能为空")
    private String content;

    private Integer type;

    private List<Integer> radioCorrectionList;

    private List<String> radioAnswerList;

    private List<Integer> multipleCorrectionList;

    private List<String> multipleAnswerList;

    private Integer judgeCorrection;

    @NotNull(message = "至少选择一个题库")
    private List<Long> repoIds;


}
