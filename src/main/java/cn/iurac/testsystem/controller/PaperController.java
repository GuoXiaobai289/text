package cn.iurac.testsystem.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.iurac.testsystem.entity.Paper;
import cn.iurac.testsystem.entity.User;
import cn.iurac.testsystem.enums.RoleEnum;
import cn.iurac.testsystem.exception.ServiceException;
import cn.iurac.testsystem.log.OperationLog;
import cn.iurac.testsystem.param.PageRequestParam;
import cn.iurac.testsystem.param.RequestDataParam;
import cn.iurac.testsystem.param.ResponseParam;
import cn.iurac.testsystem.param.request.paper.PaperPageRequestParam;
import cn.iurac.testsystem.param.request.paper.PaperRequestParam;
import cn.iurac.testsystem.service.ExamService;
import cn.iurac.testsystem.service.PaperService;
import cn.iurac.testsystem.util.ResponseUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.context.annotation.Lazy;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@Api("试卷controller")
@RequestMapping("/paper")
public class PaperController {

    @Resource
    private PaperService paperService;
    @Resource
    private ExamService examService;

    @RequiresRoles(value = {"teacher","admin","superadmin"},logical = Logical.OR)
    @GetMapping("/list")
    @ApiOperation("试卷管理：查询试卷列表")
    @OperationLog("试卷管理：查询试卷列表")
    public ResponseParam list() {
        List<Paper> paperList = paperService.list();
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        if(ObjectUtil.isNull(user) || RoleEnum.TEACHER.getCode().equals(user.getRole())){
            paperList = paperList.stream().filter(paper -> ObjectUtil.equal(paper.getCreateUserId(), user.getId())).collect(Collectors.toList());
        }
        return new ResponseParam(paperList);
    }


    @RequiresRoles(value = {"teacher","admin","superadmin"},logical = Logical.OR)
    @PostMapping("/listForPage")
    @ApiOperation("试卷管理：查询试卷")
    @OperationLog("试卷管理：查询试卷")
    public ResponseParam listForPage(@RequestBody PageRequestParam<PaperPageRequestParam> req) {
        PaperPageRequestParam data = req.getData();
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        if(ObjectUtil.isNull(user) || RoleEnum.TEACHER.getCode().equals(user.getRole())){
            data.setCreateUserId(user.getId());
        }
        Page<Paper> page = new Page<>(req.getPage(),req.getLimit());
        page = paperService.listForPage(page,data);
        return new ResponseParam(page);
    }

    @RequiresRoles(value = {"teacher","admin","superadmin"},logical = Logical.OR)
    @PostMapping("/save")
    @ApiOperation("试卷管理：添加试卷")
    @OperationLog("试卷管理：添加试卷")
    public ResponseParam save(@Validated @RequestBody RequestDataParam<PaperRequestParam> req) throws ServiceException {
        PaperRequestParam data = req.getData();
        ResponseParam responseParam = new ResponseParam();
        boolean status = paperService.save(data);
        if (!status) {
            responseParam = ResponseUtil.getErrorResponseParam("系统错误!");
        }
        return responseParam;
    }

    @RequiresRoles(value = {"teacher","admin","superadmin"},logical = Logical.OR)
    @PostMapping("/delete/{id}")
    @ApiOperation("试卷管理：删除试卷")
    @OperationLog("试卷管理：删除试卷")
    public ResponseParam delete(@PathVariable("id") Long id) throws ServiceException {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        if(ObjectUtil.isNull(user) || RoleEnum.TEACHER.getCode().equals(user.getRole())){
            Paper paper = paperService.getById(id);
            if(!ObjectUtil.equal(paper.getCreateUserId(), user.getId())){
                return ResponseUtil.getErrorResponseParam("您没有权限！");
            }
        }
        if(examService.existByPaperId(id)){
            throw new ServiceException("该试卷有绑定考试，无法删除");
        }
        boolean status = paperService.removeById(id);
        ResponseParam responseParam = new ResponseParam();
        if (!status) {
            responseParam = ResponseUtil.getErrorResponseParam("系统错误!");
        }
        return responseParam;
    }


}
