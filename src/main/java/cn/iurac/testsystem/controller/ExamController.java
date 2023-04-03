package cn.iurac.testsystem.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.iurac.testsystem.entity.*;
import cn.iurac.testsystem.enums.ExamStateEnum;
import cn.iurac.testsystem.enums.FieldFlagEnum;
import cn.iurac.testsystem.enums.RoleEnum;
import cn.iurac.testsystem.exception.JobException;
import cn.iurac.testsystem.exception.ServiceException;
import cn.iurac.testsystem.log.OperationLog;
import cn.iurac.testsystem.param.PageRequestParam;
import cn.iurac.testsystem.param.RequestDataParam;
import cn.iurac.testsystem.param.ResponseParam;
import cn.iurac.testsystem.param.request.exam.ExamPageRequestParam;
import cn.iurac.testsystem.param.request.exam.ExamRequestParam;
import cn.iurac.testsystem.param.response.page.ExamPageResponseParam;
import cn.iurac.testsystem.param.response.QuestionDetailResponseParam;
import cn.iurac.testsystem.service.*;
import cn.iurac.testsystem.util.ResponseUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@Slf4j
@Api("考试controller")
@RequestMapping("/exam")
public class ExamController {

    @Resource
    private ExamService examService;
    @Resource
    private PaperService paperService;
    @Resource
    private QuestionService questionService;
    @Resource
    private ClazzService clazzService;
    @Resource
    private GradeService gradeService;
    @Resource
    private UserService userService;
    @Resource
    private RecordService recordService;

    @RequiresRoles(value = {"teacher","superadmin"},logical = Logical.OR)
    @PostMapping("/listAnalysisForPage")
    @ApiOperation("考试管理：考试分析")
    @OperationLog("考试管理：考试分析")
    public ResponseParam listAnalysisForPage(@RequestParam(value = "page",defaultValue = "1") Long page, @RequestParam(value = "limit",defaultValue = "10") Long limit) {
        page = ObjectUtil.defaultIfNull(page,0L);
        limit = ObjectUtil.defaultIfNull(limit,0L);
        Page<ExamPageResponseParam> p = new Page<>(page,limit);
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        p = examService.listAnalysisForPage(p,user.getId());
        return new ResponseParam(p);
    }

    @RequiresRoles(value = {"teacher","admin","superadmin"},logical = Logical.OR)
    @PostMapping("/listForPage")
    @ApiOperation("考试管理：查询考试")
    @OperationLog("考试管理：查询考试")
    public ResponseParam listForPage(@RequestBody PageRequestParam<ExamPageRequestParam> req) {
        ExamPageRequestParam data = req.getData();
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        if(ObjectUtil.isNull(user) || RoleEnum.TEACHER.getCode().equals(user.getRole())){
            data.setCreateUserId(user.getId());
        }
        Page<ExamPageResponseParam> page = new Page<>(req.getPage(),req.getLimit());
        page = examService.listForPage(page,data);
        return new ResponseParam(page);
    }

    @RequiresRoles(value = {"teacher","admin","superadmin"},logical = Logical.OR)
    @PostMapping("/save")
    @ApiOperation("考试管理：添加考试")
    @OperationLog("考试管理：添加考试")
    public ResponseParam save(@Validated @RequestBody RequestDataParam<ExamRequestParam> req) throws ServiceException, JobException {
        ExamRequestParam data = req.getData();
        check(data);
        ResponseParam responseParam = new ResponseParam();
        boolean status = examService.save(data);
        if (!status) {
            responseParam = ResponseUtil.getErrorResponseParam("系统错误!");
        }
        return responseParam;
    }

    @RequiresRoles(value = {"teacher","admin","superadmin"},logical = Logical.OR)
    @PostMapping("/update/{id}")
    @ApiOperation("考试管理：更新考试")
    @OperationLog("考试管理：更新考试")
    public ResponseParam update(@PathVariable("id") Long id, @Validated @RequestBody RequestDataParam<ExamRequestParam> req) throws ServiceException, JobException {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        Exam exam = examService.getById(id);
        if(ObjectUtil.isNull(user) || RoleEnum.TEACHER.getCode().equals(user.getRole())){
            if(!ObjectUtil.equal(exam.getCreateUserId(), user.getId())){
                return ResponseUtil.getErrorResponseParam("您没有权限！");
            }
        }

        if(ObjectUtil.notEqual(exam.getState(), ExamStateEnum.NOTSTART.getCode())){
            return ResponseUtil.getErrorResponseParam("考试已经开始！");
        }

        ExamRequestParam data = req.getData();
        check(data);
        ResponseParam responseParam = new ResponseParam();
        boolean status = examService.update(id,data);
        if (!status) {
            responseParam = ResponseUtil.getErrorResponseParam("系统错误!");
        }
        return responseParam;
    }

