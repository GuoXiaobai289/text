package cn.iurac.testsystem.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.iurac.testsystem.entity.User;
import cn.iurac.testsystem.enums.RoleEnum;
import cn.iurac.testsystem.log.OperationLog;
import cn.iurac.testsystem.param.ResponseParam;
import cn.iurac.testsystem.param.response.chart.ExamAnalysisChartResponseParam;
import cn.iurac.testsystem.param.response.chart.GradeAnalysisChartResponseParam;
import cn.iurac.testsystem.param.response.chart.PieChartResponseParam;
import cn.iurac.testsystem.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


@RestController
@Api("数据统计controller")
public class ChartDataController {

    @Resource
    private UserService userService;
    @Resource
    private RepoService repoService;
    @Resource
    private QuestionService questionService;
    @Resource
    private ExamService examService;
    @Resource
    private PaperService paperService;

    @GetMapping({"/chart/gradeAnalysis"})
    @RequiresRoles(value = {"student","superadmin"}, logical = Logical.OR)
    @ApiOperation("图表展示：学生成绩折线图")
    @OperationLog("图表展示：学生成绩折线图")
    public ResponseParam gradeAnalysis(){
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        GradeAnalysisChartResponseParam param = userService.gradeAnalysis(user.getId());
        return new ResponseParam(param);
    }

    @GetMapping({"/chart/teacherAvgGradeChart"})
    @RequiresRoles(value = {"teacher","superadmin"}, logical = Logical.OR)
    @ApiOperation("图表展示：教师管理主页-学生平均成绩折线图")
    @OperationLog("图表展示：教师管理主页-学生平均成绩折线图")
    public ResponseParam teacherAvgGradeChart(){
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        GradeAnalysisChartResponseParam param = userService.avgGradeAnalysis(user.getId());
        return new ResponseParam(param);
    }

    @GetMapping({"/chart/teacherStateGradeChart"})
    @RequiresRoles(value = {"teacher","superadmin"}, logical = Logical.OR)
    @ApiOperation("图表展示：教师管理主页-学生成绩阶梯图")
    @OperationLog("图表展示：教师管理主页-学生成绩阶梯图")
    public ResponseParam teacherStateGradeChart(){
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        GradeAnalysisChartResponseParam param = userService.stateGradeAnalysis(user.getId());
        return new ResponseParam(param);
    }

    @GetMapping({"/chart/adminExamChart"})
    @RequiresRoles(value = {"admin","superadmin"}, logical = Logical.OR)
    @ApiOperation("图表展示：后台管理主页-考试折线图")
    @OperationLog("图表展示：后台管理主页-考试折线图")
    public ResponseParam adminExamChart(){
        ExamAnalysisChartResponseParam param = examService.examAnalysis(-7 , 7);
        return new ResponseParam(param);
    }

    @GetMapping({"/chart/adminUserChart"})
    @RequiresRoles(value = {"admin","superadmin"}, logical = Logical.OR)
    @ApiOperation("图表展示：后台管理主页-人数折线图")
    @OperationLog("图表展示：后台管理主页-人数折线图")
    public ResponseParam adminUserChart(){
        ExamAnalysisChartResponseParam param = userService.userAnalysis(-7 , 7);
        return new ResponseParam(param);
    }

    @GetMapping({"/chart/adminQuestionChart"})
    @RequiresRoles(value = {"admin","superadmin"}, logical = Logical.OR)
    @ApiOperation("图表展示：后台管理主页-题目分布饼图")
    @OperationLog("图表展示：后台管理主页-题目分布饼图")
    public ResponseParam adminQuestionChart(){
        Map<String,PieChartResponseParam> param = new HashMap<>();
        param.put("repo",questionService.repoAnalysis());
        param.put("type",questionService.typeAnalysis());
        return new ResponseParam(param);
    }

    @GetMapping({"/admin/index"})
    @RequiresRoles(value = {"admin","superadmin"}, logical = Logical.OR)
    @ApiOperation("数据统计：管理后台首页")
    @OperationLog("数据统计：管理后台首页")
    public ModelAndView adminIndexData(){
        ModelAndView modelAndView = new ModelAndView();
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        if(ObjectUtil.isNotNull(user)){
            modelAndView.addObject("userInfo",user);
        }

        Map<String, Integer> data = new HashMap<>();
        data.put("student",userService.countByRole(RoleEnum.STUDENT.getCode()));
        data.put("teacher",userService.countByRole(RoleEnum.TEACHER.getCode()));
        data.put("repo",repoService.count());
        data.put("question",questionService.count());
        data.put("exam",examService.count());
        data.put("paper",paperService.count());
        modelAndView.addAllObjects(data);

        modelAndView.setViewName("admin/index");
        return modelAndView;
    }

}
