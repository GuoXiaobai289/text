package cn.iurac.testsystem.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.iurac.testsystem.entity.*;
import cn.iurac.testsystem.enums.FieldFlagEnum;
import cn.iurac.testsystem.enums.RoleEnum;
import cn.iurac.testsystem.exception.UploadException;
import cn.iurac.testsystem.log.OperationLog;
import cn.iurac.testsystem.param.PageRequestParam;
import cn.iurac.testsystem.param.RequestDataParam;
import cn.iurac.testsystem.param.ResponseParam;
import cn.iurac.testsystem.param.request.grade.GradePageRequestParam;
import cn.iurac.testsystem.param.response.GradeExportResponseParam;
import cn.iurac.testsystem.param.response.page.GradePageResponseParam;
import cn.iurac.testsystem.service.ExamService;
import cn.iurac.testsystem.service.GradeService;
import cn.iurac.testsystem.service.UserService;
import cn.iurac.testsystem.util.ExcelUtil;
import cn.iurac.testsystem.util.ResponseUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.StringJoiner;

@RestController
@Slf4j
@Api("成绩controller")
@RequestMapping("/grade")
public class GradeController {

    @Resource
    private GradeService gradeService;

    @RequiresRoles(value = {"student","teacher","admin","superadmin"},logical = Logical.OR)
    @PostMapping("/listForPage")
    @ApiOperation("成绩管理：查询成绩")
    @OperationLog("成绩管理：查询成绩")
    public ResponseParam listForPage(@RequestBody PageRequestParam<GradePageRequestParam> req) {
        GradePageRequestParam data = req.getData();
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        if(ObjectUtil.isNull(user) || RoleEnum.TEACHER.getCode().equals(user.getRole())){
            data.setCreateUserId(user.getId());
        }
        Page<GradePageResponseParam> page = new Page<>(req.getPage(),req.getLimit());
        page = gradeService.listForPage(page, data);
        return new ResponseParam(page);
    }

    @RequiresRoles(value = {"teacher","superadmin"},logical = Logical.OR)
    @GetMapping("/export")
    @ApiOperation("成绩管理：导出成绩")
    @OperationLog("成绩管理：导出成绩")
    public void export(HttpServletResponse response
            , @RequestParam("username") String username
            , @RequestParam("title") String title
            , @RequestParam("startTime") String startTime
            , @RequestParam("endTime") String endTime) throws IOException, UploadException {
        GradePageRequestParam data = new GradePageRequestParam();
        data.setUsername(username);
        data.setTitle(title);
        data.setStartTime(startTime);
        data.setEndTime(endTime);
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        if(ObjectUtil.isNull(user) || RoleEnum.TEACHER.getCode().equals(user.getRole())){
            data.setCreateUserId(user.getId());
        }
        List<GradeExportResponseParam> list = gradeService.export(data);
        list.forEach(g->{
            Integer finishFlag = g.getFinishFlag();
            if(ObjectUtil.equal(finishFlag, FieldFlagEnum.END.getCode())){
                g.setFinishString("完成");
            }else if(ObjectUtil.equal(finishFlag, FieldFlagEnum.FINISH.getCode())){
                g.setFinishString("批卷中");
            }else if(ObjectUtil.equal(finishFlag, FieldFlagEnum.NORMAL.getCode())){
                g.setFinishString("答题中");
            }else if(ObjectUtil.equal(finishFlag, FieldFlagEnum.ABSENT.getCode())){
                g.setFinishString("缺考");
            }
        });
        String fileName = new StringJoiner("-")
                .add("学生成绩")
                .add(RandomUtil.randomString(4))
                .add(String.valueOf(DateUtil.current(false)))
                .toString();
        ExcelUtil.download(response,fileName,GradeExportResponseParam.class,list);
    }

    @RequiresRoles(value = {"admin","superadmin"},logical = Logical.OR)
    @PostMapping("/delete/{id}")
    @ApiOperation("成绩管理：删除成绩")
    @OperationLog("成绩管理：删除成绩")
    public ResponseParam delete(@PathVariable("id") Long id) {
        boolean status = gradeService.removeById(id);
        ResponseParam responseParam = new ResponseParam();
        if (!status) {
            responseParam = ResponseUtil.getErrorResponseParam("系统错误!");
        }
        return responseParam;
    }


}
