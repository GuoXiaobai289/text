package cn.iurac.testsystem.log;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import cn.iurac.testsystem.entity.SysOperation;
import cn.iurac.testsystem.entity.User;
import cn.iurac.testsystem.service.SysOperationService;
import cn.iurac.testsystem.util.IPUtil;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.StringJoiner;

@Component
@Aspect
public class OperationAspect {

    @Resource
    private SysOperationService sysOperationService;


    @Pointcut("@annotation(cn.iurac.testsystem.log.OperationLog)")
    public void pointcut() {
    }

    @AfterReturning("pointcut()")
    public void saveSysLog(JoinPoint joinPoint) {
        // 保存日志
        SysOperation sysOperation = new SysOperation();

        // 从切面织入点处通过反射机制获取织入点处的方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 获取切入点所在的方法
        Method method = signature.getMethod();

        // 保存获取的操作
        OperationLog operationLog = method.getAnnotation(OperationLog.class);
        if (operationLog != null) {
            sysOperation.setOperation(operationLog.value());
        }

        // 获取请求的类名、方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = method.getName();
        sysOperation.setMethod(new StringJoiner(".").add(className).add(methodName).toString());

        // 获取请求的参数
        Object[] args = joinPoint.getArgs();
        String argument = JSONUtil.toJsonPrettyStr(args);
        sysOperation.setArgument(argument);

        sysOperation.setCreateTime(DateUtil.date());
        // 获取用户id、姓名
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        if(ObjectUtil.isNotNull(user)){
            sysOperation.setUserId(user.getId());
            sysOperation.setUserName(user.getName());
        }

        // 获取请求地址、用户ip地址
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        sysOperation.setIp(IPUtil.getIpAddress(request));
        sysOperation.setUrl(request.getRequestURI());

        // 调用service保存SysLog实体类到数据库
        sysOperationService.save(sysOperation);
    }

}
