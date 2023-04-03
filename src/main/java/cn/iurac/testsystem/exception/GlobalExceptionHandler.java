package cn.iurac.testsystem.exception;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.iurac.testsystem.param.ResponseParam;
import cn.iurac.testsystem.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.ShiroException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 捕捉shiro的异常
    @ExceptionHandler(ShiroException.class)
    @ResponseBody
    public ModelAndView handle401(ShiroException e) {
        return errorModelAndView(401,"您没有权限");
    }

    //服务异常
    @ResponseStatus()
    @ExceptionHandler(value = ServiceException.class)
    @ResponseBody
    public Object handlerServiceException(HttpServletRequest request, ServiceException e){
        log.error("服务异常",e);
        if(isAjax(request)){
            return ResponseUtil.getErrorResponseParam(e.getMessage());
        }else {
            return errorModelAndView(500,e.getMessage());
        }
    }

    //文件上传下载异常
    @ResponseStatus()
    @ExceptionHandler(value = UploadException.class)
    @ResponseBody
    public Object handlerUploadException(UploadException e){
        log.error("文件上传下载异常",e);
        return ResponseUtil.getErrorResponseParam(e.getMessage());
    }

    @ExceptionHandler({ConstraintViolationException.class, BindException.class})
    @ResponseBody
    public Object validateException(HttpServletRequest request, Exception e) {
        String msg = null;
        if(e instanceof ConstraintViolationException){
            ConstraintViolationException constraintViolationException =(ConstraintViolationException)e;
            Set<ConstraintViolation<?>> violations = constraintViolationException.getConstraintViolations();
            ConstraintViolation<?> next = violations.iterator().next();
            msg = next.getMessage();
        }else if(e instanceof BindException){
            BindException bindException = (BindException)e;
            msg = bindException.getBindingResult().getFieldError().getDefaultMessage();
        }
        log.error("",e);
        return ResponseUtil.getErrorResponseParam(msg);
    }

    //其它异常
    @ResponseStatus()
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Object handlerRuntimeException(HttpServletRequest request, Exception e) {
        log.error("",e);
        if(isAjax(request)){
            return ResponseUtil.getErrorResponseParam("请求超时！");
        }else {
            return errorModelAndView(500,"请求超时");
        }
    }

    private ModelAndView errorModelAndView(Integer code,String message) {
        ModelAndView mv = new ModelAndView();
        mv.getModel().put("timestamp", DateUtil.now());
        mv.getModel().put("status", code);
        mv.getModel().put("error","系统错误");
        mv.getModel().put("message",message);
        mv.setViewName("error/4xx");
        return mv;
    }

    public static boolean isAjax(HttpServletRequest httpRequest) {
        return (ObjectUtil.isNotNull(httpRequest.getHeader("X-Requested-With"))
                && StrUtil.equals("XMLHttpRequest", httpRequest.getHeader("X-Requested-With")));
    }
}