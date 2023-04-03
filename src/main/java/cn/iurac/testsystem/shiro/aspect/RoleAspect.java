package cn.iurac.testsystem.shiro.aspect;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.iurac.testsystem.entity.User;
import cn.iurac.testsystem.enums.RoleEnum;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Aspect
@Component
@Order(10)
public class RoleAspect {

    @Pointcut("execution(* cn.iurac.testsystem.controller..*.*(..))")
    private void hasRole() {}

    // Before表示logAdvice将在目标方法执行前执行
    @Before("hasRole()")
    public void logAdvice(JoinPoint joinPoint){
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        Method method = signature.getMethod();
        RequiresRoles annotation = method.getAnnotation(RequiresRoles.class);
        if (ObjectUtil.isNull(annotation)) {
            return;
        }
        String[] roles = annotation.value();
        Logical logical = annotation.logical();

        User user = (User) SecurityUtils.getSubject().getPrincipal();
        if (roles.length == 1) {
            return;
        }
        if (!checkRoles(user.getRole(), Arrays.asList(roles),Logical.AND.equals(logical))) {
            throw new ShiroException();
        }
    }

    private boolean checkRoles(Integer role, List<String> roleList, boolean shouldAllPerm) {
        String target = Objects.requireNonNull(RoleEnum.getByCode(role)).getRole();
        for (String r : roleList) {
            if(StrUtil.equals(target,r)){
                if(shouldAllPerm){
                    continue;
                }
                return true;
            }else {
                if(shouldAllPerm){
                    return false;
                }
            }
        }
        return shouldAllPerm;
    }
}
