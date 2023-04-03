package cn.iurac.testsystem.util.answer;

import cn.hutool.core.util.StrUtil;
import cn.iurac.testsystem.entity.Answer;
import cn.iurac.testsystem.entity.Record;
import cn.iurac.testsystem.enums.FieldFlagEnum;
import cn.iurac.testsystem.param.request.record.RecordRequestParam;

import java.util.List;
import java.util.StringJoiner;

public class SelectAnswerParser implements ParserAnswerStrategy{

    @Override
    public Record transferRecord(Long userId, Long examId, RecordRequestParam param){
        List<Long> answerList = param.getAnswerList();
        List<Boolean> choiceList = param.getChoiceList();
        StringJoiner  stringJoiner = new StringJoiner(",");
        for (int i = 0; i < answerList.size(); i++) {
            Boolean choice = choiceList.get(i);
            if(choice){
                stringJoiner.add(String.valueOf(answerList.get(i)));
            }
        }
        Record record = Record.builder()
                .userId(userId)
                .examId(examId)
                .questionId(param.getQuestionId())
                .build();
        if(StrUtil.isBlank(stringJoiner.toString())){
            record.setFinishFlag(FieldFlagEnum.EMPTY.getCode());
        }else {
            record.setFinishFlag(FieldFlagEnum.FINISH.getCode());
            record.setAnsweredId(stringJoiner.toString());
        }
        return record;
    }

    @Override
    public Answer transferAnswer() {
        return null;
    }
}