    @RequiresRoles(value = {"teacher","admin","superadmin"},logical = Logical.OR)
    @PostMapping("/delete/{id}")
    @ApiOperation("考试管理：删除考试")
    @OperationLog("考试管理：删除考试")
    public ResponseParam delete(@PathVariable("id") Long id) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        if(ObjectUtil.isNull(user) || RoleEnum.TEACHER.getCode().equals(user.getRole())){
            Exam exam = examService.getById(id);
            if(!ObjectUtil.equal(exam.getCreateUserId(), user.getId())){
                return ResponseUtil.getErrorResponseParam("您没有权限！");
            }
        }
        boolean status = examService.removeById(id);
        ResponseParam responseParam = new ResponseParam();
        if (!status) {
            responseParam = ResponseUtil.getErrorResponseParam("系统错误!");
        }
        return responseParam;
    }

    @RequiresRoles(value = {"student","superadmin"},logical = Logical.OR)
    @GetMapping("/listMyExamForPage/{page}")
    @ApiOperation("考试管理：学生查询考试列表")
    @OperationLog("考试管理：学生查询考试列表")
    public ResponseParam listExamByUserId(@PathVariable Integer page) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        Page<ExamPageResponseParam> p = new Page<>(page,6);
        p = examService.listExamByUserId(p,user.getId());
        return new ResponseParam(p);
    }

    @RequiresRoles(value = {"student","superadmin"}, logical = Logical.OR)
    @GetMapping({"/view/{id}"})
    @ApiOperation("考试管理：查看考试详情")
    @OperationLog("考试管理：查看考试详情")
    public ModelAndView toStudentExamPage(@PathVariable("id") String id) throws ServiceException {
        ModelAndView mv = new ModelAndView();

        Exam exam = examService.getById(id);
        if(ObjectUtil.isNull(exam) || ObjectUtil.isNull(exam.getPaperId())){
            throw new ServiceException("找不到该考试！");
        }else if(exam.getEndTime().getTime() < DateUtil.current(false)){
            throw new ServiceException("考试已经结束！");
        }
        Paper paper = paperService.getById(exam.getPaperId());
        if(ObjectUtil.isNull(paper)){
            throw new ServiceException("找不到试卷！");
        }
        mv.getModel().put("exam", exam);
        mv.getModel().put("paper",paper);
        mv.setViewName("student/exam/view");
        return mv;
    }

    @RequiresRoles(value = {"student","superadmin"}, logical = Logical.OR)
    @GetMapping({"/attend/{id}"})
    @ApiOperation("考试管理：参加考试")
    @OperationLog("考试管理：参加考试")
    public ModelAndView attendExam(@PathVariable("id") Long id) throws ServiceException {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        ModelAndView mv = new ModelAndView();
        if(!userService.checkExamPerm(id)){
            throw new ServiceException("找不到该考试！");
        }
        Exam exam = examService.getById(id);
        if(ObjectUtil.isNull(exam) || ObjectUtil.isNull(exam.getPaperId())){
            throw new ServiceException("找不到该考试！");
        }else if(exam.getEndTime().getTime() < DateUtil.current(false)){
            throw new ServiceException("考试已经结束！");
        }else if(exam.getStartTime().getTime() > DateUtil.current(false)){
            throw new ServiceException("考试未开始！");
        }
        Paper paper = paperService.getById(exam.getPaperId());
        if(ObjectUtil.isNull(paper)){
            throw new ServiceException("找不到试卷！");
        }
        Grade grade = Grade.builder()
                .userId(user.getId())
                .examId(id)
                .build();
        List<QuestionDetailResponseParam> questionList;
        Grade gradeDb = gradeService.getOne(grade);
        DateTime date = DateUtil.date();
        if(ObjectUtil.isNull(gradeDb)){
            grade = Grade.builder()
                    .userId(user.getId())
                    .examId(id)
                    .deleteFlag(FieldFlagEnum.NORMAL.getCode())
                    .finishFlag(FieldFlagEnum.NORMAL.getCode())
                    .attendTime(date)
                    .updateTime(date)
                    .createTime(date)
                    .build();
            questionList = questionService.generate(id,paper,user.getId(),grade);
        }else {
            grade = gradeDb;
            boolean finished = ObjectUtil.notEqual(FieldFlagEnum.NORMAL.getCode(), grade.getFinishFlag())
                    || date.getTime() <= (grade.getAttendTime().getTime() + TimeUnit.SECONDS.toMillis(exam.getTime()));
            if(finished){
                throw new ServiceException("您已提交试卷！");
            }
            Record record = Record.builder().userId(user.getId()).examId(id).build();
            List<Long> questionIds = recordService.list(record).stream().map(Record::getQuestionId).collect(Collectors.toList());
            questionList = questionService.doGenerate(questionIds);
        }
        mv.getModel().put("exam", exam);
        mv.getModel().put("paper",paper);
        mv.getModel().put("questionList",questionList);
        mv.getModel().put("grade",grade);
        mv.setViewName("student/exam/attend");
        return mv;
    }

    @RequiresRoles(value = {"student","superadmin"}, logical = Logical.OR)
    @PostMapping({"/submit/{id}"})
    @ApiOperation("考试管理：提交试卷")
    @OperationLog("考试管理：提交试卷")
    public ResponseParam submitExam(@PathVariable("id") Long id) throws ServiceException {
        Grade grade = gradeService.getById(id);
        if(ObjectUtil.isNull(grade)){
            throw new ServiceException("找不到提交的记录");
        }
        grade.setFinishFlag(FieldFlagEnum.FINISH.getCode());
        grade.setFinishTime(DateUtil.date());
        grade.setUpdateTime(DateUtil.date());
        boolean status = gradeService.updateById(grade);
        ResponseParam responseParam = new ResponseParam();
        if (!status) {
            responseParam = ResponseUtil.getErrorResponseParam("系统错误!");
        }
        return responseParam;

    }

    public void check(ExamRequestParam data) throws ServiceException {
        Date startTime = data.getExamStartTime();
        Date endTime = data.getExamEndTime();
        if(startTime.getTime() > endTime.getTime()){
            throw new ServiceException("结束时间不能早于开始时间");
        }
        Integer maxTime = Convert.toInt(DateUtil.between(data.getExamStartTime(), data.getExamEndTime(), DateUnit.MINUTE));
        if(maxTime < data.getTime()){
            data.setTime(maxTime);
        }
        if(ObjectUtil.isNull(paperService.getById(data.getPaperId()))){
            throw new ServiceException("找不到该试卷");
        }
        if(clazzService.listByIds(data.getClazzIds()).size()!=data.getClazzIds().size()){
            throw new ServiceException("找不到班级");
        }
    }

}
