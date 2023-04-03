package cn.iurac.testsystem.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.iurac.testsystem.entity.*;
import cn.iurac.testsystem.enums.ExamStateEnum;
import cn.iurac.testsystem.enums.FieldFlagEnum;
import cn.iurac.testsystem.exception.JobException;
import cn.iurac.testsystem.exception.ServiceException;
import cn.iurac.testsystem.job.ExamStateHelper;
import cn.iurac.testsystem.param.request.exam.ExamPageRequestParam;
import cn.iurac.testsystem.param.request.exam.ExamRequestParam;
import cn.iurac.testsystem.param.response.chart.ExamAnalysisChartResponseParam;
import cn.iurac.testsystem.param.response.page.ExamPageResponseParam;
import cn.iurac.testsystem.service.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.iurac.testsystem.mapper.ExamMapper;
import com.google.common.collect.Lists;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@Service("ExamService")
public class ExamServiceImpl extends ServiceImpl<ExamMapper, Exam>
implements ExamService{

    @Resource
    private ExamClazzService examClazzService;
    @Resource
    private UserClazzService userClazzService;
    @Resource
    private ClazzService clazzService;
    @Resource
    private GradeService gradeService;

    @Override
    public Page<ExamPageResponseParam> listForPage(Page<ExamPageResponseParam> page, ExamPageRequestParam param) {
        Page<ExamPageResponseParam> examPageResponseParamPage = baseMapper.listForPage(page, param);
        examPageResponseParamPage.getRecords().forEach(exam -> {
            ExamClazz examClazz = ExamClazz.builder().examId(exam.getId()).build();
            List<Long> clazzIds = examClazzService.list(examClazz).stream().map(ExamClazz::getClazzId).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(clazzIds)){
                exam.setClazzList(clazzService.listByIds(clazzIds));
            }
        });
        return examPageResponseParamPage;
    }

    @Override
    @Transactional(rollbackFor = {ServiceException.class,Exception.class})
    public boolean save(ExamRequestParam data) throws ServiceException, JobException {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        Exam exam = Exam.builder()
                .title(data.getTitle())
                .content(data.getContent())
                .paperId(data.getPaperId())
                .startTime(data.getExamStartTime())
                .endTime(data.getExamEndTime())
                .state(ExamStateEnum.NOTSTART.getCode())
                .time(data.getTime())
                .createTime(DateUtil.date())
                .updateTime(DateUtil.date())
                .createUserId(user.getId())
                .deleteFlag(FieldFlagEnum.NORMAL.getCode())
                .build();
        if (!save(exam)) {
            throw new ServiceException("添加考试时失败！");
        }
        checkExecuteJob(getById(exam.getId()));
        List<Long> clazzIds = data.getClazzIds();
        List<ExamClazz> examClazzList = Lists.newArrayListWithExpectedSize(data.getClazzIds().size());
        clazzIds.forEach(clazzId ->{
            ExamClazz examClazz = ExamClazz.builder().clazzId(clazzId).examId(exam.getId()).build();
            examClazzList.add(examClazz);
        });
        if (!examClazzService.saveBatch(examClazzList)) {
            throw new ServiceException("班级绑定考试时失败！");
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = {ServiceException.class,Exception.class})
    public boolean update(Long id, ExamRequestParam data) throws ServiceException, JobException {
        Exam exam = getById(id);
        exam.setTitle(data.getTitle());
        exam.setContent(data.getContent());
        exam.setPaperId(data.getPaperId());
        exam.setStartTime(data.getExamStartTime());
        exam.setEndTime(data.getExamEndTime());
        exam.setTime(data.getTime());
        exam.setUpdateTime(DateUtil.date());
        if (!updateById(exam)) {
            throw new ServiceException("添加考试时失败！");
        }
        checkExecuteJob(getById(id));
        ExamClazz examClazz = ExamClazz.builder().examId(id).build();
        List<Long> preClazzIds = examClazzService.list(examClazz).stream().map(ExamClazz::getClazzId).collect(Collectors.toList());
        List<Long> nowClazzIds = data.getClazzIds();
        List<Long> deleteList = preClazzIds.stream().filter(x -> !CollectionUtil.contains(nowClazzIds, x)).collect(Collectors.toList());
        for (Long clazzId : deleteList) {
            ExamClazz tmp = ExamClazz.builder().clazzId(clazzId).examId(id).build();
            tmp = examClazzService.get(tmp);
            if (!examClazzService.removeById(tmp.getId())) {
                throw new ServiceException("原班级对考试解除绑定时失败！");
            }
        }
        List<Long> saveList = nowClazzIds.stream().filter(x -> !CollectionUtil.contains(preClazzIds, x)).collect(Collectors.toList());
        List<ExamClazz> examClazzList = Lists.newArrayListWithExpectedSize(saveList.size());
        saveList.forEach(clazzId ->{
            ExamClazz tmp = ExamClazz.builder().clazzId(clazzId).examId(id).build();
            examClazzList.add(tmp);
        });
        if (CollectionUtil.isNotEmpty(examClazzList) && !examClazzService.saveBatch(examClazzList)) {
            throw new ServiceException("班级绑定考试时失败！");
        }
        return true;
    }

    @Override
    public List<Exam> listWillUpdate(DateTime date, Long perMonitorMs, boolean isEnd) {
        DateTime dateTime = new DateTime(date.getTime()+perMonitorMs);
        QueryWrapper<Exam> queryWrapper = new QueryWrapper<>();
        if(isEnd){
            queryWrapper.le("end_time",dateTime)
                    .eq("state",ExamStateEnum.LOADING.getCode());
        }else {
            queryWrapper.le("start_time",dateTime)
                    .eq("state",ExamStateEnum.NOTSTART.getCode());
        }
        return list(queryWrapper);
    }

    @Override
    public Page<ExamPageResponseParam> listExamByUserId(Page<ExamPageResponseParam> page, Long id) {
        UserClazz userClazz = UserClazz.builder().userId(id).build();
        List<Long> clazzIds = userClazzService.list(userClazz).stream().map(UserClazz::getClazzId).collect(Collectors.toList());
        if(CollectionUtil.isEmpty(clazzIds)){
            return page;
        }
        return baseMapper.listExamByClazzIds(page, clazzIds);
    }

    @Override
    public ExamAnalysisChartResponseParam examAnalysis(Integer beginOffset, Integer length) {
        ExamAnalysisChartResponseParam param = new ExamAnalysisChartResponseParam();
        List<Integer> countList = Lists.newArrayListWithExpectedSize(length);
        List<Date> dateList = Lists.newArrayListWithExpectedSize(length);
        Date today = DateUtil.parse(DateUtil.today(), "yyyy-MM-dd");
        for (int i = beginOffset; i < beginOffset+length; i++) {
            Date cur = DateUtil.offsetDay(today, i);
            dateList.add(cur);
            countList.add(countByDate(cur));
        }
        param.setDateList(dateList);
        param.setCountList(countList);
        return param;
    }

    @Override
    public List<Exam> list(Exam exam) {
        QueryWrapper<Exam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjectUtil.isNotNull(exam.getCreateUserId()),"create_user_id",exam.getCreateUserId())
                .eq(ObjectUtil.isNotNull(exam.getState()),"state",exam.getState());
        return list(queryWrapper);
    }

    @Override
    public Page<ExamPageResponseParam> listAnalysisForPage(Page<ExamPageResponseParam> p, Long id) {
        Page<ExamPageResponseParam> examPageResponseParamPage = baseMapper.listAnalysisForPage(p, id);
        examPageResponseParamPage.getRecords().forEach(exam -> {
            ExamClazz examClazz = ExamClazz.builder().examId(exam.getId()).build();
            List<Long> clazzIds = examClazzService.list(examClazz).stream().map(ExamClazz::getClazzId).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(clazzIds)){
                exam.setClazzList(clazzService.listByIds(clazzIds));
            }

            Grade absentGrade = Grade.builder().examId(exam.getId()).finishFlag(FieldFlagEnum.ABSENT.getCode()).build();
            Integer absent = gradeService.list(absentGrade).size();
            exam.setAbsent(absent);

            Grade grade = Grade.builder().examId(exam.getId()).finishFlag(FieldFlagEnum.END.getCode()).build();
            List<Double> scoreList = gradeService.list(grade).stream().map(g->Convert.toDouble(g.getUserScore())).collect(Collectors.toList());
            Integer attend = scoreList.size();
            Double total = Convert.toDouble(exam.getTotal());
            Integer pass = Convert.toInt(scoreList.stream().filter(g->(NumberUtil.compare(g,NumberUtil.mul(total,(Double)0.6))>=0&&NumberUtil.compare(g,total)<=0)).count());
            Double max = 0D;
            Double min = 0D;
            Double avg = 0D;
            if(CollectionUtil.isNotEmpty(scoreList)){
                max = CollectionUtil.max(scoreList);
                min = CollectionUtil.isEmpty(scoreList)?0D:CollectionUtil.min(scoreList);
                avg = scoreList.stream().mapToDouble(t->t).average().orElse(0D);
            }
            exam.setAttend(attend);
            exam.setMax(max);
            exam.setMin(min);
            exam.setAvg(Double.parseDouble(NumberUtil.roundStr(avg,2)));
            exam.setPass(pass);
        });
        return examPageResponseParamPage;
    }

    @Override
    public List<Exam> listJustFinished() {
        QueryWrapper<Exam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("state",ExamStateEnum.FINISH.getCode())
                .le("end_time",DateUtil.date());
        return list(queryWrapper);
    }

    @Override
    public boolean existByPaperId(Long paperId) {
        QueryWrapper<Exam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("paper_id",paperId).last("limit 1");
        return count(queryWrapper) > 0;
    }

    private Integer countByDate(Date today) {
        QueryWrapper<Exam> queryWrapper = new QueryWrapper<>();
        queryWrapper.likeRight("start_time",DateUtil.formatDate(today));
        return count(queryWrapper);
    }

    public List<Exam> listLastExam(Exam exam) {
        QueryWrapper<Exam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjectUtil.isNotNull(exam.getCreateUserId()),"create_user_id",exam.getCreateUserId())
                .eq(ObjectUtil.isNotNull(exam.getState()),"state",exam.getState())
                .orderByDesc("end_time")
                .last("limit 7");
        return list(queryWrapper);
    }

    private void checkExecuteJob(Exam exam) throws JobException {
        long updateStatusTime = DateUtil.current(false) + ExamStateHelper.PER_MONITOR_MS;
        if(exam.getStartTime().getTime() < updateStatusTime){
            ExamStateHelper instance = ExamStateHelper.getInstance();
            exam.setState(ExamStateEnum.LOADING.getCode());
            instance.execute(new ExamStateHelper.UpdateStateJob(exam, new DateTime(exam.getStartTime())));
        }
    }
}




