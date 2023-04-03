package cn.iurac.testsystem.util.answer;

import cn.iurac.testsystem.entity.Answer;
import cn.iurac.testsystem.entity.Record;
import cn.iurac.testsystem.enums.FieldFlagEnum;
import cn.iurac.testsystem.param.request.record.RecordRequestParam;
import com.baomidou.mybatisplus.extension.api.R;

import java.util.List;

public class RadioAnswerParser implements ParserAnswerStrategy{

    @Override
    public Record transferRecord(Long userId, Long examId, RecordRequestParam param){
        List<Long> answerList = param.getAnswerList();
        List<Boolean> choiceList = param.getChoiceList();
        Record record = Record.builder()
                .userId(userId)
                .examId(examId)
                .questionId(param.getQuestionId())
                .build();
        boolean finish = false;
        for (int i = 0; i < answerList.size(); i++) {
            Boolean choice = choiceList.get(i);
            if(choice){
                if(finish){
                    finish = false;
                    break;
                }
                finish = true;
                record.setFinishFlag(FieldFlagEnum.FINISH.getCode());
                record.setAnsweredId(String.valueOf(answerList.get(i)));

            }

        }
        if(!finish){
            record.setFinishFlag(FieldFlagEnum.EMPTY.getCode());
            record.setAnsweredId("");
        }
        return record;
    }

    @Override
    public Answer transferAnswer() {
        return null;
    }
}
