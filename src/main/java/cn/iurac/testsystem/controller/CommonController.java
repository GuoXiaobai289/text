package cn.iurac.testsystem.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.iurac.testsystem.entity.User;
import cn.iurac.testsystem.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Controller
@Api("页面跳转controller")
public class CommonController {

    /* ------------------------------------通用------------------------------------ */

    @GetMapping({"/","/index"})
    @ApiOperation("页面跳转：主页")
    public String toIndex(Model model){
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        if(ObjectUtil.isNotNull(user)){
            model.addAttribute("userInfo",user);
        }

        return "index";
    }

    @GetMapping("/login")
    @ApiOperation("页面跳转：登录")
    public String toLogin(){
        return "login";
    }

    @GetMapping("/register")
    @ApiOperation("页面跳转：注册")
    public String toRegister(){
        return "register";
    }

    @RequiresUser
    @GetMapping({"/setting/{page}"})
    @ApiOperation("页面跳转：个人信息")
    public String toSettingPage(@PathVariable("page") String page, Model model){
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        model.addAttribute("userInfo",user);
        String nextPage = "setting/"+page;
        return nextPage;
    }

    /* ------------------------------------后台------------------------------------ */

    @GetMapping({"/admin/{page}"})
    @RequiresRoles(value = {"admin","superadmin"}, logical = Logical.OR)
    @ApiOperation("页面跳转：管理员后台")
    public String toPage(@PathVariable("page") String page, Model model){
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        model.addAttribute("userInfo",user);

        String nextPage = "admin/"+page;
        return nextPage;
    }

    @GetMapping({"/admin/user/{page}"})
    @RequiresRoles(value = {"admin","superadmin"}, logical = Logical.OR)
    @ApiOperation("页面跳转：管理员后台用户管理")
    public String toAdminUserPage(@PathVariable("page") String page, Model model){
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        model.addAttribute("userInfo",user);
        String nextPage = "admin/user/"+page;
        return nextPage;
    }

    @GetMapping({"/admin/exam/{page}"})
    @RequiresRoles(value = {"admin","superadmin"}, logical = Logical.OR)
    @ApiOperation("页面跳转：管理员考试管理")
    public String toAdminExamPage(@PathVariable("page") String page, Model model){
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        model.addAttribute("userInfo",user);
        String nextPage = "admin/exam/"+page;
        return nextPage;
    }

    @GetMapping({"/admin/question/{page}"})
    @RequiresRoles(value = {"admin","superadmin"}, logical = Logical.OR)
    @ApiOperation("页面跳转：管理员题目管理")
    public String toAdminTitlePage(@PathVariable("page") String page, Model model){
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        model.addAttribute("userInfo",user);
        String nextPage = "admin/question/"+page;
        return nextPage;
    }

/* ------------------------------------学生------------------------------------ */

    @RequiresRoles(value = {"student","superadmin"}, logical = Logical.OR)
    @GetMapping({"/student/{page}"})
    @ApiOperation("学生相关页面跳转")
    public String toStudentPage(@PathVariable("page") String page, Model model){
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        model.addAttribute("userInfo",user);
        String nextPage = "student/"+page;
        return nextPage;
    }

    @RequiresRoles(value = {"student","superadmin"}, logical = Logical.OR)
    @GetMapping({"/student/grade/{page}"})
    @ApiOperation("学生相关页面跳转")
    public String toStudentGradePage(@PathVariable("page") String page, Model model){
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        model.addAttribute("userInfo",user);
        String nextPage = "student/grade/"+page;
        return nextPage;
    }

/* ------------------------------------教师------------------------------------ */

    @RequiresRoles(value = {"teacher","superadmin"}, logical = Logical.OR)
    @GetMapping({"/teacher/{page}"})
    @ApiOperation("页面跳转：教师后台")
    public String toTeacherPage(@PathVariable("page") String page, Model model){
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        model.addAttribute("userInfo",user);
        String nextPage = "teacher/"+page;
        return nextPage;
    }

    @GetMapping({"/teacher/student/{page}"})
    @RequiresRoles(value = {"teacher","superadmin"}, logical = Logical.OR)
    @ApiOperation("页面跳转：教师后台用户管理")
    public String toTeacherStudentPage(@PathVariable("page") String page, Model model){
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        model.addAttribute("userInfo",user);
        String nextPage = "teacher/student/"+page;
        return nextPage;
    }

    @GetMapping({"/teacher/exam/{page}"})
    @RequiresRoles(value = {"teacher","superadmin"}, logical = Logical.OR)
    @ApiOperation("页面跳转：教师后台考试管理")
    public String toTeacherExamPage(@PathVariable("page") String page, Model model){
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        model.addAttribute("userInfo",user);
        String nextPage = "teacher/exam/"+page;
        return nextPage;
    }

    @GetMapping({"/teacher/question/{page}"})
    @RequiresRoles(value = {"teacher","superadmin"}, logical = Logical.OR)
    @ApiOperation("页面跳转：教师后台题目管理")
    public String toTeacherTitlePage(@PathVariable("page") String page, Model model){
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        model.addAttribute("userInfo",user);
        String nextPage = "teacher/question/"+page;
        return nextPage;
    }

}
