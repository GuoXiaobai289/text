package cn.iurac.testsystem.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.iurac.testsystem.constant.ImgDefaultName;
import cn.iurac.testsystem.entity.User;
import cn.iurac.testsystem.entity.UserClazz;
import cn.iurac.testsystem.enums.RoleEnum;
import cn.iurac.testsystem.exception.ServiceException;
import cn.iurac.testsystem.exception.UploadException;
import cn.iurac.testsystem.log.OperationLog;
import cn.iurac.testsystem.param.PageRequestParam;
import cn.iurac.testsystem.param.RequestDataParam;
import cn.iurac.testsystem.param.ResponseParam;
import cn.iurac.testsystem.param.request.user.RegisterUserParam;
import cn.iurac.testsystem.param.request.user.StudentPageRequestParam;
import cn.iurac.testsystem.param.request.user.UserPageRequestParam;
import cn.iurac.testsystem.param.request.user.UserRequestParam;
import cn.iurac.testsystem.service.UserClazzService;
import cn.iurac.testsystem.service.UserService;
import cn.iurac.testsystem.util.ResponseUtil;
import cn.iurac.testsystem.util.SaltUtil;
import com.baomidou.kaptcha.Kaptcha;
import com.baomidou.kaptcha.exception.KaptchaIncorrectException;
import com.baomidou.kaptcha.exception.KaptchaNotFoundException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
@Api("用户controller")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserClazzService userClazzService;
    @Autowired
    private Kaptcha kaptcha;

    @PostMapping("/login")
    @ResponseBody
    @ApiOperation("登录")
    @OperationLog("登录")
    public ResponseParam login(String username, String password, String code) {
        String msg = "";
        try {
            if (kaptcha.validate(code)){
                //获取主体对象
                Subject subject = SecurityUtils.getSubject();
                subject.login(new UsernamePasswordToken(username, password));
                log.info("{}登录成功",subject.getPrincipal());
                return new ResponseParam("登录成功");
            }
        } catch (KaptchaNotFoundException e) {
            msg="验证码已失效!";
        } catch (KaptchaIncorrectException e) {
            msg="验证码错误!";
        } catch (UnknownAccountException e) {
            msg="用户名错误!";
        } catch (IncorrectCredentialsException e) {
            msg="密码错误!";
        } catch (Exception e){
           throw e;
        }
        log.info("{}登录失败,{}",username,msg);
        return ResponseUtil.getErrorResponseParam(msg);
    }

    @GetMapping("logout")
    @ApiOperation("注销")
    @OperationLog("注销")
    public String logout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return "login";
    }

    @PostMapping("/register")
    @ResponseBody
    @ApiOperation("注册")
    @OperationLog("注册")
    public ResponseParam registerTeacher(@Validated @RequestBody RequestDataParam<RegisterUserParam> req) throws Exception {
        RegisterUserParam data = req.getData();
        if(ObjectUtil.notEqual(data.getRole(), RoleEnum.STUDENT.getCode()) && ObjectUtil.notEqual(data.getRole(), RoleEnum.TEACHER.getCode())){
            throw new ServiceException("注册失败");
        }
        UserRequestParam user = new UserRequestParam();
        BeanUtil.copyProperties(data, user);
        RequestDataParam<UserRequestParam> param = new RequestDataParam<>();
        param.setData(user);
        return save(param);
    }

    @RequiresUser
    @ResponseBody
    @PostMapping({"/user/information"})
    @ApiOperation("个人信息修改")
    @OperationLog("个人信息修改")
    public ResponseParam information(@Validated @RequestBody RequestDataParam<UserRequestParam> req){
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        UserRequestParam data = req.getData();
        data.setUsername(null);
        data.setRole(null);
        data.setId(user.getId());
        return update(data);
    }

    /*----------------------------------管理员------------------------------*/

    @PostMapping("/user/updateUser/{id}")
    @ResponseBody
    @ApiOperation("用户管理：修改用户")
    @OperationLog("用户管理：修改用户")
    @RequiresRoles(value = {"admin","superadmin"}, logical = Logical.OR)
    public ResponseParam updateUser(@PathVariable("id")Long id, @Validated @RequestBody RequestDataParam<UserRequestParam> req) throws Exception {
        Integer role = req.getData().getRole();
        if(!(RoleEnum.STUDENT.getCode().equals(role) || RoleEnum.TEACHER.getCode().equals(role))){
            return ResponseUtil.getErrorResponseParam("您没有权限！");
        }
        req.getData().setId(id);
        return update(req.getData());
    }

    @PostMapping("/user/saveUser")
    @ResponseBody
    @ApiOperation("用户管理：新增用户")
    @OperationLog("用户管理：新增用户")
    @RequiresRoles(value = {"admin","superadmin"}, logical = Logical.OR)
    public ResponseParam saveUser(@Validated @RequestBody RequestDataParam<UserRequestParam> req) throws Exception {
        Integer role = req.getData().getRole();
        if(!(RoleEnum.STUDENT.getCode().equals(role) || RoleEnum.TEACHER.getCode().equals(role))){
            return ResponseUtil.getErrorResponseParam("您没有权限！");
        }
        return save(req);
    }

    @RequiresRoles(value = {"admin","superadmin"}, logical = Logical.OR)
    @PostMapping("/user/listUserForPage")
    @ResponseBody
    @ApiOperation("用户管理：查询用户")
    @OperationLog("用户管理：查询用户")
    public ResponseParam listUserForPage(@Validated @RequestBody PageRequestParam<UserPageRequestParam> req) throws Exception {
        UserPageRequestParam data = req.getData();

        Page<User> page = new Page<>(req.getPage(),req.getLimit());
        page = userService.listForPage(page,data,false);

        return new ResponseParam(page);
    }

    @RequiresRoles(value = {"admin","superadmin"}, logical = Logical.OR)
    @PostMapping("/user/deleteUser/{id}")
    @ResponseBody
    @ApiOperation("用户管理：删除用户")
    @OperationLog("用户管理：删除用户")
    public ResponseParam deleteUser(@PathVariable("id")Long id) {
        User user = userService.getById(id);
        if(ObjectUtil.isNull(user) || !(RoleEnum.STUDENT.getCode().equals(user.getRole()) || RoleEnum.TEACHER.getCode().equals(user.getRole()))){
            return ResponseUtil.getErrorResponseParam("您没有权限！");
        }
        return deleteById(id);
    }

    @RequiresRoles(value = {"admin","superadmin"}, logical = Logical.OR)
    @PostMapping("/user/lockUser/{id}")
    @ResponseBody
    @ApiOperation("用户管理：封禁/解封用户")
    @OperationLog("用户管理：封禁/解封用户")
    public ResponseParam lockUser(@PathVariable("id")Long id) {
        User user = userService.getById(id);
        if(ObjectUtil.isNull(user) || !(RoleEnum.STUDENT.getCode().equals(user.getRole()) || RoleEnum.TEACHER.getCode().equals(user.getRole()))){
            return ResponseUtil.getErrorResponseParam("您没有权限！");
        }
        return lockById(id);
    }

    @RequiresRoles(value = {"admin","superadmin"}, logical = Logical.OR)
    @GetMapping("/user/listStudent")
    @ResponseBody
    @ApiOperation("用户管理：学生列表")
    @OperationLog("用户管理：学生列表")
    public ResponseParam listStudent() {
        List<User> userList = userService.listUser(RoleEnum.STUDENT.getCode());
        return new ResponseParam(userList);
    }

    @RequiresRoles(value = {"admin","superadmin"}, logical = Logical.OR)
    @GetMapping("/user/listTeacher")
    @ResponseBody
    @ApiOperation("用户管理：教师列表")
    @OperationLog("用户管理：教师列表")
    public ResponseParam listTeacher() {
        List<User> userList = userService.listUser(RoleEnum.TEACHER.getCode());
        return new ResponseParam(userList);
    }

    /*----------------------------------超级管理员------------------------------*/

    @RequiresRoles(value = "superadmin")
    @PostMapping("/user/deleteAdmin/{id}")
    @ResponseBody
    @ApiOperation("用户管理：删除管理员")
    @OperationLog("用户管理：删除管理员")
    public ResponseParam deleteAdmin(@PathVariable("id")Long id) {
        return deleteById(id);
    }

    @RequiresRoles(value = "superadmin")
    @PostMapping("/user/lockAdmin/{id}")
    @ResponseBody
    @ApiOperation("用户管理：封禁/解封管理员")
    @OperationLog("用户管理：封禁/解封管理员")
    public ResponseParam lockAdmin(@PathVariable("id")Long id) {
        return lockById(id);
    }

    @RequiresRoles(value = "superadmin")
    @PostMapping("/user/listAdminForPage")
    @ResponseBody
    @ApiOperation("用户管理：查询管理员")
    @OperationLog("用户管理：查询管理员")
    public ResponseParam listAdminForPage(@Validated @RequestBody PageRequestParam<UserPageRequestParam> req) {
        UserPageRequestParam data = req.getData();

        Page<User> page = new Page<>(req.getPage(),req.getLimit());
        page = userService.listForPage(page,data,true);

        return new ResponseParam(page);
    }

    @PostMapping("/user/saveAdmin")
    @ResponseBody
    @ApiOperation("用户管理：新增管理员")
    @OperationLog("用户管理：新增管理员")
    @RequiresRoles(value = "superadmin")
    public ResponseParam saveAdmin(@Validated @RequestBody RequestDataParam<UserRequestParam> req) throws Exception {
        return save(req);
    }

    @PostMapping("/user/updateAdmin/{id}")
    @ResponseBody
    @ApiOperation("用户管理：修改管理员")
    @OperationLog("用户管理：修改管理员")
    @RequiresRoles(value = "superadmin")
    public ResponseParam updateAdmin(@PathVariable("id")Long id, @Validated @RequestBody RequestDataParam<UserRequestParam> req) {
        req.getData().setId(id);
        return update(req.getData());
    }

    /*----------------------------------教师方法------------------------------*/

    @RequiresRoles(value = {"teacher","superadmin"}, logical = Logical.OR)
    @PostMapping("/user/listStudentForPage")
    @ResponseBody
    @ApiOperation("用户管理：查询学生")
    @OperationLog("用户管理：查询学生")
    public ResponseParam listStudentForPage(@Validated @RequestBody PageRequestParam<StudentPageRequestParam> req) throws Exception {
        StudentPageRequestParam data = req.getData();

        User user = (User) SecurityUtils.getSubject().getPrincipal();
        UserClazz userClazz = UserClazz.builder().userId(user.getId()).build();
        List<Long> clazzIds = userClazzService.list(userClazz).stream().map(UserClazz::getClazzId).collect(Collectors.toList());
        List<Long> studentIds = userClazzService.listByClazzIds(clazzIds).stream().map(UserClazz::getUserId).collect(Collectors.toList());

        data.setStudentIds(studentIds);
        Page<User> page = new Page<>(req.getPage(),req.getLimit());
        page = userService.listStudentForPage(page,data);

        return new ResponseParam(page);
    }

    /*----------------------------------学生方法------------------------------*/
    

    /*----------------------------------通用方法------------------------------*/

    public ResponseParam lockById(Long id){
        ResponseParam responseParam = new ResponseParam();
        boolean status = userService.lockById(id);
        if (!status) {
            responseParam = ResponseUtil.getErrorResponseParam("系统错误!");
        }
        return responseParam;
    }

    public ResponseParam deleteById(Long id){
        ResponseParam responseParam = new ResponseParam();
        boolean status = userService.removeById(id);
        if (!status) {
            responseParam = ResponseUtil.getErrorResponseParam("系统错误!");
        }
        return responseParam;
    }

    public ResponseParam save(RequestDataParam<UserRequestParam> req) throws ServiceException, UploadException, IOException {
        UserRequestParam data = req.getData();
        if(StrUtil.isBlank(data.getPassword())){
            throw new ServiceException("密码不能为空！");
        }
        if(userService.existUserByUsername(data.getUsername())){
            throw new ServiceException("当前用户已经存在！");
        }

        User user = User.builder().deleteFlag( 0).lockFlag( 0).createTime(DateUtil.date()).updateTime(DateUtil.date()).build();
        BeanUtil.copyProperties(data, user);
        String salt = SaltUtil.getSalt(6);
        Md5Hash md5Hash = new Md5Hash(data.getPassword(), salt, 1024);
        user.setPassword(md5Hash.toHex());
        user.setSalt(salt);
        user.setName(ObjectUtil.defaultIfBlank(user.getName(), StrUtil.concat(true,"用户",RandomUtil.randomString(8))));

        String imgName = "";
        RoleEnum roleEnum = RoleEnum.getByCode(data.getRole());
        if(ObjectUtil.isNull(roleEnum)){
            log.error("找不到该用户注册的角色");
            throw new ServiceException("系统错误");
        }else {
            if(ObjectUtil.equal(roleEnum,RoleEnum.SUPERADMIN)){
                imgName = ImgDefaultName.SUPERADMIN_ICON;
            }else if(ObjectUtil.equal(roleEnum,RoleEnum.ADMIN)){
                imgName = ImgDefaultName.ADMIN_ICON;
            }else if(ObjectUtil.equal(roleEnum,RoleEnum.TEACHER)){
                imgName = ImgDefaultName.TEACHER_ICON;
            }else{
                imgName = ImgDefaultName.STUDENT_ICON;
            }
        }
        user.setImg(imgName);

        ResponseParam responseParam = new ResponseParam();
        boolean status = userService.save(user);
        if (!status) {
            responseParam = ResponseUtil.getErrorResponseParam("系统错误！");
        }
        return responseParam;
    }

    public ResponseParam update(UserRequestParam data) {
        User user = userService.getById(data.getId());
        if(StrUtil.isNotBlank(data.getPassword())){
            String salt = SaltUtil.getSalt(6);
            Md5Hash md5Hash = new Md5Hash(data.getPassword(), salt, 1024);
            user.setPassword(md5Hash.toHex());
            user.setSalt(salt);
        }
        user.setName(ObjectUtil.defaultIfBlank(data.getName(),user.getName()));
        user.setPhone(ObjectUtil.defaultIfBlank(data.getPhone(),user.getPhone()));
        user.setEmail(ObjectUtil.defaultIfBlank(data.getEmail(),user.getEmail()));
        user.setRole(ObjectUtil.defaultIfNull(data.getRole(),user.getRole()));
        user.setUpdateTime(DateUtil.date());

        ResponseParam responseParam = new ResponseParam();
        boolean status = userService.updateById(user);
        if (!status) {
            responseParam = ResponseUtil.getErrorResponseParam("系统错误！");
        }
        return responseParam;
    }
}
