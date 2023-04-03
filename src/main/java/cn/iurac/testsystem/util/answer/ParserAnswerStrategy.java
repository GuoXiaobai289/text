package cn.iurac.testsystem.util.answer;

import cn.iurac.testsystem.entity.Answer;
import cn.iurac.testsystem.entity.Record;
import cn.iurac.testsystem.param.request.record.RecordRequestParam;

import java.util.List;

public interface ParserAnswerStrategy {

    Answer transferAnswer();

    Record transferRecord(Long userId, Long examId, RecordRequestParam param);
}
