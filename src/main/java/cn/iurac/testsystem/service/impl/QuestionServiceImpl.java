package cn.iurac.testsystem.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.iurac.testsystem.entity.*;
import cn.iurac.testsystem.enums.FieldFlagEnum;
import cn.iurac.testsystem.enums.QuestionTypeEnum;
import cn.iurac.testsystem.exception.ServiceException;
import cn.iurac.testsystem.param.request.question.QuestionPageRequestParam;
import cn.iurac.testsystem.param.request.question.QuestionRequestParam;
import cn.iurac.testsystem.param.response.chart.PieChartResponseParam;
import cn.iurac.testsystem.param.response.QuestionDetailResponseParam;
import cn.iurac.testsystem.service.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.iurac.testsystem.mapper.QuestionMapper;
import com.google.common.collect.Lists;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */
@Service("QuestionService")
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
implements QuestionService{

    @Resource
    private AnswerService answerService;
    @Resource
    private QuestionRepoService questionRepoService;
    @Resource
    private RepoService repoService;
    @Resource
    private RecordService recordService;
    @Resource
    private PaperRepoService paperRepoService;
    @Resource
    private GradeService gradeService;

    @Override
    public Page<Question> listForPage(Page<Question> page, QuestionPageRequestParam param) {
        List<Long> questionIds = null;
        if(ObjectUtil.isNotNull(param.getUserId())){
            Record record = Record.builder().userId(param.getUserId()).correctFlag(FieldFlagEnum.WRONG.getCode()).build();
            questionIds = recordService.list(record).stream().map(Record::getQuestionId).collect(Collectors.toList());
            if(CollectionUtil.isEmpty(questionIds)){
                return page;
            }
        }
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(param.getContent()),"content",param.getContent())
                .eq("type",param.getType())
                .ge(StrUtil.isNotBlank(param.getStartTime()),"create_time",param.getStartTime())
                .le(StrUtil.isNotBlank(param.getEndTime()),"create_time", param.getEndTime())
                .in(CollectionUtil.isNotEmpty(questionIds),"id", questionIds);

        return page(page,queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = {ServiceException.class,Exception.class})
    public boolean save(QuestionRequestParam data) throws ServiceException {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        Question question = Question.builder().content(data.getContent()).type(data.getType()).deleteFlag(0).createUserId(user.getId()).createTime(DateUtil.date()).updateTime(DateUtil.date()).build();
        if (!save(question)) {
            throw new ServiceException("添加题目时失败！");
        }
        Integer type = data.getType();
        solveCorrectAnswer(question, data, type);
        if (!updateById(question)) {
            throw new ServiceException("添加题目时失败！");
        }
        saveToRepo(question.getId(),data.getRepoIds());
        return true;
    }

    @Override
    @Transactional(rollbackFor = {ServiceException.class,Exception.class})
    public boolean update(Question question, QuestionRequestParam data) throws ServiceException {
        Integer type = data.getType();
        if(ObjectUtil.notEqual(type, QuestionTypeEnum.JUDGE.getCode()) && !answerService.removeByQuestionId(question.getId())){
            throw new ServiceException("删除原有选项时失败！");
        }
        solveCorrectAnswer(question, data, type);
        question.setUpdateTime(DateUtil.date());
        question.setContent(data.getContent());
        if (!updateById(question)) {
            throw new ServiceException("更新题目信息时失败！");
        }
        List<Long> list = repoService.listByQuestionId(question.getId()).stream().map(Repo::getId).collect(Collectors.toList());
        List<Long> repoIds = data.getRepoIds().stream().filter(x -> !CollectionUtil.contains(list,x)).collect(Collectors.toList());
        saveToRepo(question.getId(),repoIds);
        return true;
    }

    @Override
    public PieChartResponseParam repoAnalysis() {
        PieChartResponseParam param = new PieChartResponseParam();
        List<Repo> repoList = repoService.list();
        List<Map<String, Object>> dataList = Lists.newArrayListWithExpectedSize(repoList.size());
        repoList.forEach(r -> {
            QuestionRepo questionRepo = QuestionRepo.builder().repoId(r.getId()).build();
            List<Long> questionIds = questionRepoService.list(questionRepo).stream().map(QuestionRepo::getQuestionId).collect(Collectors.toList());
            Map<String, Object> map = new HashMap<>();
            map.put("name",r.getTitle());
            Integer count = 0;
            if(CollectionUtil.isNotEmpty(questionIds)){
                count = listByIds(questionIds).size();            }
            map.put("value",count);
            dataList.add(map);
        });
        param.setDataList(dataList);
        param.setTotal(dataList.size());
        return param;
    }

    @Override
    public PieChartResponseParam typeAnalysis() {

        PieChartResponseParam param = new PieChartResponseParam();
        List<Repo> repoList = repoService.list();
        List<Map<String, Object>> dataList = Lists.newArrayListWithExpectedSize(repoList.size());
        for (QuestionTypeEnum value : QuestionTypeEnum.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("name",value.getType());
            map.put("value",countByType(value.getCode()));
            dataList.add(map);
        }
        param.setDataList(dataList);
        param.setTotal(dataList.size());
        return param;
    }

    @Override
    @Transactional(rollbackFor = {ServiceException.class,Exception.class})
    public List<QuestionDetailResponseParam> generate(Long examId, Paper paper, Long userId, Grade grade) throws ServiceException {
        // 初始化成绩
        if(!gradeService.save(grade)){
            throw new ServiceException("初始化失败，请刷新重试！");
        }
        // 随机抽取题目id
        PaperRepo paperRepo = PaperRepo.builder().paperId(paper.getId()).build();
        List<Long> repoIds = paperRepoService.list(paperRepo).stream().map(PaperRepo::getRepoId).collect(Collectors.toList());
        if(repoIds.isEmpty()){
            throw new ServiceException("该试卷没有选择题库！");
        }
        Integer radioCount = paper.getRadioCount();
        Integer selectCount = paper.getSelectCount();
        Integer judgeCount = paper.getJudgeCount();
        List<Long> questionIds = Lists.newArrayListWithExpectedSize(radioCount + selectCount + judgeCount);
        if(radioCount > 0){
            List<Long> list = listIdByRepoIds(repoIds, QuestionTypeEnum.RADIO.getCode());
            Integer count = Math.min(list.size(), radioCount);
            Set<Long> set = RandomUtil.randomEleSet(list, count);
            questionIds.addAll(set);
        }
        if(selectCount > 0){
            List<Long> list = listIdByRepoIds(repoIds, QuestionTypeEnum.MULTIPLE.getCode());
            Integer count = Math.min(list.size(), radioCount);
            Set<Long> set = RandomUtil.randomEleSet(list, count);
            questionIds.addAll(set);
        }
        if(judgeCount > 0){
            List<Long> list = listIdByRepoIds(repoIds, QuestionTypeEnum.JUDGE.getCode());
            Integer count = Math.min(list.size(), radioCount);
            Set<Long> set = RandomUtil.randomEleSet(list, count);
            questionIds.addAll(set);
        }
        if(questionIds.isEmpty()){
            throw new ServiceException("生成试卷为空，请检查关联题库是否为空！");
        }

        // 根据题目id初始化生成相应记录，保证页面刷新后题目相同
        List<Record> recordList = questionIds.stream()
                .map(questionId -> Record.builder().userId(userId).examId(examId).questionId(questionId).finishFlag(FieldFlagEnum.EMPTY.getCode()).build())
                .collect(Collectors.toList());
        if (!recordService.saveBatch(recordList)) {
            throw new ServiceException("初始化失败，请刷新重试！");
        }
        return doGenerate(questionIds);
    }

    @Override
    public List<QuestionDetailResponseParam> doGenerate(List<Long> questionIds) {
        List<Question> questions = listByIds(questionIds);
        List<QuestionDetailResponseParam> questionList = Lists.newArrayListWithExpectedSize(questions.size());
        questions.stream().sorted(Comparator.comparing(Question::getType)).forEach(question -> {
            QuestionDetailResponseParam questionDetailResponseParam = QuestionDetailResponseParam.builder().id(question.getId()).content(question.getContent()).type(question.getType()).build();
            if(!ObjectUtil.equal(question.getType(), QuestionTypeEnum.JUDGE.getCode())){
                List<Answer> answerList = answerService.listByQuestionId(question.getId());
                answerList.forEach(a->a.setCorrectFlag(null));
                questionDetailResponseParam.setAnswerList(answerList);
            }
            questionList.add(questionDetailResponseParam);
        });

        return questionList;
    }

    private List<Long> listIdByRepoIds(List<Long> repoIds, Integer code) {
        return baseMapper.listIdByRepoIds(repoIds, code);
    }

    private Object countByType(Integer code) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type",code);
        return count(queryWrapper);
    }

    private void saveToRepo(Long id, List<Long> repoIds) throws ServiceException {
        for (Long repoId : repoIds) {
            QuestionRepo questionRepo = QuestionRepo.builder().questionId(id).repoId(repoId).build();
            if(!questionRepoService.saveOrUpdate(questionRepo)){
                throw new ServiceException("题目加入题库时发生异常");
            }
        }
    }

    private void solveCorrectAnswer(Question question, QuestionRequestParam data, Integer type) throws ServiceException {
        if(ObjectUtil.equal(type, QuestionTypeEnum.RADIO.getCode())){
            solveAnswerList(question, data.getRadioAnswerList(), data.getRadioCorrectionList(), true);
        }else if(ObjectUtil.equal(type, QuestionTypeEnum.MULTIPLE.getCode())){
            solveAnswerList(question, data.getMultipleAnswerList(), data.getMultipleCorrectionList(), false);
        }else if(ObjectUtil.equal(type, QuestionTypeEnum.JUDGE.getCode())){
            Integer judgeCorrection = data.getJudgeCorrection();
            question.setCorrectAnswerId(ObjectUtil.defaultIfBlank(String.valueOf(judgeCorrection),"0"));
        }else {
            throw new ServiceException("题目类型错误！");
        }
    }

    private void solveAnswerList(Question question, List<String> answerList, List<Integer> correctionList, boolean isRadio) throws ServiceException {
        if(answerList.size() > 7 || answerList.size() < 2){
            throw new ServiceException("选项数量不能多于7个或少于2个！");
        }
        List<Answer> saveAnswerList = Lists.newArrayListWithExpectedSize(7);
        for (int i = 0; i < answerList.size(); i++) {
            String answerTmp = answerList.get(i);
            Integer correctionTmp = correctionList.get(i);
            if(StrUtil.hasBlank(answerTmp)){
                throw new ServiceException("有选项内容为空！");
            }
            Answer answer = Answer.builder().content(answerTmp).correctFlag(ObjectUtil.defaultIfNull(correctionTmp,0)).questionId(question.getId()).build();
            saveAnswerList.add(answer);
        }
        if (!answerService.saveBatch(saveAnswerList)) {
            throw new ServiceException("添加答案时失败！");
        }
        List<Answer> correctAnswerList = saveAnswerList.stream().filter(a -> ObjectUtil.equal(a.getCorrectFlag(), 1)).collect(Collectors.toList());
        if((isRadio && correctAnswerList.size()!=1) || (!isRadio && correctAnswerList.size()<=1)){
            throw new ServiceException("答案数量与题目类型不匹配！");
        }
        if(isRadio){
            question.setCorrectAnswerId(String.valueOf(correctAnswerList.get(0).getId()));
        }else {
            question.setCorrectAnswerId(correctAnswerList.stream().map(a->String.valueOf(a.getId())).collect(Collectors.joining(",")));
        }
    }
}




