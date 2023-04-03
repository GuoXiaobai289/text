package cn.iurac.testsystem.param.request.record;

import lombok.Data;

import java.util.List;

@Data
public class RecordRequestParam {

    private Long questionId;

    private List<Long> answerList;

    private List<Boolean> choiceList;
}
