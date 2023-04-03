package cn.iurac.testsystem.param.response;

import cn.iurac.testsystem.entity.Answer;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class QuestionDetailResponseParam {

    private Long id;

    private String content;

    private Integer type;

    private List<Answer> answerList;

    private String judgeCorrection;
}
