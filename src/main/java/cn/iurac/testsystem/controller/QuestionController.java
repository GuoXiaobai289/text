package cn.iurac.testsystem.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.iurac.testsystem.entity.Answer;
import cn.iurac.testsystem.entity.Question;
import cn.iurac.testsystem.entity.User;
import cn.iurac.testsystem.enums.QuestionTypeEnum;
import cn.iurac.testsystem.enums.RoleEnum;
import cn.iurac.testsystem.exception.ServiceException;
import cn.iurac.testsystem.log.OperationLog;
import cn.iurac.testsystem.param.PageRequestParam;
import cn.iurac.testsystem.param.RequestDataParam;
import cn.iurac.testsystem.param.ResponseParam;
import cn.iurac.testsystem.param.request.question.QuestionPageRequestParam;
import cn.iurac.testsystem.param.request.question.QuestionRequestParam;
import cn.iurac.testsystem.param.response.QuestionDetailResponseParam;
import cn.iurac.testsystem.service.AnswerService;
import cn.iurac.testsystem.service.QuestionService;
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

import javax.annotation.Resource;
import java.util.List;

@RestController
@Slf4j
@Api("题目controller")
@RequestMapping("/question")
public class QuestionController {

    @Resource
    private QuestionService questionService;
    @Resource
    private AnswerService answerService;

    @RequiresRoles(value = {"student","superadmin"},logical = Logical.OR)
    @PostMapping("/listWrongQuestion")
    @ApiOperation("题目管理：查询错题集")
    @OperationLog("题目管理：查询错题集")
    public ResponseParam listWrongQuestion(@RequestBody PageRequestParam<QuestionPageRequestParam> req) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        req.getData().setUserId(user.getId());
        return listForPage(req);
    }

    @RequiresRoles(value = {"teacher","admin","superadmin"},logical = Logical.OR)
    @PostMapping("/listForPage")
    @ApiOperation("题目管理：查询题目")
    @OperationLog("题目管理：查询题目")
    public ResponseParam listForPage(@RequestBody PageRequestParam<QuestionPageRequestParam> req) {
        QuestionPageRequestParam data = req.getData();
        Page<Question> page = new Page<>(req.getPage(),req.getLimit());
        page = questionService.listForPage(page,data);
        return new ResponseParam(page);
    }

    @RequiresRoles(value = {"teacher","admin","superadmin"},logical = Logical.OR)
    @PostMapping("/save")
    @ApiOperation("题目管理：添加题目")
    @OperationLog("题目管理：添加题目")
    public ResponseParam save(@Validated @RequestBody RequestDataParam<QuestionRequestParam> req) throws ServiceException {
        QuestionRequestParam data = req.getData();
        ResponseParam responseParam = new ResponseParam();
        boolean status = questionService.save(data);
        if (!status) {
            responseParam = ResponseUtil.getErrorResponseParam("系统错误!");
        }
        return responseParam;
    }

    @RequiresRoles(value = {"teacher","admin","superadmin"},logical = Logical.OR)
    @PostMapping("/update/{id}")
    @ApiOperation("题目管理：更新题目")
    @OperationLog("题目管理：更新题目")
    public ResponseParam update(@PathVariable("id") Long id, @Validated @RequestBody RequestDataParam<QuestionRequestParam> req) throws ServiceException {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        Question question = questionService.getById(id);
        if(ObjectUtil.isNull(user) || RoleEnum.TEACHER.getCode().equals(user.getRole())){
            if(!ObjectUtil.equal(question.getCreateUserId(), user.getId())){
                return ResponseUtil.getErrorResponseParam("您没有权限！");
            }
        }
        QuestionRequestParam data = req.getData();
        ResponseParam responseParam = new ResponseParam();
        boolean status = questionService.update(question, data);
        if (!status) {
            responseParam = ResponseUtil.getErrorResponseParam("系统错误!");
        }
        return responseParam;
    }

    @RequiresRoles(value = {"teacher","admin","superadmin"},logical = Logical.OR)
    @PostMapping("/delete/{id}")
    @ApiOperation("题目管理：删除题目")
    @OperationLog("题目管理：删除题目")
    public ResponseParam delete(@PathVariable("id") Long id) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        if(ObjectUtil.isNull(user) || RoleEnum.TEACHER.getCode().equals(user.getRole())){
            Question question = questionService.getById(id);
            if(!ObjectUtil.equal(question.getCreateUserId(), user.getId())){
                return ResponseUtil.getErrorResponseParam("您没有权限！");
            }
        }
        boolean status = questionService.removeById(id);
        ResponseParam responseParam = new ResponseParam();
        if (!status) {
            responseParam = ResponseUtil.getErrorResponseParam("系统错误!");
        }
        return responseParam;
    }

    @RequiresRoles(value = {"teacher","admin","superadmin"},logical = Logical.OR)
    @GetMapping("/getDetail/{id}")
    @ApiOperation("题目管理：查询题目详情")
    @OperationLog("题目管理：查询题目详情")
    public ResponseParam getDetail(@PathVariable("id") Long id) {
        Question question = questionService.getById(id);
        QuestionDetailResponseParam questionDetailResponseParam = QuestionDetailResponseParam.builder().content(question.getContent()).type(question.getType()).build();

        if(!ObjectUtil.equal(question.getType(), QuestionTypeEnum.JUDGE.getCode())){
            List<Answer> answerList = answerService.listByQuestionId(id);
            questionDetailResponseParam.setAnswerList(answerList);
        }else {
            questionDetailResponseParam.setJudgeCorrection(question.getCorrectAnswerId());
        }

        return new ResponseParam(questionDetailResponseParam);
    }


}
