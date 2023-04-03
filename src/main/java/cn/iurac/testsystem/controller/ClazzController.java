package cn.iurac.testsystem.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.iurac.testsystem.entity.Clazz;
import cn.iurac.testsystem.entity.Paper;
import cn.iurac.testsystem.entity.User;
import cn.iurac.testsystem.entity.UserClazz;
import cn.iurac.testsystem.enums.RoleEnum;
import cn.iurac.testsystem.exception.ServiceException;
import cn.iurac.testsystem.log.OperationLog;
import cn.iurac.testsystem.param.PageRequestParam;
import cn.iurac.testsystem.param.RequestDataParam;
import cn.iurac.testsystem.param.ResponseParam;
import cn.iurac.testsystem.param.request.clazz.ClazzPageRequestParam;
import cn.iurac.testsystem.param.request.clazz.ClazzRequestParam;
import cn.iurac.testsystem.param.request.clazz.ClazzPageRequestParam;
import cn.iurac.testsystem.param.request.clazz.ClazzRequestParam;
import cn.iurac.testsystem.service.ClazzService;
import cn.iurac.testsystem.service.UserClazzService;
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
import java.util.stream.Collectors;

@RestController
@Slf4j
@Api("班级controller")
@RequestMapping("/clazz")
public class ClazzController {

    @Resource
    private ClazzService clazzService;
    @Resource
    private UserClazzService userClazzService;

    @RequiresRoles(value = {"teacher","admin","superadmin"},logical = Logical.OR)
    @GetMapping("/listByExamId/{id}")
    @ApiOperation("班级管理：根据考试id查询班级列表")
    @OperationLog("班级管理：根据考试id查询班级列表")
    public ResponseParam listByExamId(@PathVariable("id") Long id) {
        return new ResponseParam(clazzService.listByExamId(id));
    }

    @RequiresRoles(value = {"teacher","admin","superadmin"},logical = Logical.OR)
    @PostMapping("/listForPage")
    @ApiOperation("班级管理：查询班级")
    @OperationLog("班级管理：查询班级")
    public ResponseParam listForPage(@RequestBody PageRequestParam<ClazzPageRequestParam> req) {
        ClazzPageRequestParam data = req.getData();

        User user = (User) SecurityUtils.getSubject().getPrincipal();
        if(ObjectUtil.isNull(user) || RoleEnum.TEACHER.getCode().equals(user.getRole())){
            UserClazz userClazz = UserClazz.builder().userId(user.getId()).build();
            List<Long> clazzIds = userClazzService.list(userClazz).stream().map(UserClazz::getClazzId).collect(Collectors.toList());
            data.setClazzIds(clazzIds);
        }

        Page<Clazz> page = new Page<>(req.getPage(),req.getLimit());
        page = clazzService.listForPage(page,data);
        return new ResponseParam(page);
    }

    @RequiresRoles(value = {"teacher","admin","superadmin"},logical = Logical.OR)
    @PostMapping("/save")
    @ApiOperation("班级管理：添加班级")
    @OperationLog("班级管理：添加班级")
    public ResponseParam save(@Validated @RequestBody RequestDataParam<ClazzRequestParam> req) throws ServiceException {
        ClazzRequestParam data = req.getData();
        ResponseParam responseParam = new ResponseParam();
        boolean status = clazzService.save(data);
        if (!status) {
            responseParam = ResponseUtil.getErrorResponseParam("系统错误!");
        }
        return responseParam;
    }

    @RequiresRoles(value = {"teacher","admin","superadmin"},logical = Logical.OR)
    @PostMapping("/update/{id}")
    @ApiOperation("班级管理：更新班级")
    @OperationLog("班级管理：更新班级")
    public ResponseParam update(@PathVariable("id") Long id, @Validated @RequestBody RequestDataParam<ClazzRequestParam> req) throws ServiceException {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        Clazz clazz = clazzService.getById(id);
        if(ObjectUtil.isNull(user) || RoleEnum.TEACHER.getCode().equals(user.getRole())){
            if(!ObjectUtil.equal(clazz.getCreateUserId(), user.getId())){
                return ResponseUtil.getErrorResponseParam("您没有权限！");
            }
        }
        ClazzRequestParam data = req.getData();
        ResponseParam responseParam = new ResponseParam();
        clazz.setName(data.getName());
        clazz.setUpdateTime(DateUtil.date());
        boolean status = clazzService.updateById(clazz);
        if (!status) {
            responseParam = ResponseUtil.getErrorResponseParam("系统错误!");
        }
        return responseParam;
    }

    @RequiresRoles(value = {"teacher","admin","superadmin"},logical = Logical.OR)
    @PostMapping("/delete/{id}")
    @ApiOperation("班级管理：删除班级")
    @OperationLog("班级管理：删除班级")
    public ResponseParam delete(@PathVariable("id") Long id) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        if(ObjectUtil.isNull(user) || RoleEnum.TEACHER.getCode().equals(user.getRole())){
            Clazz clazz = clazzService.getById(id);
            if(!ObjectUtil.equal(clazz.getCreateUserId(), user.getId())){
                return ResponseUtil.getErrorResponseParam("您没有权限！");
            }
        }
        boolean status = clazzService.removeById(id);
        ResponseParam responseParam = new ResponseParam();
        if (!status) {
            responseParam = ResponseUtil.getErrorResponseParam("系统错误!");
        }
        return responseParam;
    }

    @RequiresRoles(value = {"teacher","admin","superadmin"},logical = Logical.OR)
    @GetMapping("/listByTeacherId/{id}")
    @ApiOperation("班级管理：查询某教师下的班级")
    @OperationLog("班级管理：查询某教师下的班级")
    public ResponseParam listByTeacherId(@PathVariable("id") Long id) {
        List<Clazz> clazzList = clazzService.listByTeacherId(id) ;
        return new ResponseParam(clazzList);
    }

    @RequiresRoles(value = {"teacher","admin","superadmin"},logical = Logical.OR)
    @GetMapping("/list")
    @ApiOperation("班级管理：查询全部班级")
    @OperationLog("班级管理：查询全部班级")
    public ResponseParam list() {
        List<Clazz> clazzList = clazzService.list();
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        if(ObjectUtil.isNull(user) || RoleEnum.TEACHER.getCode().equals(user.getRole())){
            clazzList = clazzList.stream().filter(clazz -> ObjectUtil.equal(clazz.getCreateUserId(), user.getId())).collect(Collectors.toList());
        }
        return new ResponseParam(clazzList);
    }

}
