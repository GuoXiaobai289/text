package cn.iurac.testsystem.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.iurac.testsystem.entity.*;
import cn.iurac.testsystem.enums.ExamStateEnum;
import cn.iurac.testsystem.enums.FieldFlagEnum;
import cn.iurac.testsystem.exception.ServiceException;
import cn.iurac.testsystem.log.OperationLog;
import cn.iurac.testsystem.param.RequestDataParam;
import cn.iurac.testsystem.param.ResponseParam;
import cn.iurac.testsystem.param.request.record.RecordRequestParam;
import cn.iurac.testsystem.param.response.QuestionDetailResponseParam;
import cn.iurac.testsystem.service.*;
import cn.iurac.testsystem.util.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@Api("做题记录controller")
@RequestMapping("/record")
public class RecordController {

    @Resource
    private RecordService recordService;
    @Resource
    private UserService userService;
    @Resource
    private ExamService examService;
    @Resource
    private GradeService gradeService;
    @Resource
    private QuestionService questionService;

    @RequiresRoles(value = {"student","superadmin"},logical = Logical.OR)
    @PostMapping("/saveOrUpdate/{id}")
    @ApiOperation("做题记录：保存记录")
    @OperationLog("做题记录：保存记录")
    public ResponseParam saveOrUpdate(@PathVariable("id")Long id, @RequestBody RequestDataParam<List<RecordRequestParam>> req) throws ServiceException {
        List<RecordRequestParam> data = req.getData();
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        boolean status = recordService.saveOrUpdate(user.getId(),id,data);
        ResponseParam responseParam = new ResponseParam();
        if (!status) {
            responseParam = ResponseUtil.getErrorResponseParam("系统错误!");
        }
        return responseParam;
    }

    @RequiresRoles(value = {"student","superadmin"},logical = Logical.OR)
    @GetMapping("/history/{id}")
    @ApiOperation("做题记录：查看试卷")
    @OperationLog("做题记录：查看试卷")
    public ModelAndView toHistoryPage(@PathVariable("id")Long id) throws ServiceException {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        ModelAndView mv = new ModelAndView();
        if(!userService.checkExamPerm(id)){
            throw new ServiceException("找不到该考试！");
        }
        Exam exam = examService.getById(id);
        if(ObjectUtil.isNull(exam) || ObjectUtil.isNull(exam.getPaperId())){
            throw new ServiceException("找不到该考试！");
        }else if(ObjectUtil.notEqual(exam.getState(), ExamStateEnum.END.getCode())){
            throw new ServiceException("考试还没有结束！");
        }
        Grade grade = Grade.builder()
                .userId(user.getId())
                .examId(id)
                .build();
        List<QuestionDetailResponseParam> questionList;
        grade = gradeService.getOne(grade);
        if(ObjectUtil.isNotNull(grade) && ObjectUtil.equal(grade.getFinishFlag(),FieldFlagEnum.END.getCode())){
            Record record = Record.builder().userId(user.getId()).examId(id).build();
            List<Long> questionIds = recordService.list(record).stream().map(Record::getQuestionId).collect(Collectors.toList());
            questionList = questionService.doGenerate(questionIds);
        }else {
            log.info("考试成绩信息：{}",grade);
            throw new ServiceException("您没有参加此次考试！");
        }
        mv.getModel().put("userInfo", user);
        mv.getModel().put("exam", exam);
        mv.getModel().put("questionList",questionList);
        mv.getModel().put("grade",grade);
        mv.setViewName("student/grade/history");
        return mv;
    }

    @RequiresRoles(value = {"student","superadmin"},logical = Logical.OR)
    @PostMapping("/history/{id}")
    @ApiOperation("做题记录：获取记录")
    @OperationLog("做题记录：获取记录")
    public ResponseParam history(@PathVariable("id")Long id) throws ServiceException {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        Record record = Record.builder().examId(id).userId(user.getId()).build();
        return new ResponseParam(recordService.list(record));
    }


}
