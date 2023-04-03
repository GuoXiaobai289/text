package cn.iurac.testsystem.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.iurac.testsystem.entity.Answer;
import cn.iurac.testsystem.entity.Question;
import cn.iurac.testsystem.entity.Repo;
import cn.iurac.testsystem.entity.User;
import cn.iurac.testsystem.enums.QuestionTypeEnum;
import cn.iurac.testsystem.enums.RoleEnum;
import cn.iurac.testsystem.exception.ServiceException;
import cn.iurac.testsystem.log.OperationLog;
import cn.iurac.testsystem.param.PageRequestParam;
import cn.iurac.testsystem.param.RequestDataParam;
import cn.iurac.testsystem.param.ResponseParam;
import cn.iurac.testsystem.param.request.repo.RepoPageRequestParam;
import cn.iurac.testsystem.param.request.repo.RepoRequestParam;
import cn.iurac.testsystem.service.QuestionRepoService;
import cn.iurac.testsystem.service.RepoService;
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
@Api("题库controller")
@RequestMapping("/repo")
public class RepoController {

    @Resource
    private RepoService repoService;

    @RequiresRoles(value = {"teacher","admin","superadmin"},logical = Logical.OR)
    @GetMapping("/list")
    @ApiOperation("题库管理：查询题库列表")
    @OperationLog("题库管理：查询题库列表")
    public ResponseParam listForPage() {
        return new ResponseParam(repoService.list());
    }

    @RequiresRoles(value = {"teacher","admin","superadmin"},logical = Logical.OR)
    @GetMapping("/listByQuestionId/{id}")
    @ApiOperation("题库管理：根据题目id查询题库列表")
    @OperationLog("题库管理：根据题目id查询题库列表")
    public ResponseParam listByQuestionId(@PathVariable("id") Long id) {
        return new ResponseParam(repoService.listByQuestionId(id));
    }

    @RequiresRoles(value = {"teacher","admin","superadmin"},logical = Logical.OR)
    @PostMapping("/listForPage")
    @ApiOperation("题库管理：查询题库")
    @OperationLog("题库管理：查询题库")
    public ResponseParam listForPage(@RequestBody PageRequestParam<RepoPageRequestParam> req) {
        RepoPageRequestParam data = req.getData();
        Page<Repo> page = new Page<>(req.getPage(),req.getLimit());
        page = repoService.listForPage(page,data);
        return new ResponseParam(page);
    }

    @RequiresRoles(value = {"teacher","admin","superadmin"},logical = Logical.OR)
    @PostMapping("/save")
    @ApiOperation("题库管理：添加题库")
    @OperationLog("题库管理：添加题库")
    public ResponseParam save(@Validated @RequestBody RequestDataParam<RepoRequestParam> req) throws ServiceException {
        RepoRequestParam data = req.getData();
        ResponseParam responseParam = new ResponseParam();
        boolean status = repoService.save(data);
        if (!status) {
            responseParam = ResponseUtil.getErrorResponseParam("系统错误!");
        }
        return responseParam;
    }

    @RequiresRoles(value = {"teacher","admin","superadmin"},logical = Logical.OR)
    @PostMapping("/update/{id}")
    @ApiOperation("题库管理：更新题库")
    @OperationLog("题库管理：更新题库")
    public ResponseParam update(@PathVariable("id") Long id, @Validated @RequestBody RequestDataParam<RepoRequestParam> req) throws ServiceException {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        Repo repo = repoService.getById(id);
        if(ObjectUtil.isNull(user) || RoleEnum.TEACHER.getCode().equals(user.getRole())){
            if(!ObjectUtil.equal(repo.getCreateUserId(), user.getId())){
                return ResponseUtil.getErrorResponseParam("您没有权限！");
            }
        }
        RepoRequestParam data = req.getData();
        ResponseParam responseParam = new ResponseParam();
        repo.setTitle(data.getTitle());
        repo.setUpdateTime(DateUtil.date());
        boolean status = repoService.updateById(repo);
        if (!status) {
            responseParam = ResponseUtil.getErrorResponseParam("系统错误!");
        }
        return responseParam;
    }

    @RequiresRoles(value = {"teacher","admin","superadmin"},logical = Logical.OR)
    @PostMapping("/delete/{id}")
    @ApiOperation("题库管理：删除题库")
    @OperationLog("题库管理：删除题库")
    public ResponseParam delete(@PathVariable("id") Long id) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        if(ObjectUtil.isNull(user) || RoleEnum.TEACHER.getCode().equals(user.getRole())){
            Repo repo = repoService.getById(id);
            if(!ObjectUtil.equal(repo.getCreateUserId(), user.getId())){
                return ResponseUtil.getErrorResponseParam("您没有权限！");
            }
        }
        boolean status = repoService.removeById(id);
        ResponseParam responseParam = new ResponseParam();
        if (!status) {
            responseParam = ResponseUtil.getErrorResponseParam("系统错误!");
        }
        return responseParam;
    }


}
