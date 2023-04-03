package cn.iurac.testsystem.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.iurac.testsystem.entity.*;
import cn.iurac.testsystem.enums.FieldFlagEnum;
import cn.iurac.testsystem.enums.QuestionTypeEnum;
import cn.iurac.testsystem.exception.ServiceException;
import cn.iurac.testsystem.mapper.RecordMapper;
import cn.iurac.testsystem.mapper.RepoMapper;
import cn.iurac.testsystem.param.request.record.RecordRequestParam;
import cn.iurac.testsystem.param.request.repo.RepoPageRequestParam;
import cn.iurac.testsystem.param.request.repo.RepoRequestParam;
import cn.iurac.testsystem.service.QuestionRepoService;
import cn.iurac.testsystem.service.QuestionService;
import cn.iurac.testsystem.service.RecordService;
import cn.iurac.testsystem.service.RepoService;
import cn.iurac.testsystem.util.answer.ParserAnswerStrategy;
import cn.iurac.testsystem.util.answer.ParserAnswerStrategyFactory;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@Service("RecordService")
@Slf4j
public class RecordServiceImpl extends ServiceImpl<RecordMapper, Record>
implements RecordService {

    @Resource
    QuestionService questionService;

    @Override
    public List<Record> list(Record record) {
        QueryWrapper<Record> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjectUtil.isNotNull(record.getUserId()), "user_id",record.getUserId())
                .eq(ObjectUtil.isNotNull(record.getExamId()), "exam_id",record.getExamId())
                .eq(ObjectUtil.isNotNull(record.getFinishFlag()), "finish_flag",record.getFinishFlag())
                .eq(ObjectUtil.isNotNull(record.getCorrectFlag()), "correct_flag",record.getCorrectFlag());
        return list(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = {ServiceException.class,Exception.class})
    public boolean saveOrUpdate(Long userId, Long examId, List<RecordRequestParam> data) throws ServiceException {
        List<Record> submitRecordList = Lists.newArrayListWithExpectedSize(data.size());
        for (RecordRequestParam param : data) {
            Long questionId = param.getQuestionId();
            List<Long> answerList = param.getAnswerList();
            List<Boolean> choiceList = param.getChoiceList();
            if(!ObjectUtil.isAllNotEmpty(questionId,answerList,choiceList) || ObjectUtil.notEqual(answerList.size(),choiceList.size())){
                log.info("错题记录异常,用户ID:{},考试ID:{},题目ID:{},答案:{},记录:{}", userId, examId, questionId, answerList, choiceList);
                continue;
            }
            Question question = questionService.getById(questionId);
            if(ObjectUtil.isNull(question)){
                log.info("提交试卷中有题目找不到,考试ID:{},题目ID:{}", examId, questionId);
                continue;
            }
            ParserAnswerStrategy parser = ParserAnswerStrategyFactory.getStrategy(QuestionTypeEnum.getByCode(question.getType()));
            if(ObjectUtil.isNotNull(parser)){
                Record record = parser.transferRecord(userId,examId,param);
                if(ObjectUtil.isNotNull(record)){
                    submitRecordList.add(record);
                }
            }
        }
        Record record = Record.builder().userId(userId).examId(examId).build();
        List<Record> recordList = list(record);
        List<Record> saveRecordList = submitRecordList.stream().filter(r -> !CollectionUtil.contains(recordList, r)).collect(Collectors.toList());
        if(!saveRecordList.isEmpty() && !saveBatch(saveRecordList)){
            throw new ServiceException("添加记录时失败");
        }
        List<Long> deleteRecordList = recordList.stream().filter(r -> !CollectionUtil.contains(submitRecordList, r)).map(Record::getId).collect(Collectors.toList());
        if (!deleteRecordList.isEmpty() && !removeByIds(deleteRecordList)) {
            throw new ServiceException("删除旧记录时失败");
        }
        return true;
    }
}




