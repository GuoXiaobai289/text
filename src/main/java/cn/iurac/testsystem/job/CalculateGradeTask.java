package cn.iurac.testsystem.job;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.iurac.testsystem.entity.*;
import cn.iurac.testsystem.enums.ExamStateEnum;
import cn.iurac.testsystem.enums.FieldFlagEnum;
import cn.iurac.testsystem.service.*;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CalculateGradeTask {

    Cache<Long, Question> cache = CacheBuilder.newBuilder()
            .initialCapacity(300)
            .maximumSize(300)
            .expireAfterWrite(3, TimeUnit.MINUTES)
            .build();

    public static final Integer ABSENT_STUDENT_PER_EXAM_COUNT = 5;
    public static final Integer FAIL_COUNT_LIMIT = 3;
    public static final Integer ABSENT_SCORE = 0;

    public static Integer failCount = 0;

    @Resource
    private ExamService examService;
    @Resource
    private UserService userService;
    @Resource
    private PaperService paperService;
    @Resource
    private GradeService gradeService;
    @Resource
    private RecordService recordService;
    @Resource
    private QuestionService questionService;

    @Scheduled(cron = "0 0/15 * * * ?")//每15分钟执行一次
    public void execute() {
        // 查询过去15分钟到现在结束的考试
        List<Exam> examList = examService.listJustFinished();
        log.info("定时任务——查询结束的考试{}，开始改卷",examList);
        List<Long> examIds = examList.stream().map(Exam::getId).collect(Collectors.toList());
        // 查询成绩（成绩考试时生成一个初始化的成绩详情）
        Map<String, Grade> gradeMap = gradeService.listByExamIds(examIds).stream().collect(Collectors.toMap(Grade::identifySymbol, Function.identity(),(a,b)->a));
        // 预测有缺考的学生数，手动添加成绩
        List<Grade> saveGradeList = Lists.newArrayListWithExpectedSize(examList.size()*ABSENT_STUDENT_PER_EXAM_COUNT);
        List<Grade> updateGradeList = Lists.newArrayListWithExpectedSize(gradeMap.size());

        examList.parallelStream().forEach(exam -> {
            List<Long> userIds = userService.listStudentByExamId(exam.getId());
            Paper paper = paperService.getById(exam.getPaperId());
            Integer[] scoreCard = new Integer[]{paper.getRadioScore(),paper.getSelectScore(),paper.getJudgeScore()};
            userIds.forEach(userId->{
                String identify = new StringJoiner(",").add(String.valueOf(userId)).add(String.valueOf(exam.getId())).toString();
                Grade grade = gradeMap.get(identify);
                if(ObjectUtil.isNull(grade)){
                    grade = Grade.builder()
                            .userId(userId)
                            .examId(exam.getId())
                            .finishFlag(FieldFlagEnum.ABSENT.getCode())
                            .createTime(DateUtil.date())
                            .updateTime(DateUtil.date())
                            .deleteFlag(FieldFlagEnum.NORMAL.getCode())
                            .userScore(ABSENT_SCORE)
                            .build();
                    saveGradeList.add(grade);
                    return;
                }
                DateTime date = DateUtil.date();
                grade.setFinishTime(ObjectUtil.defaultIfNull(grade.getFinishTime(),date));
                grade.setFinishFlag(FieldFlagEnum.END.getCode());
                grade.setUpdateTime(date);
                grade.setUserScore(calculateGrade(userId,exam.getId(),scoreCard));
                updateGradeList.add(grade);
            });
            exam.setState(ExamStateEnum.END.getCode());
            exam.setUpdateTime(DateUtil.date());
        });
        boolean executeSuccessfulFlag = (CollectionUtil.isEmpty(saveGradeList) || gradeService.saveBatch(saveGradeList))
                && (CollectionUtil.isEmpty(updateGradeList) || gradeService.updateBatchById(updateGradeList));
        if(!executeSuccessfulFlag){
            if(failCount++ < FAIL_COUNT_LIMIT){
                log.warn("定时任务执行失败——批卷任务(成绩新增：{}, 成绩更新：{})", saveGradeList,updateGradeList);
                execute();
            }else {
                failCount = 0;
                log.error("定时任务失败重试过多，停止重试——批卷任务(成绩新增：{}, 成绩更新：{})", saveGradeList,updateGradeList);
            }
        }
        examService.updateBatchById(examList);
        log.info("定时任务——查询结束的考试，改卷结束");
    }

    private Integer calculateGrade(Long userId, Long examId, Integer[] scoreCard) {
        AtomicReference<Integer> score = new AtomicReference<>(0);
        Record record = Record.builder().userId(userId).examId(examId).build();
        List<Record> recordList = recordService.list(record);
        List<Long> questionIds = recordList.stream().filter(q->!cache.asMap().containsKey(q.getId())).map(Record::getQuestionId).collect(Collectors.toList());
        if(CollectionUtil.isEmpty(recordList)){
            return ABSENT_SCORE;
        }
        if(CollectionUtil.isNotEmpty(questionIds)){
            Map<Long,Question> questionMap = questionService.listByIds(questionIds).stream().collect(Collectors.toMap(Question::getId,Function.identity()));
            cache.putAll(questionMap);
        }
        recordList.forEach(r -> {
            try {
                Question question = cache.get(r.getQuestionId(), () -> questionService.getById(r.getQuestionId()));
                if(ObjectUtil.notEqual(r.getFinishFlag(),FieldFlagEnum.EMPTY.getCode()) && StrUtil.equals(r.getAnsweredId(),question.getCorrectAnswerId())){
                    score.updateAndGet(v -> v + scoreCard[question.getType()-1]);
                    r.setCorrectFlag(FieldFlagEnum.CORRECT.getCode());
                }else {
                    r.setCorrectFlag(FieldFlagEnum.WRONG.getCode());
                }
            } catch (ExecutionException e) {
               log.warn("加载缓存失败",e);
            }
        });
        recordService.saveOrUpdateBatch(recordList);
        return score.get();
    }

}