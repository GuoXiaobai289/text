package cn.iurac.testsystem.util.answer;

import cn.hutool.core.util.ObjectUtil;
import cn.iurac.testsystem.entity.Answer;
import cn.iurac.testsystem.entity.Record;
import cn.iurac.testsystem.enums.QuestionTypeEnum;

public class ParserAnswerStrategyFactory {

    public static ParserAnswerStrategy getStrategy(QuestionTypeEnum questionTypeEnum){
        if(ObjectUtil.equal(questionTypeEnum,QuestionTypeEnum.RADIO)){
            return new RadioAnswerParser();
        } else if(ObjectUtil.equal(questionTypeEnum,QuestionTypeEnum.MULTIPLE)){
            return new SelectAnswerParser();
        } else if(ObjectUtil.equal(questionTypeEnum,QuestionTypeEnum.JUDGE)){
            return new RadioAnswerParser();
        }else {
            return null;
        }
    }
}
