package cn.iurac.testsystem.controller;

import com.baomidou.kaptcha.Kaptcha;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/kaptcha")
@Api("验证码")
public class KaptchaController {

    @Autowired
    private Kaptcha kaptcha;


    @GetMapping("/render")
    @ApiOperation("获取验证码图片")
    public void render(HttpServletRequest request, HttpServletResponse response) {
       kaptcha.render();
    }
}