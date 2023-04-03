package cn.iurac.testsystem.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.iurac.testsystem.entity.*;
import cn.iurac.testsystem.enums.ExamStateEnum;
import cn.iurac.testsystem.enums.FieldFlagEnum;
import cn.iurac.testsystem.param.request.user.StudentPageRequestParam;
import cn.iurac.testsystem.param.request.user.UserPageRequestParam;
import cn.iurac.testsystem.param.response.chart.ExamAnalysisChartResponseParam;
import cn.iurac.testsystem.param.response.chart.GradeAnalysisChartResponseParam;
import cn.iurac.testsystem.service.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.iurac.testsystem.mapper.UserMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.shiro.SecurityUtils;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */
@Service("UserService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
implements UserService{

    @Resource
    GradeService gradeService;
    @Resource
    ExamServiceImpl examService;
    @Resource
    PaperService paperService;
    @Resource
    UserClazzService userClazzService;

    @Override
    public boolean existUserByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        User user = getOne(queryWrapper, false);
        return ObjectUtil.isNotNull(user);
    }

    @Override
    public Page<User> listForPage(Page<User> page, UserPageRequestParam param, boolean listAdmin) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(param.getUsername()),"username",param.getUsername())
        .like(StrUtil.isNotBlank(param.getName()),"name",param.getName())
        .eq("lock_flag",param.getLockFlag())
        .ge(StrUtil.isNotBlank(param.getStartTime()),"create_time",param.getStartTime())
        .le(StrUtil.isNotBlank(param.getEndTime()),"create_time", param.getEndTime());
        if(listAdmin){
            queryWrapper.in("role","3","4");
        }else {
            queryWrapper.eq("role",param.getRole());
        }

        return page(page,queryWrapper);
    }

    @Override
    public List<User> listUser(Integer... role) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("role", Arrays.stream(role).collect(Collectors.toList()));
        return list(queryWrapper);
    }

    @Override
    public boolean lockById(Long id) {
        User user = getById(id);
        if(ObjectUtil.isNull(user)) return false;

        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id)
                .set("lock_flag", ObjectUtil.equal(user.getLockFlag(), 0)?1:0);
        return update(updateWrapper);
    }

    @Override
    public User getByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        return getOne(queryWrapper);
    }

    @Override
    public List<User> listByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(username),"username",username);
        return list(queryWrapper);
    }

    @Override
    public Page<User> listStudentForPage(Page<User> page, StudentPageRequestParam param) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(param.getUsername()),"username",param.getUsername())
                .like(StrUtil.isNotBlank(param.getName()),"name",param.getName())
                .eq("role",1)
                .in(CollectionUtil.isNotEmpty(param.getStudentIds()),"id",param.getStudentIds());
        return page(page,queryWrapper);
    }

    @Override
    public GradeAnalysisChartResponseParam gradeAnalysis(Long id) {
        GradeAnalysisChartResponseParam param = new GradeAnalysisChartResponseParam();
        Grade grade = Grade.builder().userId(id).build();
        List<Grade> list = gradeService.list(grade)
                .stream()
                .filter(g->ObjectUtil.notEqual(g.getFinishFlag(),FieldFlagEnum.NORMAL.getCode())&&ObjectUtil.notEqual(g.getFinishFlag(),FieldFlagEnum.FINISH.getCode()))
                .collect(Collectors.toList());
        List<Integer> scoreList = Lists.newArrayListWithExpectedSize(list.size());
        List<Date> dateList = Lists.newArrayListWithExpectedSize(list.size());
        list.stream()
                .sorted(Comparator.comparing(Grade::getCreateTime))
                .collect(Collectors.toList())
                .subList(Math.max(list.size()-10,0),list.size())
                .forEach(g ->{
                    scoreList.add(g.getUserScore());
                    dateList.add(g.getCreateTime());
                });
        param.setScoreList(scoreList);
        param.setDateList(dateList);
        return param;
    }

    @Override
    public Integer countByRole(Integer code) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role",code);
        return count(queryWrapper);
    }

    @Override
    public ExamAnalysisChartResponseParam userAnalysis(Integer beginOffset, Integer length){
        ExamAnalysisChartResponseParam param = new ExamAnalysisChartResponseParam();
        List<Integer> countList = Lists.newArrayListWithExpectedSize(length);
        List<Date> dateList = Lists.newArrayListWithExpectedSize(length);
        Date today = DateUtil.parse(DateUtil.today(), "yyyy-MM-dd");
        for (int i = beginOffset; i < beginOffset+length; i++) {
            Date cur = DateUtil.offsetDay(today, i);
            dateList.add(cur);
            countList.add(gradeService.countByDate(cur));
        }
        param.setDateList(dateList);
        param.setCountList(countList);
        return param;
    }

    @Override
    public GradeAnalysisChartResponseParam avgGradeAnalysis(Long id) {
        GradeAnalysisChartResponseParam param = new GradeAnalysisChartResponseParam();

        Exam exam = Exam.builder().createUserId(id).state(ExamStateEnum.END.getCode()).build();
        List<Exam> exams = examService.listLastExam(exam);
        List<Integer> scoreList = Lists.newArrayListWithExpectedSize(7);
        List<String> examList = Lists.newArrayListWithExpectedSize(7);
        exams.subList(Math.max(exams.size() - 7, 0),exams.size()).forEach(e->{
            Grade grade = Grade.builder().examId(e.getId()).finishFlag(FieldFlagEnum.END.getCode()).build();
            double avg = gradeService.list(grade).stream().mapToDouble(g -> Double.valueOf(g.getUserScore())).average().orElse(0D);
            examList.add(e.getTitle());
            scoreList.add(NumberUtil.round(avg,2).intValue());
        });
        CollectionUtil.reverse(scoreList);
        CollectionUtil.reverse(examList);
        param.setScoreList(scoreList);
        param.setExamList(examList);
        return param;
    }

    @Override
    public GradeAnalysisChartResponseParam stateGradeAnalysis(Long id) {
        GradeAnalysisChartResponseParam param = new GradeAnalysisChartResponseParam();

        Exam exam = Exam.builder().createUserId(id).state(ExamStateEnum.END.getCode()).build();
        List<Exam> exams = examService.list(exam).stream().sorted(Comparator.comparing(Exam::getEndTime)).collect(Collectors.toList());
        List<Long> unPassCountList = Lists.newArrayListWithExpectedSize(7);
        List<Long> passCountList = Lists.newArrayListWithExpectedSize(7);
        List<Long> goodCountList = Lists.newArrayListWithExpectedSize(7);
        List<Long> excellentCountList = Lists.newArrayListWithExpectedSize(7);
        List<String> examList = Lists.newArrayListWithExpectedSize(7);
        exams.subList(Math.max(exams.size() - 7, 0),exams.size()).forEach(e->{
            Grade grade = Grade.builder().examId(e.getId()).build();
            List<Integer> gradeList = gradeService.list(grade).stream().map(Grade::getUserScore).collect(Collectors.toList());
            Long paperId = e.getPaperId();
            Paper paper = paperService.getById(paperId);
            Double total = Convert.toDouble(paper.getScore());
            Double pass = NumberUtil.mul(total,(Double)0.6);
            Double good = NumberUtil.mul(total,(Double)0.8);
            Double excellent = NumberUtil.mul(total,(Double)0.9);
            unPassCountList.add(gradeList.stream().filter(g->(NumberUtil.compare(g,0)>=0&&NumberUtil.compare(g,pass)<0)).count());
            passCountList.add(gradeList.stream().filter(g->(NumberUtil.compare(g,pass)>=0&&NumberUtil.compare(g,good)<0)).count());
            goodCountList.add(gradeList.stream().filter(g->(NumberUtil.compare(g,good)>=0&&NumberUtil.compare(g,excellent)<0)).count());
            excellentCountList.add(gradeList.stream().filter(g->(NumberUtil.compare(g,excellent)>=0&&NumberUtil.compare(g,total)<=0)).count());
            examList.add(e.getTitle());
        });

        Map<String, List<Long>> map = Maps.newHashMapWithExpectedSize(4);
        map.put("unPass",unPassCountList);
        map.put("pass",passCountList);
        map.put("good",goodCountList);
        map.put("excellent",excellentCountList);
        param.setExamList(examList);
        param.setScoreStateMap(map);
        return param;
    }

    @Override
    public List<Long> listStudentByExamId(Long examId) {
        return this.baseMapper.listByExamId(examId);
    }

    @Override
    public boolean checkExamPerm(Long examId) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        UserClazz userClazz = UserClazz.builder().userId(user.getId()).build();
        List<Long> clazzIds = userClazzService.list(userClazz).stream().map(UserClazz::getClazzId).collect(Collectors.toList());
        List<Long> examIds = examService.getBaseMapper().checkExamPerm(clazzIds);
        return CollectionUtil.contains(examIds,examId);
    }
}




